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

