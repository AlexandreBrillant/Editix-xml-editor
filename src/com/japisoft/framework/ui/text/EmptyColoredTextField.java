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

package com.japisoft.framework.ui.text;

import java.awt.Color;

import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

/**
 * Special text field which color can change if it is
 * empty or not
 * @author Alexandre Brillant (https://github.com/AlexandreBrillant/Editix-xml-editor)
 * @version 1.0
 * */
public class EmptyColoredTextField extends JTextField {
	
	private Color emptyColor = null;
	private Color okColor = null;
	
	public EmptyColoredTextField(
			Color emptyColor,
			Color okColor ) {
		this.emptyColor = emptyColor;
		this.okColor = okColor;
		updateColor();
		setDocument( new CustomPlainDocument() );
	}

	/** It will used the color from the keys of the UIManager : 
	 * - EmptyColoredTextField.emptyColor
	 * - EmptyColoredTextField.okColor
	 */
	public EmptyColoredTextField() {
		emptyColor = UIManager.getColor( "EmptyColoredTextField.emptyColor" );
		okColor = UIManager.getColor( "EmptyColoredTextField.okColor" );
		updateColor();
		setDocument( new CustomPlainDocument() );
	}

	private void updateColor() {
		if ( emptyColor == null || okColor == null )
			return;
		if ( getDocument().getLength() == 0 ) {
			if ( getBackground() != emptyColor )
				setBackground( emptyColor );
		}
		else {
			if ( getBackground() != okColor )
				setBackground( okColor );
		}
	}
	
	class CustomPlainDocument extends PlainDocument {
		public void insertString(int offs, String str, AttributeSet a)
				throws BadLocationException {
			super.insertString(offs, str, a);
			updateColor();
		}
		public void remove(int offs, int len) throws BadLocationException {
			super.remove(offs, len);
			updateColor();
		}
	}
}

