package com.hatenablog.satuya.othello2017.domain.othello.event;

import com.hatenablog.satuya.othello2017.domain.othello.entity.Color;

/**
 * Created by Shusei on 2017/03/15.
 */

public interface PassEvent {

    Color getPassPlayerColor();
}
