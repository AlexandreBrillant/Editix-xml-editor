package com.japisoft.framework.xml.format;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Stack;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXResult;

import org.w3c.dom.Node;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.ext.LexicalHandler;
import com.japisoft.framework.xml.XMLToolkit;
import com.japisoft.framework.xml.refactor2.AbstractRefactor;

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
public class Formatter 
	implements 
		ContentHandler,
		LexicalHandler {
	
	private StringBuffer r = null;
	
	public Formatter() {
		r = new StringBuffer();
		openedStack = new Stack<String>();
	}

	public String getResult( String prolog ) {
		if ( prolog != null )
			r.insert(0,prolog);
		return r.toString();
	}

	public void setDocumentLocator( Locator locator ) {}

	public void startDocument() throws SAXException {		
	}

	public void endDocument() throws SAXException {
		wsStack = null;		
	}

	private ArrayList prefixList = null;
	
	public void startPrefixMapping(String prefix, String uri)
			throws SAXException {
		if ( prefixList == null )
			prefixList = new ArrayList();
		if ( !prefixList.contains( prefix ) ) {
			prefixList.add( prefix );
			prefixList.add( uri );
		}
	}

	public void endPrefixMapping(String prefix) throws SAXException {
		if ( prefixList != null ) {
			int i = prefixList.indexOf( prefix );
			if ( i > -1 ) {
				prefixList.remove( i );
				prefixList.remove( i );
			}
		}
	}

	private Stack openedStack = null;
	// Preserve, Default Stack for Whitespaces
	private Stack wsStack = null;
	private boolean delayedStartElement = false;
	private boolean firstElementFound = false;

	public void startElement(String uri, String localName, String qName,
			Attributes atts) throws SAXException {

		firstElementFound = true;
		checkForDelayedStartElement();

		if ( !foundText ) {
			indent();
		}
		
		String ns = null;
		if ( atts != null && config.isXMLSpace() )
			ns = atts.getValue( "xml:space" );
		
		// Particular case for XSLT
		if ( "text".equals( qName ) || "text".equals( localName ) ) {
			if ( "http://www.w3.org/1999/XSL/Transform".equals( uri ) ) {
				ns = "preserve";
			}
		}

		if ( ns != null && !"".equals( ns ) ) {
			if ( wsStack == null ) {
				wsStack = new Stack<String>();
			}
			wsStack.add( qName + ":" + ns );
		}

		foundText = false;
		
		r.append( "<" ).append( qName );		
		if ( atts != null && atts.getLength() > 0 ) {
			for ( int i = 0; i < atts.getLength(); i++ ) {
				String attName = atts.getQName( i );
				if ( attName == null )
					continue;
				r.append( " " ).append( attName ).append( "=\"" ).append( processAttributeText( atts.getValue( i ) ) ).append( "\"" );
			}
		}
		
		// Add prefix mapping
		if ( prefixList != null ) {
			for ( int i = 0; i < prefixList.size(); i += 2 ) {
				// Duplicate attributes with prefix definition
				if ( atts.getIndex( "xmlns" + ":" + prefixList.get( i ) ) > -1 )
					continue;
				r.append( " xmlns" );
				if ( !"".equals( prefixList.get( i ) ) )
					r.append( ":" ).append( prefixList.get( i ) );
				r.append( "=\"" );
				r.append( processAttributeText( ( String )prefixList.get( i + 1 ) ) ).append( "\"" );
			}
			prefixList = null;
		}

		if ( !isAutoCloseElement() )
			r.append( ">" );
		else
			delayedStartElement = true;
		
		openedStack.push( qName );
		
	}

	private void indent() {
		if ( config.isIndent() ) {	
			if ( !mustPreserveSpace() ) {	
				r.append( "\n" );
				for ( int i = 0; i < openedStack.size() * config.getFormatSpaceQty(); i++ ) {
					r.append( config.getFormatSpaceChr() );
				}
			}
		}
	}

	private void checkForDelayedStartElement() {
		if ( delayedStartElement ) {
			r.append( ">" );
			if ( delayedWhiteSpaces != null ) {
				r.append( delayedWhiteSpaces );
				delayedWhiteSpaces = null;
			}
			delayedStartElement = false;
		}
	}

	public void endElement(String uri, String localName, String qName)
			throws SAXException {

		boolean alreadyClosed = false;
		
		// EMPTY ELEMENT
		if ( delayedStartElement ) {
			r.append( "/>" );
			delayedStartElement = false;
			alreadyClosed = true;
		}
		
		foundCDATA = false;
		foundComment = false;
		foundProcessingInstruction = false;
		
		openedStack.pop();

		if ( !alreadyClosed ) {
			if ( !foundText )
				indent();
			foundText = false;
			
			r.append( "</" ).append( qName ).append( ">" );
		}
		
		if ( wsStack != null ) {
			if ( wsStack.size() > 0 ) {
				if ( ( ( String )wsStack.peek() ).startsWith( qName ) ) {
					wsStack.pop();
				}
			}
		}
	}

	private boolean foundText = false;

	private String delayedWhiteSpaces = null;
	
	public void characters(char[] ch, int start, int length)
			throws SAXException {
		boolean onlyWhiteSpace = !foundText;
		if ( !foundText ) {	// For sibling text node
			for ( int i = start; i < start + length; i++ ) {
				if ( !Character.isWhitespace( ch[ i ] ) ) {
					onlyWhiteSpace = false;
					break;
				}
			}
		}
		if ( !onlyWhiteSpace ) {
			foundText = true;
			checkForDelayedStartElement();			
			r.append( processElementText( ch, start, length ) );
		} else {			
			if ( mustPreserveSpace() ) {
				if ( !delayedStartElement ) {
					r.append( new String( ch, start, length ) );	
				} else {
					delayedWhiteSpaces = new String( ch, start, length );
				}
			}
		}
	}

	private StringBuffer reusableBuffer = null;

	private String processElementText( char[] ch, int start, int length ) {

		String res = null;
		
		if ( !inCDATA ) {
		
			if ( reusableBuffer == null )
				reusableBuffer = new StringBuffer();
			else
				reusableBuffer.delete( 0, reusableBuffer.length() );
	
			for ( int i = start; i < start + length; i++ ) {
				
				// Inside the start entity
				if ( ch[ i ] == '<' ) {
					// reusableBuffer.append( "&lt;" );
				}
				else
				if ( ch[ i ] == '>' ) {
					// reusableBuffer.append( "&gt;" );
				}
				else
				if ( ch[ i ] == '&' ) {
					// reusableBuffer.append( "&amp;" );
				}
				else
					reusableBuffer.append( ch[ i ] );
			}
			
			res = reusableBuffer.toString();

		} else
			res = new String( ch, start, length );
		
		if ( config.isTrimedText() ) {

			// Trim the start and the end part of the text only

			int startPart = 0;
			
			for ( int i = 0; i < res.length(); i++ ) {
				if ( !Character.isWhitespace( 
						res.charAt( i ) ) ) {
					startPart = i;
					break;
				}
			}

			int endPart = ( res.length() - 1 );
			
			for ( int i = endPart; i >= 0; i-- ) {
				if ( !Character.isWhitespace( 
						res.charAt( i ) ) ) {
					endPart = i;
					break;
				}
			}

			return res.substring( 
					startPart, 
					endPart + 1 );

		}

		return res;
	}

	private String processAttributeText( String value ) {
		if ( reusableBuffer == null )
			reusableBuffer = new StringBuffer();
		else
			reusableBuffer.delete( 0, reusableBuffer.length() );
		for ( int i = 0; i < value.length(); i++ ) {
			char c = value.charAt( i );			
			if ( c == '&' )
				reusableBuffer.append( "&amp;" );
			else
			if ( c == '"' )
				reusableBuffer.append( "&quot;" );
			else
			if ( c == '<' )
				reusableBuffer.append( "&lt;" );
			else
			if ( c == '>' )
				reusableBuffer.append( "&gt;" );
			else
				reusableBuffer.append( c );
		}
		return reusableBuffer.toString();
	}
		
	public void ignorableWhitespace(char[] ch, int start, int length)
			throws SAXException {
		if ( mustPreserveSpace() ) {
			r.append( new String( ch, start, length ) );			
		}
	}

	private boolean foundProcessingInstruction = false;
	
	public void processingInstruction(String target, String data)
			throws SAXException {
		if ( !firstElementFound ) 
			return;
		checkForDelayedStartElement();
		foundProcessingInstruction = true;
		if ( config.isIndentProcessingInstruction() )
			indent();
		r.append( "<?" ).append( target ).append( " " ).append( data ).append( "?>" );
	}

	public void skippedEntity(String name) throws SAXException {
		if ( !firstElementFound ) 
			return;
		checkForDelayedStartElement();
		r.append( "&" ).append( name ).append( ";" );
	}

	public void startDTD(String name, String publicId, String systemId)
			throws SAXException {
	}

	public void endDTD() throws SAXException {
	}

	public void startEntity(String name) throws SAXException {
		if ( !firstElementFound ) 
			return;
		checkForDelayedStartElement();
		r.append( "&" ).append( name ).append( ";" );
	}

	public void endEntity(String name) throws SAXException {
		if ( "apos".equals( name ) ) {
			if ( r.charAt( r.length() - 1 ) == '\'' ) {
				r.deleteCharAt( r.length() - 1 );
			}
		} else
		if ( "quot".equals( name ) ) {
			if ( r.charAt( r.length() - 1 ) == '"' ) {
				r.deleteCharAt( r.length() - 1 );
			}			
		}
	}

	private boolean foundCDATA = false;
	private boolean inCDATA = false;
	
	public void startCDATA() throws SAXException {
		checkForDelayedStartElement();
		foundCDATA = true;
		inCDATA = true;
		r.append( "<![CDATA[" );
	}

	public void endCDATA() throws SAXException {
		r.append( "]]>" );
		inCDATA = false;
	}

	private boolean foundComment = false;
	
	public void comment(
			char[] ch, 
			int start, 
			int length ) throws SAXException {
		if ( !firstElementFound ) 
			return;		
		checkForDelayedStartElement();
		foundComment = true;
		r.append( "<!--" ).append( new String( ch, start, length ) ).append( "-->" );
	}

	private boolean mustPreserveSpace() {
		if ( wsStack != null ) {
			if ( wsStack.size() > 0 ) {
				if ( ( ( String )wsStack.peek() ).endsWith( ":preserve" ) ) {
					return true;
				}
			}
		}
		return false;
	}
	
	private boolean isAutoCloseElement() {
		if ( config.isAutoCloseElement() ) {
			if ( mustPreserveSpace() )
				return false;
			return true;
		}
		return false;
	}
	
	private FormatterConfig config = null;
	
	public void setFormatterConfig( FormatterConfig config ) {
		if ( config == null )
			throw new RuntimeException( "The formatterConfig can't be null !" );
		this.config = config;
	}

	public static String format( 
			Object content, 
			Formatter f, 
			FormatterConfig config, 
			AbstractRefactor refactor ) throws Exception {
		// Unknown error ?
		//SAXParser sp = XMLParser.getSaxParser( false );
		SAXParserFactory spf = SAXParserFactory.newInstance();
		spf.setValidating( false );
		spf.setNamespaceAware( true );
		
		SAXParser sp = spf.newSAXParser();
		if ( f == null )	// Default case
			f = new Formatter();
		if ( config == null )
			config = new FormatterConfig();
		f.setFormatterConfig( config );
		XMLReader reader = sp.getXMLReader();
		reader.setProperty(
			      "http://xml.org/sax/properties/lexical-handler",
			      f );
		if ( refactor != null ) {
			refactor.setContentHandler( f );
			reader.setContentHandler( refactor );
		} else
			reader.setContentHandler( f );
		
		// Force an empty DTD due to a Xerces problem trying to validate with a DTD
		reader.setEntityResolver(
				new EntityResolver() {
					public InputSource resolveEntity(String publicId, String systemId) throws SAXException ,java.io.IOException {
						return new InputSource( new StringReader( "" ) ); 
					}
				} );

		if ( content instanceof String ) {
			reader.parse( 
				new InputSource( 
						new StringReader( ( String )content ) 
				)
			);
			String fullProlog = XMLToolkit.getFullProlog( ( String )content );
			return f.getResult( fullProlog );
		} else {
			
			TransformerFactory t = TransformerFactory.newInstance();
			Transformer tr = t.newTransformer();
			tr.transform( 
				new DOMSource( 
					( Node )content 
				),
				new SAXResult( f )
			);
			return f.getResult( null );
			
		}
	}

	public static String format( 
			Node content ) throws Exception {
		
		return format( 
			content, 
			null, 
			null, 
			null 
		);
	}

}
