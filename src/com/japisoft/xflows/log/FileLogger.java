// Editix XML Editor
// https://www.editix.com
// Copyright (c) 2026 Alexandre Brillant
//
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
// AI Training Restriction :
// This source code is provided for human use only.
// Using this code to train, fine-tune, or develop AI models,
// machine learning systems, or similar technologies is
// STRICTLY PROHIBITED. Violations will terminate all rights
// under the applicable license.

package com.japisoft.xflows.log;

import java.io.FileWriter;
import java.io.IOException;
import com.japisoft.framework.toolkit.LoggerListener;
import com.japisoft.xflows.LoggerModel;
import com.japisoft.xflows.XFlowsApplicationModel;

/**
 * @author Alexandre Brillant (https://github.com/AlexandreBrillant/Editix-xml-editor)
 * @version 1.0 */
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
