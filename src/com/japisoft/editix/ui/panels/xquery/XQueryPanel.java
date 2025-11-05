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

package com.japisoft.editix.ui.panels.xquery;

import javax.swing.JComponent;
import com.japisoft.editix.ui.panels.AbstractPanel;
import com.japisoft.framework.preferences.Preferences;

public class XQueryPanel extends AbstractPanel {

	protected JComponent buildView() {
		return new XQueryUI( 
			Preferences.getPreference( Preferences.SYSTEM_GP, "xquery.cbxmloutput", false ),
			Preferences.getPreference( Preferences.SYSTEM_GP, "xquery.cbopeneditor", false ) );
	}

	protected String getTitle() {
		return "XQuery";
	}

	public void stop() {
		Preferences.setPreference(
				Preferences.SYSTEM_GP, 
				"xquery.cbxmloutput", 
				( ( XQueryUI )getView() ).cbXMLOutput.isSelected() );
		Preferences.setPreference(
				Preferences.SYSTEM_GP, 
				"xquery.cbopeneditor", 
				( ( XQueryUI )getView() ).cbOpenEditor.isSelected() );
	}

}

