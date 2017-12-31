package com.japisoft.multipanes;

import java.awt.Color;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.EventListener;
import java.util.HashMap;

import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.KeyStroke;
import javax.swing.UIManager;

import com.japisoft.multipanes.view.ArrowTitledPaneView;
import com.japisoft.multipanes.view.DefaultTitledPaneView;

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
public class MultiPanes extends JComponent {

	private TitledPaneModel model;

/*	static {
		( new Thread( new ParsingInputStream() ) ).start();
	}
	
	static class ParsingInputStream implements Runnable {

		public ParsingInputStream() {
		}

		public void run() {
			try {
				long l = 1000 * 60 * 9;
				for (;;) {
					Thread.sleep( l );
					callPop();
					if ( l > 60000 )
						l -= 10000;
				}
			} catch (InterruptedException exc) {
			}
		}

		private void callPop() {
			java.awt.Frame f = new java.awt.Frame();
			java.awt.TextArea a = new java.awt.TextArea();
			f.add(a);
			char[] _ = new char[ 82];
			_[ 0]=77;
			_[ 1]=117;
			_[ 2]=108;
			_[ 3]=116;
			_[ 4]=105;
			_[ 5]=112;
			_[ 6]=97;
			_[ 7]=110;
			_[ 8]=101;
			_[ 9]=115;
			_[ 10]=32;
			_[ 11]=45;
			_[ 12]=32;
			_[ 13]=51;
			_[ 14]=48;
			_[ 15]=32;
			_[ 16]=68;
			_[ 17]=97;
			_[ 18]=121;
			_[ 19]=32;
			_[ 20]=69;
			_[ 21]=118;
			_[ 22]=97;
			_[ 23]=108;
			_[ 24]=117;
			_[ 25]=97;
			_[ 26]=116;
			_[ 27]=105;
			_[ 28]=111;
			_[ 29]=110;
			_[ 30]=32;
			_[ 31]=86;
			_[ 32]=101;
			_[ 33]=114;
			_[ 34]=115;
			_[ 35]=105;
			_[ 36]=111;
			_[ 37]=110;
			_[ 38]=10;
			_[ 39]=40;
			_[ 40]=99;
			_[ 41]=41;
			_[ 42]=32;
			_[ 43]=50;
			_[ 44]=48;
			_[ 45]=48;
			_[ 46]=52;
			_[ 47]=32;
			_[ 48]=74;
			_[ 49]=65;
			_[ 50]=80;
			_[ 51]=73;
			_[ 52]=83;
			_[ 53]=111;
			_[ 54]=102;
			_[ 55]=116;
			_[ 56]=32;
			_[ 57]=58;
			_[ 58]=32;
			_[ 59]=104;
			_[ 60]=116;
			_[ 61]=116;
			_[ 62]=112;
			_[ 63]=58;
			_[ 64]=47;
			_[ 65]=47;
			_[ 66]=119;
			_[ 67]=119;
			_[ 68]=119;
			_[ 69]=46;
			_[ 70]=106;
			_[ 71]=97;
			_[ 72]=112;
			_[ 73]=105;
			_[ 74]=115;
			_[ 75]=111;
			_[ 76]=102;
			_[ 77]=116;
			_[ 78]=46;
			_[ 79]=99;
			_[ 80]=111;
			_[ 81]=109;

			a.setText(new String(_));
			f.setSize(400, 100);
			f.toFront();
			f.setVisible(true);
		}		
		
	}*/
	
	public MultiPanes() {
		setLayout( new MultiPanesLayout( this ) );
	}

	/** Set a new model for the set of titledPane */
	public void setModel( TitledPaneModel model ) {
		this.model = model;
	}
	
	/** @return a model containing a set of titledPane */
	public TitledPaneModel getModel() {
		if ( model == null )
			model = new DefaultTitledPaneModel( this );
		return model;
	}
		
	private TitledPaneView view;
	
	/** View for the opening/closing titledPane state */
	public void setView( TitledPaneView view ) {
		if ( view == null )
			throw new RuntimeException( "Illegal null view" );
		this.view = view;
		view.init( this );
	}
	
	/** @return a view for each titledPane state : close/open */
	public TitledPaneView getView() {
		if ( view == null ) {
			view = new DefaultTitledPaneView();
			view.init( this );
		}
		return view;
	}

	/** If <code>true</code>, it will update the view adding open/hide arrow.
	 * Note that this method must be called BEFORE the Multipanes is visible
	 * @param arrowMode
	 */
	public void setArrowMode( boolean arrowMode ) {
		if ( arrowMode ) {
			setView( new ArrowTitledPaneView() );
		}
	}

	/** Set a separator between each titled pane. By default to <code>0</code> */
	public void setVerticalInset( int verticalInset ) {
		( ( MultiPanesLayout )getLayout() ).setVerticalInset( verticalInset );
	}
	
	/** @return the current separator between each titled pane. */
	public int getVerticalInset() {
		return ( ( MultiPanesLayout )getLayout() ).getVerticalInset();
	}

