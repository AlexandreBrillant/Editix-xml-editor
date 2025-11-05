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

import javax.swing.plaf.ColorUIResource;
import com.jgoodies.looks.plastic.theme.Silver;

/*
* Note
* - PlasticLookAndFeel can be found at https://www.jgoodies.com, EditiX uses an old version under BSD open source license
*   This look and feel isn’t really useful, so it should be remove it later...
* @author Alexandre Brillant (https://github.com/AlexandreBrillant/Editix-xml-editor)
*/
public class EditiXPlasticTheme extends Silver {

	// public static Color BACKGROUND_SELECTION = new Color( Integer.parseInt( "333333", 16 ) );
	
	public static Color BACKGROUND_SELECTION = EditiXLookAndFeel.DARK_BLACK;
	
	// ??
	private final ColorUIResource primary2	= new ColorUIResource( EditiXLookAndFeel.LIGHT_BLACK );	
	
	
	// Bordure externe
	private final ColorUIResource primary1	= new ColorUIResource(106,106,106);
	
	// Bouton de scrolling + Ligne arbre
	private final ColorUIResource primary3	= new ColorUIResource(200,200,200);

	// Border externe
	private final ColorUIResource secondary1	= primary1;
	
	// Zone gris�e + Separateur
	private final ColorUIResource secondary2	= new ColorUIResource(180,180,180);
	// Couleur de fond des panneaux
	private final ColorUIResource secondary3	= new ColorUIResource( Integer.parseInt( "E5E5E5", 16 ) );
	
	// Menu
	private final ColorUIResource menu = new ColorUIResource( 255,255,255 ); 

	@Override
	public ColorUIResource getMenuBackground() {
		return secondary3;
	}

	@Override
	public ColorUIResource getMenuItemSelectedBackground() {
		return primary2;
	}

	@Override
	public ColorUIResource getMenuSelectedBackground() {
		return primary2;
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
	
}

