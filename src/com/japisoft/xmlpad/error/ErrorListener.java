package com.japisoft.xmlpad.error;

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

// ErrorListener ends here
