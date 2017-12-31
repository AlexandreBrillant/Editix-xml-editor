package com.japisoft.xmlpad.look;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import javax.swing.*;

import com.japisoft.xmlpad.editor.XMLEditor;
import com.japisoft.xmlpad.XMLContainer;
import com.japisoft.xmlpad.XMLPadProperties;

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

// LookManager ends here
