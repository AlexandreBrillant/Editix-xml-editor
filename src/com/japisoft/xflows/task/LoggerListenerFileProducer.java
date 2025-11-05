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

package com.japisoft.xflows.task;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import com.japisoft.framework.toolkit.LoggerListener;

/**
 * @author Alexandre Brillant (https://github.com/AlexandreBrillant/Editix-xml-editor)
 * @version 1.0
 * */
public class LoggerListenerFileProducer {

	public LoggerListenerFileProducer( File logFile, LoggerListener listener ) {
		try {
			BufferedReader reader = new BufferedReader( new FileReader( logFile ) );
			try {
				String line = null;
				
				// Read at reverse
				ArrayList l = new ArrayList();
				while ( ( line = reader.readLine() ) != null ) {
					l.add( line );
				}
				
				Collections.reverse( l );

				for ( int i = 0; i < l.size(); i++ ) {
					line = ( String )l.get( i );
					if ( line.startsWith( "**" ) ) {
						listener.addError( line.substring( 2 ) );
					} else
					if ( line.startsWith( "*" ) ) {
						listener.addWarning( line.substring( 1 ) );
					} else {
						listener.addInfo( line );
					}
				}
			} finally {
				reader.close();
			}
		} catch( IOException exc ) {
			listener.addError( exc.getMessage() );
		}
	}

}

