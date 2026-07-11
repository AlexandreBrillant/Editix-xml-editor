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

package com.japisoft.xflows.task.concat;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import com.japisoft.xflows.task.FilesTaskRunner;
import com.japisoft.xflows.task.TaskContext;

/**
 * @author Alexandre Brillant (https://github.com/AlexandreBrillant/Editix-xml-editor)
 * @version 1.0
 * */
public class ConcatRunner extends FilesTaskRunner {


	public ConcatRunner() {
		super( new FileConcatRunner(), true );
		fileTarget = true;
		defaultProcessingLog = true;
	}

	public boolean run(TaskContext context) {
		
		if ( !context.hasParam( ConcatUI.ROOTTAG ) ) {
			context.addError( "No root tag" );
			return ERROR;
		}

		String target = context.getParam( ConcatUI.TARGETPATH );
		if ( target == null || 
				"".equals( target ) ) {
			context.addError( "No target file" );
			return ERROR;
		}

		try {

			FileWriter fw = new FileWriter( target );
			( ( FileConcatRunner )monoTask ).setFileWriter( fw );
			
			fw.write( "<?xml version=\"1.0\"?>\n" );
			fw.write( "<");
			fw.write( context.getParam( ConcatUI.ROOTTAG ) );
			fw.write( ">\n" );

			boolean ok = super.run( context );			

			fw.write( "\n</");
			fw.write( context.getParam( ConcatUI.ROOTTAG ) );
			fw.write( ">" );

			try {
				fw.close();
			} catch( IOException exc ) {
				context.addError( "Can't close " + target );
				return ERROR;
			}
			if ( ok == ERROR ) {
				new File( target ).delete();
				return ERROR;
			}
			return OK;
		} catch( IOException exc ) {
			context.addError( "Error while writing on " + target + ":" + exc.getMessage() );
			return ERROR;
		}

	}	

}
