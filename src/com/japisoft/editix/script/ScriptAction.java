package com.japisoft.editix.script;

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileNotFoundException;

import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.KeyStroke;

import com.japisoft.editix.plugin.EditiXManager;
import com.japisoft.editix.ui.EditixFactory;

/**
This program is available under two licenses : 

1. For non commercial usage : 

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.

2. For commercial usage :

You need to get a commercial license for source usage at : 

http://www.editix.com/buy.html

Copyright (c) 2018 Alexandre Brillant - JAPISOFT SARL - http://www.japisoft.com

@author Alexandre Brillant - abrillant@japisoft.com
@author JAPISOFT SARL - http://www.japisoft.com

*/
public class ScriptAction extends AbstractAction {

	private File path = null;
	
	public ScriptAction( Script s ) {
		putValue( Action.NAME, s.getName() );
		if ( !"".equals( s.getShortkey() ) );
		putValue( Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke( s.getShortkey() ) );
		this.path = s.getPath();
	}
	
	public ScriptAction( File path ) {
		this.path = path;
	}
	
	public boolean errorFound = false;
	
	public void actionPerformed(ActionEvent e) {
		errorFound = false;
		if ( !path.exists() ) {
			EditixFactory.buildAndShowErrorDialog( "Can't find this script" );
		} else {
			 // create a script engine manager
	        ScriptEngineManager factory = new ScriptEngineManager();
	        // create JavaScript engine
	        ScriptEngine engine = factory.getEngineByName("JavaScript");
	        // evaluate JavaScript code from given file - specified by first argument
	        try {
	        	Bindings b = engine.getBindings(ScriptContext.GLOBAL_SCOPE);
	        	b.put( "EditiXManager", EditiXManager.getInstance());
	        	b.put( "EditixManager", EditiXManager.getInstance());
	        	engine.eval(new java.io.FileReader( path ));
	        } catch( ScriptException exc ) {
	        	EditixFactory.buildAndShowErrorDialog( "Error(s) in your script : line " + exc.getLineNumber() );
	        	errorFound = true;
	        } catch( FileNotFoundException fne ) {
	        }
		}
	}

}
