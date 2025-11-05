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

package com.japisoft.xmlpad.nodeeditor;

import com.japisoft.framework.xml.parser.node.FPNode;
import com.japisoft.xmlpad.XMLContainer;

/**
 * <p>Here the context for each Editor. This context gives essential data like
 * the initial text, the current node for edition.</p>
 * <p>
 * Once the editor has made an editing job, it must write the result with
 * the <code>setResult</code> method.
 * </p>
 * @author Alexandre Brillant (https://github.com/AlexandreBrillant/Editix-xml-editor)
 * @see Editor */
public class EditorContext {
	private FPNode node;
	private String text;
	private XMLContainer container;

	/** 
	 * @param node The current edited node
	 * @param text The current edited text equals to the current edited node
	 */	
	public EditorContext( XMLContainer container, FPNode node, String text ) {
		this.node = node;
		this.text = text;
		this.container = container;
	}

	/** @return the current XMLContainer */	
	public XMLContainer getXMLContainer() { 
		return container; 
	}

	/** @return the current edited node */
	public FPNode getEditedNode() { 
		return node;
	}

	/** @return the current edited text */
	public String getEditedText() {
		return text;
	}

	private String result;

	/** Set the editing result. <code>null</code> means there's no
	 * editing changes
	 * @param result
	 */	
	public void setResult( String result ) {
		this.result = result;
	}

	/** @return the current editing result */
	public String getResult() {
		return result;
	}
}

