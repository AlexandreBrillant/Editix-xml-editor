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

package com.japisoft.xmlform.designer.properties;

import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JTable;

public class PropertiesTable extends JTable {
	
	private PropertiesModel model = null;
	
	public PropertiesTable() {
		setModel( model = new PropertiesModel() );
		getColumnModel().getColumn( 1 ).setCellRenderer( 
				new PropertiesRenderer() );
		getColumnModel().getColumn( 1 ).setCellEditor( 
				new PropertiesEditor() );		
	}

	public void init( PropertyDescriptor[] descriptors ) {

		// Keep the x,y, width, height to the end
		
		ArrayList<PropertyDescriptor> l = 
			new ArrayList<PropertyDescriptor>();

		if ( descriptors != null ) {
			HashMap<String,PropertyDescriptor> hm = 
				new HashMap<String,PropertyDescriptor>();

			for ( int i = 0; i < descriptors.length; i++ ) {
				
				PropertyDescriptor pd = 
					descriptors[ i ];
				
				if ( pd == null )
					continue;
				
				if ( !pd.displayable() )
					continue;
				
				if ( !( "x".equals( pd.getName() ) || 
							"y".equals( pd.getName() ) ||
								"width".equals( pd.getName() ) ||
									"height".equals( pd.getName() ) ) ) {
					l.add( pd );
				} else
					hm.put( pd.getName(), pd );
				
			}

			if ( hm.containsKey( "x" ) )
				l.add( hm.get( "x" ) );
			if ( hm.containsKey( "y" ) )
				l.add( hm.get( "y" ) );
			if ( hm.containsKey( "width" ) )
				l.add( hm.get( "width" ) );
			if ( hm.containsKey( "height" ) )
				l.add( hm.get( "height" ) );									
		}
		
		model.init( l );

	}

}


