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

/**
 * Here the task context. This is the link between the interface
 * and the task running. This context contains all the interface
 * parameters.
 * @author Alexandre Brillant (https://github.com/AlexandreBrillant/Editix-xml-editor)
 * @version 1.0 */
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
