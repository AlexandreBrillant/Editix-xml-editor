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

package com.japisoft.editix.action.search.file;

import java.io.File;

import javax.swing.JComponent;

import com.japisoft.editix.ui.panels.AbstractPanel;
import com.japisoft.framework.ApplicationModel;

public class FileSearchPanel extends AbstractPanel {

	protected JComponent buildView() {
		return new FileSearchUI();
	}

	protected String getTitle() {
		return "File search";
	}

	public void stop() {
		FileSearchUI view = ( FileSearchUI )getView();
		if ( view.mustSave() ) {
			view.saveParameters( new File( ApplicationModel.getAppUserPath(), "filesearch.cfg" ) );
		}
	}

	protected void postShow() {
		File f = new File( ApplicationModel.getAppUserPath(), "filesearch.cfg" );
		FileSearchUI view = ( FileSearchUI )getView();
		if ( f.exists() ) {
			view.readParameters( f );
		}
	}

}

