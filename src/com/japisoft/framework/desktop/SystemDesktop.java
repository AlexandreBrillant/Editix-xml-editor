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

package com.japisoft.framework.desktop;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.URI;

public class SystemDesktop {

	public static void openExplorer( File f ) throws IOException {
		if ( f.isFile() )
			f = f.getParentFile();
		Desktop desktop = Desktop.getDesktop();
		if ( desktop != null )
			desktop.open( f );
	}
	
	public static void openBrowser( URI uri ) throws IOException {
		Desktop desktop = Desktop.getDesktop();
		if ( desktop != null ) {
			desktop.browse( uri );
		}
	}
	
}

