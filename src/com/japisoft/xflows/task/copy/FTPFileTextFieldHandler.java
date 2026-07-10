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

package com.japisoft.xflows.task.copy;

import com.japisoft.editix.ui.EditixFrame;
import com.japisoft.editix.ui.leftpanels.universalbrowser.FTPChooserPanel;
import com.japisoft.editix.ui.leftpanels.universalbrowser.FTPConfig;
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

