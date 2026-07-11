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

package com.japisoft.xflows.task.exp.json;

import java.io.File;
import java.io.FileInputStream;

import org.json.JSONObject;
import org.json.XML;

import com.japisoft.framework.toolkit.FileToolkit;
import com.japisoft.framework.xml.XMLFileData;
import com.japisoft.framework.xml.XMLToolkit;
import com.japisoft.xflows.task.TaskContext;
import com.japisoft.xflows.task.TaskRunner;

/**
 * Export an XML file to JSON (*.json *.jso)
 * @author Alexandre Brillant (https://github.com/AlexandreBrillant/Editix-xml-editor) */
public class JSonFileRunner implements TaskRunner  {

	public boolean run( TaskContext context ) {
		context.addInfo( 
			"Exporting " + 
				context.getCurrentSourceFile() );

		File source = context.getCurrentSourceFile();
		File target = context.getCurrentTargetFile();

		try {
			String xmlData = context.getTaskSource();
			
			if ( xmlData == null ) {				
				XMLFileData xfd = XMLToolkit.getContentFromInputStream( 
					new FileInputStream( source ), 
					null 
				);
				xmlData = xfd.getContent();
			}
			
			JSONObject json = XML.toJSONObject( xmlData );
			String result = json.toString();
			FileToolkit.writeFile( 
				target, 
				result, 
				context.getDefaultEncoding() 
			);

		} catch( Throwable exc ) {
			context.addError(
				exc.getMessage() 
			);
			return TaskRunner.ERROR;
		}

		return TaskRunner.OK;
	}	

}
