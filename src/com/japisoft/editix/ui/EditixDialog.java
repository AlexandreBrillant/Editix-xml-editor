package com.japisoft.editix.ui;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;

import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;

import com.japisoft.framework.dialog.BasicDialogComponent;
import com.japisoft.framework.dialog.DialogFooter;
import com.japisoft.framework.dialog.DialogHeader;
import com.japisoft.framework.dialog.DialogManager;
import com.japisoft.framework.dialog.actions.DialogActionModel;

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
public class EditixDialog extends BasicDialogComponent {

	protected DialogActionModel actionModel = null;
	private DialogFooter footer = null;

	public EditixDialog( 
			String dialogTitle, 
			String title,
			String comment,
			DialogActionModel model ) {
		super( EditixFrame.THIS, dialogTitle );
		
		this.actionModel = model;
		
		DialogHeader header = DialogManager.getDefaultDialogHeader();
		header.setComment( comment );
		header.setTitle( title );
		header.setIcon( DialogManager.getDefaultDialogIcon() );

		footer = DialogManager.getDefaultDialogFooter();
		if ( actionModel == null )
			actionModel = DialogManager.getDefaultDialogActionModel();
		footer.setModel( actionModel );
		
		footer.setDialogTarget( this );

		JPanel tmp = new JPanel();
		Dimension dim = getDefaultSize();
		if ( dim != null )
			tmp.setPreferredSize( dim );
		
		init( header, tmp, footer );
		pane = tmp;
		tmp.setLayout( new BorderLayout() );

	}

	public EditixDialog( 
			String dialogTitle, 
			String title,
			String comment ) {
		this( dialogTitle, title, comment, null );
	}

	protected Dimension getDefaultSize() { return null; }
	
	public boolean isOk() {
		return ( getLastAction() == DialogManager.OK_ID );
	}
	
	private JPanel pane = null;
	
	public Container getContentPane() {
		if ( pane == null ) {
			return super.getContentPane();
		} else
			return pane;
	}

	JToggleButton fixButton;

	private String getDialogName() {
		String _ = getClass().getName();
		int i = _.lastIndexOf( "." );
		if ( i > -1 )
			return _.substring( i + 1 );
		return _;
	}

	protected Border createContentBorder() {
		return new EmptyBorder( 4, 4, 4, 4 );
	}
	
}
