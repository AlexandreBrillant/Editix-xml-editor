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

package com.japisoft.editix.action.search.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import javax.xml.transform.dom.DOMSource;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.japisoft.framework.xml.XMLFileData;
import com.japisoft.framework.xml.XMLToolkit;
import com.japisoft.framework.xml.parser.FPParser;
import com.japisoft.framework.xml.parser.dom.DocumentImpl;
import com.japisoft.framework.xml.parser.dom.DomNodeFactory;
import com.japisoft.framework.xml.parser.node.FPNode;

public class XPathSearchEngineImpl implements SearchEngine {

	public List search( File f, String item ) {
		try {
			String l = null;
			
			ArrayList res = null;

			FPParser p = new FPParser();
			p.setNodeFactory( 
					new DomNodeFactory() 
			);

			XMLFileData xfd = XMLToolkit.getContentFromInputStream( 
					new FileInputStream( f ), 
					null 
			);

			Node nn = new DocumentImpl( (Element)p.parse(new StringReader(xfd.getContent())).getRoot() );
			

			XPath xpath = XPathFactory.newInstance().newXPath();
			XPathExpression exprt =xpath.compile( item );
			NodeList list = (NodeList)exprt.evaluate( nn, XPathConstants.NODESET ); 

			res = new ArrayList();
			for ( int i = 0; i < list.getLength(); i++ ) {
				FPNode sn = ( FPNode )list.item( i );
				String line = xfd.getContent().substring( sn.getStartingOffset(), sn.getStoppingOffset() );
				SearchResult sr = new SearchResultImpl( line, sn.getStartingLine() - 1 );
				res.add( sr );
			}

			return res;
		} catch (Throwable e) {
			return null;
		}

	}

	public String toString() {
		return "XPath expression";
	}

}
