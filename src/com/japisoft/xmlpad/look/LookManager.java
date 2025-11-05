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

package com.japisoft.xmlpad.look;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import javax.swing.*;

import com.japisoft.xmlpad.editor.XMLEditor;
import com.japisoft.xmlpad.XMLContainer;
import com.japisoft.xmlpad.XMLPadProperties;

/**
 * Supported Look for the XMLEditor
 * @author Alexandre Brillant (https://github.com/AlexandreBrillant/Editix-xml-editor)
 * @version 1.0
 * @see com.japisoft.xmleditor.XMLEditor
 */
public final class LookManager {

	private static Look _look;

	static {
		try {
			_look =
				(Look) (Class
					.forName(
						XMLPadProperties.getProperty("look", "XMLPadLook"))
					.newInstance());
		} catch (Throwable th) {
			_look = new XMLPadLook();
		}
	}

	/** Set the current look. This action should be done before the
	 *  XMLEditor instantiation. Or call the <code>install</code> method
	 * with the tied editor */
	public static void setCurrentLook(Look look) {
		_look = look;
	}

	/** @return the current editor look */
	public static Look getCurrentLook() {
		return _look;
	}

	/** Install a look for the above editor */
	public static void install( XMLContainer container, XMLEditor editor) {
		if (_look != null) {
			_look.install( container, editor);
		}
	}

	/** Set the share tree view */
	public static void install( XMLContainer container, JTree tree) {
		if (_look != null) {
			_look.install( container, tree);
		}
	}

}


