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
		System.out.println( getDialogName( dialog ) );
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
		String _ = cl.getName();
		int i = _.lastIndexOf( "." );
		if ( i > -1 )
			return _.substring( i + 1 );
		return _;
	}

	public void actionPerformed( ActionEvent e ) {
	}

}
