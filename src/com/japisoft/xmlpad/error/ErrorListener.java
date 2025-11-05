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

package com.japisoft.xmlpad.error;

/**
 * Listener for XML parsing error. An error 'OnTheFly' means it can
 * be a temporary error while the user is inserting characters.
 * @author Alexandre Brillant (https://github.com/AlexandreBrillant/Editix-xml-editor)
 * @version 1.0 */
public interface ErrorListener {

	/** This is called once before checking the whole document */
	public void initErrorProcessing();

	/** This is called once after checking the whole document */
	public void stopErrorProcessing();
	
	/** Notify an XML error found while parsing
	 * @param context the error context, it can be <code>null</code> and may be ignored
	 * @param localError a flag for knowing if the error is inside the current document (sometimes this is inside a DTD or a W3C Schema)
	 * @param sourceLocation The document location
	 * @param line The error line
	 * @param col The error column
	 * @param offset The error offset
	 * @param message The error message
	 * @param onTheFly Flag for informing if this is while the user inserts or no */
    public void notifyError(
    		Object context,
    		boolean localError,
    		String sourceLocation, 
			int line, 
			int 
			col, 
			int offset, 
			String message, 
			boolean onTheFly );

    /** Notify the document is correct
     * @param onTheFly for informing if this is while inserting characters */
    public void notifyNoError( boolean onTheFly );

}


