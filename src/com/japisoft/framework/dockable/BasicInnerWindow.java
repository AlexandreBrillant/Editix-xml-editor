package com.japisoft.framework.dockable;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;

import com.japisoft.framework.dockable.action.ActionModel;
import com.japisoft.framework.dockable.action.BasicActionModel;
import com.japisoft.framework.dockable.action.CommonActionManager;
import com.japisoft.framework.dockable.action.DockableAction;
import com.japisoft.framework.dockable.action.ModelStateListener;
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
public class BasicInnerWindow extends JPanel implements Windowable {

	private InnerWindowProperties properties;
	private boolean maximized = false;

	public BasicInnerWindow( InnerWindowProperties properties ) {
		this.properties = properties;
		if ( properties.isAutoScroll() )
			realPanel = new JScrollPane();
		else
			realPanel = ComponentFactory.getComponentFactory().buildContentPaneForFrame();
		initUI( null );
		if ( realPanel instanceof JScrollPane )
			((JScrollPane)realPanel).setViewportView( properties.getView() );
		else
			realPanel.add( properties.getView() );
		
		dispatchProperties();
		
		setMinimumSize( new Dimension( 10, 10 ) );
	}

    public Dimension getMinimumSize() {
    	Dimension d = null;
    	try { 
    		d = super.getMinimumSize();
    	} catch( NullPointerException e ) {
    		e.printStackTrace();
    	}
    	if ( d == null )
    		d = new Dimension( 0, 0 );
    	return d;
    }

	private void dispatchProperties() {
		setTitle( properties.getTitle() );
		setIcon( properties.getIcon() );
		actions = properties.getActionModel();
	}

	public void requestFocus() {
		tb.focusMode( true );
	}

	Rectangle frameBounds = null; // When extracting the content
	
	private JComponent realPanel;
	private DockableFrameTitleBar tb = null;

	private void initUI( String title ) {
		super.setLayout( new BorderLayout( 0, 0 ) );
		super.add( ( tb = ComponentFactory.getComponentFactory().buildTitleBar( title ) ).getView(), BorderLayout.NORTH );
		super.add( realPanel, BorderLayout.CENTER );
		if ( !( realPanel instanceof JScrollPane ) ) {	// For the autoscroll mode
			realPanel.setLayout( new MonoLayout() );
			//realPanel.setLayout( new BorderLayout() );
		}
	}

	private CustomFocusAdapter adapter = null;
	
	public void addNotify() {
		super.addNotify();
		if ( commonActionsListener == null )
			commonActionsListener = new CustomModelStateListener();
		getActionModel().addModelStateListener( commonActionsListener );
		if ( realPanel.getComponentCount() > 0 ) {
			Component c = realPanel.getComponent( 0 );
			c.addFocusListener( adapter = new CustomFocusAdapter() );
			recursiveAddFocus( c );
		}
				
		tb.getView().addMouseListener( interactionListener = new InteractionMouseListener() );
		tb.getView().addMouseMotionListener( interactionListener );
		if ( !toolbarUpdatedOnce ) {
			notifyCommonActionModelUpdated();
			toolbarUpdatedOnce = true;
		}
	}

	private boolean toolbarUpdatedOnce = false;
	
	public String toString() {
		return "Frame:" + properties.getId();
	}
	
	private void recursiveAddFocus( Component c ) {
		if ( c instanceof Container ) {
			Container c2 = ( Container )c;
			for ( int i = 0; i < c2.getComponentCount(); i++ ) {
				c2.getComponent( i ).addFocusListener( adapter );
				if ( c2.getComponent( i ) instanceof Container )
					recursiveAddFocus( c2.getComponent( i ) );
			}
		}
	}

	private void recursiveRemoveFocus( Component c ) {
		if ( c instanceof Container ) {
			Container c2 = ( Container )c;
			for ( int i = 0; i < c2.getComponentCount(); i++ ) {
				c2.getComponent( i ).removeFocusListener( adapter );
				if ( c2.getComponent( i ) instanceof Container )
					recursiveRemoveFocus( c2.getComponent( i ) );
			}
		}
	}

	public void removeNotify() {
		super.removeNotify();
		getActionModel().removeModelStateListener( commonActionsListener );
		if ( realPanel.getComponentCount() > 0 && adapter != null ) {
			Component c = realPanel.getComponent( 0 );
			c.removeFocusListener( adapter );
			recursiveRemoveFocus( c );
			adapter = null;
		}
		tb.getView().removeMouseListener( interactionListener );
		tb.getView().removeMouseMotionListener( interactionListener );
	}

	public String getTitle() {
		return tb.getTitle();
	}
	
	public String getId() {
		return properties.getId();
	}

	public JComponent getUserView() {
		return properties.getView();
	}

	public JComponent getView() {
		return this;
	}

	public void dispose() {
		properties.dispose();
	}

	public void setTitle( String title ) {
		tb.setTitle( title );
	}

	public Icon getIcon() {
		return tb.getIcon();
	}
	
	public void setIcon(Icon icon) {
		tb.setIcon( icon );
	}
	
