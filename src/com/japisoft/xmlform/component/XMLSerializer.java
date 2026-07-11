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
import java.awt.Component;
import java.awt.Font;
import java.awt.Rectangle;
import java.beans.PropertyChangeEvent;
import java.util.HashMap;
import java.util.Iterator;

import javax.swing.border.Border;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.japisoft.xmlform.component.container.GridComponent;

public class XMLSerializer {

	public static Element serialize( 
			Document doc,
			Element parent,
			AbstractXMLFormComponent component ) {

		component.resolveNextSibling();

		Element field = doc.createElementNS(
				null,
				"field" );
		field.setAttribute( 
				"class", 
				component.getClass().getName() );
		Element p = doc.createElementNS(
				null,
				"properties" );
		field.appendChild( p );
		if ( parent != null )
			parent.appendChild( field );
		
		HashMap<String, PropertyChangeEvent> mapProperties = 
			component.getModifiedProperties();
		if ( mapProperties != null ) {
			Iterator<String> it = 
				mapProperties.keySet().iterator();
			while ( it.hasNext() ) {
				String key = it.next();
				PropertyChangeEvent e = mapProperties.get( key );
				Element property = 
					serialize( doc, e );
				if ( property != null )
					p.appendChild( property );
			}
		}

		// Add children
		for ( int i = 0; i < component.getComponentCount(); i++ ) {

			Component c = component.getComponent( i );
			
			// Process the grid
			if ( c instanceof GridComponent ) {
				
				GridComponent gc = ( GridComponent )c;
				for  ( int j = 0; j < gc.getComponentCount(); j++ ) {
					Component c2 = gc.getComponent( j );
					if ( c2 instanceof AbstractXMLFormComponent ) {
						// Add it
						field.appendChild(
								serialize( 
										doc, 
										field, 
										( AbstractXMLFormComponent )c2 ) );

					}
				}

			}

			// Process the other component
			if ( c instanceof AbstractXMLFormComponent ) {
				// Add it
				field.appendChild(
						serialize( 
								doc, 
								field, 
								( AbstractXMLFormComponent )c ) );
			}

		}

		return field;
	}

	private static Element serialize(
			Document doc,
			PropertyChangeEvent e ) {

		Object val = e.getNewValue();
		if ( val == null )
			return null;
		if ( val instanceof Border ) {
			return null;
		}

		Element field = doc.createElementNS( null, "property" );
		field.setAttribute( "name", e.getPropertyName() );
		field.setAttribute( "class", val.getClass().getName() );
		field.setAttribute( "value", valueToString( val ) );
		return field;

	}

	public static String valueToString( Object value ) {
		if ( value instanceof String ) {
			return value.toString();
		} else
		if ( value instanceof Rectangle ) {
			Rectangle r = ( Rectangle )value;
			return r.x + "," + r.y + "," + r.width + "," + r.height;
		} else
		if ( value instanceof Color ) {
			Color c = ( Color )value;
			return c.getRed() + "," + c.getGreen() + "," + c.getBlue();
		} else 
		if ( value instanceof Font ) {
			Font f = ( Font )value;
			return f.getFamily() + "," + f.getStyle() + "," + f.getSize();
		} else
		if ( value instanceof String[] ) {
			String[] ss = ( String[] )value;
			StringBuffer sb = new StringBuffer();
			for ( String s : ss ) {
				if ( sb.length() > 0 )
					sb.append( "~~" );
				sb.append( s );
			}
			return sb.toString();
		} else
		if ( value instanceof HashMap ) {
			StringBuffer sb = new StringBuffer();
			HashMap<String,String> map = ( HashMap<String, String> )value;
			for ( String key : map.keySet() ) {
				if ( sb.length() > 0 )
					sb.append( "~~" );				
				sb.append( key );
				sb.append( "~~" );
				sb.append( map.get( key ) );
			}
			return sb.toString();
		}

		return value.toString();
	}

}
