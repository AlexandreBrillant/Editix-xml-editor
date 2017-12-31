package com.japisoft.framework.xml;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import com.japisoft.framework.ApplicationModel;
import com.japisoft.framework.preferences.Preferences;

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
public final class XMLParser {

	public static final String XMLPARSER_PREFERENCE_NAME = "xmlparser";
	static final String JAXP_SCHEMA_LANGUAGE = "http://java.sun.com/xml/jaxp/properties/schemaLanguage";
	static final String W3C_XML_SCHEMA = "http://www.w3.org/2001/XMLSchema";
	public static String DEF_PARSER_NAME = "XERCES";
	public static String DEF_PARSER_CLASS = "org.apache.xerces.jaxp.SAXParserFactoryImpl";
	public static String DEF_LIBRARY_PATH = "parser";
	private static Class DEF_PARSER = null;
	private static boolean ERROR_FOUND = false;
	private static boolean NAMESPACEAWARE = false;
	private static boolean SCHEMAAWARE = true;
	public static final String CONFIG_FILE = "jaxp_parser.dat";

	// -------------------------------------------------------------------------------------

	public static void resetDefaultParser() {

		NAMESPACEAWARE = Preferences.getPreference("xmlconfig",
				"namespaceAware", true);

		// Case for XERCES
		if (Preferences.getPreference("xmlconfig", "xinclude", true)) {
			System.setProperty(
					"org.apache.xerces.xni.parser.XMLParserConfiguration",
					"org.apache.xerces.parsers.XIncludeParserConfiguration");
		}

		ApplicationModel.debug( "Reset the default parser" );
		
		if ( DEF_PARSER == null && 
				!ERROR_FOUND ) {

			if ( !loadByConfigFile() ) {

				String parser = Preferences.getPreference( 
						Preferences.SYSTEM_GP,
						XMLPARSER_PREFERENCE_NAME, 
						DEF_PARSER_NAME );

				ApplicationModel.debug( "Reset from the preference " + parser );
				
				if ( !DEF_PARSER_NAME.equals( parser ) ) {

					ApplicationModel.debug( "Load from " + DEF_LIBRARY_PATH );
					
					// Search for a library inside the ext/parser directory

					ArrayList al = XMLToolkit.browseJars(DEF_LIBRARY_PATH);

					if (al.size() == 0) {
						try {
							DEF_PARSER = Class.forName(parser);
						} catch (Throwable th) {
							System.err.println("Can't use the " + parser
									+ " class");
							ERROR_FOUND = true;
						}
					} else {

						URL[] libs = new URL[al.size()];
						for (int i = 0; i < al.size(); i++)
							libs[i] = ( URL ) al.get(i);

						try {

							URLClassLoader loader = new URLClassLoader(libs);
							DEF_PARSER = loader.loadClass(parser);
					
							ApplicationModel.debug( "Find " + DEF_PARSER );
							
						} catch (Throwable th) {
							System.err.println("Can't use " + parser + " : "
									+ th.getMessage());
							ERROR_FOUND = true;
						}

					}

				}
			}

		}

	}

	static boolean loadByConfigFile() {
		try {
			ApplicationModel.debug( "Load " + CONFIG_FILE );
			Class cl = XMLToolkit.loadByConfigFile(new File(ApplicationModel
					.getAppUserPath(), CONFIG_FILE));
			if (cl != null) {
				ApplicationModel.debug( "Class found " + cl );
				DEF_PARSER = cl;
				return true;
			} else
				ApplicationModel.debug( "No class found" );
		} catch (Exception exc) {
			ApplicationModel.debug( exc );
		}
		return false;
	}

	public static SAXParserFactory getSAXParserFactory() {

		resetDefaultParser();

		SAXParserFactory factory = null;

		if ( DEF_PARSER != null ) {
			try {
				factory = (javax.xml.parsers.SAXParserFactory) DEF_PARSER
						.newInstance();
			} catch (Throwable th) {
				System.err.println( "Can't use " + DEF_PARSER + " : "
						+ th.getMessage() );
			}
		}

		if ( DEF_PARSER == null ) {
			try {
				DEF_PARSER = Class.forName(DEF_PARSER_CLASS);
				factory = (javax.xml.parsers.SAXParserFactory) DEF_PARSER
						.newInstance();
			} catch (Throwable th) {
				th.printStackTrace();
				throw new RuntimeException("ERROR CAN'T FIND "
						+ DEF_PARSER_CLASS + "???");
			}
		}

		factory.setNamespaceAware( NAMESPACEAWARE );

		ApplicationModel.debug( "Use the factory " + factory );
		
		return factory;
	}

	private static SAXParserFactory FACTORY = null;

	public static SAXParserFactory getSaxParserFactory(boolean validating) throws Exception {
		if ( FACTORY == null )
			FACTORY = getSAXParserFactory();
		
		FACTORY.setValidating( validating );
		try {
			FACTORY.setFeature( 
				"http://apache.org/xml/features/dom/include-ignorable-whitespace",
				false
			);
		} catch( Throwable th ) {}
		return FACTORY;
	}
	
	public static SAXParser getSaxParser(boolean validating) throws Exception {
		
		SAXParser parser = getSaxParserFactory(validating).newSAXParser();

/*		
 * Provoque un BUG dans le cas d'une DTD !!
 * if ( validating )
		try {
			parser.setProperty( JAXP_SCHEMA_LANGUAGE, W3C_XML_SCHEMA );
		} catch (Exception exc) {
		}*/

		ApplicationModel.debug( "Use the parser " + parser + " / validating : " + validating );
		
		return parser;
	}

}
