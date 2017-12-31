package com.japisoft.xflows.log;

import java.io.FileWriter;
import java.io.IOException;
import com.japisoft.framework.toolkit.LoggerListener;
import com.japisoft.xflows.LoggerModel;
import com.japisoft.xflows.XFlowsApplicationModel;

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
public class FileLogger implements LoggerListener {
	private FileWriter logWarning = null;
	private FileWriter logError = null;
	private boolean checkErrorOnce = false;

	private void log( String message, String target ) {
		FileWriter logInfo = null;
		if ( !checkErrorOnce ) {
			try {
				logInfo = 
					new FileWriter(
							target, true );
			} catch( Throwable th ) {
				checkErrorOnce = true;
				System.err.println( "Can't write to " + target );
			}
		}

		if ( logInfo != null ) {
			try {
				try {
					logInfo.write( message );
					logInfo.write( System.getProperty( "line.separator" ) );
				} finally {
					logInfo.close();
					logInfo = null;
				}
			} catch( IOException exc ) {
				System.err.println( "Can't write to " + target );
				checkErrorOnce = true;
			}
		}
	}

	public void addInfo( String message ) {
		LoggerModel model = XFlowsApplicationModel.ACCESSOR.getLogger();
		if ( model.isFileLogInfoEnabled() && model.getFileLogInfo() != null )
			log( message, model.getFileLogInfo() );
	}

	public void addWarning( String message ) {
		LoggerModel model = XFlowsApplicationModel.ACCESSOR.getLogger();
		if ( model.isFileLogWarningEnabled() && model.getFileLogWarning() != null )
			log( message, model.getFileLogWarning() );		
	}

	public void addError( String message ) {
		LoggerModel model = XFlowsApplicationModel.ACCESSOR.getLogger();
		if ( model.isFileLogErrorEnabled() && model.getFileLogError() != null )
			log( message, model.getFileLogError() );				
	}

}
