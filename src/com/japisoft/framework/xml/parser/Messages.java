package com.japisoft.framework.xml.parser;
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
