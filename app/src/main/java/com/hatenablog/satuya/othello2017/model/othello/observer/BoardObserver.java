package com.hatenablog.satuya.othello2017.model.othello.observer;

import com.hatenablog.satuya.othello2017.model.othello.event.FinishEvent;
import com.hatenablog.satuya.othello2017.model.othello.event.PassEvent;
import com.hatenablog.satuya.othello2017.model.othello.event.PutEvent;
import com.hatenablog.satuya.othello2017.model.othello.event.TryWrongPosPutEvent;

/**
 * Created by Shusei on 2017/03/15.
 */

public interface BoardObserver {

    void onPut( PutEvent event );

    void onPassed( PassEvent event );

    void onFinished( FinishEvent event );

    void onTryWrongPosPut( TryWrongPosPutEvent event );
    //TODO void onUndo( UndoEvent event );
}
