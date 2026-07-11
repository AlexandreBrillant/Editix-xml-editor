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

package com.japisoft.xflows.task.xquery;

import java.io.File;

import com.japisoft.xflows.task.FilesTaskRunner;
import com.japisoft.xflows.task.TaskContext;

/**
 * @author Alexandre Brillant (https://github.com/AlexandreBrillant/Editix-xml-editor)
 * @version 1.0
 * */
public class XQueryRunner extends FilesTaskRunner {

	public XQueryRunner() {
		super( new XQueryFileRunner(), true );
	}

	public boolean run( TaskContext context ) {
		
		String xquery = context.getParam( XQueryUI.XQUERY );
		if ( xquery == null ) {
			context.addError( "No XQuery file" );
			return ERROR;
		} else {

			File fxquery = new File( xquery );
			if ( !fxquery.exists() ) {
				context.addError( "XQuery file " + fxquery + " not found ");
				return ERROR;
			}

			super.run( context );
		}

		return OK;
	}

}
