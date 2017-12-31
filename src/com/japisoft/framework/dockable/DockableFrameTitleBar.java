package com.japisoft.framework.dockable;

import java.awt.Color;

import javax.swing.Icon;
import javax.swing.JButton;
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