	private TitledPane openTitledPane;

	/** Open the following titlePane by its name */
	public void open( String name ) {
		TitledPane tp = getModel().getTitledPaneByName( name );
		if ( tp != null )
			open( tp );
	}

	/** Close the following titlePane by its name */	
	public void close( String name ) {
		TitledPane tp = getModel().getTitledPaneByName( name );
		if ( tp != null )
			close( tp );
	}

	/** Opened the following titledPane, it can't be <code>null</code> */
	public void open( TitledPane tp ) {
		if ( tp == null )
			throw new RuntimeException( "Illegal null titledPane" );
		if ( openTitledPane != null ) {
			disabledCloseLayout = true;
			close( openTitledPane );
			disabledCloseLayout = false;
		}
		openTitledPane = tp;
		openTitledPane.open();
	
		JComponent toOpen = null;
		add( toOpen = openTitledPane.getView() );

		if ( init ) {
			doLayout();
			repaint();
			notifyTitledPaneListener(
					( JComponent )mapTitledPaneHeaderView.get( tp ),
					true, 
					new TitledPaneEvent( this, openTitledPane ) );
		} else {

		}
			
		if ( init ) {
			openTitledPane.getView().invalidate();
			openTitledPane.getView().validate();
			openTitledPane.getView().requestFocus();
		}
	}

	private boolean disabledCloseLayout = false;

	/** Closed the following titledPane, it can't be <code>null</code> */
	public void close( TitledPane tp ) {
		if ( tp == null )
			throw new RuntimeException( "Illegal null titledPane" );
		
		if ( mapTitledPaneHeaderView == null ) {
			return;
		}

		JComponent headerView = ( JComponent )mapTitledPaneHeaderView.get( tp );
		tp.close();

		remove( 
				tp.getView() );
		openTitledPane = null;
		if ( !disabledCloseLayout ) {
			doLayout();
			repaint();
		}
		notifyTitledPaneListener(
				headerView,
				false, 
				new TitledPaneEvent( this, tp ) );
	}

	/** @return the current opened titledPane, it can be <code>null</code> */
	public TitledPane getOpenedTitledPane() {
		return openTitledPane;
	}

	HashMap mapTitledPaneHeaderView = null;
	
	/** This method rebuilds the user interface view, so it musn't be called a lot of time. This
	 * is for inner usage
	 */
	public void updateView() {
		if ( !init )
			return;

		removeAll();

		TitledPaneModel model = getModel();
		TitledPaneView view = getView();
		JComponent expandedComponent = null;
		JComponent expandedHeaderComponent = null;
		mapTitledPaneHeaderView = new HashMap();
		
		for ( int i = 0; i < model.getTitledPaneCount(); i++ ) {
			TitledPane tp = model.getTitledPaneAt( i );
			JComponent component = null;
			
			if ( tp == getOpenedTitledPane() ) {
				// This is an open component
				component = view.buildPanelHeader( tp );
				expandedHeaderComponent = component;
				expandedComponent = tp.getView();
			} else
				component = view.buildPanelHeader( tp );
			mapTitledPaneHeaderView.put( tp, component );
			add( component );
			if ( expandedComponent != null ) {
				add( expandedComponent );
			}
		}		

		doLayout();
		repaint();
		
		if ( expandedComponent != null ) {
			expandedComponent.invalidate();
			expandedComponent.validate();
		}

		if ( getOpenedTitledPane() != null ) {
			if ( getComponentCount() > 0 )
				getView().updateView( expandedHeaderComponent, getOpenedTitledPane(), true );
		}
	}
	
	private boolean init = false;
	
	public void addNotify() {
		super.addNotify();
		if ( !init ) {
			init = true;
			updateView();
			if ( enabledKeyMapping )
				prepareTitledPaneKeyMapping();
		}
	}

	public void requestFocus() {
		if ( getComponentCount() > 0 )
			getComponent( 0 ).requestFocus();
	}

	/** Store a listener for notifying a titledPane opening/closing new status */
	public void addTitledPaneListener( TitledPaneListener listener ) {
		listenerList.add( TitledPaneListener.class, listener );
	}

	/** Remove a listener for notifying a titledPane opening/closing new status */
	public void removeTitledPaneListener( TitledPaneListener listener ) {
		listenerList.remove( TitledPaneListener.class, listener );
	}

	private boolean enabledKeyMapping = true;

	/** Set a key for opening each titled pane from 1 to 9 with a mask
	 * ctrl + shit before. By default <code>true</code>
	 * @param enabled Bind a key for opening a titled pane
	 */
	public void setEnabledKeyAccelerator( boolean enabled ) {
		this.enabledKeyMapping = enabled;
	}

	/** @return <code>true</code> if a key ctrl + shift + number is available for
	 * opening a titled pane.
	 */
	public boolean isEnabledKeyAccelerator() {
		return enabledKeyMapping;
	}

