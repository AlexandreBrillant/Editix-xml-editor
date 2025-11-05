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

package com.japisoft.framework.dockable;

import java.awt.Color;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComponent;

/**
 * @author Alexandre Brillant (https://github.com/AlexandreBrillant/Editix-xml-editor)
 */
public interface DockableFrameTitleBar {

	/** @return the dockableFrame title */
	public String getTitle();

	/** Update the title */
	public void setTitle( String title );

	/** @return an icon for this title bar */
	public Icon getIcon();
	
	/** Update the icon */
	public void setIcon( Icon icon );
	
	/** Add a new button for acting on the DockableFrame */
	public void addButton( JButton button );

	/** Add a separator for the buttons */
	public void addSeparator();

	/** Remove all buttons */
	public void removeAllButtons();

	/** Prepare the title bar component to be shown once all buttons have been added */
	public void prepare();

	/** @return the view of the title bar. This is the final component */
	public JComponent getView();

	/** Reset the focus for this titled bar */
	public void focusMode( boolean focused );

	/** Reset the color for the background header. Use a <code>null</code>
	 * value for restoring the initial value */	
	public void setBackground( Color color );

	/** Update the foreground color. Use a <code>null</code>
	 * value for restoring the initial value */
	public void setForeground( Color color );

}

