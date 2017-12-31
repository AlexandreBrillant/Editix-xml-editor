package com.japisoft.framework.dialog;

import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.Window;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JDialog;

import com.japisoft.framework.ApplicationMain;
import com.japisoft.framework.ApplicationModel;
import com.japisoft.framework.dialog.actions.CancelAction;
import com.japisoft.framework.dialog.actions.DialogActionModel;
import com.japisoft.framework.dialog.actions.OKAction;

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
public final class DialogManager {
	private static Class HEADER = null;
	private static Class FOOTER = null;
	private static DialogComponent COMPONENT = null;
	private static Icon ICON = null;

	public static int OK_ID = OKAction.ID;
	public static int CANCEL_ID = CancelAction.ID;
	public static int DEF_ACTION = OK_ID;

//@@
	static {
		ApplicationMain.class.getName();
	}
//@@
	
	
	/** Reset the default dialog icon */
	public static void setDefaultDialogIcon( Icon icon ) {
		ICON = icon;
	}

	public static void setDefaultDialogIcon( String resourcePath ) {
		try {
			ICON = new ImageIcon( ClassLoader.getSystemResource( resourcePath ) );
		} catch( Throwable th ) {
			System.err.println( "Can't find " + resourcePath );
		}
	}

	/** Return the default dialog icon */
	public static Icon getDefaultDialogIcon() {
		if ( ICON == null )
			ICON = new ImageIcon( 
					ClassLoader.getSystemResource( "images/gear.png" ) );
		return ICON;
	}

	private static DialogActionModel DEFAULT_ACTIONMODEL = null;
	
	/** Reset the default model for dialog action */
	public static void setDefaultDialogActionModel( DialogActionModel model ) {
		DEFAULT_ACTIONMODEL = model;
	}

	/** @return the default action model */
	public static DialogActionModel getDefaultDialogActionModel() {
		if ( DEFAULT_ACTIONMODEL == null ) {
			DEFAULT_ACTIONMODEL = DialogActionModel.getDefaultDialogActionModel();
		}
		return DEFAULT_ACTIONMODEL;
	}

	/** 
	 * @param cloneIt 
	 * @return the default action model */
	public static DialogActionModel getDefaultDialogActionModel( boolean cloneIt ) {
		getDefaultDialogActionModel();
		if ( DEFAULT_ACTIONMODEL != null ) {
			return ( DialogActionModel )DEFAULT_ACTIONMODEL.clone();
		}
		return null;
	}
	
	/** @return a new instanceof of the action model cloning the default action model */
	public static DialogActionModel buildNewActionModel() {
		DialogActionModel model = getDefaultDialogActionModel();
		return ( DialogActionModel )model.clone();
	}
	
	/** Update the default dialog header */
	public static void setDefaultDialogHeader( Class headerclass ) {
		if ( !headerclass.isAssignableFrom( DialogHeader.class ) )
			throw new RuntimeException( "Invalid class, must implement DialogHeader" );
		if ( headerclass != null )
			HEADER = headerclass;
	}

	/** @return the default dialog header */
	public static DialogHeader getDefaultDialogHeader() {		
		if ( HEADER == null ) {
			return new BasicDialogHeader();
		}
		try {
			return ( (DialogHeader)HEADER.newInstance() );
		} catch( Throwable th ) {
			th.printStackTrace();
			return new BasicDialogHeader();
		}
	}

	public static DialogFooter getDefaultDialogFooter() {
		return getDefaultDialogFooter( ACTIVE_DEFAULT_BUTTON );
	}
	
	/** @return the default dialog footer with the set of buttons */
	public static DialogFooter getDefaultDialogFooter( boolean defaultButton ) {
		if ( FOOTER == null )
			return new BasicDialogFooter( defaultButton );
		try {
			return ( (DialogFooter)FOOTER.newInstance() );
		} catch( Throwable th ) {
			th.printStackTrace();
			return new BasicDialogFooter( defaultButton );
		}		
	}

	/** Reset the default dialog footer */
	public static void setDefaultDialogFooter( Class footerclass ) {
		if ( !footerclass.isAssignableFrom( DialogFooter.class ) )
			throw new RuntimeException( "Invalid class, must implement DialogFooter" );
		if ( footerclass != null )
			FOOTER = footerclass;
	}

	/** Reset the default action ID */
	public static void setDefaultAction( int id ) {
		DEF_ACTION = id;
	}

	/** @return the default action ID */
	public static int getDefaultAction() {
		return DEF_ACTION;
	}

	/** Reset the default closing window action when pressing ESCAPE, by default Cancel */
	public static void setDefaultClosingAction( int id ) {
		CANCEL_ID = id;
	}

	/** @return the default closing when pressing ESCAPE ID */
	public static int getDefaultClosingAction() {
		return CANCEL_ID;
	}

	private static int showCounter = 0;

	/**
	 * @param owner dialog or frame parent
	 * @param dialogTitle The title of the dialog
	 * @param title The main title
	 * @param comment The user comment
	 * @param icon An icon
	 * @param pane The user UI element
	 * @return an Action Id like OKAction.ID
	 */
	public static int showDialog( 
			Window owner,
			String dialogTitle, 
			String title, 
			String comment, 
			Icon icon, 
			JComponent pane ) {
		return showDialog( owner, dialogTitle, title, comment, icon, pane, null, null );
	}

