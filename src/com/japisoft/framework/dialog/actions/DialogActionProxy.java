package com.japisoft.framework.dialog.actions;

import java.awt.event.ActionEvent;
import java.beans.PropertyChangeListener;

import com.japisoft.framework.dialog.BasicDialogComponent;
import com.japisoft.framework.dialog.DialogComponent;
import com.japisoft.framework.dialog.DialogManager;

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
public class DialogActionProxy extends AbstractDialogAction {

	public DialogAction delegate;
	private DialogComponent target;
	
	public DialogActionProxy( DialogAction action, DialogComponent target ) {
		super( action.getActionId(), false );
		this.delegate = action;
		this.target = target;
	}

	public void putValue(String key, Object newValue) {
		if ( delegate != null )
			delegate.putValue( key, newValue );
		else
			super.putValue( key, newValue );
	}
	
	public Object getValue(String key) {
		if ( delegate != null )
			return delegate.getValue( key );
		return super.getValue( key );
	}

	public synchronized void addPropertyChangeListener(
			PropertyChangeListener listener) {
		if ( delegate != null )
			delegate.addPropertyChangeListener( listener );
		else
			super.addPropertyChangeListener( listener );
	}
	
	public synchronized void removePropertyChangeListener(
			PropertyChangeListener listener) {
		if ( delegate != null )
			delegate.removePropertyChangeListener( listener );
		else
			super.removePropertyChangeListener( listener );
	}

	public void actionPerformed( ActionEvent e ) {
		if ( delegate != null ) {			
			delegate.actionPerformed( e );
			if ( target instanceof BasicDialogComponent ) {
				( ( BasicDialogComponent )target ).setLastAction( 
						delegate.getActionId() );
			}
			if ( delegate instanceof ClosableAction ) {
				// Check for veto
				if ( delegate instanceof AbstractDialogAction ) {
					if ( ( ( AbstractDialogAction )delegate ).isVetoClosingDialog() )
						return;
				}
				if ( target != null ) {
					target.setVisible( false );
				}
				else {
					if( DialogManager.getCurrentDialog() != null ) {
						
						if ( DialogManager.getCurrentDialog() instanceof BasicDialogComponent ) {
							( ( BasicDialogComponent )DialogManager.getCurrentDialog() ).setLastAction( 
									delegate.getActionId() );							
						}

						DialogManager.getCurrentDialog().setVisible( false );
					}
				}
			}
		} else
			System.err.println( "No delegate found ?" );
	}

	public void setEnabled(boolean b) {
		if ( delegate != null )
			delegate.setEnabled( b );
		else
			super.setEnabled( b );
	}

	public boolean isEnabled() {
		if ( delegate != null )
			return delegate.isEnabled();
		return super.isEnabled();
	}

	public boolean isVetoClosingDialog() {
		if ( delegate != null && 
				( delegate instanceof AbstractDialogAction ) )
			return ( ( AbstractDialogAction )delegate ).isVetoClosingDialog();
		return super.isVetoClosingDialog();		
	}

}
