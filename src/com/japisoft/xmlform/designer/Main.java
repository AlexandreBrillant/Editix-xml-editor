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

package com.japisoft.xmlform.designer;

import com.japisoft.framework.ApplicationException;
import com.japisoft.framework.ApplicationMain;
import com.japisoft.framework.dialog.DialogManager;
import com.japisoft.framework.step.InterfaceBuilderStep;
import com.japisoft.framework.step.LookAndFeelStep;
import com.japisoft.framework.toolkit.Toolkit;
import com.japisoft.framework.ui.toolkit.FileManager;
import com.japisoft.xmlform.designer.step.FrameBuildingStep;
import com.jgoodies.looks.plastic.Plastic3DLookAndFeel;

public class Main extends ApplicationMain {

	/**
	 * @param args */
	public static void main(String[] args) {

		DialogManager.setDefaultDialogIcon(
				Toolkit.getImageIcon( "images/dialog.png" ) );
		
		new XmlFormModel();
		
		XmlFormModel.addApplicationStep(
				new LookAndFeelStep( 
					Plastic3DLookAndFeel.class.getName() ) );

		XmlFormModel.addApplicationStep(
			new InterfaceBuilderStep( 
				Main.class.getResource( "xfd.xml" )
		) );

		XmlFormModel.addApplicationStep(
				new FrameBuildingStep()
		);

		FileManager.setPreferenceForLastFilePath( true );		
		
		try {
			Main.start( args );
		} catch ( ApplicationException e ) {
			e.printStackTrace();
		}

	}

}
