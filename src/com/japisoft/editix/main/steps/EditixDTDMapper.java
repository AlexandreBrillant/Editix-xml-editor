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
		
		String _ = httpURL.substring( 7 );
		return new File( cacheLocation, _ );
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
			String _ = url.substring( 7 );
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
				
//				InputSource source = 
//					EditixEntityResolver.getInstance().resolveEntity( null, url );
//				if ( source != null ) {
//					try {
//						InputStream input = source.getByteStream();
//						if ( input != null )
//							return input;
//					} catch (Exception e) {
//					}
//				}

				SchemaLocator locator = new SchemaLocator(
						null,
						url,
						null );				
				
//				return new URL( url ).openStream();
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
			}
		}
		return null;
	}
	
}
