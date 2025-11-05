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

package com.japisoft.editix.action.xml;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.Action;
import javax.swing.Icon;

import com.japisoft.editix.ui.EditixFrame;
import com.japisoft.editix.ui.EditixStatusBar;
import com.japisoft.framework.actions.SynchronizableAction;
import com.japisoft.framework.preferences.Preferences;
import com.japisoft.xmlpad.XMLContainer;

/**
 * @author Alexandre Brillant (https://github.com/AlexandreBrillant/Editix-xml-editor)
 * @version 1.0 */
public class DisabledEnabledBackgroundValidationAction extends AbstractAction {
	
	public void actionPerformed( ActionEvent e ) {
		Preferences.setPreference( "xmlconfig", "backgroundValidation", ( ( AbstractButton )e.getSource() ).isSelected() );
		if ( ( ( AbstractButton )e.getSource() ).isSelected() ) {
			( ( AbstractButton )e.getSource() ).setIcon(com.japisoft.framework.app.toolkit.Toolkit
					.getImageIcon( "images/check.png" ) );
		} else {
			( ( AbstractButton )e.getSource() ).setIcon( null );
		}
	}

}

