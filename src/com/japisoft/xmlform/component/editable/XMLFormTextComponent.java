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

package com.japisoft.xmlform.component.editable;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.util.ArrayList;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.LineBorder;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.JTextComponent;
import javax.swing.text.PlainDocument;

import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.Text;

import com.japisoft.framework.ApplicationModel;
import com.japisoft.xmlform.component.ComponentContext;
import com.japisoft.xmlform.component.container.GridComponent;
import com.japisoft.xmlform.designer.properties.PropertyDescriptor;
import com.japisoft.xmlform.designer.properties.PropertyDescriptorImpl;

public class XMLFormTextComponent extends XMLEditableComponent {

	private JComponent tf = null;
	private boolean multiLine = false;
	private boolean scrollable = false;
	
	public XMLFormTextComponent( 
			boolean designMode, 
			ComponentContext context ) {	
		this( designMode, context, false );
	}

	public XMLFormTextComponent( 
			boolean designMode, 
			ComponentContext context, 
			boolean multiLine ) {
		super( designMode, context );
		setMultiLine( multiLine );
		
		if ( designMode ) {
			prepareDesignUI();
		}
	}

	private void prepareDesignUI() {
		if ( tf == null ) {
			tf = new JLabel();
			tf.setBackground( Color.WHITE );
			tf.setForeground( Color.BLACK );
			tf.setOpaque( true );
			((JLabel)tf).setVerticalAlignment( JLabel.TOP );
			add( tf, BorderLayout.CENTER );
			setPreferredSize( new Dimension( 
					200, 
					2 * GridComponent.getGridSize() ) );
		}
	}

	protected void updateLabel( String xpath ) {
		if ( designMode ) {
			prepareDesignUI();
			((JLabel)tf).setText( xpath );
		}
	}
	
	public boolean getMultiLine() {
		return multiLine;
	}

	public void setMultiLine( boolean newMultiLineMode ) {
		firePropertyChange( 
			"multiLine", 
			multiLine, 
			newMultiLineMode );
		this.multiLine = newMultiLineMode;		
	}	

	public boolean getScrollable() {
		return scrollable;
	}
	
	public void setScrollable( boolean scrollable ) {
		firePropertyChange( 
			"scrollable", 
			this.scrollable, 
			scrollable );
		this.scrollable = scrollable;
	}

	private void addEditableComponent() {
		if ( !multiLine && 
				!scrollable )
			tf = new JTextField();
		else {
			tf = new JTextArea();
			//if ( !scrollable ) {
			( ( JTextArea )tf ).setLineWrap( true );
			( ( JTextArea )tf ).setWrapStyleWord( true );
			if ( !scrollable )
				tf.setBorder( new LineBorder( Color.GRAY ) );
			//}
		}

		( ( JTextComponent)tf ).setDocument( 
			new CustomPlainDocument() );

		if ( customBackground != null )
			tf.setBackground( customBackground );
		if ( customForeground != null )
			tf.setForeground( customForeground );
		if ( customFont != null )
			tf.setFont( customFont );

		setRequired( 
			getRequired() );

		if ( scrollable ) {
			add( new JScrollPane( tf ), BorderLayout.CENTER );
		} else
			add( tf, BorderLayout.CENTER );

		tf.setToolTipText( tooltip );		
	}

	@Override
	protected boolean canBeResizedVertically() {
		if ( tf instanceof JTextField )
			return false;
		return super.canBeResizedVertically();
	}
	
	@Override
	public void addNotify() {
		super.addNotify();
		if ( designMode ) {
			tf.addMouseListener( this );
			tf.addMouseMotionListener( this );
		} else {
			addEditableComponent();
			invalidate();
			validate();		
		}
	}

	@Override
	public void removeNotify() {
		super.removeNotify();
		if ( designMode ) {
			tf.removeMouseListener( this );
			tf.removeMouseMotionListener( this );
		}
	}

