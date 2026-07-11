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

package com.japisoft.xflows.task.jsx;

import java.io.File;

import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptException;

import com.japisoft.editix.editor.jsx.action.NodeFactory;
import com.japisoft.editix.editor.jsx.domapi.Document;
import com.japisoft.editix.javascript.JavaScriptFactory;
import com.japisoft.framework.toolkit.FileToolkit;
import com.japisoft.xflows.task.TaskContext;
import com.japisoft.xflows.task.TaskRunner;

/**
 * @author Alexandre Brillant (https://github.com/AlexandreBrillant/Editix-xml-editor)
 * @version 1.0
 */
public class JSXTFileRunner implements TaskRunner {

	private TaskContext context = null;

	public boolean run(TaskContext context) {
		this.context = context;
		try {
			String fstylesheet = context.getParam(JSXUI.JSX);
			context.addInfo("Updating " + context.getCurrentSourceFile());
			return applyTransformation( context, context
					.getCurrentSourceFile(), fstylesheet, context
					.getCurrentTargetFile());
		} finally {
			this.context = null;
		}
	}

	public static boolean applyTransformation(
			TaskContext context, File data, String jsx, File res) {

		ScriptEngine engine = JavaScriptFactory.newFactory().engine();
		Document doc = null;
		
		try {
			Bindings b = engine.getBindings(ScriptContext.GLOBAL_SCOPE);
			doc = NodeFactory.newInstance().getDocument( data.toString() );					
			b.put( "document", doc );
		} catch( Exception exc ) {
			context.addError( "Can't parse this XML document " + data + " ?" );
			return ERROR;
		}

		try {
			String jsxContent = FileToolkit.getContentFromFileName( new File( jsx ), "UTF-8" );
			engine.eval( jsxContent );
			doc.save();
		} catch( ScriptException exc ) {
			context.addError( "Invalid Script JSX : " + exc.getMessage() );
			return ERROR;
		} catch( Throwable th ) {
			context.addError( "Can't load the JSX file " + jsx + " ?" );
			return ERROR;
		}
		
		return OK;
	}

}