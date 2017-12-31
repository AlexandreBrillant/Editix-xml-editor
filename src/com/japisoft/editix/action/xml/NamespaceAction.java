package com.japisoft.editix.action.xml;

import java.awt.Dimension;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import com.japisoft.editix.ui.EditixFactory;
import com.japisoft.editix.ui.EditixFrame;
import com.japisoft.framework.dialog.DialogManager;
import com.japisoft.framework.ui.toolkit.BrowserCaller;
import com.japisoft.framework.xml.parser.node.FPNode;
import com.japisoft.p3.Manager;
import com.japisoft.xmlpad.XMLContainer;
import com.japisoft.xmlpad.editor.XMLPadDocument;


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
public class NamespaceAction extends AbstractAction {

	public void actionPerformed(ActionEvent e) {
		
		XMLContainer container = EditixFrame.THIS.getSelectedContainer();
		if ( container == null )
			return;
		FPNode node = container.getCurrentNode();
		if ( node == null ) {
			EditixFactory.buildAndShowErrorDialog( "No selected node" );
			return;
		}
		
		if ( container.hasErrorMessage() ) {
			EditixFactory.buildAndShowErrorDialog( "Please fix your document before" );
			return;
		}
		
		NamespacePanel panel = new NamespacePanel( node );

		if ( DialogManager.showDialog(
				EditixFrame.THIS,
				"Namespace manager",
				"Namespace manager",
				"Add or remove a namespace definition. The current one is for the namespace of the selected node",
				null,
				panel,
				new Dimension( 400, 400 )) == 
					DialogManager.OK_ID ) {
			panel.updateNode();
			XMLPadDocument doc = container.getXMLDocument();
			String openPart = node.openDeclaration();
			String closePart = node.closeDeclaration();
			if ( node.isAutoClose() )
				closePart = null;
			doc.updateElement( openPart, closePart, node.getStartingOffset(), node.getStoppingOffset() );
		}
	}

}
