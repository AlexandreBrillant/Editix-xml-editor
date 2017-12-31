package com.japisoft.editix.ui;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;

import javax.swing.Action;

import com.japisoft.editix.script.ScriptAction;
import com.japisoft.framework.application.descriptor.ActionModel;
import com.japisoft.framework.ApplicationModel;
import com.japisoft.framework.application.descriptor.InterfaceBuilderException;
import com.japisoft.framework.application.descriptor.helpers.ActionBuilder;
import com.japisoft.framework.xml.parser.node.FPNode;
import com.japisoft.framework.xml.parser.walker.TreeWalker;
import com.japisoft.xmlpad.action.XMLAction;

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
public class EditixActionBuilder implements ActionBuilder {

	public Action buildAction(FPNode source, String action)
			throws InterfaceBuilderException {

		// Check for action child
		TreeWalker tw = new TreeWalker( source );
		FPNode actionNode = tw.getFirstTagNodeByName( "action", false );
		if  ( actionNode == null )
			actionNode = source;

		boolean editor = "true".equals(
				actionNode.getAttribute( "editor" ) );

		Action a = null;

		// Search it from the XMLPad ActionModel
		if ( editor )
			a = ( Action ) com.japisoft.xmlpad.action.ActionModel
					.getActionByName( action );
		
		if ( a == null ) {
			try {

				ClassLoader loader = getClass().getClassLoader();

				if ( actionNode.hasAttribute( "libraries" ) ) {					
					String path = actionNode.getAttribute( "libraries" );
					
					if ( path.toLowerCase().endsWith( ".js" ) ) { 	// JavaScript code
						a = new ScriptAction( new File( path ) );
					} else {	// Java code
					
						String[] pathTab = path.split( ";" );
						URL[] u = new URL[ pathTab.length ];
						int i = 0;
						for ( String pt : pathTab) {
							File f = new File( pt );
							if ( !( f.exists() ) ) {
								throw new InterfaceBuilderException( "Can't find the library [" + pt + "] ?" );
							}
							u[ i++ ] = f.toURI().toURL();
						}
						loader = new URLClassLoader( u, loader );
						
					}
				}

				if ( a == null ) {
				
					Class cl = null;
	
					if ( SOL2.equals( action ) )
						cl = AboutAction.class;
					else if ( SOL1.equals( action ) )
						cl = RA.class;
					else
						cl = loader.loadClass( action );
									
					a = ( Action ) cl.newInstance();
					if ( editor )
						com.japisoft.xmlpad.action.ActionModel
								.addActionForGroup(
										com.japisoft.xmlpad.action.ActionModel.TOOLKIT_GROUP,
										( XMLAction ) a );
					
				}

			} catch ( Throwable th ) {
				ApplicationModel.debug( th );
				throw new InterfaceBuilderException("Cannot build "
						+ th.getMessage(), th);
			}

		}

		String id = source.getFPParent().getAttribute( "id" );
		if ( id != null && a != null )
			ActionModel.storeAction( id, a );
		
		return a;
	}
	
	static String SOL1 = compute("IN", "1");
	static String SOL2 = compute("IN", "2");
	static String compute(String a, String b) {
		return a + "NER_" + b;
	}

}
