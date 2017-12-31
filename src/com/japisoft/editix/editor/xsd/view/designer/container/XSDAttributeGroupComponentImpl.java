package com.japisoft.editix.editor.xsd.view.designer.container;

import java.awt.BasicStroke;
import java.awt.Dimension;
import java.awt.Shape;
import java.awt.geom.RoundRectangle2D;

import com.japisoft.editix.editor.xsd.view.designer.LeftToRightLayout;

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
public class XSDAttributeGroupComponentImpl extends XSDContainerComponentImpl {

	public XSDAttributeGroupComponentImpl() {
		super();
		setLayout( new LeftToRightLayout() );
		float[] dash = { 5.0f };
		borderStyle = new BasicStroke(1.0f, BasicStroke.CAP_BUTT,
		        BasicStroke.JOIN_MITER, 5.0f, dash, 0.0f);
		paintName = true;		
	}

	protected Shape createBorderShape() {
		int openWidth = OPEN_ICON.getIconWidth();
		int openHeight = OPEN_ICON.getIconHeight();
		int y = 1;
		int x = 1;
		if ( !hasOpenIcon ) {
			openWidth = 0;
			openHeight = 0;
		}
		return new RoundRectangle2D.Double( (double)x, (double)y, (double)( getWidth() - openWidth - 2 ),
				(double)( getHeight() - 2 ), 10.0, 10.0 );
	}
	
	protected boolean isNameVisible() { return e.hasAttribute( "ref" ); }		

	public Dimension getPreferredSize() {
		Dimension layoutDimension = getLayout().preferredLayoutSize( this );
		return new Dimension(
				Math.max( layoutDimension.width, 50 ) + 10,
				Math.max( layoutDimension.height, 20 ) + 20
		);
	}
	
}
