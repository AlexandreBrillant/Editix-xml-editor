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
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import com.japisoft.xflows.task.TaskContext;
import com.japisoft.xflows.task.TaskRunner;

/**
 * @author Alexandre Brillant (https://github.com/AlexandreBrillant/Editix-xml-editor)
 * @version 1.0
 * */
public class FileConcatRunner implements TaskRunner {

	
	private FileWriter fileWriter = null;
	
	public void setFileWriter( FileWriter fileWriter ) {
		this.fileWriter = fileWriter;
	}

	public boolean run( TaskContext context ) {

		File source = context.getCurrentSourceFile();
		char[] data = new char[ ( int )source.length() ];

		try {

			FileReader fr = new FileReader( source );
			try {
				fr.read( data );
			} finally {
				fr.close();
			}

			boolean tagFound = false;
			
			// Search for the starting tag
			for ( int i = 0; i < data.length - 1; i++ ) {
				if ( data[ i ] == '<' && !( data[ i + 1 ] == '?' || data[ i + 1 ] == '!' ) ) {
					fileWriter.write( data, i, data.length - i );
					tagFound = true;
					break;
				}
			}
			
			if ( !tagFound )
				context.addWarning( "No tag found" );

		} catch( FileNotFoundException exc ) {
			context.addError( "Can't find " + source );
			return ERROR;
		} catch( IOException exc ) {
			context.addError( "Error while reading " + 
					source + " : " + exc.getMessage() );
			return ERROR;
		}

		return OK;
	}

}
