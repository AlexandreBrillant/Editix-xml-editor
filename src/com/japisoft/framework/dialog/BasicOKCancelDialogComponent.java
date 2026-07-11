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

package com.japisoft.framework.dialog;

import java.awt.Dialog;
import java.awt.Frame;

import javax.swing.Icon;
import javax.swing.JComponent;

import com.japisoft.framework.dialog.actions.CancelAction;
import com.japisoft.framework.dialog.actions.DialogActionModel;

/**
 * Simple Dialog with OK and Cancel Action. Note that you must called
 * setUI with your final panel before showing this dialog
 * @author Alexandre Brillant (https://github.com/AlexandreBrillant/Editix-xml-editor)
 * @version 1.0
 * */
public class BasicOKCancelDialogComponent extends BasicOKDialogComponent {

	public BasicOKCancelDialogComponent(
			Dialog owner,
			String dialogTitle,
			String title, 
			String comment, 
			Icon icon ) {
		super( owner, dialogTitle, title, comment, icon );
	}

	public BasicOKCancelDialogComponent(
			Frame owner,
			String dialogTitle,
			String title, 
			String comment, 
			Icon icon ) {
		super( owner, dialogTitle, title, comment, icon );
	}

	/** Defining the dialog actions */
	protected DialogActionModel prepareActionModel() {
		DialogActionModel actionModel = DialogManager.getDefaultDialogActionModel();
		return actionModel;
	}
		
}
