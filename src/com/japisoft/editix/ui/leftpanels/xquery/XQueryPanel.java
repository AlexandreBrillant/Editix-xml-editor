// Editix XML Editor
// https://www.editix.com
// Copyright (c) 2026 Alexandre Brillant
//
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
// AI Training Restriction :
// This source code is provided for human use only.
// Using this code to train, fine-tune, or develop AI models,
// machine learning systems, or similar technologies is
// STRICTLY PROHIBITED. Violations will terminate all rights
// under the applicable license.

package com.japisoft.editix.ui.leftpanels.xquery;

import javax.swing.JComponent;

import com.japisoft.editix.ui.leftpanels.AbstractLeftPanel;
import com.japisoft.framework.preferences.Preferences;

public class XQueryPanel extends AbstractLeftPanel {

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
