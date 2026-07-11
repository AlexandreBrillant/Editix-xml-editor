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

package com.japisoft.multipanes;

import java.util.ArrayList;

/**
 * Here a default model storing all the TitledPane
 * @author Alexandre Brillant (https://github.com/AlexandreBrillant/Editix-xml-editor)
 * @see TitledPane
 */
public class DefaultTitledPaneModel implements TitledPaneModel {

	private ArrayList model;
	private MultiPanes panes;
	
	public DefaultTitledPaneModel( MultiPanes panes ) {
		this.panes = panes;
		model = new ArrayList();
	}

	public void addTitledPane( TitledPane tp ) {
		model.add( tp );
		panes.updateView();
	}
	
	public void insertTitledPane( int location, TitledPane tp ) {
		model.add( location, tp );
		panes.updateView();
	}

	public void removeTitledPane( TitledPane tp ) {
		model.remove( tp );
		tp.dispose();
		panes.updateView();
	}

	public int getTitledPaneIndex(TitledPane tp) {
		return model.indexOf( tp );
	}	
	
	public TitledPane getTitledPaneByName(String name) {
		if ( name == null )
			throw new RuntimeException( "Illegal null name" );
		for ( int i = 0; i < model.size(); i++ ) {
			TitledPane tp = ( TitledPane )model.get( i );
			if ( name.equals( tp.getName() ) )
				return tp;
		}
		return null;
	}

	public TitledPane getTitledPaneAt(int index) {
		if ( index >= model.size() )
			return null;
		return ( TitledPane )model.get( index );
	}

	public int getTitledPaneCount() {
		return model.size();
	}

}
