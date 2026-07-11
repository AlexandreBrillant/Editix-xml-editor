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

package com.japisoft.editix.action.docbook;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.Map.Entry;

public class ParametersModel {

	private HashMap parameters;
	private HashMap helps;
	// private HashMap<String,Integer> location;
	// private int lineCounter = 0;
	
	public void read() {
		parameters = new HashMap();
		helps = new HashMap();
		// location = new HashMap<String,Integer>();
		values = new HashMap();
		
		URL location = getClass().getResource( 
				"parameters.txt" );
		if ( location == null ) {
			System.err.println( 
				"Can't find parameters.txt for docbook" );
		} else {
			try {
				BufferedReader br =
					new BufferedReader(
							new InputStreamReader(
									location.openStream() ) );
				try {
					
					String l = null;
					while ( ( l = br.readLine() ) != null ) {
						if ( l.startsWith( "-" ) )
							setCurrentType( l );
						else
							addParameter( l );
					}
					
				} finally {
					br.close();
				}
			} catch (IOException e) {
				
			}
		}
	}
	
	private String currentType = null;
	
	private void setCurrentType( String type ) {
		int i = type.lastIndexOf( "." ) + 1;
		if ( i > 0 )
			currentType = type.substring( i );
		else
			currentType = "NoCategory";
	}
	
	private void addParameter( String parameter ) {
		if ( currentType == null )
			return;	// ?
		
		// lineCounter++;		
		// location.put( parameter , lineCounter );

		String[] res = parameter.split( "�" );
		if ( res != null && res.length == 2 ) {
			String param = res[ 0 ].trim();
			String help = res[ 1 ].trim();
			List ls = 
				( List )parameters.get( currentType );
			if ( ls == null )
				parameters.put( currentType, ls = new ArrayList() );
			ls.add( param );
			helps.put( param, help );
		}
	}
	
	public List getTypes() {
		ArrayList tmp = new ArrayList( parameters.keySet() );
		Collections.sort( tmp );
		return tmp;
	}
	
	public List getParameters( String type ) {
		return ( List )parameters.get( type );
	}
	
	public String getHelp( String parameter ) {
		return ( String )helps.get( parameter );
	}

	//public int getParameterLocation( String parameter ) {
	//	return location.get( parameter );
	//}
	
	private HashMap values = null;
	
	public void setValue( String parameter, String value ) {
		if ( parameter != null ) {
			if ( value == null )
				values.remove( parameter );
			else
				values.put( parameter, value );
		}
	}

	public String getValue( String parameter ) {
		return ( String )values.get( parameter );
	}

	public Set getValues() {
		return ( Set )values.entrySet();
	}

}
