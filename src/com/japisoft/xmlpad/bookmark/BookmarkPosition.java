package com.japisoft.xmlpad.bookmark;

import javax.swing.text.Position;

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
public class BookmarkPosition implements Position {

	Object source;
	Position position;
	Object highlightFlag;
		
	/** Initialize this bookmark position
	 * @param position must not be <code>null</code> */
	public BookmarkPosition( Position position, Object highlightFlag ) {
		this.position = position;
		this.highlightFlag = highlightFlag;
	}
	
	/** @return the current bookmark position */
	public int getOffset() {
		return position.getOffset();
	}
	
	/** @return a flag for this highlight. This is for inner usage */
	public Object getHighlightFlag() {
		return this.highlightFlag;
	}

	public void setSource( Object source ) {
		this.source = source;
	}
	
	public Object getSource() {
		return source;
	}
	
	public void dispose() {
		this.source = null;
	}

}
