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

package com.japisoft.framework.toolkit;

import java.util.Date;

/**
 * @author Alexandre Brillant (https://github.com/AlexandreBrillant/Editix-xml-editor)
 * @version 1.0
 * */
public class ConsoleLogger extends Logger {

	public String log( int level, String message ) {
		switch( level ) {
			case Logger.INFO : System.out.println( message = ( getLoggerHeader() + message ) );
			break;
			case Logger.WARNING : System.out.println( message = ( "*" + getLoggerHeader() + message ) );
			break;
			case Logger.ERROR : System.out.println( message = ( "**" + getLoggerHeader() + message ) );
			break;
		}
		return message;
	}

	String getLoggerHeader() {
		return ( " [" + new Date() + "] " );
	}

}

