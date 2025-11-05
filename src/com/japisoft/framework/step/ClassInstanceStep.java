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

package com.japisoft.framework.step;

import com.japisoft.framework.ApplicationModel;
import com.japisoft.framework.ApplicationStepAdapter;
import com.japisoft.framework.ApplicationStepException;

/**
 * Here a way to create an object from a class when starting the
 * application
 * @author Alexandre Brillant (https://github.com/AlexandreBrillant/Editix-xml-editor)
 * @version 1.0
 * */
public class ClassInstanceStep extends ApplicationStepAdapter {
	
	private boolean macMode = false;
	private String className = null;
	
	/** A class name for building the object */
	public ClassInstanceStep( String className ) {
		this.className = className;
	}

	/** A class name for building the object.
	 * @param className A class name
	 * @param forMacOSXOnly decide to use it only for Mac OS X
	 */
	public ClassInstanceStep( String className, boolean forMacOSXOnly ) {
		this( className );
		this.macMode = forMacOSXOnly;
	}

	public void start(String[] args) {
		try {
			if ( !macMode || ApplicationModel.MACOSX_MODE ) {
				Class.forName( className ).newInstance();
			}
		} catch (InstantiationException e) {
			throw new ApplicationStepException( e, true );
		} catch (IllegalAccessException e) {
			throw new ApplicationStepException( e, true );
		} catch (ClassNotFoundException e) {
			throw new ApplicationStepException( e, true );
		}	
	}

}

