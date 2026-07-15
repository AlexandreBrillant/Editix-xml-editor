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
import java.net.URL;

import com.japisoft.editix.document.DocumentModel;
import com.japisoft.editix.main.EditixApplicationModel;
import com.japisoft.editix.ui.EditixActionBuilder;
import com.japisoft.editix.ui.EditixFactory;
import com.japisoft.framework.ApplicationModel;
import com.japisoft.framework.ApplicationStep;
import com.japisoft.framework.ApplicationModel.ApplicationModelListener;
import com.japisoft.framework.descriptor.InterfaceBuilder;
import com.japisoft.framework.descriptor.helpers.InterfaceBuilderFactory;
import com.japisoft.framework.dialog.DialogManager;
import com.japisoft.framework.dialog.actions.StoringLocationAction;

public class InterfaceBuilderApplicationStep implements ApplicationStep {
	
	public boolean isFinal() {
		// TODO Auto-generated method stub
		return false;
	}

	public void start(String[] args) throws Exception {

		// Interface definition
		URL url = ClassLoader.getSystemResource("editix.xml");
		if ( url == null )
			url = ClassLoader.getSystemResource("com/japisoft/editix/descriptor/editix.xml");
		if (url == null) {
			// Search from the working directory
			File f = new File("res/editix.xml");
			if (f.exists())
				url = f.toURL();
			else
				throw new RuntimeException( "Can't find res/editix.xml" );
		}
		
		if ( url == null )
			throw new RuntimeException(
					"Can't find editix.xml or res/editix.xml ?" );
		
		// Menu building
	
		InterfaceBuilderFactory.setActionBuilder( new EditixActionBuilder() );
		
		InterfaceBuilder builder = null;
	
		URL tmpDescriptorUrl = url;
	
		if ( EditixApplicationModel.getCustomEditiXDescriptor().exists() ) {
			tmpDescriptorUrl = EditixApplicationModel.getCustomEditiXDescriptor().toURL();
		}
	
		try {
			builder = new InterfaceBuilder( tmpDescriptorUrl, "com/japisoft/editix/descriptor/editix.xml" );
			builder.setEnabledActionForAllGroup( false );
			ApplicationModel.addApplicationModelListener( ( ApplicationModelListener )builder.getActionById( "open" ) );

		} catch( Exception exc ) {
			ApplicationModel.debug( exc );
			EditixFactory.buildAndShowErrorDialog( "Can't use the main descriptor [" + tmpDescriptorUrl + ", " + exc.getMessage() + "],\nTry the load the default one..." );
			builder = new InterfaceBuilder( url, "com/japisoft/editix/descriptor/editix.xml" );
		}
		
		EditixApplicationModel.INTERFACE_BUILDER = builder;
		
	}
	
	public void stop() {
		// TODO Auto-generated method stub	
	}
	
	@Override
	public void quit() {
	}

	@Override
	public void setClassLoader(ClassLoader loader) {
	}
	
}
