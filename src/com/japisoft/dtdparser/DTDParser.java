package com.japisoft.dtdparser;

import com.japisoft.dtdparser.document.*;
import com.japisoft.dtdparser.node.EntityDTDNode;
import com.japisoft.dtdparser.node.RootDTDNode;
import com.japisoft.framework.toolkit.FileToolkit;
import com.japisoft.framework.xml.SchemaLocator;

import java.util.*;
import java.io.*;
import java.net.URL;

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
public class DTDParser {
	private DTDDocumentBuilder cBuilder = null;

	public DTDParser() {
		super();
		cBuilder = new DefaultDTDDocumentBuilder();
	}

	private boolean ignoreComment = false;
	
	/** Decide to ignore comment. By default <code>true</code> */ 
	public void setIgnoreComment( boolean ignore ) {
		this.ignoreComment = ignore;
	}
		
	/** Set the builder for DTD document type */
	public void setDTDDocumentBuilder(DTDDocumentBuilder builder) {
		this.cBuilder = builder;
	}

	/** @return the current DTD document builder */
	public DTDDocumentBuilder getDTDDocumentBuilder() {
		return cBuilder;
	}

	private String toString( InputStream input ) throws IOException {
		InputStreamReader reader = new InputStreamReader(input);
		return toString( reader );
	}

	private String toString( Reader reader ) throws IOException {
		StringWriter output = null;
		try {
			// Convert it to string
			output = new StringWriter();
			char[] buffer = new char[512];
			int c = 0;
			while ((c = reader.read(buffer)) != -1) {
				output.write(buffer, 0, c);
			}
		} finally {
			reader.close();
		}
		return output.toString();		
	}
	
	/** Element definition location */
	static final String ELEMENT_MARKER = "ELEMENT ";

	/** Attribute definition location */
	static final String ATTRIBUTE_MARKER = "ATTLIST ";

	/** Entity definition location */
	static final String ENTITY_MARKER = "ENTITY ";

	/** Comment definition location */
	static final String COMMENT_MARKER = "--";

	/** Notation definition location */
	static final String NOTATION_MARKER = "NOTATION";
	
	/** @return the result of the parsing */
	public RootDTDNode getDTDElement() {
		return cBuilder.getRoot();
	}

	/** @return a XML minimal document generator */
	public XMLGenerator getXMLGenerator() {
		return (XMLGenerator) getDTDElement();
	}

	private String contentToParse = null;

	private String publicExtension = null;

	/** Limit include to subpart like 'mod' */
	public void setResolveIncludeForExtension(String publicExtension) {
		this.publicExtension = publicExtension;
	}
	
	private Hashtable htEntitiesReference = null;
	
	private boolean match( String content, int location ) {
		boolean ok = true;

		// Try COMMENT

		for (int j = 0; (j + location ) < contentToParse.length()
				&& j < content.length(); j++ ) {
			if (contentToParse.charAt(j + location ) != content.charAt(j)) {
				ok = false;
				break;
			}
		}

		return ok;
	}

	private HashMap externalDTDComment = null;
	
	/** Special file with a list of line : one line for the element and one line for the comment.
	 * This is when you decide to use external document with documentation, thus for instance you
	 * can traduce your DTD element comment */
	public void parseExternalDTDComment( URL url ) {
		try {
			BufferedReader 
				br = new BufferedReader(
						new InputStreamReader( url.openStream() ) );
			try {
				String line = null;
				while ( true ) {
					String element = br.readLine();
					if ( element == null )
						break;
					String comment = br.readLine();
					if ( comment != null ) {
						if ( externalDTDComment == null )
							externalDTDComment = new HashMap();
						externalDTDComment.put( element, comment );
					}
				}
			} finally {
				br.close();
			}
		} catch (IOException e) {
		}
	}

	private URL dtdLocation = null;

	/** Parse for this URL. It will use the DTDMapper for updating the local cache */
	public void parse( String url ) throws IOException {
		DTDMapper mapper = DTDMapperFactory.getDTDMapper();
		if ( mapper != null && mapper.isCachedEnabled() ) {
			parse( mapper.getStream( url ) );
			// Update the cache ?
			mapper.updateCache( getDTDElement(), url );
		} else {
			parse( new URL( url ) );
		}
	}
	
	/** Parse the DTD to this URL. It will not use the DTDMapper */
	public void parse( URL url ) throws IOException {
		this.dtdLocation = url;
		parse( url.openStream() );
	}

