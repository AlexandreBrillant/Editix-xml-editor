package com.japisoft.framework.step;

import java.util.ArrayList;

import javax.swing.LookAndFeel;
import javax.swing.UIManager;

import com.japisoft.framework.ApplicationModel;
import com.japisoft.framework.ApplicationStepAdapter;
import com.japisoft.framework.log.Logger;
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
public class LookAndFeelStep extends ApplicationStepAdapter {
	
	private String defaultLookAndFeel;

	/** Use the provided lookAndFeel */
	public LookAndFeelStep( String defaultLookAndFeelClassName ) {
		this.defaultLookAndFeel = defaultLookAndFeelClassName;
	}

	/** Use the system lookAndFeel by default */
	public LookAndFeelStep() {
		this.defaultLookAndFeel = 
				UIManager.getSystemLookAndFeelClassName();
	}

	public void start(String[] args) {
		// Build the default list
		UIManager.LookAndFeelInfo[] uf = UIManager
				.getInstalledLookAndFeels();
		ArrayList l = new ArrayList();
		l.add("DEFAULT");
		for (int i = 0; uf != null && i < uf.length; i++) {
			l.add(uf[i].getClassName());
		}
		String[] _ = new String[l.size()];
		for (int i = 0; i < l.size(); i++)
			_[i] = (String) l.get(i);

		String className = Preferences.getPreference( "interface",
				"lookAndFeel", _)[0];

		LookAndFeel look = null;
		if ( "DEFAULT".equals( className ) ) {
			if ( ApplicationModel.MACOSX_MODE ) {
				if ( defaultLookAndFeel != null )
					l.add( defaultLookAndFeel );
				look = UIManager.getLookAndFeel();
			} else
				className = defaultLookAndFeel;
		}
		try {
			if (look == null)
				look = (LookAndFeel) Class.forName(className)
						.newInstance();
			UIManager.setLookAndFeel(look);			
		} catch (Throwable th) {
			Logger.addWarning("Can't use this lookAndFeel "
					+ className);
		}
	}
	
}
