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

package com.japisoft.xmlform;

import java.awt.Point;

import javax.swing.JOptionPane;

import com.japisoft.framework.ApplicationModel;
import com.japisoft.xmlform.component.AbstractXMLFormComponent;

public class UIToolkit {

	public static void dispatchError( String errorMessage ) {		
		JOptionPane.showMessageDialog( 
				ApplicationModel.MAIN_FRAME, 
				errorMessage, 
				"Error", 
				JOptionPane.ERROR_MESSAGE );		
	}

	public static boolean confirm( String confirmMessage ) {
		int val = JOptionPane.showConfirmDialog(
				ApplicationModel.MAIN_FRAME, 
				confirmMessage,
				"",
				JOptionPane.YES_NO_OPTION );
		return val == JOptionPane.YES_OPTION;
	}

	public static void warn( String confirmMessage ) {
		JOptionPane.showMessageDialog(
			ApplicationModel.MAIN_FRAME,
			confirmMessage,
			"Warning",
			JOptionPane.WARNING_MESSAGE );
	}

	public static void info( String message ) {
		JOptionPane.showMessageDialog(
				ApplicationModel.MAIN_FRAME,
				message );
	}
	
	public static Point getLocation( AbstractXMLFormComponent component ) {

		Point p = component.getLocation();
		AbstractXMLFormComponent parent = component.getXMLFormComponentParent();
		return getLocation( p, parent );

	}

	private static Point getLocation( Point location, AbstractXMLFormComponent ancestor ) {
		if ( ancestor == null || ancestor.getY() < 0 )
			return location;
		Point pancestor = ancestor.getLocation();
		location.translate( pancestor.x, pancestor.y );
		return getLocation( location, ancestor.getXMLFormComponentParent() );
	}
	
}

