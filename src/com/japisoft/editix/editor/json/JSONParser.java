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

package com.japisoft.editix.editor.json;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONKey;
import org.json.JSONObject;
import org.json.JSONTokener;

import com.japisoft.framework.collection.FastArrayList;
import com.japisoft.framework.xml.parser.ErrorParsingListener;
import com.japisoft.framework.xml.parser.ParseException;
import com.japisoft.framework.xml.parser.document.Document;

import com.japisoft.framework.xml.parser.node.FPNode;
import com.japisoft.framework.xml.parser.node.MutableNode;
import com.japisoft.framework.xml.parser.node.NodeFactory;
import com.japisoft.xmlpad.tree.parser.Parser;

public class JSONParser implements Parser {

	private Document doc = null;
	
	public Document getDocument() {
		return doc;
	}

	private boolean errorFound = false;

	public boolean hasError() {
		return errorFound;
	}

	public void interruptParsing() {
	}

	public boolean isInterrupted() {
		return false;
	}
	
	public void setLightweightMode(boolean b) {
	}
	public boolean isLightweightMode() {
		return false;
	};
	
	FPNode transform( JSONObject obj ) {
		return new JSONToJAPISOFT( obj ).root;
	}
	
	public Document parse( Reader reader, Object context ) throws ParseException {
		errorFound = false;
		try {
			String json = IOUtils.toString( reader );
			
			if ( json.trim().startsWith( "[" ) ) {
				// Array mode
				doc = new JSONToJAPISOFT( new JSONArray( json ) );
			} else
				doc = new JSONToJAPISOFT( new JSONObject( json ) );
			return doc;
		} catch( IOException exc ) {
			throw new ParseException( exc.getMessage() );
		} catch( JSONException error ) {
			errorFound = true;
			if ( errorMode ) {
				errorListener.parsingError( 
					error.getMessage(), 
					error.offset, 
					error.line, 
					0 
				);
			}
			throw new ParseException( error.getMessage() );			
		}
	}
	
	public void setBackgroundMode( boolean b ) {
	}
	
	private String tmpContent;
	
	public void setContent(char[] charArray) {
		this.tmpContent = new String( charArray );
	}
	
	private ErrorParsingListener errorListener;

	private boolean errorMode = false;
	
	public void setErrorSignal(ErrorParsingListener parsingErrorListener) {
		this.errorListener = parsingErrorListener;
		if ( parsingErrorListener != null )
			errorMode = true;
	}

	public void setFlatView(boolean b) {
	}

	private NodeFactory factory;

	public void setNodeFactory(NodeFactory factory) {
		this.factory = factory;
	}

	public void setParsingMode(int continueParsingMode) {
	}

	// ----------------------------------------------------------------------------

	class JSONToJAPISOFT extends Document {
		private FPNode root = null;
		private ArrayList<FPNode> fv = null;

		public JSONToJAPISOFT( JSONArray jsdoc ) {
			root = new FPNode( FPNode.TAG_NODE, "root" );
			root.setDocument( this );
			root.setStartingLine( 0 );
			root.setStartingOffset( 0 );
			root.setStoppingOffset( 0 );

			setFlatView(true);
			fv = new ArrayList<FPNode>();
			setFlatNode(fv);
			fv.add( root );

			synchro( root, jsdoc );
			setRoot(root);			
		}

		public JSONToJAPISOFT( JSONObject jsdoc ) {
			root = new FPNode( FPNode.TAG_NODE, "root" );
			root.setDocument( this );
			root.setApplicationObject( jsdoc );
			root.setStartingLine( jsdoc.getLine() );
			root.setStartingOffset( 0 );
			root.setStoppingOffset( jsdoc.getOffset() );
			setFlatView(true);
			fv = new ArrayList<FPNode>();
			setFlatNode(fv);
			fv.add( root );
			synchro( root, jsdoc );
			setRoot(root);
		}
		
		private void synchro( FPNode sn, Object jsonObj ) {
			if ( jsonObj instanceof JSONObject ) {
				JSONObject n = ( JSONObject )jsonObj;
				JSONKey key = null;				
				for ( int i = 0; i < n.getKeyCount(); i++ ) {
					key = n.getKey( i );
					FPNode nn = new FPNode( FPNode.TAG_NODE, key.getKey() );
					nn.setApplicationObject( key );
					nn.setDocument( this );
					fv.add( nn );
					nn.setStartingLine( key.getLine() );
					nn.setStoppingLine( key.getLine() );
					nn.setStartingOffset( key.getStart() );
					nn.setStoppingOffset( key.getEnd() );
					sn.appendChild( nn );
					Object child = n.get( key.getKey() );
					synchro( nn, child );

					if ( child instanceof JSONObject ) {
						// Property with an object content, we extend the property size to the object size
						JSONObject objValue = ( JSONObject )child;
						nn.setStoppingOffset( ( int )objValue.getEndingOffset() );
					}					
					
					JSONKey value = key.getValue();
					if ( value != null ) {
						FPNode nn2 = new FPNode( FPNode.TEXT_NODE, value.getKey() );
						nn2.setDocument( this );
						fv.add( nn2 );
						nn2.setStartingLine( value.getLine() );
						nn2.setStoppingLine( value.getLine() );
						nn2.setStartingOffset( value.getStart() );
						nn2.setStoppingOffset( value.getEnd() );
						nn.appendChild( nn2 );


					}
				}
				
			} else {
				if ( jsonObj instanceof JSONArray ) {
					JSONArray arr = ( JSONArray )jsonObj;
					for ( int i = 0; i < arr.length(); i++ ) {
						FPNode nn = new FPNode( FPNode.TAG_NODE, "[" + i + "]" );
						nn.setApplicationObject( arr.get( i ) );

						nn.setDocument( this );
						fv.add( nn );
						sn.appendChild( nn );
						synchro( nn, arr.get( i ) );
					}
				}
			}
			
		}

		@Override
		public MutableNode getRoot() {
			return super.getRoot();
		}
	}
	
	public static void main( String[] args ) throws Exception {
		
		// File f = new File( "C:/Users/alexandre/Documents/testEditiX/test-schema.json" );
		// String json = IOUtils.toString( new FileReader( f ) );
		String json = "{ \"aa\":{ \"bb\":\"cc\" } }";
		// JSONObject obj = new JSONObject( json );
		JSONTokener jt = new JSONTokener( json );
		JSONObject obj = new JSONObject( jt );
		
		JSONParser fp = new JSONParser();
		FPNode node = fp.transform( obj );
		System.out.println( node.debugLocation().getRawXML( 1 ) );
		
	}

}

