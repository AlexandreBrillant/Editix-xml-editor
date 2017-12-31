package com.japisoft.framework.xml;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;

import com.japisoft.framework.ApplicationModel;

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
public class XMLToolkit {

	/** @return the file content from this fileName */
	public static XMLFileData getContentFromURI( 
			String uri, 
			String defaultEncoding ) throws Throwable {
		if ( uri.indexOf( "://" ) == -1 ) {
			
			if ( uri.startsWith( "file:" ) )
				uri = uri.substring( 5 );

			File f = new File( uri );
			XMLFileData xfd = getContentFromInputStream( new FileInputStream( uri ), defaultEncoding );
			xfd.modifiedDate = f.lastModified();
			xfd.uri = uri;
			return xfd;
		}
		else {
			XMLFileData xfd = getContentFromInputStream( new URL( uri ).openStream(), defaultEncoding );
			xfd.uri = uri;
			return xfd;
		}
	}

	/** @return a file content relative to another one */
	public static XMLFileData getContentFromRelativeOrAbsoluteLocation( String location, String refUri ) throws Throwable {
		// Absolute location
		if ( location.contains( "://" ) ) {
			XMLFileData xfd = XMLToolkit.getContentFromURI( location, null );
			xfd.uri = location;
			return xfd;
		} else {
			// Try relative one
			// From URI
			if ( refUri.contains( "://" ) ) {
				int i = refUri.lastIndexOf( "/" );
				String finalURI = refUri.substring( 0, i + 1 ) + location;
				XMLFileData xfd = XMLToolkit.getContentFromURI( finalURI, null );
				xfd.uri = finalURI;
				return xfd;
			} else {
				// From FilePath
				File f = new File( location );
				// May be absolute
				if ( f.exists() ) {
					XMLFileData xfd = XMLToolkit.getContentFromURI( location, null );
					xfd.uri = location;
					return xfd;
				} else {
					// Try relative
					f = new File( new File( refUri ).getParentFile(), location );
					XMLFileData xfd = XMLToolkit.getContentFromURI( f.toString(), null );
					xfd.uri = f.toString();
					return xfd;
				}
			}
		}
	}
	
	public static void save( File output, String content ) throws Exception {
		OutputStreamWriter ow = new OutputStreamWriter( 
				new FileOutputStream( output ), 
				getXMLEncoding( content ) );
		try {
			ow.write( content );
		} finally {
			ow.close();
		}
	}
	
	/** @return the XML encoding */
	public static String getXMLEncoding( String content ) {
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
			encoding = null;
		}
		
		if ( encoding.startsWith( "$" ) )	// Template parameter case	
			encoding = "UTF-8";
		
		return encoding;
	}

	public static XMLFileData getContentFromBytes( byte[] data, String encodingMode ) throws Throwable {
		return getContentFromInputStream( new ByteArrayInputStream( data ), encodingMode );
	}

	/*
	 [22] prolog	   ::= XMLDecl? Misc* (doctypedecl  Misc*)?
	 [23] XMLDecl	   ::= '<?xml' VersionInfo EncodingDecl? SDDecl? S? '?>'
	 [24] VersionInfo	   ::=   	S 'version' Eq ("'" VersionNum "'" | '"' VersionNum '"')
	 [25] Eq	   ::=   	S? '=' S?
	 [26] VersionNum	   ::=   	'1.0'
	 [27] Misc	   ::=   	Comment | PI | S */
	
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
		
		// BOM for UTF-8 EF BB BF

		if ( encodingMode == null || 
				"AUTOMATIC".equals( encodingMode ) ) {
			encoding = "UTF-8";
			c = input.read();
			
			if ( c == 0xEF ) {
				c = input.read();
				if ( c == 0xBB ) {
					c = input.read();
					if ( c == 0xBF )
						c = input.read();
				}
			}
			
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
			System.gc();
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

	static ArrayList browseJars( String location ) {
		ArrayList al = new ArrayList();
		URL url = ClassLoader.getSystemResource( location );
		if (url != null) {
			String str = url.toExternalForm();
			if ( str.startsWith( "file://" ) )
				str = str.substring( 7 );
			else if ( str.startsWith( "file:" ) )
				str = str.substring( 5 );
			str = str.replaceAll( "%20", " " );

			File dir = new File( str );
			String[] files = dir.list();
			for (int i = 0; i < files.length; i++) {
				String file = files[i];
				try {
					al.add( new File( dir, file ).toURL() );
				} catch (Exception exc) {
					exc.printStackTrace();
				}
			}
		}
		return al;
	}	

	static boolean check( URL[] urls, String className ) {
		try {
			URLClassLoader loader = new URLClassLoader( urls);
			loader.loadClass( className).newInstance();
			return true;
		} catch( Exception exc ) {
		}
		return false;
	}

	static Class loadByConfigFile( File file ) throws Exception {
		if  (!file.exists() )
			return null;
		BufferedReader reader = new BufferedReader( new FileReader( file ));
		try {
			String className = reader.readLine();
			if ( className == null )
				return null;
			if ( "".equals( className ) )
				return null;
			ArrayList al = new ArrayList();
			String name = reader.readLine();
			while ( name != null ) {
				al.add( name );
				name = reader.readLine();
			}
			if ( al.size() == 0 )
				return null;
			URL[] urls = new URL[ al.size() ];
			for ( int i = 0; i < al.size(); i++ ) {
				urls[ i ] = new URL( ( String )al.get( i ) );
			}
			URLClassLoader loader = new URLClassLoader( urls );
			ApplicationModel.debug( "Load class " + className );
			return loader.loadClass( className );
		} finally {
			reader.close();
		}
	}

	public static String resolveCharEntities( String value ) {
		StringBuffer sb = new StringBuffer();
		for ( int i = 0; i < value.length(); i++ ) {
			char c = value.charAt( i );
			if ( c == '<' )
				sb.append( "&lt;" );
			else
			if ( c == '>' )
				sb.append( "&gt;" );
			else
			if ( c == '\"' )
				sb.append( "&quot;" );
			else
			if ( c == '\'' )
				sb.append( "&apos;" );
			else
			if ( c == '&' && ! ( i + 1 < value.length() && value.charAt( i + 1 ) == '#' ) )
				sb.append( "&amp;" );
			else
			if ( c == '\n' )
				sb.append( "&#10;" );
			else
			sb.append( c );
		}
		return sb.toString();
	}

	/** @return prolog,comment,... before the root node **/
	public static String getFullProlog( String xmlContent ) {
		char previousChar = 0;
		boolean mayBeCommentMode = false;

		int i = 0;

		while ( i < xmlContent.length() ) {
			char c = xmlContent.charAt( i );
			if ( previousChar == '<' ) {
				if ( ( c != '!' ) && ( c != '?' ) ) {
					break;
				} else
					if ( c == '!' ) {
						mayBeCommentMode = true;
					}
			}
			if ( mayBeCommentMode ) {
				if ( c == '-' ) {
					// SKIP COMMENT			
					while ( ( i + 1 ) < xmlContent.length() ) {
						i++;
						c = xmlContent.charAt( i );
						if ( ( c == '-' ) && 
								( previousChar == '-' ) )
							break;
						previousChar = c;
						
					}
					mayBeCommentMode = false;
				} else
					mayBeCommentMode = false;
			}
			previousChar = c;
			i++;
		}

		if ( i > 2 ) {
			String prolog = xmlContent.substring( 0, i - 1 );
			if ( prolog.endsWith( "\n" ) )
				prolog = prolog.substring( 0, prolog.length() - 1 );
			return prolog;
		}

		return "";
	}

}
