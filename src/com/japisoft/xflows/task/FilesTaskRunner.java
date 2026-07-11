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

package com.japisoft.xflows.task;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import javax.swing.JDialog;
import javax.swing.JTextField;

/**
 * @author Alexandre Brillant (https://github.com/AlexandreBrillant/Editix-xml-editor)
 * @version 1.0
 * */
public class FilesTaskRunner implements TaskRunner {
	
	protected TaskRunner monoTask;
	
	public static final String SOURCEPATH = "sourcepath";
	public static final String SOURCEFILTER = "sourcefilter";
	public static final String TARGETPATH = "targetpath";
	public static final String TARGETNAME = "targetname";

	private boolean targetRequired = false;

	public FilesTaskRunner( TaskRunner monoTask, boolean targetRequired ) {
		this.monoTask = monoTask;
		this.targetRequired = targetRequired;
	}

	protected boolean fileTarget = false;
	protected boolean defaultProcessingLog = false;
	
	public boolean run( TaskContext context ) {

		// Check source path
		String sourcePath = context.getParam( SOURCEPATH );
		if ( sourcePath == null ) {
			context.addError( "No source path " );
			return ERROR;
		}
		File fsource = new File( sourcePath );
		if ( !fsource.exists() ) {
			context.addError( "Cannot find the source path : " + sourcePath );
			return ERROR;
		}

		String sourceFilter = null;
		boolean sourceDirectory = false;
		
		if ( fsource.isDirectory() ) {
			sourceDirectory = true;
			sourceFilter = context.getParam( SOURCEFILTER );
			if ( sourceFilter == null ) {
				sourceFilter = "(.*)\\.(.*)";
				context.addWarning( "No sourceFilter found : Use the default one " + sourceFilter );
			}
		}

		// Check target path
		String targetPath = context.getParam( TARGETPATH );
		String targetName = null;
		File ftarget = null;
		
		if ( targetPath != null ) {
			ftarget = new File( targetPath );

			if ( ftarget.isDirectory() ) {
				
				if  ( !sourceDirectory ) {
					context.addError( "Invalid target, a file name is required" );
					return ERROR;
				}

				// It requires a targetname
				targetName = context.getParam( TARGETNAME );
				if ( targetName == null ) {
					context.addError( "Target name not found " );
					return ERROR;
				}
			} else {
				
				if ( !fileTarget && context.hasParam( TARGETNAME ) ) {

					context.addWarning( "Target name defined but no directory found for the target" );

				}
				
			}
				

		} else {
			if ( targetRequired ) {
				context.addError( "No target path" );
				return ERROR;
			}
		}

		if ( !sourceDirectory ) {
			context.setCurrentSourceFile( fsource );
			context.setCurrentTargetFile( ftarget );
			if ( defaultProcessingLog )
				context.addInfo( "Processing " + context.getCurrentSourceFile() );
			return monoTask.run( context );
		} else {

			String[] content = fsource.list();
			boolean errorFound = false;
			
			if ( content != null ) {
				try {
					boolean processOnce = false;
					/*StringBuffer sb = new StringBuffer();
					for ( int i = 0; i < sourceFilter.length(); i++ ) {
						if ( sourceFilter.charAt( i ) == '\\' )
							sb.append( "\\\\" );
						else
							sb.append( sourceFilter.charAt( i ) );
					} */					
					Pattern p = Pattern.compile( sourceFilter );
					for ( int i = 0; i < content.length; i++ ) {
						if ( context.isInterrupted() )
							break;

						String name = content[ i ];						
						Matcher m = p.matcher( name );

						if ( m.matches() ) {
							processOnce = true;
							String finalTarget = targetName;
							if  ( targetName != null ) {								
								for ( int j = 0; j < m.groupCount() ;j++ ) {
									String s = m.group( j + 1 );
									finalTarget = finalTarget.replaceAll( "\\$" + ( j + 1 ), s );
								}
							}
							context.setCurrentSourceFile( new File( fsource, name ) );
							if ( targetName != null && ftarget != null ) {
								context.setCurrentTargetFile( new File( ftarget, finalTarget ) );
							} else
							if ( ftarget != null ) {
								context.setCurrentTargetFile( ftarget );
							}

							if ( defaultProcessingLog )
								context.addInfo( "Processing " + context.getCurrentSourceFile() );
							
							if ( monoTask.run( context ) == ERROR )
								errorFound = true;
						}						
					}

					if ( !processOnce )
						context.addWarning( "No source file found" );

				} catch( PatternSyntaxException exc ) {
					context.addError( "Wrong source filter " + sourceFilter + " : " + exc.getMessage( ) );
				}
				
				if ( errorFound )
					return ERROR;
				
			} else
				context.addWarning( "No content for directory " + fsource );
		}		
		return OK;
	}

	public static void main( String[] args ) {
		
		JDialog dialog = new JDialog();
		dialog.setModal( true );
		JTextField tf = new JTextField();
		dialog.getContentPane().add( tf );
		dialog.setSize( 400, 30 );
		dialog.setVisible( true );

		Pattern p = Pattern.compile( tf.getText() );
		Matcher m = p.matcher( "test.toto" );
		System.out.println( "Match = " + m.matches() );
	}
	
}
