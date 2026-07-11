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

package com.japisoft.xmlpad.tree.parser;

import com.japisoft.framework.xml.parser.ErrorParsingListener;
import com.japisoft.xmlpad.error.ErrorManager;

/**
 * Error producer while parsing
 * @author Alexandre Brillant (https://github.com/AlexandreBrillant/Editix-xml-editor)
 * @version 1.0 */
public class ParsingErrorListener implements ErrorParsingListener {
	private ErrorManager manager;
		
	/** We need to inform all the other components for any parsing problems */
	ParsingErrorListener( ErrorManager manager ) {
		this.manager = manager;
	}
	public void dispose() { 
		manager = null; 
	}
	// Fire an error while a parsing problem
	public void parsingError(
			String message, 
			int offset, 
			int line, 
			int column ) {
		manager.notifyError(
				ErrorManager.ON_THE_FLY_PARSING_CONTEXT,
				true,
				null, 
				line - 1,
				column,
				offset,
				message,
				true );
	}
}
