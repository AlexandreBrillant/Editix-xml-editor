 package com.japisoft.editix.xslt.debug;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Action;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;

import com.japisoft.editix.ui.EditixDialog;
import com.japisoft.editix.ui.EditixFactory;
import com.japisoft.editix.ui.EditixFrame;
import com.japisoft.framework.dialog.DialogManager;
import com.japisoft.framework.dialog.actions.AbstractDialogAction;
import com.japisoft.xmlpad.XMLContainer;
import com.japisoft.xmlpad.bookmark.BookmarkModel;
import com.japisoft.xmlpad.bookmark.BookmarkPosition;

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
public class BreakpointsDialog extends EditixDialog implements ActionListener {
	
	private BookmarkModel model;
	
	private JButton btnRemove;
	private JButton btnSelect;

	public BreakpointsDialog( BookmarkModel model ) {
		super( 
				"Breakpoints List", 
				"Breakpoints", 
				"You can remove each breakpoint or select it",
				DialogManager.buildNewActionModel() );
		this.model = model;
		initUI();

		AbstractDialogAction a = new 
			AbstractDialogAction( 10, false ) {	};
		a.setActionDelegate( this );
		a.putValue( Action.NAME, "Remove" );
		a.putValue( Action.ACTION_COMMAND_KEY, "Remove" );
		actionModel.addDialogAction( a );

		a = new 
		AbstractDialogAction( 11, false ) {	};
		a.setActionDelegate( this );
		a.putValue( Action.NAME, "Select" );
		a.putValue( Action.ACTION_COMMAND_KEY, "Select" );
		actionModel.addDialogAction( a );
	}

	protected Dimension getDefaultSize() {
		return new Dimension( 350, 300 );
	}
	
	public void actionPerformed( ActionEvent e ) {
		String cmd = e.getActionCommand();
		if ( "Remove".equals( cmd ) ) {
			remove();
		} else
		if ( "Select".equals( cmd ) ) {
			select();
		}
	}
	
	private void remove() {
		int index = list.getSelectedIndex();
		if ( index == -1 ) {
			EditixFactory.buildAndShowErrorDialog( "No Selection" );
		} else {
			((DefaultListModel)list.getModel()).remove( index );
			BookmarkPosition position;
			model.removeBookmarkPosition(
					position = model.getBookmarkPositionAt( index ) );
			EditixFrame.THIS.getSelectedContainer().getEditor().getHighlighter().removeHighlight( position.getHighlightFlag() );
			EditixFrame.THIS.getSelectedContainer().getView().repaint();
		}
	}
	
	private void select() {
		int index = list.getSelectedIndex();
		if ( index == -1 ) {
			EditixFactory.buildAndShowErrorDialog( "No Selection" );
		} else {
			BookmarkPosition position = model.getBookmarkPositionAt( index );
			int offset = position.getOffset();
			XMLContainer container = EditixFrame.THIS.getSelectedContainer();
			container.getEditor().setCaretPosition( offset );
		}		
	}
	
	JList list = new JList();
	
	private void initUI() {
		DefaultListModel lm = new DefaultListModel();
		list.setCellRenderer( new SimpleRenderer() );
		for ( int i = 0; i < model.getBookmarkCount(); i++ ) {
			BookmarkPosition position = model.getBookmarkPositionAt( i );
			lm.addElement( "Breakpoint " + ( i + 1 ) );
		}
		list.setModel( lm );
		getContentPane().add( list );
	}

	ImageIcon icon = new ImageIcon( ClassLoader.getSystemResource( "images/breakpoint.png" ) );
	
	class SimpleRenderer extends DefaultListCellRenderer {
		
		public Component getListCellRendererComponent(
		        JList list,
		        Object value,
		        int index,
		        boolean isSelected,
		        boolean cellHasFocus) {
			JLabel lbl = ( JLabel )super.getListCellRendererComponent( list, value, index, isSelected, cellHasFocus );
			lbl.setIcon( icon );
			return lbl;
		}

	}
}
