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

package com.japisoft.editix.editor.xsd.view.element;

import java.awt.BorderLayout;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import org.w3c.dom.Element;

import com.japisoft.editix.editor.xsd.Changeable;
import com.japisoft.editix.editor.xsd.Factory;
import com.japisoft.editix.editor.xsd.toolkit.SchemaHelper;
import com.japisoft.editix.editor.xsd.view.View;
import com.japisoft.editix.editor.xsd.view.element.simpletype.SimpleTypeViewImpl;

public class ElementViewImpl extends JPanel implements View, Changeable {	
	private JTabbedPane tp = new JTabbedPane( JTabbedPane.BOTTOM );
	private PropertiesViewImpl av = new PropertiesViewImpl(); 
	private SimpleTypeViewImpl stv = null;

	private Factory factory = null;
	
	public ElementViewImpl( 
			Factory factory, 
			PropertiesViewListener listener ) {
		stv = new SimpleTypeViewImpl( factory );
		setLayout( new BorderLayout() );
		add( tp );
		tp.addTab( "Properties", 
				new JScrollPane( av.getView(), JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER) );
		tp.addTab( "Simple type", 
				stv.getView() );
		av.setPropertiesViewListener( listener );
	}

	public void setDesignerMode( boolean designerMode ) {
		av.setDesignerMode( designerMode );
	}	

	public boolean isDesignerMode() {
		return av.isDesignerMode();
	}
			
	public void init( Element schemaNode ) {
		av.init( schemaNode );
		stv.init( schemaNode );
		
		
		if ( schemaNode != null ) {
			tp.setEnabledAt( 0, true );
			tp.setEnabledAt( 1, "element".equals( schemaNode.getLocalName() )
					|| "simpleType".equals( schemaNode.getLocalName() )
						|| "attribute".equals( schemaNode.getLocalName() ) );
			if ( !tp.isEnabledAt( 1 ) ) {
				if ( tp.getSelectedIndex() == 1 )
					tp.setSelectedIndex( 0 );
			}
			
			// Check for complexType case
			if ( "element".equals( schemaNode.getLocalName() ) ) {
				if ( SchemaHelper.hasChild( schemaNode, "complexType" ) ) {
					tp.setEnabledAt( 1, false );
				}
			}

		} else {
			tp.setSelectedIndex( 0 );
			tp.setEnabledAt( 0, false );
			tp.setEnabledAt( 1, false );
		}
	}
	
	public boolean isChanged() { 
		return av.isChanged() || stv.isChanged();
	}
	
	public JComponent getView() {
		return this;
	}

	public void dispose() {
		av.dispose();
		stv.dispose();
	}

	public void stopEditing() {		
		av.stopEditing();
	}
	
	@Override
	public void copy() {
	}
	@Override
	public void cut() {
	}
	@Override
	public void paste() {
	}
	
}
