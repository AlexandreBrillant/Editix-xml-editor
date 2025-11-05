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

import com.japisoft.editix.ui.EditixContainerListener;
import com.japisoft.editix.ui.EditixFrame;
import com.japisoft.editix.ui.PanelStateListener;
import com.japisoft.editix.ui.PanelStateManager;
import com.japisoft.framework.application.descriptor.ActionModel;
import com.japisoft.framework.preferences.Preferences;
import com.japisoft.xmlpad.XMLContainer;

public class PreviousSelectionAction extends AbstractAction 
		implements PanelStateListener, EditixContainerListener {

	static Stack STACK_PREVIOUS = null;
	private static Action ref;
	
	public PreviousSelectionAction() {
		super();
		ref = this;
		PanelStateManager.addPanelStateListener( this );
	}

	public void actionPerformed(ActionEvent e) {
		XMLContainer container = 
			EditixFrame.THIS.getSelectedContainer();

		if ( STACK_PREVIOUS.size() > 0 ) {
		
			StackItem item = ( StackItem )STACK_PREVIOUS.pop();		
			StackItem nextPath = item;
	
			if ( container != null ) {
				// Skip the current one except for the next
				if ( container.getCurrentDocumentLocation() != null 
						&& container.getCurrentDocumentLocation().equals( item.path ) ) {
					// Try the next
					item = ( StackItem )STACK_PREVIOUS.pop();
				}
			}
			if ( !selectOrOpen( item ) ) {
				// Only if not closed
				NextSelectionAction.addNextPath( nextPath );			
			}
			resetToolTip( STACK_PREVIOUS, this );
			
		}
	}

	static boolean selectOrOpen( StackItem item ) {
		if ( !EditixFrame.THIS.activeXMLContainer( item.path ) ) {
			ActionModel.activeActionById(
				ActionModel.OPEN, 
				null, 
				item.path, 
				item.type, 
				item.encoding );
			return true;
		} else
			return false;
	}

	///////////////////////////////////////////////////////////////////

	public void newPath(String previousPath, String newPath) {
		
		if ( STACK_PREVIOUS != null ) {
			for ( int i = 0; i < STACK_PREVIOUS.size(); i++ ) {

				StackItem item = ( StackItem )STACK_PREVIOUS.get( i );
				if ( item.path.equals( previousPath ) ) {
					item.path = newPath;
				}
				
			}
		}
	}

	static void resetToolTip( Stack stack, Action a ) {		
		StackItem top = null;
		
		// Ignore the current one
		int delta = 0;
		if ( !stack.isEmpty() ) { 
			top = ( StackItem )stack.peek();
			if ( EditixFrame.THIS.getSelectedContainer() != null ) {
				if ( top.path.equals( 
						EditixFrame.THIS.getSelectedContainer().getCurrentDocumentLocation() ) )
					delta = 1;
			}
		}
		StringBuffer sb = new StringBuffer( "<html><body><b>" );
		sb.append( stack.size() - delta ).append( " File" ).append( ( stack.size() - delta ) > 1 ? "s": "" ).append( "</b><br>" );
		for ( int i = stack.size() - 1 - delta; i >= 0; i-- ) {
			StackItem item = ( StackItem )stack.get( i );
			sb.append( item.path ).append( "<br>");
		}
		sb.append( "</body></html>" );
		a.putValue( Action.SHORT_DESCRIPTION, sb.toString() );
	}

	public void setCurrentXMLContainer(XMLContainer container) {
		if ( container != null ) {
			if ( container.getCurrentDocumentLocation() != null )
				addPreviousPath( 
						container.getCurrentDocumentLocation(),
						container.getDocumentInfo().getType(),
						container.getDocumentInfo().getEncoding(),
						container.hasProperty( "save.delegate" )
				);
		} else {
			if ( !ref.isEnabled() && STACK_PREVIOUS != null && STACK_PREVIOUS.size() > 0 )
				ref.setEnabled( true );
		}
	}
	
	public void close(XMLContainer container) {
		
		if ( container == null )
			return;
		
		if ( container.hasProperty( "save.delegate" ) ) {

			StackItem item = new StackItem();
			item.path = container.getCurrentDocumentLocation();

			// Don't maintain it
			
			if ( STACK_PREVIOUS != null ) {
				// We musn't store it
				STACK_PREVIOUS.remove( item );				
				ref.setEnabled( STACK_PREVIOUS.size() > 1 );
				resetToolTip( STACK_PREVIOUS, ref );			
			}
			
			if ( NextSelectionAction.STACK_NEXT != null ) {
				NextSelectionAction.STACK_NEXT.remove( item );				
				NextSelectionAction.ref.setEnabled( 
						STACK_PREVIOUS.size() > 1 );
				resetToolTip( 
						NextSelectionAction.STACK_NEXT, 
						NextSelectionAction.ref );							
			}

		}
	}

	static void checkForSize( Stack stack ) {
		if ( stack.size() > Preferences.getPreference( "interface", "fileHistoryStack", 10 ) ) {
			stack.remove( 0 );
		}
	}

	static void addPreviousPath( String path, String type, String encoding, boolean special ) {
		if ( STACK_PREVIOUS == null ) {
			STACK_PREVIOUS = new Stack();
		}

		StackItem item = new StackItem();
		item.path = path;
		item.type = type;
		item.encoding = encoding;
		item.special = special;
		
		STACK_PREVIOUS.remove(
				item
		);
		STACK_PREVIOUS.push( 
				item		
		);		
		ref.setEnabled( STACK_PREVIOUS.size() > 1 );
		resetToolTip( STACK_PREVIOUS, ref );
		checkForSize( STACK_PREVIOUS );
	}

	static class StackItem {
		
		public String path;
		public String type;
		public String encoding;
		// From XML database
		public boolean special;

		public boolean equals( Object obj ) {
			if ( obj instanceof StackItem ) {
				StackItem si = ( StackItem )obj;
				return si.path.equals( path );
			} else
				return super.equals(obj);
		}
	}

}

