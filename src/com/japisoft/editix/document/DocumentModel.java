package com.japisoft.editix.document;

import java.awt.Color;
import java.io.InputStream;
import java.net.URL;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.StringTokenizer;

import com.japisoft.editix.ui.EditixFactory;
import com.japisoft.framework.ApplicationModel;
import com.japisoft.framework.preferences.Preferences;
import com.japisoft.framework.xml.Encoding;
import com.japisoft.framework.xml.parser.FPParser;
import com.japisoft.framework.xml.parser.node.FPNode;
import com.japisoft.framework.xml.parser.walker.NodeNameCriteria;
import com.japisoft.framework.xml.parser.walker.TreeWalker;
import com.japisoft.xmlpad.Debug;
import com.japisoft.xmlpad.XMLDocumentInfo;
import com.japisoft.xmlpad.xml.validator.Validator;

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
public final class DocumentModel {

	private static ArrayList model = null;
	public static String[] DOCUMENT_TYPE;
	static Hashtable PARENT_TYPE;
	
	static {
		PARENT_TYPE = new Hashtable();
		loadModel();
	}

	public static String getParentType( String type ) {
		return ( String )PARENT_TYPE.get( type );
	}

	public static void loadModel() {
		// Search documents.xml

		InputStream input = ClassLoader
				.getSystemResourceAsStream( "documents.xml" );
		if ( input == null ) {
			System.err.println( "Can't find documents.xml !" );
			System.exit(1);
			return;
		}

		FPParser p = new FPParser();
		try {
			FPNode sn = ( FPNode )p.parse(input).getRoot();

			model = new ArrayList();
			
			for ( int i = 0; i < sn.childCount(); i++ ) {
				model.add(
					buildDocument( 
							sn.childAt( i ) ) );
			}

			DOCUMENT_TYPE = new String[getDocumentCount()];
			for (int i = 0; i < getDocumentCount(); i++) {
				XMLDocumentInfo info = getDocumentAt(i);
				DOCUMENT_TYPE[i] = info.getType();
			}
		} catch (Exception exc) {
			System.err.println("Can't parse documents.xml : "
					+ exc.getMessage() + " !");
			exc.printStackTrace();
			System.exit(1);
		}
	}

	public static int getDocumentCount() {
		if ( model == null ) {
			return 0;
		}
		return model.size();
	}

	static XMLDocumentInfo prepareDocument( XMLDocumentInfo newDoc ) {
 		newDoc.setParam( "default-encoding", Preferences.getPreference(
 				"template", "default-encoding", Encoding.BASIC_ENCODINGS )[ 0 ] );
 		newDoc.setParam( "company", Preferences.getPreference(
 				"template", "company", "" ) );
 		newDoc.setParam( "firstName", Preferences.getPreference(
 				"template", "firstName", "" ) );
 		newDoc.setParam( "lastName", Preferences.getPreference(
 				"template", "lastName", "" ) );
 		newDoc.setParam( "address", Preferences.getPreference(
 				"template", "address", "" ) );
 		newDoc.setParam( "phone", Preferences.getPreference(
 				"template", "phone", "" ) ); 		 		
 		newDoc.setParam( "email", Preferences.getPreference(
 				"template", "email", "" ) );
 		newDoc.setParam( "website", Preferences.getPreference(
 				"template", "website", "" ) );
 		return newDoc;
	}
	
	public static XMLDocumentInfo getDocumentForType( String type ) {
		for (int i = 0; i < getDocumentCount(); i++) {
			XMLDocumentInfo doc = getDocumentAt( i );			
			if ( doc.getType().equals( type ) ) {
				return prepareDocument( doc );
			}
		}
		return getDefaultDocument();
	}

	/* No default document */
	public static XMLDocumentInfo getDocumentForType2( String type ) {
		for (int i = 0; i < getDocumentCount(); i++) {
			XMLDocumentInfo doc = getDocumentAt( i );			
			if ( doc.getType().equals( type ) ) {
				return prepareDocument( doc );
			}
		}
		return null;
	}
		
