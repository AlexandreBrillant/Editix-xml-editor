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

package com.japisoft.xflows.task.ui;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;

import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;

import com.japisoft.editix.action.xsl.result.DocumentTypeModel;
import com.japisoft.editix.ui.EditixFactory;
import com.japisoft.framework.ApplicationModel;
import com.japisoft.framework.preferences.Preferences;

/**
 * @author Alexandre Brillant (https://github.com/AlexandreBrillant/Editix-xml-editor)
 * @version 1.0
 */
public class XFlowsFactory {

	public static void buildAndShowErrorDialog(String message) {
		EditixFactory.buildAndShowErrorDialog( message );
	}

	public static String buildAndShowInputDialog(String title) {
		return EditixFactory.buildAndShowInputDialog( title, "" );
	}

	public static void buildAndShowInformationDialog(String message) {
		EditixFactory.buildAndShowInformationDialog( message );
	}

	public static int buildAndShowChoiceDialog(String message) {
		return EditixFactory.buildAndShowConfirmDialog( message ) ? 1 : 0;
	}

	/** @return a FileChooser for opening a project file */
	public static JFileChooser buildProjectFileChooser() {
		JFileChooser fc = new JFileChooser();
		fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fc.setFileFilter(new FileFilter() {
			public String getDescription() {
				return "*.xfl (XFlows project file)";
			}
			public boolean accept(File f) {
				return (
					f.isDirectory()
						|| f.toString().toLowerCase().endsWith(".xfl" ) );
			}
		});

		String currentDir = 
			Preferences.getPreference( "defaultpath", "project", ( String )null );

		if ( currentDir != null )
			fc.setCurrentDirectory( new File( currentDir ) );

		return fc;
	}

	public static JComboBox getSourceFilter( String type ) {

		String[] filters = null;

		if ( "jsx".equals( type ) ) {
			filters = new String[] {"(.*).xml"};
		} else
		if ( "all".equals( type ) ) {
			filters = new String[] {
				"(.*)"	
			};
		} else
		if ( "zip".equals( type ) ) {
			filters = new String[] {
				"(.*).zip",
				"(.*).docx",
				"(.*).xlsx",
				"(.*).idml"
			};
		} else
		if ( "csv".equals( type ) ) {
			filters = new String[] {
					"(.*).csv",
					"(.*)\\.(.*)"					
			};
		} else
		if ( "htmlxml".equals( type ) ) {
			filters = new String[] { 
					"(.*).html",
					"(.*).htm",
					"(.*)\\.(.*)"
			};
		} else
			filters = new String[] {
				"(.*).xml",
				"(.*).jso",
				"(.*).json",
				"(.*)\\.(.*)"
			};

		JComboBox combo = new JComboBox(
				filters
		);
		return combo;
	}

	public static JComboBox getTargetName( String type ) {
		String [] filters = null;
		if ( "xml".equals( type ) ) {
			filters = new String[] {
				"$1.xml"
			};
		} else {
			filters = new String[] { 
				"$1.html",
				"$1.htm",
				"$1.xml" };
			
			if ( type != null && type.startsWith( "xsl" ) ) {
				
				ArrayList<String> tout = new ArrayList<String>();
				Collections.addAll( tout, filters );
				
				// Add complex output document like docx...
				for ( int i = 0; i < DocumentTypeModel.instance().size(); i++ ) {
					tout.add( "$1." + DocumentTypeModel.instance().fileExt( i ) );
				}
				
				filters = tout.toArray( new String[ tout.size() ] );
			}
			
		}
		JComboBox combo = new JComboBox( filters );
		return combo;
	}

	public static ImageIcon getImageIcon(String resource) {
		URL url = ClassLoader.getSystemClassLoader().getResource(resource);
		if (url != null)
			return new ImageIcon(url);
		else
			com.japisoft.framework.toolkit.Logger.addWarning("Can't find " + resource);
		return null;
	}
	
}