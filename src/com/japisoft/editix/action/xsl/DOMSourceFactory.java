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

package com.japisoft.editix.action.xsl;

import java.io.File;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.transform.dom.DOMSource;

import com.japisoft.editix.wizard.document.JSON2XMLDocument;
import com.japisoft.framework.toolkit.FileToolkit;
import com.japisoft.xmlpad.SharedProperties;
import com.japisoft.xmlpad.xml.validator.XMLPadSAXParserFactory;

public class DOMSourceFactory {

	private DOMSourceFactory() {
	}

	private static DOMSourceFactory INSTANCE = null;

	public static DOMSourceFactory Instance() {
		if ( INSTANCE == null )
			INSTANCE = new DOMSourceFactory();
		return INSTANCE;
	}
	
	public DOMSource getDOMSource( String filePath ) throws Throwable {
		DocumentBuilder builder = XMLPadSAXParserFactory.getNewDocumentBuilder( false, false );
		if ( SharedProperties.DEFAULT_ENTITY_RESOLVER != null )
			builder.setEntityResolver( SharedProperties.DEFAULT_ENTITY_RESOLVER );
		
		File f = new File( filePath );

		String type = FileToolkit.fileExt( filePath );
		if ( "json".equals( type ) || "jso".equals( type ) ) {
			return new DOMSource( JSON2XMLDocument.JSONTODOM( f ) );
		}

		return new DOMSource( builder.parse( f ) );				
	}
	
}
