package com.japisoft.editix.editor.xsd.view.designer.container;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

import com.japisoft.editix.editor.xsd.view.DesignerViewImpl;
import com.japisoft.editix.editor.xsd.view.designer.XSDAbstractComponentImpl;

/**
This program is available under two licenses : 

1. For non commercial usage : 

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.

2. For commercial usage :

You need to get a commercial license for source usage at : 

http://www.editix.com/buy.html

Copyright (c) 2018 Alexandre Brillant - JAPISOFT SARL - http://www.japisoft.com

@author Alexandre Brillant - abrillant@japisoft.com
@author JAPISOFT SARL - http://www.japisoft.com

*/
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
