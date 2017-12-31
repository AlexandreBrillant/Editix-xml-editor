package com.japisoft.framework.xml.parser.sax;

import java.io.*;
import java.util.*;
import org.xml.sax.*;
import org.xml.sax.helpers.*;

import com.japisoft.framework.xml.parser.HandlerException;
import com.japisoft.framework.xml.parser.ParseException;
import com.japisoft.framework.xml.parser.FPParser;
import com.japisoft.framework.xml.parser.document.DocumentBuilder;

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
public final class SaxParser extends FPParser implements org.xml.sax.Parser,
		Locator {

	public SaxParser() {
		super();
		setEnabledNameSpace(false);
		bufferingMode(false);
	}

	/**
	 * Allow an application to request a locale for errors and warnings.
	 * 
	 * <p>
	 * SAX parsers are not required to provide localisation for errors and
	 * warnings; if they cannot support the requested locale, however, they must
	 * throw a SAX exception. Applications may not request a locale change in
	 * the middle of a parse.
	 * </p>
	 * 
	 * @param locale
	 *            A Java Locale object.
	 * @exception org.xml.sax.SAXException
	 *                Throws an exception (using the previous or default locale)
	 *                if the requested locale is not supported.
	 * @see org.xml.sax.SAXException
	 * @see org.xml.sax.SAXParseException
	 */
	public void setLocale(Locale locale) throws SAXException {
		throw new SAXException("Not supported");
	}

	/**
	 * Allow an application to register a custom entity resolver.
	 * 
	 * <p>
	 * If the application does not register an entity resolver, the SAX parser
	 * will resolve system identifiers and open connections to entities itself
	 * (this is the default behaviour implemented in HandlerBase).
	 * </p>
	 * 
	 * <p>
	 * Applications may register a new or different entity resolver in the
	 * middle of a parse, and the SAX parser must begin using the new resolver
	 * immediately.
	 * </p>
	 * 
	 * @param resolver
	 *            The object for resolving entities.
	 * @see EntityResolver
	 * @see HandlerBase
	 */
	public void setEntityResolver(EntityResolver resolver) {
		// No effect
	}

	/**
	 * Allow an application to register a DTD event handler.
	 * 
	 * <p>
	 * If the application does not register a DTD handler, all DTD events
	 * reported by the SAX parser will be silently ignored (this is the default
	 * behaviour implemented by HandlerBase).
	 * </p>
	 * 
	 * <p>
	 * Applications may register a new or different handler in the middle of a
	 * parse, and the SAX parser must begin using the new handler immediately.
	 * </p>
	 * 
	 * @param handler
	 *            The DTD handler.
	 * @see DTDHandler
	 * @see HandlerBase
	 */
	public void setDTDHandler(DTDHandler handler) {
		// No effect
	}

	/**
	 * Allow an application to register a document event handler.
	 * 
	 * <p>
	 * If the application does not register a document handler, all document
	 * events reported by the SAX parser will be silently ignored (this is the
	 * default behaviour implemented by HandlerBase).
	 * </p>
	 * 
	 * <p>
	 * Applications may register a new or different handler in the middle of a
	 * parse, and the SAX parser must begin using the new handler immediately.
	 * </p>
	 * 
	 * @param handler
	 *            The document handler.
	 * @see DocumentHandler
	 * @see HandlerBase
	 */
	public void setDocumentHandler(DocumentHandler handler) {
		this.handler = handler;
		handler.setDocumentLocator(this);
	}

	private DocumentHandler handler;

	/**
	 * Allow an application to register an error event handler.
	 * 
	 * <p>
	 * If the application does not register an error event handler, all error
	 * events reported by the SAX parser will be silently ignored, except for
	 * fatalError, which will throw a SAXException (this is the default
	 * behaviour implemented by HandlerBase).
	 * </p>
	 * 
	 * <p>
	 * Applications may register a new or different handler in the middle of a
	 * parse, and the SAX parser must begin using the new handler immediately.
	 * </p>
	 * 
	 * @param handler
	 *            The error handler.
	 * @see ErrorHandler
	 * @see SAXException
	 * @see HandlerBase
	 */
	public void setErrorHandler(ErrorHandler handler) {
		this.errorHandler = handler;
	}

	private ErrorHandler errorHandler;

	/**
	 * Parse an XML document.
	 * 
	 * <p>
	 * The application can use this method to instruct the SAX parser to begin
	 * parsing an XML document from any valid input source (a character stream,
	 * a byte stream, or a URI).
	 * </p>
	 * 
	 * <p>
	 * Applications may not invoke this method while a parse is in progress
	 * (they should create a new Parser instead for each additional XML
	 * document). Once a parse is complete, an application may reuse the same
	 * Parser object, possibly with a different input source.
	 * </p>
	 * 
	 * @param source
	 *            The input source for the top-level of the XML document.
	 * @exception org.xml.sax.SAXException
	 *                Any SAX exception, possibly wrapping another exception.
	 * @exception java.io.IOException
	 *                An IO exception from the parser, possibly from a byte
	 *                stream or character stream supplied by the application.
	 * @see org.xml.sax.InputSource
	 * @see #parse(java.lang.String)
	 * @see #setEntityResolver
	 * @see #setDTDHandler
	 * @see #setDocumentHandler
	 * @see #setErrorHandler
	 */
	public void parse(InputSource source) throws SAXException, IOException {
		systemId = source.getSystemId();
		try {
			start = false;
			parse( source.getCharacterStream());
			if (handler != null) {
				handler.endDocument();
			}
		} catch (ParseException e) {
			if (errorHandler != null) {
				errorHandler.error(new SAXParseException(e.getMessage(), null,
						null, e.getLine(), e.getCol()));
			}
			throw new SAXException(e.getMessage(), e);
		}
	}
	
	/**
	 * Parse an XML document from a system identifier (URI).
	 * 
	 * <p>
	 * This method is a shortcut for the common case of reading a document from
	 * a system identifier. It is the exact equivalent of the following:
	 * </p>
	 * 
	 * <pre>
	 * parse(new InputSource(systemId));
	 * </pre>
	 * 
	 * <p>
	 * If the system identifier is a URL, it must be fully resolved by the
	 * application before it is passed to the parser.
	 * </p>
	 * 
	 * @param systemId
	 *            The system identifier (URI).
	 * @exception org.xml.sax.SAXException
	 *                Any SAX exception, possibly wrapping another exception.
	 * @exception java.io.IOException
	 *                An IO exception from the parser, possibly from a byte
	 *                stream or character stream supplied by the application.
	 * @see #parse(org.xml.sax.InputSource)
	 */
	public void parse(String systemId) throws IOException, SAXException {
		parse(new InputSource(this.systemId = systemId));
	}

	/////////////////////////////////////////////////////////////////////////////

	private boolean start = false;

	private String currentTag;

	private String currentAttribute;

	private AttributeListImpl currentAttributes;

	protected void fireItemFound(DocumentBuilder db, int state, String item) throws ParseException {
		if (handler == null)
			super.fireItemFound(db, state, item);
		else {
			try {
				if (!start) {
					handler.startDocument();
					start = true;
				}

				String tmpCurrentTag = null;

				if (currentTag != null) {
					if ((state != 5) && (state != 8)) {

						if (currentAttributes == null)
							currentAttributes = new AttributeListImpl();

						// Open tag
						handler.startElement(currentTag, currentAttributes);
						tmpCurrentTag = currentTag;
						currentTag = null;
						currentAttributes = null;
					}
				}

				switch (state) {
				case 2: // TEXT
					handler.characters(item.toCharArray(), 0, item.length());
					break;
				case 3: {
					if (item.startsWith("/")) {
						handler.endElement(item.substring(1));
					} else {
						if (!item.startsWith("?")) {
							if (!"".equals(item)) {
								currentTag = item;
								currentAttributes = null;
							}
						}
					}
					break;
				}
				case 5: // Attribute name
					currentAttribute = item;
					break;
				case 8: // Attribute value
					if (currentAttributes == null) {
						currentAttributes = new AttributeListImpl();
					}
					currentAttributes
							.addAttribute(currentAttribute, null, item);
					currentAttribute = null;
					break;
				case 10:
					if ("/".equals(item)) {
						if (tmpCurrentTag == null)
							handler.endElement(currentTag);
						else
							handler.endElement(tmpCurrentTag);
						tmpCurrentTag = null;
						currentTag = null;
						currentAttributes = null;
					}
					break;
				case 13:
					// IGNORE COMMENT ?
					break;
				case 15: // CDATA
					item = item
							.substring("[CDATA[".length() - 1, item.length());
					handler.characters(item.toCharArray(), 0, item.length());
					break;
				}
			} catch (SAXException e) {
				throw new ParseException(e.getMessage());
			} catch (Throwable th) {
				throw new HandlerException(th);
			}
		}
	}

	//////////////////// LOCATOR PART ///////////////////

	private String systemId = null;

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
		SaxParser sp = new SaxParser();
		sp.parse(new FileInputStream("/tmp/test.xml"));
		System.out.println("Ok");
	}
	
}

// SaxParser ends here
