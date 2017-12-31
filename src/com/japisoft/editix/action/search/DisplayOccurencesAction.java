package com.japisoft.editix.action.search;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;

import com.japisoft.editix.action.panels.PanelAction;
import com.japisoft.editix.ui.EditixFactory;
import com.japisoft.editix.ui.EditixFrame;
import com.japisoft.framework.application.descriptor.ActionModel;
import com.japisoft.framework.ui.toolkit.BrowserCaller;
import com.japisoft.framework.xml.parser.node.FPNode;
import com.japisoft.p3.Manager;
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
public class DisplayOccurencesAction extends AbstractAction {

	public void actionPerformed(ActionEvent e) {
		//£££
		Action a = ActionModel.restoreAction( "criteria" );
		if ( a == null ) {
			System.err.println( "Can't find the action criteria in editix.xml ??" );
		} else {
			
			String param = ( String )getValue( "param" );
			
			if ( param == null) {
				// Try to find the current element
				XMLContainer container = EditixFrame.THIS.getSelectedContainer();
				if ( container == null ) { 
					EditixFactory.buildAndShowErrorDialog( "No document" );
					return;
				}
				FPNode node = container.getCurrentElementNode();
				if ( node == null ) {
					EditixFactory.buildAndShowWarningDialog( "No element found" );
					return;
				}
				else {
					param = "e" + node.getContent();
				}
			}

			a.putValue( "param2", param );
			
			( ( PanelAction )a ).setForceAlwaysShown( true );
			a.actionPerformed( e );
			a.putValue( "param2", null );
			( ( PanelAction )a ).setForceAlwaysShown( false );

		}
		//££
	}

}
 
