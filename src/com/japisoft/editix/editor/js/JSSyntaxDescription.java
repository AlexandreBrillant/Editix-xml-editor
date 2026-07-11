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

package com.japisoft.editix.editor.js;

public class JSSyntaxDescription {
	
	// Fast test
	public boolean isDelimiter( char c ) {
		return ( c == '{' ) || 
					( c == '}' ) || 
						( c == ';' ) || 
							( c == '(' ) || 
								( c == ')' ) || 
										( c == '[' ) ||
											( c == ']' ) ||
												( c == ',' );
	}

	private static char[] DELIMITERS = {
		'{',
		'}',
		';',
		'(',
		')',
		',',
		'[',
		']'
	};

	public char[] getDelimiters() {
		return DELIMITERS;
	}
	
	public boolean isStringDelimiter( char c ) {
		return c == '\'' || c == '\"';
	}

	public boolean isStartingLinesComment( char c, char oldc ) {
		return c == '*' && oldc == '/';
	}
	
	public boolean isStoppingLinesComment( char c, char oldc ) {
		return c == '/' && oldc == '*'; 
	}
	
	public boolean isStartingLineComment( char c, char oldc ) {
		return c == '/' && oldc == '/';
	}
	
	public String[] getKeywords() {
		return RESERVED_KEYWORDS;
	}

	// JS Keywords ECMA Script 6
	private static String[] RESERVED_KEYWORDS = {
		"break",
		"case",
		"class",
		"catch",
		"const",
		"continue",
		"debugger",
		"default",
		"delete",
		"do",
		"else",
		"export",
		"extends",
		"finally",
		"for",
		"function",
		"if",
		"import",
		"in",
		"instanceof",
		"let",
		"new",
		"null",
		"return",
		"super",
		"switch",
		"this",
		"throw",
		"try",
		"typeof",
		"var",
		"void",
		"while",
		"with",
		"yield"
	};

}
