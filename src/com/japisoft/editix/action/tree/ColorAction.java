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

package com.japisoft.editix.action.tree;

import java.awt.Color;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JColorChooser;
import javax.swing.JTree;
import javax.swing.tree.TreePath;

import com.japisoft.editix.ui.EditixFactory;
import com.japisoft.editix.ui.windows.EditixFrame;
import com.japisoft.framework.xml.parser.node.FPNode;
import com.japisoft.xmlpad.XMLContainer;

public class ColorAction extends AbstractAction {
	
	public void actionPerformed(ActionEvent e) {

		XMLContainer container = 
			EditixFrame.THIS.getSelectedContainer();
		if ( container != null ) {
			JTree t = container.getTree();
			TreePath tp = t.getSelectionPath();
			if ( tp != null ) {
				FPNode sn = ( FPNode )tp.getLastPathComponent();
				if ( sn.isTag() ) {
					JColorChooser jc = new JColorChooser();
					String color = ( String )container.getProperty( "color." + sn.getContent() );
					Color initialColor = null;
					if ( color != null ) {
						initialColor = new Color( Integer.parseInt( color, 16 ) );
					}
					Color c = jc.showDialog( t, "Color a node", initialColor );
					if ( c != null ) {
						container.setProperty( "color." + sn.getContent(), Integer.toHexString( c.getRGB() ).substring( 2 ) );
						container.getTree().repaint();
						container.getEditor().repaint();
					}
				} else {
					EditixFactory.buildAndShowWarningDialog( "Please choose a node" );
				}
			}
		}
		
	}

}
