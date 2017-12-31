package com.japisoft.xflows.task.concat;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import com.japisoft.xflows.task.TaskContext;
import com.japisoft.xflows.task.TaskRunner;

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
public class FileConcatRunner implements TaskRunner {

	
	private FileWriter fileWriter = null;
	
	public void setFileWriter( FileWriter fileWriter ) {
		this.fileWriter = fileWriter;
	}

	public boolean run( TaskContext context ) {

		File source = context.getCurrentSourceFile();
		char[] data = new char[ ( int )source.length() ];

		try {

			FileReader fr = new FileReader( source );
			try {
				fr.read( data );
			} finally {
				fr.close();
			}

			boolean tagFound = false;
			
			// Search for the starting tag
			for ( int i = 0; i < data.length - 1; i++ ) {
				if ( data[ i ] == '<' && !( data[ i + 1 ] == '?' || data[ i + 1 ] == '!' ) ) {
					fileWriter.write( data, i, data.length - i );
					tagFound = true;
					break;
				}
			}
			
			if ( !tagFound )
				context.addWarning( "No tag found" );

		} catch( FileNotFoundException exc ) {
			context.addError( "Can't find " + source );
			return ERROR;
		} catch( IOException exc ) {
			context.addError( "Error while reading " + 
					source + " : " + exc.getMessage() );
			return ERROR;
		}

		return OK;
	}

}
