
package com.japisoft.editix.update;

import com.japisoft.editix.main.EditixApplicationModel;
import com.japisoft.framework.ApplicationModel;
import com.japisoft.framework.preferences.Preferences;
import com.japisoft.framework.ui.toolkit.BrowserCaller;

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
public class UpdateManager {

		static {

			if ( EditixApplicationModel.MAJOR_VERSION == 6 ) {
				if ( "false".equals( Preferences.getPreference( "system", "update6.1", "false" ) ) ) {
					System.out.println( "Update default browser" );
					if ( !ApplicationModel.isOtherPlatform() ) {
						Preferences.getPreference( "viewer", "html", "firefox" );
					}
					Preferences.setPreference( "system", "update6.1", "true" );
				}
			}

			if ( EditixApplicationModel.MAJOR_VERSION == 6 ) {
				
				if ( "false".equals( Preferences.getPreference( "system", "update6.2", "false" ) ) ) {
					System.out.println( "Update default browser" );

					if ( ApplicationModel.isWindowsPlatform() ) {
						Preferences.setPreference( "viewer", "html", BrowserCaller.WIN_PATH + " " + BrowserCaller.WIN_FLAG  );
					} else
					if ( ApplicationModel.isMacOSXPlatform() ) {
						Preferences.setPreference( "viewer", "html", BrowserCaller.MAC_PATH );
					} else
					if ( ApplicationModel.isOtherPlatform() ) {
						Preferences.setPreference( "viewer", "html", BrowserCaller.UNIX_PATH );						
					}

					Preferences.setPreference( "system", "update6.2", "true" );
				}

			}			
		}

		static void updateForANewVersion( int major, int minor ) {
			// Remove all dialogs location
			Preferences.removeAllPreferencesForType( Preferences.RECTANGLE );
			EditixApplicationModel.debug( "Update for release " + major + " . " + minor );
			
			if ( major > 5 || ( major == 5 && minor >= 1 ) ) {
				Preferences.removePreference( "xslt", "parameter" );
			}			
		}

}
