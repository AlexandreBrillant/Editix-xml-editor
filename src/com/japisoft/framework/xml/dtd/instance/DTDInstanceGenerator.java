// Editix XML Editor
// https://www.editix.com
// Copyright (c) 2026 Alexandre Brillant
//
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
// AI Training Restriction :
// This source code is provided for human use only.
// Using this code to train, fine-tune, or develop AI models,
// machine learning systems, or similar technologies is
// STRICTLY PROHIBITED. Violations will terminate all rights
// under the applicable license.

package com.japisoft.framework.xml.dtd.instance;

import java.io.StringReader;

import com.japisoft.dtdparser.DTDParser;
import com.japisoft.dtdparser.node.RootDTDNode;
import com.japisoft.framework.xml.XMLFileData;
import com.japisoft.framework.xml.XMLToolkit;

public class DTDInstanceGenerator {

	public static String generateXMLInstance( String rootElement, String dtdURI, String dtdContent ) throws Throwable {

		XMLFileData data = null;
		if ( dtdContent == null )
			data = XMLToolkit.getContentFromURI( dtdURI, null );
		else
			data = new XMLFileData( null, dtdContent );
		DTDParser parser = new DTDParser();
		parser.parse( new StringReader( data.getContent() ) );
		RootDTDNode node = parser.getDTDElement();
		StringBuffer res = new StringBuffer();
		DTDBuildInstance dbi = new DTDBuildInstance();
		dbi.buildElement( res, rootElement, node );
		return res.toString();

	}

}
