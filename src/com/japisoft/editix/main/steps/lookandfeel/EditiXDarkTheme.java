// Editix XML Editor
// https://www.editix.com
// Copyright (c) 2025 Alexandre Brillant
// 
// For non-commercial usage :
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
// For commercial use or integration into proprietary software :
// A commercial license is required. Visit https://www.editix.com for details.

package com.japisoft.editix.main.steps.lookandfeel;

import java.awt.Color;
import java.awt.Font;

import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.FontUIResource;

import com.japisoft.framework.preferences.Preferences;
import com.jgoodies.looks.plastic.theme.Silver;

public class EditiXDarkTheme extends Silver {

	public static String DEFAULT_RGB_BACKGROUND = "1e1e1e";
	public static String DEFAULT_RGB_FOREGROUND = "c8c8c8";
	public static String DEFAULT_RGB_SELECTIONBACKGROUND = "0078d4";
	public static String DEFAULT_RGB_SELECTIONFOREGROUND = "FFFFFF";

	public static ColorUIResource DEFAULT_BACKGROUND = new ColorUIResource( new Color( Integer.parseInt( DEFAULT_RGB_BACKGROUND, 16 ) ) );
	public static ColorUIResource DEFAULT_FOREGROUND = new ColorUIResource( new Color( Integer.parseInt( DEFAULT_RGB_FOREGROUND, 16 ) ) );
	public static ColorUIResource DEFAULT_SELECTIONBACKGROUND = new ColorUIResource( new Color( Integer.parseInt( DEFAULT_RGB_SELECTIONBACKGROUND, 16) ) );
	public static ColorUIResource DEFAULT_SELECTIONFOREGROUND = new ColorUIResource( new Color( Integer.parseInt( DEFAULT_RGB_SELECTIONFOREGROUND, 16) ) );
	public static ColorUIResource DEFAULT_ERROR_FOREGROUND = new ColorUIResource( new Color( 200, 100, 100 ) );

	////////////////////////////////////////////////////////////////////////////////////
	
	public static ColorUIResource DEBUG_COLOR = new ColorUIResource( 255, 0, 0 );
		
	// public static Color BACKGROUND_SELECTION = new Color( Integer.parseInt( "333333", 16 ) );
	
	public static Color BACKGROUND_SELECTION = EditiXLookAndFeel.DARK_BLACK;
	
	// Background selection
	public static final ColorUIResource primary2 = DEFAULT_SELECTIONBACKGROUND;
	
	// Bordure externe
	private final ColorUIResource primary1	= new ColorUIResource(106,106,106);
	
	// Bouton de scrolling + Ligne arbre
	private final ColorUIResource primary3	= new ColorUIResource(200,200,200);

	// Border externe
	private final ColorUIResource secondary1	= primary1;
	
	// Zone gris�e + Separateur
	private final ColorUIResource secondary2	= new ColorUIResource(180,180,180);
	
	// Couleur de fond des panneaux
	private final ColorUIResource secondary3	= new ColorUIResource( Integer.parseInt( "252526", 16 ) );

	@Override
	public ColorUIResource getMenuBackground() {
		return secondary3;
	}
	
	@Override
	public ColorUIResource getMenuItemBackground() {
		return secondary3;
	}

	@Override
	public ColorUIResource getMenuForeground() {
		return DEFAULT_FOREGROUND;
	}
	
	@Override
	public ColorUIResource getMenuItemSelectedBackground() {
		return primary2;
	}

	@Override
	public ColorUIResource getMenuSelectedBackground() {
		return primary2;
	}
	
	@Override
	public ColorUIResource getUserTextColor() {
		return DEFAULT_FOREGROUND;
	}
	
	@Override
	public ColorUIResource getControlTextColor() {
		return DEFAULT_FOREGROUND;
	}
	
	@Override
	public ColorUIResource getSystemTextColor() {
		return DEFAULT_FOREGROUND;
	}
	
	protected ColorUIResource getPrimary1()		{ return primary1; }
	protected ColorUIResource getPrimary2()		{ return primary2; }
	protected ColorUIResource getPrimary3()		{ return primary3; }

	protected ColorUIResource getSecondary1()		{ return secondary1; }
	protected ColorUIResource getSecondary2()		{ return secondary2; }
	protected ColorUIResource getSecondary3()		{ return secondary3; }
	
	public ColorUIResource getTitleTextColor()	{ return primary1; }
	
	@Override
	public ColorUIResource getFocusColor() {
		return secondary2;
	}	

	private FontUIResource defaultFont = null;
	
	@Override
	protected FontUIResource getFont() {
		if ( defaultFont == null ) {
			Font tmp = super.getFont();
			defaultFont = new FontUIResource( Preferences.getPreference( "interface", "defaultFont", new Font( Font.SANS_SERIF, tmp.getStyle(), tmp.getSize() ) ) );
		}
		if ( defaultFont != null )
			return defaultFont;
		else
			return super.getFont();
	}
	
}

