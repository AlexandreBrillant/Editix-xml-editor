package com.japisoft.editix.script;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JMenu;

import com.japisoft.framework.ApplicationModel;
import com.japisoft.framework.xml.parser.FPParser;
import com.japisoft.framework.xml.parser.document.Document;
import com.japisoft.framework.xml.parser.node.FPNode;

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
public class ScriptModel {

	private static ScriptModel THIS = null;

	public static ScriptModel getInstance() {
		if ( THIS == null )
			THIS = new ScriptModel();
		return THIS;
	}

	private File path = new File( ApplicationModel.getAppUserPath(), "scripts.xml" );
	
	private List<Script> scripts = null;
	
	private ScriptModel() {
		load();
	}
	
	private void load() {
		scripts = new ArrayList<Script>();
		if ( path.exists() ) {
			try {
				FPParser fp = new FPParser();
				Document doc = fp.parse( new FileInputStream( path ) );
				FPNode root = ( FPNode )doc.getRoot();
				for ( int i = 0; i < root.childCount(); i++ ) {
					FPNode script = root.childAt( i );
					scripts.add( new BasicScript( script.getAttribute( "name" ), new File( script.getAttribute( "path" ) ), script.getAttribute( "shortkey" ) ) );
				}
			} catch( Exception exc ) {
				System.err.println( "Can't parse " + path );
				ApplicationModel.debug( exc );
			}
		}
	}
	
	public List<Script> getScripts() {
		return scripts;
	}
	
	public void setScripts( List<Script> s ) throws IOException {
		this.scripts = s;
		save();
	}

	public void save() throws IOException {
		FPNode root = new FPNode( FPNode.TAG_NODE, "scripts" );
		for ( Script s : scripts ) {
			root.appendChild( new FPNode( FPNode.TAG_NODE, "script" ) ).att( "path", s.getPath().toString() ).att( "name", s.getName() ).att( "shortkey", s.getShortkey() );
		}
		FileWriter fw = new FileWriter( path );
		fw.write( root.getRawXML() );
		fw.close();
		synchroMenu();
	}

	public void synchroMenu() {
		JMenu menu = ApplicationModel.INTERFACE_BUILDER.getMenu( "runscript" );
		if ( menu == null ) {
			System.err.println( "Can't find the menu runscript ?" );
		} else {
			menu.removeAll();
			for ( int i = 0; i < getScripts().size(); i++ ) {
				menu.add( new ScriptAction( getScripts().get( i ) ) ); 
			}
			menu.setEnabled( menu.getItemCount() > 0 );
		}
	}

}
