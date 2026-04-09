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

package com.japisoft.framework.dialog.actions;

import java.awt.Container;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;

import javax.swing.Action;
import javax.swing.ImageIcon;

import com.japisoft.framework.dialog.BasicDialogComponent;
import com.japisoft.framework.dialog.DialogComponent;
import com.japisoft.framework.dialog.DialogFooter;
import com.japisoft.framework.dialog.IdDialog;
import com.japisoft.framework.preferences.Preferences;

/**
 * Action for storing / restoring the last dialog location 
 * @author Alexandre Brillant (https://github.com/AlexandreBrillant/Editix-xml-editor)
 * @version 1.0
 * */
public class StoringLocationAction extends AbstractDialogAction implements DialogVisibilityObserver, ToggleActionTransformer {

	public static final int ID = 100;
	public static String preferenceCat = "dialog";
	
	public StoringLocationAction() {
		super( ID, false );
		putValue( Action.SMALL_ICON, new ImageIcon( getClass().getResource( "lock2.png" ) ) );
		putValue( Action.SHORT_DESCRIPTION, "When selecting, it fixes the last dialog location" );
		setSpecial( true );
	}

	public void dialogHidden( DialogComponent dialog, DialogFooter footer ) {
		if ( footer.isDialogActionSelected( ID ) ) {
			Preferences.setRawPreference(
				preferenceCat,
				getDialogName( dialog ),
				dialog.getView().getBounds() );
		}		
	}

	public void dialogShown( DialogComponent dialog, DialogFooter footer ) {
		Rectangle r = 
			Preferences.getPreference(
				preferenceCat,
				getDialogName( dialog ),
				(Rectangle)null );
		if ( r != null )
			dialog.getView().setBounds( r );		
	}

	private String getDialogName( DialogComponent dialog ) {
		if ( dialog instanceof IdDialog )
			return ( ( IdDialog )dialog ).getId();
		Class cl = dialog.getClass();
		String __ = cl.getName();
		int i = __.lastIndexOf( "." );
		if ( i > -1 )
			return __.substring( i + 1 );
		return __;
	}

	public void actionPerformed( ActionEvent e ) {
	}

}

