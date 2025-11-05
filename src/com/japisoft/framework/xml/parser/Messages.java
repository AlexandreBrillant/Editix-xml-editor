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

package com.japisoft.framework.xml.parser;

/**
 * This class store all error message. This is useful for translating
 * each error message to another language with a resource bundle.
 * @author Alexandre Brillant (https://github.com/AlexandreBrillant/Editix-xml-editor)
 * @version 1.0
 * */
public final class Messages {
	
	public static String ERROR_ENTITY1 = "Invalid entity value";
	public static String ERROR_ENTITY2 = "Invalid empty value";
	public static String ERROR_ENTITY3 = "Invalid value";
	public static String ERROR_ENTITY4 = "Invalid character &, need entity";
	public static String ERROR_ENTITY5 = "Invalid empty entity";
	public static String ERROR_ENTITY6 = "Invalid entity";
	
	public static String ERROR_TAG1 = "The main document tag has not been closed";
	public static String ERROR_TAG2 = "Bad closing tag";
	public static String ERROR_TAG3 = "wait for";
	public static String ERROR_TAG4 = "Invalid close instruction";
	
	public static String ERROR_PREFIX1 = "Unknown prefix";
	public static String ERROR_PREFIX2 = "Unknown prefix URI for";
	
	public static String ERROR1 = "Syntax error";
	public static String ERROR2 = "Error while parsing";
	public static String LINE = "Line";
	
	public static String ERROR_PROLOG = "Invalid XML prolog : need such declaration  <?xml version=\"1.0\"?>";
	
}

