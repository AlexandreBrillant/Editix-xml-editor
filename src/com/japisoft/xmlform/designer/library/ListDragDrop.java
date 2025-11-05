// Editix XML Editor
// https://www.editix.com
// Copyright (c) 2025 Alexandre Brillant
// 
// For non-commercial usage :
// This program is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
// See the GNU General Public License for more details: https://www.gnu.org/licenses/gpl-3.0
// 
// For commercial use or integration into proprietary software :
// A commercial license is required. Visit https://www.editix.com for details.

package com.japisoft.xmlform.designer.library;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;

import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.TransferHandler;

public class ListDragDrop extends TransferHandler implements
		MouseMotionListener {

	private JList list = null;

	public ListDragDrop( JList list ) {
		this.list = list;
		list.setDragEnabled( true );		
		list.setTransferHandler( this );
		list.addMouseMotionListener( this );

	}

	public void mouseDragged(MouseEvent e) {
		exportAsDrag( list, e, TransferHandler.MOVE );
	}

	public void mouseMoved(MouseEvent e) {}

	private ComponentDescriptor descriptor = null;
	
	public void exportAsDrag(
			JComponent comp, 
			InputEvent e, 
			int action ) {
		descriptor = ( ComponentDescriptor )list.getSelectedValue();
		super.exportAsDrag( comp, e, action );
	}

	@Override
	public boolean canImport(JComponent comp, DataFlavor[] transferFlavors) {
		return false;
	}

	@Override
	protected Transferable createTransferable(JComponent c) {
		if ( descriptor != null )
			return new StringSelection( "new:" + descriptor.getName() );
		else
			return super.createTransferable( c );
	}

	@Override
	public int getSourceActions(JComponent c) {
		return TransferHandler.MOVE;
	}

}

