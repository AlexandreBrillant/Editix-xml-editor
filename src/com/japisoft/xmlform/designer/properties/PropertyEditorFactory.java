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

import java.awt.Color;
import java.awt.Font;
import java.util.HashMap;

import com.japisoft.xmlform.designer.properties.editors.BooleanEditor;
import com.japisoft.xmlform.designer.properties.editors.ColorEditor;
import com.japisoft.xmlform.designer.properties.editors.DictionnaryEditor;
import com.japisoft.xmlform.designer.properties.editors.FontEditor;
import com.japisoft.xmlform.designer.properties.editors.TextEditor;

public class PropertyEditorFactory {

	private static HashMap<Class,PropertyEditor> mapEditors = 
		new HashMap<Class, PropertyEditor>();

	private static HashMap<Class,PropertyEditor> mapRenderers = 
		new HashMap<Class, PropertyEditor>();
	
	static {
		mapEditors.put( String.class, new TextEditor() );
		mapEditors.put( Integer.class, new TextEditor( TextEditor.INT_TYPE ) );
		mapEditors.put( Color.class, new ColorEditor() );
		mapEditors.put( Font.class, new FontEditor() );
		mapEditors.put( Boolean.class, new BooleanEditor() );
		mapEditors.put( HashMap.class, new DictionnaryEditor() );
		
		mapRenderers.put( String.class, new TextEditor() );
		mapRenderers.put( Integer.class, new TextEditor( TextEditor.INT_TYPE ) );
		mapRenderers.put( Color.class, new ColorEditor() );
		mapRenderers.put( Font.class, new FontEditor() );
		mapRenderers.put( Boolean.class, new BooleanEditor() );
		mapRenderers.put( HashMap.class, new DictionnaryEditor() );
	}

	public static PropertyEditor getEditor( 
			PropertyDescriptor descriptor ) {

		PropertyEditor editor = mapEditors.get( 
				descriptor.getType() );

		if ( editor == null )
			editor = mapEditors.get( String.class );
		editor.setValue( descriptor.getValue() );
		return editor;

	}

	public static PropertyEditor getEditorForRendering( 
			PropertyDescriptor descriptor ) {

		PropertyEditor editor = mapRenderers.get( 
				descriptor.getType() );

		if ( editor == null )
			editor = mapRenderers.get( String.class );
		editor.setValue( descriptor.getValue() );
		return editor;

	}
	
}
