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

package com.japisoft.dtdparser;

/**
 * Element location
 * @author Alexandre Brillant (https://github.com/AlexandreBrillant/Editix-xml-editor)
 * @version 1.0 */
class ParserElement {
	/** Stop location */
	public int stop = -1;
	/** Character sopping the element */
	public char token = 0;
	public char occ = 0;
	public String content = null;
	public boolean literal = false;

	public ParserElement() {
	}

	public boolean hasToken() {
		return token != 0;
	}

	public static boolean isLiteral(char ch) {
		return ch == '\'' || ch == '"';
	}

	public boolean isWord() {
		return (content != null);
	}

	public static boolean isOperator(char ch) {
		return ch == '?' || ch == '*' || ch == '+';
	}

	public static boolean isToken(char ch) {
		return ch == '('
			|| ch == ')'
			|| ch == ','
			|| ch == '|'
			|| ch == '>'
			|| ch == '%'
			|| ch == ';'
			|| isOperator(ch);
	}

}


