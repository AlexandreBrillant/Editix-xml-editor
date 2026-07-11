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

package com.japisoft.framework.dialog.error;

import java.io.PrintWriter;
import java.io.StringWriter;
import com.japisoft.framework.ApplicationModel;

/** Simple output on the console for application command mode */
public class ErrorConsole {

	/** Show this exception with information to the user in the error output */
	public static void show( Throwable th ) {
		StringWriter sw = new StringWriter();
		th.printStackTrace(new PrintWriter( sw ) );
		StringBuffer sb = new StringBuffer();
		sb.append( ApplicationModel.getAppNameVersion() );
		sb.append( "\nHas met an unknown error, please send it to : " + ApplicationModel.MAIN_SUPPORT_EMAIL + "\n" );
		sb.append( "specifying your operating system version and java version\n" );
		sb.append( "* Java version : " ).append( System.getProperty( "java.version" ) ).append( "\n" );
		sb.append( "* OS : " ).append( System.getProperty( "os.name" ) ).append( "\n" );		
		sb.append( "\n----------------------------------------------------------------\n\n" );
		sb.append( sw.toString() );
		System.err.println( sb.toString() );		
	}
	
}
