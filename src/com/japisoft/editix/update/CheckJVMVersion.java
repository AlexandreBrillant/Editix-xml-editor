package com.japisoft.editix.update;

import com.japisoft.framework.ApplicationModel;

/**
This program is available under two licenses : 

1. For non commercial usage : 

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.

2. For commercial usage :

You need to get a commercial license for source usage at : 

http://www.editix.com/buy.html

Copyright (c) 2018 Alexandre Brillant - JAPISOFT SARL - http://www.japisoft.com

@author Alexandre Brillant - abrillant@japisoft.com
@author JAPISOFT SARL - http://www.japisoft.com

*/
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
			}

		} 

		return null;
	}

	private static String getRequireMessage() {
		String require = "EditiX requires at least a Java 5 version\nPlease download it at ";
		if ( ApplicationModel.isMacOSXPlatform() ) {
			require += "http://developer.apple.com/java/";
		} else
			require += "http://www.javasoft.com";
		return require;
	}
	
}
