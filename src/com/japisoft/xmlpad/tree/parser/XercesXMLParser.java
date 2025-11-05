// Editix XML Editor
// https://www.editix.com
// Copyright (c) 2025 Alexandre Brillant
// 
// For non-commercial usage :
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
// For commercial use or integration into proprietary software :
// A commercial license is required. Visit https://www.editix.com for details.

package com.japisoft.xmlpad.tree.parser;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.xerces.parsers.Locator3;
import org.xml.sax.Attributes;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.ext.DefaultHandler2;

import com.japisoft.framework.xml.parser.ErrorParsingListener;
import com.japisoft.framework.xml.parser.ParseException;
import com.japisoft.framework.xml.parser.document.Document;
import com.japisoft.framework.xml.parser.node.FPNode;
import com.japisoft.framework.xml.parser.node.MutableNode;
import com.japisoft.framework.xml.parser.node.NodeFactory;
import com.japisoft.framework.xml.parser.node.NodeFactoryImpl;
import com.japisoft.xmlpad.IXMLPanel;

public class XercesXMLParser extends DefaultHandler2 implements Parser, ErrorHandler {
	
	private Locator locator;
	
	public XercesXMLParser() {
	}
	
	public void setDocumentLocator(org.xml.sax.Locator locator) {
		this.locator = locator;
	}
	
	private boolean interrupt = false;
	
	@Override
	public boolean isInterrupted() {
		return interrupt;
	}

	@Override
	public void interruptParsing() {
		interrupt = true;
	}

	private boolean flatViewMode = false;
	
	@Override
	public void setFlatView(boolean b) {
		flatViewMode = b;
	}

	private boolean lightweightMode = false;
	
	// For avoiding parsing with text update
	@Override
	public void setLightweightMode(boolean b) {
		this.lightweightMode = b;
	}
	
	public boolean isLightweightMode() {
		return this.lightweightMode;
	}
	
	@Override
	public void setBackgroundMode(boolean b) {
	}

	@Override
	public void setParsingMode(int continueParsingMode) {
	}

	private ErrorParsingListener errorListener = null;
	
	@Override
	public void setErrorSignal(ErrorParsingListener parsingErrorListener) {
		this.errorListener = parsingErrorListener;
	}

	private NodeFactory nf;

	/** Update the factory for building node */
	public void setNodeFactory(NodeFactory nf) {
		this.nf = nf;
	}

	/** @return the factory for building node */
	public NodeFactory getNodeFactory() {
		if ( nf == null )
			nf = NodeFactoryImpl.getFactory();
		return nf;
	}

	private Document doc = null;
	private Stack<MutableNode> stacks = null;
	private List<FPNode> flatNodes = null;
	
	private static SAXParserFactory factory = null;
		
	public Document parse(InputStream reader, Object context ) throws ParseException {
		return parse( new InputSource( reader ), context );
	}
	
	@Override	
	public Document parse(Reader reader, Object context ) throws ParseException {
		return parse( new InputSource( reader ), context );		
	}

	private Document parse(InputSource reader, Object context ) throws ParseException {
		try {
			if ( factory == null ) {
				factory = SAXParserFactory.newInstance();
				factory.setNamespaceAware( true );	
				factory.setXIncludeAware( false );
				factory.setValidating( false );
				factory.setFeature( "http://xml.org/sax/features/validation", false );
				factory.setFeature( "http://apache.org/xml/features/nonvalidating/load-external-dtd", false );
			}

			if ( lightweightMode ) {
				lightweightIndex = 0;
			}
			
			String currentPath = null;
			
			if ( context instanceof IXMLPanel ) {
				IXMLPanel panel = ( IXMLPanel )context;
				currentPath = panel.getMainContainer().getCurrentDocumentLocation();
				if ( panel.getMainContainer().getRootNode() != null ) {
					List<FPNode> tmp = panel.getMainContainer().getRootNode().getDocument().getFlatNodes();
					if ( lightweightMode ) {
						if ( tmp != null ) 
							flatNodes = tmp;
						else
							lightweightIndex = -1;
					}
				} else
					lightweightIndex = -1;
			}
			
			doc = new Document();
			if ( flatViewMode ) {
				if ( flatNodes == null )
					flatNodes = new ArrayList<FPNode>();
				doc.setFlatNode( flatNodes );
			}
			
			stacks = null;
			error = false;
			
			try {
				SAXParser parser = factory.newSAXParser();
				XMLReader saxreader = parser.getXMLReader();
				saxreader.setErrorHandler( this );
				saxreader.setContentHandler( this );
				
				InputSource is = reader;
				if ( currentPath != null )
					is.setSystemId( currentPath );
				
				saxreader.parse( is );
				
				
				// parser.parse( is, this );
			} finally {
				stacks = null;
				flatNodes = null;
				lightweightIndex = 0;
				error = false;
			}
			
			try {
				FPNode node = (FPNode)doc.getRoot();
				if ( node.getStartingOffset() == -1 ) {
					// issue with the parser, try to find it
					Reader res = reader.getCharacterStream();
					if ( res instanceof StringReader ) {
						StringReader sr = ( StringReader )res;
						String source = sr.toString();
						node.setStartingOffset( source.indexOf( "<" + node.getContent() ) );
					}
				}
			} catch( Throwable th ) {}
			
			return doc;
		} catch( ParserConfigurationException exc ) {
			throw new ParseException( exc.getMessage() );
		} catch( SAXException exc ) {
			error = true;
			// exc.printStackTrace();
			// if ( errorListener != null )
			//	errorListener.parsingError( exc.getMessage(), getOffset(), getLine(), getColumn() );
			throw new ParseException( exc.getMessage(), getOffset(), getLine() );
		} catch( IOException exc ) {
			return null;	// Memory buffer => Impossible
		}
	}

