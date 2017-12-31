package com.japisoft.framework.dockable;

import java.awt.Color;
import java.awt.Rectangle;

import javax.swing.Icon;
import javax.swing.JComponent;

import com.japisoft.framework.dockable.action.ActionModel;

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
