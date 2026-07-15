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

import java.util.ArrayList;

import com.japisoft.framework.toolkit.Toolkit;
import com.japisoft.xmlform.component.XMLFormLabelComponent;
import com.japisoft.xmlform.component.XMLFormSeparatorComponent;
import com.japisoft.xmlform.component.container.XMLFormContainer;
import com.japisoft.xmlform.component.editable.XMLFormTextComponent;

public class ComponentDescriptorModel {

	private static ArrayList<ComponentDescriptor> model =
		new ArrayList<ComponentDescriptor>();
	
	static {
		/*
		model.add(
				new ComponentDescriptor(
						Toolkit.getImageIcon( "images/components/window.png" ),
						"Container",
						XMLFormContainer.class.getName()
				) );
		*/
		model.add(
				new ComponentDescriptor(
						Toolkit.getImageIcon( "images/components/text.png" ),
						"Textfield",
						XMLFormTextComponent.class.getName()
				) );
		
		model.add(
				new ComponentDescriptor(
						Toolkit.getImageIcon( "images/components/font.png" ),
						"Label",
						XMLFormLabelComponent.class.getName()
				) );

		model.add(
				new ComponentDescriptor(
						Toolkit.getImageIcon( "images/components/layout_horizontal.png" ),
						"Separator",
						XMLFormSeparatorComponent.class.getName() ) );
		
	}

	public static int getComponentDescriptorCount() {
		return model.size();
	}

	public static ComponentDescriptor getComponentDescriptor( int index ) {
		return model.get( index );
	}

	public static ComponentDescriptor getComponentDescriptor( String name ) {
		for ( ComponentDescriptor cd : model ) {
			if ( cd.getName().equals( name ) )
				return cd;
		}
		return null;
	}
	
}