	/** Parse this DTD. It will not use the DTDMapper */
	public void parse( InputStream input ) throws IOException {
		contentToParse = toString( input );
		innerParse();
	}
	
	/** Parse this DTD. It will not use the DTDMapper */
	public void parse( Reader input ) throws IOException {
		contentToParse = toString( input );
		innerParse();
	}

	/** Parse this DTD. It will use the DTDMapper for updating the local cache */
	public void parse( SchemaLocator input ) throws IOException {
		try {
			if ( input.getSource() != null ) {
				if ( input.getSource().indexOf( "://" ) > -1 )
					dtdLocation = new URL( input.getSource() );
			}
			contentToParse = toString( input.getReader() );
			innerParse();
			DTDMapper mapper = DTDMapperFactory.getDTDMapper();
			if ( mapper != null && mapper.isCachedEnabled() ) {
				if ( input.location != null && input.location.toLowerCase().startsWith( "http:" ) ) {
					mapper.updateCache( getDTDElement(), input.location );					
				}
			}
		} catch( Exception ex ) {
			throw new IOException( ex.getMessage() );
		}
	}

	private int cursor = 0;

	private void innerParse() throws IOException {
		cursor = 0;
		
		cBuilder.notifyStartDTD();
				
		boolean okForNext = false;
		
		while (cursor < contentToParse.length()) {
			char tmp = contentToParse.charAt(cursor++);
			okForNext = ( cursor < contentToParse.length() );
			
			if (tmp == '<') {
				
				if ( okForNext && contentToParse.charAt(cursor) == '!') {
					
					if ( match( "[", cursor + 1 ) ) {
						cursor = parseConditionnal( cursor + 2 );
						continue;
					}
					
					if ( match( COMMENT_MARKER, cursor + 1 ) ) {
						if ( ignoreComment ) {
							int _ = contentToParse.indexOf( "-->", cursor + 1 );
							if ( _ > 1 )
								cursor = _ + 1;
							continue;
						}
						cursor = parseComment(cursor + 1 + COMMENT_MARKER.length());
						continue;
					}
					
					// Try ELEMENT

					if ( match( ELEMENT_MARKER, cursor + 1 ) ) {
						cursor = parseElement(cursor + 1 + ELEMENT_MARKER.length());
						continue;
					}
					
					// Try ATTRIBUTE
					
					if ( match( ATTRIBUTE_MARKER, cursor + 1 ) ) {
						cursor = parseAttribute(cursor + 1 + ATTRIBUTE_MARKER.length());
						continue;
					}

					// Try ENTITY
					
					if ( match( ENTITY_MARKER, cursor + 1 ) ) {
						cursor = parseEntity(cursor + 1 + ENTITY_MARKER.length());
						continue;
					}
					
					// Notation
					
					if ( match( NOTATION_MARKER, cursor + 1 ) ) {
						int _ = contentToParse.indexOf( ">", cursor + 1 );
						if ( _ > -1 ) {
							cursor = _ + 1;
							continue;
						}

					}
				} else {
					if ( contentToParse.charAt( cursor ) == '?' ) {

						int _ = contentToParse.indexOf( "?>", cursor + 1 );
						if ( _ > 1 )
							cursor = _ + 1;
						else
							cursor++;
						continue;
						
					}
				}

			} else if (tmp == '%') {

				if ( managedEntityReference( cursor ) ) {
					cursor--;
					continue;
				}
				
			}
		}

		cBuilder.notifyStopDTD();
		
	}
	
	private boolean managedEntityReference( int location ) {
		if ( htEntitiesReference == null )
			return false;
		// Entity reference ?
		StringBuffer _ = new StringBuffer();
		for (int ii = location; ii < contentToParse.length(); ii++) {
			char c = contentToParse.charAt( ii );
			if (c == ' ' || c == '\n' || c == '\t') {
				_ = null;
				break;
			}
			if (c == ';')
				break;
			_.append( c );
		}
		if (_ != null && _.length() > 0) { // Continue with the new
										   // string
			if ( htEntitiesReference.containsKey(_.toString())) {
				contentToParse = contentToParse.substring(0, location - 1 )
						+ htEntitiesReference.get(_.toString())
						+ contentToParse.substring(location + _.length() + 1 );
				
				return true;
			}
		}
		return false;
	}
	
