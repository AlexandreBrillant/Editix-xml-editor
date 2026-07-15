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

package com.japisoft.editix.action.file.project;

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Enumeration;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import javax.swing.AbstractAction;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

import com.japisoft.editix.action.file.BrowseZIPAction;
import com.japisoft.editix.ui.EditixFactory;
import com.japisoft.editix.ui.windows.EditixFrame;
import com.japisoft.framework.ApplicationModel;
import com.japisoft.framework.descriptor.ActionModel;
import com.japisoft.framework.toolkit.FileToolkit;

public class ImportProjectAction extends AbstractAction {

	@Override
	public void actionPerformed(ActionEvent e) {
		JFileChooser fileChooser = new JFileChooser();
		BrowseZIPAction.addFileFilterForZipArchives( fileChooser, false );
		FileFilter[] ff = fileChooser.getChoosableFileFilters();

		if ( ff.length > 1 ) {
			fileChooser.setFileFilter( ff[ 1 ] );
		}
		if ( fileChooser.showOpenDialog( EditixFrame.THIS ) == 
				JFileChooser.APPROVE_OPTION ) {

			boolean overwrite = true;
			
			
			// Store by default the new project inside the home/.editix directory
			File f = fileChooser.getSelectedFile();
			if ( f == null )
				return;
			String fileName = f.getName();
			File projectPath = ApplicationModel.getAppFile(fileName);
			if ( !projectPath.mkdirs() ) {
				if ( projectPath.exists() ) {
					if ( !EditixFactory.buildAndShowConfirmDialog( "This project exists, do you want to overwrite it ?" ) ) {
						overwrite = false;
					}
				} else {
					EditixFactory.buildAndShowErrorDialog( "Can't store the project here ? " + projectPath );
					return;
				}
			}
			
			try {
				
				if ( overwrite ) {
				
					FileToolkit.unzip( f, projectPath );
					
					/*
					
					// Unzip the content
					ZipFile z = new ZipFile( f );
					try {
						Enumeration<? extends ZipEntry> entries = z.entries();
						while ( entries.hasMoreElements() ) {
							ZipEntry ze = entries.nextElement();
							File zef = new File( projectPath, ze.getName() );
							
							if ( ze.isDirectory() ) {
								zef.mkdirs();
							} else {
								// Write the file
								FileOutputStream out = new FileOutputStream( zef );
								try {
									InputStream i = z.getInputStream( ze );
									int c;
									while ( ( c = i.read() ) != -1 )
										out.write( c );								
								} finally {
									out.close();
								}
							}
						}
						
					} finally {
						z.close();
					} */
					
					
					
				}
				
				( ( NewProjectAction )ActionModel.restoreAction( "newp" ) ).loadProject( projectPath );
				
			} catch( Exception exc ) {
				EditixFactory.buildAndShowErrorDialog( "Can't use this file : " + exc.getMessage() );
				return;
			}
			
		}
	}

	public static void archiveProject( File projectPath, File archive ) throws Exception {
		final Path pp = Paths.get( projectPath.toString() );
		final ZipOutputStream z = new ZipOutputStream( new FileOutputStream( archive ) );
		
		/*
		// Lambda version
		Files.walk( pp ).filter( f -> !Files.isDirectory( f ) ).forEach(
			f -> {
				ZipEntry ze = new ZipEntry( pp.relativize( f ).toString() );
				try {
					z.putNextEntry( ze );
					Files.copy( f, z );
					z.closeEntry();
				} catch( IOException exc ) {
					
				}
		} );
		*/
		
		// No Lambda
		Files.walk( pp ).filter(
				new Predicate<Path>() {
					public boolean test(Path f) {
						return !Files.isDirectory(f);
					};
				} ).forEach(
						new Consumer<Path>() {
							@Override
							public void accept(Path f) {
								ZipEntry ze = new ZipEntry( pp.relativize( f ).toString() );
								try {
									z.putNextEntry( ze );
									Files.copy( f, z );
									z.closeEntry();
								} catch( IOException exc ) {
									
								}
								
							}
						} );
		
		
		z.close();
	}
	
}
