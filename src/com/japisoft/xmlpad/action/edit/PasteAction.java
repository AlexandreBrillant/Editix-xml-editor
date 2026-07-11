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

package com.japisoft.xmlpad.action.edit;

import com.japisoft.xmlpad.action.XMLAction;

/**
 * Paste action
 * @author Alexandre Brillant (https://github.com/AlexandreBrillant/Editix-xml-editor)
 * @version 1.1 */
public class PasteAction extends XMLAction {

	public static final String ID = PasteAction.class.getName();
	
	public PasteAction() {
		super();
	}
	
	protected boolean autoRequestFocus() { return false; }

	public boolean notifyAction() {
		editor.paste();
		return VALID_ACTION;
	}

	protected void notifyXMLContainer() {
		setEnabled( container.isEditable() );
	}

}

