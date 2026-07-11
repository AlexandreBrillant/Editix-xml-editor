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

package com.japisoft.xmlpad.bookmark;

import javax.swing.text.Position;

/**
 * Object from the BookmarkModel
 * @author Alexandre Brillant (https://github.com/AlexandreBrillant/Editix-xml-editor)
 * @version 1.0
 * @see BookmarkModel */
public class BookmarkPosition implements Position {

	Object source;
	Position position;
	Object highlightFlag;
		
	/** Initialize this bookmark position
	 * @param position must not be <code>null</code> */
	public BookmarkPosition( Position position, Object highlightFlag ) {
		this.position = position;
		this.highlightFlag = highlightFlag;
	}
	
	/** @return the current bookmark position */
	public int getOffset() {
		return position.getOffset();
	}
	
	/** @return a flag for this highlight. This is for inner usage */
	public Object getHighlightFlag() {
		return this.highlightFlag;
	}

	public void setSource( Object source ) {
		this.source = source;
	}
	
	public Object getSource() {
		return source;
	}
	
	public void dispose() {
		this.source = null;
	}

}
