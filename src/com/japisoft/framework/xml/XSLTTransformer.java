package com.japisoft.framework.xml;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;

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
public final class XSLTTransformer {

	public static String DEF_TRANS_NAME = "XALAN";

	//public static String DEF_TRANS_CLASS = "org.apache.xalan.processor.TransformerFactoryImpl";
	// Use SAXON 1 due to wrong error messages
	public static String DEF_TRANS_CLASS = "com.icl.saxon.TransformerFactoryImpl";
	
	public static String DEF_LIBRARY_PATH = "xslt";

	private static Class DEF_TRANS = null;

	private static boolean ERROR_FOUND = false;

	public static final String CONFIG_FILE = "jaxp_transformer.dat";

	public static void resetDefaultTransformer() {

		// The default parser is xerces
		System.setProperty("javax.xml.parsers.SAXParserFactory",
				"org.apache.xerces.jaxp.SAXParserFactoryImpl");

		ApplicationModel.debug( "Reset the default transformer" );
				
		if (DEF_TRANS == null && !ERROR_FOUND) {

			if (!loadByConfigFile()) {

				String transformer = Preferences.getPreference("xmlconfig",
						"transformer", DEF_TRANS_NAME);

				if (!DEF_TRANS_NAME.equals(transformer)) {

					// Search for a library inside the ext/xslt directory

					ArrayList al = XMLToolkit.browseJars(DEF_LIBRARY_PATH);

					if (al.size() == 0) {

						try {
							DEF_TRANS = Class.forName(transformer);
						} catch (Throwable th) {
							System.err.println("No library found inside the "
									+ DEF_LIBRARY_PATH + " directory");
							System.err.println("Can't use the " + transformer
									+ " class");
							ERROR_FOUND = true;
						}
					} else {

						URL[] libs = new URL[al.size()];
						for (int i = 0; i < al.size(); i++)
							libs[i] = (URL) al.get(i);

						try {

							URLClassLoader loader = new URLClassLoader(libs);
							DEF_TRANS = loader.loadClass(transformer);
							
						} catch (Throwable th) {
							ERROR_FOUND = true;
							System.err.println("Can't use " + transformer
									+ " : " + th.getMessage());
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
				DEF_TRANS = cl;
				return true;
			} else
				ApplicationModel.debug( "No class found" );
		} catch (Exception exc) {
		}
		return false;
	}

	public static TransformerFactory getTransformerFactory() {

		resetDefaultTransformer();

		TransformerFactory factory = null;

		if (DEF_TRANS != null) {
			try {
				factory = (TransformerFactory) DEF_TRANS.newInstance();
			} catch (Throwable th) {
				th.printStackTrace();
				System.err.println("Can't use " + DEF_TRANS + " : "
						+ th.getMessage());
			}
		}

		if (DEF_TRANS == null) {
			try {
				DEF_TRANS = Class.forName(DEF_TRANS_CLASS);
				factory = (TransformerFactory) DEF_TRANS.newInstance();
			} catch (Throwable th) {
				th.printStackTrace();
				throw new RuntimeException("ERROR CAN'T FIND "
						+ DEF_TRANS_CLASS + "???");
			}
		}

		ApplicationModel.debug( "Use the factory " + factory );
		
		return factory;
	}

	private static TransformerFactory FACTORY = null;

	public static Transformer getTransformer() throws Exception {

		if (FACTORY == null)
			FACTORY = getTransformerFactory();

		Transformer transformer = FACTORY.newTransformer();

		ApplicationModel.debug( "Use the transformer " + transformer );
		
		return transformer;
	}

}
