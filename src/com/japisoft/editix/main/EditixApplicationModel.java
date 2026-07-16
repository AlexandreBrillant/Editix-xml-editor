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

package com.japisoft.editix.main;

import java.io.File;


import com.japisoft.editix.main.steps.CheckNewVersionStep;
import com.japisoft.editix.main.steps.ConfigurationApplicationStep;
import com.japisoft.editix.main.steps.EPStep;
import com.japisoft.editix.main.steps.InitParserStep;
import com.japisoft.editix.main.steps.InterfaceBuilderApplicationStep;
import com.japisoft.editix.main.steps.MainFrameApplicationStep;
import com.japisoft.editix.main.steps.MenuScriptsStep;

import com.japisoft.editix.main.steps.SplashScreenApplicationStep;
import com.japisoft.editix.main.steps.StartingFilesApplicationStep;
import com.japisoft.editix.main.steps.TestApplicationStep;
import com.japisoft.editix.main.steps.XMLApplicationStep;
import com.japisoft.editix.main.steps.XMLPadApplicationStep;
import com.japisoft.editix.main.steps.lookandfeel.LookAndFeelApplicationStep;
import com.japisoft.framework.ApplicationModel;
import com.japisoft.framework.descriptor.InterfaceBuilder;
import com.japisoft.framework.llm.DefaultLLMContext;
import com.japisoft.framework.llm.LLMContext;
import com.japisoft.framework.preferences.Preferences;

/**
 * @author Alexandre Brillant (https://github.com/AlexandreBrillant/Editix-xml-editor)
 * @version 1.0
 * */
public class EditixApplicationModel extends ApplicationModel {
	
	static {

		
		addApplicationStep( new SplashScreenApplicationStep() );
		addApplicationStep( new EPStep() );

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
	}

	public static File getCustomEditiXDescriptor() {
		return new File( 
			getAppUserPath(),
			"editix.xml"
		);
	}

	public static File getEditixLLMContext() {
		return new File( getAppUserPath(), "context.xml" );
	}
	
	private static LLMContext DEFAULT = null;
	
	public static LLMContext getDefaultEditixLLMContext() {
		if ( DEFAULT == null ) {
			try {
				DEFAULT = new DefaultLLMContext( getEditixLLMContext() );
			} catch( Exception exc ) {
				DEFAULT = new DefaultLLMContext();
			}
		}
		return DEFAULT;
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
	
}
