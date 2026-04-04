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

import java.io.File;

import javax.swing.SwingUtilities;

import com.japisoft.editix.action.file.OpenAction;

import com.japisoft.framework.ApplicationStep;

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
	
	@Override
	public void quit() {
	}

}

