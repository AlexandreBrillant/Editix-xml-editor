package com.japisoft.multipanes;

import java.awt.Color;
import java.beans.PropertyChangeListener;

import javax.swing.Icon;
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
