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

package com.japisoft.framework.xml.xsd.instance;

import java.io.StringReader;
import java.util.List;

import com.japisoft.framework.collection.FastVector;
import com.japisoft.framework.xml.XMLFileData;
import com.japisoft.framework.xml.XMLToolkit;
import com.japisoft.framework.xml.parser.FPParser;
import com.japisoft.framework.xml.parser.node.FPNode;

public class XSDInstanceGenerator {

	public static String generateXMLInstance( String rootElement, String schemaURI ) throws Throwable {
		
		XMLFileData xfd = 
			XMLToolkit.getContentFromURI( schemaURI, null );
		String fullText = xfd.getContent();
		FPParser p = new FPParser();
		p.setFlatView( true );
		FPNode root = ( FPNode )p.parse(new StringReader(fullText)).getRoot();
		
		String targetNamespace = root.getAttribute( "targetNamespace" );
		if ( targetNamespace != null ) {
			for ( int i = 0; i < root.childCount(); i++ ) {
				root.childAt( i ).setApplicationObject( targetNamespace );
			}
		}

		resolveNamespaceReference( root );
		resolveIncludeRedefineImport( root, schemaURI );
		StringBuffer res = new StringBuffer();
		
		XSDBuildInstance xbi = new XSDBuildInstance();
		xbi.buildElement( res, rootElement, root, schemaURI, true );

		return res.toString();
	}

	private static String resetReferenceName( String name, FPNode root ) {
		int i = name.lastIndexOf( ":" );
		if ( i > -1 ) {
			String prefix = name.substring( 0, i );
			String uri = root.getNameSpaceDeclarationURI( prefix );
			if ( uri != null )
				return "{" + uri + "}" + name.substring( i + 1 );
		}
		return null;
	}
	
	private static void resolveNamespaceReference( FPNode root ) {

		// Replace all the reference with the real namespace

		List<FPNode> fv = root.getDocument().getFlatNodes();
		for ( int j = 0; j < fv.size(); j++ ) {
			
			FPNode sn = ( FPNode )fv.get( j );
			if ( sn.hasAttribute( "base" ) ) {
				
				String newBase = resetReferenceName( sn.getAttribute( "base" ), root );
				if ( newBase != null )
					sn.setAttribute( "base", newBase );

			} else
			if ( sn.hasAttribute( "ref" ) ) {

				String newRef = resetReferenceName( sn.getAttribute( "ref" ), root );
				if ( newRef != null )
					sn.setAttribute( "ref", newRef );
								
			} else
			if ( sn.hasAttribute( "type" ) ) {

				String newType = resetReferenceName( sn.getAttribute( "type" ), root );
				if ( newType != null )
					sn.setAttribute( "type", newType );
								
			}

		}		
		
	}
	
	private static void resolveIncludeRedefineImport( FPNode root, String schemaURI ) throws Throwable {

		String targetNamespace = root.getAttribute( "targetNamespace" );
		
		for ( int i = 0; i < root.childCount(); i++ ) {
			
			if ( root.childAt( i ).matchContent( "include" ) ) {
				resolveInclude( targetNamespace, root.childAt( i ), schemaURI );
			} else
			if ( root.childAt( i ).matchContent( "redefine" ) ) {
				resolveRedefine( targetNamespace, root.childAt( i ), schemaURI );
			} else
			if ( root.childAt( i ).matchContent( "import" ) ) {
				resolveImport( root.childAt( i ), schemaURI );
			}

		}

	}

	private static FPNode resolveSchemaLocation( FPNode node, String schemaURI ) throws Throwable {
		String location = node.getAttribute( "schemaLocation" );
		if ( location != null ) {
			XMLFileData xfd = XMLToolkit.getContentFromRelativeOrAbsoluteLocation( location, schemaURI );
			FPParser p = new FPParser();
			p.setFlatView( true );
			FPNode newRoot = ( FPNode )p.parse(new StringReader(xfd.getContent())).getRoot();
			resolveNamespaceReference( newRoot );
			resolveIncludeRedefineImport( newRoot, xfd.uri );
			return newRoot;
		} else 
			return null;
	}

	private static void resolveInclude( String targetNamespace, FPNode node, String schemaURI ) throws Throwable {
		// Include the root content
		FPNode newRoot = resolveSchemaLocation( node, schemaURI );
		if ( newRoot != null ) {
			FPNode parentNode = node.getFPParent();
			for ( int i = 0; i < newRoot.childCount(); i++ ) {
				newRoot.childAt( i ).setApplicationObject( targetNamespace );
				parentNode.appendChild( newRoot.childAt( i ) );
				
			}
		}
	}

	private static void resolveRedefine( String targetNamespace, FPNode node, String schemaURI ) throws Throwable {
		// Redefine the root content
		FPNode newRoot = resolveSchemaLocation( node, schemaURI );
		if ( newRoot != null ) {
			FPNode parentNode = node.getFPParent();
			for ( int i = 0; i < newRoot.childCount(); i++ ) {
				newRoot.childAt( i ).setApplicationObject( targetNamespace );
				parentNode.appendChild( newRoot.childAt( i ) );
			}
			// Add overriding elements in priority
			for ( int i = 0; i < node.childCount(); i++ ) {
				node.childAt( i ).setApplicationObject( "override" );
				parentNode.insertChildNode( 0, node.childAt( i ) );
			}
		}
	}

	private static void resolveImport( FPNode node, String schemaURI ) throws Throwable {
		// Redefine the root content
		FPNode newRoot = resolveSchemaLocation( node, schemaURI );
		if ( newRoot != null ) {
			FPNode parentNode = node.getFPParent();
			for ( int i = 0; i < newRoot.childCount(); i++ ) {
				// Store the targetNamespace for global definition
				newRoot.childAt( i ).setApplicationObject( newRoot.getAttribute( "targetNamespace" ) );
				parentNode.appendChild( newRoot.childAt( i ) );
			}
		}		
	}
	
}

