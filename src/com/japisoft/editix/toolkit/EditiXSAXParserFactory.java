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

package com.japisoft.editix.toolkit;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.SAXException;

import com.japisoft.framework.ApplicationModel;
import com.japisoft.framework.preferences.Preferences;
import com.japisoft.framework.xml.XMLParser;
import com.japisoft.xmlpad.xml.validator.*;

/**
 * For updating the Parser
 * @author Alexandre Brillant (https://github.com/AlexandreBrillant/Editix-xml-editor)
 * @version 1.0 */
public class EditiXSAXParserFactory extends XMLPadSAXParserFactory {

	private static final String HTTP_APACHE_ORG_XML_FEATURES_XINCLUDE = "http://apache.org/xml/features/xinclude";

	protected SAXParser getNonStaticNewSAXParser( boolean validating )
		throws ParserConfigurationException, SAXException {
		try {
			SAXParserFactory spf = XMLParser.getSaxParserFactory( validating );
			try {
				if (Preferences.getPreference(
						"xmlconfig", "xinclude", true ))
					spf.setXIncludeAware( true );
			} catch (RuntimeException e) {
			}

			SAXParser sp = spf.newSAXParser();
			return sp;
		} catch( Exception exc ) {
			return super.getNonStaticNewSAXParser( validating );
		}
	}

	protected DocumentBuilderFactory getDocumentBuilderFactory() {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		
		// XInclude features
		
/*		
 * BUG FROM XERCES
 * if ( Preferences.getPreference(
				"xmlconfig", "xinclude", true ) )
		dbf.setXIncludeAware( true ); */
		
		return dbf;
	}

}

