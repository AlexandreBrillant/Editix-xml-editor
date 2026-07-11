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

package com.japisoft.framework.step;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import com.japisoft.framework.ApplicationModel;
import com.japisoft.framework.ApplicationStepAdapter;
import com.japisoft.framework.ApplicationStepException;
import com.japisoft.framework.application.descriptor.InterfaceBuilder;
import com.japisoft.framework.application.descriptor.InterfaceBuilderException;
import com.japisoft.framework.application.descriptor.helpers.ActionBuilder;

public class InterfaceBuilderStep extends ApplicationStepAdapter {
	private String descriptorPath = null;
	private URL descriptorURL = null;
	
	private ActionBuilder actionBuilder = null;

	public InterfaceBuilderStep() {
		/* empty */
	}

	public InterfaceBuilderStep( URL descriptorURL ) {
		this.descriptorURL = descriptorURL;
	}
	
	public InterfaceBuilderStep(String descriptorPath) {
		this.descriptorPath = descriptorPath;
	}

	public InterfaceBuilderStep(ActionBuilder actionBuilder) {
		this.actionBuilder = actionBuilder;
	}

	public InterfaceBuilderStep(
			String descriptorPath,
			ActionBuilder actionBuilder) {
		this(descriptorPath);
		this.actionBuilder = actionBuilder;
	}

	public void start(String[] args) {
		String path = descriptorPath;
		if (path == null)
			path = ApplicationModel.USERINTERFACE_FILE;

		
		try {
			URL url = descriptorURL;
			if ( url == null ) {
				url = ClassLoader.getSystemResource(path);	
			}

			if ( url == null ) {
				File f = new File(path);
				if (f.exists())
					url = f.toURI().toURL();
				else
					throw new ApplicationStepException(
							"Can't find " + path, true );
			}
			if ( url == null )
				throw new ApplicationStepException(
						"Can't find " + path, true );
			InterfaceBuilder builder = new InterfaceBuilder(
					url, 
					actionBuilder,
					null);
			ApplicationModel.INTERFACE_BUILDER = builder;
		} catch (MalformedURLException e) {
			throw new ApplicationStepException(e, true);
		} catch (InterfaceBuilderException e) {
			throw new ApplicationStepException(e, true);
		}
	}
}
