package com.japisoft.framework.xml.parser;

/**
This program is available under two licenses : 

1. For non commercial usage : 

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.

2. For commercial usage :

You need to get a commercial license for source usage at : 

http://www.editix.com/buy.html

Copyright (c) 2018 Alexandre Brillant - JAPISOFT SARL - http://www.japisoft.com

@author Alexandre Brillant - abrillant@japisoft.com
@author JAPISOFT SARL - http://www.japisoft.com

*/
public class ParseException extends Exception {


    public ParseException( String cause ) {
	super( cause );
    }

    
    public ParseException( String cause, int offset, int line ) {
    	super( cause );
    	this.line = line;
    	this.caret = offset;
    }
    
    
    private int caret;
    private int line;
    private int col;
    private String causeBy;

    void setCauseBy( String s ) {
	causeBy = s;
    }

    void setCaret( int caret ) {
	this.caret = caret;
    }

    void setLine( int line ) {
	this.line = line;
    }

    void setCol( int col ) {
	this.col = col;
    }

    /** @return the current error position starting from 0 */
    public int getCaret() { return caret; }
    /** @return the current error line */
    public int getLine() { return line; }
    /** @return the current error colomn */
    public int getCol() { return col; }

    /** @return a reason about the error */
    public String causeBy() { return causeBy; }

    public String toString() {
	return getMessage() + " i" + caret + " l" + line + " c" + col;
    }

}

// ParseException ends here
