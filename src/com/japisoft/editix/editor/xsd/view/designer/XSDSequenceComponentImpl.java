package com.japisoft.editix.editor.xsd.view.designer;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Shape;

import javax.swing.ImageIcon;

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
public class XSDSequenceComponentImpl extends XSDSimpleComponentImpl {

	static final ImageIcon SEQUENCE_ICON = new ImageIcon(
			XSDSequenceComponentImpl.class.getResource("sequence.png"));
	
	protected ImageIcon mainImage;
	
	public XSDSequenceComponentImpl() {
		super();
		mainImage = SEQUENCE_ICON;
		paintElementName = false;
	}

	protected void paintSpecialContent( Graphics2D g ) {
		int openWidth = OPEN_ICON.getIconWidth();
		int openHeight = OPEN_ICON.getIconHeight();

		int y = 1;
		int x = 1;

		if ( !hasOpenIcon ) {
			openWidth = 0;
			openHeight = 0;
		}

		int w = getWidth() - openWidth - 2;
		int h = getHeight() - 2;

		g.drawImage( mainImage.getImage(), 
				( ( w - mainImage.getIconWidth() ) / 2 ) + 2,
				( ( h - mainImage.getIconHeight() ) / 2 ) + 1, null );
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

		int w = getWidth() - openWidth - 2;
		int h = getHeight() - 2;
		
		return new Polygon(
				new int[] {
						1, 
						w / 4,
						( 3 * w ) / 4,
						w,
						( 3 * w ) / 4,
						w / 4,
						1
				},
				new int[] {
						h / 2,
						0,
						0,
						h / 2,
						h,
						h,
						h/2
				},
				7
				);

	}

	public Dimension getPreferredSize() {
		return new Dimension( 40, 20 );
	}
	
}
