package com.japisoft.multipanes;

import java.util.ArrayList;

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
