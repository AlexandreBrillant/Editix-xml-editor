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

package com.japisoft.xmlpad.editor;

import javax.swing.text.BadLocationException;

import com.japisoft.xmlpad.editor.XMLEditor;

public class ExpressionHighlighterManager {

	public static void highlight( XMLEditor editor ) {

		// Remove the previous one
		editor.removeHighlightExpression();

		try {
			String part = 
				editor.getText( editor.getCaretPosition(), 1 );
			char c = 
				part.charAt( 0 );
			
			int searchDirection = +1;
			char o = 0;

			boolean beginPart = ( c == '<' || c == '(' || c == '{' || c == '[' );
			if ( !beginPart ) {
				part = editor.getText( editor.getCaretPosition() - 1, 1 );
				c = part.charAt( 0 );
			}
			boolean endPart = ( c == '>' || c == ')' || c == '}' || c == ']' );
			
			int location = editor.getCaretPosition();
						
			if ( endPart ) {
				searchDirection = -1;
				if ( c == '>' )
					o = '<';
				else
				if ( c == ')' )
					o = '(';
				else
				if ( c == '}' )
					o = '{';
				else
				if ( c == ']' )
					o = '[';
				location--;
			} else
			if ( beginPart ) {
				if ( c == '<' )
					o = '>';
				else
				if ( c == '(' )
					o = ')';
				else
				if ( c == '{' )
					o = '}';
				else
				if ( c == '[' )
					o = ']';
				location++;
			} else
				return;	// Nothing to check

			int counter = 0;

			for ( int i = 0; i < 1000; i++ ) {
				
				location += searchDirection;
				String tmp = 
					editor.getText( location, 1 );
				
				if ( tmp.charAt( 0 ) == c ) {
					counter++;
				}
				
				if ( tmp.charAt( 0 ) == o ) {
					if ( counter == 0 ) {
						
						editor.highlightExpression( location );
						
						break;
					}
					counter--;
				}
				
			}
			
		} catch ( BadLocationException e ) {
		}
		
	}

}
