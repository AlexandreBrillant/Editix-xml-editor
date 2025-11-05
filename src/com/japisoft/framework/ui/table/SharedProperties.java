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

package com.japisoft.framework.ui.table;

import java.awt.Color;

/**
 * @author Alexandre Brillant (https://github.com/AlexandreBrillant/Editix-xml-editor)
 * @version 1.0 */
public final class SharedProperties {

	/** Background Color for a disabled row */
	public static Color DISABLED_BGCOLOR = new Color( 0, 0, 0 );

	/** Foreground Color for a disabled row */ 
	public static Color DISABLED_FGCOLOR = Color.LIGHT_GRAY;

	/** Color for a row with error */
	public static Color ERROR_BGCOLOR = new Color( 255, 200, 200 );

}

