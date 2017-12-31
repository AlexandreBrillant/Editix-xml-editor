package com.japisoft.xmlpad.error;

import java.util.ArrayList;

import com.japisoft.xmlpad.Debug;
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
public class ErrorManager implements ErrorListener {
	
	public static final String ON_THE_FLY_PARSING_CONTEXT = "onfly";
	
	private ArrayList errors = null;

	/** Add a new listener for onTheFly or full parsing error */
	public void addErrorListener( ErrorListener listener ) {
		if ( errors == null )
			errors = new ArrayList();
		if ( listener != null && !errors.contains( listener ) )
			errors.add( listener );
	}

	public void removeErrorListener( ErrorListener listener ) {
		if ( errors != null )
			errors.remove( listener );
	}

	/** Inform all the listeners, that some error reporting is beginning */
	public void initErrorProcessing() {
		flushLastError();
		Debug.debug( "** Init error processing " );
		if ( errors != null ) {
			for ( int i = 0; i < errors.size(); i++ ) {
				( ( ErrorListener )errors.get( i ) ).initErrorProcessing();
			}
		}
	}

	/** Inform all the listeners, that no more error reporting will be done */
	public void stopErrorProcessing() {
		Debug.debug( "** Stop error processing " );
		if ( errors != null ) {
			for ( int i = 0; i < errors.size(); i++ ) {
				( ( ErrorListener )errors.get( i ) ).stopErrorProcessing();
			}
		}		
	}

	boolean lastCaseInError = false;
	boolean lastCaseOnTheFly = false;
	
	/** Here a shortcut for sending a single error message */
	public void notifyError(
			String message ) {
		notifyError(
				message, 
				0
		);
	}

	/** Here a shortcut for sending a single error message at the following line */	
	public void notifyError(
			String message,
			int line ) {
		notifyError(
				null,
				true,
				null,
				line,
				0,
				0,
				message
				,
				true );		
	}

	private String resolverSourceLocationSpace( String location ) {
		if ( location == null )
			return null;
		location = location.replaceAll( "%20", " " );
		return location;
	}
	
	/** Inform all the listeners. It is possible to have multiple errors.
	 * So the errors reporting must be completed by calling the
	 * <code>stopErrorProcessing</code> method.
	 * */	
	public void notifyError(
			Object context,
			boolean localError,
			String sourceLocation, 
			int line,
			int column,
			int offset,
			String message, 
			boolean onTheFly ) {
		
		Debug.debug( 
				"** XML Error : " + 
				message + 
				" local:" + 
				localError + 
				" source:" + sourceLocation +
				" line:" + line +
				" col:" + column +
				" offset:" + offset +
				" onFly:" + onTheFly );
		
		sourceLocation = resolverSourceLocationSpace( sourceLocation );
		
		lastCaseInError = true;
		lastCaseOnTheFly = onTheFly;
		if ( errors != null ) {
			for ( int i = 0; i < errors.size(); i++ ) {
				( ( ErrorListener )errors.get( i ) ).notifyError(
						context,
						localError,
						sourceLocation,
						line,
						column,
						offset,
						message,
						onTheFly );
			}			
		}		
	}

	/** 
	 * Notify only one error. It will called the stopErrorProcessing method
	 * at the end
	 * @param localError
	 * @param sourceLocation
	 * @param line
	 * @param column
	 * @param offset
	 * @param message
	 * @param onTheFly*/
	public void notifyUniqueError(
			boolean localError,
			String sourceLocation, 
			int line,
			int column,
			int offset,
			String message, 
			boolean onTheFly ) {
		notifyError( 
				null,
				localError,
				sourceLocation,
				line,
				column,
				offset,
				message,
				onTheFly );
		stopErrorProcessing();
	}

	/** Inform all the listeners */	
	public void notifyNoError( boolean onTheFly ) {
		if ( errors != null ) {
			for ( int i = 0; i < errors.size(); i++ ) {
				( ( ErrorListener )errors.get( i ) ).notifyNoError( onTheFly );				
			}
		}
		lastCaseInError = false;
	}

	/** Eliminate any trace of the last error. Should'nt be called */
	public void flushLastError() { 
		lastCaseInError = false;
		lastCaseOnTheFly = false;
		
	}

	/** @return <code>true</code> if an error exists */
	public boolean hasLastError() { return lastCaseInError; }
	/** @return <code>true</code> if the last error is due to an onthefly problem */
	public boolean hasLastErrorOnTheFly() { return lastCaseOnTheFly; }
	
	public void dispose() {
		errors = null;
	}
}
