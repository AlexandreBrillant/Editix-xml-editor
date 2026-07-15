// Editix XML Editor
// https://www.editix.com
// Copyright (c) 2026 Alexandre Brillant
//
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
// AI Training Restriction :
// This source code is provided for human use only.
// Using this code to train, fine-tune, or develop AI models,
// machine learning systems, or similar technologies is
// STRICTLY PROHIBITED. Violations will terminate all rights
// under the applicable license.

package com.japisoft.editix.action.xml;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.SwingUtilities;

import com.japisoft.editix.ui.EditixStatusBar;
import com.japisoft.editix.ui.windows.EditixFrame;
import com.japisoft.framework.actions.SynchronizableAction;
import com.japisoft.xmlpad.XMLContainer;

/**
 * Locked / Unlocked tag
 * @author Alexandre Brillant (https://github.com/AlexandreBrillant/Editix-xml-editor)
 */
public class LockedTagAction extends AbstractAction implements SynchronizableAction {

	public void actionPerformed(ActionEvent e) {
		
		if ( EditixFrame.THIS == null )
			return;
		
		XMLContainer container = EditixFrame.THIS.getSelectedContainer();
		if ( container == null )
			return;
		
		boolean state = container.getDocumentIntegrity().isProtectTag();
		if ( state ) {
			// Disabled it
			container.getDocumentIntegrity().setProtectTag( false );
			EditixStatusBar.ACCESSOR.setXPathLocation( "Tag update enabled" );
		} else {
			// Enabled it
			container.getDocumentIntegrity().setProtectTag( true );
			EditixStatusBar.ACCESSOR.setXPathLocation( "Tag update disabled" );
		}
		// Save it
		container.setProperty( getClass().getName(), "" + !state );
		refreshState( container );		
	}

	public void synchronizeState(Object source) {
		if ( source instanceof XMLContainer ) {
			XMLContainer container = ( XMLContainer )source;
			
			boolean state = refreshState( container );
			
			// Check for state
			if ( state ) {
				if ( !container.getDocumentIntegrity().isProtectTag() ) {
					container.getDocumentIntegrity().setProtectTag( true );
				}
			} else {
				if ( container.getDocumentIntegrity().isProtectTag() ) {
					container.getDocumentIntegrity().setProtectTag( false );
				}
			}
			
			SwingUtilities.invokeLater(
				new DelayedRepaint( container ) );
			
		}
	}	

	class DelayedRepaint implements Runnable {
		private XMLContainer container;
		
		public DelayedRepaint( XMLContainer container ) {
			this.container = container;
		}
		
		public void run() {
			container.getEditor().requestFocus();
			container.getEditor().repaint();
			container = null;
		}
	}
	
	private Icon okIcon = null;

	private boolean refreshState( XMLContainer container ) {
		boolean state = 
			"true".equals( 
					container.getProperty( getClass().getName(), "false" ) );
		if ( !state ) {
			if ( okIcon == null ) {
				okIcon = ( Icon )getValue( Action.SMALL_ICON );
			}
			putValue( Action.SMALL_ICON, okIcon );
		} else {
			if ( okIcon == null ) {
				okIcon = ( Icon )getValue( Action.SMALL_ICON );
			}			
			putValue( Action.SMALL_ICON, getValue( Action.SMALL_ICON + "2" ) );
		}
		
		SwingUtilities.invokeLater(
				new DelayedRepaint( container ) );		
		
		return state;
	}

}
