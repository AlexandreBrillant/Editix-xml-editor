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

package com.japisoft.xflows.task.imp.json;

import java.io.File;
import java.io.FileInputStream;

import org.json.JSONObject;
import org.json.XML;

import com.japisoft.framework.toolkit.FileToolkit;
import com.japisoft.framework.xml.XMLToolkit;
import com.japisoft.xflows.task.TaskContext;
import com.japisoft.xflows.task.TaskRunner;

/**
 * Import a JSON file (.json .jso) to XML
 * @author Alexandre Brillant (https://github.com/AlexandreBrillant/Editix-xml-editor) */
public class JSonFileRunner implements TaskRunner  {

	public boolean run( TaskContext context ) {
		context.addInfo( 
			"Importing " + 
				context.getCurrentSourceFile() );

		File source = context.getCurrentSourceFile();
		File target = context.getCurrentTargetFile();

		try {

			String jsonData = FileToolkit.getContentFromInputStream(
				new FileInputStream( source ),
				context.getDefaultEncoding() 
			);

			String resultContent = XML.toString(
				new JSONObject( jsonData ),
				"root"
			);

			if ( target == null ) {
				context.setTaskResult( "<?xml version='1.0'?>\n" + resultContent );
			} else {
				XMLToolkit.save( target, resultContent );
			}

		} catch( Throwable exc ) {
			context.addError(
				exc.getMessage() 
			);
			return TaskRunner.ERROR;
		}

		return TaskRunner.OK;
	}	

}

