package com.japisoft.xflows.task.exp.json;

import java.io.File;
import java.io.FileInputStream;

import org.json.JSONObject;
import org.json.XML;

import com.japisoft.framework.toolkit.FileToolkit;
import com.japisoft.framework.xml.XMLFileData;
import com.japisoft.framework.xml.XMLToolkit;
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
public class JSonFileRunner implements TaskRunner  {

	public boolean run( TaskContext context ) {
		context.addInfo( 
			"Exporting " + 
				context.getCurrentSourceFile() );

		File source = context.getCurrentSourceFile();
		File target = context.getCurrentTargetFile();

		try {
			String xmlData = context.getTaskSource();
			
			if ( xmlData == null ) {				
				XMLFileData xfd = XMLToolkit.getContentFromInputStream( 
					new FileInputStream( source ), 
					null 
				);
				xmlData = xfd.getContent();
			}
			
			JSONObject json = XML.toJSONObject( xmlData );
			String result = json.toString();
			FileToolkit.writeFile( 
				target, 
				result, 
				context.getDefaultEncoding() 
			);

		} catch( Throwable exc ) {
			context.addError(
				exc.getMessage() 
			);
			return TaskRunner.ERROR;
		}

		return TaskRunner.OK;
	}	

}
