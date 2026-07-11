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

package com.japisoft.stylededitor.model;

import java.awt.Dimension;
import java.awt.Point;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.UndoableEditListener;
import javax.swing.event.DocumentEvent.EventType;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.Position;
import javax.swing.text.Segment;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.ProcessingInstruction;
import org.w3c.dom.Text;

import com.japisoft.framework.css.Parser;
import com.japisoft.framework.css.ParserFactory;
import com.japisoft.framework.css.Rule;

public class XMLDocument implements Document {

	private Element root = null;
	private org.w3c.dom.Document domDocument = null;
	private com.japisoft.framework.css.CSSDocument cssRules;
	
	public XMLDocument( 
			String baseuri, 
			org.w3c.dom.Document document ) throws Exception {
		updateCSSFile( 
			baseuri, 
			document 
		);		
		updateContent( 
			document 
		);
	}

	public org.w3c.dom.Document getDOMDocument() {
		return domDocument;
	}

	private void updateCSSFile( String baseuri, org.w3c.dom.Document document ) throws Exception {
		NodeList nl = document.getChildNodes();
		for ( int i = 0; i < nl.getLength(); i++ ) {
			Node n = nl.item( i );
			if ( n instanceof ProcessingInstruction ) {
				ProcessingInstruction pi = ( ProcessingInstruction )n;
				if ( "xml-stylesheet".equalsIgnoreCase( pi.getTarget() ) ) {
					String data = pi.getData();
					int j = data.indexOf( "href=" );
					if( j > -1 ) {
						int k1 = data.indexOf( "\"", j );
						if ( k1 == -1 )
							k1 = data.indexOf( "'", j );
						int k2 = data.indexOf( "\"", k1 + 1 );
						if ( k2 == -1 )
							k2 = data.indexOf( "'", k1 + 1 );						
						if ( k2 != -1 ) {
							String uri = data.substring( k1 + 1, k2 );
							Parser p = ParserFactory.getInstance().newParser();
							cssRules = p.parse( baseuri, uri );
						}
					}
				}
			}
		}
	}

	private void updateContent( org.w3c.dom.Document document ) {
		this.domDocument = document;
		root = new DocumentElement( this, document );
		// Check for CSS binding
		if ( cssRules != null ) {
			for ( int i = 0; i < root.getElementCount(); i++ ) {
				if ( root.getElement( i ) instanceof NodeElement ) {
					NodeElement ne = ( NodeElement )root.getElement( i );
					if ( ne.getNode() instanceof org.w3c.dom.Element ) {
						org.w3c.dom.Element e = ( org.w3c.dom.Element )ne.getNode();
						Rule r = cssRules.matchElement( e );
						if ( r != null ) {
							e.setUserData( "css", r, null );
						}
					}
				}
			}
		}
	}

	public void updateContent() {
		updateContent( this.domDocument );
	}

	private List<DocumentListener> listeners = null;

	public void addDocumentListener( DocumentListener listener ) {
		if ( listeners == null )
			listeners = new ArrayList<DocumentListener>();
		listeners.add( listener );
	}

	public void removeDocumentListener( DocumentListener listener ) {
		if ( listeners != null )
			listeners.remove( listener );
	}

	private void fireDocumentInsert( int offset, String str ) {
		updateContent();
		if ( listeners != null ) {
			DocumentEvent de = new SimpleDocumentEvent( 
					EventType.INSERT, 
					offset, 
					str 
			);
			for ( DocumentListener dl : listeners ) {
				dl.insertUpdate( de );
			}
		}
		// dump();
	}
	
	private void fireDocumentRemove( int offset, String str ) {
		DocumentEvent de = null;
		if ( listeners != null ) {
			de = new SimpleDocumentEvent( 
					EventType.REMOVE, 
					offset, 
					str 
			);			
		}
		updateContent();
		if ( listeners != null ) {
			for ( DocumentListener dl : listeners ) {
				dl.removeUpdate( de );
			}
		}
		// dump();
	}
	
	public void addUndoableEditListener( UndoableEditListener listener ) {
	}

	public Position createPosition( int offs ) throws BadLocationException {
		return null;
	}

	public Element getDefaultRootElement() {
		return root;
	}

	public Position getEndPosition() {
		return null;
	}

	public Node getDOMAt( int x, int y ) {

		NodeList nl = domDocument.getElementsByTagName( "*" );

		Node res = null;
		
		for ( int i = 0; i < nl.getLength(); i++ ) {
			org.w3c.dom.Element e = ( org.w3c.dom.Element )nl.item( i );
			
			
			
			Point p = ( Point )e.getUserData( "location" );
			if ( p == null )
				continue;
			Dimension size = ( Dimension )e.getUserData( "size" );
			if ( size == null )
				continue;
			if ( x >= p.x && x <= p.x + size.width && y >= p.y && y <= p.y + size.height ) {
				res = e;
			}
		}

		return res;
	}

