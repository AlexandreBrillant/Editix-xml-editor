package com.japisoft.xmlpad.look;

import java.awt.*;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.tree.DefaultTreeModel;

import com.japisoft.framework.xml.parser.node.FPNode;
import com.japisoft.xmlpad.XMLContainer;
import com.japisoft.xmlpad.editor.XMLEditor;
import com.japisoft.xmlpad.tree.renderer.*;

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
public class DefaultLook implements Look {

	public DefaultLook() {
		super();
		UIManager.put( "xmlpad.helper.backgroundColor", 
			new Color( 230, 230, 250 ) );
	}

	/** Change the graphics attributes of this realtime tree */
	public void install(XMLContainer container, JTree tree) {
		//JPF default font
		FastTreeRenderer renderer = new FastTreeRenderer( container );
		tree.setCellRenderer(renderer);
		tree.setModel( new DefaultTreeModel( new FPNode(FPNode.TAG_NODE, "" ) ) );
		
		//JPF
	}

	/** Change the graphics attributes of this editor */
	public void install(XMLContainer container, XMLEditor editor) {

		// NEWS

		// Color

		editor.setColorForEntity(Color.blue);
		editor.setColorForDeclaration(Color.gray.darker());
		editor.setColorForDeclarationStart( Color.gray );
		editor.setColorForDeclarationEnd( Color.gray );
		editor.setColorForDocType( editor.getColorForDeclaration() );
		editor.setColorForDocTypeStart( Color.gray );
		editor.setColorForDocTypeEnd( Color.gray );
		editor.setColorForDocTypeBackground( new Color( 220, 240, 220 ) );
		editor.setColorForLiteral(Color.blue);
		editor.setColorForTag(new Color(50, 150, 50));
		editor.setColorForInvalid(Color.red);
		editor.setColorForText(Color.black);
		editor.setColorForAttribute(new Color(20, 150, 150));
		editor.setColorForTagDelimiter( Color.gray );
		editor.setColorForTagBackground( new Color( 250, 250, 220 ) );
		editor.setColorForDeclarationBackground( new Color( 240, 240, 240 ) );
		editor.setColorForEntityBackground( new Color( 220, 240, 220 ) );
		editor.setColorForCommentBackground( new Color( 220, 220, 240 ) );
		editor.setColorForComment( editor.getColorForDeclaration() );
		editor.setColorForCommentStart( Color.gray );
		editor.setColorForCommentEnd( Color.gray );
		editor.setColorForTagUnderline(Color.gray);
		editor.setColorForAttributeSeparator(Color.black);
		editor.setColorForTagEnd(Color.black );
		editor.setColorForNameSpace(new Color(20, 100, 100));
		editor.setColorForCDATA( Color.black );
		editor.setColorForCDATAStart( Color.gray );
		editor.setColorForCDATAEnd( Color.gray );
		editor.setColorForCDATABackground( Color.gray );
		editor.setColorForTagBorderLine( Color.gray );
		editor.setColorForLineSelection( new Color( 100, 100, 255 ) );
		editor.setColorForLineError( Color.red );
		
		// For DTD

		editor.setColorForDTDElement( Color.CYAN.darker() );
		editor.setColorForDTDAttribute( Color.GREEN.darker() );
		editor.setColorForDTDEntity( Color.ORANGE.darker() );
		editor.setColorForDTDNotation( Color.CYAN.darker() );
		
		// Font

		editor.setCaretColor(Color.black);

		editor.setBackground(Color.white);
		editor.setForeground(Color.black);

		editor.getCaret().setBlinkRate(500);
	}

	public void uninstall(XMLEditor editor) {
	}

}

// DefaultLook ends here
