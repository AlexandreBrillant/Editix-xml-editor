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

package com.japisoft.xmlform.designer.properties;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.japisoft.framework.ApplicationModel;

public class PropertyDescriptorImpl implements PropertyDescriptor {

	private Object component = null;
	private String name = null;
	private Class paramType = null;
	
	private Method setter = null;
	private Method getter = null;
	
	public PropertyDescriptorImpl( 
			String name, 
			Class paramType, 
			Object component ) throws Exception {
		this.name = name;
		this.component = component;
		this.paramType = paramType;
		
		name = Character.toUpperCase( name.charAt( 0 ) ) + 
			name.substring( 1 );
		
		if ( paramType == Boolean.class )
			paramType = boolean.class;
		
		setter = component.getClass().getMethod( 
				"set" + name, 
				paramType );

		getter = component.getClass().getMethod( 
				"get" + name, 
				null );
	}

	public String getName() {
		return name;
	}
	
	public Class getType() {
		return paramType;
	}

	private boolean displayable = true;

	public boolean displayable() {
		return displayable;
	}	
	
	public Object getValue() {
		try {
			return getter.invoke( component, null );
		} catch (IllegalArgumentException e) {
			ApplicationModel.debug( e );
		} catch (IllegalAccessException e) {
			ApplicationModel.debug( e );
		} catch (InvocationTargetException e) {
			ApplicationModel.debug( e );
		}
		return null;
	}

	public void setValue( Object value ) {
		try {
			setter.invoke( component, value );
		} catch (IllegalArgumentException e) {
			ApplicationModel.debug( e );
		} catch (IllegalAccessException e) {
			ApplicationModel.debug( e );
		} catch (InvocationTargetException e) {
			ApplicationModel.debug( e );
		}
	}

	@Override
	public String toString() {
		return getName();
	}
	
}
