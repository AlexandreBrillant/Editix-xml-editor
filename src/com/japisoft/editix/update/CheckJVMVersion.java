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

package com.japisoft.editix.update;

import com.japisoft.framework.ApplicationModel;

public class CheckJVMVersion {

	public static String check() {

		String version = System.getProperty( "java.vm.version" );
		
		if ( version != null ) {
			if ( version.startsWith( "1.1" ) ) {
				return "You current Java version is too old (1.1). " + getRequireMessage();
			} else
			if ( version.startsWith( "1.2" ) ) {
				return "You current Java version is too old (1.2). " + getRequireMessage();
			} else
			if ( version.startsWith( "1.3" ) ) {
				return "You current Java version is too old (1.3). " + getRequireMessage();
			} else
			if ( version.startsWith( "1.4" ) ) {
				return "You current Java version is too old (1.4). " + getRequireMessage();
			} else
			if ( version.startsWith( "1.5" ) ) {
				return "You current Java version is too old (1.5). " + getRequireMessage();
			} else
			if ( version.startsWith( "1.6" ) ) {
				return "You current Java version is too old (1.6). " + getRequireMessage();
			} else
			if ( version.startsWith( "1.7" ) ) {
				return "You current Java version is too old (1.7). " + getRequireMessage();
			}

		} 

		return null;
	}

	private static String getRequireMessage() {
		String require = "EditiX requires at least a Java 8 version\nPlease download it at ";
		if ( ApplicationModel.isMacOSXPlatform() ) {
			require += "http://developer.apple.com/java/";
		} else
			require += "http://www.javasoft.com";
		return require;
	}
	
}

