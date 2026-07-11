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
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;

import org.w3c.dom.Node;
import org.w3c.dom.Text;

import com.japisoft.framework.internationalization.Traductor;
import com.japisoft.framework.xml.grammar.GrammarNode;
import com.japisoft.framework.xml.grammar.GrammarText;
import com.japisoft.framework.xml.grammar.GrammarType;
import com.japisoft.xmlform.component.ComponentContext;
import com.japisoft.xmlform.component.container.GridComponent;
import com.japisoft.xmlform.designer.data.GrammarNodeTreeNode;
import com.japisoft.xmlform.designer.properties.PropertyDescriptor;
import com.japisoft.xmlform.designer.properties.PropertyDescriptorImpl;

public class XMLEnumComponent extends XMLEditableComponent 
		implements ActionListener {

	private JComboBox cb = null;
	
	public XMLEnumComponent( 
			boolean designMode, 
			ComponentContext context ) {	
		super( designMode, context );
		cb = new JComboBox();
		
		if ( !designMode ) // For forcing best height
			add( new JLabel( " " ), BorderLayout.NORTH );

		add( cb, BorderLayout.CENTER );

		setPreferredSize( new Dimension( 
				200, 
				4 * GridComponent.getGridSize() ) );		

		// checkEnumValues();
	}

	@Override
	public void setTooltip(String tooltip) {
		super.setTooltip(tooltip);
		cb.setToolTipText( tooltip );
	}
	
	private void checkEnumValues() {
	
		if ( designMode && cb != null ) {
			GrammarNode n = node.getSource();
			GrammarType t = n.getType();
			if ( t != null ) {
				List<GrammarNode> nodes = 
					t.getValues();
				ArrayList<String> vs = new ArrayList<String>();
				for ( GrammarNode gn : nodes ) {
					if ( gn instanceof GrammarText ) {
						GrammarText gt = ( GrammarText )gn;
						vs.add( gt.getValue() );
					}
				}
				String[] r = new String[ vs.size() ];
				for ( int i = 0; i < vs.size(); i++ )
					r[ i ] = 
						vs.get( i );
				setValues( r );
			}			
		}		

	}
	
	@Override
	public void setGrammarNode(GrammarNodeTreeNode node) {
		super.setGrammarNode(node);
		checkEnumValues();
	}
	
	@Override
	protected void setDOM( Node newDom ) {
		if ( newDom instanceof Text ) {
			newDom = newDom.getParentNode();
		}
		super.setDOM( newDom );
		domText = null;
		Node t = getDOMText();
		
		String value = t.getNodeValue();
		if ( dictionnary != null )
			value = dictionnary.get( value );
		
		cb.setSelectedItem( value );
	}

	public void addNotify() {
		super.addNotify();
		if ( designMode ) {
			cb.addMouseListener( this );
			cb.addMouseMotionListener( this );
		} else
			cb.addActionListener( this );
	}
	
	@Override
	public void removeNotify() {
		super.removeNotify();
		if ( designMode ) {
			cb.removeMouseListener( this );
			cb.removeMouseMotionListener( this );
		} else
			cb.removeActionListener( this );
	}

	private String[] values = null;
	
	public String[] getValues() {
		return values;
	}
	
	public void setValues( String[] values ) {
		firePropertyChange( "values", this.values, values );
		this.values = values;
		cb.removeAllItems();
		cb.addItem( Traductor.traduce( "nos", "No Selection" ) );
		for ( String v : values ) {
			
			if ( dictionnary != null )
				v = dictionnary.get( v );

			cb.addItem( v );
		}
	}

	private HashMap<String,String> dictionnary = null;
	
	public HashMap<String,String> getDictionnary() {
		if ( dictionnary == null ) {
			dictionnary = new HashMap<String, String>();
		}

		if ( values != null ) {
			for ( String value : values ) {
				if ( !dictionnary.containsKey( value ) ) {
					dictionnary.put( value, value );
				}
			}
		}
		return dictionnary;
	}
	
	public void setDictionnary( HashMap<String,String> dictionnary ) {
		firePropertyChange( "dictionnary", null, dictionnary );
		this.dictionnary = dictionnary;
		
		if ( values != null ) {
			//for ( int i = 0; i < values.length; i++ ) {
			//	values[ i ] = dictionnary.get( values[ i ] );
			//}
			
			DefaultComboBoxModel model = new DefaultComboBoxModel();			
			model.addElement( cb.getItemAt( 0 ) );

			for ( String value : values ) {

				if ( dictionnary.containsKey( value ) )
					model.addElement( 
							dictionnary.get( value ) );
				else
					model.addElement( value );

			}

			cb.setModel( model );
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		int index = cb.getSelectedIndex();
		if ( index == 0 )
			deleteDOMText();
		else {
			
			String value = ( String )cb.getSelectedItem();

			if ( dictionnary != null ) {
				// Translate it

				for ( Entry<String,String> entry : dictionnary.entrySet() ) {
					
					if ( value.equals( entry.getValue() ) ) {
						value = entry.getKey();
						break;
					}
					
				}
			}

			getDOMText().setNodeValue( value );
		}
	}

	protected void prepareProperties( 
			ArrayList<PropertyDescriptor> l ) throws Exception {
		super.prepareProperties( l );
		l.add( new PropertyDescriptorImpl( 
			"dictionnary", 
			HashMap.class, 
			this ) );
	}

}
