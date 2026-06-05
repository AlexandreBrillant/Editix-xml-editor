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

package com.japisoft.xflows;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;

import com.japisoft.framework.ApplicationModel;
import com.japisoft.framework.preferences.Preferences;
import com.japisoft.framework.xml.parser.FPParser;
import com.japisoft.framework.xml.parser.node.FPNode;
import com.japisoft.framework.xml.parser.walker.NodeNameCriteria;
import com.japisoft.framework.xml.parser.walker.TreeWalker;
import com.japisoft.xflows.task.Task;
import com.japisoft.xflows.task.ui.builder.ScenarioBuilder;

/**
 * @author Alexandre Brillant (https://github.com/AlexandreBrillant/Editix-xml-editor)
 * @version 1.0 */
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
		Iterator enu = walker.getTagNodeByName( "task", false );
		list = new ArrayList();
		while ( enu.hasNext() ) {
			FPNode child = ( FPNode )enu.next();
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

