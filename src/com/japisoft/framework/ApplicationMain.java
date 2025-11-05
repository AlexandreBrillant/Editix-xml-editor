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

package com.japisoft.framework;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import com.japisoft.framework.log.Logger;
import com.japisoft.framework.preferences.Preferences;
import com.japisoft.framework.step.Threadable;

/**
 * This is the beginning part for starting an application. The user must
 * call the <code>start</code> method. This operation will use a set of 
 * application step for building the application. The user must have at least
 * one application step for starting the application
 * @author Alexandre Brillant (https://github.com/AlexandreBrillant/Editix-xml-editor)
 * @version 1.0
 * */
public class ApplicationMain {

	static {
	}

	private static String[] initArgs;
	private static List<ApplicationStepListener> listeners = null;
	
	public static void addApplicationStepListener( ApplicationStepListener listener ) {
		if ( listeners == null )
			listeners = new ArrayList<ApplicationStepListener>();
		listeners.add( listener );
	}

	public static void removeApplicationStepListener( ApplicationStepListener listener ) {
		if ( listeners != null )
			listeners.remove( listener );
	}
	
	public static void fireApplicationStepEvent( ApplicationStep step, int indice, int maxIndice ) {
		if ( listeners != null ) {
			for ( ApplicationStepListener listener : listeners ) {
				listener.run( step, indice, maxIndice );
			}
		}
	}

	/** Start the application using the following application descriptor file 
	 * @param applicationDescriptorFile A path using the CLASSPATH for the application descriptor file
	 * @param args The application main parameters
	 * @throws ApplicationException when cannot find the application descriptor file */
	public static void start( String applicationDescriptorFile, String[] args ) throws ApplicationException {
		ApplicationModel.init( applicationDescriptorFile );
		start( args );
	}

	/** Start the application without application descriptor file. The user must
	 * update each correct field from the application model before calling it
	 * @param args The application main parameters */
	public static void start( String[] args ) throws ApplicationException {
		initArgs = args;
		if ( ApplicationModel.getApplicationStepCount() == 0 ) {
			throw new ApplicationException( "No application step found" );
		}
		boolean started = false;
		try {
			for ( int i = 0; i < ApplicationModel.getApplicationStepCount(); i++ ) {
				ApplicationStep step = ApplicationModel.getApplicationStepAt( i );
				if ( !step.isFinal() ) {
					ApplicationModel.debug(
							"Start " + ( i + "/" + ApplicationModel.getApplicationStepCount() ) + " " + step.getClass() );
					started = true;
					try {
						fireApplicationStepEvent(step, i, ApplicationModel.getApplicationStepCount() );
						startStep( step, args );
					} catch( Exception e ) {
						if ( e instanceof ApplicationStepException ) {
							if ( ( ( ApplicationStepException )e ).isCritical() ) {
								throw new ApplicationException( e );
							}
						} else
							throw new ApplicationException( e );
					}
				}
			}
		} finally {
			// Stop all the non final application step
			for ( int i = 0; i < ApplicationModel.getApplicationStepCount(); i++ ) {
				ApplicationStep step = ApplicationModel.getApplicationStepAt( i );
				if ( !step.isFinal() && !(step instanceof Threadable ) ) {
					try {
						ApplicationModel.debug(
								"Stop " + step.getClass() );						
						step.stop();
					} catch( Throwable th ) {
						Logger.addException( th );
					}
					
				}
			}
			
			listeners = null;
			
		}
		if ( !started )
			throw new ApplicationException( "No application step found. Only final application step available ?" );
	}

	private static void startStep( final ApplicationStep step, final String[] args ) throws Exception {
		if ( step instanceof Threadable ) {
			// Asynchronous step
			new Thread( new Runnable() {
				
				@Override
				public void run() {
					try {
						step.start( args );
						step.stop();
					} catch( Exception exc ) {
						ApplicationModel.debug( exc );
					}
				}
			} ).start();

		} else {
			step.start( args );
		}
	}
	
	/** Should be called when terminating the application. This method will call the exit method */
	public static void quit( int exitCode ) {
		/* Called any final step */
		for ( int i = 0; i < ApplicationModel.getApplicationStepCount(); i++ ) {
			ApplicationStep step = ApplicationModel.getApplicationStepAt( i );
			if ( step.isFinal() ) {
				try {
					ApplicationModel.debug( "Start and Stop " + step.getClass() );					
					step.start( initArgs );
					step.stop();
				} catch( Throwable th ) {
					Logger.addException( th );
				}
			} else
				step.quit();
		}
		Preferences.savePreferences();
		System.exit( exitCode );
	}
	
	/**
	 * Show a dialog box with the following error
	 * @param th An exception while starting the application */
	public static void showException( Throwable th ) {
		th.printStackTrace();
		StringWriter sw = new StringWriter();
		th.printStackTrace(
				new PrintWriter( sw ) );

		JFrame fr = new JFrame( "Unknown error !" );
		StringBuffer sb = new StringBuffer();
		sb.append( ApplicationModel.LONG_APPNAME + " " + ApplicationModel.getAppVersion()
				+ " - Build " + ApplicationModel.BUILD);
		sb.append("\nHas met an unknown error, please send it to : "
				+ ApplicationModel.MAIN_SUPPORT_EMAIL + "\n");
		sb
				.append("specifying your operating system version and java version\n");
		
		sb.append( "Java Version : ").append( System.getProperty( "java.version" ) ).append( "\n" );
		sb.append( "Operating System : " ).append( System.getProperty( "os.name" ) ).append( "\n" );
		
		sb
				.append("\n----------------------------------------------------------------\n\n");
		sb.append(sw.toString());
		JTextArea ar = new JTextArea( sb.toString() );
		fr.getContentPane().add( new JScrollPane( ar ) );
		fr.pack();
		fr.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		fr.setVisible( true );
	}
	
}

