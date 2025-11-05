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

package com.japisoft.xflows.task.zip;

import java.io.File;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import com.japisoft.datasource.DataSource;
import com.japisoft.datasource.DataSourceContainer;
import com.japisoft.datasource.DataSourceContainerProxy;
import com.japisoft.datasource.DataSourceItem;
import com.japisoft.datasource.DataSourceManager;
import com.japisoft.framework.toolkit.FileToolkit;
import com.japisoft.xflows.task.TaskContext;
import com.japisoft.xflows.task.TaskRunner;
import com.japisoft.xflows.task.copy.CopyUI;

public class ZipRunner implements TaskRunner {

	@Override
	public boolean run(TaskContext context) {
		if ( !context.hasParam( ZipUI.SOURCEPATH ) ) {
			context.addError("No source path found");
			return ERROR;
		}

		if ( !context.hasParam( ZipUI.TARGETPATH ) ) {
			context.addError("No target path found");
			return ERROR;
		}
		
		String source = context.getParam( ZipUI.SOURCEPATH );
		String filter = context.getParam( ZipUI.SOURCEFILTER );
		String target = context.getParam( ZipUI.TARGETPATH );
		String tname = context.getParam( ZipUI.TARGETNAME );
		
		Pattern pname = null;
		
		try {
			pname = Pattern.compile( filter );
		} catch( PatternSyntaxException exc ) {
			context.addError( "Invalid regular expression for the source filter :" + exc.getMessage() );
			return ERROR;
		}
		
		if ( source.equalsIgnoreCase( target ) ) {
			context.addError( "You target must be different from your source" );
			return ERROR;
		}
		
		DataSourceContainer dsc = DataSourceManager.getDataSourceContainer( source );
		
		if ( dsc == null )
			return ERROR;
		
		if ( context.hasParam( ZipUI.SOURCEFILTER ) ) {
			dsc = new DataSourceContainerProxy( dsc, context
					.getParam(ZipUI.SOURCEFILTER));
		}
		
		try {
			List<DataSource> l = dsc.list();
			
			for (int i = 0; i < l.size(); i++) {

				DataSource ds = l.get( i );
				
				// Get directories only

				if ( ds instanceof DataSourceContainer ) {
					// Unzip it
					
					DataSourceContainer item = ( DataSourceContainer )ds;
					
					String name = item.getName();
					Matcher m = pname.matcher( name );
					
					if ( m.matches() ) {

						String newName = name;
						
						if ( m.groupCount() > 0 ) {
							newName = m.group( 1 );
						}
	
						if ( newName == null ) {
							throw new Exception( "Invalid name for the target name, check your source filter " + pname.pattern() );
						}
						
						newName = tname.replace( "$1", newName );
						
						File archiveName = new File( target, newName );
						FileToolkit.zip( new File( item.getPath() ) , archiveName );
					
					}
					
				}
			}
			
			
		} catch( Exception exc ) {
			context.addError( "Can't zip this content " + exc.getMessage() );
			return ERROR;
		}
		
		
		return OK;
	}
	
	public static void main( String[] args ) {
		Pattern p = Pattern.compile( "(.*)" );
		Matcher m = p.matcher( "AAAA" );
		if ( m.matches() ) {
			System.out.println( m.group( 1 ) );
		}
		
	}
	
}

