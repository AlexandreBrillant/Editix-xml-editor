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

package com.japisoft.framework.ui.multipanes;

import java.awt.Color;
import java.beans.PropertyChangeListener;

import javax.swing.Icon;
import javax.swing.JComponent;

/**
 * Interface for the multipanes content. This content
 * is a row with a button, click on this button will show
 * the panel content. The button is built using the provided
 * properties (getTitle, getIcon, getToolTip), the panel content is
 * available by the getView. Not that this is more optimized to build
 * the panel content only if this is requested, once.
 * @author Alexandre Brillant (https://github.com/AlexandreBrillant/Editix-xml-editor)
 */
public interface TitledPane {

	/** @return the main title of the panel. It can't be <code>null</code> */
	public String getTitle();
	
	/** @return an icon for the panel, it can be <code>null</code> */
	public Icon getIcon();
	
	/** @return a toolTip for the panel header, it can be <code>null</code> */
	public String getToolTip();

	/** @return a name for this panel */
	public String getName();
	
	/** @return the panel content */
	public JComponent getView();
	
	/** Called when the titledPane is opened by the user */
	public void open();
	
	/** Called when the titlePanel is closed by the user */
	public void close();

	/** Enabled / Disabled */
	public boolean isEnabled();
	
	/** The titlePane has been removed from the multipanes */
	public void dispose();

	/** @return a color for the titled pane background. */
	public Color getBackground();

	/** @return a color for the titled pane foreground */
	public Color getForeground();

	/** Listener about the user interface properties like title, icon or tooltip
	 *  properties name is binded on the swing Action.key */
	public void addPropertyChangeListener(PropertyChangeListener listener);

}
