package com.japisoft.editix.action.search;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;

import com.japisoft.framework.application.descriptor.ActionModel;
import com.japisoft.editix.ui.EditixContainerListener;
import com.japisoft.editix.ui.EditixDialog;
import com.japisoft.editix.ui.EditixFrame;
import com.japisoft.findreplace.FindReplacePanel;
import com.japisoft.framework.dialog.BasicDialogComponent;
import com.japisoft.framework.dialog.actions.DialogActionModel;
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
public class FindAction extends AbstractAction implements EditixContainerListener {

	private BasicDialogComponent dialog = null;
	FindReplacePanel panel = null;

	public FindAction() {
		super();
		EditixFrame.addEditixContainerListener( this );
	}
	
	public void actionPerformed(ActionEvent e) {
		XMLContainer container = EditixFrame.THIS.getSelectedContainer();
		if ( container == null )
			return;

		if ( dialog == null ) {
			panel = new FindReplacePanel( container.getEditor(), true );
			
			String param = ( String )getValue( "param" );

			if ( param != null ) {
				panel.setFindValue( param );
			}

			DialogActionModel model = DialogActionModel.getDefaultDialogActionModel();
			
			dialog = new BasicDialogComponent( EditixFrame.THIS, "Find/Replace" );
			dialog.setModal( false );

			dialog.getContentPane().add( panel );
			dialog.setSize( 300, 350 );
			dialog.setVisible( true );
		} else {			
			
			panel.updateTextComponent( container.getEditor(), true );

			String param = ( String )getValue( "param" );

			if ( param != null ) {
				panel.setFindValue( param );
			}
			
			dialog.setVisible( true );
		}
		
		SearchAgainAction action = ( SearchAgainAction )ActionModel.restoreAction( "searchAgain" );
		if ( action != null )
			action.setEnabled( true );
		else
			System.err.println( "Cannot find 'searchAgain' action ??????????????" );
		
	}

	// Freeing inner reference
	public void close( XMLContainer container ) {
		if ( container == null )	// Unknown case
			return;
		if ( panel != null && panel.getCurrentTextComponent() == container.getEditor() ) {
			panel.updateTextComponent( null, false );
			// Disabled searchAgain
			Action searchAgain = ActionModel.restoreAction( "searchAgain" );
			if ( searchAgain != null )
				searchAgain.setEnabled( false );
		}
	}

}
