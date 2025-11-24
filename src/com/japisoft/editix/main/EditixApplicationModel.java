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

package com.japisoft.editix.main;

import java.io.File;

import com.japisoft.editix.main.steps.CheckNewVersionStep;
import com.japisoft.editix.main.steps.ConfigurationApplicationStep;
import com.japisoft.editix.main.steps.InitParserStep;
import com.japisoft.editix.main.steps.InterfaceBuilderApplicationStep;
import com.japisoft.editix.main.steps.MainFrameApplicationStep;
import com.japisoft.editix.main.steps.MenuScriptsStep;
import com.japisoft.editix.main.steps.Release2026Step;
import com.japisoft.editix.main.steps.SplashScreenApplicationStep;
import com.japisoft.editix.main.steps.StartingFilesApplicationStep;
import com.japisoft.editix.main.steps.TestApplicationStep;
import com.japisoft.editix.main.steps.XMLApplicationStep;
import com.japisoft.editix.main.steps.XMLPadApplicationStep;
import com.japisoft.editix.main.steps.lookandfeel.LookAndFeelApplicationStep;
import com.japisoft.framework.ApplicationModel;
import com.japisoft.framework.application.descriptor.InterfaceBuilder;
import com.japisoft.framework.preferences.Preferences;

import com.japisoft.p3.HackerController;
import com.japisoft.p3.Manager;
import com.japisoft.p3.info.WDialog;

/**
 * @author Alexandre Brillant (https://github.com/AlexandreBrillant/Editix-xml-editor)
 * @version 1.0
 * */
public class EditixApplicationModel extends ApplicationModel implements HackerController {
	
	static {

		addApplicationStep( new Release2026Step() );
		addApplicationStep( new SplashScreenApplicationStep() );
		addApplicationStep( new TestApplicationStep() );
		addApplicationStep( new ConfigurationApplicationStep() );
		addApplicationStep( new LookAndFeelApplicationStep() );		
		addApplicationStep( new InterfaceBuilderApplicationStep() );
		addApplicationStep( new XMLApplicationStep() );
		addApplicationStep( new XMLPadApplicationStep() );
		addApplicationStep( new MainFrameApplicationStep() );
		addApplicationStep( new StartingFilesApplicationStep() );
		addApplicationStep( new MenuScriptsStep() );
		addApplicationStep( new InitParserStep() );
		addApplicationStep( new CheckNewVersionStep() );
		
	}

	public static EditixApplicationModel ACCESSOR = null;

	public static InterfaceBuilder INTERFACE_BUILDER = null;

	static int[] data;
	
	EditixApplicationModel() {
		ACCESSOR = this;		
		data = new int[] {
				67
				,113
				,52
				,112
				,113
				,96
				,113
				,119
				,96
				,113
				,112
				,52
				,117
				,122
				,52
				,125
				,120
				,120
				,113
				,115
				,117
				,120
				,52
				,97
				,103
				,117
				,115
				,113
				,52
				,123
				,114
				,52
				,81
				,112
				,125
				,96
				,125
				,76
				,56
				,52
				,96
				,124
				,125
				,103
				,52
				,125
				,103
				,52
				,117
				,52
				,99
				,117
				,102
				,122
				,125
				,122
				,115
				,30
				,56
				,52
				,109
				,123
				,97
				,102
				,52
				,89
				,65
				,71
				,64
				,52
				,115
				,113
				,96
				,52
				,117
				,52
				,120
				,113
				,115
				,117
				,120
				,52
				,98
				,113
				,102
				,103
				,125
				,123
				,122
				,52
				,117
				,96
				,52
				,124
				,96
				,96
				,100
				,46
				,59
				,59
				,99
				,99
				,99
				,58
				,113
				,112
				,125
				,96
				,125
				,108
				,58
				,119
				,123
				,121
				,52
				,117
				,122
				,112
				,52
				,118
				,97
				,109
				,52
				,117
				,52
				,102
				,113
				,115
				,125
				,103
				,96
				,113
				,102
				,113
				,112
				,52
				,127
				,113
				,109
				};
		
	}

	public static File getCustomEditiXDescriptor() {
		return new File( 
			getAppUserPath(),
			"editix.xml"
		);
	}

	public static void init( String[] args ) {		
		if ( 
			System.getProperty( "user" ) != null && 
				System.getProperty( "key") != null ) {
			try {
				Manager.registered(
					System.getProperty( "user" ),
					System.getProperty( "key") );
			} catch( Exception exc ) {
				System.out.println( "Can't register : " + exc.getMessage() );
			}
		}
		new EditixApplicationModel();	
	}
	
	// For XML formatting usage
	public static int getIndentSpace() {
		return 
			Preferences.getPreference(
				"xmlconfig", "format-space", 1 );
	}	

	public static String getIndentString() {
		String res = "";
		for ( int i = 0; i < getIndentSpace(); i++ ) {
			res += "\t";
		}
		return res;
	}
	
	void checkHck() {
	}

	public void notifyHackerDetected() {
		Manager.getRegisteredFile().delete();
		WDialog.showFor( data );
		System.exit( 1 );
	}
}

