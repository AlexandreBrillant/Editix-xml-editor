package com.japisoft.xmlpad.xml.validator;

import com.japisoft.dtdparser.DTDMapper;
import com.japisoft.dtdparser.DTDMapperFactory;
import com.japisoft.xmlpad.Debug;
import com.japisoft.xmlpad.SharedProperties;
import com.japisoft.xmlpad.XMLContainer;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.text.Element;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import org.w3c.dom.*;
import org.xml.sax.*;

import com.japisoft.xmlpad.editor.XMLEditor;

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
public class DefaultValidator implements Validator, ErrorHandler {

	private XMLEditor editor;
	static final String JAXP_SCHEMA_LANGUAGE = "http://java.sun.com/xml/jaxp/properties/schemaLanguage";
	static final String W3C_XML_SCHEMA = "http://www.w3.org/2001/XMLSchema";
	static final String JAXP_SCHEMA_SOURCE = "http://java.sun.com/xml/jaxp/properties/schemaSource";
	
	public int validate( XMLContainer container, boolean silentMode ) {
		editor = container.getEditor();
		int res = OK;
		try {
			if ( canValidate( container ) )
				res = notifyAction( container, silentMode );
			else
				res = ERROR;
		} finally {
			editor = null;
		}
		return res;
	}
	
	protected boolean canValidate( XMLContainer container ) {
		return true;
	}

	// Return false for a bad location
	private boolean checkLocation(String s) {
		if ( 
				s.indexOf("://") == -1 && 
				DTDMapperFactory.getDTDMapper() == null ) {
			File f = new File( s );
			return f.exists();
		} else
			return true;
	}

	private boolean domBuilderMode = false;

	/**
	 * Include a DOM builder for retreiving a full DOM document from the parsing
	 * action. By default this property is <code>false</code>
	 * 
	 * @param domBuilderMode
	 *            If <code>true</code> a DOM document will be built using the
	 *            parsing action
	 */
	public void setDomBuilderMode(boolean domBuilderMode) {
		this.domBuilderMode = domBuilderMode;
	}

	private Document dom = null;

	public DefaultValidator() {
		super();
	}

	/**
	 * If <code>true</code>, a DOM document will be built and retreive using
	 * the getDocument method.
	 * 
	 * @param domBuilderMode
	 */
	public DefaultValidator(boolean domBuilderMode) {
		this();
		this.domBuilderMode = domBuilderMode;
	}

	private boolean tryToValidate = true;

	/**
	 * If <code>true</code>, a DOM document will be built and retreive using
	 * the getDocument method.
	 * 
	 * @param domBuilderMode
	 *            Build a final DOM document
	 * @param tryValidating
	 *            check if the document can be validated
	 */
	public DefaultValidator(boolean domBuilderMode, boolean ToValidating) {
		this();
		this.domBuilderMode = domBuilderMode;
		this.tryToValidate = ToValidating;
	}

