package com.japisoft.xmlpad;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

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
public interface Accessibility {

	/** Reset the editing content. It should be a full XML document */
	public void setText( String text );

	/** @return the current editing content */
	public String getText();

	/** Reset the editing content using this reader */
	public void read( Reader reader ) throws IOException;

	/** Reset this writer with the editing content */
	public void write( Writer writer ) throws IOException;

	/** Call an XML action. Look at the <code>ActionModel</code> for action name
	 * @return XMLAction.VALID_ACTION when the action has been done without error */
	public boolean invokeAction( String actionName );

	/** This is only for inner usage, it mustn't be called by the user */
	void dispose();
}
