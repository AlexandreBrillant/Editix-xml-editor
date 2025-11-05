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

package com.japisoft.editix.ui;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JRadioButtonMenuItem;

import com.japisoft.framework.application.descriptor.helpers.MenuBuilderDelegate;
import com.japisoft.framework.preferences.Preferences;
import com.japisoft.framework.xml.Encoding;

public class XMLSpaceBuilder implements MenuBuilderDelegate {

	public void build(JMenu menu) {
		
		menu.setEnabled( true );		
		ChangeXMLSpaceAction action = new ChangeXMLSpaceAction();
		JCheckBoxMenuItem item = new JCheckBoxMenuItem( action );				
		item.setSelected( Preferences.getPreference( "xmlconfig", "format-xml:space", true ) );

		menu.add( item );

	}

	class ChangeXMLSpaceAction extends AbstractAction {
		
		ChangeXMLSpaceAction() {
			putValue( Action.NAME, "automatic" );
		}
		public void actionPerformed(ActionEvent e) {
			Preferences.setPreference( "xmlconfig", "format-xml:space", ( ( JCheckBoxMenuItem )e.getSource() ).isSelected() );
			EditixFactory.buildAndShowInformationDialog( "Please restart EditiX for applying" );
		}
	}

}

