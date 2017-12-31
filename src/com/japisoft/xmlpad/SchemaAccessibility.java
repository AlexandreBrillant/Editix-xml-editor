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
public interface SchemaAccessibility {

	/** @return the current DTD location */
	public String getCurrentDTD();
	
	/** @return the current DTD root */	
	public String getCurrentDTDRoot();

	/** Reset the default document DTD location
	 * @param dtdRoot The DTD Root element
	 * @param dtd The DTD path or URL */
	public void setDefaultDTD(String dtdRoot, String dtd);

	/** Reset the default document DTD location
	 * @param dtdRoot The DTD Root element
	 * @param dtd The DTD inputStream content
	 */	
	public void setDefaultDTD( String dtdRoot, URL dtd );

	/** Reset the current dtd root and dtd location by this one. This is done automatically for 
	 * a new document and for parsing action. If you want default dtd, call rather the
	 * <code>setDefaultDTD</code> method */
	public void setDTD(String dtdRoot, String dtdLocation, int dtdDeclarationLine );

	/** Reset a local DTD. The SchemaLocator object will contain a path or a stream towards a full
	 * DTD. This is used for sample for a local DTD declaration. Don't use this method if
	 * your know the DTD location or URL.
	 * @param dtdRoot the dtd root element
	 * @param locator  The dtd content
	 * @exception Exception if a problem occurs when using this locator
	 * */
	public void setDTD( String dtdRoot, SchemaLocator locator ) throws Exception;

	/** @return the current schema location */
	public String getCurrentSchema();

	/** @return the current schema root */
	public String getCurrentSchemaRoot();

	/** Reset the current schema location 
	 * @param root Schema root tag
	 * @param namespace list of namespace bound to the schema location
	 * @param schemaLocation Schema URL or current document relative location 
	 * @param schemaDeclarationLine The location of the declaration inside the Document, you can ignore this value and put -1
	 * */
	public void setSchema(String schemaRoot, String[] namespace, String[] schemaLocation, int schemaDeclarationLine );

	/** @return the current DTD root element */
	public String getDefaultDTDRoot();
	
	/** @return the current DTDLocation built with the current document location */
	public String getDefaultDTDLocation();
	
	/** 
	 * @param resolve if <code>true</code> the location is built using the current document location
	 * @return the current DTD location. This DTD location can take into account relative document location 
	 * */
	public String getDTDLocation(boolean resolve);
	
	/** @return the default schema file/url location */
	public String getDefaultSchemaLocation();
	
	/** @return the default schema root tag */
	public String getDefaultSchemaRoot();
	
	/** Reset the default schema root tag and document location (url or file path) */
	public void setDefaultSchema(String root, String location);
	
	/** Reset the current RelaxNG document path for validating the current document. At this moment, 
	 * there's no way to embedded th RelaxNG usage inside the XML document like for the DTD or the
	 * schema, so you Must specify the relaxNG location each time you need it for parsing. Note that to 
	 * remove this option, you have to call it with a <code>null</code> value */
	public void setRelaxNGValidationLocation(String location);

	/** Reset the current RelaxNG document path for validating the current document */	
	public void setRelaxNGValidationLocation( SchemaLocator locator );

	/** @return the current RelaxNG documant location */
	public SchemaLocator getRelaxNGValidationLocation();
	
	/** 
	 * @param resolve if <code>true</code> the location is built using the current document location
	 * @return the current Schema location. This schema location can take into account relative document location
	 */
	public String getSchemaLocation(boolean resolve) ;

	/** This is only for inner usage, it mustn't be called by the user */
	void dispose();

}
