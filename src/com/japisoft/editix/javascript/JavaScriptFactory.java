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

package com.japisoft.editix.javascript;

import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import com.japisoft.editix.plugin.EditiXManager;
import com.japisoft.editix.ui.EditixFrame;

public class JavaScriptFactory {

	private static JavaScriptFactory THIS = null;
	
	private JavaScriptFactory() {
		THIS = this;
	}
	
	public static JavaScriptFactory newFactory() {
		if ( THIS == null )
			THIS = new JavaScriptFactory();
		return THIS;
	}
	
	private String[] defaultFunctions = {
		"function alert(msg) {com.japisoft.editix.ui.EditixFactory.buildAndShowInformationDialog(msg);}",
		"function prompt() {"
		+ "if (arguments.length > 1)"
			+ "return com.japisoft.editix.ui.EditixFactory.buildAndShowInputDialog(arguments[0],arguments[1]);"
		+ "if (arguments.length == 1)"
			+ "return com.japisoft.editix.ui.EditixFactory.buildAndShowInputDialog(arguments[0],\"\");"
		+ "return \"\";}"
	};
	
	public ScriptEngine engine() {
		ScriptEngineManager manager = new ScriptEngineManager();
		ScriptEngine engine = manager.getEngineByExtension( "js" );
		if ( engine == null )
			engine = manager.getEngineByName( "javascript" );
		if ( engine == null )
			engine = new EmptyEngine();

		Bindings b = engine.getBindings(ScriptContext.GLOBAL_SCOPE);
		try {
			for ( String func : defaultFunctions )
				engine.eval( func );
		} catch( ScriptException exc ) {
			System.err.println( "Can't use this JavaScript function :" + exc.getMessage() );
		}
		if ( b != null ) {
			b.put( "console", new Console() );
			b.put( "EditiXManager", EditiXManager.getInstance());
			b.put( "EditixManager", EditiXManager.getInstance());
		}
		return engine;
	}
	
}

