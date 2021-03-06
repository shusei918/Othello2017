package com.hatenablog.satuya.othello2017.domain2.othello.entity;

public class Point {

	public int x;
	public int y;

	public Point() {
		this( 0, 0 );
	}

	public Point( int x, int y ) {
		this.x = x;
		this.y = y;
	}

	public Point( String coord ) throws IllegalArgumentException {

		if ( coord == null || coord.length() < 2 ) {
			throw new IllegalArgumentException( "The argument must be Othello style coordinates." );
		}

		x = coord.charAt( 0 ) - 'a' + 1;
		y = coord.charAt( 1 ) - '1' + 1;
	}

	public String toString() {

		String coord = new String();
		coord += ( char ) ( 'a' + x - 1 );
		coord += ( char ) ( '1' + y - 1 );

		return coord;
	}

	public Point clone() {

		Point point = new Point( this.x, this.y );
		return point;
	}
}
