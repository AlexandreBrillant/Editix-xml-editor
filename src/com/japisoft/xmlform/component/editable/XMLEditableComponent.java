// Editix XML Editor
// https://www.editix.com
// Copyright (c) 2025 Alexandre Brillant
// 
// For non-commercial usage :
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
// For commercial use or integration into proprietary software :
// A commercial license is required. Visit https://www.editix.com for details.

package com.japisoft.xmlform.component.editable;

import java.awt.Color;
import java.util.ArrayList;

import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.Text;

import com.japisoft.framework.preferences.Preferences;
import com.japisoft.xmlform.component.AbstractXMLFormComponent;
import com.japisoft.xmlform.component.ComponentContext;
import com.japisoft.xmlform.designer.properties.PropertyDescriptor;
import com.japisoft.xmlform.designer.properties.PropertyDescriptorImpl;

public class XMLEditableComponent extends AbstractXMLFormComponent {

	public XMLEditableComponent( 
			boolean designMode, 
			ComponentContext context ) {
		super( designMode, context );
	}	
	
	protected String tooltip = null;
	
	public void setTooltip( String tooltip ) {
		firePropertyChange( "tooltip", this.tooltip, tooltip );
		this.tooltip = tooltip;
	}

	public String getTooltip() {
		return tooltip;
	}

	@Override
	public void setMinOccurs( int minOccurs ) {
		super.setMinOccurs(minOccurs);
	}

	@Override
	protected void prepareProperties( 
			ArrayList<PropertyDescriptor> l ) throws Exception {
		super.prepareProperties(l);
		l.add(
			new PropertyDescriptorImpl(
					"tooltip",
					String.class,
					this ) );
	}

	public Color getColorForRequiredField() {
		return Preferences.getPreference( 
				"editor", 
				"requiredField", 
				new Color( 246,191,191 ) );
	}
	
	protected Node domText = null;

	private Text getFirstTextNode( Node parentNode ) {
		if ( parentNode.hasChildNodes() ) {
			if ( parentNode.getFirstChild() instanceof Text ) {
				return ( Text )parentNode.getFirstChild();
			} else
				return getFirstTextNode( parentNode.getFirstChild() );
		} else
			return null;
	}
	
	protected Node getDOMText() {
		if ( domText == null ) {
			Node n = getDOM();
			
			if ( !( n instanceof Text ) ) {
			
				if ( n instanceof Attr ) {
					this.domText = n;
				} else {
					domText = getFirstTextNode( n );
					if ( domText == null ) {
						domText = context.getDocument().createTextNode( "" );
						domText.setUserData( "ui", this, null );
						n.appendChild( domText );
					}
				}

			} else {
				
				domText = n;
				
			}
		}
		
		checkNodeIsBound( getDOM(), this );
		return domText;
	}

	protected void deleteDOMText() {
		if ( domText != null ) {			
			Node parent = domText.getParentNode();
			
			if ( domText instanceof Attr ) 
				parent = ( ( Attr )domText ).getOwnerElement();

			if ( parent != null ) {
				if ( domText instanceof Attr ) {
					( ( Element )parent ).removeAttributeNode( ( Attr )domText );
				} else
					parent.removeChild( domText );
			}
			domText = null;
		}
	}
	
}