	public int notifyAction(XMLContainer container, boolean silentMode ) {

		try {

			errorStatus = false;
			InputStream input = null;
			boolean validating = false;
			InputSource is = null;

			Reader inputReader = null;

			String content = container.getText();
			if (domBuilderMode)
				content = content.replaceAll("&#10;", "&amp;#10;");
			inputReader = new StringReader(content);

			is = new InputSource(inputReader);
			if (container.getCurrentDocumentLocation() != null) {
				is.setSystemId( new File( container.getCurrentDocumentLocation() )
						.toURL().toString() );
			}

			// DTD Detection

			boolean schemaFound = false;
			boolean dtdFound = false;
			boolean resolverMode = ( SharedProperties.DEFAULT_ENTITY_RESOLVER != null );

			if ( tryToValidate ) {
				
				try {
					
					// Schema Detection first


					schemaFound = container.searchAndParseSchema();

					if (schemaFound) {
						String _ = container.getSchemaAccessibility()
								.getSchemaLocation(true);

						if (!container.hasSyntaxCompletion()
								&& !checkLocation(_)) {
							errorStatus = true;
							//editor.notifyError(null, 0, "Can't find Schema " + _);
							return ERROR;
						}

						validating = true;
					}
					
					if ( !schemaFound ) {
						dtdFound = container.searchAndParseDTD();
						
						if ( dtdFound ) {
							String s = container.getSchemaAccessibility()
									.getDTDLocation(true);
							
							if ( s != null ) { // For local DTD
								if ( !container.hasSyntaxCompletion()
										&& !checkLocation( s ) ) {
									errorStatus = true;
									//editor.notifyError(null, 0, "Can't find DTD " + s);
									return ERROR;
								}
							}
	
							validating = true;
						}
					}

				} catch ( Throwable th ) {
				}


			}

			try {

				// Due to schema location ?
				if ( container.getErrorManager().hasLastError() )
					return ERROR;

				boolean externalSchema = 
					( container.getDocumentInfo().getSchemaXSDValid() != null );
				if ( externalSchema )
					validating = true;
				
				if ( !domBuilderMode ) {

					SAXParser parser = XMLPadSAXParserFactory
							.getNewSAXParser( validating );

					Debug.debug( "Parser=" + parser );

					if ( schemaFound || externalSchema ) {
						try {
							parser.setProperty(
									JAXP_SCHEMA_LANGUAGE,
									W3C_XML_SCHEMA );
						} catch ( SAXNotRecognizedException exc ) {
						}
					}

					if ( externalSchema ) {

						SAXParser parserTmp = buildCustomParser( container );
						if ( parserTmp != null ) {
							parser = parserTmp;
						}

					}

					XMLReader reader = parser.getXMLReader();
					reader.setErrorHandler( this );

					EntityResolver specificEntityResolver = 
						( EntityResolver )container.getProperty( "entityresolver" );

					if ( specificEntityResolver == null ) {

						if ( resolverMode ) {
							reader.setEntityResolver( SharedProperties.DEFAULT_ENTITY_RESOLVER );
						} else if ( DTDMapperFactory.getDTDMapper() != null )
							reader.setEntityResolver( new CustomEntityResolver() );
					
					} else
						reader.setEntityResolver( specificEntityResolver );

					reader.parse( is );

				} else {

					if ( externalSchema )
						validating = false;

					DocumentBuilder builder = XMLPadSAXParserFactory
							.getNewDocumentBuilder( validating, schemaFound );


					Debug.debug( "builder : " + builder );
					
					builder.setErrorHandler(this);
					
					if ( resolverMode )
						builder.setEntityResolver( SharedProperties.DEFAULT_ENTITY_RESOLVER );
					else {
						if ( DTDMapperFactory.getDTDMapper() != null )
							builder.setEntityResolver( new CustomEntityResolver() );
					}

					dom = builder.parse( is );
					if ( dom != null ) {
						if ( dom.getDocumentElement() != null ) {
							dom.getDocumentElement().setUserData( "path", container.getCurrentDocumentLocation(), null );
						}
					}
					

				}
			
				if ( !errorStatus ) {
					errorStatus = postAction( container, silentMode );
				}
				
			} finally {
				if ( is.getCharacterStream() != null )
					is.getCharacterStream().close();
			}
		} catch ( SAXException ex ) {
			errorStatus = true;
		} catch ( IOException ex ) {
			try {
				error( new SAXParseException( ex.getMessage(), null ) );
			} catch (SAXException e) {
			}
			errorStatus = true;
		} catch ( ParserConfigurationException ex ) {
			errorStatus = true;
		} catch ( FactoryConfigurationError exc ) {
			errorStatus = true;
		}

		if ( errorStatus )
			return ERROR;
		return OK;
	}

	// For specific processing
	protected boolean postAction( XMLContainer container, boolean silentMode ) { return false; }
	
	protected SAXParser buildCustomParser( XMLContainer container ) {
		return null;
	}
	
	private boolean errorStatus = false;

	/**
	 * @return a DOM document after the parsing process. Note that the
	 *         domBuilderMode must be enabled for having a document */
	public Document getDocument() {
		return dom;
	}

	public void dispose() {
		this.dom = null;
	}

	/** An error has been found while parsing */
	public boolean hasError() {
		return errorStatus;
	}

	public void warning( SAXParseException exception )
			throws org.xml.sax.SAXException {
	}

	public void error( SAXParseException exception )
			throws org.xml.sax.SAXException {
		processSAXException( exception );
	}

	public void fatalError( SAXParseException exception )
			throws org.xml.sax.SAXException {
		processSAXException( exception );
	}

	private void processSAXException( SAXParseException exception ) {
		errorStatus = true;
		if ( editor != null ) {
			boolean local = true;
			try {
				local = ( exception.getSystemId() == null ) || 
					new URL( exception.getSystemId() ).equals( 
							editor.getXMLContainer().getCurrentDocumentLocationURL() );
			} catch ( MalformedURLException e1 ) {
			}

			int offset = 0;

			try {
				Element e = editor.getDocument().getDefaultRootElement().getElement( 
						exception.getLineNumber() );

				if ( e != null )
					offset = e.getStartOffset() + exception.getColumnNumber();

				editor.getXMLContainer().getErrorManager().notifyError(
						null,
						local,					
						exception.getSystemId(),
						exception.getLineNumber(),
						exception.getColumnNumber(),
						offset,
						exception.getLocalizedMessage(),
						false );
			} catch (ArrayIndexOutOfBoundsException e) {
				editor.getXMLContainer().getErrorManager().notifyError(
						exception.getMessage() );
			}
		}
	}

	class CustomEntityResolver implements EntityResolver {
		public InputSource resolveEntity(String publicId, String systemId)
				throws SAXException, IOException {
			DTDMapper mapper = DTDMapperFactory.getDTDMapper();
			if (mapper != null) {
				InputStream stream = mapper.getStream(systemId);
				if (stream != null) {
					return new InputSource(stream);
				}
			}
			return new InputSource(systemId);
		}
	}

}
