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

package com.japisoft.multipanes;

import javax.swing.JComponent;

/**
 * Interface for showing the opening/closing state
 * @author Alexandre Brillant (https://github.com/AlexandreBrillant/Editix-xml-editor)
 */
public interface TitledPaneView {

	/** Initialize the pane view */
	public void init( MultiPanes mp );
	
	/** @return a component showing panel header */
	public JComponent buildPanelHeader( TitledPane pane );
	
	/** Update the view due to opening / closing new state 
	 * @param headerView the top view part containing the title, this is the component returned by the buildPanelHeader method
	 * @param pane The title pane content
	 * @param openedState true if the titledPane is opened
	 * */
	public void updateView( JComponent headerView, TitledPane pane, boolean openedState );
	
}

