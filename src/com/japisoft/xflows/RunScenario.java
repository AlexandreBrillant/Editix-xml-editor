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
