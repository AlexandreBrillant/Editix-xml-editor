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

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONKey;
import org.json.JSONObject;
import org.json.JSONTokener;

public class SchemaResolver {

	private JSONObject root;
	
	public SchemaResolver( String path ) throws IOException {

		root = new JSONObject( new JSONTokener( new FileReader( path ) ) );
		
	}
	
	public List<SchemaProperty> resolve( Object subject, String pointer ) {
		
		List<SchemaProperty> res = new ArrayList<SchemaProperty>();
		
		try {
		
			// Update the pointer
			String[] parts = pointer.split( "/" );
			
			JSONObject parent = root;
			
			for( String part : parts ) {
				if ( "#".equals( part ) ) {
					parent = root;
				} else {
					// Array index
					if ( part.matches( "/d+" ) ) {
						
					} else {
						// Property
	
						if ( parent.has( "properties" ) ) {
							
							parent = ( JSONObject )parent.get( "properties" );
							if ( parent.has( part ) ) {
								parent = ( JSONObject )parent.get( part );
								
							} else
								break;
	
						} else
							break;
											
					}
				}			
			}
			
			if ( parent.has( "type" ) ) {
				String type = ( String )parent.get( "type" );
				if ( "object".equals( type ) ) {
					// Extract all properties
					
					if ( parent.has( "properties" ) ) {
	
						JSONObject properties = ( JSONObject )parent.get( "properties" );
						for ( int i = 0; i < properties.getKeyCount(); i++ ) {
							JSONKey key = properties.getKey( i );
							String property = key.getKey();
							JSONObject obj = properties.getJSONObject( property );							
							SchemaProperty sp = new SchemaProperty( property, obj.getString( "type" ), obj.getString( "description", "" ) );
							res.add( sp );
							
						}
						
					}
					
					if ( parent.has( "required" ) ) {
						
						JSONArray array = ( JSONArray )parent.getJSONArray( "required" );
						for ( int i = 0; i < array.length(); i++ ) {
							String name = array.getString( i );
							for ( SchemaProperty sp : res ) {
								if ( sp.name.equals( name ) )
									sp.required = true;
							}
						}
						
					}
					
				}
			}
			
			// Remove already present properties inside the subject
			
			if ( subject instanceof JSONObject ) {
				JSONObject obj = ( JSONObject )subject;
				for ( int i = 0; i < obj.getKeyCount(); i++ ) {
					JSONKey key = obj.getKey( i );
					for ( int j = 0; j < res.size(); j++ )
						if ( res.get( j ).name.equals( key.getKey() ) ) {
							res.remove( j );
							break;
						}
				}
			}
			
		} catch( ClassCastException exc ) {
			exc.printStackTrace();
		}

		return res;
		
	}
	
	public static void main( String[] args ) throws Exception {
		
		SchemaResolver sr = new SchemaResolver( "C:/Users/alexandre/Documents/testEditiX/schema1.json" );
		JSONObject root = new JSONObject( new JSONTokener( new FileReader( "C:/Users/alexandre/Documents/testEditiX/test-schema.json"  ) ) );
		List<SchemaProperty> l = sr.resolve( root, "#" );
		System.out.println( l.size() + " properties" );
		for( SchemaProperty sp : l ) {
			System.out.println( "- " + sp.name + " [" + sp.type + "] - " + sp.description + " " + ( sp.required ? " required " : "" ) );
		}
		
	}
	
}

