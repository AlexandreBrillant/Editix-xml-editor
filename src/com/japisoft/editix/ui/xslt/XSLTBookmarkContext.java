package com.japisoft.editix.ui.xslt;

import java.awt.Color;

import javax.swing.ImageIcon;

import com.japisoft.xmlpad.bookmark.BookmarkContext;
import com.japisoft.xmlpad.bookmark.BookmarkModel;
import com.japisoft.xmlpad.bookmark.BookmarkPosition;
import com.japisoft.xmlpad.bookmark.DefaultBookmarkContext;

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
public class XSLTBookmarkContext extends DefaultBookmarkContext {
	
	public XSLTBookmarkContext() {
		super( new ImageIcon( 
				ClassLoader.getSystemResource( "images/breakpoint.png" ) ),
					new Color( 255, 143, 107 ) 
		);
	}

	public XSLTBookmarkContext( XSLTBookmarkContext parent ) {
		this();
		// Marge bookmark location
		merge( parent, null );
	}

	public void merge( BookmarkContext parent, Object source ) {
		if ( parent != null ) {
			BookmarkModel parentModel = 
				parent.getModel();
			BookmarkModel model = getModel();
			for ( int i = 0; i < parentModel.getBookmarkCount(); i++ ) {
				BookmarkPosition position = 
					parentModel.getBookmarkPositionAt( i );
				position.setSource( source );
				model.addBookmarkPosition( position );
			}
		}
	}

}