	public int getLength() {
		return root.getEndOffset();
	}

	public Object getProperty( Object key ) {
		return null;
	}

	public Element[] getRootElements() {
		return new Element[] { root };
	}

	public Position getStartPosition() {
		return null;
	}

	public String getText( int offset, int length ) throws BadLocationException {
		StringBuffer tmp = new StringBuffer();
		Element root = getDefaultRootElement();
		BasicNodeElement previousElement = null;
		for ( int i = offset; i < ( offset + length ); i++ ) {
			int index = root.getElementIndex( i );
			BasicNodeElement e = ( BasicNodeElement )root.getElement( index );
			int relPos = i - e.getStartOffset();

			char ch = 0;
			if ( relPos < e.getName().length() )
				ch = e.getName().charAt( relPos );
			
			if ( previousElement != null && previousElement != e ) {
				tmp.append( "\n" );
			}

			if ( ch > 0 )
				tmp.append( ch );

			previousElement = e;
		}

		return tmp.toString();
	} 

	public void getText( int offset, int length, Segment txt )
			throws BadLocationException {
	}

	public void insertString( 
		int offset, 
		String str, 
		AttributeSet a ) throws BadLocationException {
		int index = 
			getDefaultRootElement().getElementIndex( 
				offset );
		if ( index > -1 ) {
			Element element = getDefaultRootElement().getElement( index );
			// Update the current Text Node
			if ( element instanceof TextElement ) {
				TextElement te = ( TextElement )element;
				String text = te.getName();
				int relOffset = offset - te.getStartOffset();
				// if ( text.endsWith( "\n" ) ) {
				//	if ( relOffset == text.length() )
				//		relOffset--;
				// }
				if ( relOffset > 0 ) {
					String queue = text.substring( relOffset );
					text = text.substring( 0, relOffset ) + str;
					text = text + queue;
					
					if ( "\n".equals( str ) && "".equals( queue ) ) {	// Must force a new line
						text = text + "\n";
					}
					
				} else
					text = str + text;
				te.getNode().setNodeValue( text );
				//if ( relOffset == text.length() - 1 && "\n".equals( str ) ) {
				//	Text tn = domDocument.createTextNode( str );
				//	te.getNode().getParentNode().appendChild( tn );					
				// }
			} else {
				// Create a new Text Node inside the NodeElement
				Text tn = domDocument.createTextNode( str );
				( ( NodeElement )element ).getNode().appendChild( tn );
			}						
			fireDocumentInsert( offset, str );
		} else {
			// ?
		}
	}

	public void putProperty( Object key, Object value ) {
	}

	public void remove( int offset, int length ) throws BadLocationException {
		
		StringBuffer deleteTrace = new StringBuffer();
		TextElement lte = null;
		int deleteFrom = 0;
		
		for ( int i = offset; i < ( offset + length ); i++ ) {
			int index = getDefaultRootElement().getElementIndex( i );
			TextElement te = ( TextElement )getDefaultRootElement().getElement( index );
			if ( te != lte ) {
				deleteFrom = i - te.getStartOffset();
			}
			deleteTrace.insert( 0, deleteFrom );
			te.deleteChar( deleteFrom );
			lte = te;
		}

		fireDocumentRemove(
			offset,
			deleteTrace.toString()
		);

	}

	public void removeUndoableEditListener( UndoableEditListener listener ) {
	}

	public void render( Runnable r ) {
	}

	public void dump() {
		System.out.println( "*********** DUMP ***********" );
		DocumentElement root = ( DocumentElement )getDefaultRootElement();
		dump( root );
	}
	
	public void dump( Element e ) {
		System.out.println( "-" + e.getName() + " start:" + e.getStartOffset() + " - end:" + e.getEndOffset() + " - name:[" + e.getName() + "]" );
		for ( int i = 0; i < e.getElementCount(); i++ ) {
			dump( 
				( Element )e.getElement( i ) 
			);
		}
	}
	
	class SimpleDocumentEvent implements DocumentEvent {

		private EventType type;
		private int offset;
		private String str;
		
		public SimpleDocumentEvent( EventType type, int offset, String str ) {
			this.type = type;
			this.offset = offset;
			this.str = str;
		}
		
		public ElementChange getChange(Element elem) {
			return null;
		}
		
		public Document getDocument() {
			return XMLDocument.this;
		}

		public int getLength() {
			return 0;
		}

		public int getOffset() {
			return offset;
		}

		public EventType getType() {
			return type;
		}

		@Override
		public String toString() {
			return str;
		}

	}
	
	public static void main( String[] args ) throws Exception {
		DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		File f = new File( "c:/travail/soft/EditorByCSS/data/Test2.xml" );
		org.w3c.dom.Document doc = db.parse( f );
		XMLDocument d = new XMLDocument( f.toURI().toString(), doc );
		d.insertString( 2, "\n", null );
		d.dump();
	}

}