	// Search the next element from the str expression */
	private ParserElement nextElement(int location) {
		// Search for the next non empty char
		int i = location;
		ParserElement e = new ParserElement();
		boolean okForWord = false;
		StringBuffer sb = new StringBuffer();
		
		while ( i < contentToParse.length() ) {
			char ch = contentToParse.charAt( i++ );
			// Ignore inner comment
			if ( ch == '-' ) {
				try {
					if ( contentToParse.charAt( i - 1 ) == '-' ) {
						int j = contentToParse.indexOf( "--", i + 1 );
						if ( j > -1 ) {
							i = j + 2;
							continue;
						}
					}
					// For SGML DTD Compatibility
/*					if ( contentToParse.charAt( i + 1 ) == ' ' ) {
						if ( contentToParse.charAt( i + 2 ) == '0' || 
								contentToParse.charAt( i + 2 ) == '-' ) {
							i += 2;
							continue;
						}
					}*/
				} catch( StringIndexOutOfBoundsException ee ) {} 
			}

			if ( ch == '%' && ( i + 1 < contentToParse.length() && contentToParse.charAt( i + 1 ) != ' ' ) && htEntitiesReference != null ) {
				if ( managedEntityReference( i ) ) {
					i--;
					continue;
				}
			}

			// Token found

			if ( ParserElement.isToken( ch ) ) {
				e.token = ch;
				e.stop = ( i - 1 );
				return e;
			}

			// Beginning of word

			if (!Character.isWhitespace(ch)) {
				okForWord = true;

				// Literal ?

				if (ParserElement.isLiteral(ch)) {
					// Get all ending the next end character
					// Return the result nothing to do else

					for (int j = i; j < contentToParse.length(); j++) {
						char ch2 = contentToParse.charAt(j);

						if (ch2 == ch) {
							// Return the word
							e.content = sb.toString();
							e.stop = j;
							e.literal = true;
							return e;
						} else
							if ( ch2 == '%' ) {
								if ( managedEntityReference( j + 1 ) ) {
									j--;
									continue;
								}
							} else {
								
//								// For SGML DTD Compatibility
//								if ( contentToParse.charAt( j ) == '-' )
//								if ( contentToParse.charAt( j + 1 ) == ' ' ) {
//									if ( contentToParse.charAt( j + 2 ) == '0' || 
//											contentToParse.charAt( j + 2 ) == '-' ) {
//										j += 2;
//										continue;
//									}
//								}
								
								sb.append( ch2 );
							}
					}
					
				} else {

					// Match until the next token
					sb.append( ch );
					char oldCh = 0;

					while ( i < contentToParse.length() ) {
						ch = contentToParse.charAt( i++ );
						
						if (ParserElement.isToken( ch )
								|| Character.isWhitespace( ch ) ) {

							e.content = sb.toString();
							e.stop = ( i - 2 );
							return e;
						} else {

							// For SGML DTD Compatibility

							
							
							sb.append( ch );
						}

						oldCh = ch;
					}
				}
			}
		}

		return null; // Bad case ?
	}
	
	/** Download a PUBLIC entity */
	protected String downloadPublicEntity(String ref) {
		URL downloadURL = null;
		
		if ( ref.indexOf( "://" ) > -1 ) {
			try {
				return FileToolkit.getContentFromURL( new URL( ref ) );
			} catch( Throwable th ) {
				return "";
			}
		} else { // Relative one

			if ( dtdLocation != null ) {				
				try {
					return FileToolkit.getContentFromURL( new URL( dtdLocation, ref ) );
				} catch( Throwable th ) {
					return "";
				}
			} else
				return "";
			
		}
	}
	
	/**
	 * Parse the entity starting at location
	 * 
	 * @return the next location to process
	 */
	protected int parseEntity(int location) {

		ParserElement id = nextElement(location);

		boolean reference = false;

		if (id.token == '%') {
			reference = true;
			id = nextElement(id.stop + 1);
		}
		
		ParserElement value = nextElement(id.stop + 1);

		if (value.hasToken() && value.token == '%') {
			reference = true;
			value = nextElement(value.stop + 1);
		}

		int type = DTDDocumentBuilder.INTERNAL_ENTITY;

		if ("SYSTEM".equals(value.content)) {
			type = DTDDocumentBuilder.SYSTEM_ENTITY;
			value = nextElement(value.stop + 1);
			if ( value != null && value.content != null ) {
				value.content = downloadPublicEntity(value.content);
			} else
				if ( reference )
					reference = false;
		} else if ("PUBLIC".equals(value.content)) {
			type = DTDDocumentBuilder.PUBLIC_ENTITY;
			value = nextElement(value.stop + 1);
			value = nextElement(value.stop + 1);
			if ( value != null && value.content != null ) {
				value.content = downloadPublicEntity(value.content);
			} else {
				if ( reference )
					reference = false;
			}
		}

		if (!reference)
			cBuilder.notifyEntity(id.content, reference, type, value.content);

		if (reference) {
			if (htEntitiesReference == null)
				htEntitiesReference = new Hashtable();
			// Get only the first declaration !!
			if ( !htEntitiesReference.containsKey( id.content ) )
				htEntitiesReference.put(id.content, value.content);
		}

		return value.stop;
	}
	
