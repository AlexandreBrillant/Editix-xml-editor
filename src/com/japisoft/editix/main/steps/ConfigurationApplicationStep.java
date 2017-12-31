package com.japisoft.editix.main.steps;

import java.io.File;

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
public class ConfigurationApplicationStep implements ApplicationStep {

	static {
		EditixApplicationModel.SHORT_APPNAME = "editix";
		EditixApplicationModel.LONG_APPNAME = "Editix XML Editor Community Edition";
		EditixApplicationModel.BUILD = "010118";
		EditixApplicationModel.INNER_BUILD = "010118";
		EditixApplicationModel.BETA_VERSION = 0;
		EditixApplicationModel.MAJOR_VERSION = 16;

		Preferences.PREF_FILENAME = "pref" + EditixApplicationModel.MAJOR_VERSION + ".prop";		

		EditixApplicationModel.MAJOR_YEAR = 2018;
		EditixApplicationModel.SERVICE_PACK = 0;
		EditixApplicationModel.MINOR_VERSION = 0;
		EditixApplicationModel.SUBMINOR_VERSION = 0;
		EditixApplicationModel.MAIN_SUPPORT_EMAIL = "editixsupport@japisoft.com";
		EditixApplicationModel.REGISTERED_FILE = "editix" + EditixApplicationModel.MAJOR_VERSION + ".reg";
		EditixApplicationModel.DEF_MANUAL_PATH = "http://www.editix.com/doc/manual" + EditixApplicationModel.MAJOR_VERSION + "/index.html";
		EditixApplicationModel.PREFERENCES_SUBMENU =
			Main.class.getResource( 
				"prefnode.txt" 
		);

		EditixApplicationModel.setSharedProperty( "defaultFont", "consolas" );

		//%%%%%

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

		
	}

	public void stop() {
	}

}
