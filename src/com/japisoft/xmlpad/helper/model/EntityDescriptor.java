package com.japisoft.xmlpad.helper.model;

import com.japisoft.xmlpad.SharedProperties;

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
public class EntityDescriptor extends AbstractDescriptor {
	private String name;
	private String value;

	public EntityDescriptor( String name, String value ) {
		this.name = name;
		this.value = value;	
		type = "entity";
		setComment( "Entity : " + value );
	}

	public boolean isRaw() {
		return false;
	}

	public String getName() { 
		if ( name.length() > SharedProperties.MAX_ENTITY_VISIBLE_SIZE_HELPER )
			return name.substring( 0, SharedProperties.MAX_ENTITY_VISIBLE_SIZE_HELPER ) + "...";
		return name;
	}
	
	public String getNameForHelper() {
		return getValueForHelper() + " (" + getName() + ")";
	}
	
	public String getValue() { return value; }

	public String getValueForHelper() {
		if ( value.length() > SharedProperties.MAX_ENTITY_VISIBLE_SIZE_HELPER )
			return value.substring( 0, SharedProperties.MAX_ENTITY_VISIBLE_SIZE_HELPER ) + "...";
		return value;
	}
	
	public boolean isEnabled() {
		return true;
	}
	
	public String getBuiltEntity() {
		return name + ";";
	}
	
	public String toExternalForm() {
		return name + ";";
	}
	
	public String toString() {
		return getName() + " : " + getValue();
	}

}
