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
import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.w3c.dom.Document;

import com.japisoft.editix.wizard.document.JSON2XMLDocument;
import com.japisoft.framework.toolkit.FileToolkit;

public class StreamSourceFactory {

	private StreamSourceFactory() {
	}
	
	private static StreamSourceFactory INSTANCE = null;

	public static StreamSourceFactory Instance() {
		if ( INSTANCE == null )
			INSTANCE = new StreamSourceFactory();
		return INSTANCE;
	}
	
	public StreamSource getStreamSource( String filePath ) throws Throwable {
		String type = FileToolkit.fileExt( filePath );
		if ( "json".equals( type ) || "jso".equals( type ) ) {
			// Convert it to XML first
			Document doc = JSON2XMLDocument.JSONTODOM( new File( filePath ) );
			StringWriter sw = new StringWriter();
			TransformerFactory.newInstance().newTransformer().transform( new DOMSource( doc ), new StreamResult( sw ) );
			return new StreamSource( new StringReader( sw.toString() ) );
		}
		return new StreamSource( filePath );
	}
	
}
