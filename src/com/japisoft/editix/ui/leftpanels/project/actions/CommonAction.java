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

package com.japisoft.editix.ui.leftpanels.project.actions;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractAction;

import com.japisoft.editix.ui.EditixFactory;
import com.japisoft.editix.ui.leftpanels.LeftPanel;
import com.japisoft.editix.ui.leftpanels.LeftPanelManager;
import com.japisoft.editix.ui.leftpanels.project.ProjectPanel;
import com.japisoft.framework.ApplicationModel;

abstract class CommonAction extends AbstractAction {
	static final String OPEN_CMD = "open";
	static final String REN_CMD = "ren";
	static final String LOCK_CMD = "lock";
	static final String DEL_CMD = "del";
	static final String ADDD_CMD = "addd";
	static final String ADDF_CMD = "addf";
	static final String ADDF2_CMD = "addf2";	
	static final String CHECK_CMD = "check";
	static final String ADDFS_CMD = "addfs";
	
	public void actionPerformed( ActionEvent e ) {
		LeftPanel p = LeftPanelManager.getPanelByAction( 
				"projectManager" );
		if ( p == null ) {
			ApplicationModel.debug(
					"Wrong panel action : " + 
						ProjectPanel.class.getName() + 
							"???" );
		} else {
			if ( p.isShown() || !mustBeInVisiblePanel() ) {
				Object view = p.getView();
				if ( view instanceof ActionListener ) {
					( ( ActionListener )view ).actionPerformed(
							new ActionEvent(
									e.getSource(),
									e.getID(),
									getActionCommand() ) 
					);
				}
			} else {
				EditixFactory.buildAndShowWarningDialog( "Can't execute this action, please open or reopen the project panel" );
			}
		}
	}

	abstract String getActionCommand();

	public boolean mustBeInVisiblePanel() {
		return true;
	}
	
}
