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

package com.japisoft.framework.xml.parser.dom;

import org.w3c.dom.*;

/**
 * ProcesingInstruction. Not implemented
 * 
 * @author Alexandre Brillant (https://github.com/AlexandreBrillant/Editix-xml-editor)
 * @version 1.0
 */
public class ProcessingInstructionImpl
	extends NodeImpl
	implements ProcessingInstruction {
	public ProcessingInstructionImpl() {
		super();
	}

	/**
	  * The target of this processing instruction. XML defines this as being the 
	  * first token following the markup that begins the processing instruction.
	  */
	public String getTarget() {
		throw new RuntimeException("Not supported");
	}

	/**
	 * The content of this processing instruction. This is from the first non 
	 * white space character after the target to the character immediately 
	 * preceding the <code>?&gt;</code>.
	 * @exception DOMException
	 *   NO_MODIFICATION_ALLOWED_ERR: Raised when the node is readonly.
	 */
	public String getData() {
		throw new RuntimeException("Not supported");
	}

	public void setData(String data) throws DOMException {
		throw new RuntimeException("Not supported");
	}

}

