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

package com.japisoft.xmlpad;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

/**
 * Interface for interacting easily with JXMLPad.
 * Call getAccessibility from the XMLContainer for the implantation.
 * @author Alexandre Brillant (https://github.com/AlexandreBrillant/Editix-xml-editor)
 * @version 1.0
 * @see XMLContainer
 * */
public interface Accessibility {

	/** Reset the editing content. It should be a full XML document */
	public void setText( String text );

	/** @return the current editing content */
	public String getText();

	/** Reset the editing content using this reader */
	public void read( Reader reader ) throws IOException;

	/** Reset this writer with the editing content */
	public void write( Writer writer ) throws IOException;

	/** Call an XML action. Look at the <code>ActionModel</code> for action name
	 * @return XMLAction.VALID_ACTION when the action has been done without error */
	public boolean invokeAction( String actionName );

	/** This is only for inner usage, it mustn't be called by the user */
	void dispose();
}
