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

package com.japisoft.xflows.task;

import java.io.File;
import com.japisoft.editix.ui.EditixFactory;

public class SwingActionTaskContext extends AbstractTaskContext {

	private File sourceFile;
	private File targetFile;

	public SwingActionTaskContext( 
		File sourceFile, 
		File targetFile ) {
		this.sourceFile = sourceFile;
		this.targetFile = targetFile;
	}

	public SwingActionTaskContext( 
			File sourceFile ) {
		this( 
			sourceFile, 
			null 
		);
	}	

	@Override
	public File getCurrentSourceFile() {
		return sourceFile;
	}

	@Override
	public File getCurrentTargetFile() {
		return targetFile;
	}
	
	private boolean errorFound = false;
	
	@Override
	public void addError( String message ) {
		EditixFactory.buildAndShowErrorDialog( message );
		interrupt();
		errorFound = true;
	}
	
	@Override
	public boolean hasErrorFound() {
		return errorFound;
	}

}

