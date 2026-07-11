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

package com.japisoft.editix.ui.leftpanels.diff;

import java.awt.Color;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.UIManager;

/**
 * @author Alexandre Brillant (https://github.com/AlexandreBrillant/Editix-xml-editor)
 * @version 1.0
 * */
public class DiffResourceFactory {

	static ImageIcon DEF = new ImageIcon( ClassLoader.getSystemResource( "images/element_ok.png" ) ); 
	static ImageIcon ADD = new ImageIcon( ClassLoader.getSystemResource( "images/element_add.png" ) ); 
	static ImageIcon MINUS = new ImageIcon( ClassLoader.getSystemResource( "images/element_delete.png" ) ); 
	static ImageIcon ATT = new ImageIcon( ClassLoader.getSystemResource( "images/element_new.png" ) );
	
	static final int ADD_TYPE = 0;
	static final int REMOVE_TYPE = 1;
	static final int ATT_TYPE = 2;
	
	public static Icon getIconForType( int type ) {

		if ( type == ADD_TYPE )
			return ADD;
		else
		if ( type == REMOVE_TYPE )
			return MINUS;
		else
		if ( type == ATT_TYPE )
			return ATT;
		
		return DEF;
	}

	static Color COLOR_REMOVED = new Color( 220, 150, 150 );
	static Color COLOR_ADDED  = new Color( 150, 150, 220 );
	static Color COLOR_ATT = new Color( 255, 239, 146 );
	
	static {
		if ( UIManager.getColor( "editix.panel.diff.removed" ) != null ) {
			COLOR_REMOVED = UIManager.getColor( "editix.panel.diff.removed" );
		}
		if ( UIManager.getColor( "editix.panel.diff.added" ) != null ) {
			COLOR_ADDED = UIManager.getColor( "editix.panel.diff.added" );
		}
		if ( UIManager.getColor( "editix.panel.diff.att" ) != null ) {
			COLOR_ATT = UIManager.getColor( "editix.panel.diff.att" );
		}
	}

	public static Color getBgColorForType( int type ) {

		if ( type == ADD_TYPE )
			return COLOR_ADDED;
		else
		if ( type == REMOVE_TYPE )
			return COLOR_REMOVED;
		else
		if ( type == ATT_TYPE )
			return COLOR_ATT;
		
		return Color.WHITE;
	}

}
