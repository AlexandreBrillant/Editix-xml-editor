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

package com.japisoft.xmlpad.action.edit;

import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;

import com.japisoft.xmlpad.action.XMLAction;

/**
 * Copy the current File location
 * @author Alexandre Brillant (https://github.com/AlexandreBrillant/Editix-xml-editor)
 * @version 1.0 */
public class CopyFileLocation extends XMLAction {

	public static final String ID = CopyFileLocation.class.getName();
	public static boolean NORMALIZED_PATH = true;
	protected boolean autoRequestFocus() { return false; }

	public boolean notifyAction() {
		if ( container == null || 
				container.getCurrentDocumentLocation() == null )
			return INVALID_ACTION;
		try {
			
			String path = container.getCurrentDocumentLocation();
			if ( NORMALIZED_PATH )
				path = path.replace( '\\', '/' );
			
			Toolkit
				.getDefaultToolkit()
				.getSystemClipboard()
				.setContents(
					new StringSelection(
						path )
						, null );
			return VALID_ACTION;
		} catch (Throwable th) {}
		return INVALID_ACTION;
	}

}

