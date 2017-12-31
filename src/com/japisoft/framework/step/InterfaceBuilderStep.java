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
