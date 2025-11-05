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

package com.japisoft.xflows.action.options.tasks;

import java.awt.event.ActionEvent;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;

import javax.swing.AbstractAction;

import com.japisoft.framework.ApplicationModel;
import com.japisoft.framework.dialog.DialogManager;
import com.japisoft.framework.xml.parser.node.FPNode;
import com.japisoft.xflows.task.ui.XFlowsFactory;

/**
 * @author Alexandre Brillant (https://github.com/AlexandreBrillant/Editix-xml-editor)
 * @version 1.0
 * */
public class TaskConfigAction extends AbstractAction {

	public void actionPerformed( ActionEvent e ) {
		
		TasksConfigPanel panel = new TasksConfigPanel(); 
		
		if ( DialogManager.showDialog(
				ApplicationModel.MAIN_FRAME,
				"Tasks manager",
				"Tasks manager",
				"Manage your own XFlows Task here",
				null,
				panel ) ==
					DialogManager.OK_ID ) {

			FPNode node = panel.getRootNode();
			URL documentUrl = ClassLoader.getSystemClassLoader().getResource( "tasks.xml" );

			try {
				String fileName = documentUrl.toExternalForm();
				fileName = fileName.replaceAll("%20", " ");
				fileName = fileName.substring(5);				
				node.getDocument().write( new FileOutputStream( fileName ) );
				XFlowsFactory.buildAndShowInformationDialog( "Restart XFlows for using the new tasks" );
			} catch (IOException e1) {
				XFlowsFactory.buildAndShowErrorDialog( "Can't write tasks.xml : " + e1.getMessage() );
			}
		}
	}

}

