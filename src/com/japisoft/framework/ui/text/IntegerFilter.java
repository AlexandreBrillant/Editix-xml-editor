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

package com.japisoft.framework.ui.text;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

/** This is a document that can be used for JTextField for
 * avoiding the user to insert another thing than an integer.
 * You must call setDocument from your textfield with this object. */
public class IntegerFilter extends PlainDocument {

	public void insertString(int offs, String str, AttributeSet a)
			throws BadLocationException {

		boolean canBeInserted = true;
		
		for ( int i = 0; i < str.length(); i++ ) {

			if ( !Character.isDigit( str.charAt( i ) ) ) {
				canBeInserted = false;
				break;
			}

		}

		if ( canBeInserted )
			super.insertString( offs, str, a );

	}
}
