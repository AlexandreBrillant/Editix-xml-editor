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

/**
This program is available under two licenses : 

1. For non commercial usage : 

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.

2. For commercial usage :

You need to get a commercial license for source usage at : 

http://www.editix.com/buy.html

Copyright (c) 2018 Alexandre Brillant - JAPISOFT SARL - http://www.japisoft.com

@author Alexandre Brillant - abrillant@japisoft.com
@author JAPISOFT SARL - http://www.japisoft.com

*/
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
