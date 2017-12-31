package com.japisoft.editix.action.docbook;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import com.japisoft.framework.application.descriptor.ActionModel;
import com.japisoft.editix.action.xsl.XSLTAction;
import com.japisoft.editix.action.xsl.XSLTDialog;
import com.japisoft.editix.ui.EditixFactory;
import com.japisoft.editix.ui.EditixFrame;
import com.japisoft.xmlpad.IXMLPanel;
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
public class DocBookLastAction extends AbstractAction {
	
	public void actionPerformed(ActionEvent e) {
		XMLContainer container = EditixFrame.THIS.getSelectedContainer();
		IXMLPanel panel = EditixFrame.THIS.getSelectedPanel();

		if ( container == null )
			return;
		
		if ( EditixFactory.mustSaveDialog( container ) ) {
			return;
		}	
		
		boolean ok = com.japisoft.xmlpad.action.ActionModel.activeActionByName(
			com.japisoft.xmlpad.action.ActionModel.SAVE_ACTION,
			container,
			container.getEditor() );
		
		if ( !ok )
			return;

		if ( !"true".equals( container.getProperty( "docbook.ok" ) ) ) {
			ActionModel.activeActionById( "docbookt", e );
		} else {
			DocBookAction.applyTransformation( container, true );
		}
	}
	
}
