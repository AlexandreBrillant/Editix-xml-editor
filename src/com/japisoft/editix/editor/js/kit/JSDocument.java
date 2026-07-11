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

package com.japisoft.editix.editor.js.kit;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;

import com.japisoft.framework.preferences.Preferences;
import com.japisoft.xmlpad.editor.XMLPadDocument;

public class JSDocument extends XMLPadDocument {
	
	private boolean autoIndent = false;
	
	public JSDocument( String preferenceGroupe ) {
		super(null);
		autoIndent = Preferences.getPreference( preferenceGroupe, "autoIndent", true );
	}

	@Override
	public void insertString(int offs, String str, AttributeSet a)
			throws BadLocationException {
		super.insertString(offs, str, a);
		if ( autoIndent ) {
			if ( "{".equals( str ) ) {
				String indent = getIndentAtOffset(offs);			
				super.insertString(offs + 1, "\n" + indent + "\t\n" + indent + "}", a );
				getCurrentEditor().setCaretPositionWithoutNotification( offs + indent.length() + 3 );
			}
		}
	}
	
	
	
}
