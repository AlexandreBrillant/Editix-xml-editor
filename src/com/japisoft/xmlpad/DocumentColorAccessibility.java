package com.japisoft.xmlpad;

import java.awt.Color;
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
public interface DocumentColorAccessibility {

	/** Define a color for this tagName. Use a <code>null</code> color for removing it */
	public void setColorForTag( String tagName, Color c );

	/** @return a custom color for this tagName */ 
	public Color getColorForTag( String tagName );
	
	/** @return <code>true</code> if a custom color exists for this tagName */
	public boolean hasColorForTag( String tagName );	

	/** Choose a particular color for an attribute. Use the color <code>null</code> for removing it */
	public void setColorForAttribute( String attributeName, Color c );
		
	/** @return the user custom color for this attribute */
	public Color getColorForAttribute( String attributeName );

	/** @return <code>true</code> if this attribute has a custom color */ 
	public boolean hasColorForAttribute( String attributeName );
	
	/** Choose a particular color for a tag prefix. Use the color <code>null</code> for removing it */
	public void setColorForPrefix( String prefixName, Color c );
	
	/** @return a custom color for this prefix name */
	public Color getColorForPrefix( String prefixName );

	/** @return <code>true</code> if a color exist for this prefixName */
	public boolean hasColorForPrefix( String prefixName );	

	/** Choose a particular background color for a tag prefix. Use the color <code>null</code> for removing it */
	public void setBackgroundColorForPrefix( String prefixName, Color c );
	
	/** @return a custom background color for this prefix name */
	public Color getBackgroundColorForPrefix( String prefixName );

	/** @return <code>true</code> if a background color exist for this prefixName */
	public boolean hasBackgroundColorForPrefix( String prefixName );	

	/** This is only for inner usage, it mustn't be called by the user */
	void dispose();
	
}
