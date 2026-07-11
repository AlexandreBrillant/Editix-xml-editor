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

package com.japisoft.framework.toolkit;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

public class BatchGenerator {
	
	public void createBatch( File dir, String output, String mainClass ) throws Throwable {
		File lib = new File( dir, "lib" );

		BufferedWriter bw = new BufferedWriter(
				new FileWriter( output + ".bat" ) );
		try {
			String[] strl = lib.list();
			bw.write( "set R=../lib" );
			bw.newLine();
			bw.write( "set CP=../res" );
			bw.newLine();
			for ( int i = 0; i < strl.length; i++ ) {
				if ( strl[ i ].endsWith( "jar" ) ) { 
					bw.write(
						"set CP=%CP%;%R%/" + strl[ i ] );
					bw.newLine();
				}
			}
			bw.write( "java -classpath %CP% -Xms64m -Xmx512m " + mainClass + " %1" );
		} finally {
			bw.close();
		}
		
		bw = new BufferedWriter(
				new FileWriter( output + ".sh" ) );
		try {
			String[] strl = lib.list();
			bw.write( "CP=../res" );
			for ( int i = 0; i < strl.length; i++ ) {
				if ( strl[ i ].endsWith( "jar" ) ) { 
					bw.write(
						":../lib/" + strl[ i ] );
					System.out.println( "<string>$JAVAROOT/" + strl[ i ] + "</string>" );					
				}
			}
			bw.newLine();			
			bw.write( "java -classpath $CP -Xms64m -Xmx512m " + mainClass + " $1" );
		} finally {
			bw.close();
		}		


		
		
	}

	public static void main( String[] args ) throws Throwable {				
		BatchGenerator bg = new BatchGenerator();
		
		bg.createBatch( 
				new File( "C:/travail/soft/xflows/distrib/install-content" ),
				"C:/travail/soft/xflows/distrib/install-content/bin/run",
				"com.japisoft.xflows.Main" );
				
		
/*		
 
 		if ( args == null || args.length == 0 ) {
			System.out.println( "[output dir] [main class]");
			System.exit( 1 );
		}
 
		bg.createBatch(
				new File( System.getProperty( "user.dir") ),
				args[ 0 ],
				args[ 1 ] ); */
	}

}
