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

package com.japisoft.xmlform.editor.actions.file;

import java.awt.event.ActionEvent;

import com.japisoft.framework.internationalization.Traductor;
import com.japisoft.framework.preferences.Preferences;
import com.japisoft.xmlform.UIToolkit;
import com.japisoft.xmlform.editor.actions.CommonAction;

public class QuitAction extends CommonAction {

	@Override
	public void actionPerformed2(ActionEvent e) {

		if ( UIToolkit.confirm( 
				Traductor.traduce( "leave",
					"Leave the XML Form Editor ?" ) ) ) {
			Preferences.savePreferences();
			System.exit( 0 );
		}

	}

}

