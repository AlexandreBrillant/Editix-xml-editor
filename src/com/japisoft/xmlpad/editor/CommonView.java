package com.japisoft.xmlpad.editor;

import java.awt.Color;

import javax.swing.text.Element;
import javax.swing.text.PlainView;
import javax.swing.text.Segment;

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
public class CommonView extends PlainView implements XMLViewable {

	protected LineParsing lp;

	protected Segment line;

	public CommonView(Element elem) {
		super(elem);
		line = new Segment();
		lp = new LineParsing();
	}

	protected ViewPainterListener painterListener;
	
	public void setViewPainterListener( ViewPainterListener listener ) {
		this.painterListener = listener;
	}

	protected boolean visibleSpace = false; 
	
	public void setDisplaySpace( boolean visibleSpace ) {
		this.visibleSpace = visibleSpace;
	}
	
	protected Color darker(Color c) {
		return new Color(Math.max((int) (c.getRed() * 0.8), 0), Math.max(
				(int) (c.getGreen() * 0.8), 0), Math.max(
				(int) (c.getBlue() * 0.8), 0));
	}

	public void setSyntaxColor(boolean enabled) {
		if (!enabled)
			lp = null;
	}
	
	public void setDTDMode(boolean enabled) {
		// IGNORED
	}	

}
