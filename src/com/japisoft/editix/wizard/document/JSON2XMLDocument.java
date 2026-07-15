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

package com.japisoft.editix.wizard.document;

import java.io.File;
import java.io.FileInputStream;
import java.io.StringWriter;
import java.util.Set;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.XML;
import org.w3c.dom.Element;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import com.japisoft.editix.action.xml.format.FormatAction;
import com.japisoft.editix.ui.EditixFactory;
import com.japisoft.editix.ui.windows.EditixFrame;
import com.japisoft.framework.toolkit.FileToolkit;

public class JSON2XMLDocument implements DocumentWizard {

	private File source;
	
	@Override
	public String start() {
		JFileChooser fc = EditixFactory.buildFileChooser( new FileFilter() {			
			@Override
			public String getDescription() {
				return "JSON file (*.json, *.jso)";
			}
			@Override
			public boolean accept( File f ) {
				if ( f.isFile() ) {
					String tmp = f.getName().toLowerCase();
					return tmp.endsWith( ".json" ) || tmp.endsWith( ".jso" );
				} else
					return true;
			}
		});
		if ( fc.showOpenDialog( EditixFrame.THIS ) == 
				JFileChooser.APPROVE_OPTION ) {
			source = fc.getSelectedFile();
			try {
				Document doc = JSONTODOM( source );
				
				StringWriter outWriter = new StringWriter();
				StreamResult result = new StreamResult( outWriter );				
				
				TransformerFactory.newInstance().newTransformer().transform( new DOMSource( doc ), result );
				
				return outWriter.toString();
				
			} catch( Throwable th ) {
				th.printStackTrace();
				EditixFactory.buildAndShowErrorDialog( th.getMessage() );
				return null;
			}

		}
		return null;
	}
	
	@Override
	public File getSource() {
		return source;
	}
	
	public static Document JSONTODOM( File source ) throws Throwable {
		String jsonData = FileToolkit.getContentFromInputStream(
			new FileInputStream( source ),
			null
		);

		JSONObject root = new JSONObject( jsonData );
		Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
		Element domRoot = doc.createElement( "root" );
		doc.appendChild( domRoot );
		convertToXML( root, doc, domRoot );
		
		return doc;
	}
	
	private static void convertToXML( JSONObject obj, Document doc, Element node ) {
		Set<String> keys = obj.keySet();
		for ( String key : keys ) {
			Object value = obj.get( key );
			if ( "ed:text".equals( key ) ) {
				node.setTextContent( value.toString() );
			} else
			if ( "ed:attributes".equals( key ) ) {
				JSONObject attributes = ( JSONObject )value;
				Set<String> names = attributes.keySet();
				for ( String attName : names ) {
					node.setAttribute( attName, attributes.getString( attName ));
				}
			} else
			if ( "ed:children".equals( key ) ) {
				JSONArray array = ( JSONArray )value;
				for ( int i = 0; i < array.length(); i++ ) {
					convertToXML( ( JSONObject )array.get( i ), doc, node );
				}
			} else {
				if ( value instanceof JSONObject ) {
					Element child = doc.createElement( regulName( key ) );
					node.appendChild( child );
					convertToXML( ( JSONObject )value, doc, child );
				} else
				if ( value instanceof JSONArray ) {
					Element child = doc.createElement( regulName( key ) );
					node.appendChild( child );
					JSONArray array = ( JSONArray )value;
					for ( int i = 0; i < array.length(); i++ ) {
						if ( array.get( i ) instanceof JSONObject )
							convertToXML( ( JSONObject )array.get( i ), doc, child );
						else {
							if ( array.get( i ) instanceof String ) {
								Element item = doc.createElement( "item" + ( i + 1 ) );
								child.appendChild( item );
								item.setTextContent( array.getString( i ) );
							}
						}
					}
				} else {
					Element child = doc.createElement( regulName( key ) );
					child.setTextContent( value.toString() );
					node.appendChild( child );
				}
				
			}
		}
	}
	
    private static String regulName( String tagName ) {
    	if ( tagName != null ) {
    		char[] all = tagName.toCharArray();
    		boolean modified = false;
    		for ( int i = 0; i < all.length; i++ ) {
    			if ( !Character.isAlphabetic( all[ i ] ) ) {
    				if ( i > 0 && Character.isLetterOrDigit( all[ i ] ) )
    					continue;
    				all[ i ] = '_';
    				modified = true;
    			}
    		}
    		if ( modified )
    			return new String( all );
    	}
    	return tagName;
    }
    	
	
}
