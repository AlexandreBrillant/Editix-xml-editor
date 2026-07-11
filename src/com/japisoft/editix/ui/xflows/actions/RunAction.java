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

package com.japisoft.editix.ui.xflows.actions;

import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

import com.japisoft.editix.ui.EditixDialog;
import com.japisoft.editix.ui.EditixFactory;
import com.japisoft.editix.ui.EditixFrame;
import com.japisoft.editix.ui.xflows.XFlowsEditor;
import com.japisoft.framework.dialog.DialogManager;
import com.japisoft.framework.toolkit.Logger;
import com.japisoft.xflows.LoggerModel;
import com.japisoft.xflows.task.Task;
import com.japisoft.xflows.task.TaskLogTable;
import com.japisoft.xflows.task.TaskManager;
import com.japisoft.xmlpad.IXMLPanel;

public class RunAction extends AbstractAction {
	
	public void actionPerformed( ActionEvent e ) {		
		IXMLPanel panel = EditixFrame.THIS.getSelectedPanel();
		
		if ( panel instanceof XFlowsEditor ) {

			XFlowsEditor xe = ( XFlowsEditor )panel;
			List<Task> tasks = xe.getTasks();

			if ( tasks != null ) {
				
				final List<Task> tasks2 = tasks;
				
				EditixDialog ed = new EditixDialog( 
						"XML Scenario", 
						"XML Scenario", 
						"Running " + tasks.size() + " task(s)" 
				);
				ed.getContentPane().add( new JScrollPane( new TaskLogTable() ) );
				ed.setSize( 300, 400 );
				ed.addWindowListener(
					new WindowAdapter() {						
						public void windowOpened(WindowEvent e) {
							TaskManager.run( tasks2, TaskManager.BACKGROUND );			
						};
					}
				);
				ed.setVisible( true );

			} else {
				EditixFactory.buildAndShowWarningDialog( "No task to run ?" );
			}
		} else {
			EditixFactory.buildAndShowWarningDialog( "Can't find a scenario ?" );
		}
	}
	
}
