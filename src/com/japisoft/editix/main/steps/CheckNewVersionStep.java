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

package com.japisoft.editix.main.steps;

import com.japisoft.editix.action.options.CheckVersionAction;
import com.japisoft.editix.main.EditixApplicationModel;
import com.japisoft.framework.ApplicationModel;
import com.japisoft.framework.ApplicationStep;
import com.japisoft.framework.preferences.Preferences;

public class CheckNewVersionStep implements ApplicationStep, Runnable {

	public boolean isFinal() {
		return false;
	}

	public void start(String[] args) {
		if ( Preferences.getPreference( "interface", "checkNewVersion", true ) ) {
			new Thread( this ).start();
		}
	}
	
	public void stop() {}
	
	@Override
	public void quit() {
	}
	
	public void run() {
		try {
			String lastVersion = CheckVersionAction.getLastVersion();
			
			double officialVersion = Double.parseDouble( lastVersion );

			double currentVersion = EditixApplicationModel.MAJOR_VERSION;
			currentVersion += EditixApplicationModel.MINOR_VERSION / 10;			
			
			try {			
				if ( officialVersion > currentVersion ) {
					ApplicationModel.fireApplicationValue( "information", "A new version " + lastVersion + " is available !" );
				} else
				if ( currentVersion > officialVersion ) {
					ApplicationModel.fireApplicationValue( "information", "You are using an unofficial release" );
				}
			} catch( NumberFormatException exc ) {
				ApplicationModel.fireApplicationValue( "information", "A new version " + lastVersion + " is available" );
			}
			
		} catch( Exception exc ) {
			ApplicationModel.debug( exc );
		}
	}

}

