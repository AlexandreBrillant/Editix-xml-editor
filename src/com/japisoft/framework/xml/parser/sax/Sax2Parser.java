package com.japisoft.framework.xml.parser.sax;

import java.io.*;
import java.net.URL;
import java.util.*;
import org.xml.sax.*;
//import org.xml.sax.ext.*;
import org.xml.sax.helpers.*;

import com.japisoft.framework.collection.FastVector;
import com.japisoft.framework.xml.parser.HandlerException;
import com.japisoft.framework.xml.parser.ParseException;
import com.japisoft.framework.xml.parser.FPParser;
import com.japisoft.framework.xml.parser.document.Document;
import com.japisoft.framework.xml.parser.tools.TraceContentHandler;

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
public final class Sax2Parser extends FPParser implements XMLReader, Locator {

	public Sax2Parser() {
		super();
		bufferingMode(false);
	}

	/** @return true if a feature is supported */
	public boolean getFeature(String name) throws SAXNotRecognizedException,
			SAXNotSupportedException {
		if ("http://xml.org/sax/features/namespaces".equals(name))
			return true;
		if ("http://xml.org/sax/features/namespace-prefixes".equals(name))
			return true;
		return false;
	}

	/** Set a Parser feature */
	public void setFeature(String name, boolean value)
			throws SAXNotRecognizedException, SAXNotSupportedException {
		// No effect
	}

	/** Property : no effect */
	public Object getProperty(String name) throws SAXNotRecognizedException,
			SAXNotSupportedException {
		throw new SAXNotRecognizedException("Unknown property " + name);
	}

	/** Property : no effect */
	public void setProperty(String name, Object value)
			throws SAXNotRecognizedException, SAXNotSupportedException {
		throw new SAXNotSupportedException("Unknown property " + name);
	}

	/** No effect without validation support */
	public void setEntityResolver(EntityResolver resolver) {

	}

	/** No effect without validation support */
	public EntityResolver getEntityResolver() {
		return null;
	}

	/** No effect without validation support */
	public void setDTDHandler(DTDHandler handler) {

	}

	/** No effect without validation support */
	public DTDHandler getDTDHandler() {
		return null;
	}

	private ContentHandler handler;

	/** Set the listener for XML processing events */
	public void setContentHandler(ContentHandler handler) {
		this.handler = handler;
		handler.setDocumentLocator(this);
	}

	/** @return the listener for XML processing events */
	public ContentHandler getContentHandler() {
		return handler;
	}

	private ErrorHandler errorHandler;

	public void setErrorHandler(ErrorHandler handler) {
		this.errorHandler = handler;
	}

	public ErrorHandler getErrorHandler() {
		return errorHandler;
	}

	protected void unknownEntityDetected(String entity) {
		try {
			handler.skippedEntity(entity);
		} catch (SAXException exc) {
		}
	}
	
	public void parse(InputSource source) throws IOException, SAXException {
		systemId = source.getSystemId();
		try {
			parse( source.getCharacterStream());
		} catch (ParseException e) {
			if (errorHandler != null) {
				errorHandler.error(new SAXParseException(e.getMessage(), null,
						null, e.getLine(), e.getCol()));
			}

			throw new SAXException(e.getMessage(), e);
		}
	}
	
	private String currentTag;

	private String currentLocalName;

	private String currentAttribute;

	private String currentAttributeLocal;

	private AttributesSax2Impl currentAttributes;

	private String closingTagPrefix = null;

	private Hashtable htURI;
	private Hashtable htPrefix;

	private String systemId = null;

	public void parse(String systemId) throws IOException, SAXException {
		this.systemId = systemId;
		parse(new InputSource(systemId));
	}

	/*
	public Document parse() throws ParseException {
		if (handler == null)
			throw new ParseException("No contentHandler");

		try {		
			handler.startDocument();
			try {
				return super.parse();
			} finally {
				handler.endDocument();
			}
		}  catch (SAXException e) {
			throw new ParseException(e.getMessage());
		}
	}
	*/

