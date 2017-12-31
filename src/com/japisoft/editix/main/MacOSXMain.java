package com.japisoft.editix.main;

import java.io.File;
import java.util.List;

import com.apple.eawt.AboutHandler;
import com.apple.eawt.Application;
import com.apple.eawt.OpenFilesHandler;
import com.apple.eawt.PreferencesHandler;
import com.apple.eawt.QuitHandler;
import com.apple.eawt.QuitResponse;
import com.apple.eawt.AppEvent.AboutEvent;
import com.apple.eawt.AppEvent.OpenFilesEvent;
import com.apple.eawt.AppEvent.PreferencesEvent;
import com.apple.eawt.AppEvent.QuitEvent;
import com.japisoft.editix.action.file.OpenAction;
import com.japisoft.framework.application.descriptor.ActionModel;

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
public class MacOSXMain {
	
	MacOSXMain() {
		try {
			MacOSXApplication m = new MacOSXApplication();
			Application app = Application.getApplication();
			app.setOpenFileHandler( m );
			app.setQuitHandler( m );
			app.setAboutHandler( m );
			app.setPreferencesHandler( m );
		} catch( Throwable th ) {
			System.out.println( "Can't use Java Extension for Mac OS X...");
			th.printStackTrace();
		}
	}

	static class MacOSXApplication implements AboutHandler, OpenFilesHandler, PreferencesHandler, QuitHandler {
		public void openFiles(OpenFilesEvent arg0) {
			List<File> files = arg0.getFiles();
			if ( files != null ) {
				for ( File f : files ) {
					OpenAction.openFile( null, false, f, null );
				}
			}			
		}
		public void handleQuitRequestWith(QuitEvent arg0, QuitResponse arg1) {
			ActionModel.activeActionById( "quit", null );
		}
		public void handleAbout(AboutEvent arg0) {
			ActionModel.activeActionById( "about", null );
		}
		public void handlePreferences(PreferencesEvent arg0) {
			ActionModel.activeActionById( "preferences", null );
		}
	}
	
}
