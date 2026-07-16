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

package com.japisoft.editix.action.search;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JPanel;

import com.japisoft.editix.ui.EditixContainerListener;
import com.japisoft.editix.ui.windows.EditixFrame;
import com.japisoft.framework.descriptor.ActionModel;
import com.japisoft.framework.dialog.BasicDialogComponent;
import com.japisoft.framework.dialog.actions.DialogActionModel;
import com.japisoft.framework.preferences.Preferences;
import com.japisoft.framework.ui.findreplace.FindReplacePanel;
import com.japisoft.framework.ui.findreplace.Findable;
import com.japisoft.xmlpad.XMLContainer;
import com.japisoft.xmlpad.editor.XMLEditor;

/**
 * @author Alexandre Brillant (https://github.com/AlexandreBrillant/Editix-xml-editor)
 * @version 1.0 */
public class FindAction extends AbstractAction implements EditixContainerListener {

	private BasicDialogComponent dialog = null;
	Findable panel = null;

	public FindAction() {
		super();
		EditixFrame.addEditixContainerListener( this );
	}
	
	public void actionPerformed(ActionEvent e) {
		XMLContainer container = EditixFrame.THIS.getSelectedContainer();
		if ( container == null )
			return;

		XMLEditor editor = container.getEditor();
		
		if ( Preferences.getPreference( "interface", "oldFindReplace", false ) ) { 
				
			if ( dialog == null ) {
				panel = new FindReplacePanel( container.getEditor(), true );
				
				String param = ( String )getValue( "param" );
	
				if ( param != null ) {
					panel.setFindValue( param );
				}
	
				DialogActionModel model = DialogActionModel.getDefaultDialogActionModel();
				
				dialog = new BasicDialogComponent( EditixFrame.THIS, "Find/Replace" );
				dialog.setModal( false );
	
				dialog.getContentPane().add( (JPanel)panel );
				dialog.setSize( 300, 350 );
				dialog.setVisible( true );
			} else {			
				
				panel.updateTextComponent( container.getEditor(), true );
	
				String param = ( String )getValue( "param" );
	
				if ( param != null ) {
					panel.setFindValue( param );
				}
				
				dialog.setVisible( true );
			}
		
		} else {
		
			panel = editor.FindAndReplaceBox();
			panel.updateTextComponent( editor, true );
			
		}
		
		SearchAgainAction action = ( SearchAgainAction )ActionModel.restoreAction( "searchAgain" );
		if ( action != null )
			action.setEnabled( true );
		else
			System.err.println( "Cannot find 'searchAgain' action ??????????????" );
		
	}

	// Freeing inner reference
	public void close( XMLContainer container ) {
		if ( container == null )	// Unknown case
			return;
		if ( panel != null && panel.getCurrentTextComponent() == container.getEditor() ) {
			panel.updateTextComponent( null, false );
			// Disabled searchAgain
			Action searchAgain = ActionModel.restoreAction( "searchAgain" );
			if ( searchAgain != null )
				searchAgain.setEnabled( false );
		}
	}

}
