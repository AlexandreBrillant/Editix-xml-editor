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

package com.japisoft.editix.ui;

import java.util.ArrayList;
import com.japisoft.xmlpad.XMLContainer;

/** Connect your objet for receiving info about the current XML container */
public class PanelStateManager {

	private static ArrayList panels = null;
	
	public static void addPanelStateListener( PanelStateListener listener ) {
		if ( panels == null )
			panels = new ArrayList();
		panels.add( listener );
	}

	// Don't call
	static void fireCurrentXMLContainer( XMLContainer container ) {
		if ( panels != null ) {
			for ( int i = 0; i < panels.size(); i++ ) {
				( ( PanelStateListener )panels.get( i ) ).setCurrentXMLContainer( 
						container );
			}
		}
	}

	// Don't call
	static void fireNewPath( String previousPath, String newPath ) {
		if ( panels != null ) {
			for ( int i = 0; i < panels.size(); i++ ) {
				( ( PanelStateListener )panels.get( i ) ).newPath( 
						previousPath, newPath );
			}
		}		
	}
	
	// Don't call
	static void fireClose( XMLContainer container ) {
		if ( panels != null ) {
			for ( int i = 0; i < panels.size(); i++ ) {
				( ( PanelStateListener )panels.get( i ) ).close( container );
			}
		}				
	}

}
 
