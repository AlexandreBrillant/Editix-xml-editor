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

package com.japisoft.editix.ui.xslt;

import javax.swing.JOptionPane;

import com.japisoft.framework.ui.text.PathBuilder;
import com.japisoft.xmlpad.XMLContainer;

/**
 * For usage outside editiX
 * @author Alexandre Brillant (https://github.com/AlexandreBrillant/Editix-xml-editor)
 * @version 1.0
 * */
public class SingleFactoryImpl implements Factory {

	public XMLContainer buildNewContainer( String type ) {
		XMLContainer container = new XMLContainer();
		container.setToolBarAvailable( false );
		return container;
	}

	public void buildAndShowInformationDialog(String info) {
		JOptionPane.showMessageDialog( null, info );
	}

	public void buildAndShowErrorDialog(String error) {
		JOptionPane.showMessageDialog( null, error );
	}

	public PathBuilder getPathBuilder() {
		return null;
	}

}
