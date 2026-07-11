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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.util.ArrayList;

import javax.swing.JLabel;
import javax.swing.JOptionPane;

import com.japisoft.xmlform.component.container.GridComponent;
import com.japisoft.xmlform.designer.properties.PropertyDescriptor;
import com.japisoft.xmlform.designer.properties.PropertyDescriptorImpl;

public class XMLFormLabelComponent extends StaticXMLFormComponent {

	private JLabel label = null;
	
	public XMLFormLabelComponent( boolean designMode, 
			ComponentContext context ) {
		super( designMode, context );
		add( label = new JLabel() );
		setLabel( "Your label" );
		Dimension dim = label.getPreferredSize();
		dim.width += 40;
		dim.height = 2 * GridComponent.getGridSize();
		setPreferredSize( dim );
	}

	private String text = null;
	
	public void setLabel( String text ) {
		firePropertyChange( "label", this.text, text );
		this.text = text;
		label.setText( text );
	}

	public String getLabel() {
		return text;
	}

	@Override
	public void setFont(Font font) {
		label.setFont( font );
	}
	
	@Override
	public Font getFont() {
		return label.getFont();
	}
	
	@Override
	public void setBackground( Color bg ) {
		label.setBackground( bg );
	}

	@Override
	public Color getBackground() {
		return label.getBackground();
	}

	@Override
	public void setForeground(Color fg) {
		label.setForeground(fg);
	}

	@Override
	public Color getForeground() {
		return super.getForeground();
	}

	protected void activeAction() {
		String newText = JOptionPane.showInputDialog( "New text", getLabel() );
		if ( newText != null )
				setLabel( newText );
	}

	protected void prepareProperties( ArrayList<PropertyDescriptor> l ) throws Exception {
		super.prepareProperties( l );
		l.add( new PropertyDescriptorImpl( "label", String.class, this ) );
	}

}
