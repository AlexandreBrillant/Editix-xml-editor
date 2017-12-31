package com.japisoft.framework.dockable;

import java.awt.Component;
import java.awt.Container;
import java.awt.Rectangle;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;

import javax.swing.JComponent;

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
class DefaultState implements State {

	private Hashtable resizeState; 
	private Hashtable swapState;
	private Hashtable frameState;
	private String maximized;
	
	// Store the current state
	void read( JDock dock ) {
		
		DockableLayout layout = dock.getInnerLayout();

		// Maximized

		BasicInnerWindow maxIW = ( BasicInnerWindow )dock.getInnerLayout().maximizedComponent;
		if ( maxIW != null )
			maximized = maxIW.getId();

		// Size

		if ( layout.htComponentConstraints != null && 
				layout.htConstraintsResize != null ) {
			resizeState = new Hashtable();

			Iterator enume = layout.htComponentConstraints.keySet().iterator();
			while ( enume.hasNext() ) {
				JComponent comp = ( JComponent )enume.next();
				if ( comp instanceof BasicInnerWindow ) {
					BasicInnerWindow iw = ( BasicInnerWindow )comp;
					String id = iw.getId();
					Object constraint = layout.htComponentConstraints.get( comp );
					Object size = layout.htConstraintsResize.get( constraint );
					
					if ( size != null ) {
						resizeState.put( id, new Rectangle( ( Rectangle )size ) );
					}
				}
			}
		}

		// Swap
		
		if ( layout.htCompComp != null ) {
			
			swapState = new Hashtable();
			Iterator enume = layout.htCompComp.keySet().iterator();
			while ( enume.hasNext() ) {
				JComponent comp = ( JComponent )enume.next();
				if ( comp instanceof BasicInnerWindow ) {
					BasicInnerWindow iw = ( BasicInnerWindow )comp;
					if ( layout.htCompComp.get( iw ) instanceof BasicInnerWindow ) {
						BasicInnerWindow target = ( BasicInnerWindow )layout.htCompComp.get( iw );
						swapState.put( iw.getId(), target.getId() );
					}
				}
			}			
		}

		// Frame
		
		Container container = dock.panel;
		for ( int i = 0; i < container.getComponentCount(); i++ ) {
			Component c = container.getComponent( i );
			if ( c instanceof BasicInnerWindow ) {
				BasicInnerWindow iw = ( BasicInnerWindow )c;
				if ( iw.frameBounds != null ) {
					if ( frameState != null )
						frameState = new Hashtable();
					frameState.put( iw.getId(), iw.frameBounds );
				}
			}
		}

	}

	// Restore the current state
	void write( JDock dock ) {	

		DockableLayout layout = dock.getInnerLayout();
				
		// Maximized
		
		if ( maximized != null ) {
			try {
				dock.maximizeInnerWindow( maximized );
			} catch( RuntimeException exc ) {		
			}
		} else
			layout.maximizedComponent = null;
	
		// Size
	
		if ( resizeState != null ) {

			layout.htConstraintsResize = new HashMap();
			
			Enumeration keys = resizeState.keys();
			while ( keys.hasMoreElements() ) {
				String id = ( String )keys.nextElement();
				Rectangle size = new Rectangle( ( Rectangle )resizeState.get( id ) );
				
				Windowable w = dock.getInnerWindowForId( id );
				JComponent c = null;
				if ( w != null )
					c = w.getView();
				
				if ( c != null ) {
					layout.htConstraintsResize.put( 
									layout.htComponentConstraints.get( c ),
									size );
				
				} else
					System.err.println( "Unknown " + id );

			}

		}
		
		// Swap
		
		if ( swapState != null ) {

			layout.htCompComp = new HashMap();

			Enumeration keys = swapState.keys();

			while ( keys.hasMoreElements() ) {
				String id = ( String )keys.nextElement();
				String id2 = ( String )swapState.get( id );

				Windowable iww1 = dock.getInnerWindowForId( id );
				Windowable iww2 = dock.getInnerWindowForId( id2 );

				JComponent iw1 = null;
				JComponent iw2 = null;
				
				if ( iww1 != null && iww2 != null ) {

					iw1 = iww1.getView();
					iw2 = iww2.getView();

				}
				
				if ( iw1 != null && iw2 != null ) {
					
					layout.htCompComp.put( iw1, iw2 );
					
				}
				
			}	
	
		}
		
		// Frame

		if ( frameState != null ) {
	
			Enumeration keys = frameState.keys();

			while ( keys.hasMoreElements() ) {
				String id = ( String )keys.nextElement();
				
				Windowable iw = dock.getInnerWindowForId( id );
				if ( iw != null ) {
				
					iw.setFrameBounds( ( Rectangle )frameState.get( id ) );
					
				}
			}			

		}
		
	}

}
