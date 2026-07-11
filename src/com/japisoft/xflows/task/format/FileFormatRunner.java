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

package com.japisoft.xflows.task.format;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;

import com.japisoft.framework.xml.refactor.Refactor;
import com.japisoft.xflows.task.TaskContext;
import com.japisoft.xflows.task.TaskRunner;

public class FileFormatRunner implements TaskRunner {

	public boolean run(TaskContext context) {

		File source = context.getCurrentSourceFile();
		Refactor r = new Refactor();
		try {
			String newDoc = r.format( source );
			String encoding = r.getXMLEncoding();
			OutputStreamWriter output = 
				new OutputStreamWriter(
						new FileOutputStream( context.getCurrentTargetFile() ),
						encoding );
			try {
				output.write( newDoc );
			} finally {
				output.close();
			}
			
		} catch (Exception e) {
			context.addError( "Can't format file " + source + " : " + e.getMessage() );
		}

		return false;
	}	
	
}
