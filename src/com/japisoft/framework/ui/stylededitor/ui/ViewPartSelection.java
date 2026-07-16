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

package com.japisoft.framework.ui.stylededitor.ui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;

import javax.swing.text.Element;

import com.japisoft.framework.ui.stylededitor.EditorByCSS;

public class ViewPartSelection implements ViewPart {

	public void paint( EditorByCSS editor, Graphics gc ) {
		
		if ( editor.getSelectionEnd() != editor.getSelectionStart() ) {
			// Display selection
			
			int start = Math.min( editor.getSelectionStart(), editor.getSelectionEnd() );
			int end = Math.max( editor.getSelectionStart(), editor.getSelectionEnd() );

			int indexStartSelection = editor.getDocument().getDefaultRootElement().getElementIndex( start );
			int indexEndSelection = editor.getDocument().getDefaultRootElement().getElementIndex( end );

			Element startElement = editor.getDocument().getDefaultRootElement().getElement( indexStartSelection );
			Element endElement = editor.getDocument().getDefaultRootElement().getElement( indexEndSelection );

			if ( startElement == null ) return;
			if ( endElement == null ) return;

			int startOffset = start - startElement.getStartOffset();
			int endOffset = end - endElement.getStartOffset();			
			
			for ( int i = indexStartSelection; i<= indexEndSelection; i++ ) {

				Element e = editor.getDocument().getDefaultRootElement().getElement( i );
				if ( e == null )
					return;

				int startOffsetTmp = e.getStartOffset();
				int endOffsetTmp = e.getEndOffset();

				if ( i == indexStartSelection ) {
					startOffsetTmp += startOffset;
				}

				if ( i == indexEndSelection ) {
					endOffsetTmp = e.getStartOffset() + endOffset;
				}

				Rectangle r1 = editor.modelToView( 
						startOffsetTmp 
				);
				Rectangle r2 = editor.modelToView( 
						endOffsetTmp 
				);				

				if ( r1 == null ) {
					return;
				}

				if ( r2 == null ) {
					return;
				}

				int xi = r1.x + r1.width;
				int yi = r1.y;

				int xj = r2.x + r2.width;
				int yj = r2.y + r2.height;

				gc.setColor( Color.WHITE );
				gc.setXORMode( Color.BLUE );
				gc.fillRect( xi, yi, xj - xi, yj - yi );
				
			}

		}
	}

}
