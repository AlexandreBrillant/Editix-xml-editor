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

package com.japisoft.framework.dialog;

import java.awt.Component;

import javax.swing.JComponent;

/**
 * @author Alexandre Brillant (https://github.com/AlexandreBrillant/Editix-xml-editor)
 * @version 1.0
 * */
public interface DialogComponent {

	/** @return the last user action */
	public int getLastAction();

	/** Show or hide the dialog */
	public void setVisible( boolean visible );
	
	public void setVisible( boolean visible, int lastAction );

	public void setModal( boolean modal );

	/**
	 * @param header The header component
	 * @param pane The user component
	 * @param footer The component with the available buttons */
	public DialogComponent init( DialogHeader header, JComponent pane, DialogFooter footer );

	/** Freeing all the resource */
	public void dispose();
	
	/** @return the dialog component */
	public Component getView();
	
}

