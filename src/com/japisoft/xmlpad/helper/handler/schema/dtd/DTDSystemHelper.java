package com.japisoft.xmlpad.helper.handler.schema.dtd;

import com.japisoft.xmlpad.helper.model.SystemHelper;
import com.japisoft.xmlpad.helper.model.TagDescriptor;

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
public class DTDSystemHelper extends SystemHelper {

	public DTDSystemHelper() {
		super( true );
		addTagDescriptor( new TagDescriptor( "-- -->", null, true, true ) );
		addTagDescriptor( new TagDescriptor( "ELEMENT >", null, true, true ) );
		addTagDescriptor( new TagDescriptor( "ATTLIST >", null, true, true ) );
		addTagDescriptor( new TagDescriptor( "ENTITY >", null, true, true ) );
		addTagDescriptor( new TagDescriptor( "NOTATION >", null, true, true ) );
	}

}
