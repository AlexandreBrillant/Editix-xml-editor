// Editix XML Editor
// https://www.editix.com
// Copyright (c) 2025 Alexandre Brillant
// 
// For non-commercial usage :
// This program is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
// See the GNU General Public License for more details: https://www.gnu.org/licenses/gpl-3.0
// 
// For commercial use or integration into proprietary software :
// A commercial license is required. Visit https://www.editix.com for details.

package com.japisoft.framework.xml.parser;

/**
 * <b>Created Sat Dec 14 17:22:21 2002</b>
 * <p>
 * Parser error exception
 * </p>
 * @author Alexandre Brillant (https://github.com/AlexandreBrillant/Editix-xml-editor)
 * @version 1.0
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


