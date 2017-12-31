package com.japisoft.xflows;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;

import com.japisoft.framework.ApplicationModel;
import com.japisoft.framework.preferences.Preferences;
import com.japisoft.framework.xml.parser.FPParser;
import com.japisoft.framework.xml.parser.node.FPNode;
import com.japisoft.framework.xml.parser.walker.NodeNameCriteria;
import com.japisoft.framework.xml.parser.walker.TreeWalker;
import com.japisoft.xflows.task.Task;
import com.japisoft.xflows.task.ui.builder.ScenarioBuilder;

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
public class XFlowsApplicationModel extends ApplicationModel {
	public static XFlowsApplicationModel ACCESSOR = null;

	XFlowsApplicationModel() {
		ACCESSOR = this;
	}

	private ArrayList list;

	public ArrayList getTasks() {
		if ( list == null )
			list = new ArrayList();
		return list;
	}

	private LoggerModel loggerModel;
	
	public LoggerModel getLogger() {
		if ( loggerModel == null ) {
			loggerModel = new LoggerModel();
		}
		return loggerModel;
	}

	public void setLogger( LoggerModel model ) {
		this.loggerModel = model;
	}

	private static boolean modified = false;
	
	public static void setModified() {
		modified = true;
	}

	public static boolean isModified() {
		return modified;
	}

	public void reload() {
		if ( lastComponent != null )
			setCurrentApplicationComponent( lastComponent );
	}

	public void newProject() {
		currentProjectFile = null;
		list = new ArrayList();
		loggerModel = new LoggerModel();
		reload();
	}

	private ApplicationComponent lastComponent = null;

	public void setCurrentApplicationComponent( 
			ApplicationComponent component ) {
		this.lastComponent = component;
		component.stopEditing();
		component.setApplicationModel( this );
		modified = false;
	}

	private File currentProjectFile = null;

	public File getCurrentProjectFile() { return currentProjectFile; }
	
	public void store( File file ) throws IOException {
		
		
	}
	
	public void store() throws IOException {
		if ( currentProjectFile == null )
			throw new RuntimeException( "Invalid usage" );
		store( currentProjectFile );
	}

	public void read( File file ) throws Exception {
		this.currentProjectFile = file;
		FPParser p = new FPParser();
		FPNode root = ( FPNode )p.parse(new FileReader( file )).getRoot();
		TreeWalker walker = new TreeWalker( root );
		Enumeration enu = walker.getTagNodeByName( "task", false );
		list = new ArrayList();
		while ( enu.hasMoreElements() ) {
			FPNode child = ( FPNode )enu.nextElement();
			Task t = new Task();
			t.updateFromXML( child );
			list.add( t );
		}
		
		FPNode logger = walker.getOneNodeByCriteria( 
				new NodeNameCriteria( "logger" ), false );

		if ( logger != null ) {
			loggerModel = new LoggerModel();
			loggerModel.updateFromXML( logger );
		}
		reload();
		modified = false;
	}
	
	public void init() {
		if ( Preferences.getPreference(
				"interface",
				"restoreLastProject",
				true ) ) {
			String lastProject = 
				Preferences.getPreference(
					Preferences.SYSTEM_GP,
					"lastProjectFile",
					( String )null );
			if ( lastProject != null ) {
				File f = new File( lastProject );
				if ( f.exists() ) {
					try {
						read( f );
					} catch (Exception e) {
					}
				}
			}
		}
	}

	public void stop() {
		if ( currentProjectFile != null )
			Preferences.setPreference(
				Preferences.SYSTEM_GP,
				"lastProjectFile",
				currentProjectFile.toString() );
	}

}
