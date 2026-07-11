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

package com.japisoft.editix.action.xml.update;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.SwingUtilities;

import com.japisoft.editix.document.TemplateModel;
import com.japisoft.editix.editor.jsx.JSXContainer;
import com.japisoft.editix.ui.EditixFactory;
import com.japisoft.editix.ui.EditixFrame;
import com.japisoft.framework.application.descriptor.ActionModel;
import com.japisoft.editix.action.file.NewAction;

public class UpdateWithJSX extends AbstractAction {

	@Override
	public void actionPerformed(ActionEvent arg0) {
		if ( EditixFrame.THIS.getSelectedContainer() == null ) {
			EditixFactory.buildAndShowErrorDialog( "Need a valid XML document ?" );
			return;
		}
		if ( EditixFrame.THIS.getSelectedContainer().getCurrentDocumentLocation() == null ) {
			EditixFactory.buildAndShowErrorDialog( "You must save your document before transforming" );
			return;
		}
		
		ActionModel.activeActionById( ActionModel.SAVE, arg0 );

		final String xmlPath = EditixFrame.THIS.getSelectedContainer().getCurrentDocumentLocation();
		
		NewAction na = new NewAction();
		na.setTemplate( TemplateModel.getTemplateByType( "JSX" ) );
		na.actionPerformed( arg0 );

		SwingUtilities.invokeLater(
				new Runnable() {
					@Override
					public void run() {
						EditixFrame.THIS.getSelectedPanel().setProperty( JSXContainer.DATAFILE_KEY, xmlPath );
					}
				} );
		
	}
	
}
