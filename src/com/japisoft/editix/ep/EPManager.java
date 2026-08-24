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

package com.japisoft.editix.ep;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import com.japisoft.framework.ApplicationModel;
import com.japisoft.framework.toolkit.FileToolkit;

public class EPManager {

	private EPManager() {}

	private static EPManager instance = null;

	public static EPManager instance() {
		if ( instance == null )
			instance = new EPManager();
		return instance;
	}
	
	public File getEPHome() {
		File home = ApplicationModel.getAppUserPath();
		File root = new File( home, "ep" );
		return new File( root, Integer.toString( ApplicationModel.MAJOR_YEAR ) );
	}
	
	public boolean hasEp() {
		return getEPHome().exists();
	}

	public boolean install( File source ) throws Exception {
		boolean ok = false;		
		ZipInputStream input = new ZipInputStream( new FileInputStream( source ) );
		try {
			ZipEntry ze = null;
			File home = ApplicationModel.getAppUserPath();
			if ( !home.canWrite() ) {
				throw new Exception( "Can't write to " + home + " ? [check application rights]" );
			}
			File output = new File( home, "ep" );
			output.mkdirs();

			if ( !output.exists() )
				throw new Exception( "Can't create " + output + " ? [check application rights]" );

			while ( ( ze = input.getNextEntry() ) != null ) {
				if ( !ze.isDirectory() )
				if ( installEp( ze.getName(), input, output ) )
					ok = true;
			}			
		} finally {
			input.close();
		}
		if ( !ok ) {
			throw new Exception( "Invalid format" );
		} else
			return true;
	}
	
	public boolean uninstall() throws IOException {
		File home = ApplicationModel.getAppUserPath();
		File output = new File( home, "ep" );
		File finalDirectory = new File( output, Integer.toString( ApplicationModel.MAJOR_YEAR ) );
		FileToolkit.deleteDirectory( finalDirectory );

		File doc = new File( "doc/ext/" + ApplicationModel.MAJOR_VERSION );
		if ( doc.exists() )
			FileToolkit.deleteDirectory( doc );

		return !finalDirectory.exists();
	}

	private byte[] buffer = null;
	
	private boolean installEp( String name, InputStream input, File output ) throws Exception {
		int i = name.indexOf( "ep/" );
		if ( i == -1 )
			return false;
		name = name.substring( i + 3 );
		
		if ( name.startsWith( "/" ) )
			name = name.substring( 1 );
		if ( "".equals( name ) )
			return false;
		i = name.lastIndexOf( "/" );
		if ( i > -1 ) {
			output = new File( output, name.substring( 0, i ) );
			output.mkdirs();
			name = name.substring( i + 1 );
		}

		if ( buffer == null )
			buffer = new byte[ 1024 ];
		
		int c;
		
		FileOutputStream foutput = new FileOutputStream( new File( output, name ) );

		while ( ( c = input.read( buffer ) ) != -1 ) {
			foutput.write( buffer, 0 , c );
		}
		
		foutput.close();
		return true;
	}

}
