package com.japisoft.xmlpad.helper.model;

import com.japisoft.framework.xml.parser.node.FPNode;
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
public interface TagHelper extends Helper {
	public void setNamespace( String namespace );
	/** @return available tags */
	public TagDescriptor[] getTags();
	/** @return a tag descriptor for this tag name */
	public TagDescriptor getTag( FPNode node );
	/** add a new descriptor */
	public TagDescriptor addTagDescriptor( TagDescriptor tag );
	/** Reset the current location for computing available tags */
	public void setLocation( FPNode locationPath, int offset );
	/** Force a new namespace prefix */
	//public void setForcePrefix( String locationPrefix );
	/** @return the initial schema location */
	public String getSource();
}
