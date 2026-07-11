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

package com.japisoft.framework.dialog.report;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;

import com.japisoft.framework.ApplicationModel;
import com.japisoft.framework.dialog.DialogManager;

/**
 * @author Alexandre Brillant (https://github.com/AlexandreBrillant/Editix-xml-editor)
 * @version 1.0
 * */
public class ReportingListAction extends AbstractAction {

	private String url;
	
	public ReportingListAction( String url ) {
		super();
		this.url = url;
	}

	public String comment = "Bugs and suggests list.\nThis is the result of your bugs reporting and feature requests"; 
	
	public void actionPerformed( ActionEvent e ) {
		DialogManager.showDialog(
				ApplicationModel.MAIN_FRAME,
				"Reporting list",
				"User reports",
				comment,
				null,
				new ReportingListPanel( url ),
				null,
				new Dimension( 500, 400 ) );		
	}	

}
