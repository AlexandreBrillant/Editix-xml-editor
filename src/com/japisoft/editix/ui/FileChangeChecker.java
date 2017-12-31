package com.japisoft.editix.ui;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

import com.japisoft.editix.main.EditixApplicationModel;
import com.japisoft.framework.xml.XMLFileData;
import com.japisoft.framework.xml.XMLToolkit;
import com.japisoft.xmlpad.IXMLPanel;
import com.japisoft.xmlpad.XMLContainer;

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
public class FileChangeChecker extends TimerTask {

	static Timer t = null;
	
	public static void start() {
		t = new Timer();
		t.schedule( 
				new FileChangeChecker(), 2000, 4000 
		);
	}

	public static void stop() {
		if ( t != null )
			t.cancel();
	}
	
	private int errorCounter = 0;

	public void run() {
		try {
			for ( int i = 0; i < EditixFrame.THIS.getXMLContainerCount(); i++ ) {
				IXMLPanel panel = EditixFrame.THIS.getIXMLPanel( i );
				if ( panel == null )
					continue;
				XMLContainer container = panel.getMainContainer();
				if ( container == null )
					continue;
				
				if ( container.hasProperty( "save.delegate" ) ) 
					continue;
				
				if ( container.hasProperty( "file.checker.ignore" ) )
					continue;
				
				String s = container.getCurrentDocumentLocation();
				if ( s != null ) {
					if ( s.indexOf( "://" ) == -1 ) {	
						File f = new File( s );
						long md = f.lastModified();
						if ( md != container.getDocumentInfo().getCurrentDocumentModifiedDate() ) {
							// Recharger le fichier ???
							if ( EditixFactory.buildAndShowConfirmDialog( "The file " + s + " has been modified, reload it ?" ) ) {
								if ( !panel.reload() ) {
									EditixFactory.buildAndShowErrorDialog( "Can't reload " + s );
								}
							}
							container.getDocumentInfo().setCurrentDocumentModifiedDate( md );
						}
					}
				}
			}
		} catch (RuntimeException e) {
			// This code mustn't create editix error
			EditixApplicationModel.debug( e );
			errorCounter++;
			if ( errorCounter > 5 ) // No more running, too dangerous
				stop();
		}
	}

}
