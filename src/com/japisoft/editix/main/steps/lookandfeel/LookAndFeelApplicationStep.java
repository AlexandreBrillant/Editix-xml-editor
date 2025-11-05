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

package com.japisoft.editix.main.steps.lookandfeel;

import java.util.ArrayList;

import javax.swing.LookAndFeel;
import javax.swing.UIManager;

import org.netbeans.swing.plaf.aqua.AquaLFCustoms;

import com.japisoft.editix.main.EditixApplicationModel;
import com.japisoft.framework.ApplicationModel;
import com.japisoft.framework.ApplicationStep;
import com.japisoft.framework.preferences.Preferences;
import com.japisoft.framework.toolkit.Logger;
import com.japisoft.xmlpad.look.LookManager;

/**
 * @author Alexandre Brillant (https://github.com/AlexandreBrillant/Editix-xml-editor)
 */
public class LookAndFeelApplicationStep implements ApplicationStep {

	public boolean isFinal() {
		return false;
	}

	public void start(String[] args) {
		
		boolean blackMode = false;
		
		// Build the default list
		UIManager.LookAndFeelInfo[] uf = UIManager
				.getInstalledLookAndFeels();
		ArrayList l = new ArrayList();
		l.add("DEFAULT");
		l.add("WHITE");
		l.add("LIGHT");
		l.add("DARK");

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
				className = EditiXDarkLookAndFeel.class.getName();
				blackMode = true;
			}
		} else
		if ( "EDITIX".equals( className ) || "DARK".equals( className ) ) {
			className = EditiXDarkLookAndFeel.class.getName();
			blackMode = true;
		} else
		if ( "WHITE".equals( className ) || "LIGHT".equals( className ) ) {
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
			// look = new PlasticLookAndFeel();
			look = new EditiXDarkLookAndFeel();
		}
		if ( look != null ) {
			try {
				UIManager.setLookAndFeel(look);
			} catch( Exception exc ) {
				ApplicationModel.debug( exc );
			}
		}

		if ( blackMode ) {
			LookManager.setCurrentLook( new EditixDarkLook() );
		} else {
			LookManager.setCurrentLook( new EditixLook() );
		}

		EditixApplicationModel.setSharedProperty( "darkMode", new Boolean( blackMode ) );
		EditixApplicationModel.DARK_MODE = blackMode;
	}

	public void stop() {
	}
	
	@Override
	public void quit() {
	}

}