	/** @return the good XMLDocumentInfo matching this file extension */
	public static XMLDocumentInfo getDocumentForExt( String fileExt ) {
		if ( fileExt != null )
		for ( int i = 0; i < getDocumentCount(); i++ ) {
			XMLDocumentInfo doc = getDocumentAt(i);
			if (doc.matchFileExt( fileExt ) )
				return doc;
		}
		return getDefaultDocument();
	}

	/** @return the good XMLDocumentInfo matching this file extension. No default document */
	public static XMLDocumentInfo getDocumentForExt2( String fileExt ) {
		if ( fileExt != null )
		for ( int i = 0; i < getDocumentCount(); i++ ) {
			XMLDocumentInfo doc = getDocumentAt(i);
			if (doc.matchFileExt( fileExt ) )
				return doc;
		}
		return null;
	}

	public static XMLDocumentInfo getDocumentAt( int index ) {
		if ( model == null ) {
			return null;
		}
		return ( XMLDocumentInfo ) model.get( index );
	}

	static XMLDocumentInfo defDoc = null;

	public static XMLDocumentInfo getDefaultDocument() {
		if ( defDoc == null ) {
			defDoc = getDocumentForType( "XML" );
		}
		return defDoc;
	}

	/** @return a XMLDocumentInfo depending on the fileName Ext */
	public static XMLDocumentInfo getDocumentByFileName( String fileName ) {
		if ( fileName == null )
			return new XMLDocumentInfo();
		String ext = "xml";
		int i = fileName.lastIndexOf( "." );
		if ( i > -1 )
			ext = fileName.substring( i + 1 );
		XMLDocumentInfo info = DocumentModel.getDocumentForExt( ext );
		return info;
	}

	/** @return a XMLDocumentInfo depending on the fileName Ext. No default document ! */
	public static XMLDocumentInfo getDocumentByFileName2( String fileName ) {
		if ( fileName == null )
			return new XMLDocumentInfo();
		String ext = "xml";
		int i = fileName.lastIndexOf( "." );
		if ( i > -1 )
			ext = fileName.substring( i + 1 );
		XMLDocumentInfo info = DocumentModel.getDocumentForExt2( ext );
		return info;
	}

	/** @return the type of the document using the file extension */
	public static String getTypeForFileName( String fileName ) {
		XMLDocumentInfo info = getDocumentByFileName( fileName );
		return info.getType();
	}

	/** @return the type of the document using the file extension */
	public static String getTypeForFileName2( String fileName ) {
		XMLDocumentInfo info = getDocumentByFileName2( fileName );
		if ( info == null )
			return null;
		return info.getType();
	}
		
