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

package com.japisoft.editix.main.steps;

import java.io.File;
import java.net.MalformedURLException;

import org.apache.xerces.impl.Version;

import com.japisoft.editix.document.DocumentModel;
import com.japisoft.editix.main.EditixApplicationModel;
import com.japisoft.editix.main.Main;
import com.japisoft.editix.ui.EditixFrame;
import com.japisoft.editix.update.UpdateManager;
import com.japisoft.framework.ApplicationModel;
import com.japisoft.framework.ApplicationStep;
import com.japisoft.framework.dialog.about.AboutPanel;
import com.japisoft.framework.dialog.console.ConsolePanel;
import com.japisoft.framework.preferences.Preferences;
import com.japisoft.p3.Manager;

public class ConfigurationApplicationStep implements ApplicationStep {

	static {
		EditixApplicationModel.SHORT_APPNAME = "editix";
		EditixApplicationModel.LONG_APPNAME = "Editix XML Editor";
		EditixApplicationModel.BUILD = "010626";
		EditixApplicationModel.INNER_BUILD = "010626";
		EditixApplicationModel.BETA_VERSION = 0;
		EditixApplicationModel.MAJOR_VERSION = 21;
		
		Preferences.PREF_FILENAME = "pref" + EditixApplicationModel.MAJOR_VERSION + ".prop";		
		
		EditixApplicationModel.MAJOR_YEAR = 2025;
		EditixApplicationModel.SERVICE_PACK = 0;
		EditixApplicationModel.MINOR_VERSION = 0;
		EditixApplicationModel.SUBMINOR_VERSION = 0;
		EditixApplicationModel.MAIN_SUPPORT_EMAIL = "editixsupport@japisoft.com";
		EditixApplicationModel.REGISTERED_FILE = "editix" + EditixApplicationModel.MAJOR_VERSION + ".reg";
		EditixApplicationModel.REGISTERED_FILE2 = "editix" + ( EditixApplicationModel.MAJOR_VERSION - 1 ) + ".reg";
		EditixApplicationModel.COMPANY_URL = "https://www.editix.com";
		
		try {
			EditixApplicationModel.DEF_MANUAL_PATH = new File( "doc/index.html" ).toURI().toURL().toExternalForm();
		} catch( MalformedURLException badUrl ) {
			EditixApplicationModel.DEF_MANUAL_PATH = "https://www.editix.com/doc/manual" + ( EditixApplicationModel.MAJOR_VERSION + 1 ) + "/index.html";
		}

		EditixApplicationModel.PURCHASING_URL = "https://www.editix.com";
		
		EditixApplicationModel.PREFERENCES_SUBMENU =
			Main.class.getResource( 
				"prefnode.txt" 
		);
		
		EditixApplicationModel.setSharedProperty( "defaultFont", "consolas" );

		Manager.PERS_SIGNATURE = "pers" + EditixApplicationModel.MAJOR_VERSION;
		Manager.PROF_SIGNATURE = "prof" + EditixApplicationModel.MAJOR_VERSION;
		Manager.STUD_SIGNATURE = "stud" + EditixApplicationModel.MAJOR_VERSION;
		Manager.ENT_SIGNATURE = "ent" + EditixApplicationModel.MAJOR_VERSION;
		Manager.FLOAT_SIGNATURE = "float" + EditixApplicationModel.MAJOR_VERSION;
		Manager.NONCOMMERCIAL_SIGNATURE = "noncom" + EditixApplicationModel.MAJOR_VERSION;

		Manager.PERS_SIGNATURE2 = "pers" + ( EditixApplicationModel.MAJOR_VERSION - 1 );
		Manager.PROF_SIGNATURE2 = "prof" + ( EditixApplicationModel.MAJOR_VERSION - 1 );
		Manager.STUD_SIGNATURE2 = "stud" + ( EditixApplicationModel.MAJOR_VERSION - 1 );
		Manager.ENT_SIGNATURE2 = "ent" + ( EditixApplicationModel.MAJOR_VERSION - 1 );
		Manager.FLOAT_SIGNATURE2 = "float" + ( EditixApplicationModel.MAJOR_VERSION - 1 );
		Manager.NONCOMMERCIAL_SIGNATURE2 = "noncom" + ( EditixApplicationModel.MAJOR_VERSION - 1 );
		
		Manager.MAGIC_NUMBER_1 = 19;
		Manager.MAGIC_NUMBER_2 = 11;
		
		Manager.CURRENT_PRO_FILE1 = ".sysedb55";
		Manager.CURRENT_PRO_FILE2 = ".sysedb56";
		
		Manager.PREVIOUS_FILES = new String[] {
				".sysedb53", ".sysedb54",
				".sysedb51", ".sysedb52",				
				".sysedb49", ".sysedb50",
				".sysedb47", ".sysedb48",
				".sysedb45", ".sysedb46",
				".sysedb44", ".sysedb43",
				".sysedb42", ".sysedb41",
				".sysedb40", ".sysedb39",
				".sysedb37", ".sysedb38",
				".sysedb35", ".sysedb36",
				".sysedb33", ".sysedb34",
				".sysedb31", ".sysedb32",
				".sysedb29", ".sysedb30",
				".sysedb27", ".sysedb28",
				".sysedb21", ".sysedb22",
				".sysedb19", ".sysedb20"
		};
		
		Manager.PREVIOUS_INC = 25;

		try {
			Preferences.loadPreferences();
		} catch( Throwable th ) {
			System.err.println( "Can't load preferences :" + th.getMessage() );
		}

		if ( "true".equals( System.getProperty( "editix.debug" ) ) )
			System.out.println( "DEBUG MODE" );

	}

