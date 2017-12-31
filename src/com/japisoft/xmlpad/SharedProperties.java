package com.japisoft.xmlpad;

import java.awt.Color;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import org.xml.sax.EntityResolver;

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
public class SharedProperties {

	/** Line number color for the selected node */
	public static Color LINE_NUMBER_COLOR_SELECTED = Color.DARK_GRAY;

	/** Line number color for the non selected part */
	public static Color LINE_NUMBER_COLOR = new Color(150, 150, 150);
		
	/** This is the maximum visible characters in the tree for text and attribute value*/
	public static int VISIBLE_TREENODE_TEXTE = 15;

	/** Use the resource bundle for finding the localized message. 
	 * Disable it will improve the loading performance. By default <code>true</code> */
	public static boolean LOCALIZED_MESSAGE = true;

	/** Delay before an popup helper is shown in ms */
	public static int HELPER_DELAY = 400;
	
	/** This is a property for activating a line wrapping. By default <code>false</code>. It must be called before using the XMLContainer class */
	public static boolean WRAPPED_LINE = false;

	/** This is a property for switching to a full text view. It must be called before using the XMLContainer class */
	public static boolean FULL_TEXT_VIEW = true;
	
	/** This is a property for caching a SCHEMA inside the content assistant. By default <code>false</code> */
	public static boolean SCHEMA_CACHING = false;

	/** Entity resolver for various entities. Useful for XML catalogs */
	public static EntityResolver DEFAULT_ENTITY_RESOLVER = null;

	/** Property for activating/disactivacting tooltip for each tree node */
	public static boolean TOOLTIP_TREE = true;

	/** Property for defining the default directory when loading a file */
	public static String DEFAULT_LOAD_DIRECTORY = null;
	
	/** Highligh syntax errors inside the editor on-the-fly */
	public static boolean HIGHLIGHT_ERROR_ONTHEFLY = false;

	/** Maximal size for the entity name and value inside the helper */
	public static int MAX_ENTITY_VISIBLE_SIZE_HELPER = 10;
	
	/** Add an helper panel for each content assistant element */
	public static boolean HELPER_PANEL = true;

	private static ImageIcon miniErrorIcon = null;

	/** Display the line number beside the text */
	public static boolean EDITOR_LINE_NUMBER = true;
	
	/** @return a minimal icon superposed in the tree or column bar for each error */
	public static ImageIcon getBugLittleIcon() {
		if ( miniErrorIcon == null )
			miniErrorIcon = new ImageIcon( SharedProperties.class.getResource( "little_bug_red.png" ) );
		return miniErrorIcon;
	}

	static Icon SYSTEM_ICON;
	static Icon ENTITY_ICON;

	/** Icon for content assistant */
	public static Icon getDefaultSystemHelperIcon() {
		if ( SYSTEM_ICON == null )
			SYSTEM_ICON = 
				new ImageIcon(
						SharedProperties.class.getResource( 
							"element2.png" ) );
		return SYSTEM_ICON;
	}	

	/** Icon for content assistant */	
	public static Icon getDefaultEntityHelperIcon() {
		if ( ENTITY_ICON == null )
			ENTITY_ICON = 
				new ImageIcon(
						SharedProperties.class.getResource( 
							"element3.png" ) );
		return ENTITY_ICON;
	}	
	
}
