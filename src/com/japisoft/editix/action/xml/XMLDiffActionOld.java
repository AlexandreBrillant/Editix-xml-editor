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

package com.japisoft.editix.action.xml;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import com.japisoft.editix.ui.EditixDialog;
import com.japisoft.editix.ui.EditixFactory;
import com.japisoft.editix.ui.EditixFrame;
import com.japisoft.editix.ui.leftpanels.diff.DiffPanel;
import com.japisoft.framework.dialog.actions.DialogActionModel;
import com.japisoft.framework.dialog.actions.StoringLocationAction;
import com.japisoft.framework.ui.toolkit.BrowserCaller;
import com.japisoft.framework.xml.parser.node.FPNode;
import com.japisoft.p3.Manager;
import com.japisoft.xmlpad.XMLContainer;

/**
 * @author Alexandre Brillant (https://github.com/AlexandreBrillant/Editix-xml-editor)
 * @version 1.0 */
public class XMLDiffActionOld extends AbstractAction { //implements XMLDiffSelection {

	public void actionPerformed( ActionEvent e ) {
		
		if ( Manager.isFree() ) {		
			EditixFactory.buildAndShowInformationDialog( "This action is not available inside the Free Edition.\nPlease look at http://www.editix.com" );
			BrowserCaller.displayURL( "http://www.editix.com" );
			return;
		}

		//���
		EditixDialog dialog = new EditixDialog(
				"XML diff",
				"Compare XML documents",
				"Select both documents to compare the XML content",
				DialogActionModel.getDefaultDialogCloseActionModel().addDialogAction(
						new StoringLocationAction()		
				)		
		);

		XMLContainer container = 
			EditixFrame.THIS.getSelectedContainer();
		// XMLDiffPanel diff = null;
		//XMLPathBuilder pb = new XMLPathBuilder();

/*		if ( container != null && container.getTree() != null && container.getTree().getModel() != null && container.getTree().getModel().getRoot() instanceof SimpleNode ) {
			dialog.getContentPane().add(
					diff = new XMLDiffPanel( 
							( TreeNode )container.getTree().getModel().getRoot(), 
							container.getCurrentDocumentLocation(), pb ) 
					);
		}  else 
			dialog.getContentPane().add(
					diff = new XMLDiffPanel( pb ) 
			); */
		
		dialog.getContentPane().add( new DiffPanel( container ) );

		dialog.setSize( 650, 550 );
		
		dialog.setModal( false );
		
		dialog.setVisible( true );
		//��
	}

	public void select(String documentPath, FPNode node) {
		XMLContainer container = 
			EditixFrame.THIS.getSelectedContainer();
		if ( ( container != null && container.getCurrentDocumentLocation() == null ) || 
				documentPath.equals( container.getCurrentDocumentLocation() ) )
			container.getEditor().highlightLine( node.getStartingLine() );	
	}

}

