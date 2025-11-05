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

package com.japisoft.universalbrowser;

import javax.swing.Icon;
import org.apache.commons.vfs.FileObject;

/**
 * Here a file filter
 * @author Alexandre Brillant (https://github.com/AlexandreBrillant/Editix-xml-editor)
 * @version 1.0
 * */
public interface FileFilter {
	/** @return the file filter goal like "Text files (*.txt)" */
	public String getDescription();
	/** @return an icon for this file filter. It can return <code>null</code> */
	public Icon getIcon();
	/** @return true if this file is not filtered */
	public boolean accept( FileObject file );
}

