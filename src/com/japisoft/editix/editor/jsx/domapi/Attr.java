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

package com.japisoft.editix.editor.jsx.domapi;

/**
 * The Attr interface represents one of an element's attributes as an object. In most situations, you will directly retrieve the attribute value as a string (e.g., Element.getAttribute()), but certain functions (e.g., Element.getAttributeNode()) or means of iterating return Attr instances.
 */
public class Attr extends Node {

	public String localName;
	public String name;
	public String namespaceURI;
	public Element ownerElement;
	public String prefix;
	public String value;
	
	public Attr( Document owner, org.w3c.dom.Node node ) {
		super( owner, node );
		
		this.localName = node.getLocalName();		
		this.namespaceURI = node.getNamespaceURI();
		this.prefix = node.getPrefix();
		
		if ( node instanceof org.w3c.dom.Attr ) {
			this.name = ((org.w3c.dom.Attr)node).getName();
			this.value = ((org.w3c.dom.Attr)node).getValue();
		}
	}
	
	public Attr( Element ownerElement, org.w3c.dom.Node node ) {
		this( ownerElement.getOwner(), node );
		this.ownerElement = ownerElement;
	}
	
}
