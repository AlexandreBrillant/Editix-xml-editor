package com.japisoft.xflows.task.ui.runner;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.util.ArrayList;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JScrollPane;

import com.japisoft.framework.dockable.InnerWindowProperties;
import com.japisoft.framework.dockable.JDock;
import com.japisoft.framework.dockable.action.ActionModel;
import com.japisoft.xflows.ApplicationComponent;
import com.japisoft.xflows.XFlowsApplicationModel;

import com.japisoft.xflows.task.Task;
import com.japisoft.xflows.task.TaskLogTable;
import com.japisoft.xflows.task.TaskManager;
import com.japisoft.xflows.task.TaskRunnerListener;
import com.japisoft.xflows.task.ui.XFlowsFactory;

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
public class ScenarioRunner extends JDock 
		implements 
			ApplicationComponent, 
			RunnerAction,
			TaskRunnerListener {

	private TasksPanel panel;
	private TaskLogTable log;
	
	public ScenarioRunner() {
		setLayout( new BorderLayout() );
		panel = new TasksPanel( this );
		panel.setPreferredSize( new Dimension( 100, 300 ) );
		
		addInnerWindow(
				new InnerWindowProperties(
				"tasks",
				"Scenario",
				panel ),
				BorderLayout.NORTH );

		addInnerWindow(
				new InnerWindowProperties(
				"logs",
				"Logs",
				new JScrollPane( log = new TaskLogTable() ) ), 
				BorderLayout.CENTER );
		
		ActionModel model = getInnerWindowActionsForId( "tasks" );
		model.addAction( ActionModel.SEPARATOR );
		panel.fillActionModel( model );

		model = getInnerWindowActionsForId( "logs" );
		model.addAction( ActionModel.SEPARATOR );
		model.addAction(
				new CleanLog() );
		
		TaskManager.setTaskRunnerListener( this );
	}

	public void run(Task t) {
		String name = t.getName();
		panel.table.selectTaskForName( name );
	}	
	
	public void setApplicationModel( XFlowsApplicationModel model ) {
		panel.table.setTasks( model.getTasks() );
	}

	public void stopEditing() {
	}	
	
	public void run() {
		log.clean();
		ArrayList taskList = XFlowsApplicationModel.ACCESSOR.getTasks();
		TaskManager.run( taskList, TaskManager.BACKGROUND );
	}	

	public void stop() {
		TaskManager.stopCurrentTask();
	}

	class CleanLog extends AbstractAction {
		public CleanLog() {
			putValue( Action.SHORT_DESCRIPTION, "Clean the logs" );
			putValue( Action.SMALL_ICON, XFlowsFactory.getImageIcon( "images/document_dirty.png" ) );
		}

		public void actionPerformed( ActionEvent e ) {
			log.clean();
		}
	}
	
}
