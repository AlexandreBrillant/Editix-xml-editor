package com.japisoft.framework.dockable.action;

import java.util.ArrayList;
import javax.swing.Action;

import com.japisoft.framework.dockable.ComponentFactory;
import com.japisoft.framework.dockable.BasicInnerWindow;
import com.japisoft.framework.dockable.DockableFrameTitleBar;
import com.japisoft.framework.dockable.action.common.*;
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
public final class CommonActionManager {

	static ArrayList model = new ArrayList();

	static {
		//addCommonAction( ExtractAction.class );
		addCommonAction( MaxMinAction.class );
	}

	/** Add a common action by its class */
	public static void addCommonAction( Class a ) {
		model.add( a );
	}

	/** Add a common action by its class */
	public static void addCommonAction( Class a, int location ) {
		model.add( location, a );
	}

	/** Remove an action by its class */
	public static void removeCommonAction( Class a ) {
		model.remove( a );
	}

	/** @return the available action count */
	public static int getCommonActionCount() {
		return model.size();
	}

	/** @return a common action class for this index starting from 0 */
	public static Class getCommonActionAt( int index ) {
		return ( Class )model.get( index );
	}

	/** @return a new instance of the Action for this index starting from 0 working for this panel */	
	public static Action buildCommonActionAt( int index, BasicInnerWindow panel ) {
		Class cl = ( Class )getCommonActionAt( index );
		try {
			Action a = ( Action )cl.newInstance();
			if ( a instanceof DockableAction ) {
				( ( DockableAction )a ).setDockableContext( panel );
			}
			return a;
		} catch( Throwable th ) { 
			th.printStackTrace();
			return null;
		}
	}

	/** Update the model with the following common actions */
	public static void fillModelWithCommonActions( BasicInnerWindow panel, ActionModel am ) {
		am.removeAll();
		for ( int i = 0; i < model.size(); i++ ) {
			am.addAction( buildCommonActionAt( i, panel ) );		
		}
	}

	/** Fill the inner window header with buttons from the following action model */
	public static void fillWindowTitleBar( DockableFrameTitleBar titleBar, ActionModel am ) {
		titleBar.removeAllButtons();

		for ( int i = 0; i < am.getActionCount(); i++ ) {
			if ( !am.isSeparator( i ) ) { 
				titleBar.addButton( 
						ComponentFactory.getComponentFactory().buildButton( 
								am.getAction( i ) ) );
			} else {
				titleBar.addSeparator();
			}
		}

	}
	
}
 
