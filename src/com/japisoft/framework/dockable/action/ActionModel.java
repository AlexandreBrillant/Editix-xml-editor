package com.japisoft.framework.dockable.action;

import javax.swing.Action;

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
public interface ActionModel {

	/** Special action for adding a separator */
	public static Action SEPARATOR = null;

	/** @return the action count */
	public int getActionCount();

	/** @return the action at this index starting from zero */
	public Action getAction( int index );

	/** @return <code>true</code> if a separator is available at this index */
	public boolean isSeparator( int index );
	
	/** @return an action matching this className */
	public Action getActionByClass( Class className );

	/** Add a new action or a <code>SEPARATOR</code> */
	public void addAction( Action a );
	
	/** Add an action for this index */
	public void addAction( Action a, int index );

	/** Remove an action */
	public void removeAction( Action a );

	/** Remove all actions */
	public void removeAll();

	/** Add a listener for knowing the action model state change */
	public void addModelStateListener( ModelStateListener listener );

	/** Remove a known listener */
	public void removeModelStateListener( ModelStateListener listener );
	

}