	// For line command

	public static File startingFile = null;
	public static File startingProject = null;

	public static File getRegisteredPath() {
		File home = new File(System.getProperty("user.home"));
		home = new File(home, ".editix");
		if (!home.exists()) {
			boolean rs = home.mkdirs();
			if (!rs) {
				return null;
			}
		}
		return home;
	}
	
	public boolean isFinal() {
		return false;
	}

	@Override
	public void quit() {
	}
	
	public void start(String[] args) {
		
		ApplicationModel.addApplicationModelListener( new Main() );
		
		if (args.length > 0) {
			if (args[0].endsWith(".pre"))
				startingProject = new File(args[0]);
			else
				startingFile = new File(args[0]);
		}

		new UpdateManager();
		
		// Anti aliasing
		if ( Preferences.getPreference( "interface", "antialiasing", true ) ) {
			System.setProperty("awt.useSystemAAFontSettings","on");
			System.setProperty( "swing.aatext", "true" );
			ApplicationModel.debug( "Setting anti aliasing on" );
		}

		EditixApplicationModel.init( args );
		EditixApplicationModel.starting();

		// Disable logs for FOP
		System.setProperty( 
				"org.apache.commons.logging.Log", 
				EmptyApacheLogs.class.getName() 
		);
		
		EditixFrame frame = null;

		try {
			AboutPanel.addAboutProperty( "XERCES VERSION", Version.getVersion() );
			AboutPanel.addAboutProperty( "FOP VERSION", org.apache.fop.Version.getVersion() );
			AboutPanel.addAboutProperty( "SAXON VERSION", net.sf.saxon.Version.getProductVersion() );
			AboutPanel.addAboutProperty( "SYSTEM FILE ENCODING", System.getProperty("file.encoding") );
			if ( EditixApplicationModel.INNER_BUILD != null )
					AboutPanel.addAboutProperty( "inner build", EditixApplicationModel.INNER_BUILD );	
		} catch (RuntimeException e1) {
			e1.printStackTrace();
		}

		if ( "on".equals( System.getProperty( "uiconsole", "on" ) ) )
			ConsolePanel.initConsoleState();
		
		// EditixApplicationModel.ACCESSOR.checkHck();

		String proxyHost = Preferences.getPreference("proxy", "host", "");
		int proxyPort = Preferences.getPreference("proxy", "port", 0);

		if (proxyHost.length() > 0 && proxyPort != 0) {
			System.setProperty("proxyHost", proxyHost);
			System.setProperty("proxyPort", "" + proxyPort);
			System.setProperty("proxySet", "true");
			System.setProperty("http.proxyHost", proxyHost);
			System.setProperty("http.proxyPort", "" + proxyPort);
			System.setProperty("http.proxySet", "true");
		}
		
		DocumentModel.class.getName();

		
		
		File userHome = ApplicationModel.getAppUserPath();
		ApplicationModel.debug( "Home directory " + userHome );
		ApplicationModel.debug( "Can read home directory ? " + userHome.canRead() );
		ApplicationModel.debug( "Can write home directory ? " + userHome.canWrite() );

	}

	public void stop() {
	}

}

