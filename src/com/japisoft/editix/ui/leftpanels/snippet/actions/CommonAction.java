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

package com.japisoft.editix.ui.leftpanels.snippet.actions;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractAction;

import com.japisoft.editix.ui.leftpanels.LeftPanel;
import com.japisoft.editix.ui.leftpanels.LeftPanelManager;
import com.japisoft.editix.ui.leftpanels.snippet.SnippetPanel;
import com.japisoft.framework.ApplicationModel;

abstract class CommonAction extends AbstractAction {
	
	static final String INSERT_CMD = "insert";	
	static final String REN_CMD = "ren";
	static final String DEL_CMD = "del";
	static final String ADDD_CMD = "addd";
	static final String ADDS_CMD = "add";
	static final String ANY_CMD = "any";	
	static final String HLP_CMD = "hlp";
	
	public void actionPerformed( ActionEvent e ) {
		LeftPanel p = LeftPanelManager.getPanelByAction( 
				"snippet" );
		if ( p == null ) {
			ApplicationModel.debug(
					"Wrong panel action : " + 
						SnippetPanel.class.getName() + 
							"???" );
		} else {
			if ( p.isShown() || 
					!mustBeInVisiblePanel() ) {
				Object view = p.getView();
				if ( view instanceof ActionListener ) {
					( ( ActionListener )view ).actionPerformed(
							new ActionEvent(
									e.getSource(),
									e.getID(),
									getActionCommand() ) );
				}
			}
		}
	}

	abstract String getActionCommand();

	public boolean mustBeInVisiblePanel() {
		return true;
	}

}