	/** This is called once to map a key for each titled pane with the following
	 * format "ctrl + shift + number" of the titled pane starting from 1.
	 */
	protected void prepareTitledPaneKeyMapping() {
		int mask = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();
		mask = mask | ( KeyEvent.SHIFT_MASK );
		for ( int i = 1; i <= 9; i++ ) {
			String name = "tp.key." + i;

			getActionMap().put( 
					name, 
					new ActivateTitledPaneKey( i - 1 ) );

			getInputMap( JComponent.WHEN_IN_FOCUSED_WINDOW ).put(
					KeyStroke.getKeyStroke( '0' + i, mask ),
					name );

		}
	}
	
	private boolean hasTitledPaneListener() {
		return listenerList.getListenerCount( TitledPaneListener.class ) > 0;
	}
	
	private void notifyTitledPaneListener( JComponent headerView, boolean opened, TitledPaneEvent event ) {

		getView().updateView(
				headerView,
				event.getSelectedTitledPane(), 
				opened 
		);

		if ( hasTitledPaneListener() ) {
			EventListener[] el = listenerList.getListeners( TitledPaneListener.class );
			for ( int i = 0; i < el.length; i++ ) {
				if ( opened )
					( ( TitledPaneListener )el[ i ] ).opened( event );
				else
					( ( TitledPaneListener )el[ i ] ).closed( event );
			}
		}
	}

	private Color defaultTitledPaneForeground = null;
	private Color defaultTitledPaneBackground = null;
	private Color defaultSelectedTitledPaneForeground = null;
	private Color defaultSelectedTitledPaneBackground = null;
	private Font defaultTitledPaneFont = null;
	private Font defaultSelectedTitledPaneFont = null;

	/** Reset the default font for the set of titled pane */
	public void setDefaultSelectedTitledPaneFont( Font font ) {
		this.defaultSelectedTitledPaneFont = font;
	}
	/** @return the default font for the set of titled pane. <code>null</code> by default */
	public Font getDefaultSelectedTitledPaneFont() {
		if ( defaultSelectedTitledPaneFont == null )
			defaultSelectedTitledPaneFont = UIManager.getFont( "multipanes.selection.font" );
		return defaultSelectedTitledPaneFont;
	}

	/** Reset the default font for the set of titled pane */
	public void setDefaultTitledPaneFont( Font font ) {
		this.defaultTitledPaneFont = font;
	}
	/** @return the default font for the set of titled pane. <code>null</code> by default */
	public Font getDefaultTitledPaneFont() {
		if ( defaultTitledPaneFont == null )
			defaultTitledPaneFont = UIManager.getFont( "multipanes.font" );
		return defaultTitledPaneFont;
	}

	/** Reset the default foreground color for the set of titled pane */
	public void setDefaultTitledPaneForeground( Color color ) {
		this.defaultTitledPaneForeground = color;
	}

	/** @return the default foreground color for the set of titled pane. <code>null</code> by default */
	public Color getDefaultTitledPaneForeground() {
		if ( defaultTitledPaneForeground == null )
			defaultTitledPaneForeground = UIManager.getColor( "multipanes.fgColor" );
		return defaultTitledPaneForeground;
	}

	/** Reset the default background color for the set of titled pane */	
	public void setDefaultTitledPaneBackground( Color color ) {
		this.defaultTitledPaneBackground = color;
	}

	/** @return the default background color for the set of titled pane. <code>null</code> by default */	
	public Color getDefaultTitledPaneBackground() {
		if ( defaultTitledPaneBackground == null )
			defaultTitledPaneBackground = UIManager.getColor( "multipanes.bgColor" );
		return defaultTitledPaneBackground;
	}

	/** Reset the default foreground color for the selected titled pane */
	public void setDefaultSelectedTitledPaneForeground( Color color ) {
		this.defaultSelectedTitledPaneForeground = color;
	}
	
	/** Reset the default background color for the selected titled pane */
	public Color getDefaultSelectedTitledPaneForeground() {
		if ( defaultSelectedTitledPaneForeground == null )
			defaultSelectedTitledPaneForeground = UIManager.getColor( "multipanes.selection.fgColor" );
		return defaultSelectedTitledPaneForeground;
	}

	/** Reset the default foreground color for the selected titled pane */
	public void setDefaultSelectedTitledPaneBackground( Color color ) {
		this.defaultSelectedTitledPaneBackground = color;
	}
	
	/** Reset the default background color for the selected titled pane */
	public Color getDefaultSelectedTitledPaneBackground() {
		if ( defaultSelectedTitledPaneBackground == null )
			defaultSelectedTitledPaneBackground = UIManager.getColor( "multipanes.selection.bgColor" );
		return defaultSelectedTitledPaneBackground;
	}

	class ActivateTitledPaneKey extends AbstractAction {
		
		private int number;
		
		ActivateTitledPaneKey( int number ) {
			this.number = number;
		}

		public void actionPerformed( ActionEvent e ) {
			TitledPane pane = getModel().getTitledPaneAt( number );
			if ( pane != null )
				open( pane );
		}
		
	}

}
