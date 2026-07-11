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

package com.japisoft.xflows.task.imp.cvs;

import java.io.FileWriter;
import java.io.IOException;

import com.japisoft.framework.xml.imp.CSVImport;
import com.japisoft.xflows.task.TaskContext;
import com.japisoft.xflows.task.TaskRunner;

/**
 * @author Alexandre Brillant (https://github.com/AlexandreBrillant/Editix-xml-editor)
 * @version 1.0
 * */
public class CSVFileRunner implements TaskRunner {

	public boolean run( TaskContext context ) {
		context.addInfo( "Importing " + context.getCurrentSourceFile() );

		CSVImportParamsImpl params = 
			new CSVImportParamsImpl( context.getParams() );

		try {
			String buffer = CSVImport.impCSV(
					context.getCurrentSourceFile(),
					params );
			FileWriter writer = new FileWriter( context.getCurrentTargetFile() );
			try {
				writer.write( buffer );
			} finally {
				writer.close();
			}
		} catch (IOException e) {
			context.addError( e.getMessage() );
			return TaskRunner.ERROR;
		}

		return TaskRunner.OK;
	}

}
