package com.japisoft.xmlpad.toolkit;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;

import com.japisoft.xmlpad.XMLContainer;
import com.japisoft.xpath.XPathException;

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
public final class XMLToolkit {

	/** Highlight the nodes from the editor from this xpathExpression
	 * @param container The main editor  
	 * @param xpathExpression The XPath query
	 * @param relative if <code>true</code> this is relative to the current node else to the root node 
	 * @return an XPathHandler for selecting each result node
	 * @throws XPathException Throw an exception is the query cannot be computed */
	public static XPathHandler highlightXPathNodes( 
			XMLContainer container, 
			String xpathExpression,
			boolean relative ) throws XPathException {
		return new XPathHandler( container, xpathExpression, relative );
	}

	/** @return the file content from this fileName */
	public static XMLFileData getContentFromFileName( String fileName, String defaultEncoding ) throws Throwable {
		return getContentFromInputStream( new FileInputStream( fileName ), defaultEncoding );
	}

	/** @return the file content from this URL */
	public static XMLFileData getContentFromFileName( URL url, String defaultEncoding ) throws Throwable {
		return getContentFromInputStream( url.openStream(), defaultEncoding );
	}

	/*
	With a Byte Order Mark:
		00 00 FE FF 	UCS-4, big-endian machine (1234 order)
		FF FE 00 00 	UCS-4, little-endian machine (4321 order)
		00 00 FF FE 	UCS-4, unusual octet order (2143)
		FE FF 00 00 	UCS-4, unusual octet order (3412)
		FE FF ## ## 	UTF-16, big-endian
		FF FE ## ## 	UTF-16, little-endian
		EF BB BF 	UTF-8	
*/

	private static int readCharWith16bitsCase( InputStream input, StringBuffer buffer ) throws Throwable {
		int c;
		c = input.read();
		boolean ucs4Case = false;
		if ( c == 0 ) {	// For UCS 4 case
			//buffer.append( (char)0 );
			c = input.read();
		}
		if ( c == 0 ) {	// For UCS 4 case
			//buffer.append( (char)0 );
			c = input.read();
		}
		if ( c == 0 ) {	// For UCS 4 case
			//buffer.append( (char)0 );
			c = input.read();
			ucs4Case = true;
		}		
		if ( c != -1 )
			buffer.append( (char)c );
		if ( !ucs4Case && ( c == 0 ) ) {	// Other case ?
			c = input.read();
			if ( c != -1 )
				buffer.append( (char)c );
		}
		return c;
	}

	public static XMLFileData getContentFromInputStream( InputStream input, String encodingMode ) throws Throwable {
		String encoding = encodingMode;
		StringBuffer sb = new StringBuffer();
		int c;
		
		if ( encodingMode == null || "AUTOMATIC".equals( encodingMode ) ) {
			encoding = "UTF-8";
			c = input.read();
			if ( c == 255 || c == 254 )	// SKIP UTF-16 header
				c = input.read();
			if ( c == 255 || c == 254 )
				c = input.read();
			if ( c == 0 )	// UCS Case
				c = input.read();
			if ( c == 0 )
				c = input.read();			
			if ( c != -1 )
				sb.append( ( char )c );
			if ( c != -1 && c == '<' ) {
				c = readCharWith16bitsCase( input, sb );
				if ( c != -1 && c == '?' ) {
					c = readCharWith16bitsCase( input, sb );
					if ( c != -1 && c == 'x' ) {
						c = readCharWith16bitsCase( input, sb );
						if ( c != -1 && c == 'm' ) {
							c = readCharWith16bitsCase( input, sb );
							if ( c != -1 && c == 'l' ) {
								c = readCharWith16bitsCase( input, sb );
								boolean blankState = false;
								// Loop until the end of the prolog
								for ( ;c != '>' && c != -1; ) {
									c = readCharWith16bitsCase( input, sb );
									if ( blankState && 
											c == 'e' ) {
										// Search for the encoding value
										do {
											c = readCharWith16bitsCase( input, sb );
										} while ( ( c != '\'' && c != '\"' ) && c != -1 );
										StringBuffer encodingBuffer = new StringBuffer();
										c = readCharWith16bitsCase( input, sb );
										while ( ( c != -1 ) && c != '\'' && c != '"' ) {
											encodingBuffer.append( ( char )c );
											c = readCharWith16bitsCase( input, sb );
										}
										encoding = encodingBuffer.toString().toUpperCase();
									} else {
										blankState = ( c == ' ' || c == '\t' );
									}
								}
							}
						}
					}
				}
			}
			if ( c == -1 ) {
				input.close();
				return new XMLFileData( encoding, sb.toString() );
			}			
		}
		InputStreamReader reader = null;
		if ( encoding == null || "DEFAULT".equals( encoding ) || "AUTOMATIC".equals( encoding ) ) {
			reader = new InputStreamReader( input );
		} else {
			
			if ( "${DEFAULT-ENCODING}" .equals( encoding ) ) {
				reader = new InputStreamReader( input );
			} else {
				try {
					reader = new InputStreamReader( input, encoding );
				} catch( UnsupportedEncodingException e ) {
					reader = new InputStreamReader( input );
				}
			}
		}
		char[] buffer = new char[ 1024 ];
		try {
			while ( ( c = reader.read( buffer ) ) != -1 ) {
				sb.append( new String( buffer, 0, c ) );
			}
		}
		finally {
			reader.close();
		}
		
		try {
			if ( sb.charAt( sb.length() - 3 ) == 65533 ) {	// Unknown case ??
				sb.setCharAt( sb.length() - 3, ' ' );
			}
		} catch (RuntimeException e) {
			// For out of bounds exception
		}

		return new XMLFileData( encoding, sb.toString() );
	}


	/** @return the XML encoding */
	public static String getXMLEncoding( String content, String defaultEncoding ) {
		int i = 0;
		String encoding = "UTF-8";
		try {
			char c = content.charAt( i++ );
			if ( c == '<' ) {
				c = content.charAt( i++ );
				if ( c == '?' ) {
					c = content.charAt( i++ );
					if ( c == 'x' ) {
						c = content.charAt( i++ );
						if ( c == 'm' ) {
							c = content.charAt( i++ );
							if ( c == 'l' ) {
								c = content.charAt( i++ );
								boolean blankState = false;
								// Loop until the end of the prolog
								for ( ;c != '>'; ) {
									c = content.charAt( i++ );
									if ( blankState && 
											c == 'e' ) {
										// Search for the encoding value
										do {
											c = content.charAt( i++ );
										} while ( c != '\'' && c != '\"' );
										StringBuffer encodingBuffer = new StringBuffer();
										c = content.charAt( i++ );
										while ( c != '\'' && c != '"' ) {
											encodingBuffer.append( c );
											c = content.charAt( i++ );
										}					
										encoding = encodingBuffer.toString().toUpperCase();
									} else {
										blankState = ( c == ' ' || c == '\t' );
									}
								}
							}
						}
					}
				}
			}
		} catch (RuntimeException e) {
			//encoding = null;
		}
		return encoding;
	}

}
