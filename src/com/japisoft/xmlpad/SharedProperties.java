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

package com.japisoft.xmlpad;

import java.awt.Color;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import org.xml.sax.EntityResolver;

import com.japisoft.framework.app.toolkit.Toolkit;

/**
 * Set of common properties. In most of the case you don't have to
 * alter such properties. If you alter it, it must be done before using
 * the <code>XMLContainer</code>
 * @author Alexandre Brillant (https://github.com/AlexandreBrillant/Editix-xml-editor)
 * @version 1.0
 * */
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
			miniErrorIcon = (ImageIcon)Toolkit.getImageIcon( "images/bug.png" );
		return miniErrorIcon;
	}

	static Icon SYSTEM_ICON;
	static Icon ENTITY_ICON;

	/** Icon for content assistant */
	public static Icon getDefaultSystemHelperIcon() {
		if ( SYSTEM_ICON == null )
			SYSTEM_ICON = Toolkit.getImageIcon( "images/elements2.png" );
		return SYSTEM_ICON;
	}	

	/** Icon for content assistant */	
	public static Icon getDefaultEntityHelperIcon() {
		if ( ENTITY_ICON == null )
			ENTITY_ICON = Toolkit.getImageIcon( "images/element3.png" );
		return ENTITY_ICON;
	}	
	
}
