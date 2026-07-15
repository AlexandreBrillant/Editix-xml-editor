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

package com.japisoft.xmlform.component;

import java.awt.Dimension;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JMenuItem;

import com.japisoft.framework.ApplicationModel;
import com.japisoft.framework.toolkit.Toolkit;
import com.japisoft.framework.xml.grammar.GrammarNode;
import com.japisoft.framework.xml.grammar.GrammarText;
import com.japisoft.framework.xml.grammar.GrammarType;
import com.japisoft.xmlform.component.container.XMLFormContainer;
import com.japisoft.xmlform.component.editable.XMLEnumComponent;
import com.japisoft.xmlform.component.editable.XMLFormTextComponent;
import com.japisoft.xmlform.designer.XmlFormModel;
import com.japisoft.xmlform.designer.data.GrammarNodeTreeNode;
import com.japisoft.xmlform.designer.library.ComponentDescriptor;
import com.japisoft.xmlform.designer.library.ComponentDescriptorModel;

public class XMLFormComponentFactory {

	private boolean editableMode = false;
	private ComponentContext context = null;
	
	public XMLFormComponentFactory( 
			boolean editableMode, 
			ComponentContext context ) {
		super();
		this.editableMode = editableMode;
		this.context = context;
	}

	public XMLFormContainer newRootContainer() {
		XMLFormContainer container =
			new XMLFormContainer( editableMode, context );
		container.setTopComponent( true );
		return container;
	}

	public AbstractXMLFormComponent newComponentFromTreeNode( GrammarNodeTreeNode nt ) {
		
		GrammarNode n = nt.getSource();

		GrammarType type = n.getType();
		
		if ( !"complex".equals( type.getType() ) ) {
				
			List<GrammarNode> values = type.getValues();
			if ( values != null ) {
				List<String> r = new ArrayList<String>();
				for ( GrammarNode gn : values ) {
					if ( gn instanceof GrammarText ) {
						r.add( ( ( GrammarText )gn ).getValue() );
					}
				}
				if ( r.size() > 0 ) {
					XMLEnumComponent c = 
						new XMLEnumComponent( true, context );
					String[] ss = new String[ r.size() ];
					for ( int i = 0; i < r.size(); i++ )
						ss[ i ] = r.get( i );
					c.setValues( ss );
					return c;
				}
			}

		}
		
		return newComponentFromTreeNode( nt.toXPath() );
	}

	public AbstractXMLFormComponent newComponentFromTreeNode( String xpath ) {

		AbstractXMLFormComponent component = null;
		
		if ( xpath.endsWith( "text()" ) || xpath.contains( "@" ) ) {
			boolean multiLine = xpath.endsWith( "text()" );
			component = new XMLFormTextComponent( true, context, multiLine );
		} else
			component = new XMLFormContainer( true, context );

		return component;
	}

	public AbstractXMLFormComponent newComponentFromDescriptor( String name ) {
		try {
			ComponentDescriptor cd = 
				ComponentDescriptorModel.getComponentDescriptor( name );
			if ( cd == null )
				return null;
			return cd.create( true, context );
		} catch (Exception e) {
			ApplicationModel.debug( e );
			return null;
		}
	}

	public JButton newAddDeleteComponent() {
		JButton b = 
			new JButton(
					Toolkit.getImageIcon( 
						"images/nav_down_blue.png" ) );
		b.setBorderPainted( false );
		b.setContentAreaFilled( false );
		b.setMargin( null );
		b.setPreferredSize( new Dimension( 10, 10 ) );
		return b;
	}

	public JMenuItem newAddMenuItem() {
		JMenuItem b = 
			new JMenuItem(
					"Add a new item",
					Toolkit.getImageIcon( 
						"images/element_add.png" ) );
		b.setActionCommand( "add" );
		return b;
	}

	public JMenuItem newDeleteMenuItem() {
		JMenuItem b = 
			new JMenuItem(
					"Delete this Item",
					Toolkit.getImageIcon( "images/element_delete.png" ) );
		b.setActionCommand( "delete" );
		return b;		
	}

	public AbstractXMLFormComponent newComponent( 
			String className, 
			HashMap<String,Object> properties, 
			boolean editing, 
			ComponentContext context ) throws Exception {
		
		AbstractXMLFormComponent component = null;
		if ( XMLFormContainer.class.getName().equals( className ) ) {
			component = new XMLFormContainer( editing, context );
		} else
		if ( XMLFormTextComponent.class.getName().equals( className ) ) {
			boolean multiLine = false;
			if ( properties != null ) {
				if ( properties.containsKey( "multiLine" ) )
					multiLine = ( Boolean )properties.get( "multiLine" );
			}
			component = new XMLFormTextComponent( editing, context, multiLine );
		} else
		if ( XMLFormLabelComponent.class.getName().equals( className ) )
			component = new XMLFormLabelComponent( editing, context );
		else
		if ( XMLFormSeparatorComponent.class.getName().equals( className ) ) 
			component = new XMLFormSeparatorComponent( editing, context );
		else
		if ( XMLEnumComponent.class.getName().equals( className ) )
			component = new XMLEnumComponent( editing, context );
		else
			throw new Exception( "Unknown component " + className + "?" );
		
		// Apply properties
		if ( properties != null ) {
			for ( String key : properties.keySet() ) {
				String method = "set" + ( Character.toUpperCase( key.charAt( 0 ) ) ) +
					key.substring( 1 );
				
				Object v = properties.get( key );
				try {
					
					Class cl = v.getClass();
					if ( cl == Boolean.class )
						cl = boolean.class;
					if ( cl == Integer.class )
						cl = int.class;
					
					Method m = 
						component.getClass().getMethod( method, cl );
					m.invoke( component, v );
					
				} catch( Exception e ) {
					XmlFormModel.debug( e );
				}
			}
		}
		return component;
	}

}
