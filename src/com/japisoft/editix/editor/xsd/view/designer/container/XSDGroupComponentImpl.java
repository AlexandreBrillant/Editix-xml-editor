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

package com.japisoft.editix.editor.xsd.view.designer.container;

import java.awt.Dimension;
import java.awt.Graphics;

import org.w3c.dom.Element;

import com.japisoft.editix.editor.xsd.view.designer.LeftToRightLayout;

public class XSDGroupComponentImpl extends XSDContainerComponentImpl {

	public XSDGroupComponentImpl() {
		setLayout( new LeftToRightLayout() );
	}

	private boolean foundRef = false;
	private Dimension refPreferredSize = null;
	private String lastRef;

	public void setElement(Element e) {
		super.setElement(e);
		resetSize( false );
	}	
	
	private void resetSize( boolean invalidateMode ) {
		if ( !foundRef ) {
			if ( e.hasAttribute( "ref" ) ) {
				foundRef = true;
				paintName = true;
				paintElementName = false;
				lastRef = e.getAttribute( "ref" );
				// Reduce the size
				refPreferredSize = getNameSize();
				if ( invalidateMode ) {
					invalidate();
					getParent().validate();
				}
			}
		} else {
			// The ref attribute has been remove
			if ( !e.hasAttribute( "ref" ) ) {
				foundRef = false;
				paintName = false;
				paintElementName = true;
				refPreferredSize = null;
				if ( invalidateMode ) {
					invalidate();
					getParent().invalidate();
					getParent().getParent().validate();
				}
			} else 
				if ( lastRef != null ) {
					if ( !lastRef.equals( e.getAttribute( "ref" ) ) ) {
						lastRef = e.getAttribute( "href" );
						refPreferredSize = getNameSize();
						if ( invalidateMode ) {
							invalidate();
							getParent().validate();
						}
					}
				}
		}
	}
	
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		resetSize( true );
	}

	public Dimension getPreferredSize() {
		if ( refPreferredSize != null )
			return refPreferredSize;
		return super.getPreferredSize();
	}	

}
