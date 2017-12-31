package com.japisoft.editix.action.dtdschema.generator;

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
public class MetaObject {

	public static final String TEXT_TYPE = "TEXT";
	public static final String DATE_TYPE = "DATE";
	public static final String TIME_TYPE = "TIME";
	public static final String BOOL_TYPE = "BOOLEAN";
	public static final String DECIMAL_TYPE = "DECIMAL";
	public static final String DOUBLE_TYPE = "DOUBLE";
	public static final String URI_TYPE = "URI";
	public static final String ID_TYPE = "ID";
	public static final String IDREF_TYPE = "IDREF";
	
	public static final String[] AVAILABLE_TYPES = {
			TEXT_TYPE,
			DATE_TYPE,
			TIME_TYPE,
			BOOL_TYPE,
			DECIMAL_TYPE,
			DOUBLE_TYPE,
			URI_TYPE,
			ID_TYPE,
			IDREF_TYPE
	};

	public static final String[] OCCURENCES = {
		"0",
		"1",
		"unbounded"
	};

	private boolean always = true;
	
	public void setAlways( boolean always ) {
		this.always = always;
	}

	public boolean isAlways() {
		return always;
	}

}
