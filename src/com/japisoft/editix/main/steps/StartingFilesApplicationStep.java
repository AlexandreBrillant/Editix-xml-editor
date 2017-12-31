package com.japisoft.editix.main.steps;

import java.io.File;

import javax.swing.SwingUtilities;

import com.japisoft.editix.action.file.OpenAction;
import com.japisoft.editix.main.EditixApplicationModel;
import com.japisoft.framework.ApplicationStep;

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
public class StartingFilesApplicationStep implements ApplicationStep {

	public boolean isFinal() {
		return false;
	}

	public void start(String[] args) {
				
		for ( String file : args ) {
			final String file2 = file;
			SwingUtilities.invokeLater(
				new Runnable() {
					public void run() {
						OpenAction.openFile(
								null,
								false,
								new File( file2 ),
								null
						);											
					}
				} 
			);
		}
	}

	public void stop() {
	}

}
