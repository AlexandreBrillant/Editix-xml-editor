package com.japisoft.framework.xml.xsd.instance;

import java.io.StringReader;

import com.japisoft.framework.collection.FastVector;
import com.japisoft.framework.xml.XMLFileData;
import com.japisoft.framework.xml.XMLToolkit;
import com.japisoft.framework.xml.parser.FPParser;
import com.japisoft.framework.xml.parser.node.FPNode;

/**
This program is available under two licenses : 

1. For non commercial usage : 

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.

2. For commercial usage :

You need to get a commercial license for source usage at : 

http://www.editix.com/buy.html

Copyright (c) 2018 Alexandre Brillant - JAPISOFT SARL - http://www.japisoft.com

@author Alexandre Brillant - abrillant@japisoft.com
@author JAPISOFT SARL - http://www.japisoft.com

*/
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

		FastVector fv = root.getDocument().getFlatNodes();
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
