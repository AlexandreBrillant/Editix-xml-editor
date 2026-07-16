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

package com.japisoft.xflows.task.copy;

import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import com.japisoft.framework.datasource.DataSource;
import com.japisoft.framework.datasource.DataSourceContainer;
import com.japisoft.framework.datasource.DataSourceContainerProxy;
import com.japisoft.framework.datasource.DataSourceItem;
import com.japisoft.framework.datasource.DataSourceManager;
import com.japisoft.xflows.task.TaskContext;
import com.japisoft.xflows.task.TaskRunner;

/**
 * @author Alexandre Brillant (https://github.com/AlexandreBrillant/Editix-xml-editor)
 * @version 1.0
 */
public class CopyRunner implements TaskRunner {

	public boolean run(TaskContext context) {

		if ( !context.hasParam( CopyUI.SOURCEPATH ) ) {
			context.addError("No source path found");
			return ERROR;
		}

		if ( !context.hasParam( CopyUI.TARGETPATH ) ) {
			context.addError("No target path found");
			return ERROR;
		}

		DataSourceContainer dsc = DataSourceManager.getDataSourceContainer( context
				.getParam(CopyUI.SOURCEPATH) );

		if ( dsc == null )
			return ERROR;
		
		if ( context.hasParam( CopyUI.SOURCEFILTER ) ) {
			dsc = new DataSourceContainerProxy( dsc, context
					.getParam(CopyUI.SOURCEFILTER));
		}
		
		DataSourceContainer dsct = DataSourceManager.getDataSourceContainer( context
				.getParam(CopyUI.TARGETPATH) );
		
		if ( dsct == null )
			return ERROR;

		boolean processed = false;
		Pattern p = null;

		try {

			List<DataSource> l = dsc.list();

			for (int i = 0; i < l.size(); i++) {

				DataSource ds = l.get( i );

				if ( ds instanceof DataSourceItem ) {

						processed = true;
						byte[] content = ( ( DataSourceItem )ds ).getContent();
						DataSourceItem item = dsct.createItem( ds.getName() );
						item.setContent( content );

				}

			}

			if (!processed) {
				context.addWarning("No file found in "
						+ context.getParam(CopyUI.SOURCEPATH));
			}

		} catch (PatternSyntaxException exc) {
			context.addError("Wrong source filter "
					+ context.getParam(CopyUI.SOURCEFILTER) + " : "
					+ exc.getMessage());
		} catch( Exception exc ) {
			context.addError( "Can't copy " + exc.getMessage() );
		}

		return OK;
	}

}