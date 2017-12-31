package com.japisoft.editix.plugin;

import java.io.StringReader;
import java.io.StringWriter;

import javax.swing.text.JTextComponent;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import com.japisoft.editix.action.file.SaveAction;
import com.japisoft.editix.ui.EditixFrame;
import com.japisoft.xmlpad.XMLContainer;
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
public class EditixDocument {

	private int index;

	EditixDocument( int index ) {
		this.index = index;
	}

	/**
	 * @return The document type (DTD, CSS, XML, XSD...) */
	public String getType() {
		XMLContainer c = getContainer();
		if ( c == null )
			return null;
		return c.getDocumentInfo().getType();
	}

	/**
	 * @return The document path */
	public String getLocation() {
		XMLContainer c = getContainer();
		if ( c == null )
			return null;
		return c.getCurrentDocumentLocation();
	}
	
	/** @return the caret position from 0 */
	public int getCaretLocation() {
		XMLContainer c = getContainer();
		if ( c == null )
			return 0;
		return c.getEditor().getCaretPosition();		
	}

	/**
	 * @param location Set a default location
	 */
	public void setLocation( String location ) {
		XMLContainer c = getContainer();
		if ( c == null )
			return;
		c.setCurrentDocumentLocation( location );
	}

	/**
	 * @return The document content as a simple text. <code>null</code> for no current document
	 */
	public String getTextContent() {
		XMLContainer c = getContainer();
		if ( c == null )
			return null;
		return c.getText(); 
	}

	/**
	 * Replace the text content by this one. 
	 * @param content New text content. */
	public void setTextContent( String content ) {
		XMLContainer c = getContainer();
		if ( c != null ) {
			c.setText( content );
		}
	}
	
	/** 
	 * @return The current selection */
	public String getTextSelection() {
		XMLContainer c = getContainer();
		if ( c == null )
			return null;
		return c.getEditor().getSelectedText();
	}

	/**
	 * Replace the selection by this content */
	public void replaceTextSelection( String content ) {
		XMLContainer c = getContainer();
		if ( c != null ) { 
			c.getEditor().replaceSelection( content );
		}
	}

	/** Insert a new content at this location */
	public void insertTextAt( int location, String content ) {
		try {
			XMLContainer c = getContainer();
			if ( c != null ) 
				c.getEditor().getDocument().insertString( location, content, null );
		} catch( Exception exc ) {
		}
	}

	/**
	 * @return The document content as a DOM object, <code>null</code> is returned for no current document 
	 * @throws Exception if the document can't be parsed to DOM
	 * */
	public Document getDOMContent() throws Exception {	
		String content = getTextContent();
		if ( content != null ) {
			DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();		
			return db.parse( new InputSource( new StringReader( content ) ) );
		} else
			return null;
	}

	/** 
	 * @param content The new document content as a DOM object
	 * @throws Exception if the document can't be serialized to a text
	 */
	public void setDomContent( Document content ) throws Exception {
		StringWriter sw = new StringWriter();
		Transformer t = TransformerFactory.newInstance().newTransformer();
		t.transform( new DOMSource( content ), new StreamResult( sw ) );
		setTextContent( sw.toString() );
	}

	/**
	 * @return the Swing editor used by editix, this is useful for particular UI effect */
	public JTextComponent getUIComponent() {
		XMLContainer c = getContainer();
		if ( c == null )
			return null;
		return c.getEditor();
	}

	private XMLContainer getContainer() {	
		return ( XMLContainer )EditixFrame.THIS.getXMLContainer( index );
	}

	/**
	 * Save the current document
	 * @return <code>true</code> if the operation is a success
	 */
	public boolean save() {
		XMLContainer c = getContainer();
		if ( c != null ) {
			SaveAction sa = new SaveAction();
			return sa.save( c );
		}
		return false;
	}

}
