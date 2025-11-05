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

package com.japisoft.xmlform.component;

import java.awt.Component;
import java.awt.FocusTraversalPolicy;
import java.awt.Rectangle;

import javax.swing.FocusManager;
import javax.swing.JScrollPane;
import javax.swing.JViewport;
import javax.swing.SwingUtilities;

public class CustomFocusManager extends FocusManager {

	private AbstractXMLFormComponent getAncestor( Component c ) {
		if ( c == null )
			return null;		
		if ( c instanceof AbstractXMLFormComponent )
			return ( AbstractXMLFormComponent )c;
		return getAncestor( c.getParent() );
	}

	private JScrollPane getScrollPaneAncestor( Component c ) {
		if ( c == null )
			return null;		
		if ( c instanceof JScrollPane )
			return ( JScrollPane )c;
		return getScrollPaneAncestor( c.getParent() );
	}
	
	@Override
	public void focusNextComponent( Component component ) {

		component = 
			getCurrentManager().getFocusOwner();
		
		AbstractXMLFormComponent c = getAncestor( component );
		
		if ( c != null  ) {
			c.nextXMLFormFocus();
			c.scrollRectToVisible( c.getBounds() );
		} else
			super.focusNextComponent( component );
	}

	@Override
	public void focusPreviousComponent( Component component ) {

		component = 
			getCurrentManager().getFocusOwner();
		
		JScrollPane sp = getScrollPaneAncestor( component );
		if ( sp != null ) {
			
			if ( component.getParent() instanceof JViewport ) {
				component = component.getParent().getParent();
			}
			
			Rectangle destination = 
				SwingUtilities.convertRectangle( 
						component, 
						component.getBounds(), 
						sp );

			destination.translate( 0, -100 );			
			
			sp.getViewport().scrollRectToVisible( destination );

		}
	}

}

