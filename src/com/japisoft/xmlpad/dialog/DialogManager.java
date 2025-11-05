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

package com.japisoft.xmlpad.dialog;

import java.awt.Dialog;
import java.awt.Frame;
import java.awt.Window;

import javax.swing.Icon;
import javax.swing.JComponent;

/**
 * @author Alexandre Brillant (https://github.com/AlexandreBrillant/Editix-xml-editor)
 * @version 1.0
 * */
public class DialogManager {

	public static final int OK = 0;
	public static final int CANCEL = 1;

	static DialogManagerDelegate DELEGATE = null;
	
	public static void setDialogManagerDelegate( DialogManagerDelegate manager ) {
		DELEGATE = manager;
	}

	/**
	 * @param owner dialog or frame parent
	 * @param dialogTitle The title of the dialog
	 * @param title The main title
	 * @param comment The user comment
	 * @param icon An icon
	 * @param pane The user UI element
	 * @return an Action Id like OKAction.ID
	 */
	public static int showDialog( 
			Window owner, 
			String dialogTitle, 
			String title, 
			String comment, 
			Icon icon, 
			JComponent pane ) {

		if ( DELEGATE != null )
			return DELEGATE.showDialog(
					owner,
					dialogTitle,
					title,
					comment,
					icon, 
					pane );
		
		OkCancelDialog dialog = null;
		
		if ( owner instanceof Frame ) {

			dialog = new OkCancelDialog(
					(Frame)owner,
					dialogTitle,
					title,
					comment );
			
		} else
		if ( owner instanceof Dialog ) {

			dialog = new OkCancelDialog(
					(Dialog)owner,
					dialogTitle,
					title,
					comment );

		} else {

			dialog = new OkCancelDialog(
					(Frame)null,
					dialogTitle,
					title,
					comment );
						
		}

		dialog.getContentPane().add( pane );
		dialog.pack();
		dialog.setVisible( true );
		if ( dialog.isOk() )
			return OK;
		return CANCEL;
		
	}	
	
}
 
