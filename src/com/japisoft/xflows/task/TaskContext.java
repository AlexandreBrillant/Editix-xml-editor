package com.japisoft.xflows.task;

import java.io.File;
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
public interface TaskContext {

	public Task currentTask();
	
	/** Enabled/Disabled the information output */
	public void setQuietInfoMode( boolean quiet );

	/** Enabled/Disabled the warning output */
	public void setQuietWarningMode( boolean quiet );

	/** Enabled/Disabled the error output */
	public void setQuietErrorMode( boolean quiet );

	/** Add a warning message in the log */
	public void addWarning( String message );

	/** Add an information in the log */
	public void addInfo( String message );

	public boolean hasErrorFound();

	/** Add an error message in the log */
	public void addError( String message );

	/** @return a parameter value from the UI part */
	public String getParam( String name );

	/** 
	 * @return a parameter value from the UI part */
	public String getParam( String name, String def );

	/** @return a parameter with a directory path */
	public String getParamForPath( String name );
		
	public boolean hasParam( String name );
	
	public TaskParams getParams();
	
	/** Interrupt the task ? */
	public void interrupt();
	
	public boolean isInterrupted();
	
	/** Reset the current source file */
	public void setCurrentSourceFile( File f );

	/** Update the default encoding for the task */
	public void setDefaultEncoding( String encoding );
	
	public String getDefaultEncoding();

	/** Reset the current target file */
	public void setCurrentTargetFile( File f );
	
	/** Store a task result */
	public void setTaskResult( String result );
	
	/** Get a task result */
	public String getTaskResult();

	/** Store a task source */
	public void setTaskSource( String source );
	
	/** Get a task source */
	public String getTaskSource();
	
	/** @return the current source file */
	public File getCurrentSourceFile();
	
	/** @return the current target file */
	public File getCurrentTargetFile();

}
