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

package com.japisoft.framework.step;

import java.util.ArrayList;

import javax.swing.LookAndFeel;
import javax.swing.UIManager;

import com.japisoft.framework.ApplicationModel;
import com.japisoft.framework.ApplicationStepAdapter;
import com.japisoft.framework.log.Logger;
import com.japisoft.framework.preferences.Preferences;

/**
 * Here a lookAndFeelStep using the user preferences ( interface/lookAndFeel keys ) 
 * @author Alexandre Brillant (https://github.com/AlexandreBrillant/Editix-xml-editor)
 * @version 1.0
 * */
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
		String[] __ = new String[l.size()];
		for (int i = 0; i < l.size(); i++)
			__[i] = (String) l.get(i);

		String className = Preferences.getPreference( "interface",
				"lookAndFeel", __)[0];

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
