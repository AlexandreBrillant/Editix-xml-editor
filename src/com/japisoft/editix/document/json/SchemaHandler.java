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

package com.japisoft.editix.document.json;

import java.util.List;

import org.json.JSONKey;
import org.json.JSONObject;

import com.japisoft.framework.xml.parser.node.FPNode;
import com.japisoft.xmlpad.XMLContainer;
import com.japisoft.xmlpad.editor.XMLPadDocument;
import com.japisoft.xmlpad.helper.handler.AbstractHelperHandler;
import com.japisoft.xmlpad.helper.model.BasicDescriptor;

public class SchemaHandler extends AbstractHelperHandler {
	
	private String getPointer( FPNode currentNode ) {
		StringBuffer sb = new StringBuffer();
		Object jsonObj = currentNode.getApplicationObject();
		
		if ( currentNode.getApplicationObject() instanceof JSONKey ) {
			JSONKey key = ( JSONKey )currentNode.getApplicationObject();
			sb.insert( 0, key.getKey() );
			jsonObj = key.getParent();
		}
		
		if ( jsonObj instanceof JSONObject ) {
			JSONObject subject = ( JSONObject )jsonObj;
			// Build the JSON pointer
			
			while ( currentNode.getParent() != null ) {
				currentNode = ( FPNode )currentNode.getParent();
				if ( currentNode.getApplicationObject() != null ) {
					if ( currentNode.getApplicationObject() instanceof JSONKey ) {
						JSONKey key = ( JSONKey )currentNode.getApplicationObject();
						sb.insert(0, key + "/" );
					}
				}
			}
			
		}

		sb.insert( 0, "#/" );
		return sb.toString();
	}
	
	protected void installDescriptors( 
			FPNode currentNode,
			XMLPadDocument document, 
			int offset,
			String addedString ) {	
		
		XMLContainer container = document.getContainer();
		
		if ( currentNode == null ) {
			currentNode = container.getRootNode(); 
		}
		
		String schemaPath = null;

		if ( ( schemaPath = ( String )container.getProperty( "json-schema" ) ) != null ) {
			
			SchemaResolver jsonSchema = ( SchemaResolver )container.getProperty( "json-resolver" );
			if ( jsonSchema == null ) {
				try {
					container.setProperty( "json-resolver", jsonSchema = new SchemaResolver( schemaPath ) );
				} catch( Exception exc ) {
				}
			}

			if ( jsonSchema != null && currentNode != null ) {

				
				String pointer = getPointer( currentNode ); 
									
				List<SchemaProperty> l = jsonSchema.resolve( currentNode.getApplicationObject(), pointer );
				for ( SchemaProperty sp : l ) {
					BasicDescriptor bd = new BasicDescriptor( sp.name );
					bd.setRawContent( sp.toString() );
					bd.setComment( sp.description );
					addDescriptor( bd );
				}

			}
		}
		
	}
	
	public String getTitle() {
		return "JSON content";
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

		return activatorString == null;
	}

	
	
}