	private Stack operatorStack = null;
	
	/**
	 * Parse the element starting at location
	 * @return the next location to process */
	private int parseElement(int location) {

		// Real element

		ParserElement e = nextElement(location);

		if (e == null) // Bad ??
			return (location + 1);
		
		String id = e.content;

		if (id == null) {
			return location + 1;
		}
		
		if ( externalDTDComment != null ) {
			String comment = ( String )externalDTDComment.get( id );
			if ( comment != null ) {
				cBuilder.notifyComment( comment );
			}
		}

		cBuilder.notifyStartElement(id);
		String lastWord = null;
		char lastOperatorToken = 0;
		char delayedOperator = 0;
		
		e = nextElement( e.stop + 1 );
		
		while ( e != null && e.token != '>' ) {

			if ( e.token == '<' ) {
				// Error
				break;
			}
			
			if ( ParserElement.isOperator(e.token) ) {
				if ( lastWord == null )
					cBuilder.notifyOperator( e.token );
				else
					delayedOperator = e.token;
				
			} else 
			if ( e.token == '(' ) {

				if ( lastOperatorToken == '|' || 
						lastOperatorToken == ',' ) {

					if ( operatorStack == null )
						operatorStack = new Stack();
					operatorStack.push( new Character( e.token ) );
					
					if ( lastWord == null && 
							lastOperatorToken == ',' )
						cBuilder.notifyElementIncludeItem( null );
					if ( lastWord == null &&
							lastOperatorToken == '|' )
						cBuilder.notifyElementChoiceItem( null );
					
				}

				cBuilder.notifyStartElementChildren();
				
			} else
			if ( e.token == ')' ) {

				if ( lastWord != null ) {
					cBuilder.notifyElementIncludeItem( lastWord );
					lastWord = null;
					if ( delayedOperator > 0 ) {
						cBuilder.notifyOperator( delayedOperator );
						delayedOperator = 0;
					}
				}
				
				if ( operatorStack != null && !operatorStack.isEmpty() ) {

					lastOperatorToken = ((Character)operatorStack.pop()).charValue();
					
				}
				
				cBuilder.notifyStopElementChildren();
				
			} else
			if ( e.token == '|' ) {
				
				lastOperatorToken = e.token;
				
				
			} else
			if ( e.token == ',' ) {

				lastOperatorToken = e.token;
												
			} else {
				lastWord = e.content;
			}
			
			if ( lastWord != null ) {
			
				if ( lastOperatorToken == '|' ) {
					cBuilder.notifyElementChoiceItem( lastWord );

					if ( delayedOperator > 0 ) {
						cBuilder.notifyOperator( delayedOperator );
						delayedOperator = 0;
					}
					
					lastWord = null;
				} else
				if ( lastOperatorToken == ',' ) {
					cBuilder.notifyElementIncludeItem( lastWord );
					lastWord = null;
					
					if ( delayedOperator > 0 ) {
						cBuilder.notifyOperator( delayedOperator );
						delayedOperator = 0;
					}
				}	
			}
		
			e = nextElement( e.stop + 1 );
			
			if ( e.token == '>' && lastWord != null ) {
				cBuilder.notifyElementIncludeItem( lastWord );
				cBuilder.notifyStopElement();
			}
		}
		
		cBuilder.notifyStopElement();
		if ( e != null )
			return (e.stop + 1);
		return location + 1;
	}
	
