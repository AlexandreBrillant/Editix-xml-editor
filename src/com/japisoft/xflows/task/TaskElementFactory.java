package com.japisoft.xflows.task;

import java.io.File;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import com.japisoft.framework.xml.parser.ParseException;
import com.japisoft.framework.xml.parser.FPParser;
import com.japisoft.framework.xml.parser.node.FPNode;
import com.japisoft.framework.xml.parser.walker.AttributeCriteria;
import com.japisoft.framework.xml.parser.walker.TreeWalker;

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
public class TaskElementFactory {

	static String[] TYPES = null;
	static String[] HELP = null;
	
	public static String[] getAvailableTypes() {
		prepare();
		return TYPES;
	}

	private static HashMap MAPUI = new HashMap();

	private static void prepareMapUI() {
	}

	private static HashMap MAPRUNNER = new HashMap();

	private static void prepareMapRunner() {
	}

	private static HashMap classInstance = null;

	static boolean prepared = false;
	static FPNode rootNode = null;

	public static void updateInnerNode( FPNode root ) {
		rootNode = root;
	}
	
	public static FPNode getNewDocumentRoot() {
		InputStream input = ClassLoader.getSystemClassLoader()
				.getResourceAsStream( "tasks.xml" );
		if (input == null) {
			throw new RuntimeException( "Can't find tasks.xml !!" );
		} else {
			FPParser p = new FPParser();
			try {
				return ( FPNode )p.parse( input ).getRoot();
			} catch (ParseException e) {
				throw new RuntimeException( "Can't parse tasks.xml !! " );
			}
		}
	}

	public static FPNode prepare() {
		if (prepared)
			return rootNode;

		rootNode = getNewDocumentRoot();

		ArrayList l = new ArrayList();
		ArrayList l2 = new ArrayList();
		for (int i = 0; i < rootNode.childCount(); i++) {
			FPNode taskNode = rootNode.childAt( i );
			l.add(taskNode.getAttribute( "name" ) );
		}

		Collections.sort( l );
		
		TYPES = new String[l.size()];
		for (int i = 0; i < l.size(); i++)
			TYPES[i] = (String) l.get(i);

		prepared = true;
		return rootNode;
	}

	static Class loadClass(String type, String attribute, HashMap map) {
		Class cl = (Class) map.get(type);

		if (cl != null)
			return cl;

		TreeWalker walker = new TreeWalker(rootNode);
		FPNode task = walker.getOneNodeByCriteria(new AttributeCriteria(
				"name", type), true);

		if (task == null)
			throw new RuntimeException( "Error wrong type " + type );

		String classe = task.getAttribute( attribute );
		String archive = task.getAttribute( "archive" );
		if (archive == null) {
			try {
				cl = Class.forName(classe);
			} catch (ClassNotFoundException e) {
				throw new RuntimeException("Error wrong type definition for "
						+ type + " cannot find " + task.getAttribute(attribute));
			}
		} else {

			try {
				URLClassLoader loader = new URLClassLoader(
						new URL[] { new File(archive).toURL() });
				cl = loader.loadClass(classe);
			} catch (MalformedURLException e) {
				throw new RuntimeException("Can't use " + archive);
			} catch (ClassNotFoundException e) {
				throw new RuntimeException("Error wrong type definition for "
						+ type + " cannot find " + task.getAttribute(attribute));
			}

		}

		map.put(type, cl);

		return cl;
	}

	public static String getHelpForType( String type ) {
		if ( type == null )
			return "";
		FPNode root = prepare();
		TreeWalker tw = new TreeWalker( root );
		FPNode node = tw.getOneNodeByCriteria(
				new AttributeCriteria( "name", type ), false );
		if ( node == null )
			return "";
		else
			return node.getAttribute( "help", "" );
	}
	
	public static TaskUI getUIForType(String type) {
		prepare();

		Class cl = loadClass(type, "ui", MAPUI);

		TaskUI ui = null;
		if (classInstance == null) {
			classInstance = new HashMap();
		}
		ui = (TaskUI) classInstance.get(cl);
		if (ui == null) {
			try {
				ui = (TaskUI) cl.newInstance();
				classInstance.put(cl, ui);
			} catch (Exception exc) {
				exc.printStackTrace();
			}
		}
		return ui;
	}

	public static TaskRunner getRunnerForType(String type) {
		prepare();

		Class cl = loadClass(type, "class", MAPRUNNER);
		TaskRunner runner = null;
		if (classInstance == null) {
			classInstance = new HashMap();
		}
		runner = (TaskRunner) classInstance.get(cl);
		if (runner == null) {
			try {
				runner = (TaskRunner) cl.newInstance();
				classInstance.put(cl, runner);
			} catch (Exception exc) {
				exc.printStackTrace();
			}
		}
		return runner;
	}

}
