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

package com.japisoft.xflows.task.jsx;

import java.io.File;

import com.japisoft.xflows.task.FilesTaskRunner;
import com.japisoft.xflows.task.TaskContext;

/**
 * @author Alexandre Brillant (https://github.com/AlexandreBrillant/Editix-xml-editor)
 * @version 1.0
 * */
public class JSXRunner extends FilesTaskRunner {


	public JSXRunner() {
		super( new JSXTFileRunner(), false );
	}

	public boolean run( TaskContext context ) {
		
		String sourcePath = context.getParam( SOURCEPATH );
		
		
		String jsx = context.getParam( JSXUI.JSX );
		if ( jsx == null ) {
			context.addError( "No JavaScript (JSX) file ?" );
			return ERROR;
		} else {
			
			if ( jsx.indexOf( "://" ) == -1 ) {
				File fstylesheet = new File( jsx );
				if ( !fstylesheet.exists() ) {
					context.addError( "JavaScript (JSX) " + fstylesheet + " not found ");
					return ERROR;
				}
			}
			
			super.run( context );
		}

		return OK;
	}

}

