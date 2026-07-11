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

package com.japisoft.xflows.task.ui.runner;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;

import com.japisoft.framework.dockable.action.ActionModel;
import com.japisoft.xflows.task.TaskTable;
import com.japisoft.xflows.task.ui.XFlowsFactory;

/**
 * @author Alexandre Brillant (https://github.com/AlexandreBrillant/Editix-xml-editor)
 * @version 1.0
 * */
public class TasksPanel extends JPanel {

	private RunnerAction action;
	
	public TasksPanel( RunnerAction action ) {
		super();
		init();
		this.action = action;
	}

	TaskTable table;

	private void init() {
		setLayout( new BorderLayout() );
		add( new JScrollPane( 
				table = new TaskTable( null, false ) ), 
				BorderLayout.CENTER );
/*JToolBar tb = new JToolBar();
		add( tb, BorderLayout.NORTH );
		tb.add( new RunAction() );
		tb.add( stopAction = new StopAction() ); */
	}

	void fillActionModel( ActionModel model ) {
		model.addAction( new StopAction() );
		model.addAction( new RunAction() );
	}

	Action stopAction = null;

	//////////////////////////////////////////////////////////
	
	class RunAction extends AbstractAction {
		public RunAction() {
			super();
			//putValue( Action.NAME, "Run" );
			putValue( Action.SMALL_ICON, XFlowsFactory.getImageIcon( "images/element_run.png" ) );
			putValue( Action.SHORT_DESCRIPTION, "Run the scenario" );
		}
		public void actionPerformed( ActionEvent e ) {
			SwingUtilities.invokeLater( 
					new Runnable() {
						public void run() {
							action.run();
					}
			} );
		}
	}

	class StopAction extends AbstractAction {
		public StopAction() {
			super();
			//putValue( Action.NAME, "Stop" );
			putValue( Action.SMALL_ICON, XFlowsFactory.getImageIcon( "images/element_stop.png" ) );
			putValue( Action.SHORT_DESCRIPTION, "Stop the scenario" );
		}

		public void actionPerformed( ActionEvent e ) {
			action.stop();
		}
	}

}
