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

/**
 * Interface for the model containing a set of <code>TitledPane</code>
 * @author Alexandre Brillant (https://github.com/AlexandreBrillant/Editix-xml-editor)
 */
public interface TitledPaneModel {

	/** Add a new titledPane component */
	public void addTitledPane( TitledPane tp );
	
	/** Remove a previously inserted component */
	public void removeTitledPane( TitledPane tp );

	/** Insert a titledPane at a specific location */
	public void insertTitledPane( int index, TitledPane tp );

	/** @return a titledPane matching the parameter name */
	public TitledPane getTitledPaneByName( String name );

	/** @return the number of titledPane */
	public int getTitledPaneCount();
	
	/** @return a titledPane for a location starting at 0 */
	public TitledPane getTitledPaneAt( int location );
	
	/** @return the index of the titledPane starting at 0 */
	public int getTitledPaneIndex( TitledPane tp );
}
