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

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

import com.japisoft.editix.editor.xsd.view.DesignerViewImpl;
import com.japisoft.editix.editor.xsd.view.designer.XSDAbstractComponentImpl;

public class XSDContainerComponentImpl extends XSDAbstractComponentImpl {

	public XSDContainerComponentImpl() {
		float[] dash = { 5.0f };
		borderStyle = new BasicStroke(1.0f, BasicStroke.CAP_BUTT,
		        BasicStroke.JOIN_MITER, 5.0f, dash, 0.0f);
		setLayout( new BorderLayout( 20, 20 ) );
		opened = true; 	// Always open
		hasOpenIcon = false;
		paintName = false;
		setBackground( new Color( 230, 250, 230 ) );
	}

	// (annotation?, (simpleContent | complexContent | ((group | all |
	// choice | sequence)?, ((attribute | attributeGroup)*,
	// anyAttribute?))))
	
	public Dimension getPreferredSize() {
		Dimension layoutDimension = getLayout().preferredLayoutSize( this );
		return new Dimension(
				Math.max( layoutDimension.width, 70 ) + 10,
				Math.max( layoutDimension.height, 70 ) + 20
		);
	}

	public void paintComponent(Graphics g) {
		super.paintComponent( g );
		g.setColor( Color.GRAY );
		DesignerViewImpl.drawElementLines( getElement(), g );
	}	
	
}
