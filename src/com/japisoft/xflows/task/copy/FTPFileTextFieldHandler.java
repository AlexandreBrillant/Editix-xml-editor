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

package com.japisoft.xflows.task.copy;

import com.japisoft.editix.ui.leftpanels.universalbrowser.FTPChooserPanel;
import com.japisoft.editix.ui.leftpanels.universalbrowser.FTPConfig;
import com.japisoft.editix.ui.windows.EditixFrame;
import com.japisoft.framework.dialog.DialogManager;
import com.japisoft.framework.ui.text.FileTextField;
import com.japisoft.framework.ui.text.FileTextFieldHandler;

/**
 * @author Alexandre Brillant (https://github.com/AlexandreBrillant/Editix-xml-editor)
 * @version 1.0
 * */
public class FTPFileTextFieldHandler implements FileTextFieldHandler {

	public static final String HOST = "ftphost";
	public static final String PASSWORD = "ftppassword";
	public static final String USER = "ftpuser";

	public String selectResource(
			FileTextField source,
			String previousPath,
			boolean directoryMode,
			boolean fileMode,
			boolean openedMode,
			String[] fileExt,
			String currentDir ) {
		
		FTPChooserPanel fcp = new FTPChooserPanel();
		if ( DialogManager.showDialog( 
				EditixFrame.THIS, 
				"FTP Configuration", 
				"FTP Configuration", 
				"Choose your FTP parameters", 
				null, 
				fcp 
		) == DialogManager.OK_ID ) {
			FTPConfig config = fcp.getFTPConfig();
			String tmp = config.host;
			if ( config.user != null ) {
				tmp = config.user + ":" + config.password + "@" + tmp;
			}
			if ( config.directory != null )
				tmp = tmp + "/" + config.directory;
			return "ftp://" + tmp;
		}

		return null;
	}

	public void createResource( String path ) {
	}

	public void deleteResource( String path ) {
	}

	public boolean isCreateResourceManaged() {
		return false;
	}
	
	public boolean isDeleteResourceManaged() {
		return false;
	}	
	
}
