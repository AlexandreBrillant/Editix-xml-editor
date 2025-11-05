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

package com.japisoft.framework.toolkit;

import java.util.ArrayList;

/**
 * Store information about running
 * @author Alexandre Brillant (https://github.com/AlexandreBrillant/Editix-xml-editor)
 * @version 1.0 */
public class Logger {	
	public static final int INFO = 0;
	public static final int WARNING = 1;
	public static final int ERROR = 2;

	private static Logger DELEGATE = null;
	private static ArrayList LISTENERS = null;

	public static void addLoggerListener( LoggerListener listener ) {
		if ( LISTENERS == null )
			LISTENERS = new ArrayList();
		LISTENERS.add( listener );
	}

	public static void removeLoggerListener( LoggerListener listener ) {
		if ( LISTENERS != null )
			LISTENERS.remove( listener );
	}

	public static void setDefaultLogger( Logger logger ) {
		DELEGATE = logger;
	}

	public String log( int level, String message ) {
		return DELEGATE.log( level, message );		
	}

	public static String addMessage( int level, String message ) {
		if ( DELEGATE == null )
			DELEGATE = new ConsoleLogger();
		return DELEGATE.log( level, message );
	}

	public static void addInfo( String message ) {
		message = addMessage( INFO, message );
		if ( LISTENERS != null ) {
			for ( int i = 0; i < LISTENERS.size(); i++ ) {
				( ( LoggerListener )LISTENERS.get( i ) ).addInfo( message );
			}
		}
	}
	
	public static void addWarning( String message ) {
		message = addMessage( WARNING, message );
		if ( LISTENERS != null ) {
			for ( int i = 0; i < LISTENERS.size(); i++ ) {
				( ( LoggerListener )LISTENERS.get( i ) ).addWarning( message );
			}
		}				
	}

	public static void addError( String message ) {
		message = addMessage( ERROR, message );
		if ( LISTENERS != null ) {
			for ( int i = 0; i < LISTENERS.size(); i++ ) {
				( ( LoggerListener )LISTENERS.get( i ) ).addError( message );
			}
		}		
	}

}

