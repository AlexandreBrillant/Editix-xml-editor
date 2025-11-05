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

package com.japisoft.xmlpad.action.file;

import javax.swing.text.BadLocationException;

import com.japisoft.xmlpad.action.XMLAction;

/**
 * New action
 * @author Alexandre Brillant (https://github.com/AlexandreBrillant/Editix-xml-editor)
 * @version 1.1 */
public class NewAction extends XMLAction {

	public static final String ID = NewAction.class.getName();
	
	public NewAction() {
		super();
	}

	public void notifyXMLEditor() {
		//	notifyAction();
	}

	public boolean notifyAction() {
		boolean rtSupport = editor.isEnabledRealTimeStructureChanged();
		editor.setEnabledRealTimeStructureChanged(false);

		if (container.getDocumentInfo().getDefaultDocument() != null) {
			container.setText(container.getDocumentInfo().getDefaultDocument());
		} else {
			if ( container.getTemplate() != null ) {
				
				String model = container.getTemplate().toString( container.getDocumentInfo() ); 
				container.setText( model );

				try {
					if ( container.getTemplate().getCursorLocation() >= 0 )
							container.getEditor().setCaretPosition( 
								container.getTemplate().getCursorLocation() );
				} catch( Throwable th ) {
				}
			}
		}
		try {
			editor.getUndoManager().discardAllEdits();
		} catch (Throwable th) {
		}

		editor.setEnabledRealTimeStructureChanged(rtSupport);
		editor.notifyStructureChanged();
		container.setModifiedState(false);
		container.setCurrentDocumentLocation(null);
		return VALID_ACTION;
	}

	protected void notifyXMLContainer() {
		setEnabled(container.isEditable());
	}

}


