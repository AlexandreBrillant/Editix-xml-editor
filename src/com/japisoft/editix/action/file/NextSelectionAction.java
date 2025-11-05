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

package com.japisoft.editix.action.file;

import java.awt.event.ActionEvent;
import java.util.Stack;

import javax.swing.AbstractAction;
import javax.swing.Action;
import com.japisoft.editix.action.file.PreviousSelectionAction.StackItem;

public class NextSelectionAction extends AbstractAction {

	static Stack STACK_NEXT = null;
	static Action ref = null;
	
	public NextSelectionAction() {
		ref = this;
	}

	public void actionPerformed(ActionEvent e) {
		StackItem item = ( StackItem )STACK_NEXT.pop();
		PreviousSelectionAction.selectOrOpen( item );
		ref.setEnabled( STACK_NEXT.size() > 0 );
		PreviousSelectionAction.resetToolTip( STACK_NEXT, this );
	}

	static void addNextPath( StackItem item ) {
		if ( STACK_NEXT == null ) {
			STACK_NEXT = new Stack();
		}
		STACK_NEXT.remove(
				item
		);
		STACK_NEXT.push( 
				item		
		);
		PreviousSelectionAction.resetToolTip( STACK_NEXT, ref );
		PreviousSelectionAction.checkForSize( STACK_NEXT );		
		ref.setEnabled( STACK_NEXT.size() > 0 );
	}

}

