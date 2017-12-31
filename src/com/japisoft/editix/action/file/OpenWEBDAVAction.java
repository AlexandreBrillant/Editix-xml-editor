package com.japisoft.editix.action.file;

import java.awt.event.ActionEvent;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;

import javax.swing.AbstractAction;

import com.japisoft.editix.ui.EditixDialog;
import com.japisoft.editix.ui.EditixFactory;
import com.japisoft.editix.ui.EditixFrame;
import com.japisoft.framework.preferences.Preferences;
import com.japisoft.xmlpad.IXMLPanel;
import com.japisoft.xmlpad.XMLContainer;

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
public class OpenWEBDAVAction extends AbstractAction {

	public static String PROTOCOL = "WEBDAV";

	public void actionPerformed(ActionEvent e) {
/*		String host = Preferences.getPreference( "system", "wdHost", ( String )null );
		State model = null;
		
		if ( host != null ) {
			model = new State();
			model.putValue( State.HOST_KEY, host );
			String user = Preferences.getPreference( "system", "wdUser", ( String )null );
			if ( user != null )
				model.putValue( State.USER_KEY, user );
			String password = Preferences.getPreference( "system", "wdPassword", ( String )null );
			if ( password != null )
				model.putValue( State.PASSWORD_KEY, password );
			String[] files = Preferences.getPreference( "system", "wdFiles", ( String[] )null );
			if ( files != null )
				model.putValue( State.FILES_KEY, files );
		}
		
		WEBDAVBrowser browser = new WEBDAVBrowser( model );
		EditixDialog dialog = new EditixDialog("WebDAV", "WebDAV Browser", "Double-click for navigating into a directory and press OK for editing a file" );
		dialog.getContentPane().add( browser );
		dialog.setSize( 500, 550 );
		dialog.setVisible( true );
		dialog.dispose();
		
		if ( dialog.isOk() ) {

			String location = browser.getSelectedPathFile();
			
			if ( location == null ) {
				return;
			}
			
			String charset = Preferences.getPreference( "file", "rw-encoding", com.japisoft.framework.app.toolkit.Toolkit.FILE_ENCODING )[ 0 ];
			if ( "DEFAULT".equals( charset ) )
				charset = null;

			String content = null;
			
			try {
			
				byte[] bcontent = browser.getSelectedContentFile( true );
				if ( bcontent == null )
					bcontent = new byte[] {};
				
				if ( charset != null )
					content = new String( bcontent, charset );
				else
					content = new String( bcontent );

			} catch( UnsupportedEncodingException exc ) {
			}
			
			if ( content == null ) {
				EditixFactory.buildAndShowErrorDialog( "Can't open the file " + location );
				return;
			}

			IXMLPanel panel = EditixFactory.buildNewContainer( location );
			XMLContainer container = panel.getMainContainer();
			container.getDocumentInfo().setCurrentDocumentLocation( location );
			EditixFrame.ACCESSOR.addContainer( panel );
			container.setText( content );
			
			State modelFTP = browser.getState();
			modelFTP.putValue( "protocol", PROTOCOL );
			
			container.setCurrentDocumentLocationArg( modelFTP );

			if ( modelFTP.hasKey( State.HOST_KEY ) ) {
				Preferences.setPreference( "system", "wdHost", ( String )modelFTP.getValue( State.HOST_KEY ) );
			}
			if  ( modelFTP.hasKey( State.USER_KEY ) ) {
				Preferences.setPreference( "system", "wdUser", ( String )modelFTP.getValue( State.USER_KEY ) );
			}
			if ( modelFTP.hasKey( State.PASSWORD_KEY ) ) {
				Preferences.setPreference( "system", "wdPassword", ( String )modelFTP.getValue( State.PASSWORD_KEY ) );
			}
			if ( modelFTP.hasKey( State.FILES_KEY ) ) {
				Preferences.setRawPreference( "system", "wdFiles", ( String[] )modelFTP.getValue( State.FILES_KEY ) );
			}
		}		 */
	}

/*
	public static boolean save( XMLContainer container ) {

		HashMap map = ( HashMap )container.getCurrentDocumentLocationArg();

		try {

			WEBDAVResourceManager manager = new WEBDAVResourceManager();
	
			manager.start( 
					(String)map.get( State.HOST_KEY ),
					(String)map.get( State.USER_KEY ),
					(String)map.get( State.PASSWORD_KEY ) );
	
			manager.setContent( new BasicResourceItem( container.getCurrentDocumentLocation(), true, null ),
					container.getText().getBytes() );
	
			manager.stop();
		
		} catch( Exception exc ) {
			return false;
		}
				
		return true;
	}
*/	
	
}
