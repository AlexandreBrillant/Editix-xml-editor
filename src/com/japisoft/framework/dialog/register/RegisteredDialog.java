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

package com.japisoft.framework.dialog.register;

import java.awt.Window;

import com.japisoft.framework.dialog.DialogManager;

/**
 * Dialog for registering to the product
 * @author Alexandre Brillant (https://github.com/AlexandreBrillant/Editix-xml-editor)
 * @version 1.0
 * */
public class RegisteredDialog {

	/**
	 * @param owner Window parent
	 * @param locationToOrder Site for ordering
	 * @param personalPrice Personal price for the product. It can be <code>null</code>
	 * @param companyPrice Company price for the product
	 * @param currency Currency for buying if null then 'US dollar' 
	 * @return The result pane if the user press 'OK' else <code>null</code>
	 */
	public static RegisteredPane showDialog( Window owner, String locationToOrder, String personalPrice, String companyPrice, String currency ) {
		RegisteredPane pane = new RegisteredPane( locationToOrder, personalPrice, companyPrice, currency == null ? "US Dollar ($)" : currency );
		if ( DialogManager.showDialog(
				owner,
				"Register", 
				"Register", 
				"You can order at " + locationToOrder + " and \nreceive instantly by mail your registered key",
				null,
				pane ) == DialogManager.OK_ID )
			return pane;
		
		return null;
	}

}
