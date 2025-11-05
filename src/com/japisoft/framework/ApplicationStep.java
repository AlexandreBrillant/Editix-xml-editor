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

/**
 * Here an interface bound to a part of the application starting.
 * The application starting is composed of one or several application steps
 * @author Alexandre Brillant (https://github.com/AlexandreBrillant/Editix-xml-editor)
 * @version 1.0
 * */
public interface ApplicationStep {

	/** Start this application step. The application main parameters are provided. It may throw an
	 * ApplicationStepException */
	public void start( String[] args ) throws Exception;

	/** This is called when terminating this application step. You can use it for
	 * freeing any allocated resource */
	public void stop();

	/** @return <code>true</code> if this application step must be called when terminating the application */
	public boolean isFinal();
	
	public void quit();

}

