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

package com.japisoft.xmlpad.look;

import java.awt.Color;

import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.tree.TreeCellRenderer;

import com.japisoft.xmlpad.XMLContainer;
import com.japisoft.xmlpad.editor.XMLEditor;
import com.japisoft.xmlpad.tree.renderer.FastTreeRenderer;

/**
 * Default look for XMLPad
 * @author Alexandre Brillant (https://github.com/AlexandreBrillant/Editix-xml-editor)
 * @version 1.0
 * @see Look */
public class XMLPadLook extends MozillaLook {

	public void install( XMLContainer container, JTree tree ) {
		super.install( container, tree );
		TreeCellRenderer renderer = tree.getCellRenderer();
		if ( renderer instanceof FastTreeRenderer ) {
			FastTreeRenderer ftr  =  (FastTreeRenderer)renderer;
			
			if( UIManager.getLookAndFeel().getClass().getName().indexOf( "WindowsLookAndFeel" ) > -1 )
				ftr.setDashUnderlineMode( false );
			else
				ftr.setDashUnderlineMode( true );

			ftr.setDashUnderlineColor( getTreeSelectionBackgroundColor() );
		}
	}

	protected Color getTreeSelectionBackgroundColor() {
		return Color.GRAY;
	}

	public void install( XMLContainer container, XMLEditor editor ) {
		super.install( container, editor );
	}

}

