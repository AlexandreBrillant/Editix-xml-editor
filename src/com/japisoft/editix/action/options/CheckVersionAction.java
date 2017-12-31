package com.japisoft.editix.action.options;

import java.awt.event.ActionEvent;
import java.io.InputStream;
import java.net.URL;

import javax.swing.AbstractAction;

import com.japisoft.editix.main.EditixApplicationModel;
import com.japisoft.editix.ui.EditixFactory;
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
public class CheckVersionAction extends AbstractAction {
	
	public void actionPerformed(ActionEvent e) {
		try {
			String version = getLastVersion();
			if ( !( EditixApplicationModel.getAppVersion().equals( version ) ) ) {
				EditixFactory.buildAndShowInformationDialog( "You can download the version " + version + " at http://www.editix.com" );
				BrowserCaller.displayURL( "http://www.editix.com/download.html" );
			} else
				EditixFactory.buildAndShowInformationDialog( "You have the last version" );
		} catch( Throwable th ) {
			EditixFactory.buildAndShowErrorDialog( "Can't check the last version" );
		}
	}

	public static String getLastVersion() throws Exception {
		URL url = new URL( "http://www.editix.com/editix.ver" );
		InputStream input = url.openStream();
		StringBuffer sb = new StringBuffer();
		int c;
		while ( ( c = input.read() ) != -1 ) {
			sb.append( ( char )c );
		}
		return sb.toString().trim();
	}
	
}
