package com.japisoft.editix.editor.xsd.view.designer;

import java.awt.Color;
import java.util.HashMap;

import com.japisoft.framework.preferences.Preferences;

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
public final class ColorFactory {
	
	static HashMap mapOfColor;
	static Color selectionForegroundColor;
	static Color selectionBackgroundColor;
	static Color foregroundColor;
	
	static {
		mapOfColor = new HashMap();
		mapOfColor.put( 
				"element",
				Preferences.getPreference(
						"xsdEditor", "element",
						new Color( 150, 255, 150 ) ) );
		mapOfColor.put( 
				"any",
				Preferences.getPreference(
						"xsdEditor", "any",
						new Color( 150, 255, 150 ) )
		);
		mapOfColor.put(
				"attribute",
				Preferences.getPreference(
						"xsdEditor", "attribute",
						new Color( 250, 250, 150 ) )
		);
		mapOfColor.put(
				"anyAttribute",
				Preferences.getPreference(
						"xsdEditor", "anyAttribute",
						new Color( 250, 250, 150 ) )	
		);
		mapOfColor.put(
				"attributeGroup",
				Preferences.getPreference(
						"xsdEditor", "attributeGroup",
						new Color( 220, 220, 120 ) ) );
		mapOfColor.put(
				"sequence",
				Preferences.getPreference(
						"xsdEditor", "sequence",
				new Color( 255, 150, 150 ) ) );
		mapOfColor.put(
				"all",
				Preferences.getPreference( 
						"xsdEditor", "all",
						new Color( 255, 150, 150 ) ) );
		mapOfColor.put(
				"choice",
				Preferences.getPreference(
						"xsdEditor", "choice",
						new Color( 255, 150, 150 ) ) );
		mapOfColor.put(
				"complexType",
				Preferences.getPreference(
						"xsdEditor", "complexType",
						new Color( 240, 240, 240 ) )	
		);
		mapOfColor.put(
				"complexContent",
				Preferences.getPreference(
						"xsdEditor", "complexContent",
						new Color( 230, 230, 230 ) )
		);
		mapOfColor.put(
				"simpleContent",
				Preferences.getPreference(
						"xsdEditor", "simpleContent",
						new Color( 220, 220, 220 ) )	
		);
		mapOfColor.put(
				"group",
				Preferences.getPreference(
						"xsdEditor", "group",
						new Color( 220, 220, 220 ) )
		);
		mapOfColor.put(
				"restriction",
				Preferences.getPreference(
						"xsdEditor", "restriction",
						new Color( 210, 210, 210 ) )	
		);
		mapOfColor.put(
				"extension",
				Preferences.getPreference(
						"xsdEditor", "extension",
						new Color( 210, 210, 210 ) )	
		);
		
		selectionBackgroundColor = 
			Preferences.getPreference(
					"xsdEditor", "selectionBackground",
					new Color( 180, 180, 255 ) );

		selectionForegroundColor = 
			Preferences.getPreference(
					"xsdEditor", "selectionForeground",
					Color.WHITE );
		
		foregroundColor = 
			Preferences.getPreference( 
					"xsdEditor", "foreground", 
					Color.BLACK );
	}

	static Color getForegroundColor( String elementName ) {
		return foregroundColor;
	}

	static Color getBackgroundColor( String elementName ) {
		Color c = ( Color )mapOfColor.get( elementName );
		if ( c != null )
			return c;
		return Color.WHITE;
	}

	static Color getSelectedBackgroundColor( String elementName ) {
		return selectionBackgroundColor;
	}

	static Color getSelectedForegroundColor( String elementName ) {
		return selectionForegroundColor;
	}
	
}
