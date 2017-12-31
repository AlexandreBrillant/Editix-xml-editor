package com.japisoft.framework.toolkit;

import java.util.ArrayList;

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
