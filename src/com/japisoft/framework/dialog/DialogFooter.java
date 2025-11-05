// Editix XML Editor
// https://www.editix.com
// Copyright (c) 2025 Alexandre Brillant
// 
// For non-commercial usage :
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
// For commercial use or integration into proprietary software :
// A commercial license is required. Visit https://www.editix.com for details.

package com.japisoft.framework.dialog;

import javax.swing.JComponent;
import com.japisoft.framework.dialog.actions.DialogActionModel;

/**
 * This is the bottom part of the dialog for showing the
 * available actions like OK / CANCEL ...
 * @author Alexandre Brillant (https://github.com/AlexandreBrillant/Editix-xml-editor)
 * @version 1.0
 * */
public interface DialogFooter {

	/** Reset the model for the buttons */
	public void setModel( DialogActionModel model );

	/** @return the footer view with the buttons */
	public JComponent getView();

	/** Reset the dialog that will have this dialog footer */
	public void setDialogTarget( DialogComponent dialog );
	
	/** Called once when the dialog is shown */
	public void dialogShown();

	/** Called once when the dialog is hidden */
	public void dialogHidden();

	/** @return <code>true</code> if the following action is selected */
	public boolean isDialogActionSelected( int actionId );	

	/**
	 * Enabled/Disabled an action 
	 * @param actionId a dialog action id
	 * @param enabled enabled or disable this action */
	public void setEnabled( int actionId, boolean enabled );

	/** Invoke the following action. A runtime exception can be thrown for an unknown actionId */
	public void invokeAction( int actionId );

	/** Freeing any inner resource */
	public void dispose();

}

