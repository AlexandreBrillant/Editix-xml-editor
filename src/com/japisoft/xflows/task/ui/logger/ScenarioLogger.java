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

package com.japisoft.xflows.task.ui.logger;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import com.japisoft.framework.dockable.InnerWindowProperties;
import com.japisoft.framework.dockable.JDock;
import com.japisoft.framework.dockable.action.ActionModel;
import com.japisoft.xflows.ApplicationComponent;
import com.japisoft.xflows.LoggerModel;
import com.japisoft.xflows.XFlowsApplicationModel;
import com.japisoft.xflows.task.LoggerListenerFileProducer;
import com.japisoft.xflows.task.TaskLogTable;
import com.japisoft.xflows.task.ui.XFlowsFactory;

/**
 * @author Alexandre Brillant (https://github.com/AlexandreBrillant/Editix-xml-editor)
 * @version 1.0 */
public class ScenarioLogger extends JDock 
		implements ApplicationComponent {

	private File currentLogFile = null;

	public void setApplicationModel( XFlowsApplicationModel model ) {
		log.synchroUI( model.getLogger() );
		mail.synchroUI( model.getLogger() );
		table.clean();
		currentLogFile = null;
	}

	public void stopEditing() {}	

	public ScenarioLogger() {
		init();
	}
	
	FileLogPanel log;
	MailLogPanel mail;
	TaskLogTable table = null;
	
	private void init() {
		setLayout( new BorderLayout() );
		JPanel panel = new FileLogPanel();
		panel.setPreferredSize( new Dimension( 100, 200 ) );

		addInnerWindow(
				new InnerWindowProperties(
				"logs",
				"File Logs",
				panel ),
				BorderLayout.NORTH);

		log = ( FileLogPanel )panel;
		
		addInnerWindow(
				new InnerWindowProperties(
				"mails",
				"Mail Logs",
				panel = new MailLogPanel() ), 
				BorderLayout.CENTER );

		mail = ( MailLogPanel )panel;

		panel.setPreferredSize( new Dimension( 100, 200 ) );
		
		
		JScrollPane sp = null;
		
		addInnerWindow(
				new InnerWindowProperties(
				"logstable",
				"Logs Reader",
				sp = new JScrollPane( table = new TaskLogTable() ) ), 
				BorderLayout.SOUTH );
		ActionModel model = getInnerWindowActionsForId( "logstable" );
		model.addAction( ActionModel.SEPARATOR );
		model.addAction( new ReadLog( 2 ) );
		model.addAction( new ReadLog( 1 ) );
		model.addAction( new ReadLog( 0 ) );		
		model.addAction( ActionModel.SEPARATOR );
		model.addAction( new DeleteLog() );
		sp.setPreferredSize( new Dimension( 100, 150 ) );
	}

	public LoggerModel getModel() {
		LoggerModel model = new LoggerModel();
		log.synchroModel( model );
		mail.synchroModel( model );
		return model;
	}

	private void readInfoLog() {
		LoggerModel model = XFlowsApplicationModel.ACCESSOR.getLogger();
		if ( model.getFileLogInfo() != null ) {
			File f = new File( model.getFileLogInfo() );
			if ( !f.exists() ) {
				XFlowsFactory.buildAndShowErrorDialog( "No info log found" );
			} else {
				currentLogFile = f;
				new LoggerListenerFileProducer( f, table );
			}			
		}
	}

	private void readWarningLog() {
		LoggerModel model = XFlowsApplicationModel.ACCESSOR.getLogger();
		if ( model.getFileLogWarning() != null ) {
			File f = new File( model.getFileLogWarning() );
			if ( !f.exists() ) {
				XFlowsFactory.buildAndShowErrorDialog( "No warning log found" );
			} else {
				currentLogFile = f;
				new LoggerListenerFileProducer( f, table );
			}			
		}		
	}

	private void readErrorLog() {
		LoggerModel model = XFlowsApplicationModel.ACCESSOR.getLogger();
		if ( model.getFileLogError() != null ) {
			File f = new File( model.getFileLogError() );
			if ( !f.exists() ) {
				XFlowsFactory.buildAndShowErrorDialog( "No error log found" );
			} else {
				currentLogFile = f;
				new LoggerListenerFileProducer( f, table );
			}			
		}		
	}

	class DeleteLog extends AbstractAction {

		public DeleteLog() {
			putValue( Action.SHORT_DESCRIPTION, "Delete logs" );
			putValue( Action.SMALL_ICON, XFlowsFactory.getImageIcon( "images/delete2.png" ) ); 
		}

		public void actionPerformed( ActionEvent e ) {
			if ( currentLogFile != null ) {
				if ( XFlowsFactory.buildAndShowChoiceDialog( "Delete " + currentLogFile + " ?" ) == JOptionPane.YES_OPTION ) {
					currentLogFile.delete();
					table.clean();
				}
			}
		}
	}

	class ReadLog extends AbstractAction {
		
		private int type;
		
		public ReadLog( int type ) {
			this.type = type;
			if ( type == 0 ) {
				putValue( Action.SHORT_DESCRIPTION, "Read the last info log" );
				putValue( Action.SMALL_ICON, XFlowsFactory.getImageIcon( "images/bug_green.png" ) );
			} else
			if ( type == 1 ) {
				putValue( Action.SHORT_DESCRIPTION, "Read the last warning log" );
				putValue( Action.SMALL_ICON, XFlowsFactory.getImageIcon( "images/bug_yellow.png" ) );				
			} else
			if ( type == 2 ) {
				putValue( Action.SHORT_DESCRIPTION, "Read the last error log" );
				putValue( Action.SMALL_ICON, XFlowsFactory.getImageIcon( "images/bug_red.png" ) );				
			}
		}

		public void actionPerformed( ActionEvent e ) {
			table.clean();
			if ( type == 0 ) {
				readInfoLog();
			} else
			if ( type == 1 ) {
				readWarningLog();
			} else
			if ( type == 2 ) {
				readErrorLog();
			}
		}
	}

}

