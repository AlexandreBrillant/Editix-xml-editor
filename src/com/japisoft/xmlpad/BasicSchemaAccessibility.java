package com.japisoft.xmlpad;

import java.net.URL;

import com.japisoft.framework.xml.SchemaLocator;

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
class BasicSchemaAccessibility implements SchemaAccessibility {

	XMLContainer container = null;
	
	public BasicSchemaAccessibility( XMLContainer container ) {
		this.container = container;
	}
	
	public String getCurrentDTD() {
		return container.getCurrentDTD();
	}

	public String getCurrentDTDRoot() {
		return container.getCurrentDTDRoot();
	}

	public String getCurrentSchema() {
		return container.getCurrentSchema();
	}

	public String getCurrentSchemaRoot() {
		return container.getCurrentSchemaRoot();
	}

	public String getDefaultDTDLocation() {
		return container.getDefaultDTDLocation();
	}

	public String getDefaultDTDRoot() {
		return container.getDefaultDTDRoot();
	}

	public String getDefaultSchemaLocation() {
		return container.getDefaultSchemaLocation();
	}

	public String getDefaultSchemaRoot() {
		return container.getDefaultSchemaRoot();
	}

	public String getDTDLocation(boolean resolve) {
		return container.getDTDLocation(resolve);
	}

	public SchemaLocator getRelaxNGValidationLocation() {
		return container.getRelaxNGValidationLocation();
	}

	public String getSchemaLocation(boolean resolve) {
		return container.getSchemaLocation(resolve);
	}

	public void setDefaultDTD(String dtdRoot, String dtd) {
		container.setDefaultDTD(dtdRoot, dtd);
	}

	public void setDefaultDTD(String dtdRoot, URL dtd) {
		container.setDefaultDTD(dtdRoot, dtd);
	}

	public void setDefaultSchema(String root, String location) {
		container.setDefaultSchema(root, location);
	}

	public void setDTD(String dtdRoot, String dtdLocation, int dtdDeclarationLine ) {
		container.setDTD(dtdRoot, dtdLocation, dtdDeclarationLine );
	}

	public void setDTD( String dtdRoot, SchemaLocator locator ) throws Exception {
		container.setDTD( dtdRoot, locator );
	}

	public void setRelaxNGValidationLocation(String location) {
		container.setRelaxNGValidationLocation(new SchemaLocator(
				location));
	}

	public void setRelaxNGValidationLocation(SchemaLocator locator) {
		container.setRelaxNGValidationLocation(locator);
	}

	public void setSchema( String schemaRoot, String[] namespace, String[] schemaLocation, int schemaDeclaration ) {
		container.setSchema( schemaRoot, namespace, schemaLocation, schemaDeclaration );
	}

	/** This is only for inner usage, it mustn't be called by the user */
	public void dispose() {
		this.container = null;
	}
}
