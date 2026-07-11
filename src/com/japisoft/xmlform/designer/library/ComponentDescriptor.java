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

package com.japisoft.xmlform.designer.library;

import java.lang.reflect.Constructor;

import javax.swing.Icon;

import com.japisoft.xmlform.component.AbstractXMLFormComponent;
import com.japisoft.xmlform.component.ComponentContext;

public class ComponentDescriptor {

	private Icon icon = null;
	private String name = null;
	private String className = null; 

	public ComponentDescriptor( Icon icon, String name, String className ) {
		this.icon = icon;
		this.name = name;
		this.className = className;
	}

	public Icon getIcon() {
		return icon;
	}
	
	public String getName() {
		return name;
	}
	
	public AbstractXMLFormComponent create( 
			boolean designMode, 
			ComponentContext context ) throws Exception {

		Class cl = Class.forName( className );
		Constructor constructor = cl.getConstructor( boolean.class, ComponentContext.class );
		AbstractXMLFormComponent component = 
			( AbstractXMLFormComponent )constructor.newInstance( designMode, null );
		// For avoiding it to get the current grammar node
		component.setContext( context );
		component.setXpath( "Location" );
		return component;

	}

}
