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
import javax.swing.ImageIcon;

import com.japisoft.editix.ui.EditixFrame;
import com.japisoft.editix.ui.EditixStatusBar;
import com.japisoft.framework.actions.SynchronizableAction;
import com.japisoft.xmlpad.XMLContainer;

/**
 * @author Alexandre Brillant (https://github.com/AlexandreBrillant/Editix-xml-editor)
 * @version 1.0 */
public class DisabledEnabledCompletionAction extends AbstractAction implements SynchronizableAction {
	
	public void actionPerformed( ActionEvent e ) {
		if ( EditixFrame.THIS == null )
			return;
		XMLContainer container = EditixFrame.THIS.getSelectedContainer();
		if ( container == null )
			return;

		boolean state = container.hasSyntaxCompletion();
		if ( state ) {
			// Disabled it
			container.setSyntaxCompletion( false );
			EditixStatusBar.ACCESSOR.setXPathLocation( "Syntax completion disabled" );
		} else {
			// Enabled it
			container.setSyntaxCompletion( true );
			EditixStatusBar.ACCESSOR.setXPathLocation( "Syntax completion enabled" );
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
				if ( !container.hasSyntaxCompletion() ) {
					container.setSyntaxCompletion( true );
				}
			} else {
				if ( container.hasSyntaxCompletion() ) {
					container.setSyntaxCompletion( false );
				}
			}

		}
	}	

	private Icon okIcon = null;

	private boolean refreshState( XMLContainer container ) {
		boolean state = "true".equals( container.getProperty( getClass().getName(), "true" ) );
		if ( state ) {
			if ( okIcon == null ) {
				okIcon = ( Icon )getValue( Action.SMALL_ICON );
			}
			putValue( Action.SMALL_ICON, okIcon );
		} else {
			putValue( Action.SMALL_ICON, getValue( Action.SMALL_ICON + "2" ) );
		}
		return state;
	}

}
