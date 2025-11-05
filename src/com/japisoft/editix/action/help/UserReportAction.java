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

package com.japisoft.editix.action.help;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import com.japisoft.framework.ApplicationModel;
import com.japisoft.framework.dialog.report.ReportingListAction;

/**
 * @author Alexandre Brillant (https://github.com/AlexandreBrillant/Editix-xml-editor)
 * @version 1.0
 * */
public class UserReportAction extends AbstractAction {

	public void actionPerformed( ActionEvent e ) {
		ReportingListAction action = new ReportingListAction(
				"http://www.japisoft.com/editix/report/editixreply4.txt"
		);
		action.comment = 
				"Bugs and suggests list.\nThis is the result of your bugs reporting and feature requests\nNote that the version number is inner code 6 is for 2008, 7 for 2009...\nYour Current Version is " + ApplicationModel.getAppVersion();			
		action.actionPerformed( e );
	}

}

