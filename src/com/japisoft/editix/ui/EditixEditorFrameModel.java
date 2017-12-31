package com.japisoft.editix.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

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
public class EditixEditorFrameModel extends ArrayList 
		implements ActionListener {

	public static EditixEditorFrameModel ACCESSOR = null;
	private static int ID = 1;

	public static int addEditixEditorFrame( EditixEditorFrame frame ) {
		if ( ACCESSOR == null )
			ACCESSOR = new EditixEditorFrameModel();
		
		if ( ACCESSOR.size() == 0 ) // Reset the frame counter
			ID = 1;
		
		ACCESSOR.add( frame );
		// Update the menu
		JMenu menu = EditixFrame.THIS.getBuilder().getMenu( "extracted" );
		menu.setEnabled( true );
		String title = "Frame " + ID;
		if ( frame.getXMLContainer().getCurrentDocumentLocation() != null )
			title = title + " ( " + frame.getXMLContainer().getCurrentDocumentLocation() + " )";
		frame.getXMLContainer().setProperty( "id", "" + ID );
		JMenuItem item = new JMenuItem( title );
		item.addActionListener( ACCESSOR );
		item.setActionCommand( "" + ID );
		menu.add( item );
		ID++;
		return ( ID - 1 );
	}

	public static void removeEditixEditorFrame( EditixEditorFrame frame ) {
		String id = ( String )frame.getXMLContainer().getProperty( "id" );
		JMenu menu = EditixFrame.THIS.getBuilder().getMenu( "extracted" );
		for ( int i = 0; i < menu.getMenuComponentCount(); i++ ) {
			JMenuItem item = ( JMenuItem )menu.getMenuComponent( i );
			if ( item.getActionCommand().equals( id ) ) {
				item.removeActionListener( ACCESSOR );
				menu.remove( item );
				break;
			}
		}
		menu.setEnabled( menu.getMenuComponentCount() > 0 );
		ACCESSOR.remove( frame );
	}

	public void actionPerformed( ActionEvent e ) {
		String id = e.getActionCommand();
		for ( int i = 0; i < getXMLContainerCount(); i++ ) {
			EditixEditorFrame frame = ( EditixEditorFrame )ACCESSOR.get( i );
			if ( frame.getXMLContainer().getProperty( "id" ).equals( id ) ) {
				active( frame );
				break;
			}
		}
	}
	
	public static int getXMLContainerCount() {
		if ( ACCESSOR == null )
			return 0;
		return ACCESSOR.size();
	}

	public static XMLContainer getXMLContainer( int index ) {
		EditixEditorFrame frame = ( EditixEditorFrame )ACCESSOR.get( index );
		return frame.getXMLContainer();
	}

	public static IXMLPanel getIXMLPanel( int index ) {
		EditixEditorFrame frame = ( EditixEditorFrame )ACCESSOR.get( index );
		return frame.getIXMLPanel();		
	}

	public static void active( int index ) {
		EditixEditorFrame frame = ( EditixEditorFrame )ACCESSOR.get( index );
		active( frame );
	}

	public static void active( EditixEditorFrame frame ) {
		frame.toFront();
		XMLContainer currentContainer = frame.getXMLContainer();
		EditixFrame.THIS.updateCurrentXMLContainer( currentContainer );
	}

	public static XMLContainer getSelectedXMLContainer() {
		for ( int i = 0; i < getXMLContainerCount(); i++ ) {
			EditixEditorFrame frame = ( EditixEditorFrame )ACCESSOR.get( i );
			if ( frame.isActive() )
				return frame.getXMLContainer();
		}
		return null;
	}

}
