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

package com.japisoft.editix.document.schema;

import java.util.Enumeration;
import java.util.Iterator;

import com.japisoft.framework.xml.parser.node.FPNode;
import com.japisoft.framework.xml.parser.walker.NodeNameCriteria;
import com.japisoft.framework.xml.parser.walker.OrCriteria;
import com.japisoft.framework.xml.parser.walker.TreeWalker;
import com.japisoft.xmlpad.XMLContainer;
import com.japisoft.xmlpad.editor.XMLPadDocument;
import com.japisoft.xmlpad.helper.handler.AbstractHelperHandler;
import com.japisoft.xmlpad.helper.model.BasicDescriptor;

public class SchemaTypeHandler extends AbstractHelperHandler {

	protected void installDescriptors( 
			FPNode currentNode,
			XMLPadDocument document, 
			int offset,
			String addedString ) {	

		XMLContainer container = document.getContainer();
		if ( container == null )
			return;
		FPNode root = container.getRootNode();
		if ( root == null )
			return;

		String prefix = root.getNameSpacePrefix();
		if ( prefix == null )
			prefix = "";
		else
			prefix = prefix + ":";
		
		if ( addedString == null )
			addedString = "";
	
		// Add complex type
		TreeWalker tw = new TreeWalker( root );
		Iterator enume = tw.getNodeByCriteria(
				new OrCriteria(
						new NodeNameCriteria( "complexType" ),
						new NodeNameCriteria( "simpleType" ) )
						, false );
		String prefixComplexType = "";
		if ( enume != null ) {
			// Check for targetNameSpace
			if ( root.hasAttribute( "targetNamespace" ) ) {
				String namespace = root.getAttribute( "targetNamespace" );
				Iterator<String> enum2 = root.getNameSpaceDeclaration();
				// Search a prefix
				if ( enum2 != null ) {
					while ( enum2.hasNext() ) {
						String p = ( String )enum2.next();
						if ( namespace.equals( root.getNameSpaceDeclarationURI( p ) ) ) {
							prefixComplexType = p + ":";
							break;
						}
					}
				}
			}
			while ( enume.hasNext() ) {
				FPNode n = ( FPNode )enume.next();
				String name = n.getAttribute( "name" );
				if ( name != null )
					addDescriptor( new BasicDescriptor( prefixComplexType + name ) );
			}
		}

		for ( int i = 0; i < PRIM_TYPES.length; i++ ) {
			addDescriptor(
					new BasicDescriptor( 
							addedString + 
							prefix + 
							PRIM_TYPES[ i ] + 
							addedString ) );					
		}
	}

	protected String getActivatorSequence() {
		return null;
	}

	public boolean haveDescriptors(
			FPNode currentNode,
			XMLPadDocument document, 
			boolean insertBefore, 
			int offset,
			String activatorString ) {
		if ( activatorString == null ) {
			return match(
				document, 
				offset, 
				"", 
				"type=\"" );
		} else {
			if  ( "\"".equals( activatorString ) ) {
				return match(
						document, 
						offset, 
						"", 
						"type=" );
			} else
				return false;
		}
	}

	public String getTitle() {
		return "W3C Schema Types";
	}

	public int getPriority() {
		return 1;
	}

	private static String[] PRIM_TYPES = {
		"string",
		"boolean",
		"float",
		"double",
		"decimal",
		"duration",
		"dateTime",
		"time",
		"date",
		"gYearMonth",
		"gYear",
		"gMonthDay",
		"gDay",
		"gMonth",
		"hexBinary",
		"base64Binary",
		"anyURI",
		"QName",
		"NOTATION",
		"normalizedString",
		"token",
		"language",
		"IDREFS",
		"ENTITIES",
		"NMTOKEN",
		"NMTOKENS",
		"Name",
		"NCName",
		"ID",
		"IDREF",
		"ENTITY",
		"integer",
		"nonPositiveInteger",
		"negativeInteger",
		"long",
		"int",
		"short",
		"byte",
		"nonNegativeInteger",
		"unsignedLong",
		"unsignedInt",
		"unsignedShort",
		"unsignedByte",
		"positiveInteger",
	};
	
}