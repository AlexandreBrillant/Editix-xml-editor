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

package com.japisoft.framework.application.descriptor.composer;

import java.io.File;
import java.net.URL;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;

public class DescriptorPersistenceService {

	public Document loadDescriptor( URL source ) throws Exception {
		DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		return documentBuilder.parse( new InputSource( source.openStream() ) );
	}

	public void saveDescriptor( Element descriptor, File outputFile ) throws Exception {
		Transformer transformer = TransformerFactory.newInstance().newTransformer();
		transformer.transform( new DOMSource( descriptor ), new StreamResult( outputFile ) );
	}

	public void exportDescriptor( Element descriptor, File outputFile ) throws Exception {
		saveDescriptor( descriptor, outputFile );
	}
}