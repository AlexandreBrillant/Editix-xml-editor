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

package com.japisoft.xmlpad;

import javax.swing.JFileChooser;

/**
 * This factory will help you to customize a component used by JXMLPad
 * @author Alexandre Brillant (https://github.com/AlexandreBrillant/Editix-xml-editor)
 */
public class UIFactory {

	private static UIFactory singleton = null; 

	protected UIFactory() {
		singleton = this;
	}

	/**
	 * You can override the default one by this one
	 * @param factory
	 */
	public static void setInstance( UIFactory factory ) {
		singleton = factory;
	}
	
	public static UIFactory getInstance() {
		if ( singleton == null )
			new UIFactory();
		return singleton; 
	}
	
	public JFileChooser getOpenFileChooser() {
		return new JFileChooser();
	}

	public JFileChooser getSaveFileChooser() {
		return new JFileChooser();
	}
	
}

