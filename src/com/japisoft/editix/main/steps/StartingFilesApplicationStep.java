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
