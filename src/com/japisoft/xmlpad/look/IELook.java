package com.japisoft.xmlpad.look;

import java.awt.Color;

import javax.swing.JTree;
import javax.swing.tree.DefaultTreeModel;

import com.japisoft.framework.xml.parser.node.FPNode;
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
public class IELook implements Look {

	public void install( XMLContainer container, JTree tree) {
		FastTreeRenderer renderer = new FastTreeRenderer( container );
		renderer.setElementIcon( null );
		renderer.setTextIcon( null );
		renderer.setTextColor( Color.blue );
		renderer.setTextFont(tree.getFont());
		tree.setCellRenderer(renderer);
		tree.setModel(
				new DefaultTreeModel(
						new FPNode( FPNode.TAG_NODE, "" ) ) );
	}

	/** Change the graphics attributes of this editor */
	public void install( XMLContainer container, XMLEditor editor) {
		// NEWS

		// Color

		editor.setColorForEntity(Color.blue);
		editor.setColorForComment( Color.gray );
		editor.setColorForDeclaration(Color.gray.darker());
		editor.setColorForDocType(Color.blue );
		editor.setColorForLiteral(Color.black);
		editor.setColorForTag(new Color( 222, 38, 18 ) );
		editor.setColorForInvalid(Color.red);
		editor.setColorForText(Color.black);
		editor.setColorForAttribute( new Color( 222, 38, 18 ) );
		editor.setColorForTagDelimiter(Color.green.darker());
		editor.setColorForTagUnderline(Color.gray);
		editor.setColorForAttributeSeparator(Color.blue);
		editor.setColorForTagEnd(Color.black);
		editor.setColorForNameSpace(new Color(222, 38, 18));
		editor.setColorForCDATA(editor.getColorForDeclaration());

		editor.setColorForTagDelimiter( Color.blue );
		editor.setColorForTagEnd( Color.blue );

		editor.setCaretColor(Color.black);

		editor.setBackground(Color.white);
		editor.setForeground(Color.black);

		editor.setColorForTagBorderLine( Color.gray );

		editor.getCaret().setBlinkRate(500);
	}

	public void uninstall(XMLEditor editor) {

	}

}
