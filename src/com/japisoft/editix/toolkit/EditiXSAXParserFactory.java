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
