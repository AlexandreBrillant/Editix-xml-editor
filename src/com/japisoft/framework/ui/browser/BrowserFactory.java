package com.japisoft.framework.ui.browser;

import javax.swing.JFrame;

import com.japisoft.framework.ApplicationModel;
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
		ApplicationModel.debug( "Use JFX Browser :" + jfxEnabled );
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
