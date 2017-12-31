package com.japisoft.framework.dialog.actions;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import javax.swing.AbstractAction;
import javax.swing.Action;

import com.japisoft.framework.SharedProperties;

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
public abstract class AbstractDialogAction extends AbstractAction implements DialogAction {

	private int id;

	/**
	 * Create a new action with this id
	 * @param id
	 */
	public AbstractDialogAction( int id ) {
		this( id, true );
	}

	/**
	 * @param id a new action id
	 * @param bundleMode decide to reset the label and name with a property file
	 */
	public AbstractDialogAction( int id, boolean bundleMode ) {
		this.id = id;
		if ( bundleMode && SharedProperties.BUNDLE_MODE )
			prepareBundleValues();
		else
			prepareStaticValues();
	}

	/** Called once for setting label, tooltip ... */
	protected void prepareStaticValues() {
	}

	/** Called once for reading properties from bundle */
	protected void prepareBundleValues() {
		try {
			resource = ResourceBundle.getBundle( getBundledName() );
		} catch( MissingResourceException exc ) {
			System.out.println( "Warning : no resource for the action " + getClass().getName() );
		}
		putValue( Action.NAME, getLabel( "NAME", "?" ) );
		putValue( Action.SHORT_DESCRIPTION, getLabel( "SHORT_DESCRIPTION", "?" ) );		
	}

	/** @return the name of the bundle for internationalization */
	protected String getBundledName() {
		return getClass().getName();
	}
	
	private ResourceBundle resource = null;

	/** @return a label from the resource bundle or the default one */	
	protected String getLabel( String name, String def ) {
		if ( resource == null )		
			return def;
		try {
			return resource.getString( name );			
		} catch( MissingResourceException exc ) {
		}
		return def;
	}

	public int getActionId() {
		return id;
	}

	private ActionListener listenerDelegate;
	
	public void setActionDelegate( ActionListener delegate ) {
		this.listenerDelegate = delegate;
	}
	
	public void actionPerformed( ActionEvent e ) {
		if ( listenerDelegate != null )
			listenerDelegate.actionPerformed( e );
	}

	private boolean forFooter = true;

	/** This is a property for not adding the action inside the dialog footer */
	public void setForDialogFooter( boolean forFooter ) {
		this.forFooter = forFooter;
	}

	/** @return <code>true</code> if the action is for the footer. */
	public boolean isForDialogFooter() {
		return forFooter;
	}

	private boolean specialMode = false;
	
	/** @return <code>true</code> if this is a specific action that must appear on the left part */
	public boolean isSpecial() {
		return specialMode;
	}

	/** Update the action status. If <code>true</code>, the action will be on the left part of the footer */
	public void setSpecial( boolean specialMode ) {
		this.specialMode = specialMode;
	}

	private boolean veto = false;
	
	/** Avoid the current dialog to be closed if the action implements the <code>ClosingAction</code> interface */
	public void vetoClosingDialog( boolean veto ) {
		this.veto = veto;
	}

	/** Check if the <code>ClosingAction</code> must be ignored because we don't wish to close
	 * automatically the bound dialog
	 */
	public boolean isVetoClosingDialog() { return veto; }
}
