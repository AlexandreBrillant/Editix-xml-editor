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

package com.japisoft.editix.ui;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.DefaultListModel;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;
import javax.swing.Timer;

import com.japisoft.xmlpad.XMLContainer;
import com.japisoft.xmlpad.error.ErrorManager;
import com.japisoft.editix.ui.windows.EditixFrame;
import com.japisoft.framework.ApplicationModel;
import com.japisoft.framework.ApplicationModel.ApplicationModelListener;
import com.japisoft.framework.dialog.DialogManager;
import com.japisoft.framework.dialog.actions.DialogActionModel;
import com.japisoft.framework.dialog.console.ConsolePanel;
import com.japisoft.framework.dockable.Windowable;
import com.japisoft.framework.dockable.action.ActionModel;
import com.japisoft.framework.job.JobManager;
import com.japisoft.framework.job.JobManagerListener;
import com.japisoft.framework.job.ShowHeavyJobAction;
import com.japisoft.framework.preferences.Preferences;

/**
 * @author Alexandre Brillant (https://github.com/AlexandreBrillant/Editix-xml-editor)
 * @version 1.0 */
public class EditixStatusBar extends JPanel 
		implements 
			JobManagerListener, 
			MouseListener,
			Windowable,
			ApplicationModelListener,
			ActionListener {

	public static EditixStatusBar ACCESSOR = null;
	
	public EditixStatusBar() {
		ACCESSOR = this;
		ui();
		
		ApplicationModel.addApplicationModelListener( 
			this 
		);
			
		JobManager.setJobManagerListener( this );

		openCloseBtn.setFont( new Font( Font.MONOSPACED, Font.PLAIN, 12 ) );
		openCloseBtn.setText( "[+]" );
	}

	public void addNotify() {
		super.addNotify();
		lbError.addMouseListener( this );
		openCloseBtn.addActionListener( this );
	}

	public void removeNotify() {
		super.removeNotify();
		lbError.removeMouseListener( this );
		openCloseBtn.removeActionListener( this );
	}
	
	public void mouseClicked(MouseEvent e) {
	}
	public void mouseEntered(MouseEvent e) {
		lbError.setToolTipText( 
			EditixErrorPanel.getErrorTooltip( 
				0, 
				lbError.getText() 
			) 
		);
	}
	public void mouseExited(MouseEvent e) {
	}
	public void mousePressed(MouseEvent e) {
	}
	public void mouseReleased(MouseEvent e) {
	}

	private boolean mustShowSouthPanels = true;
	
	@Override
	public void actionPerformed(ActionEvent e) {		
		EditixFrame.THIS.setBottomPanels( mustShowSouthPanels );
		mustShowSouthPanels = !mustShowSouthPanels;
		openCloseBtn.setText( !mustShowSouthPanels ? "[-]" : "[+]" );
	}
	
	private FastLabel lblWorking; 
	private FastLabel lbXPath;
	private FastLabel lbLocation;
	private FastLabel lbError;
	private JButton openCloseBtn;

	private TableLayout layout = null;
	
	public void fireApplicationData( String key, Object... values ) {
		if ( "location".equals( key ) ) {
			if ( values != null && values.length == 1 )
				setMessage( ( String )values[ 0 ] );
		} else
		if ( "message".equals( key ) ) {
			setMessageWithPriority( ( String )values[ 0 ] );
		}		
	}

	private void ui() {
		setLayout( layout = new TableLayout( new double[][] {
			{ 0.02, 0.48, 0.4, 0.05, 0.05 },
			{ TableLayout.FILL } } ) );
		
		add( lblWorking = new FastLabel( false ), "0,0" );
		add( lbXPath = new FastLabel( false ), "1,0" );
		add( lbError = new FastLabel( false, false, true ), "2,0" );
		add( lbLocation = new FastLabel( false, true ), "3,0" );
		add( openCloseBtn = new JButton( "" ), "4,0" );
		
		openCloseBtn.setBorderPainted( false );
		
		Font f = new Font("dialog", Font.PLAIN, 10 ); 
		setFont( f );
		FontMetrics fm = getFontMetrics( f);
		setPreferredSize( 
			new Dimension( 0, fm.getHeight() + 10 ) );
		
		lblWorking.setAction( ShowHeavyJobAction.getInstance() );
		lbError.setAction( new ErrorAction() );

		// lbXPath.setIcon( Resource.getImage( "images/copy.png" ) );
		
		lbXPath.setAction(
			new AbstractAction() {				
				@Override
				public void actionPerformed(ActionEvent arg0) {
					StringSelection stringSelection = new StringSelection( lbXPath.getText() );
					Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
					clipboard.setContents(stringSelection, null);
				}
			} );
	}
	
	////////////////////////////////////
	
	// FOR JDOCK
	
	public void fireDockEvent(String id, int type) {
	}

	public ActionModel getActionModel() {
		return null;
	}

	public JComponent getContentPane() {
		return null;
	}

	public Rectangle getFrameBounds() {
		return null;
	}

	public Icon getIcon() {
		return null;
	}

	public String getId() {
		return "statusbar";
	}

	public String getTitle() {
		return null;
	}

	public JComponent getUserView() {
		return null;
	}

	public JComponent getView() {
		return null;
	}

	public boolean isFixed() {
		return true;
	}

	public boolean isMaximized() {
		return false;
	}

	public boolean isResizable() {
		return false;
	}

	public void setContentPane(JComponent container) {}

	public void setFixed(boolean fixed) {}

	public void setFrameBounds(Rectangle r) {}

	public void setIcon(Icon icon) {}

	public void setMaximized(boolean max) {}

	public void setResizable(boolean resize) {}

	public void setTitle(String title) {}

	/////////////////////////////
	
	public void storeState( XMLContainer container ) {
		container.setProperty( "sb.xpath", lbXPath.getText() );
		container.setProperty( "sb.loc", lbLocation.getText() );
		container.setProperty( "sb.err", lbError.getText() );
	}

	public void restoreState( XMLContainer container ) {
		Object o = container.getProperty( "sb.xpath" );
		String __ = o != null ? o.toString() : null;
		lbXPath.setText( __ );
		o = container.getProperty( "sb.loc" );
		__ = o != null ? o.toString() : null;
		lbLocation.setText( __ );
		o = container.getProperty( "sb.err" );
		__ = o != null ? o.toString() : null;
		lbError.setText( __ );
	}

	public void clearState() {
		lbXPath.setText( null );
		lbLocation.setText( null );
		lbError.setText( null );
		lbError.setPopupMode( false );
		if ( lastErrors != null )
			lastErrors.removeAll( lastErrors );
	}

	////////////////////////////////////

	public void setXPathLocation( String location ) {
		lbXPath.setText( location );
	}

	public void setMessage( String message ) {
		lbXPath.setMessage( message );
		paintComponent( getGraphics() );
	}
	
	public void setMessageWithPriority( String message ) {
		setMessage( message );
		if ( errorMode )
			restoredLblError();
	}
	
	public void setLocation( int x, int y ) {
		lbLocation.setText( y + ":" + x );
	}
	
	public void setDelayedMessage( String message ) {
		DelayedMessage dm = new DelayedMessage( message );
		dm.start();
	}
	
	private List lastErrors = null;
	
	public void setError( Object context,boolean local, String url, String error, int line ) {		
		if ( lastErrors == null )
			lastErrors = new ArrayList();
		if ( error == null )
			lastErrors.removeAll( lastErrors );
		
		lbError.setPopupMode( error != null );
		lbError.error = ( error != null );
		
		String tmp = null;
		try {
			if ( ErrorManager.ON_THE_FLY_PARSING_CONTEXT.equals( context ) )
				tmp = " (Press " +
					com.japisoft.framework.ui.toolkit.Toolkit.getKeyStrokeView(
							(KeyStroke)EditixFrame.THIS.getBuilder().getActionById( "parse" ).getValue( 
							Action.ACCELERATOR_KEY ) ) +
							" for more details)";
			else
				tmp = "";
		} catch( RuntimeException e ) {
			ApplicationModel.debug( e );
			tmp = "";
		}

		if ( error != null )
			lbError.setText( error + tmp );
		else
			lbError.setText( null );

		if ( error != null ) {
			if ( !lastErrors.contains( error ) ) {
				lastErrors.add( error );
				lastErrors.add( new Integer( line ) );
				lastErrors.add( new Boolean( local ) );
				lastErrors.add( url );				
				lbError.setErrorNumber( "[" + lastErrors.size() / 4 + "]" );
			}
			maximizedLblError();
		} else
			restoredLblError();
	}

	private boolean errorMode = false;
	
	private void maximizedLblError() {
	}

	private void restoredLblError() {
	}

	static ImageIcon ICON = null;
	static ImageIcon DOWN = null;
	static ImageIcon UP = null;

	public void startKnownJob( Object source, String name, boolean heavy ) {
		if ( heavy ) {
			if ( ICON == null )
				ICON = new ImageIcon( ClassLoader.getSystemResource( "images/gear.png" ) );
			if ( Preferences.getPreference( 
					"system", 
					"taskdialog" + name, 
					true ) ) {				
				ApplicationModel.fireApplicationValue( "information", "Operation '" + name + "' Started" );				
			}

			lbError.error = false;
			lblWorking.setIcon( ICON );
		}
	}
	
	public void stopKnownJob( String name, String error, boolean heavy ) {
		if ( !lbError.error && 
				lbError.text != null ) {
			lbError.error = false;
			lbError.setMessage( "Done" );
		}
		if ( heavy ) {
			lblWorking.setIcon( null );
			if ( error == null ) {
				if ( Preferences.getPreference( "interface", "beepForActionEnd", true ))
					try { Toolkit.getDefaultToolkit().beep(); } catch( Throwable th ) {}
				ApplicationModel.fireApplicationValue( "information", "Operation '" + name + "' Completed" );
			} else
				ApplicationModel.fireApplicationValue( "error", error );
		}
	}

	private void showErrorPopups() {
		JPopupMenu menu = new JPopupMenu();
		for ( int i = 0; i < lastErrors.size(); i += 4 ) {
			String errorName = ( String )lastErrors.get( i );
			Integer line = ( Integer )lastErrors.get( i + 1 );
			boolean local = ( ( Boolean )lastErrors.get( i + 2 ) ).booleanValue();
			String source = ( String )lastErrors.get( i + 3 );

			JMenuItem item = menu.add( new ErrorPopupItemAction( 
					errorName, 
					line.intValue(),
					local,
					source
					) );
			item.setForeground( FastLabel.ERROR_COLOR );
		}
		menu.show( lbError, 10, - ( int )menu.getPreferredSize().getHeight() );
	}

	/////////////////////////////////////////////////////////

	class ErrorAction extends AbstractAction {
		public void actionPerformed( ActionEvent e ) {
			if ( lastErrors != null && 
					lastErrors.size() > 0 ) {
				showErrorPopups();
			}
		}
	}
	
	class ErrorPopupItemAction extends AbstractAction {
		int line;
		String error;
		boolean local;
		String sourceLocation;
		
		public ErrorPopupItemAction( String error, int line, boolean local, String source ) {
			this.error = error;
			this.line = line;
			putValue( Action.NAME, error );
			putValue( Action.SHORT_DESCRIPTION, error );
		}
		
		public void actionPerformed( ActionEvent e ) {
			EditixFrame.THIS.displaySelectedError( local, sourceLocation, error, line );
		}
	}

	class DelayedMessage extends Timer implements ActionListener {
		private String savedMessage;

		public DelayedMessage( String message ) {
			super( 2000, null );
			setRepeats( false );
			savedMessage = lbXPath.getText();
			lbXPath.setMessageMode( true );
			lbXPath.setText( message );
			addActionListener( this );
		}

		public void actionPerformed(ActionEvent e) {
			lbXPath.setMessageMode( false );
			lbXPath.setText( savedMessage );
			removeActionListener( this );
		}
	}
	
}
