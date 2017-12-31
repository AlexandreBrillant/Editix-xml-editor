package com.japisoft.xflows.task.ui.builder;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.japisoft.framework.dockable.InnerWindowProperties;
import com.japisoft.framework.dockable.JDock;
import com.japisoft.framework.dockable.action.ActionModel;
import com.japisoft.framework.ui.InitPanel;
import com.japisoft.xflows.ApplicationComponent;
import com.japisoft.xflows.XFlowsApplicationModel;
import com.japisoft.xflows.task.Task;
import com.japisoft.xflows.task.TaskElementFactory;
import com.japisoft.xflows.task.TaskParams;
import com.japisoft.xflows.task.TaskUI;
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
public class ScenarioBuilder extends JDock 
	implements 
		TaskTypeListener,
		ApplicationComponent {

	public ScenarioBuilder() {
		super();
		init();
	}
	
	private TaskUIPanel parameters;
	private TasksPanel panel;

	public List<Task> getTasks() {
		return panel.getTasks();
	}
	
	public void setTasks( List<Task> tasks ) {
		panel.setTasks( tasks );
	}
	
	private void init() {
		setLayout( new BorderLayout() );
		panel = new TasksPanel( this );
		panel.setPreferredSize( new Dimension( 100, 250 ) );

		addInnerWindow(
				new InnerWindowProperties(
				"tasks",
				"Tasks",
				panel ),
				BorderLayout.NORTH );

		addInnerWindow(
				new InnerWindowProperties(
				"parameters",
				"Parameters",
				parameters = new TaskUIPanel() ), 
				BorderLayout.CENTER );

		ActionModel model = getInnerWindowActionsForId( "tasks" );
		model.addAction( ActionModel.SEPARATOR );
		panel.fillActionModel( model );
		
		model = getInnerWindowActionsForId( "parameters" );
		model.addAction( ActionModel.SEPARATOR );
		model.addAction( new CopyAction() );
		model.addAction( paste = new PasteAction() );
		paste.setEnabled( false );		
	}

	private PasteAction paste = null;
	
	public void setApplicationModel( XFlowsApplicationModel model ) {
		panel.table.setTasks( model.getTasks() );
		panel.table.getSelectionModel().setSelectionInterval( 0, 0 );		
	}	

	public void updateCurrentParams() {
		if ( parameters.oldTask != null )
			parameters.oldTask.stop();
	}
	
	public void update() {
		updateCurrentParams();
	}
	
	public void stopEditing() {
		panel.table.stopEditing();
	}
	
	public void showUI( Task t ) {
		parameters.update( t );
	}

	public void print( Printable renderer ) throws PrinterException {
		renderer.print( parameters.getGraphics(), new PageFormat(), 0 );
	}
	
	class TaskUIPanel extends JPanel {
		private JComponent label = null;
		
		private TaskUIPanel() {
			setLayout( new BorderLayout() );

			add( label = 
					new JLabel( 
						"No parameters. Please select a task with an action" 
			) );
		}

		private TaskUI oldTask = null;

		public void update( Task t ) {
			if ( t == null || t.getType() == null ) {
				if ( !( getComponentCount() == 1 && 
						getComponent( 0 ) == label ) ) {
					removeAll();
					add( label );
					invalidate();
					validate();
					repaint();					
				} 
			} else
			if ( t.getType() != null ) {
				try {
					TaskUI ui = TaskElementFactory.getUIForType( t.getType() );
					if ( oldTask != null )
						oldTask.stop();
					removeAll();
					add( ui.getView() );
					invalidate();
					validate();
					repaint();
					ui.start();
					ui.setParams( t.getParams() );
					oldTask = ui;
				} catch( Throwable th ) {
					XFlowsApplicationModel.debug( th );
					XFlowsFactory.buildAndShowErrorDialog( "XFlows has met an error using this task " + th.getMessage() );
				}
			}
		}
	}

	private TaskParams currentCopy = null;

	/** Copy the parameters */
	class CopyAction extends AbstractAction {
		public CopyAction() {
			putValue( Action.SHORT_DESCRIPTION, "Copy the parameters" );
			putValue( Action.SMALL_ICON,  XFlowsFactory.getImageIcon( "images/copy.png" ) );
		}
		
		public void actionPerformed( ActionEvent e ) {
			Task t = panel.table.getCurrentTask();
			if ( t != null ) {
				currentCopy = t.getParams().cloneParams();
				paste.setEnabled( true );
			}
		}
	}

	/** Paste the parameters */
	class PasteAction extends AbstractAction {
		public PasteAction() {
			putValue( Action.SHORT_DESCRIPTION, "Paste the parameters" );
			putValue( Action.SMALL_ICON,  XFlowsFactory.getImageIcon( "images/paste.png" ) );			
		}

		public void actionPerformed( ActionEvent e ) {
			if ( currentCopy != null ) {
				Task t = panel.table.getCurrentTask();
				if ( t != null ) {
					t.setParams( currentCopy );
					panel.table.valueChanged( null );
				}
			}
		}
	}
}
