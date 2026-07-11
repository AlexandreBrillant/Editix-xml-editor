// Editix XML Editor
// https://www.editix.com
// Copyright (c) 2026 Alexandre Brillant
//
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
// AI Training Restriction :
// This source code is provided for human use only.
// Using this code to train, fine-tune, or develop AI models,
// machine learning systems, or similar technologies is
// STRICTLY PROHIBITED. Violations will terminate all rights
// under the applicable license.

package com.japisoft.editix.ui.xslt;

import java.awt.Color;

import javax.swing.ImageIcon;
import javax.swing.UIManager;

import com.japisoft.xmlpad.bookmark.BookmarkContext;
import com.japisoft.xmlpad.bookmark.BookmarkModel;
import com.japisoft.xmlpad.bookmark.BookmarkPosition;
import com.japisoft.xmlpad.bookmark.DefaultBookmarkContext;

/**
 * @author Alexandre Brillant (https://github.com/AlexandreBrillant/Editix-xml-editor)
 * @version 1.0
 * */
public class XSLTBookmarkContext extends DefaultBookmarkContext {
	
	public XSLTBookmarkContext() {
		super( new ImageIcon( 
				ClassLoader.getSystemResource( "images/breakpoint.png" ) ),
					new Color( 255, 143, 107 ) 
		);
		
		if ( UIManager.getColor( "editix.xslt.breakpoint") != null ) {
			updateLineColor( UIManager.getColor( "editix.xslt.breakpoint" ) );
		}
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
