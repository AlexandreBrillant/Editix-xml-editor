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
 * @author Alexandre Brillant (https://github.com/AlexandreBrillant/Editix-xml-editor)
 * @version 1.0
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

					boolean jsxMode = path.toLowerCase().endsWith( ".jsx" );
					
					if ( path.toLowerCase().endsWith( ".js" ) || jsxMode ) { 	// JavaScript code
						a = new ScriptAction( new File( path ), jsxMode );
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