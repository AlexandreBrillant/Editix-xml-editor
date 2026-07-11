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

package com.japisoft.editix.action.xsl.result;

import java.io.File;
import java.io.OutputStream;

import javax.xml.transform.stream.StreamResult;

import com.japisoft.framework.toolkit.FileToolkit;

public class StreamResultFactory {

	private StreamResultFactory() {}
	
	private static StreamResultFactory Instance = null;
	
	public static StreamResultFactory instance() {
		if ( Instance == null )
			Instance = new StreamResultFactory();
		return Instance;
	}
	
	public StreamResult streamResult( int version, String res ) throws Exception {
		StreamResult sr = null;
		
		OutputStream output = DocumentTypeModel.instance().open( res );
		if ( output != null )
			return new StreamResult( output );
		
		// Default
		
		if ( version == 1 ) {
			sr = new StreamResult( res );
		}
		else {
			sr = new StreamResult( new File( res ) );
		}
		return sr;
	}
	
	public void endProcess( String res )  throws Exception {
		DocumentTypeModel.instance().close( res );
	}
	
	public boolean canRead( String res ) {
		String fileExt = FileToolkit.fileExt( res );
		return !DocumentTypeModel.instance().processed( fileExt );
	}
	
}
