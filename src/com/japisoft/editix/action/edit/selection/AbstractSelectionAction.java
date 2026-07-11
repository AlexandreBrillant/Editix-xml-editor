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

package com.japisoft.editix.action.edit.selection;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.text.BadLocationException;

import com.japisoft.editix.ui.EditixFactory;
import com.japisoft.editix.ui.EditixFrame;
import com.japisoft.xmlpad.IXMLPanel;
import com.japisoft.xmlpad.XMLContainer;

public abstract class AbstractSelectionAction extends AbstractAction {

	@Override
	public void actionPerformed(ActionEvent e) {
		IXMLPanel panel = EditixFrame.THIS.getSelectedPanel();
		if ( panel == null ) {
			EditixFactory.buildAndShowWarningDialog( "No editor ?" );
			return;
		}
		
		XMLContainer container = panel.getMainContainer();
		if ( container == null ) {
			EditixFactory.buildAndShowWarningDialog( "Can't find current editor ?" );
			return;
		}
		int start = container.getEditor().getSelectionStart();
		int end = container.getEditor().getSelectionEnd();
		if ( start == -1 || end == -1 || start == end ) {
			EditixFactory.buildAndShowWarningDialog( "No selected text ?" );
			return;
		}
		try {
			String selection = container.getDocument().getText( start, end - start );
			String new_selection = processSelection( selection );
			if ( new_selection != null ) {
				container.getEditor().replaceSelection( new_selection );
			}
		} catch( BadLocationException exc ) {
			EditixFactory.buildAndShowErrorDialog( "Can't get the selected text ? [" + exc.getMessage() + "]" );
		}

	}
	
	 abstract protected String processSelection( String selection );
	
	
}