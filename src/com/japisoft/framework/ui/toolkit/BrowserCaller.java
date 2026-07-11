// Editix XML Editor
// https://www.editix.com
// Copyright (c) 2026 Alexandre Brillant
//
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
// AI Training Restriction :
// This source code is provided for human use only.
// Using this code to train, fine-tune, or develop AI models,
// machine learning systems, or similar technologies is
// STRICTLY PROHIBITED. Violations will terminate all rights
// under the applicable license.

package com.japisoft.framework.ui.toolkit;

import java.io.IOException;
import java.net.URI;

import com.japisoft.framework.preferences.Preferences;
import java.awt.Desktop;


/** Here a toolkit for calling external browser like IE depending the platform */
public class BrowserCaller {

	public static boolean displayURL(String url) {
		
		if (isDesktopSupported()) {
			try {
				Desktop.getDesktop().browse(new URI( url ));
				return true;
			} catch( Exception exc ) {

			}
		}
		
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

	public static boolean isDesktopSupported() {
		return Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE);
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
