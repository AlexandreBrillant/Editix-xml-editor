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

package com.japisoft.editix.main.steps.lookandfeel;

import java.awt.Color;
import java.awt.Font;

import javax.swing.JTree;
import javax.swing.UIManager;

import com.japisoft.framework.ApplicationModel;
import com.japisoft.framework.preferences.Preferences;
import com.japisoft.xmlpad.XMLContainer;
import com.japisoft.xmlpad.editor.XMLEditor;

public class EditixDarkLook extends EditixLook {

	@Override
	protected Color getDefaultTreeBackgroundColor() {
		return Preferences.getPreference( "tree", "background2", UIManager.getColor( "tree.background" ) );
	}
	
	@Override
	protected Color getDefaultTreeTextColor() {
		return Preferences.getPreference( "tree", "text2", EditiXDarkTheme.DEFAULT_FOREGROUND );
	}
	
	@Override
	protected Color getTreeSelectionBackgroundColor() {
		return Preferences.getPreference( "tree", "selectionbg2", EditiXDarkTheme.DEFAULT_SELECTIONBACKGROUND );
	}

	@Override
	protected Color getDefaultTreeSelectionColor() {
		return Preferences.getPreference( "tree", "selectiontext2", EditiXDarkTheme.DEFAULT_SELECTIONFOREGROUND );
	}
	
}

