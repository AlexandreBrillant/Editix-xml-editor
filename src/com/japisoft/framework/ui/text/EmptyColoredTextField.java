package com.japisoft.framework.ui.text;

import java.awt.Color;

import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

/**
This program is available under two licenses : 

1. For non commercial usage : 

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.

2. For commercial usage :

You need to get a commercial license for source usage at : 

http://www.editix.com/buy.html

Copyright (c) 2018 Alexandre Brillant - JAPISOFT SARL - http://www.japisoft.com

@author Alexandre Brillant - abrillant@japisoft.com
@author JAPISOFT SARL - http://www.japisoft.com

*/
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
