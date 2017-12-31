package com.japisoft.xmlpad.look.themes;

import java.awt.Color;

import javax.swing.JFrame;
import javax.swing.UIManager;

import com.japisoft.xmlpad.XMLContainer;

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
public class BlueTheme {

	/** Install this theme */
	public static void install() {
	
		UIManager.put( "xmlpad.editor.dtdElementColor",
				new Color( 100, 200, 100 ) );

		UIManager.put( "xmlpad.editor.dtdAttributeColor",
				new Color( 100, 200, 100 ) );
		
		UIManager.put( "xmlpad.editor.dtdEntityColor",
				new Color( 100, 200, 100 ) );
		
		UIManager.put( "xmlpad.editor.tagBorderLineColor",
				new Color( 100, 100, 200 ) );
		
		UIManager.put( "xmlpad.editor.cdataColor",
				new Color( 150, 100, 200 ) );
		
		UIManager.put( "xmlpad.editor.entityColor",
				new Color( 200, 100, 100 ) );
		
		UIManager.put( "xmlpad.editor.commentColor",
				new Color( 100, 100, 200 ) );
		
		UIManager.put( "xmlpad.editor.declarationColor",
				new Color( 150, 150, 150 ) );
		
		UIManager.put( "xmlpad.editor.docTypeColor",
				new Color( 150, 150, 180 ) );
		
		UIManager.put( "xmlpad.editor.literalColor",
				new Color( 40, 40, 40 ) );
		
		UIManager.put( "xmlpad.editor.tagColor",
				new Color( 50, 50, 200 ) );
		
		UIManager.put( "xmlpad.editor.invalidColor",
				new Color( 250, 100, 100 ) );
		
		UIManager.put( "xmlpad.editor.textColor",
				new Color( 10, 10, 150 ) );
		
		UIManager.put( "xmlpad.editor.attributeColor",
				new Color( 50, 100, 250 ) );
		
		UIManager.put( "xmlpad.editor.attributeSeparatorColor",
				new Color( 50, 150, 250 ) );

		UIManager.put( "xmlpad.editor.selectionHighlightColor",
				new Color( 50, 200, 100 ) );
		
		UIManager.put( "xmlpad.editor.backgroundColor",
				new Color( 230, 240, 250 ) );

		/////////// Tree part

		//UIManager.put( "xmlpad.tree.font",
		//		new Font( null, 0, 11 ) );
		
		//UIManager.put( "xmlpad.tree.elementIcon",
		//		new ImageIcon(
		//				ClassLoader.getSystemClassLoader().getResource( "demo/images/element_selection.png" ) ) );

		//UIManager.put( "xmlpad.tree.textIcon",
		//		new ImageIcon( 
		//				ClassLoader.getSystemClassLoader().getResource( "demo/images/element_selection.png" ) )	
		//		);
		
		UIManager.put( "xmlpad.tree.selectionColor",
				new Color( 100, 100, 220 ) );
		
		// Element table view
		
		//UIManager.put( "xmlpad.tableElementView.font",
		//		new Font( null, 0, 11 ) );
		
		UIManager.put( "xmlpad.tableElementView.prefixNameColor",
				new Color( 210, 220, 240 ) );

		UIManager.put( "xmlpad.tableElementView.highlightColor",
				new Color( 210, 220, 250 ) );
		
		UIManager.put( "xmlpad.tableElementView.lowlightColor",
				new Color( 200, 210, 240 ) );

		// Helper
		
		UIManager.put( "xmlpad.helper.backgroundColor",
				new Color( 230, 240, 250 ) );
		
		UIManager.put( "xmlpad.helper.foregroundColor",
				new Color( 50, 50, 50 ) );

		UIManager.put( "xmlpad.helper.selectionBackgroundColor",
				new Color( 210, 220, 240 ) );
		
		UIManager.put( "xmlpad.helper.selectionForegroundColor",
				new Color( 90, 90, 90 ) );
		
		//UIManager.put( "xmlpad.helper.icon", 
		//		new ImageIcon(
		//				ClassLoader.getSystemClassLoader().getResource( "demo/images/element_selection.png" ) ) );

	}


}
