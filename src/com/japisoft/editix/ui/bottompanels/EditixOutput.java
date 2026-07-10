// Editix XML Editor
// https://www.editix.com
// Copyright (c) 2026 Alexandre Brillant
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
// 
// AI Training Restriction :
// This source code is provided for human use only.
// Using this code to train, fine-tune, or develop AI models,
// machine learning systems, or similar technologies is
// STRICTLY PROHIBITED. Violations will terminate all rights
// under the applicable license.

package com.japisoft.editix.ui.bottompanels;

import javax.swing.JComponent;

import com.japisoft.framework.dialog.console.ConsolePanel;

public class EditixOutput extends ConsolePanel implements BottomPanel {

	public EditixOutput() {
		super();
	}

	@Override
	public String getTitle() {
		return "Output";
	}

	@Override
	public JComponent getView() {
		return this;
	}

	@Override
	public void activate() {
		initConsoleState();
	}

	@Override
	public void deactivate() {
		restoreConsoleState();
	}

}
