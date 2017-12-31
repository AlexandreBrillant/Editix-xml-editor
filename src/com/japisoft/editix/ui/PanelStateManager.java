package com.japisoft.editix.ui;

import java.util.ArrayList;
import com.japisoft.xmlpad.XMLContainer;

/**
This program is available under two licenses : 

1. For non commercial usage : 

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.

2. For commercial usage :

You need to get a commercial license for source usage at : 

http://www.editix.com/buy.html

Copyright (c) 2018 Alexandre Brillant - JAPISOFT SARL - http://www.japisoft.com

@author Alexandre Brillant - abrillant@japisoft.com
@author JAPISOFT SARL - http://www.japisoft.com

*/
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
 
