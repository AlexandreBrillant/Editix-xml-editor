package com.japisoft.editix.action.template;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.JComboBox;
import javax.swing.JPanel;

import com.japisoft.editix.ui.EditixFrame;
import com.japisoft.framework.dialog.DialogManager;
import com.japisoft.xmlpad.action.toolkit.InsertAction;

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
public class InsertParamAction extends AbstractAction {

	public void actionPerformed( ActionEvent e ) {
		
		JPanel panel = new JPanel(); 
		JComboBox cb = new JComboBox(
				ParamModel.DEFAULT );
		cb.setEditable( false );
		panel.add( cb );

		if ( DialogManager.showDialog(
				EditixFrame.THIS,
				"Template Param",
				"Choose a template param",
				"Choose a template param. It will be used when creating a new document. Go to the preferences for setting the default values",
				null,
				panel,
				new Dimension( 300, 200 )
				 ) == DialogManager.OK_ID ) {
			InsertAction a = new InsertAction( "${" + ( String )cb.getSelectedItem() + "}" );
			a.setXMLContainer( EditixFrame.THIS.getSelectedContainer() );
			try {
				a.notifyAction();
			} finally {
				a.setXMLContainer( null );
			}
		}
	}	

}
