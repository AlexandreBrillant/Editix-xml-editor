package com.japisoft.multipanes;

import javax.swing.JComponent;

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
