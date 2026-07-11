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

import com.japisoft.framework.ApplicationModel;

/**
 * @author Alexandre Brillant (https://github.com/AlexandreBrillant/Editix-xml-editor)
 * @version 1.0
 * */
public class RequestReportingAction extends BugReportingAction {

	/**
	 * @param url remote server for storing user request */
	public RequestReportingAction( String url ) {
		super( url );

		dialogTitle = "Feature request";
		type = "SUG";
		userInformation = "Insert a request in the field below, this interface will include automatically your release and operating system. The title is required. If you wish to receive a reply, please insert your email. Note that you MUST have an active internet connection for using this form.";		
		image = "images/help2.png";		

	}

	public RequestReportingAction() {
		this( ApplicationModel.REPORTING_URL );
	}

}