	private boolean prolog = false;
	private boolean closeIt = false;
	private FastVector prefixToCheck = null;
	
	protected void fireItemFound(int state, String item) throws ParseException {
	
		try {

			String tmpCurrentTag = null;
			String tmpCurrentPrefix = null;

			if (currentTag != null) {
				if ( (state != 5) && 
						(state != 8) && 
							(state != 17) && 
								(state != 18)) {

					prolog = false;
					
					if (currentAttributes == null)
						currentAttributes = new AttributesSax2Impl();
					else {

						if ( prefixToCheck != null ) {
							for ( int i = 0; i < prefixToCheck.size(); i++ ) {
								String prefix = ( String )prefixToCheck.get( i );
								if ( htURI.get( prefix ) == null ) {
									throw new SAXException( "Invalid prefix '" + prefix + "'" );
								}
							}
							prefixToCheck = null;
						}
						
					}

					if (currentLocalName != null) {
						// NameSpace prefix
						if (htURI == null)
							throw new SAXException("Invalid prefix '"
									+ currentTag + "'");
						String uri = (String) htURI.get(currentTag);

						if (uri == null)
							throw new SAXException("Invalid prefix '"
									+ currentTag + "'");

						handler.startElement(uri, currentLocalName, currentTag
								+ ":" + currentLocalName, currentAttributes);
						tmpCurrentTag = currentLocalName;
						tmpCurrentPrefix = currentTag;
					} else {
						tmpCurrentTag = currentTag;
						tmpCurrentPrefix = null;
						// No NameSpace

						handler.startElement("", currentTag, currentTag,
								currentAttributes);
					}

					if  (closeIt) {
						closeIt = false;
						handler.endElement( "", currentTag, currentTag );
					}
					
					currentAttributes = null;
					currentTag = null;
					currentLocalName = null;
				}
			}

			switch (state) {
			case 2: // TEXT
				handler.characters(item.toCharArray(), 0, item.length());
				break;
			case 3: {

				if ("?xml".equals(item))
					prolog = true;

				if (item.startsWith("/")) {
					item = item.substring(1);

					currentLocalName = null;

					if (!realTimeCurrentNS) {
						// URI ?

						handler.endElement("", item, item);

						if (htPrefix != null) {
							// Check for endPrefixMapping
							FastVector v = (FastVector) htPrefix.get(item);
							if (v != null) {
								for (int i = v.size() - 1; i >= 0; i--)
									handler.endPrefixMapping((String) v.get(i));
							}

						}
					} else {
						closingTagPrefix = item;
					}
				} else {
					if (!prolog) {
						if ( item.endsWith( "/" ) ) {
							item = item.substring( 0, item.length() - 1 );
							closeIt = true; 
						}
						currentTag = item;
					}
				}
				break;
			}

			case 5: // Attribute name or prefix
				currentAttribute = item;
				//		currentNS = false;
				break;
			case 8: // Attribute value

				if (prolog)
					return;

				if (currentAttributes == null) {
					currentAttributes = new AttributesSax2Impl();
				}

				if (currentAttributeLocal != null) {
					if (htURI == null) {
						htURI = new Hashtable();
						htURI.put( "xml", "");
						htPrefix = new Hashtable();
					}

					if ( "xmlns".equals( currentAttribute ) ) {
						handler.startPrefixMapping(currentAttributeLocal, item);

						htURI.put(currentAttributeLocal, item);

						String tmp = currentTag;
						if (currentLocalName != null)
							tmp = currentLocalName;
						FastVector v = (FastVector) htPrefix.get(tmp);
						if (v == null) {
							v = new FastVector();
							htPrefix.put(tmp, v);
						}
						v.add(currentAttributeLocal);

					} else {
						
						String uri = ( String )htURI.get( currentAttribute );
						if (uri == null) {
							if ( prefixToCheck == null )
								prefixToCheck = new FastVector();
							prefixToCheck.add( currentAttribute );
						}
//							throw new SAXException("Invalid prefix '"
//									+ currentAttribute + "'");

						currentAttributes.addAttribute(currentAttribute,
								currentAttributeLocal, uri, "CDATA", item);
					}
				} else {
					if (!"xmlns".equals(currentAttribute))
						currentAttributes.addAttribute(null, currentAttribute,
								"", "CDATA", item);
				}

				currentAttributeLocal = null;
				break;
			case 10:

				if ("?".equals(item))
					prolog = false;

				if ("/".equals(item)) {
					currentLocalName = null;

					if (tmpCurrentPrefix == null) {
						handler.endElement("", tmpCurrentTag, tmpCurrentTag);
					} else {
						if (htURI == null)
							throw new SAXException("Invalid prefix '"
									+ tmpCurrentPrefix + "'");
						String uri = (String) htURI.get(tmpCurrentPrefix);
						if (uri == null)
							throw new SAXException("Invalid prefix '"
									+ tmpCurrentPrefix + "'");

						handler.endElement(uri, tmpCurrentTag, tmpCurrentPrefix
								+ ":" + tmpCurrentTag);

						// NameSpace prefix ?
						FastVector v = null;
						if ((v = (FastVector) htPrefix.get(tmpCurrentTag)) != null) {
							for (int i = v.size() - 1; i >= 0; i--) {
								String tmp;
								handler.endPrefixMapping(tmp = (String) v
										.get(i));
								htURI.remove(tmp);
							}
						}
					}

					tmpCurrentPrefix = null;
					tmpCurrentTag = null;
					currentTag = null;
					currentAttributes = null;
					currentLocalName = null;
				}
				break;
			case 13:
				// IGNORE COMMENT ?
				break;
			case 15: // CDATA
				item = item.substring( "[CDATA[".length() - 1, item.length() );
				handler.characters(item.toCharArray(), 0, item.length());
				break;

			case 17: // ELEMENT PREFIX
				
				boolean flag =  false;
				
				if ( item.endsWith( "/" ) ) {
					item = item.substring( 0, item.length() - 1 );
					
					String uri = ( String ) htURI.get( currentTag );
					if (uri == null)
						uri = "";

					handler.startElement( uri, item, currentTag + ":" + item, new AttributesSax2Impl() );
					closingTagPrefix = currentTag;
					flag = true;
				}

				currentLocalName = item;
				if (closingTagPrefix != null) {
					String uri = (String) htURI.get(closingTagPrefix);
					if (uri == null)
						uri = "";

					handler
							.endElement(uri, item, closingTagPrefix + ":"
									+ item);

					currentLocalName = null;

					// Check for endPrefixMapping
					FastVector v = (FastVector) htPrefix.get(item);
					if (v != null) {
						for (int i = v.size() - 1; i >= 0; i--)
							handler.endPrefixMapping((String) v.get(i));
					}
					closingTagPrefix = null;
				}

				if ( flag ) {
					currentLocalName = null;
					currentTag = null;
				}
				
				break;

			case 18: // ATTRIBUT PREFIX // LOCAL NAME ...
				currentAttributeLocal = item;
				break;
			}

		} catch (SAXException e) {
			throw new ParseException(e.getMessage());
		} catch (Throwable th) {
			throw new HandlerException(th);
		}
	}

	//////////////////// LOCATOR PART ///////////////////

	public int getColumnNumber() {
		return col;
	}

	public int getLineNumber() {
		return line;
	}

	public String getPublicId() {
		return null;
	}

	public String getSystemId() {
		return systemId;
	}

	/////////////////////////////////////////////////////

	public static void main(String[] args) throws Exception {
		Sax2Parser sp = new Sax2Parser();
		sp.setContentHandler(new TraceContentHandler());
		sp.parse(new FileInputStream(
		"/home/japisoft/xml-samples/namespace.xml" ));
		System.out.println("Ok");
	}

}

// Sax2Parser ends here
