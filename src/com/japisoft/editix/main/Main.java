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


package com.japisoft.editix.main;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;

import javax.swing.Action;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import com.japisoft.editix.action.file.project.RecentProjectManager;

import com.japisoft.editix.ui.EditixFrame;
import com.japisoft.editix.ui.panels.PanelManager;
import com.japisoft.framework.ApplicationMain;
import com.japisoft.framework.ApplicationModel;
import com.japisoft.framework.ApplicationModel.ApplicationModelListener;
import com.japisoft.framework.preferences.Preferences;

/**
 * @author Alexandre Brillant (https://github.com/AlexandreBrillant/Editix-xml-editor)
 * @version 1.0 */
public class Main extends ApplicationMain implements ApplicationModelListener {

	public void fireApplicationData(String key, Object... values) {
		if ( "lastProject".equals( key ) ) {
			String project = ( String )values[ 0 ];
			// Store the last opened project
			Preferences.setPreference( 
				Preferences.SYSTEM_GP, 
				"lastProject",
				project 
			);
			if ( project != null ) {
				RecentProjectManager.getInstance().addProject( new File( project ) );
			}
		} else
		if ( "windowOpened".equals( key ) ) {
			String lastProject = Preferences.getPreference(
				Preferences.SYSTEM_GP, 
				"lastProject",
				( String )null 
			);
			if ( lastProject != null ) {				
				ApplicationModel.fireApplicationValue(
					"lastProject",
					lastProject
				);				
			}
		} else
		if ( "windowClosing".equals( key ) ) {
			Action a = ( Action )com.japisoft.framework.application.descriptor.ActionModel.restoreAction( "quit" );
			a.actionPerformed( null );			
		} else
		if ( "quit".equals( key ) ) {
			try {
				EditixFrame.THIS.getBuilder().save();
			} catch ( Throwable e1 ) {
				System.err.println( "Error, can't save recent files menu ? : " + e1.getMessage() );
				ApplicationModel.debug( e1 );
			}
			Preferences.setRawPreference(
				"dialog",
				"editix",
				EditixFrame.THIS.getBounds() 
			);
			PanelManager.saveState( true );
			Preferences.savePreferences();
			System.exit( 0 );
		}
	}
	

	
	public static void main( String[] args ) throws Throwable {
		
		try {

			// System.setProperty( "uiconsole", "off" ); 
			
			new EditixApplicationModel();

			if ( EditixApplicationModel.isMacOSXPlatform() ) {
				try {
					Class.forName( "MacOSXMain" ).newInstance();
				} catch( Throwable th ) {
					th.printStackTrace();
				}				
			}

			start( args );

		} catch ( Throwable th ) {
			th.printStackTrace();
			StringWriter sw = new StringWriter();
			th.printStackTrace(new PrintWriter(sw));

			JFrame fr = new JFrame( "Unknown error !" );
			StringBuffer sb = new StringBuffer();
			sb.append("EditiX " + EditixApplicationModel.getAppVersion()
					+ " - build " + EditixApplicationModel.BUILD);
			sb.append("\nHas met an unknown error while starting, please send it to : "
					+ EditixApplicationModel.MAIN_SUPPORT_EMAIL + "\n");
			sb
					.append("specifying your operating system version and java version\n");
			sb
					.append("\n----------------------------------------------------------------\n\n");
			sb.append(sw.toString());
			JTextArea ar = new JTextArea(sb.toString());
			fr.getContentPane().add(new JScrollPane(ar));
			fr.pack();
			fr.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			fr.setVisible(true);
		}

	}
}

