package com.japisoft.xflows.task.imp.cvs;

import java.io.FileWriter;
import java.io.IOException;

import com.japisoft.framework.xml.imp.CSVImport;
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
public class CSVFileRunner implements TaskRunner {

	public boolean run( TaskContext context ) {
		context.addInfo( "Importing " + context.getCurrentSourceFile() );

		CSVImportParamsImpl params = 
			new CSVImportParamsImpl( context.getParams() );

		try {
			String buffer = CSVImport.impCSV(
					context.getCurrentSourceFile(),
					params );
			FileWriter writer = new FileWriter( context.getCurrentTargetFile() );
			try {
				writer.write( buffer );
			} finally {
				writer.close();
			}
		} catch (IOException e) {
			context.addError( e.getMessage() );
			return TaskRunner.ERROR;
		}

		return TaskRunner.OK;
	}

}
