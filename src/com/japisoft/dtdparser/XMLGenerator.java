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

import java.io.*;

/**
 * Interface for generating XML document from the DTD
 * 
 * @author Alexandre Brillant (https://github.com/AlexandreBrillant/Editix-xml-editor)
 * @version 1.0
 * @see com.japisoft.dtdparser.node.RootDTDNode */
public interface XMLGenerator {

    // Encoding type

    public static String ENCODING_UTF_8="UTF-8";
    public static String ENCODING_UTF_16="UTF-16";
    public static String ENCODING_ISO_10646_UCS_2="ISO-10646-UCS-2";
    public static String ENCODING_ISO_10646_UCS_4="ISO-10646-UCS-4";
    public static String ENCODING_ISO_8859_1="ISO-8859-1";
    public static String ENCODING_ISO_8859_2="ISO-8859-2";
    public static String ENCODING_ISO_8859_3="ISO-8859-3";
    public static String ENCODING_ISO_8859_4="ISO-8859-4";
    public static String ENCODING_ISO_8859_5="ISO-8859-5";
    public static String ENCODING_ISO_8859_6="ISO-8859-6";
    public static String ENCODING_ISO_8859_7="ISO-8859-7";
    public static String ENCODING_ISO_8859_8="ISO-8859-8";
    public static String ENCODING_ISO_8859_9="ISO-8859-9";
    public static String ENCODING_ISO_2022_JP="ISO-2022-JP";
    public static String ENCODING_Shift_JIS="Shift_JIS";
    public static String ENCODING_EUC_JP="EUC-JP";

    /** Generate a minimal valid XML document. You may insert an encoding using the <code>ENCODING_...</code> constants
	@param output final document target
	@param encoding use <code>ENCODING..<code> constants
	@param root the root node for the generation
	@param dtdURI dtd location */
    public void writeDocument( PrintWriter output, String encoding, String root, String dtdURI ) throws IOException;

    /** Generate a minimal valid XML document. 
	@param output final document target
	@param root the root node for the generation
	@param dtdURI dtd location */
    public void writeDocument( PrintWriter output, String root, String dtdURI ) throws IOException;

}


