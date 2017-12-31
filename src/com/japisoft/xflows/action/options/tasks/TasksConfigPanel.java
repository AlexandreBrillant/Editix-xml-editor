package com.japisoft.xflows.action.options.tasks;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

import com.japisoft.framework.wizard.JWizard;
import com.japisoft.framework.xml.parser.node.FPNode;
import com.japisoft.xflows.action.options.tasks.wizard.NewTaskWizard;
import com.japisoft.xflows.task.TaskElementFactory;
import com.japisoft.xflows.task.ui.XFlowsDialog;
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
public class TasksConfigPanel extends JPanel 
	implements ListSelectionListener, ActionListener {
	
	JScrollPane spTableTasks = new JScrollPane();

	JTable tableTasks = new JTable() {
		public boolean isCellEditable(int row, int column) {
			return false;
		}
	};

	JButton btnAddTask = new JButton();
	JButton btnRemoveTask = new JButton();
	JLabel lblRunningCl = new JLabel();
	JTextField tfRunningCl = new JTextField();
	JLabel UICl = new JLabel();
	JTextField tfUICl = new JTextField();
	JLabel lblArchive = new JLabel();
	JTextField tfArchive = new JTextField();
	JButton btnCheckTask = new JButton();
	GridBagLayout gridBagLayout1 = new GridBagLayout();

	public TasksConfigPanel() {
		try {
			jbInit();
			
			tfRunningCl.setEnabled( false );
			tfUICl.setEnabled( false );
			tfArchive.setEnabled( false );
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void addNotify() {
		super.addNotify();
		prepare();
		tableTasks.getSelectionModel().addListSelectionListener( this );
		btnAddTask.addActionListener( this );
		btnRemoveTask.addActionListener( this );
		btnCheckTask.addActionListener( this );
		tableTasks.getSelectionModel().setSelectionInterval( 0, 0 );
	}

	public void removeNotify() {
		super.removeNotify();
		tableTasks.getSelectionModel().removeListSelectionListener( this );
		btnAddTask.removeActionListener( this );
		btnRemoveTask.removeActionListener( this );
		btnCheckTask.removeActionListener( this );
	}

	public void valueChanged( ListSelectionEvent e ) {
		int row = tableTasks.getSelectedRow();
		if ( row > -1 ) {
			String running = ( String )tableTasks.getModel().getValueAt( row, 2 );
			String ui = ( String )tableTasks.getModel().getValueAt( row, 3 );
			String def = ( String )tableTasks.getModel().getValueAt( row, 1 );
			String archive = ( String )tableTasks.getModel().getValueAt( row, 4 );
	
			btnRemoveTask.setEnabled( !"true".equals( def ) );
			btnCheckTask.setEnabled( !"true".equals( def ) );
	
			tfRunningCl.setText( running );
			tfUICl.setText( ui );
			tfArchive.setText( archive );
		}
	}

	public void actionPerformed( ActionEvent e ) {
		if ( e.getSource() == btnAddTask ) {
			addTask();
		} else
		if ( e.getSource() == btnRemoveTask ) {
			removeTask();
		} else
		if ( e.getSource() == btnCheckTask ) {
			checkTask();
		}
	}

	private void removeTask() {
		int row = tableTasks.getSelectedRow();
		((DefaultTableModel)tableTasks.getModel()).removeRow( row );
		rootNode.removeChildNodeAt( row );
	}

	private void checkTask() {
		TaskElementFactory.updateInnerNode( rootNode );
		int row = tableTasks.getSelectedRow();
		String type = ( String )tableTasks.getModel().getValueAt( row, 0 );
		try {
			TaskElementFactory.getRunnerForType( type );
			TaskElementFactory.getUIForType( type );
		} catch( Throwable th ) {
			XFlowsFactory.buildAndShowErrorDialog( "Error found : " + th.getMessage() );
		}
	}

	private void addTask() {
		NewTaskWizard wizard = new NewTaskWizard();
		XFlowsDialog dialog = new XFlowsDialog(
				"Wizard", 
				"New Task Wizard", 
				"Create a new XFlows Task" 
		);

		dialog.setUI( 
				wizard.getView().getView(), 
				true, 
				false 
		);
		
		dialog.setSize( 600, 500, true );

		if ( wizard.show( dialog, false ) == 
			JWizard.OK_ACTION ) {
			
			File archive = ( File )wizard.getWizardContext().getSharedData( "file" );
			String name = ( String )wizard.getWizardContext().getSharedData( "name" );
			String running = ( String )wizard.getWizardContext().getSharedData( "running" );
			String ui = ( String )wizard.getWizardContext().getSharedData( "ui" );
						
			if ( name != null && 
					running != null && 
						ui != null ) {

				FPNode node = new FPNode( FPNode.TAG_NODE, "task" );
				node.setAttribute( "name", name );
				node.setAttribute( "class", running );
				node.setAttribute( "ui", ui );
				node.setAttribute( "archive", archive.toString() );

				rootNode.appendChild( node );
				
				prepareModel();
			}
		}
	}

	private FPNode rootNode = null;

	FPNode getRootNode() { return rootNode; }
	
	private void prepare() {
		rootNode = TaskElementFactory.getNewDocumentRoot();
		prepareModel();
	}

	private void prepareModel() {
		DefaultTableModel model = new DefaultTableModel(
				new String[] {
						"Name",
						"Def",
						"Running class",
						"UI class",
						"Archive"
				}, 0 );

		tableTasks.setModel( model );		
		for ( int i = 0; i < rootNode.childCount(); i++ ) {
			FPNode node = rootNode.childAt( i );
			String name = node.getAttribute( "name" );
			String def = node.getAttribute( "system" );
			String runningClass = node.getAttribute( "class" );
			String uiClass = node.getAttribute( "ui" );
			String archive = node.getAttribute( "archive" );

			model.addRow(
					new Object[] {
							name,
							def,
							runningClass,
							uiClass,
							archive
					} );
		}
	}

	private void jbInit() throws Exception {
		this.setLayout(gridBagLayout1);
		btnAddTask.setText( "Add a task" );
		btnRemoveTask.setText( "Remove a task" );
		lblRunningCl.setText( "Running class" );
		UICl.setText( "UI Class" );
		lblArchive.setText( "Archive" );
		tfRunningCl.setText("");
		tfUICl.setText("");
		tfArchive.setText("");
		btnCheckTask.setText( "Check" );
		this.add(spTableTasks, new GridBagConstraints(0, 0, 4, 1, 1.0, 1.0,
				GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(
						5, 4, 0, 3), -4, -221));
		spTableTasks.getViewport().add(tableTasks, null);
		this.add(btnRemoveTask, new GridBagConstraints(2, 1, 1, 1, 0.0, 0.0,
				GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(
						6, 17, 0, 0), 0, 0));
		this.add(btnAddTask, new GridBagConstraints(0, 1, 2, 1, 0.0, 0.0,
				GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(
						6, 4, 0, 0), 0, 0));
		this.add(lblRunningCl, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(
						15, 4, 0, 0), 0, 0));
		this.add(tfRunningCl, new GridBagConstraints(1, 2, 3, 1, 1.0, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
				new Insets(13, 7, 0, 3), 363, 0));
		this.add(UICl, new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(
						15, 4, 0, 32), 0, 0));
		this.add(tfUICl, new GridBagConstraints(1, 3, 3, 1, 1.0, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
				new Insets(13, 7, 0, 3), 363, 0));
		this.add(lblArchive, new GridBagConstraints(0, 4, 1, 1, 0.0, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(
						16, 4, 13, 34), 0, 0));
		this.add(tfArchive, new GridBagConstraints(1, 4, 3, 1, 1.0, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
				new Insets(14, 7, 13, 3), 363, 0));
		this.add(btnCheckTask, new GridBagConstraints(3, 1, 1, 1, 0.0, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(6,
						17, 0, 0), 0, 0));
		
		tableTasks.getSelectionModel().setSelectionMode(
				ListSelectionModel.SINGLE_SELECTION );
	}

}
