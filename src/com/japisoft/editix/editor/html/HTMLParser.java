// Editix XML Editor
// https://www.editix.com
// Copyright (c) 2026 Alexandre Brillant
//
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
// AI Training Restriction :
// This source code is provided for human use only.
// Using this code to train, fine-tune, or develop AI models,
// machine learning systems, or similar technologies is
// STRICTLY PROHIBITED. Violations will terminate all rights
// under the applicable license.

package com.japisoft.editix.editor.html;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.jsoup.nodes.Attributes;
import org.jsoup.nodes.Element;
import org.jsoup.parser.HtmlTreeBuilder;
import org.jsoup.parser.ParseError;
import org.jsoup.parser.ParseErrorList;
import org.jsoup.parser.Token;
import org.jsoup.select.Elements;

import com.japisoft.framework.collection.FastArrayList;
import com.japisoft.framework.xml.parser.ErrorParsingListener;
import com.japisoft.framework.xml.parser.ParseException;
import com.japisoft.framework.xml.parser.document.Document;

import com.japisoft.framework.xml.parser.node.FPNode;
import com.japisoft.framework.xml.parser.node.MutableNode;
import com.japisoft.framework.xml.parser.node.NodeFactory;
import com.japisoft.xmlpad.tree.parser.Parser;

public class HTMLParser implements Parser {

	private Document doc = null;
	
	public HTMLParser() {

	}
	
	public Document getDocument() {
		return doc;
	}

	private boolean errorFound = false;

	public boolean hasError() {
		return errorFound;
	}

	public void interruptParsing() {
	}

	public boolean isInterrupted() {
		return false;
	}
	
	@Override
	public void setLightweightMode(boolean b) {
	}
	@Override
	public boolean isLightweightMode() {
		return false;
	}
	
	public Document parse(Reader reader, Object context ) throws ParseException {
		ParseErrorList pel = null;
		if ( errorMode )
			pel = new ParseErrorList();		
		try {
			String html = IOUtils.toString(reader);
			org.jsoup.nodes.Document soupDoc = org.jsoup.parser.Parser.parse( html, new CustomTreeBuilder(), pel );
			doc = new JSoupToJAPISOFT( soupDoc );
			if ( pel != null ) {
				if ( errorListener != null ) {
					for ( int i = 0; i < pel.size(); i++ ) {
						ParseError pe = pel.get( i );
						errorListener.parsingError( pe.getErrorMessage(), pe.getPosition(), pe.getLine() + 1, 0 );
					}
				} else {
					throw new ParseException( "Error(s) found" );
				}
			}
			return doc;
		} catch( IOException exc ) {
			throw new ParseException( exc.getMessage() );
		}
	}

	public void setBackgroundMode(boolean b) {
	}

	private String tmpContent;
	
	public void setContent(char[] charArray) {
		this.tmpContent = new String( charArray );
	}

	private ErrorParsingListener errorListener;

	private boolean errorMode = false;
	
	public void setErrorSignal(ErrorParsingListener parsingErrorListener) {
		this.errorListener = parsingErrorListener;
		if ( parsingErrorListener != null )
			errorMode = true;
	}

	public void setFlatView(boolean b) {
	}

	private NodeFactory factory;
	
	public void setNodeFactory(NodeFactory factory) {
		this.factory = factory;
	}

	public void setParsingMode(int continueParsingMode) {
	}

	// ----------------------------------------------------------------------------

	class JSoupToJAPISOFT extends Document {
		private FPNode root = null;

		public JSoupToJAPISOFT( org.jsoup.nodes.Document jsdoc ) {
			org.jsoup.nodes.Element e = jsdoc.child( 0 );
			root = new FPNode( FPNode.TAG_NODE, e.nodeName() );	
			synchroLocation( e, root );
			synchro( root, e );
			setRoot(root);
			setFlatView(true);
			ArrayList<FPNode> fv = new ArrayList<FPNode>();
			Elements descendants = e.getAllElements();
			for ( Element ee: descendants ) {
				if ( ee.getUserApp() != null ) {
					fv.add( (FPNode) ee.getUserApp() );
					ee.setUserApp( null );
				}
			}
			setFlatNode(fv);
		}

		private void synchroLocation( org.jsoup.nodes.Node child, FPNode nn ) {
			child.setUserApp( nn );
			nn.setStartingOffset( child.getStart() );
			nn.setStoppingOffset( child.getStop() );
			nn.setStartingLine( child.getLine() );
			if ( child instanceof org.jsoup.nodes.Element ) {
				org.jsoup.nodes.Element e = ( org.jsoup.nodes.Element )child;
				nn.setStoppingOffset( e.getStop2() );
				if ( e.getStop2() == 0 ) {	// Auto close
					nn.setStartingOffset( child.getStop() );
				}
			}
			nn.setDocument( this );
		}

		private void synchro( FPNode sn, org.jsoup.nodes.Node n ) {			
			for ( int i = 0; i < n.childNodeSize(); i++ ) {
				org.jsoup.nodes.Node child = n.childNode( i );
				FPNode nn = new FPNode( FPNode.TAG_NODE, null );
				synchroLocation( child, nn );
				sn.appendChild( nn );

				if ( child instanceof org.jsoup.nodes.TextNode ) {
					nn.setType( FPNode.TEXT_NODE );
					nn.setContent( ( ( org.jsoup.nodes.TextNode )child ).getWholeText() );
				} else {
					if ( child instanceof org.jsoup.nodes.Element ) {						
						nn.setNodeContent( child.nodeName() );
						Attributes atts = child.attributes();
						if ( atts != null ) {
							Iterator<org.jsoup.nodes.Attribute> itt = atts.iterator();
							while ( itt.hasNext() ) {
								org.jsoup.nodes.Attribute att = itt.next();
								nn.setAttribute( att.getKey(), att.getValue() );
							}
						}
						synchro( nn, child );
					}
				}
			}
		}

		@Override
		public MutableNode getRoot() {
			return super.getRoot();
		}
	}

	class CustomTreeBuilder extends HtmlTreeBuilder {
		
		@Override
		protected void insert(Token.Character characterToken) {
			if ( !characterToken.isEmpty() )
				super.insert(characterToken);
		}
		
		
	}

}