	public void setBackground( Color color ) {
		if ( tb != null )
			tb.setBackground( color );
	}
	
	public void setForeground( Color color ) {
		if ( tb != null )
			tb.setForeground( color );
	}
	
	public Rectangle getFrameBounds() {
		return frameBounds;
	}
	
	public void setFrameBounds(Rectangle r) {
		this.frameBounds = r;
	}	
	
	public JComponent getContentPane() {
		return realPanel;	
	}

	public void setContentPane( JComponent component ) {
		add( realPanel = component, BorderLayout.CENTER );
		invalidate();
		validate();
		repaint();
	}

	private boolean fixed = false;
	
	public void setFixed( boolean fixed ) {
		this.fixed = fixed;
	}

	public boolean isFixed() {
		return fixed;
	}

	private boolean resize = true;
	
	public boolean isResizable() {
		return resize;
	}
	public void setResizable(boolean resize) {
		this.resize = resize;
	}	

	public boolean isMaximized() {
		return maximized;
	}

	public void setMaximized( boolean max ) {
		this.maximized = max;
	}

	private ActionModel actions = null;
	
	/** @return a model for user actions. If empty this is filled with common actions from the CommonActionManager */
	public	ActionModel getActionModel() {
		if ( actions == null ) {
			actions = new BasicActionModel();
			CommonActionManager.fillModelWithCommonActions( this, actions );
		}
		for ( int i = 0; i < actions.getActionCount(); i++ ) {

			Action a = actions.getAction( i );
			if ( a instanceof DockableAction ) {
				DockableAction da = ( DockableAction )a;
				da.setDockableContext( this );
			}	
		}
		return actions;
	}

	private void notifyCommonActionModelUpdated() {
		CommonActionManager.fillWindowTitleBar( tb, getActionModel() );
	}

	public JDock getJDock() {
		return ( (JDock.InnerPanel)getParent() ).getJDock();
	}
	
	public void fireDockEvent( String id, int type ) {
		getJDock().fireJDockEvent( id, type );
	}

	private JPopupMenu popup = null;

	// Popup for the window bar action set
	private boolean activatePopup( int x, int y ) {
		if ( !getJDock().isEnabledActionPopup() )
			return false;
		if ( tb.getIcon() != null ) {
			if ( x <= tb.getIcon().getIconWidth() && 
					y <= tb.getIcon().getIconHeight() ) {
				ActionModel model = 
					getJDock().getInnerWindowActionsForId( getId() );
				if ( model.getActionCount() > 0 ) {
					popup = new JPopupMenu();
					for ( int i = model.getActionCount() - 1; i >= 0; i-- ) {
						Action a = model.getAction( i );
						if ( a == null ) {
							if ( popup.getComponentCount() > 0 && i > 0 )
								popup.addSeparator();
							continue;
						}
						if ( a.getValue( Action.NAME ) == null ) {
							if ( a.getValue( Action.SHORT_DESCRIPTION ) == null ) {
								continue;
							}
							JMenuItem item = 
								new JMenuItem();
							item.setAction( a );
							item.setText( ( String )a.getValue( Action.SHORT_DESCRIPTION ) );
							popup.add( item );
						} else {
							popup.add( a );
						}
					}
					popup.show( this, x, y );
					return true;
				}
			}
		}
		return false;
	}

	private void desactivatePopup() {
		popup = null;
	}
	
	private CustomModelStateListener commonActionsListener = null;
	private InteractionMouseListener interactionListener = null;

	class CustomFocusAdapter implements FocusListener {
		
		public void focusGained(FocusEvent e) {
			tb.focusMode( true );
		}

		public void focusLost(FocusEvent e) {
		}
	}
	
	class CustomModelStateListener implements ModelStateListener {
		public void modelModified( ActionModel source ) {
			if ( source == actions )
				notifyCommonActionModelUpdated();
		}
	}

	class InteractionMouseListener implements MouseListener, MouseMotionListener {
		
		public void mouseClicked( MouseEvent e ) {
			if ( getJDock().getInnerLayout().hasMaximizedComponent() )
				return;
		}

		public void mouseDragged( MouseEvent e ) {
			if ( getJDock().getInnerLayout().hasMaximizedComponent() )
				return;
			if ( popup == null )
				getJDock().workingDrag( BasicInnerWindow.this, e );
		}

		public void mouseEntered( MouseEvent e ) {
		}

		public void mouseExited( MouseEvent e ) {
		}

		public void mouseMoved( MouseEvent e ) {
		}

		public void mousePressed( MouseEvent e ) {
			if ( !activatePopup( e.getX(), e.getY() ) ) {
				if ( getJDock().getInnerLayout().hasMaximizedComponent() )
					return;
				getJDock().startDrag( e, BasicInnerWindow.this );
			}
		}

		public void mouseReleased( MouseEvent e ) {
			desactivatePopup();
			if ( getJDock().getInnerLayout().hasMaximizedComponent() )
				return;	
			getJDock().stopDrag();
		}
	}

	//////////////////////////////////////////////////////////

}
