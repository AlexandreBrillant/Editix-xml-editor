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

package com.japisoft.editix.ui.container.xpath;

import javax.swing.SwingUtilities;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.japisoft.editix.ui.container.xpath.XPathEditorModel.XPathColumn;

public class XPathRunner implements Runnable {

	private XPathResultTableModel model;
	
	public XPathRunner( XPathResultTableModel model ) {
		this.model = model;
	}

	public void run() {
		String globalXPath = this.model.getXPathEditorModel().getXPath();
		Document document = this.model.getSource();

		// XPathFactory factory = XPathFactory.newInstance();
		
		XPathFactory factory = new net.sf.saxon.xpath.XPathFactoryImpl(); 
		
		XPath xpath = factory.newXPath();
		
		try {
			NodeList nl = ( NodeList )xpath.evaluate( 
				globalXPath, 
				document, 
				XPathConstants.NODESET 
			);
			for ( int i = 0; i < nl.getLength(); i++ ) {
				addResult( xpath, nl.item( i ) );
			}
			SwingUtilities.invokeLater(
				new Runnable() {
					public void run() {
						model.fireTableUpdate();						
					}
				} 
			);
		} catch( Exception exc ) {
			model.setErrorFound( exc.getMessage() );
		}
		
	}

	public static XPathRunnerResultObject[] getResultObject( XPathEditorModel columns, XPath xpath, Node node ) throws Exception {
		XPathRunnerResultObject[] objects = new XPathRunnerResultObject[ columns.getColumnCount() ];
		for ( int i = 0; i < columns.getColumnCount(); i++ ) {
			XPathColumn xpc = columns.getColumn( i );
			XPathExpression xe = xpc.getXPathExpression( xpath );
			XPathRunnerResultObject re = new XPathRunnerResultObject( node, xe );
			objects[ i ] = re;
		}
		return objects;
	}

	private void addResult( XPath xpath, Node node ) throws Exception {
		XPathEditorModel columns = this.model.getXPathEditorModel();	
		model.addResult( getResultObject( columns, xpath, node ) );
	}

}

