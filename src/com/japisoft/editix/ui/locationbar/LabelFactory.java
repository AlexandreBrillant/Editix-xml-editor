package com.japisoft.editix.ui.locationbar;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

import com.japisoft.framework.xml.parser.node.FPNode;

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
public class LabelFactory implements Label {

	private static LabelFactory INSTANCE = null;

	public static LabelFactory getInstance() {
		if ( INSTANCE == null )
			INSTANCE = new LabelFactory();
		return INSTANCE;
	}

	private Map<String,Label> labels = null;

	private LabelFactory() {
		labels = new HashMap<String, Label>();
		labels.put( "default", new DefaultLabel() );
		labels.put( "XSLT", new XSLTLabel() );
		labels.put( "XSLT2", labels.get( "XSLT" ) );
		labels.put( "XSLT3", labels.get( "XSLT" ) );
		labels.put( "XSD", new XSDLabel() );
		labels.put( "RNG", new RNGLabel() );
		labels.put( "XHTML", new XHTMLLabel() );
		labels.put( "FO", new FOLabel() );
		labels.put( "SVG", new SVGLabel() );
	}

	public String getLabel( String type, FPNode node ) {
		Label lbl = getLabel( type );
		String tmp = lbl.getLabel( node );
		if ( tmp == null )
			return getLabel( node );
		return tmp;
	}

	public Color getColor( String type, FPNode node ) {
		Label lbl = getLabel( type );
		Color tmp = lbl.getColor( node );
		if ( tmp == null )
			tmp = getColor( node );
		return tmp;
	}

	public Color getColor(FPNode node) {
		return getColor( "default", node );
	}

	public String getLabel(FPNode node) {
		return getLabel( "default", node );
	}
		
	private Label getLabel( String type ) {
		Label lbl = labels.get( type );
		if ( lbl == null ) {
			lbl = labels.get( "default" );
		}
		return lbl;
	}

	// ---------------------------------------------------
	
	static Color DEF = new Color( 200, 200, 200 );
	
	static class DefaultLabel implements Label {
		public Color getColor(FPNode node) {
			return DEF;
		}
		public String getLabel(FPNode node) {
			return node.getNodeContent();
		}
	}

}
