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

package com.japisoft.stylededitor.action;

import javax.swing.text.Element;

import com.japisoft.stylededitor.EditorByCSS;
import com.japisoft.stylededitor.model.TextElement;

public class CaretDownAction extends CaretAction {

	public CaretDownAction() {
		super();
	}

	public CaretDownAction( boolean selectMode ) {
		super( selectMode );
	}
		
	@Override
	protected void moveCaret(EditorByCSS view) {
		int currentOffset = view.getCaretPosition();
		int index = view.getDocument().getDefaultRootElement().getElementIndex( currentOffset );
		if ( index < view.getDocument().getDefaultRootElement().getElementCount() - 1 ) {
			Element currentElement = view.getDocument().getDefaultRootElement().getElement( index );
			int relOffset = currentOffset - currentElement.getStartOffset();
			Element upElement = null;
			while ( !( upElement instanceof TextElement ) ) {
				index++;
				upElement = view.getDocument().getDefaultRootElement().getElement( index );
				if ( index == 0 )
					break;
			}
			int newOffset = Math.min( upElement.getEndOffset(), upElement.getStartOffset() + relOffset );
			view.setCaretPosition( newOffset );
		}

	}

}

