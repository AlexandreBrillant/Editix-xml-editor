// Editix XML Editor
// https://www.editix.com
// Copyright (c) 2026 Alexandre Brillant
//
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
// AI Training Restriction :
// This source code is provided for human use only.
// Using this code to train, fine-tune, or develop AI models,
// machine learning systems, or similar technologies is
// STRICTLY PROHIBITED. Violations will terminate all rights
// under the applicable license.

package com.japisoft.framework.dockable;

import java.awt.Color;
import java.awt.Rectangle;

import javax.swing.Icon;
import javax.swing.JComponent;

import com.japisoft.framework.dockable.action.ActionModel;

/**
 * For inner usage only
 * @author Alexandre Brillant (https://github.com/AlexandreBrillant/Editix-xml-editor)
 * @version 1.0
 * */
public interface Windowable {

	/** @return the id of the component */
	public String getId();

	/** @return the real UI component */	
	public JComponent getUserView();	

	/** @return the global component */
	public JComponent getView();
	
	/** @return the current title */
	public String getTitle();

	/** Reset for a new title */
	public void setTitle( String title );

	/** Fixed a window, so it cannot be swapped with another window */
	public void setFixed( boolean fixed );
	
	/** @return <code>true</code> if the window cannot be swapped with another window */
	public boolean isFixed();

	/** The window can be resized */ 
	public void setResizable( boolean resize );

	/** @return true if you can Resize the window ? */
	public boolean isResizable();
	
	/** @return the current icon */
	public Icon getIcon();
	
	/** Reset for a new icon */
	public void setIcon( Icon icon );

	/** @return the set of available actions */
	public	ActionModel getActionModel();	

	/** Update the backgrond for this window */
	public void setBackground( Color background );	

	/** Update the foreground for this window */
	public void setForeground( Color foreground );	

	/** Reset the focus */
	public void requestFocus();

	/** @return the container with the user UI panel */
	public JComponent getContentPane();	
	
	/** Repaint the view */
	public void repaint();
	
	/** If the inner window has been transformed inside a frame */
	public Rectangle getFrameBounds();

	/** Last location and size when the inner window has been transformed inside a frame */
	public void setFrameBounds( Rectangle r );

	/** Reset the final content */
	public void setContentPane( JComponent container );	

	/** Maximized state */
	public void setMaximized( boolean max );
	
	/** @return the maximized state */
	public boolean isMaximized();

	void fireDockEvent( String id, int type );	
	
}	