	private boolean error = false;
	
	@Override
	public boolean hasError() {
		return error;
	}
	
	/////////////////////////////////////////////////// SAX ////////////////////////////////////////////

	private int lightweightIndex = 0;
	
	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		if ( interrupt )
			return;
		super.startElement(uri, localName, qName, attributes);
		
		FPNode tag = null;
		
		if ( lightweightMode && lightweightIndex != -1 && flatNodes != null && flatNodes.size() > 0 ) {

			tag = flatNodes.get( lightweightIndex++ );
			if ( stacks == null ) {
				stacks = new Stack<MutableNode>();			
			}
			
			if ( doc.getRoot() == null )
				doc.setRoot( tag );
			
		} else {
		
			MutableNode parent = null;			
			tag = getNodeFactory().getTagNode( localName != null ? localName : qName );
			
						
			if ( flatNodes != null )
				flatNodes.add( tag );
			
			if ( stacks == null ) {
				stacks = new Stack<MutableNode>();			
			} else {
				parent = stacks.peek();
			}
			
			if ( parent == null )
				doc.setRoot( tag );
			else
				parent.addNode( tag );	

			if ( uri != null && !"".equals( uri ) ) {
				tag.setDefaultNamespace( uri );
				if ( qName != null ) {
					int i = qName.indexOf( ":" );
					if ( i > -1 ) {
						String prefix = qName.substring( 0, i );
						if ( tag instanceof FPNode ) {
							((FPNode)tag).setNameSpacePrefix( prefix );
						}
						
						if ( tag.isRoot() ) {
							FPNode root = ( FPNode )doc.getRoot();
							root.addNameSpaceDeclaration( prefix, uri );
							root.setNameSpace( prefix, uri );							
						}
					}
				}
			}
			
		}
			
		tag.setDocument( doc );
		
		if ( attributes != null && attributes.getLength() > 0 ) {
			FPNode n = ( FPNode )tag;
			n.removeAllAttributes();
			
			// Store attributes
			for ( int i = 0; i < attributes.getLength(); i++ )
				( ( FPNode )tag ).setAttribute( attributes.getQName( i ), attributes.getValue( i ) );
		}
		
		stacks.push( tag );
		
		if ( locator != null ) {
			tag.setStartingLine( locator.getLineNumber() );
			if ( locator instanceof Locator3 ) {
				tag.setStartingOffset( ( (Locator3)locator ).getLastStartingPart() - 1 );
			}
		}
	}
	
	private int getOffset() {
		if ( locator instanceof Locator3 ) {
			return ( ( Locator3 )locator ).getCharacterOffset();
		}
		return 0;
	}
	
	private int getLine() {
		if ( locator != null )
			return locator.getLineNumber();
		return 0;
	}
	
	private int getColumn() {
		if ( locator != null )
			return locator.getColumnNumber();
		return 0;
	}
	
	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		if ( interrupt )
			return;
		super.endElement(uri, localName, qName);

		MutableNode node = stacks.pop();
		node.setStoppingLine( locator.getLineNumber() );

		if ( locator instanceof Locator3 ) {
			node.setStoppingOffset( ( (Locator3)locator ).getCharacterOffset() - 1 );
		}
	}
	
	@Override
    public void error (SAXParseException exc )
            throws SAXException {
    	error = true;
    	if ( errorListener != null )
    		errorListener.parsingError( exc.getMessage(), getOffset(), getLine(), getColumn() );    	
    	
    }
    
	@Override
	public void fatalError(SAXParseException exc) throws SAXException {
    	error = true;
    	if ( errorListener != null ) {
    		errorListener.parsingError( exc.getMessage(), getOffset(), getLine(), getColumn() );
    	}
	
	}

}