	/**
	 * Parse the attribute starting at location
	 * @return the next location to process */
	private int parseAttribute(int location) throws CannotFindElementException {
		// ID
		ParserElement e = nextElement(location);

		if (e == null) // Bad ??
			return (location + 1);

		String element = e.content;
		ParserElement priorElement = null;

		// Parse all attribute definition
		while (!(e.hasToken() && e.token == '>')) {

			if (priorElement == null)
				e = nextElement(e.stop + 1);
			else {
				e = priorElement;
				priorElement = null;
			}

			if (e.hasToken() && e.token == '>')
				break;

			// Attribute name
			String att = e.content;

			// Attribute type
			e = nextElement(e.stop + 1);

			Vector vEnum = new Vector();

			int valueType = DTDDocumentBuilder.CDATA_ATT_VAL;

			// Enum values
			if (e.hasToken() && e.token == '(') {

				// Get all until the next ')'
				while (e.token != ')' && e.token != '>') {

					e = nextElement(e.stop + 1);
					if (e == null)
						break; // Error case ?
					if (!e.hasToken()) {
						vEnum.addElement(e.content);
					}
				}

			} else {
				// ID
				// IDREF
				// ENTITY
				// ENTITIES
				// NMTOKEN
				// NMTOKENS
				// CDATA

				if ("ID".equals(e.content))
					valueType = DTDDocumentBuilder.ID_ATT_VAL;
				else if ("IDREF".equals(e.content))
					valueType = DTDDocumentBuilder.IDREF_ATT_VAL;
				else if ("ENTITY".equals(e.content))
					valueType = DTDDocumentBuilder.ENTITY_ATT_VAL;
				else if ("ENTITIES".equals(e.content))
					valueType = DTDDocumentBuilder.ENTITIES_ATT_VAL;
				else if ("NMTOKEN".equals(e.content))
					valueType = DTDDocumentBuilder.NMTOKEN_ATT_VAL;
				else if ("NMTOKENS".equals(e.content))
					valueType = DTDDocumentBuilder.NMTOKENS_ATT_VAL;
				else if ("CDATA".equals(e.content))
					valueType = DTDDocumentBuilder.CDATA_ATT_VAL;
			}

			// Attribute declaration
			int attDec = DTDDocumentBuilder.IMPLIED_ATT;

			e = nextElement((e.stop + 1));
			if (e == null) {
				return location + 1;
			}

			String def = "";
			if (e.literal)
				def = e.content;

			if ("#IMPLIED".equals(e.content)) {
				attDec = DTDDocumentBuilder.IMPLIED_ATT;
			} else if ("#REQUIRED".equals(e.content)) {
				attDec = DTDDocumentBuilder.REQUIRED_ATT;
			} else if ("#FIXED".equals(e.content)) {
				// May be default value
				attDec = DTDDocumentBuilder.FIXED_ATT;
			}

			// Default value ?

			if (!e.literal) {
				e = nextElement( e.stop + 1 );
				if ( e != null ) {
					if (e.literal) {
						def = e.content;
					} else
						priorElement = e;
				}
			}

			// Notify this attribute
			String[] enume = new String[vEnum.size()];
			vEnum.copyInto(enume);

			cBuilder
					.notifyAttribute(element, att, valueType, enume, attDec, def);
		}

		return e.stop + 1;
	}
	
	/**
	 * Parse the comment starting at location
	 * @return the next location to process */
	private int parseComment(int location) {
		int end = contentToParse.indexOf("-->", location);
		if (end > -1) {
			cBuilder.notifyComment(contentToParse
					.substring(location, end) );
			return end + 3;
		} else
			return location + 1;
	}

	private int parseConditionnal( int location ) {
		
		ParserElement e = nextElement( location );
		
		if (e == null) // Bad ??
			return (location + 1);
		
		if ( EntityDTDNode.IGNORE.equals( e.content ) ) {
			
			// Go to the next ]]>
			
			int end = contentToParse.indexOf( "]]>", location );
			if ( end > -1 ) {
				return end + 3;
			} else
				return location + 1; // ??
			
		} else
		if ( EntityDTDNode.INCLUDE.equals( e.content ) ) {
		
			// Go to the next [
			
			int end = contentToParse.indexOf( "[", location );
			if ( end > -1 )
				return end + 1;
			else
				return location + 1; // ??
			
		} else 
		return location + 1; // ??
		
	}
	
	public static void main( String[] args ) throws Exception {
		DTDParser p = new DTDParser();
		p.parse( new InputStreamReader( new FileInputStream( "C:/travail/clients/compoMeca/UP-v1.dtd" ) ) );
		RootDTDNode node = p.getDTDElement();
		node.writeDTD( new PrintWriter( new OutputStreamWriter( 
			new FileOutputStream( 
				"C:/travail/clients/compoMeca/UP-v1-editix.dtd" ) ) )
		);
	}

}

// Parser ends here
