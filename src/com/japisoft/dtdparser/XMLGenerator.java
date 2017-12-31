package com.japisoft.dtdparser;

import java.io.*;

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

// XMLGenerator ends here
