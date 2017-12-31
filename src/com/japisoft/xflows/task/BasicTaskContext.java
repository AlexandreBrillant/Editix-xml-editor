package com.japisoft.xflows.task;

import java.io.File;

import com.japisoft.framework.toolkit.Logger;

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
public class BasicTaskContext extends AbstractTaskContext {

	public static final int INFO = 0;
	public static final int WARNING = 1;
	public static final int ERROR = 2;

	private Task source = null;

	public BasicTaskContext( Task source ) {
		this.source = source;
	}

	public BasicTaskContext( TaskContext context ) {
		this( context.currentTask() );
	}

	public Task currentTask() {
		return source;
	}
	
	boolean quietInfo = false;

	/** Enabled/Disabled the information output */
	public void setQuietInfoMode( boolean quiet ) {
		this.quietInfo = quiet;
	}

	boolean quietWarning = false;
	
	/** Enabled/Disabled the warning output */
	public void setQuietWarningMode( boolean quiet ) {
		this.quietWarning = quiet;
	}

	boolean quietError = false;
	
	/** Enabled/Disabled the error output */
	public void setQuietErrorMode( boolean quiet ) {
		this.quietError = quiet;
	}

	/** Add a warning message in the log */
	public void addWarning( String message ) {
		if ( !quietWarning )
			Logger.addWarning( message );
	}
	
	/** Add an information in the log */
	public void addInfo( String message ) {
		if ( !quietInfo )
			Logger.addInfo( message );
	}

	private boolean errorFound = false;

	public boolean hasErrorFound() {
		return errorFound;
	}

	/** Add an error message in the log */
	public void addError( String message ) {
		this.errorFound = true;
		if ( !quietError )
			Logger.addError( message );
	}	

	/** @return a parameter value from the UI part */
	public String getParam( String name ) {
		String value = source.getParams().getParamValue( name );
		if ( "".equals( value ) )
			return null;
		return value;
	}

	/** 
	 * @return a parameter value from the UI part */
	public String getParam( String name, String def ) {
		String p = getParam( name );
		if ( p != null )
			return p;
		return def;
	}

	/** @return a parameter with a directory path */
	public String getParamForPath( String name ) {
		String param = getParam( name );
		if ( param != null && !param.endsWith( "/" ) )
			return param + "/";
		return param;
	}
		
	public boolean hasParam( String name ) {
		return source.getParams().hasParamValue( name ); 
	}
	
	public TaskParams getParams() {
		return source.getParams();
	}
	
	private boolean interrupted = false;

	/** Interrupt the task ? */
	public void interrupt() {
		interrupted = true;
	}
	
	public boolean isInterrupted() {
		return interrupted;
	}

	private File sourceFile = null;
	private File targetFile = null;
	
	/** Reset the current source file */
	public void setCurrentSourceFile( File f ) {
		this.sourceFile = f;
	}

	/** Reset the current target file */
	public void setCurrentTargetFile( File f ) {
		this.targetFile = f;
	}

	/** @return the current source file */
	public File getCurrentSourceFile() {
		return sourceFile;
	}
	
	/** @return the current target file */
	public File getCurrentTargetFile() {
		return targetFile;
	}	
	
}
