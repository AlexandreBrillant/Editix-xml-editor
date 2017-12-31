package com.japisoft.editix.ui.xmlpad;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.geom.Rectangle2D;

import com.japisoft.xmlpad.editor.renderer.LineRenderer;

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
public class EditixXPathRenderer implements LineRenderer {

	static EditixXPathRenderer singleton = null;
	
	public static LineRenderer getSharedInstance() {
		if ( singleton == null )
			singleton = new EditixXPathRenderer();
		return singleton;
	}

	private EditixXPathRenderer() {}

	private static Stroke STROKE = new BasicStroke( 1, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 4, new float[] { 4 }, 0 ); 

	private Color bgXPathColor = new Color( Integer.parseInt( "BBFFBB", 16 ) );

	public void renderer(
		Graphics gc,
		Color color,
		int x,
		int y,
		int width,
		int height) {
		
		gc.setColor( bgXPathColor );
		gc.fillRect( x, y, width, height );

		gc.setColor( color );
		
		Graphics2D g2d = ( Graphics2D )gc;
        g2d.setPaint( color );   
        
        g2d.setStroke( STROKE );                  
        g2d.draw( new Rectangle2D.Double( x, y, width, height ) );		

	}

}
