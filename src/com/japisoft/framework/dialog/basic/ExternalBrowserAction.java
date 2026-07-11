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

package com.japisoft.framework.dialog.basic;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JOptionPane;

import com.japisoft.framework.ApplicationModel;
import com.japisoft.framework.ui.toolkit.BrowserCaller;

/** Call an external HTML browser like IE with the following page URL
 * This URL is called set a param value to the action
 * @author Alexandre Brillant (https://github.com/AlexandreBrillant/Editix-xml-editor) */
public class ExternalBrowserAction extends AbstractAction {

	public void actionPerformed(ActionEvent e) {
		String url = ( String )getValue( "param" );
		if ( url == null ) {
			JOptionPane.showMessageDialog( 
					ApplicationModel.MAIN_FRAME,
					"Wrong configuration no param for this action ??" );
		}
		if ( !BrowserCaller.displayURL( url ) ) {
			JOptionPane.showMessageDialog( 
					ApplicationModel.MAIN_FRAME,
					"Can't show " + url );
		}
	}

}
