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

package com.japisoft.framework.ui.browser;

import javax.swing.JFrame;

import com.japisoft.framework.ApplicationModel;
import com.japisoft.framework.preferences.Preferences;

public class BrowserFactory {

	private boolean jfxEnabled = false;
	
	private BrowserFactory() {
		// Webkit browser disabled by default
		if ( Preferences.getPreference( "interface", "webkitBrowser", false ) ) {
			try {
				Class.forName( "javafx.scene.web.WebEngine" );
				jfxEnabled = true;
			} catch( Exception exc ) {			
			}
		}
	}

	private static BrowserFactory INSTANCE = null;
	
	public static BrowserFactory getInstance() {
		if ( INSTANCE == null )
			INSTANCE = new BrowserFactory();
		return INSTANCE;
	}
	
	public Browser newBrowser() {
		/*
		ApplicationModel.debug( "Use JFX Browser :" + jfxEnabled );
		if ( !jfxEnabled ) {
			return new SwingBrowser();
		}
		else {
			try {
				return new JFXBrowser();
			} catch( Throwable th ) {
				jfxEnabled = false;
				return newBrowser();
			}
		}
		*/
		return new SwingBrowser();
	}

	public static void main( String[] args ) {

		JFrame f = new JFrame();
		Browser b = BrowserFactory.getInstance().newBrowser();
		b.setHTML( "<html><body><b style='box-shadow:1px 1px 10px red'>Hello</b> world!</b></body></html>", null );
		f.add( b.getView() );
		f.setSize( 300, 300 );
		f.setVisible( true );		

	}

}

