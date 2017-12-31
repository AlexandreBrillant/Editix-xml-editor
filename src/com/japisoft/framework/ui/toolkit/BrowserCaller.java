package com.japisoft.framework.ui.toolkit;

import java.io.IOException;

import com.japisoft.framework.preferences.Preferences;

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
public class BrowserCaller {

	public static boolean displayURL(String url) {
		String cmd = null;
		try {
			Process p = null;
			if ( isWindowsPlatform() ) {
				String prefix = Preferences.getPreference( "viewer", "html", WIN_PATH + " " + WIN_FLAG );
				cmd = prefix + " " + url;
				p = Runtime.getRuntime().exec( cmd );
			} else
			if ( isMacOSXPlatform() ) {
				String prefix = Preferences.getPreference( "viewer", "html", MAC_PATH );
				cmd = prefix + " " + url;
				p = Runtime.getRuntime().exec( cmd );
			} else {
				String prefix = Preferences.getPreference( "viewer", "html", UNIX_PATH );
				//cmd = prefix + "(" + url + ")";
				cmd = prefix + " " + url;
				p = Runtime.getRuntime().exec( cmd );
			}
		} catch ( IOException x ) {
			return false;
		}
		return true;
	}

	public static boolean isWindowsPlatform() {
		String os = System.getProperty( "os.name" );
		if ( os != null && 
				( os.toLowerCase().indexOf( WIN_ID ) > -1 ) )
			return true;
		else
			return false;
	}

	public static boolean isMacOSXPlatform() {
		String os = System.getProperty( "os.name" );
		if ( os != null && 
				( os.toLowerCase().indexOf( MACOSX_ID ) > -1 ) )
			return true;
		return false;
	}

	private static final String MACOSX_ID = "mac os x";
	private static final String WIN_ID = "windows";

	public static final String WIN_PATH = "rundll32";
	public static final String WIN_FLAG = "url.dll,FileProtocolHandler";
	//private static final String UNIX_PATH = "netscape";
	public static final String UNIX_PATH = "firefox";
	//private static final String UNIX_FLAG = "-remote openURL";
	public static final String UNIX_FLAG = "";
	public static final String MAC_PATH = "open";
}
