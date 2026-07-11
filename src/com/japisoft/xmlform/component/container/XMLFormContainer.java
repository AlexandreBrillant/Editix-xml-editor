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

package com.japisoft.xmlform.component.container;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.KeyStroke;
import javax.swing.border.LineBorder;

import com.japisoft.xmlform.component.AbstractXMLFormComponent;
import com.japisoft.xmlform.component.ComponentContext;
import com.japisoft.xmlform.component.editable.XMLEditableComponent;
import com.japisoft.xmlform.designer.properties.PropertyDescriptor;
import com.japisoft.xmlform.designer.properties.PropertyDescriptorImpl;

public class XMLFormContainer extends 
		AbstractXMLFormComponent implements Action {

	private GridComponent gc = null;
	
	public XMLFormContainer( 
			boolean designMode, 
			ComponentContext context ) {
		super( designMode, context );
		super.add( 
				gc = 
					new GridComponent( 
							designMode, 
							context ) 
		);
		setPreferredSize( 
				new Dimension( 200, 200 ) );
		if ( !designMode ) {
			setBorder( 
					new LineBorder( 
							Color.GRAY ) );
		} else {
			getActionMap().put( 
					"delete", 
					this );
			getInputMap( JComponent.WHEN_IN_FOCUSED_WINDOW ).put( 
					KeyStroke.getKeyStroke( KeyEvent.VK_DELETE, 0 ) , "delete" );
		}
	}

	private String schemaURI = null;

	public void setSchemaURI( String schema ) {
		this.schemaURI = schema;
	}

	public String getSchemaURI() { return schemaURI; }

	private String formURI = null;
	
	public void setFormTemplateURI( String path ) {
		this.formURI = path;
	}

	public String getFormTemplateURI() {
		return formURI;
	}

	@Override
	public Container getComponentContainer() {
		return gc;
	}
	
	@Override
	public Component add(Component comp) {
		// Switch to the grid
		return gc.add( comp );
	}

	@Override
	public void addNotify() {
		super.addNotify();
		if ( designMode ) {
			gc.addMouseListener( this );
			gc.addMouseMotionListener( this );
		}
	}

	@Override
	public void removeNotify() {
		super.removeNotify();
		gc.removeMouseListener( this );
		gc.removeMouseMotionListener( this );
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		super.actionPerformed( e );
		if ( "fc:delete".equals( e.getActionCommand() ) )
			context.action( ComponentContext.DELETE_ACTION, null );
	}

	public Object getValue(String key) {
		if ( Action.ACTION_COMMAND_KEY.equals( key ) )
			return "fc:delete";
		return null;
	}

	private void setTitleForeground( Color fg ) {
		if ( label == null ) {
			if ( title != null ) {
				updateLabel( title );
			}
		}
		if ( label != null ) {
			label.setForeground( fg );
		}
	}

	@Override
	public void setForeground(Color fg) {
		firePropertyChange( 
				"foreground", 
				gc.getForeground(), 
				fg );		
		gc.setForeground( fg );
		setTitleForeground( fg );		
	}

	@Override
	public Color getForeground() {
		return gc.getForeground();
	}
	
	@Override
	public void setBackground(Color bg) {
		firePropertyChange( 
				"background", 
				gc.getBackground(), 
				bg );
		gc.setBackground( bg );
	}

	private void setTitleFont( Font f ) {
		if ( label == null ) {
			if ( title != null ) {
				updateLabel( title );
			}
		}
		if ( label != null ) {
			label.setFont( f );
		}
	}

	public void setFont( Font f ) {
		firePropertyChange( 
				"font", 
				gc.getFont(), 
				f );		
		gc.setFont( f );
		setTitleFont( f );
	}

	public Font getFont() {
		return gc.getFont();
	}

	@Override
	public Color getBackground() {
		return gc.getBackground();
	}

	private String title = null;
	
	public void setTitle( String title ) {
		firePropertyChange( 
				"title", 
				this.title, 
				title );
		this.title = title;
		updateLabel( resolveTitle() );
	}

	@Override
	protected String resolveTitle() {
		if ( title == null )
			return super.resolveTitle();
		if ( !designMode ) 
			return " " + title;
		else {
			return "<html><body> title : " + title + " - <i> xpath : " + xpath + "</i> </body></html>";  
		}
	}	

	public String getTitle() {
		return title;
	}

	public void putValue( String key, Object value ) {
	}

	@Override
	public void requestFocus() {
		Component c = getNearest( gc, null );
		if ( ( c != null ) && ( c instanceof AbstractXMLFormComponent ) ) {
			( ( AbstractXMLFormComponent )c ).requestFocus();
		}
	}

	public XMLEditableComponent getFirstEditableComponent() {
		int minY = 
			Integer.MAX_VALUE;
		AbstractXMLFormComponent result = null;

		for ( int i = 0; i < gc.getComponentCount(); i++ ) {
			Component c = gc.getComponent( i );

			if ( c instanceof XMLFormContainer || 
					c instanceof XMLEditableComponent ) {
				if ( c.getY() < minY ) {
					result = ( AbstractXMLFormComponent )c;
					minY = c.getY();
				}
			}
		}

		if ( result instanceof XMLEditableComponent ) {
			return ( XMLEditableComponent )result;
		} else
			if ( result instanceof XMLFormContainer ) {
				return ( ( XMLFormContainer )result ).getFirstEditableComponent();
			} else
				return null;
	}

	@Override
	protected void prepareProperties( ArrayList<PropertyDescriptor> l ) throws Exception {
		super.prepareProperties( l );
		l.add(
				new PropertyDescriptorImpl( 
						"title", 
						String.class, 
						this ) );
	}

	public void cut() {
		context.action( ComponentContext.DELETE_ACTION, null );
	}

}
