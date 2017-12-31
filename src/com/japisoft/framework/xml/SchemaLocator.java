package com.japisoft.framework.xml;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.net.URL;
import java.net.URLConnection;

import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
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
public class SchemaLocator {

	public String documentLocation;

	public String location;

	public int schemaDeclarationLine = -1;

	private Reader reader;

	private URL locationURL;

	private boolean streamProvided = false;

	/**
	 * @param location
	 *            an URL or a file path to a schema
	 */
	public SchemaLocator(String location) {
		this.location = location;
	}

	/**
	 * @param documentLocation Source document
	 * @param location Relatif schema location */
	public SchemaLocator(String documentLocation, String location) {
		this.location = location;
		this.documentLocation = documentLocation;
	}

	private EntityResolver resolver;
	
	/**
	 * @param documentLocation Source document
	 * @param location Relatif schema location */
	public SchemaLocator(String documentLocation, String location, EntityResolver resolver ) {
		this( documentLocation, location );
		this.resolver = resolver;
	}

	/**
	 * @param location
	 *            an URL to a schema
	 */
	public SchemaLocator(URL location) {
		this.locationURL = location;
	}

	/**
	 * @param stream
	 *            a stream for a schema content
	 */
	public SchemaLocator(InputStream stream) {
		this.reader = new InputStreamReader(stream);
		this.streamProvided = true;
	}

	/** @return true if a stream was used for the constructor */
	public boolean streamProvided() {
		return streamProvided;
	}

	/**
	 * @param stream
	 *            A Stream for a schema content
	 * @param source
	 *            A location
	 */
	public SchemaLocator(InputStream stream, String location) {
		this(stream);
		this.location = location;
	}

	/** @return a location or an URL if available */
	public String getSource() {		
		if ( location.indexOf( "://" ) > -1 )
			return location;
		if ( documentLocation != null &&
				documentLocation.indexOf( "://" ) > -1 ) {
			String newURL = documentLocation;
			int i = newURL.lastIndexOf( "/" );
			newURL = newURL.substring( 0, i + 1 );
			newURL += location;
			return newURL;
		}

		
		if ( location.startsWith( "file:/" ) )
			location = location.substring( 6 );
		
		
		File f = new File( location );
		if ( f.exists() )			// Absolute one
			return location;

		f = new File(new File(documentLocation)	// Relative one
				.getParentFile(), location);
		
		return f.toString();
		
	}

	/**
	 * @param reader a reader for a schema content */
	public SchemaLocator( Reader reader ) {
		this.reader = reader;
	}

	private String downloadedContent = null;
	private byte[] binaryDownloadedContent = null;

	/** This is better for encoding management */
	public InputStream getInputStream() throws Exception {		
		if ( binaryDownloadedContent != null )
			return new ByteArrayInputStream( binaryDownloadedContent );

		InputStream input = null;
		if (locationURL != null) {
			input = locationURL.openStream();
		}
		if ( input == null ) {
			
			if ( resolver != null ) {
				InputSource source = resolver.resolveEntity( null, location );
				if ( source != null ) {
					input = source.getByteStream();
				}
			}

			if ( input == null ) {
				if ( location.indexOf( "://" ) > -1 ) {	// URL
					
					URLConnection conn = new URL( location ).openConnection();
					conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");					
					input = conn.getInputStream();

				} else
				if ( documentLocation != null && documentLocation.indexOf( "://" ) > -1 ) {
					// Relative to an URL
					int i = documentLocation.lastIndexOf( "/" );
					String parentURL = documentLocation.substring( 0, i + 1 );
					input = new URL( parentURL + location ).openStream();
				}
			}
		}

		if ( input == null ) {
			// Try file access
			if ( documentLocation == null )
				input = new FileInputStream( location );
			// Try relative access
			if ( documentLocation != null ) {
				File parentFile = new File( documentLocation );
				File finalFile = new File( parentFile.getParentFile(), location );
				if ( finalFile.exists() )
					input = new FileInputStream( finalFile );
				else
					input = new FileInputStream( location );
			}
		}
		
		if ( input == null )
			throw new Exception( "Can't find file " + location );
		
		byte[] buffer = new byte[ 1024 ];
		int c = 0;
		ByteArrayOutputStream result = new ByteArrayOutputStream();
		while ( ( c = input.read( buffer ) ) > 0 ) {
			result.write( buffer, 0, c );
		}

		binaryDownloadedContent = result.toByteArray();
		
		try {
			input.close();
		} catch (IOException e) {
		}
		
		return getInputStream();
		
	}

	/**
	 * @return The final reader
	 * @throws Exception
	 *             If the reader can't be gotten
	 */
	public Reader getReader() throws Exception {
		if (reader != null) {

			// Download in memory

			if ( downloadedContent == null ) {
				StringBuffer sb = new StringBuffer();
				char[] buffer = new char[ 1024 ];
				int c;
				try {
					while ( ( c = reader.read( buffer ) ) != -1 ) {
						sb.append( new String( buffer, 0, c ) );
					}
				} finally {
					reader.close();
				}
				downloadedContent = sb.toString();
			}

			if ( downloadedContent != null ) {
				reader = new StringReader( downloadedContent );
			}

			return reader;
		} else {

			if (locationURL != null) {
				return new InputStreamReader( locationURL.openStream() );
			}

			if (location == null)
				throw new Exception("Null schema location?");

			// URL Case
			if ( location.indexOf( "://" ) > -1 ) {
				location = location.replaceAll( "%20", " " );
				locationURL = new URL( location );
				return new InputStreamReader(
						locationURL.openStream() );
			} else {
				
				if ( location.startsWith( "file:" ) ) {
					location = location.substring( 5 );
				}

				File f = new File( location );
				if ( f.exists() ) {
					locationURL = f.toURI().toURL();
					return new FileReader( location );
				} else {

					// By the file system
					
					if ( documentLocation != null ) {
						f = new File(
								new File( documentLocation ).getParentFile(),
								location );
						if ( f.exists() ) {
							locationURL = f.toURI().toURL();
							return new FileReader( f );
						}
					}

					// By the classpath

					locationURL = ClassLoader.getSystemResource( location );
					if ( locationURL != null ) {
						try {
							return new InputStreamReader( locationURL
								.openStream() );
						} catch( Exception exc ) {
						}
					}

					// By the url
					try {
						locationURL = new URL( 
							new URL( documentLocation ), location );
						return new InputStreamReader( locationURL.openStream() );
					} catch( Exception exc ) {
					}

				}
			}
		}

		throw new Exception("No reader found ?");
	}

	public static void main( String[] args ) throws Exception {
		URL url = ( new URL( "http://moqui.org/xsd/xml-screen-1.6.xsd" ) );
		URLConnection conn = url.openConnection();
		conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
		
	}
	
}
