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


package com.japisoft.editix.main.steps;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.Reader;

import com.japisoft.editix.main.EditixApplicationModel;
import com.japisoft.framework.ApplicationStepAdapter;

public class NonCommercialUsageStep extends ApplicationStepAdapter {

	@Override
	public void start(String[] args) {
		File reg1File = EditixApplicationModel.getAppFile( "editix21.reg" );
		if ( !reg1File.exists() ) {
			File reg2File = EditixApplicationModel.getAppFile( "editix20.reg" );
			if ( !reg2File.exists() ) {
				try {
					Reader r = new InputStreamReader( ClassLoader.getSystemResourceAsStream( "key.txt" ) );
					BufferedReader br = new BufferedReader( r );
					try {
						String user = br.readLine();
						String key = br.readLine();

						FileWriter w = new FileWriter( reg1File );
						try {
							w.write( user + "\n" );
							w.write( key + "\n" );
						} finally {
							w.close();
						}


					} finally {
						br.close();
					}
				} catch( Exception exc ) {
					exc.printStackTrace();
				}
			}
		}
	}


}
