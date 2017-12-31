package com.japisoft.xmlpad.action.edit;

import java.awt.Point;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;

import javax.swing.JOptionPane;
import javax.swing.text.BadLocationException;

import com.japisoft.framework.xml.parser.node.FPNode;
import com.japisoft.xmlpad.XMLContainer;
import com.japisoft.xmlpad.action.XMLAction;

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
public class CopyNodeAction extends XMLAction {

	public static final String ID = CopyNodeAction.class.getName();

	protected boolean autoRequestFocus() { return false; }

	protected Point getNodeOffset() {
		FPNode n = ( FPNode )container.getCurrentNode();
		return getNodeOffset( n );
	}

	public static Point getNodeOffset( FPNode n ) {
		if ( n == null )
			return null;
		int start = n.getStartingOffset();
		int stop = n.getStoppingOffset();
		if ( n.isTag() )
			stop++;
		else
			if ( n.isText() )
				stop--;
		return new Point( start, stop );		
	}

	public static boolean copyAction( XMLContainer container, FPNode node ) {
		Point n = getNodeOffset( node );
		if (n == null) {
			JOptionPane.showMessageDialog( container.getView(), "No Node Found", "Error", JOptionPane.ERROR_MESSAGE );
		}

		container.getEditor().requestFocus();

		try {
			String content = container.getDocument().getText( n.x, n.y - n.x + 1 );
			
			Clipboard systemClipboard =
				Toolkit
					.getDefaultToolkit()
					.getSystemClipboard();
			Transferable transferableText =
				new StringSelection( content );
			systemClipboard.setContents(
				transferableText,
				null );
			
		} catch( Exception e ) {
			return INVALID_ACTION;
		}
		
		return VALID_ACTION;
	}
	
	public boolean notifyAction() {
		
		if ( container.getTreeListeners() == null )
			return INVALID_ACTION;
		if ( container.getTree() == null )
			return INVALID_ACTION;
		
		return copyAction( 
			container, 
			container.getCurrentNode() 
		);
		
	}

}
