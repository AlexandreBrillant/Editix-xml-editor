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

package com.japisoft.xflows;

import java.io.File;
import java.io.FileInputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import com.japisoft.editix.main.steps.ConfigurationApplicationStep;
import com.japisoft.framework.xml.XMLFileData;
import com.japisoft.framework.xml.XMLToolkit;
import com.japisoft.framework.xml.parser.ParseException;
import com.japisoft.framework.xml.parser.FPParser;
import com.japisoft.framework.xml.parser.document.Document;
import com.japisoft.framework.xml.parser.node.FPNode;
import com.japisoft.xflows.task.Task;
import com.japisoft.xflows.task.TaskManager;

public class RunScenario {

	static {
		// Force static resolution
		try {
			new ConfigurationApplicationStep();
		} catch( Throwable th ) {}
	}

	/**
	 * @param args */
	public static void main(String[] args) {
		if ( args.length == 0 ) {
			System.err.println( "Wrong parameter, no scenario file ?" );
			System.err.println( "RunScenario [Path to a scenario file]" );
			System.exit( 1 );
		} else {
			
			File scenario = new File( args[ 0 ] );
			if ( !scenario.exists() ) {
				System.err.println( "Can't find your scenario file ? => " + scenario );
				System.exit( 1 );
			} else {
				
				XMLFileData xfd = null;
				
				try {
					xfd = XMLToolkit.getContentFromInputStream( 
						new FileInputStream( scenario ), null
					);
				} catch( Throwable exc ) {
					System.err.println( "Can't read your scenario " + scenario + " :" + exc.getMessage() );
					System.exit( 1 );
				}

				FPParser p = new FPParser();
				try {
					Document doc = p.parse( new StringReader( xfd.getContent() ) );					
					List<Task> tasks = new ArrayList<Task>();
					FPNode root = ( FPNode )doc.getRoot();
					for ( int i = 0; i < root.childCount(); i++ ) {
						FPNode taskNode = ( FPNode )root.childAt( i );
						Task t = new Task();
						t.updateFromXML( taskNode );
						tasks.add( t );
					}

					System.out.println( "Running " + tasks.size() + " task(s)..." );
					TaskManager.run( tasks, TaskManager.FOREGROUND );
					System.out.println( "Terminated" );

				} catch( ParseException pe ) {
					System.err.println( "XML error found inside your scenario " + scenario + " : " + pe.getMessage() );
					System.exit( 1 );
				}
			}
			
		}
	}

}
