package com.japisoft.editix.main;

import java.io.File;

import com.japisoft.editix.main.steps.CheckNewVersionStep;
import com.japisoft.editix.main.steps.ConfigurationApplicationStep;
import com.japisoft.editix.main.steps.InterfaceBuilderApplicationStep;
import com.japisoft.editix.main.steps.LookAndFeelApplicationStep;
import com.japisoft.editix.main.steps.MainFrameApplicationStep;
import com.japisoft.editix.main.steps.MenuScriptsStep;
import com.japisoft.editix.main.steps.SplashScreenApplicationStep;
import com.japisoft.editix.main.steps.StartingFilesApplicationStep;
import com.japisoft.editix.main.steps.XMLApplicationStep;
import com.japisoft.editix.main.steps.XMLPadApplicationStep;
import com.japisoft.framework.ApplicationModel;
import com.japisoft.framework.application.descriptor.InterfaceBuilder;
import com.japisoft.framework.preferences.Preferences;

import com.japisoft.p3.HackerController;
import com.japisoft.p3.Manager;
import com.japisoft.p3.info.WDialog;

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
public class EditixApplicationModel extends ApplicationModel {
	
	static {

		addApplicationStep( new CheckNewVersionStep() );
		addApplicationStep( new SplashScreenApplicationStep() );
		addApplicationStep( new ConfigurationApplicationStep() );
		addApplicationStep( new LookAndFeelApplicationStep() );		
		addApplicationStep( new InterfaceBuilderApplicationStep() );
		addApplicationStep( new XMLApplicationStep() );
		addApplicationStep( new XMLPadApplicationStep() );
		addApplicationStep( new MainFrameApplicationStep() );
		addApplicationStep( new StartingFilesApplicationStep() );
		addApplicationStep( new MenuScriptsStep() );
		addApplicationStep( new CheckNewVersionStep() );

	}

	public static EditixApplicationModel ACCESSOR = null;

	public static InterfaceBuilder INTERFACE_BUILDER = null;

	static int[] data;
	
	EditixApplicationModel() {
		ACCESSOR = this;		
		
	}

	public static File getCustomEditiXDescriptor() {
		return new File( 
			getAppUserPath(),
			"editix.xml"
		);
	}

	public static void init( String[] args ) {		
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

}
