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

import com.japisoft.datasource.DataSource;
import com.japisoft.datasource.DataSourceContainer;
import com.japisoft.datasource.DataSourceContainerProxy;
import com.japisoft.datasource.DataSourceItem;
import com.japisoft.datasource.DataSourceManager;
import com.japisoft.framework.toolkit.FileToolkit;
import com.japisoft.xflows.task.TaskContext;
import com.japisoft.xflows.task.TaskRunner;
import com.japisoft.xflows.task.copy.CopyUI;

public class UnzipRunner implements TaskRunner {

	@Override
	public boolean run(TaskContext context) {
		
		if ( !context.hasParam( UnzipUI.SOURCEPATH ) ) {
			context.addError("No source path found");
			return ERROR;
		}

		if ( !context.hasParam( UnzipUI.TARGETPATH ) ) {
			context.addError("No target path found");
			return ERROR;
		}
		
		String source = context.getParam( UnzipUI.SOURCEPATH );
		String target = context.getParam( UnzipUI.TARGETPATH );
		
		if ( source.equalsIgnoreCase( target ) ) {
			context.addError( "You target must be different from your source" );
			return ERROR;
		}
		
		DataSourceContainer dsc = DataSourceManager.getDataSourceContainer( source );
		
		if ( dsc == null )
			return ERROR;
		
		if ( context.hasParam( CopyUI.SOURCEFILTER ) ) {
			dsc = new DataSourceContainerProxy( dsc, context
					.getParam(CopyUI.SOURCEFILTER));
		}

		try {
			List<DataSource> l = dsc.list();
			
			for (int i = 0; i < l.size(); i++) {

				DataSource ds = l.get( i );

				if ( ds instanceof DataSourceItem ) {
					// Unzip it
					
					DataSourceItem item = ( DataSourceItem )ds;
					unzip( item.getPath(), target );
					
				}
			}
			
			
		} catch( Exception exc ) {
			context.addError( "Can't unzip " + exc.getMessage() );
		}
		
		
		return OK;
	}

	private void unzip( String file, String target ) throws Exception {
		
		File f = new File( file );
		File realTarget = new File( target, f.getName() );
		realTarget.mkdirs();
		FileToolkit.unzip( f, realTarget );
		
	}
	
}

