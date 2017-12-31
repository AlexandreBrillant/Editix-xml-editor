package com.japisoft.editix.action.xml;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.SwingUtilities;

import com.japisoft.editix.ui.EditixFactory;
import com.japisoft.editix.ui.EditixFrame;
import com.japisoft.editix.ui.EditixStatusBar;
import com.japisoft.framework.actions.SynchronizableAction;
import com.japisoft.framework.ui.toolkit.BrowserCaller;
import com.japisoft.p3.Manager;
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
