package com.japisoft.xmlpad.error;

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
