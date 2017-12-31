package com.japisoft.editix.editor.xsd.view;

import javax.swing.JComponent;
import javax.swing.JTextArea;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

import org.w3c.dom.Element;
import org.w3c.dom.Text;

import com.japisoft.editix.editor.xsd.toolkit.SchemaHelper;

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
public class AnnotationViewImpl extends JTextArea implements View {
	private Element initE;

	public AnnotationViewImpl() {
		setDocument( new CustomPlainDocument() );
	}

	public void init( Element schemaNode ) {
		if ( schemaNode != null )
			if ( "annotation".equals( schemaNode.getLocalName() ) )
				schemaNode = ( Element )schemaNode.getParentNode();

		this.initE = schemaNode;
		setEnabled( initE != null );
		if ( initE != null ) {
			Element annotation = SchemaHelper.getChildAt( 
					schemaNode, 
					0, 
					new String[] { "annotation" } );
			if ( annotation != null ) {
				Element documentation = SchemaHelper.getChildAt( annotation, 0, new String[] { "documentation" } );
				if ( documentation != null ) {
					setText( SchemaHelper.getTexts( documentation ) );
				} else
					setText( null );
			} else
				setText( null );
		}
	}

	public JComponent getView() {
		return this;
	}

	public void dispose() {
		initE = null;
	}	

	public void stopEditing() {}

	class CustomPlainDocument extends PlainDocument {
		public void insertString(int offs, String str, AttributeSet a) 
				throws BadLocationException {
			super.insertString(offs, str, a);
			resetTexts();
		}
		public void remove(int offs, int len) 
				throws BadLocationException {
			super.remove(offs, len);
			if ( getLength() == 0 ) {
				// Remove documentation node
				Element annotation = SchemaHelper.getChildAt( initE, 0, new String[] { "annotation" } );
				if ( annotation != null ) {
					Element documentation = SchemaHelper.getChildAt( annotation, 0, new String[] { "documentation" } );
					if ( documentation != null ) {
						annotation.removeChild( documentation );
						if ( !SchemaHelper.hasDOMElementChild( annotation ) ) {
							initE.removeChild( annotation );
						}
					}
				}
			} else
				resetTexts();
		}
		private void resetTexts() {
			Element annotation = SchemaHelper.getChildAt( initE, 0, new String[] { "annotation" } );
			if ( annotation == null ) {
				annotation = SchemaHelper.createTag( initE, "annotation" );
				if ( initE.hasChildNodes() ) {
					initE.insertBefore( annotation, initE.getChildNodes().item( 0 ) );
				} else
					initE.appendChild( annotation );
				Element documentation = SchemaHelper.createTag( initE, "documentation" );
				Text t = initE.getOwnerDocument().createTextNode( AnnotationViewImpl.this.getText() );
				documentation.appendChild( t );
				annotation.appendChild( documentation );
			} else {
				Element documentation = SchemaHelper.getChildAt( 
						annotation, 
						0, 
						new String[] { "documentation" } );
				if ( documentation == null ) {
					documentation = SchemaHelper.createTag( initE, "documentation" );
					Text t = initE.getOwnerDocument().createTextNode( AnnotationViewImpl.this.getText() );
					documentation.appendChild( t );
					annotation.appendChild( documentation );					
				} else {
					// Update text
					SchemaHelper.removeChildren( documentation );
					Text t = initE.getOwnerDocument().createTextNode( AnnotationViewImpl.this.getText() );
					documentation.appendChild( t );					
				}
			}
		}
	}

}
