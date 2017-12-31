package com.japisoft.xflows.task.concat;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import com.japisoft.xflows.task.FilesTaskRunner;
import com.japisoft.xflows.task.TaskContext;

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
public class ConcatRunner extends FilesTaskRunner {


	public ConcatRunner() {
		super( new FileConcatRunner(), true );
		fileTarget = true;
		defaultProcessingLog = true;
	}

	public boolean run(TaskContext context) {
		
		if ( !context.hasParam( ConcatUI.ROOTTAG ) ) {
			context.addError( "No root tag" );
			return ERROR;
		}

		String target = context.getParam( ConcatUI.TARGETPATH );
		if ( target == null || 
				"".equals( target ) ) {
			context.addError( "No target file" );
			return ERROR;
		}

		try {

			FileWriter fw = new FileWriter( target );
			( ( FileConcatRunner )monoTask ).setFileWriter( fw );
			
			fw.write( "<?xml version=\"1.0\"?>\n" );
			fw.write( "<");
			fw.write( context.getParam( ConcatUI.ROOTTAG ) );
			fw.write( ">\n" );

			boolean ok = super.run( context );			

			fw.write( "\n</");
			fw.write( context.getParam( ConcatUI.ROOTTAG ) );
			fw.write( ">" );

			try {
				fw.close();
			} catch( IOException exc ) {
				context.addError( "Can't close " + target );
				return ERROR;
			}
			if ( ok == ERROR ) {
				new File( target ).delete();
				return ERROR;
			}
			return OK;
		} catch( IOException exc ) {
			context.addError( "Error while writing on " + target + ":" + exc.getMessage() );
			return ERROR;
		}

	}	

}
