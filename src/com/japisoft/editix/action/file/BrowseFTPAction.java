package com.japisoft.editix.action.file;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Properties;

import com.japisoft.editix.action.panels.PanelAction;
import com.japisoft.editix.ui.EditixFrame;
import com.japisoft.editix.ui.panels.universalbrowser.FTPChooserPanel;
import com.japisoft.editix.ui.panels.universalbrowser.FTPConfig;
import com.japisoft.editix.ui.panels.universalbrowser.CommonUniversalBrowserPanel;
import com.japisoft.framework.ApplicationModel;
import com.japisoft.framework.dialog.DialogManager;

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
public class BrowseFTPAction extends PanelAction {
	
	public void actionPerformed(ActionEvent e) {
		
		FTPChooserPanel panel = new FTPChooserPanel();
		panel.setPreferredSize( new Dimension( 300, 300 ) );
		
 		Properties prop = new Properties();
		File workingPath = ApplicationModel.getAppUserPath();
		File fConfig = new File( workingPath, "ftp.cfg" );

		if ( fConfig.exists() ) {
			try {
				prop.load( new FileInputStream( fConfig ) );
			} catch (FileNotFoundException e1) {
			} catch (IOException e1) {
			}
			Enumeration keys = prop.keys();
			ArrayList al = new ArrayList();
			while ( keys.hasMoreElements() ) {
				String key = ( String )keys.nextElement();
				String val = prop.getProperty( key );
				int i = key.indexOf( "-" );
				FTPConfig config = new FTPConfig();
				config.host = key.substring( 0, i );
				config.user = key.substring( i + 1 );
				i = val.indexOf( "-" );
				config.directory = val.substring( 0, i );
				config.password = val.substring( i + 1 );
				al.add( config );
			}
			panel.restoreSaveHosts( al );
		}

		if ( DialogManager.showDialog(
				EditixFrame.THIS,
				"FTP host",
				"FTP host",
				"Define your FTP access",
				null,
				panel ) ==
					DialogManager.OK_ID ) {

			FTPConfig config = panel.getFTPConfig();
			
			if ( config.directory != null ) {
				if ( !config.directory.startsWith( "/" ))
					config.directory = "/" + config.directory;
			} else
				config.directory = "/";

			//ftp://myusername:mypassword@somehost

			String uri = 
				"ftp://" + config.host + config.directory;

				
			( ( CommonUniversalBrowserPanel )( preparePanel() ) ).browse(
					uri, config.user, config.password );
			
			if ( config.save ) {
				prop.put( config.host + "-" + config.user, config.directory + "-" + config.password );
				try {
					prop.save( new FileOutputStream( fConfig ), null );
				} catch (FileNotFoundException e1) {
				}
			}
			
			alwaysShown = true;

			super.actionPerformed(e);
			
		}

	}

}
