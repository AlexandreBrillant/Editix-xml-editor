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

package com.japisoft.editix.ui.container.xpath;

import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class XPathRunnerResultObject {

	private Node sourceNode;
	private Node xpathResultNode;
	private XPathExpression xpathEngine;
	private String value;

	public XPathRunnerResultObject( 
			Node sourceNode, 
			XPathExpression xe ) {
		this.sourceNode = sourceNode;
		this.xpathEngine = xe;
	}

	public Node getSourceNode() {
		return sourceNode;
	}

	public String getValue() {
		if ( value == null ) {
			try {
				xpathResultNode = ( Node )xpathEngine.evaluate( 
						sourceNode, 
						XPathConstants.NODE 
				);
				if ( xpathResultNode != null ) {
					value = xpathResultNode.getNodeValue();
				}
			} catch( Exception exc ) {
				value = "XPath Error";
			}
		}
		return value;
	}

	public void setValue( String value ) {
		if ( xpathResultNode != null ) {
			xpathResultNode.setNodeValue( value );
			this.value = value;
		} else {
			if ( sourceNode instanceof Element ) {
				sourceNode.appendChild( sourceNode.getOwnerDocument().createTextNode( value ) );
			}
		}
	}

}
