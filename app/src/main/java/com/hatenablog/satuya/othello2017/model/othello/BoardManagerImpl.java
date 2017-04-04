package com.hatenablog.satuya.othello2017.model.othello;

import com.hatenablog.satuya.othello2017.model.othello.engine.Board;
import com.hatenablog.satuya.othello2017.model.othello.entity.Color;
import com.hatenablog.satuya.othello2017.model.othello.entity.Disc;
import com.hatenablog.satuya.othello2017.model.othello.entity.Point;
import com.hatenablog.satuya.othello2017.model.othello.event.FinishEvent;
import com.hatenablog.satuya.othello2017.model.othello.event.FinishEventImpl;
import com.hatenablog.satuya.othello2017.model.othello.event.PassEvent;
import com.hatenablog.satuya.othello2017.model.othello.event.PassEventImpl;
import com.hatenablog.satuya.othello2017.model.othello.event.PutEventImpl;
import com.hatenablog.satuya.othello2017.model.othello.event.TryWrongPosPutEvent;
import com.hatenablog.satuya.othello2017.model.othello.event.TryWrongPosPutEventImpl;
import com.hatenablog.satuya.othello2017.model.othello.observer.BoardObserver;
import com.hatenablog.satuya.othello2017.model.othello.player.Player;
import com.hatenablog.satuya.othello2017.model.othello.player.PlayerGroup;
import com.hatenablog.satuya.othello2017.model.othello.player.UIPlayer;

import java.util.ArrayList;
import java.util.Iterator;

import javax.inject.Inject;

import static com.hatenablog.satuya.othello2017.model.othello.OthelloUtility.convertColorCodeToColor;
import static com.hatenablog.satuya.othello2017.model.othello.entity.Disc.BLACK;

/**
 * Created by Shusei on 2017/03/15.
 */

public class BoardManagerImpl implements BoardManager {

    public static final int DUMMY_SCORE = 50; //ダミーのスコア

    private ArrayList<BoardObserver> observers = null;

    private PlayerGroup playerGroup = null; //TODO null

    private Board board = null; //TODO null

    private int currentColorCode;

    private boolean isUIPutFinished = true;

    @Inject
    public BoardManagerImpl( Board board, PlayerGroup group ) {

        this.observers = new ArrayList<>();
        this.board = board;
        this.playerGroup = group;
        playerGroup.setManager( this );
        initBoard();
    }

    @Override
    public void initBoard() {
        this.board.init();
        this.currentColorCode = this.board.getCurrentColor();
        turnChange( this.currentColorCode );
    }

    @Override
    public void addObserver( BoardObserver observer ) {
        this.observers.add( observer );
    }

    @Override
    synchronized public boolean put( Point point ) { //TODO 排他制御確認

        boolean canPut = board.put( point );

        if ( !canPut ) {
            onTryWrongPosPut( point );
            return false;
        } else {
            onPut(); //本処理 returnしていないのは正常
        }

        if ( board.isGameOver() ) {
            onFinished();
            return true;
        }

        if ( board.pass() ) {
            this.currentColorCode = board.getCurrentColor();
            onPassed();
        }

        if ( this.currentColorCode == BLACK ) {
            Player player = playerGroup.getBlackPlayer();
            if ( player instanceof UIPlayer ) {
//                isUIPutFinished = false;
                isUIPutFinished = true;
            }
        } else {
            Player player = playerGroup.getWhitePlayer();
            if ( player instanceof UIPlayer ) {
//                isUIPutFinished = false;
                isUIPutFinished = true;
            }
        }

        this.currentColorCode = board.getCurrentColor();

        new Thread( new Runnable() {
            @Override
            public void run() {
                while ( true ) {
                    if ( isUIPutFinished ) {
                        turnChange( BoardManagerImpl.this.currentColorCode );
                        break;
                    }
                }
            }
        } ).start();

        return true;
    }

    synchronized public boolean undo() { //TODO 未実装

        boolean canPut = board.undo();

        if ( canPut ) {
            return false;
        }

        return true;
    }

    @Override
    public Board getBoard() { //TODO 削除する

        return this.board;
    }

    @Override
    public void onUIPutFinished() {

        isUIPutFinished = true;
    }

    private void onTryWrongPosPut( Point point ) {

        TryWrongPosPutEvent event =
                new TryWrongPosPutEventImpl( point, convertColorCodeToColor( currentColorCode ) );

        Iterator<BoardObserver> iterator = observers.iterator();
        while ( iterator.hasNext() ) {
            BoardObserver observer = iterator.next();
            observer.onTryWrongPosPut( event );
        }

        for ( BoardObserver observer : observers ) {

            observer.onTryWrongPosPut( event );
        }
    }

    private void onFinished() {

        int blackNumber = this.board.countDisc( BLACK );
        int whiteNumber = this.board.countDisc( Disc.WHITE );

        Color winnerColor = null;
        Color loserColor = null;
        if ( blackNumber == whiteNumber ) {
            winnerColor = Color.UNKNOWN;
            loserColor = Color.UNKNOWN;
        } else if ( blackNumber > whiteNumber ) {
            winnerColor = Color.BLACK;
            loserColor = Color.WHITE;
        } else if ( blackNumber < whiteNumber ) {
            winnerColor = Color.WHITE;
            loserColor = Color.BLACK;
        }

        FinishEvent event = new FinishEventImpl( winnerColor, loserColor, DUMMY_SCORE );

        Iterator<BoardObserver> iterator = observers.iterator();
        while ( iterator.hasNext() ) {
            BoardObserver observer = iterator.next();
            observer.onFinished( event );
        }
    }

    private void onPassed() {

        PassEvent event = new PassEventImpl( convertColorCodeToColor( -currentColorCode ) );

        Iterator<BoardObserver> iterator = observers.iterator();
        while ( iterator.hasNext() ) {
            BoardObserver observer = iterator.next();
            observer.onPassed( event );
        }
    }

    private void onPut() {

        PutEventImpl event = new PutEventImpl( convertColorCodeToColor( this.currentColorCode ),
                this.board.getUpdateDiscs(), this.board.countDisc( BLACK ), this.board.countDisc( Disc.WHITE ) );

        Iterator<BoardObserver> iterator = observers.iterator();
        while ( iterator.hasNext() ) {
            BoardObserver observer = iterator.next();
            observer.onPut( event );
        }
    }

    private void turnChange( int colorCode ) {

        if ( colorCode == BLACK ) {
            playerGroup.getBlackPlayer().onTurn();
        } else {
            playerGroup.getWhitePlayer().onTurn();
        }
    }
}