	static XMLDocumentInfo buildDocument( FPNode n ) {
		XMLDocumentInfo info = new XMLDocumentInfo();

		if ( defDoc == null )
			defDoc = info;

		// UI
		FPNode ui = ( FPNode ) n.childAt( 0 );
		String icon = ui.getAttribute( "icon" );
		boolean tree = "true".equals( ui.getAttribute( "tree" ) );
		String systemClass = ui.getAttribute( "systemHelper" );
		boolean rt = "true".equals( ui.getAttribute( "rt", "true" ) );
		info.setRealTimeTree( rt );
		info.setTreeAvailable( tree );
		info.setSystemHelperClass( systemClass );
		boolean sc = "true".equals( ui.getAttribute( "sc", "true" ) );
		info.setSyntaxColor(sc);
		boolean ac = "true".equals( ui.getAttribute( "ac", "true" ) );
		info.setAutoClosing( ac );
		boolean sh = "true".equals( ui.getAttribute( "sh", "true" ) );
		info.setSyntaxHelper( sh );

		info.setType( n.getAttribute( "type" ) );
		info.setMetaType( n.getAttribute( "metaType" ) );
		info.setDocumentDescription( n.getAttribute( "label" ) );
		info.setParentType( n.getAttribute( "parentType" ) );
		
		info.setSelectFirstTagAfterReading( "true".equals( n.getAttribute( "selectTag" ) ) );

		if ( info.getParentType() != null
				&& info.getType() != null )
			PARENT_TYPE.put(
					info.getType(), info.getParentType() );

		if ( icon != null ) {
			try {
				info.setDocumentIconPath( icon );
				info.setDocumentIcon( EditixFactory.getImageIcon( icon ) );
			} catch (Throwable th) {
			}
		}

		// File exts
		TreeWalker tw = new TreeWalker(n);
		Enumeration enume = tw.getNodeByCriteria( new NodeNameCriteria( "ext" ),
				false );
		while (enume.hasMoreElements()) {
			FPNode _ = ( FPNode ) enume.nextElement();
			info.addFileExt(_.getAttribute("name"));
			if ("true".equals(_.getAttribute("def"))) {
				String _tmp = null;
				info.setDefaultFileExt(_tmp = _.getAttribute("name"));
			}
		}

		// Default DTD
		enume = tw.getNodeByCriteria( new NodeNameCriteria( "defDTD" ), false );
		if (enume.hasMoreElements()) {
			FPNode _ = (FPNode) enume.nextElement();
			String location = _.getAttribute("path");
			
			String externalDoc = _.getAttribute( "doc" );
			
			if (location != null) {
				// Debugger.print( "Reset DTD " + location + " for " + info.getType() );
				info.setDefaultDTD(_.getAttribute("root"), location);
				
				// Convert it to URL
				if ( externalDoc != null ) {
					URL u = ClassLoader.getSystemResource( externalDoc );
					if ( u == null ) {
						System.out.println( "Can't load [" + externalDoc + "]" );
					} else
						externalDoc = u.toExternalForm();
				}
				info.setDTDExternalCommentFile( externalDoc );
			}
		} else {
			// Debugger.print( "No DTD for " + info.getType() );
		}

		// Default namespace
		FPNode namespace = tw.getOneNodeByCriteria(
				new NodeNameCriteria( "namespace" ), false );		
		if ( namespace != null ) {
			if ( namespace.childCount() > 0 ) {
				String value = namespace.childAt( 0 ).getContent();
				info.setDefaultNamespace( value );
			} else
				System.err.println( "No namespace content for the document model !" );
		}

		// Activators for attributes assistant
		FPNode activator = tw.getOneNodeByCriteria(
				new NodeNameCriteria( "activator" ), false );		
		if ( activator != null ) {
			String atts = activator.getAttribute( "attributes" );
			if ( atts != null ) {
				StringTokenizer st = new StringTokenizer( atts, ";," );
				String[] res = new String[ st.countTokens() ];
				int i = 0;
				while ( st.hasMoreTokens() ) {
					res[ i++ ] = st.nextToken();
				}
				info.setListOfAttributesWithAutoAssistant( res );
			}
		}

		// Default Schema
		enume = tw.getNodeByCriteria( 
				new NodeNameCriteria( "defSchema" ), false );
		if (enume.hasMoreElements()) {
			FPNode _ = (FPNode) enume.nextElement();
			String location = _.getAttribute("path");

			if (location != null) {
				// Debugger.print( "Reset Schema " + location + " for " + info.getType() );
				info.setDefaultSchema(_.getAttribute("root"), location);
			}
		}
		
		// Schema for validating
		FPNode validNode = tw.getFirstTagNodeByName( "validator", false );
		if ( validNode != null ) {

			if ( validNode.hasAttribute( "schemaPath" ) ) {
			
				// Resolve the path to an URL
				URL url = ClassLoader.getSystemClassLoader().getResource(
						validNode.getAttribute( "schemaPath" ) );
				if ( url != null ) {
					info.setSchemaXSDValid( url );
					info.setSchemaXSDNSValid( validNode.getAttribute( "schemaNS" ) );
				} else {
					ApplicationModel.debug( "Can't find " + validNode.getAttribute( "schemaPath" ) + "!!!" );
				}
			
			} else
				
			if ( validNode.hasAttribute( "relaxngPath" ) ) {

				// Resolve the path to an URL
				URL url = ClassLoader.getSystemClassLoader().getResource(
						validNode.getAttribute( "relaxngPath" ) );
				if ( url != null ) {
					info.setSchemaRNGValid( url );
				} else {
					ApplicationModel.debug( "Can't find " + validNode.getAttribute( "relaxngPath" ) + "!!!" );
				}

			} else
				
			if ( validNode.hasAttribute( "class" ) ) {
				
				try {
					Validator v = ( Validator )Class.forName( validNode.getAttribute( "class" ) ).newInstance();
					info.setCustomValidator( v );
				} catch ( Exception e ) {
					Debug.debug( e );
				}
				
			}
		}

		ArrayList assistants = null;

		// Load the assistant
		enume = tw.getNodeByCriteria( new NodeNameCriteria( "assistant" ), false );
		while ( enume.hasMoreElements() ) {
			FPNode node = ( FPNode )enume.nextElement();
			String cl = node.getAttribute( "class" );
			if ( "none".equals( cl ) ) {
				info.setDefaultAssistant( false );
				break;
			}
			if ( assistants == null )
				assistants = new ArrayList();
			try {
				ApplicationModel.debug( "Loading " + cl );
				assistants.add( Class.forName( cl ).newInstance() );
			} catch ( Exception e ) {
				e.printStackTrace();
			}
		}
		if ( assistants != null ) {
			info.setHelperHandlers( 
				assistants, 
				"true".equals( n.getAttribute( "assistantAppend" ) ) 
			);
		}

		enume = tw.getNodeByCriteria( new NodeNameCriteria( "refactor" ), false );
		ArrayList refactors = null;
		while ( enume.hasMoreElements() ) {
			FPNode node = ( FPNode )enume.nextElement();
			String cl = node.getAttribute( "class" );
			try {
				ApplicationModel.debug( "Loading " + cl );
				if ( refactors == null )
					refactors = new ArrayList();
				refactors.add( Class.forName( cl ).newInstance() );
			} catch ( Exception e ) {
				e.printStackTrace();
			}			
		}
		if ( refactors != null )
			info.setProperty( "refactor", refactors );
		
		enume = tw.getNodeByCriteria( new NodeNameCriteria( "prefix" ), false );
		while ( enume.hasMoreElements() ) {
			FPNode node = ( FPNode )enume.nextElement();
			String p = node.getAttribute( "name" );
			String c = node.getAttribute( "color" );
			Color co = Color.decode( c );
			info.setColorForPrefix( p, co );
		}

		// Search help like matching template for XSLT
		ArrayList mappers = null;
		enume = tw.getNodeByCriteria( new NodeNameCriteria( "mapper" ), false );		
		while ( enume.hasMoreElements() ) {
			FPNode node = ( FPNode )enume.nextElement();
			String cl = node.getAttribute( "class" );
			if ( mappers == null )
				mappers = new ArrayList();
			try {
				ApplicationModel.debug( "Loading mapper " + cl );
				mappers.add( Class.forName( cl ).newInstance() );
			} catch( Exception e ) {
				e.printStackTrace();
			}
		}
		info.setMappers( mappers );
		
		if ( "DTD".equals( info.getType() ) )
			info.setDtdMode( true );

		// Parameters
		// <param name="checkerid" value="parse"/>

		enume = tw.getNodeByCriteria( new NodeNameCriteria( "param" ), false );		
		while ( enume.hasMoreElements() ) {
			FPNode node = ( FPNode )enume.nextElement();
			info.setParam( node.getAttribute( "name" ), node.getAttribute( "value" ) );
		}

		return info;
	}
	
}
