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

package com.japisoft.framework.dockable.action;

import javax.swing.Action;

/**
 * Set of action for the DockablePanel
 * @author Alexandre Brillant (https://github.com/AlexandreBrillant/Editix-xml-editor)
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

