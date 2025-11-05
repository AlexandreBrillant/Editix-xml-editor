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

package com.japisoft.p3;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import com.japisoft.framework.ApplicationModel;

/**
 * Very bad users preferring stealing my work
 * @author Alexandre Brillant (https://github.com/AlexandreBrillant/Editix-xml-editor)
 * @version 1.0
 * */
public final class WrongUserController {
	
	static ArrayList DEAD_ZONE = new ArrayList();

	static {
		DEAD_ZONE.add( "Doi La Phu Du" );
		DEAD_ZONE.add( "Malaika D. Maddox" );

		try {
			InputStream input = ClassLoader.getSystemResourceAsStream( "users.dea" );
			BufferedReader reader = new BufferedReader(
					new InputStreamReader( input ) );
			try {
				
				ApplicationModel.debug( "Using users.dea" );				
				
				String line = null;
				int forMajorVersion = -1;
				while ( ( line = reader.readLine() ) != null ) {
					if ( line.startsWith( "-" ) ) {
						forMajorVersion = Integer.parseInt( line.substring( 1 ) );
					} else
						if ( forMajorVersion == ApplicationModel.MAJOR_VERSION ) {
							DEAD_ZONE.add( line );
						}

				}
			} finally {
				reader.close();
			}
		} catch( Throwable th ) {
		}
	}

	public static void addToDeadZone( String user ) {
		DEAD_ZONE.add( user );
	}

	static void control(String user) {
		if ( DEAD_ZONE.contains( user ) ) {
			throw new RuntimeException( "Wrong user" );
		}
	}

}

