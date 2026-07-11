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

package com.japisoft.editix.action.dtdschema.generator;

import java.util.HashMap;

import com.japisoft.framework.xml.parser.node.FPNode;

/**`
 * Generate a DTD/W3C Schema/XML Relax NG from the current document
 * @author Alexandre Brillant (https://github.com/AlexandreBrillant/Editix-xml-editor)
 * @version 1.0
 * */
public class SchemaGenerator {

	public static MetaNode getMetaModel( FPNode root ) {
		FPNode documentRoot = new FPNode( FPNode.DOCUMENT_NODE, null );
		documentRoot.appendChild( root );

		MetaNode metaRoot = new MetaNode( documentRoot, new HashMap<String, MetaNode>() );
		metaRoot.manageAttributes( documentRoot );
		metaRoot.manageChildren( documentRoot );
		metaRoot.manageMissing( documentRoot );
		
		return metaRoot;
	}

	public static String generate(
			MetaNode metaRoot,
			Transformer transformer ) {
		String newDocument = transformer.transform( ( MetaNode )metaRoot.getChildren().get( 0 ), metaRoot.getNodeCollection() );
		return newDocument;
	}
	
	public static String generate( 
			FPNode root, 
			Transformer transformer ) {
		MetaNode metaRoot = getMetaModel( root );
		return generate( metaRoot, transformer );
	}

}
