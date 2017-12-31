package com.japisoft.multipanes;

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
