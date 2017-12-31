package com.japisoft.xmlpad.look;

import java.awt.Color;

import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.tree.TreeCellRenderer;

import com.japisoft.xmlpad.XMLContainer;
import com.japisoft.xmlpad.editor.XMLEditor;
import com.japisoft.xmlpad.tree.renderer.FastTreeRenderer;

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
public class XMLPadLook extends MozillaLook {

	public void install( XMLContainer container, JTree tree ) {
		super.install( container, tree );
		TreeCellRenderer renderer = tree.getCellRenderer();
		if ( renderer instanceof FastTreeRenderer ) {
			FastTreeRenderer ftr  =  (FastTreeRenderer)renderer;
			
			if( UIManager.getLookAndFeel().getName().toLowerCase().contains( "windows" ) )
				ftr.setDashUnderlineMode( false );
			else
				ftr.setDashUnderlineMode( true );

			ftr.setDashUnderlineColor( getTreeSelectionBackgroundColor() );
		}
	}

	protected Color getTreeSelectionBackgroundColor() {
		return Color.GRAY;
	}

	public void install( XMLContainer container, XMLEditor editor ) {
		super.install( container, editor );
	}

}
