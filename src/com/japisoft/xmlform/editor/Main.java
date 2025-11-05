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

package com.japisoft.xmlform.editor;

import com.japisoft.framework.ApplicationException;
import com.japisoft.framework.ApplicationMain;
import com.japisoft.framework.step.InterfaceBuilderStep;
import com.japisoft.framework.step.LookAndFeelStep;
import com.japisoft.framework.ui.toolkit.FileManager;
import com.japisoft.xmlform.editor.step.FrameBuildingStep;
import com.jgoodies.looks.plastic.Plastic3DLookAndFeel;

public class Main extends ApplicationMain {

	/**
	 * @param args */
	public static void main(String[] args) {

		
		new EditorModel( args );
		
		EditorModel.addApplicationStep(
				new LookAndFeelStep( 
					Plastic3DLookAndFeel.class.getName() ) );

		System.out.println( "Running Editor" );

		EditorModel.addApplicationStep(
				new InterfaceBuilderStep(
					Main.class.getResource( "xfe.xml" ) ) );

		EditorModel.addApplicationStep(
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