	/**
	 * @param owner dialog or frame parent
	 * @param dialogTitle The title of the dialog
	 * @param title The main title
	 * @param comment The user comment
	 * @param icon An icon
	 * @param pane The user UI element
	 * @param initialSize the default size for the dialog
	 * @return an Action Id like OKAction.ID
	 */
	public static int showDialog( 
			Window owner, 
			String dialogTitle, 
			String title, 
			String comment, 
			Icon icon, 
			JComponent pane, 
			Dimension initialSize ) {
		return showDialog( owner, dialogTitle, title, comment, icon, pane, null, initialSize );
	}

	
	public static boolean ACTIVE_DEFAULT_BUTTON = true;
	
	/**
	 * @param owner dialog or frame parent
	 * @param dialogTitle The title of the dialog
	 * @param title The main title
	 * @param comment The user comment
	 * @param icon An icon
	 * @param pane The user UI element
	 * @param model The model of the available actions like OK
	 * @param size The initial size
	 * @return an Action Id like OKAction.ID
	 */
	public static int showDialog( 
			Window owner, 
			String dialogTitle, 
			String title, 
			String comment, 
			Icon icon, 
			JComponent pane, 
			DialogActionModel model, 
			Dimension size ) {
		DialogComponent dialog = null;
		dialog = buildDialog( owner == null ? ApplicationModel.MAIN_FRAME : owner, dialogTitle, title, comment, icon, pane, model, ( ACTIVE_DEFAULT_BUTTON && !(  pane instanceof AutoClosableDialog ) ) );
		
		showCounter++;

		if ( dialog.getView() instanceof Window ) {
			( ( Window )dialog.getView() ).pack();
		}
		
		if ( size != null ) {
			dialog.getView().setSize( size );
		}
		if ( AUTO_CENTER ) {
			Dimension d = dialog.getView().getSize();
			Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
			dialog.getView().setLocation(
					( screen.width - d.width ) / 2,
					( screen.height - d.height ) / 2 );
		} else
		if ( DEFAULT_LOCATION != null )
			dialog.getView().setLocation( DEFAULT_LOCATION );
	
		CURRENT_DIALOG = dialog;
		
		if ( pane instanceof AutoClosableDialog ) {
			
			( ( AutoClosableDialog ) pane ).setDialogListener(
					new AutoClosableListener() {
						public void closeDialog() {
							if ( CURRENT_DIALOG != null ) {
								CURRENT_DIALOG.setVisible( false, DialogManager.OK_ID );
							}
						}
					} 
			);

		}

		dialog.setVisible( true );

		CURRENT_DIALOG = null;
		dialog.dispose();
		showCounter--;
		return dialog.getLastAction();
	}

	private static DialogComponent CURRENT_DIALOG = null;
	
	/** Helper for having the current dialog instance shown */
	public static DialogComponent getCurrentDialog() {
		return CURRENT_DIALOG;
	}
	
	private static boolean AUTO_CENTER = false;
	
	/** Enable the auto center location for dialogs. By default to false */
	public static void setAutoCenter( boolean autoCenter ) {
		AUTO_CENTER = autoCenter;
	}

	private static Dimension DEFAULT_SIZE = null;

	/**
	 * @param dim Update for a default size before showing a dialog
	 */
	public static void resetDefaultSize( Dimension dim ) {
		DEFAULT_SIZE = dim;
	}
	
	private static Point DEFAULT_LOCATION = null;

	/**
	 * @param point Update a default location before showing a dialog
	 */
	public static void resetDefaultLocation( Point point ) {
		DEFAULT_LOCATION = point;
	}

	private static DialogComponent buildDialog( 
			Window owner,
			String dialogTitle,
			String title, 
			String comment, 
			Icon icon, 
			JComponent pane, 
			DialogActionModel model,
			boolean defaultButton ) {
		DialogComponent dialog = null;
		
		if ( owner instanceof Dialog ) {
			dialog = new BasicDialogComponent( ( Dialog )owner, dialogTitle );
		} else
		if ( owner instanceof Frame ) {
			dialog = new BasicDialogComponent( ( Frame )owner, dialogTitle );
		} else
			dialog = new BasicDialogComponent( dialogTitle );

		DialogHeader header = getDefaultDialogHeader();

		header.setTitle( title );
		header.setComment( comment );
		header.setIcon( icon == null ? 
							getDefaultDialogIcon() : icon );

		if ( model == null )
			model = getDefaultDialogActionModel();

		DialogFooter footer = getDefaultDialogFooter( defaultButton );

		footer.setDialogTarget( dialog );
		footer.setModel( model );

		dialog.init( header, pane, footer );

		if ( DEFAULT_SIZE != null ) {
			if ( dialog instanceof JDialog ) {
				if ( dialog instanceof BasicDialogComponent ) {
					((BasicDialogComponent)dialog).setSize( DEFAULT_SIZE.width, DEFAULT_SIZE.height, true );
				} else
					((JDialog)dialog).setSize( DEFAULT_SIZE.width, DEFAULT_SIZE.height );
			}
			DEFAULT_SIZE = null;
		}
		
		return dialog;
	}
	
}
