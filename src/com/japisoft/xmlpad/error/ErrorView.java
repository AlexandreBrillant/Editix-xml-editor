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

package com.japisoft.xmlpad.error;

import javax.swing.JComponent;

/**
 * Interface for defining the Error Panel. This is the panel that
 * is shown below the main panel while parsing the whole document.
 * @author Alexandre Brillant (https://github.com/AlexandreBrillant/Editix-xml-editor)
 * @version 1.0 */
public interface ErrorView extends ErrorListener {
	
	/** It must always return the same view object. Don't create
	 * a new object for this method return */
	public JComponent getView();
	
	/**
	 * @return <code>true</code> if the error panel is shown for onTheFly error (while inserting characters) */
	public boolean isShownForOnTheFly();
	
	/** Add a selection listener for listening error choosen */
	public void addErrorSelectionListener( ErrorSelectionListener listener );

	/** Remove a selection listener for listening error not choosen */
	public void removeErrorSelectionListener( ErrorSelectionListener listener );

	/** Call when the view is not be used more. Useful for freeing some memory parts */
	public void dispose();
	
	/** Call whent the error view has been added */
	public void initOnceAdded();
}
