package com.japisoft.editix.main.steps;

import java.util.ArrayList;

import javax.swing.LookAndFeel;
import javax.swing.UIManager;

import org.netbeans.swing.plaf.aqua.AquaLFCustoms;

import com.japisoft.editix.main.EditixApplicationModel;
import com.japisoft.editix.ui.xmlpad.EditixLook;
import com.japisoft.framework.ApplicationModel;
import com.japisoft.framework.ApplicationStep;
import com.japisoft.framework.preferences.Preferences;
import com.japisoft.framework.toolkit.Logger;
import com.japisoft.xmlpad.look.LookManager;
import com.jgoodies.looks.plastic.PlasticLookAndFeel;

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
public class LookAndFeelApplicationStep implements ApplicationStep {

	public boolean isFinal() {
		return false;
	}

	public void start(String[] args) {
		// Build the default list
		UIManager.LookAndFeelInfo[] uf = UIManager
				.getInstalledLookAndFeels();
		ArrayList l = new ArrayList();
		l.add("DEFAULT");
		for (int i = 0; uf != null && i < uf.length; i++) {
			l.add(uf[i].getName());
		}
		String[] _ = new String[l.size()];
		for (int i = 0; i < l.size(); i++)
			_[i] = (String) l.get(i);

		String className = Preferences.getPreference( "interface",
				"lookAndFeel", _)[0];

		if ( EditixApplicationModel.MACOSX_MODE ) {
			l.add( "EDITIX" );
			UIManager.put( "tabContainer.controlButtons.disabled", true );
			try {
				Class.forName(
						"com.japisoft.editix.MacDocumentHandler" )
						.newInstance();
			} catch ( Throwable th ) {
				System.err
						.println( "Can't managed MacDocumentHandler : "
								+ th.getMessage() );
			}
			AquaLFCustoms aqua = new AquaLFCustoms();
			Object[] keyValue = aqua
					.createApplicationSpecificKeysAndValues();
			for (int i = 0; i < keyValue.length; i += 2) {
				UIManager.put(keyValue[i], keyValue[i + 1]);
			}

		}

		LookAndFeel look = null;
		if ("DEFAULT".equals(className)) {
			if (EditixApplicationModel.MACOSX_MODE) {
				look = UIManager.getLookAndFeel();
			} else {
				className = EditiXLookAndFeel.class.getName();
			}
		} else
		if ( "EDITIX".equals( className ) ) {
			className = EditiXLookAndFeel.class.getName();
		}
		try {

			if (look == null) {
				// Search for the class name
				for ( int i = 0; i < uf.length; i++ ) {
					if ( uf[ i ].getName().equals( className ) )
						className = uf[ i ].getClassName();
				}
				if ( className != null )
					look = ( LookAndFeel ) Class.forName( className ).newInstance();
			}

		} catch (Throwable th) {
			Logger.addWarning("Can't use this lookAndFeel "
					+ className);
			look = new PlasticLookAndFeel();
		}
		if ( look != null ) {
			try {
				UIManager.setLookAndFeel(look);
			} catch( Exception exc ) {
				ApplicationModel.debug( exc );
			}
		}

		LookManager.setCurrentLook( new EditixLook() );

	}

	public void stop() {
	}

}