	@Override
	protected void setDOM( Node newDom ) {
		if ( newDom instanceof Text ) {
			newDom = newDom.getParentNode();
		}
		super.setDOM( newDom );
		domText = null;
		Node t = getDOMText();
		// For avoiding to erase to the first caracter
		if ( "".equals( ( (JTextComponent)tf ).getText() ) )
			( ( JTextComponent )tf ).setText( t.getNodeValue() );
	}

	private Font customFont = null;

	@Override
	public void setFont(Font font) {
		firePropertyChange( "font", customFont, font );
		this.customFont = font;
	}

	@Override
	public Font getFont() {
		return customFont;
	}

	@Override
	public void setRequired( boolean required ) {
		super.setRequired( required );
		if ( tf != null ) {
			if ( required ) {
				tf.setBackground( 
					getColorForRequiredField() );
			} else {
				if ( customBackground != null )
					tf.setBackground( 
						customBackground 
					);
				else
					tf.setBackground( 
						Color.WHITE 
					);
			}
		}
	}

	private Color customBackground = null;

	@Override
	public void setBackground(Color bg) {
		firePropertyChange( "background", customBackground, bg );
		this.customBackground = bg;		
		if ( !getRequired() && designMode )
			tf.setBackground( bg );
	}

	@Override
	public Color getBackground() {
		return customBackground;
	}	
	
	private Color customForeground = null;

	@Override
	public void setForeground(Color fg) {
		firePropertyChange( "foreground", customForeground, fg );
		this.customForeground = fg;
	}

	@Override
	public Color getForeground() {
		return customForeground;
	}
	
	@Override
	public void requestFocus() {
		if ( tf != null )
			tf.requestFocus();
	}

	public void cut() {
		if ( tf != null )
			( ( JTextComponent )tf ).cut();
	}
	
	public void copy() {
		if ( tf != null )
			( ( JTextComponent )tf ).copy();		
	}
	
	public void paste() {
		if ( tf != null )
			( ( JTextComponent )tf ).paste();
	}
	
	public void selectAll() {
		if ( tf != null )
			( ( JTextComponent )tf ).selectAll();
	}

	@Override
	protected void prepareProperties( ArrayList<PropertyDescriptor> l )
		throws Exception {
		super.prepareProperties(l);
		l.add( 
			new PropertyDescriptorImpl( 
					"multiLine", 
					Boolean.class, 
					this ) );
		l.add( 
			new PropertyDescriptorImpl( 
					"scrollable", 
					Boolean.class, 
					this ) );		
	}

	private void deleteCurrentNode() {
		Node t = getDOMText();
		if ( t instanceof Attr ) {
			Element owner = ( ( Attr )t ).getOwnerElement();
			owner.removeAttributeNode( ( Attr )t );
			domText = null;
		} else
		if ( t instanceof Text ) {
			Element p = ( Element )t.getParentNode();
			// Remove only if the parent is empty
			if ( !p.hasAttributes() ) {
				if ( p.getParentNode() instanceof Element ) {
					Element pp = ( Element )p.getParentNode();
					pp.removeChild( p );

					dom = null;
				}
			}
			p.removeChild( t );
			domText = null;
		}
	}

	class CustomPlainDocument extends PlainDocument {

		private void updateDomText() {
			Node t = getDOMText();
			try {
				String s = 
					getText( 0, getLength() );

				boolean empty = 
					( s == null ) || 
						"".equals( s );

				if ( getRequired() ) {
					if ( empty ) {
						tf.setBackground( 
							getColorForRequiredField() );
					} else {
						if ( customBackground != null )
							tf.setBackground(
									customBackground );
						else
							tf.setBackground( Color.WHITE );
					}
				}

				if ( !empty ) {
					t.setNodeValue( 
						getText( 0, getLength() ) );
				} else {
					deleteCurrentNode();
				}
			} catch (DOMException e) {
				ApplicationModel.debug( e );
			} catch (BadLocationException e) {
				ApplicationModel.debug( e );
			}
		}

		@Override
		public void insertString( int offs, String str, AttributeSet a )
				throws BadLocationException {
			super.insertString(offs, str, a);
			updateDomText();
		}
		@Override
		public void remove( int offs, int len ) throws BadLocationException {
			super.remove(offs, len);
			updateDomText();
		}
	}

}
