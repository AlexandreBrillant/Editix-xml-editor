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

import java.awt.Component;
import java.awt.Container;
import java.awt.Rectangle;

import javax.swing.JViewport;

import com.japisoft.xmlform.component.container.GridComponent;

public class DynamicLayout {

	public static int BORDER_Y = 4;
	
	public static void remove( 
			Container parent,
			AbstractXMLFormComponent child ) {

		// Remove from DOM
		AbstractXMLFormComponent xmlParent = child.getXMLFormComponentParent();
		xmlParent.getDOM().removeChild( child.getDOM() );
		
		int limitY = child.getY();
		int deltaY = child.getHeight();

		// Remove physically
		parent.remove( child );
		
		unpushSiblingComponent( parent, limitY, deltaY );
		
		parent.invalidate();
		parent.validate();
	}
	
	private static void unpushSiblingComponent(
			Container parent,
			int limitY,
			int deltaY ) {

		for ( int i = 0; i < parent.getComponentCount(); i++ ) {
			Component c = 
				parent.getComponent( i );		
			if ( c.getY() > limitY ) {
				Rectangle r = c.getBounds();
				r.translate( 0, -deltaY );
				c.setBounds( r );
			}
		}			

		if ( parent instanceof GridComponent ) {
			parent = parent.getParent();
		}

		Rectangle r = parent.getBounds();
		r.height -= deltaY;
		parent.setBounds( r );

		if ( parent.getParent() instanceof XMLFormComponent ) {
			parent.invalidate();
			parent.validate();
			
			unpushSiblingComponent( parent.getParent(), r.y, deltaY );
		} else {
			
			// Force scrollPane resize
			if ( parent.getParent() instanceof JViewport ) {
				parent.setPreferredSize( parent.getSize() );
				parent.getParent().getParent().invalidate();
				parent.getParent().getParent().validate();
			}
			
			
		}
	}

	// ----------------------------------------------------------------------------------
	
	public static void add( 
			Container parent,
			AbstractXMLFormComponent child,
			AbstractXMLFormComponent siblingChild ) {

		// Insert it under the child
		
		Rectangle r = child.getBounds();		
		
		Rectangle r2 = siblingChild.getBounds();
		r2.y = r.y + r.height + BORDER_Y;
		
		parent.add( siblingChild );

		siblingChild.setBounds( r2 );

		pushSiblingComponent( 
				parent, 
				siblingChild, 
				siblingChild.getY(), 
				siblingChild.getHeight() + BORDER_Y );

		parent.invalidate();
		parent.validate();

	}

	private static void pushSiblingComponent(
			Container parent,
			Component newChild,
			int limitY,
			int deltaY ) {

		int maxY = 0;

		for ( int i = 0; i < parent.getComponentCount(); i++ ) {
			Component c = 
				parent.getComponent( i );
			if ( c != newChild ) {
				if ( c.getY() >= limitY - 2 * BORDER_Y ) {
					Rectangle r = c.getBounds();
					r.translate( 0, deltaY + BORDER_Y );
					c.setBounds( r );
				}
			}
			maxY = Math.max( 
					c.getY() + c.getHeight(), 
					maxY );
		}

		if ( maxY > parent.getHeight() ) {
			// Resize the parent too
			
			int delta = 
				( maxY - parent.getHeight() );

			if ( parent instanceof GridComponent ) {
				parent = parent.getParent();
			}

			Rectangle r = parent.getBounds();
			r.height += delta + BORDER_Y;
			parent.setBounds( r );

			if ( parent.getParent() instanceof XMLFormComponent ) {
				parent.invalidate();
				parent.validate();
				
				// Check this parent again
				pushSiblingComponent( 
						parent.getParent(), 
						parent, 
						parent.getY(), 
						delta + BORDER_Y );
			}

			// Force scrollPane resize
			if ( parent.getParent() instanceof JViewport ) {
				parent.setPreferredSize( 
						parent.getSize() );
				parent.getParent().getParent().invalidate();
				parent.getParent().getParent().validate();
			}

		}

	}

}
