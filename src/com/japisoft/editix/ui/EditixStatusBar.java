package com.japisoft.editix.ui;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JPopupMenu;
import javax.swing.KeyStroke;
import javax.swing.Timer;

import com.japisoft.xmlpad.XMLContainer;
import com.japisoft.xmlpad.error.ErrorManager;


import com.japisoft.framework.ApplicationModel;
import com.japisoft.framework.ApplicationModel.ApplicationModelListener;
import com.japisoft.framework.dockable.Windowable;
import com.japisoft.framework.dockable.action.ActionModel;
import com.japisoft.framework.job.JobManager;
import com.japisoft.framework.job.JobManagerListener;
import com.japisoft.framework.job.ShowHeavyJobAction;
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
public class EditixStatusBar extends JComponent 
		implements 
			JobManagerListener, 
			MouseListener,
			Windowable,
			ApplicationModelListener
			{
	public static EditixStatusBar ACCESSOR = null;
	private boolean capsLock = false;
	private boolean numLock = false;

	public EditixStatusBar() {
		ACCESSOR = this;
		ui();
		
		ApplicationModel.addApplicationModelListener( 
			this 
		);

		try {
			capsLock = Toolkit.getDefaultToolkit().getLockingKeyState( 
				KeyEvent.VK_CAPS_LOCK
			);
			numLock = Toolkit.getDefaultToolkit().getLockingKeyState( 
				KeyEvent.VK_NUM_LOCK
			);
		} catch( Throwable th ) {
			System.out.println( "Can't get CAPS/NUM state" );
		}

		InputMap input = getInputMap( WHEN_IN_FOCUSED_WINDOW );
		ActionMap mActionMap = getActionMap();

		mActionMap.put( 
			"numLockAction", 
			new ActionNumLock() 
		);

		mActionMap.put( 
			"capsLockAction", 
			new ActionCapsLock() 
		);

		input.put(
			KeyStroke.getKeyStroke( KeyEvent.VK_CAPS_LOCK, 0 ), 
			"capsLockAction" );
		input.put(
			KeyStroke.getKeyStroke( KeyEvent.VK_NUM_LOCK, 0 ), 
			"numLockAction" );
			
		JobManager.setJobManagerListener( this );
	}

	private void resetCapsNum() {
		setCapsMode( capsLock );
		setNumMode( numLock = true );
	}

	public void addNotify() {
		super.addNotify();
		resetCapsNum();
		lbError.addMouseListener( this );
	}

	public void removeNotify() {
		super.removeNotify();
		lbError.removeMouseListener( this );
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

	private FastLabel lblWorking; 
	private FastLabel lbXPath;
	private FastLabel lbLocation;
	private FastLabel lbCaps;
	private FastLabel lbNum;
	private FastLabel lbError;

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
			{ 0.02, 0.48, 0.3, 0.1, 0.05, 0.05 },
			{ TableLayout.FILL } } ) );

		add( lblWorking = new FastLabel( true ), "0,0" );
		add( lbXPath = new FastLabel( true ), "1,0" );
		add( lbError = new FastLabel( true, false, true ), "2,0" );
		add( lbLocation = new FastLabel( true, true ), "3,0" );
		add( lbCaps = new FastLabel( true, false ), "4,0" );
		add( lbNum = new FastLabel( true, false ), "5,0" );

		Font f = new Font("dialog", Font.PLAIN, 10 ); 
		setFont( f );
		FontMetrics fm = getFontMetrics( f);
		setPreferredSize( 
			new Dimension( 0, fm.getHeight() + 10 ) );
		
		lblWorking.setAction( ShowHeavyJobAction.getInstance() );
		lbError.setAction( new ErrorAction() );
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

	public void setContentPane(JComponent container) {
	}

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
		String _ = o != null ? o.toString() : null;
		lbXPath.setText( _ );
		o = container.getProperty( "sb.loc" );
		_ = o != null ? o.toString() : null;
		lbLocation.setText( _ );
		o = container.getProperty( "sb.err" );
		_ = o != null ? o.toString() : null;
		lbError.setText( _ );		
	}

	public void clearState() {
		lbXPath.setText( null );
		lbLocation.setText( null );
		lbError.setText( null );
		lbError.setPopupMode( false );
		if ( lastErrors != null )
			lastErrors.removeAllElements();
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
	
	public void setCapsMode( boolean enable ) {
		if ( enable )
			lbCaps.setText( "Caps" );
		else
			lbCaps.setText( null );
	}
	
	public void setNumMode( boolean enable ) {
		if ( enable )
			lbNum.setText( "Num" );
		else
			lbNum.setText( null );		
	}

	private Vector lastErrors = null;
	
	public void setError( Object context,boolean local, String url, String error, int line ) {
		if ( lastErrors == null )
			lastErrors = new Vector();
		if ( error == null )
			lastErrors.removeAllElements();
		
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
		layout.maximized( ( JComponent )lbError );
		doLayout();
		repaint();
		errorMode = true;
	}

	private void restoredLblError() {
		layout.maximized( ( JComponent )null );
		doLayout();
		repaint();
		errorMode = false;
	}

	static ImageIcon ICON = null;

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
		setMessageWithPriority( "Preparing '" + name + "'" );
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

			menu.add( new ErrorPopupItemAction( 
					errorName, 
					line.intValue(),
					local,
					source
					) );
		}
		menu.show( lbError, 10, - ( int )menu.getPreferredSize().getHeight() );
	}

	/////////////////////////////////////////////////////////

	class ActionNumLock extends AbstractAction {
		public ActionNumLock() {
			super();
		}
		public void actionPerformed( ActionEvent e ) {
			setNumMode( numLock = !numLock );
		}
	}

	class ActionCapsLock extends AbstractAction {
		public ActionCapsLock() {
			super();
		}
		public void actionPerformed( ActionEvent e ) {
			setCapsMode( capsLock = !capsLock );
		}
	}

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
