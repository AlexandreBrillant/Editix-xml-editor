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

package com.japisoft.xmlpad.helper.model;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;

import javax.swing.DefaultListCellRenderer;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.UIManager;

import com.japisoft.framework.toolkit.Toolkit;

/**
 * Common renderer for popup
 * @author Alexandre Brillant (https://github.com/AlexandreBrillant/Editix-xml-editor)
 * <p>UIManager properties :
 * - xmlpad.helper.icon
 * </p>
 * @version 1.1
 * */
public class CommonDescriptorRenderer extends DefaultListCellRenderer {

	private Icon defIcon;

	private CommonDescriptorRenderer() {
		super();
		defIcon = UIManager.getIcon("xmlpad.helper.icon");
		if ( defIcon == null )
			defIcon = Toolkit.getImageIcon( "images/element.png" );
				
	}

	private static CommonDescriptorRenderer RENDERER = null;

	public static CommonDescriptorRenderer getRenderer() {
		if ( RENDERER == null )
			RENDERER = new CommonDescriptorRenderer();
		return RENDERER;
	}

	Font f = null;

	public Component getListCellRendererComponent(
		JList list,
		Object value,
		int index,
		boolean isSelected,
		boolean cellHasFocus) {
		
		JLabel c =
			( JLabel )super.getListCellRendererComponent(
				list,
				value,
				index,
				isSelected,
				cellHasFocus );

		Icon icon = null;		

		if ( ( value != null ) && ( value instanceof Descriptor ) ) {
			Descriptor d = ( Descriptor )value;
			c.setEnabled( d.isEnabled() );
			c.setText( d.getNameForHelper() );
			if ( d.getColor() != null )
				c.setForeground( 
						d.getColor() );
			else {
				if ( isSelected )
					c.setForeground( 
							list.getSelectionForeground() );
				else
					c.setForeground( 
							list.getForeground() );
			}
			icon = d.getIcon();
			if ( icon == null )
				icon = defIcon;
		}

		if (f == null)
			c.setFont(
				f = new Font( 
					getFont().getName(), 
					0, 
					getFont().getSize() ) );
		else
			c.setFont( f );

		if ( icon != null )
			c.setIcon( icon );
				
		return c;
	}

}
