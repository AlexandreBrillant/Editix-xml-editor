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

import java.io.File;

import com.japisoft.editix.ep.EPManager;
import com.japisoft.editix.ui.EditixActionBuilder;
import com.japisoft.editix.ui.EditixFactory;
import com.japisoft.framework.ApplicationStep;
import com.japisoft.framework.ApplicationStepAdapter;
import com.japisoft.framework.jar.JarsClassLoader;

public class EPStep extends ApplicationStepAdapter {

	private boolean epFlag = false;
	
	@Override
	public void start(String[] args) {
		
		if ( EPManager.instance().hasEp() ) {
			epFlag = true;
			
			File root = EPManager.instance().getEPHome();
			File libRoot = new File( root, "lib" );
			String[] librairies = libRoot.list();
			if ( librairies == null ) {
				EditixFactory.buildAndShowWarningDialog( "Can't find the Extension Pack librairies ?" );
			} else {
				try {
					JarsClassLoader loader = new JarsClassLoader();
					for ( String lib : librairies ) {
						loader.addJar( new File( libRoot, lib ) );
					}
					Class[] cls = loader.scanClasses( getClass().getClassLoader(), "Step" );
					for ( Class cl : cls  ) {
						ApplicationStep step = ( ApplicationStep )cl.newInstance();
						step.setClassLoader( getClass().getClassLoader() );
						step.start( args );
					}
				} catch( Exception exc ) {
					EditixFactory.buildAndShowErrorDialog( "Can't use the Extension Pack : " + exc.getMessage() );
					exc.printStackTrace();
				}
			}			
		}

	}

	@Override
	public void stop() {
		super.stop();
		if ( epFlag ) 
			EditixFactory.buildAndShowInformationDialog( "Extension Pack is ready" );
	}
	
}
