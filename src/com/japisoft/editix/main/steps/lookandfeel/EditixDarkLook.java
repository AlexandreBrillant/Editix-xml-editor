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
