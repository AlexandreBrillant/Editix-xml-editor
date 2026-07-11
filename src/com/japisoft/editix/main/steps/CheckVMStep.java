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

package com.japisoft.editix.main.steps;

import com.japisoft.editix.ui.EditixFactory;
import com.japisoft.editix.update.CheckJVMVersion;
import com.japisoft.framework.ApplicationStep;
import com.japisoft.framework.preferences.Preferences;

public class CheckVMStep implements ApplicationStep {

	public boolean isFinal() {

		return false;
	}

	public void start(String[] args) throws Exception {
		if ( !Preferences.getPreference( Preferences.SYSTEM_GP, "jvm-checked", false ) ) {
			String jvmError = CheckJVMVersion.check();
			if ( jvmError != null ) {
				System.err.println( jvmError );
				EditixFactory.buildAndShowWarningDialog( jvmError );
			}
			Preferences.setPreference( Preferences.SYSTEM_GP, "jvm-checked", true );	
		}		
	}

	public void stop() {
	}
	
	@Override
	public void quit() {
	}

}
