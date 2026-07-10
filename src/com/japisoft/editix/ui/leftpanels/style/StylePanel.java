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

package com.japisoft.editix.ui.leftpanels.style;

import java.io.IOException;

import javax.swing.JComponent;

import com.japisoft.editix.ui.leftpanels.AbstractLeftPanel;
import com.japisoft.editix.ui.leftpanels.snippet.SnippetTool;
import com.japisoft.editix.ui.leftpanels.snippet.SnippetUI;
import com.japisoft.framework.ApplicationModel;

public class StylePanel extends AbstractLeftPanel {

	@Override
	protected JComponent buildView() {
		return new StyleUI();
	}

	@Override
	protected String getTitle() {
		return "Style library";
	}
	
	public void stop() {
		try {
			StyleTool.storeSnippetsFile();
		} catch (IOException e) {
			ApplicationModel.debug( e );
		}
	}

	public void init() {
		( ( StyleUI )getView() ).init();		
	}
	

}

