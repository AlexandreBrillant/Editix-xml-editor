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

package com.japisoft.editix.main.steps;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.URL;

import org.xml.sax.InputSource;

import com.japisoft.dtdparser.DTDMapper;
import com.japisoft.dtdparser.node.RootDTDNode;
import com.japisoft.framework.xml.SchemaLocator;

/**
 * @author Alexandre Brillant (https://github.com/AlexandreBrillant/Editix-xml-editor)
 * @version 1.0
 * */
public class EditixDTDMapper implements DTDMapper {

	private EditixDTDMapper() {
		super();
	}

	private static EditixDTDMapper MAPPER = null;

	public static EditixDTDMapper getInstance() {
		if (MAPPER == null)
			MAPPER = new EditixDTDMapper();
		return MAPPER;
	}
	
	public boolean isCachedEnabled() {
		return true;
	}
	
	private String cacheLocation = null;
	
	public File getPathForURL( String httpURL ) {
		if ( cacheLocation == null ) {
			URL u = ClassLoader.getSystemClassLoader().getResource( "dtd" );
			
			if ( u == null )
				return null;
			
			String cache = u.toExternalForm();
			cache = cache.replaceAll( "%20", " " );

		File p = null;
		if ( cache.startsWith( "file://" ) ) 
			p =
				new File(
					cache.substring( 7 ) );
		else if ( cache.startsWith( "file:" ) )
			p =
				new File(
					cache.substring( 5 ) );
		
		if ( p != null )
			cacheLocation = p.toString();
		else 
			return null;
		}
		
		String __ = httpURL.substring( 7 );
		return new File( cacheLocation, __ );
	}
	
	/** @return the directory for the cache */
	public File updateCache( RootDTDNode root, String url ) {
		if ( !url.toLowerCase().startsWith( "http://" ) )
			return null;
		File f = getPathForURL( url );
		if ( f == null )
			return null;
		if ( f.exists() )
			return f;
		File parent = f.getParentFile();
		parent.mkdirs();
		try {
			FileWriter writer = new FileWriter( f );
			try {
				root.writeDTD( new PrintWriter( writer ) );
			} finally {
				writer.close();
			}
		} catch( IOException exc ) {
		}
		return f;
	}

	public InputStream getStream( String url ) throws IOException {
		if (url.startsWith( "http://" ) ) {
			File path = getPathForURL( url );
			if ( path != null && path.exists() ) {
				return new FileInputStream( path.toString() );
			} else {
				
				String resolvedPath = EditixEntityResolver.getInstance().resolveCatalog(null, url );
				if ( resolvedPath != null ) {
					File f = new File( resolvedPath );
					if ( f.exists() )
						return new FileInputStream( resolvedPath );
				}
				

				SchemaLocator locator = new SchemaLocator(
						null,
						url,
						null );				
				
				try {
					return locator.getInputStream();
				} catch( Exception exc ) {
					throw new IOException( exc );
				}
			}
		} else {
			String file = url;
			File f = new File( file );
			if ( f.exists() ) {
				return new FileInputStream( f );
			} else {
				return ClassLoader.getSystemResourceAsStream( url );
			}
		}
	}
	
}
