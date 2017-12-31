package com.japisoft.framework.dockable;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.LayoutManager2;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import com.japisoft.framework.dockable.action.ActionModel;
import com.japisoft.framework.dockable.action.common.ExtractAction;
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
public class JDock {
	/** Minimal component size */
	private static final int DEFAULT_MINSIZE = 10;
	/** Managed icon with and height */
	private static final int ICON_WIDTH_HEIGHT = 16;

	/** Default constructor. The shadow mode is activated */
	public JDock() {
		panel = createInnerView();
	}

	/**
	 * @param shadowMode
	 *            Display a shadow for the inner frames. By default
	 *            <code>true</code>
	 */
	public JDock(boolean shadowMode) {
		this();
		if (!shadowMode)
			setShadowMode(shadowMode);
	}

	/**
	 * This is only a way to customize the default view which must be a
	 * descendant of the InnerPanel
	 */
	protected InnerPanel createInnerView() {
		return new InnerPanel();
	}

	CustomMouseListener listener1 = null;

	ArrayList leftComponent = null;
	ArrayList rightComponent = null;
	ArrayList topComponent = null;
	ArrayList bottomComponent = null;

	private boolean shadowMode = true;

	/**
	 * Here a mode for displaying shadow around the inner windows. By default
	 * <code>true</code>
	 */
	public void setShadowMode(boolean shadow) {
		this.shadowMode = shadow;
		checkLayout();
		((DockableLayout) (panel.getLayout())).borderX = shadow ? 5 : 3;
		((DockableLayout) (panel.getLayout())).borderY = shadow ? 5 : 3;
	}

	/** @return <code>true</code> if a shadow is displayed for the inner frames */
	public boolean isShadowMode() {
		return shadowMode;
	}

	boolean visibleMode = false;

	/**
	 * This is a method for knowing when the JDock view can be added or removed
	 * inside another container. Note that you can use this method for
	 * connecting or unconnecting your listeners.
	 * 
	 * @param visible
	 *            <code>true</code> if the JDock view is available and
	 *            <code>false</code> else
	 */
	protected void setUIReady(boolean added) {
	}

	protected class InnerPanel extends JPanel implements JDockSource {

		public JDock getSource() {
			return JDock.this;
		}

		public void addNotify() {
			super.addNotify();
			if (listener1 == null)
				listener1 = new CustomMouseListener();
			addMouseListener(listener1);
			addMouseMotionListener(listener1);
			visibleMode = true;
			buildMenuWindow();
			setUIReady(true);
			if (statusBarView != null)
				statusBarView.prepare(JDock.this);
		}

		public void removeNotify() {
			super.removeNotify();
			removeMouseListener(listener1);
			removeMouseMotionListener(listener1);
			visibleMode = false;
			setUIReady(false);
		}

		public JDock getJDock() {
			return JDock.this;
		}

		public void paint( Graphics gc ) {
			super.paint( gc );

			try {
				if ( showResizeShadow ) {
					if ( lastPoint != null ) {
						gc.setColor(resizingShadowColor);
						if (horizontal) {
							gc.drawRect(0, lastPoint.y, getWidth(), 1);
						} else {
							gc.drawRect(lastPoint.x, 0, 1, getHeight());
						}
					}
				}
	
				if (showDraggingShadow && draggingFrame != null && draggingBorder != null ) {
					gc.setColor(draggingShadowColor);
					gc.drawRect(draggingBorder.x, draggingBorder.y,
							draggingBorder.width, draggingBorder.height);
	
				}
	
				if (selectedComponent != null) {
					gc.setColor(draggingActiveWindow);
					gc.drawRect(selectedComponent.getX(), selectedComponent.getY(),
							selectedComponent.getWidth(), selectedComponent
									.getHeight());
					gc.drawRect(selectedComponent.getX() + 1, selectedComponent
							.getY() + 1, selectedComponent.getWidth() - 2,
							selectedComponent.getHeight() - 2);
				}
			} catch( Throwable th ) {}
		}

		public void paintComponent(Graphics gc) {
			super.paintComponent(gc);

			if (shadowMode)

				// Draw dark for all the DockableFrame

				for (int i = 0; i < getComponentCount(); i++) {
					Component c = getComponent(i);
					if (c instanceof BasicInnerWindow) {
						gc.setColor(Color.gray);
						gc.drawRect(c.getX() + c.getWidth() + 1, c.getY() + 4,
								1, c.getHeight() - 3);
						gc.drawRect(c.getX() + 3, c.getY() + c.getHeight(), c
								.getWidth() - 2, 1);
					}
				}
		}
	}

	InnerPanel panel = null;

	/**
	 * Reset a layout for initializing each frame. By default a
	 * <code>BorderLayout</code>
	 */
	public void setLayout(LayoutManager2 layout) {
		if (panel.getLayout() instanceof DockableLayout) {
			// Freeing the resource
			( ( DockableLayout )panel.getLayout()).dispose();
		}
		panel.setLayout( new DockableLayout( this, layout ) );
	}
	
	private JMenu menuWindow = null;

	/**
	 * Here a menu for your main frame or dialog containg a set of available
	 * inner windows to be hidden or shown. This menu will be automatically
	 * managed by JDock so it must be empty and not managed by the user.
	 * 
	 * @param menuWindow
	 *            A menu for the set of frame
	 */
	public void setMenuWindow(JMenu menuWindow) {
		this.menuWindow = menuWindow;
	}

	private void addBasicInnerWindowsForMenu(BasicInnerWindow iw) {
		String id = iw.getId();
		String title = iw.getTitle();
		if (title == null)
			title = id;
		HideShowAction action = new HideShowAction(title, id);
		JCheckBoxMenuItem item = new JCheckBoxMenuItem(action);
		item.setSelected(iw.getView().getParent() != null);
		menuWindow.add(item);
	}

	private void buildMenuWindow() {
		if (menuWindow != null && menuWindow.getMenuComponentCount() == 0) {
			for (int i = 0; i < panel.getComponentCount(); i++) {
				Component c = panel.getComponent(i);
				if (c instanceof BasicInnerWindow) {
					BasicInnerWindow iw = (BasicInnerWindow) c;
					addBasicInnerWindowsForMenu(iw);
				}
			}
			if (hasHiddenWindows()) {
				Enumeration enume = hiddenWindows();
				while (enume.hasMoreElements()) {
					Windowable w = (Windowable) enume.nextElement();
					if (w instanceof BasicInnerWindow) {
						addBasicInnerWindowsForMenu((BasicInnerWindow) w);
					}
				}
			}
		}
	}

	/**
	 * Free all inner resources. It should be called for stopping definitly
	 * using the JDock container thus removing any inner pointers.
	 */
	public void dispose() {
		DockManager.removeAllDockedFrame();
		DockManager.LAST_FOCUS = null;

		if (panel.getLayout() instanceof DockableLayout) {
			// Freeing the resource
			((DockableLayout) panel.getLayout()).dispose();
		}
		// Dispose all the frames
		for (int i = 0; i < panel.getComponentCount(); i++) {
			if (panel.getComponent(i) instanceof BasicInnerWindow) {
				BasicInnerWindow fr = (BasicInnerWindow) panel.getComponent(i);
				fr.dispose();
			}
		}
		if (menuWindow != null)
			menuWindow.removeAll();

		menuWindow = null;
		htComponent2ComponentIdable = null;

		if (statusBarView != null)
			statusBarView.dispose();
	}

	boolean isUIReady() {
		return visibleMode;
	}

	private void checkInvalidate() {
		if (visibleMode) {
			panel.invalidate();
			panel.validate();
		}
	}

	/**
	 * Add a component without bar. Such component can't be managed by the user
	 * except for resizing it. This is similar to
	 * <code>addInnerWindow( view, constraint ).</code>
	 * 
	 * @param view
	 *            A component to insert
	 * @param constraint
	 *            A layout constraint. For a non BorderLayout, You must call
	 *            <code>setLayout</code> before.
	 */
	public void add(JComponent view, Object constraint) {
		addInnerWindow(view, constraint);
	}

	/**
	 * Insert a frame inside the JDock container.
	 * 
	 * @param properties.
	 *            All the Inner window properties like the id, the title and the
	 *            final component
	 * @param constraint
	 *            A layout constraint. For a non BorderLayout (the default
	 *            layout), You must call <code>setLayout</code> before.
	 */
	public void addInnerWindow(InnerWindowProperties properties,
			Object constraint) {
		if (properties == null)
			throw new JDockException("properties can't be null");

		if (properties.getIcon() != null
				&& (properties.getIcon().getIconHeight() != ICON_WIDTH_HEIGHT && properties
						.getIcon().getIconWidth() != ICON_WIDTH_HEIGHT)) {
			throw new JDockException("Invalid icon, must be 16x16 !");
		}
		checkLayout();

		if (((DockableLayout) panel.getLayout()).isConstraintsKnown(constraint)) {
			throw new JDockException(
					"You can't reuse the same constraint for different added components");
		}

		panel.add(new BasicInnerWindow(properties), constraint);
		checkForStatusBar();
		checkInvalidate();
	}

	private void checkForStatusBar() {
		if (visibleMode) {
			if (statusBarView != null)
				statusBarView.prepare(JDock.this);
		}
	}

	/**
	 * Add a component without bar. Such component can't be managed by the user
	 * except for resizing it. This is similar to
	 * <code>add( view, constraint ).</code>
	 * 
	 * @param view
	 *            A component to insert
	 * @param constraint
	 *            A layout constraint. For a non BorderLayout, You must call
	 *            <code>setLayout</code> before. */
	public void addInnerWindow(JComponent view, Object constraint) {
		checkLayout();

		if (((DockableLayout) panel.getLayout()).isConstraintsKnown(constraint)) {
			throw new JDockException(
					"You can't reuse the same constraint for different added components");
		}

		panel.add(view, constraint);
		checkForStatusBar();
		checkInvalidate();
	}

	// Add a borderLayout for no <code>setLayout</code> call
	private void checkLayout() {
		if (!(panel.getLayout() instanceof DockableLayout)) {
			setLayout(new BorderLayout());
		}
	}

	JComponent finalView = null;
	BasicStatusBar statusBarView = null;

	/** For inner usage only */
	JComponent getDockingView() {
		return panel;
	}

	/** @return the main container for JDock */
	public JComponent getView() {
		if (finalView == null) {
			if (!statusBarEnabled)
				finalView = panel;
			else {
				finalView = new JPanel();
				finalView.setLayout(new BorderLayout());
				finalView.add(panel, BorderLayout.CENTER);
				finalView.add(statusBarView = new BasicStatusBar(),
						BorderLayout.SOUTH);
			}
		}
		return finalView;
	}

	private boolean statusBarEnabled = false;

	/**
	 * Enable a status bar containg the hidden inner window. By default this is
	 * not enabled. Note that this method must be called before calling the
	 * getView();
	 * 
	 * @param enabled
	 *            <code>true</code> for having a status bar with the hidden
	 *            windows */
	public void setEnabledStatusBar( boolean enabled ) {
		this.statusBarEnabled = enabled;
		if (statusBarView != null) {
			if (enabled && statusBarView.getParent() == null) {
				getView().add(statusBarView, BorderLayout.SOUTH);
			} else if (!enabled) {
				getView().remove(statusBarView);
			}
			getView().invalidate();
			getView().validate();
		}
	}

	/**
	 * @return <code>true</code> if a status bar is available with the inner
	 *         window
	 */
	public boolean isEnabledStatusBar() {
		return statusBarEnabled;
	}

	private boolean enabledActionPopup = true;

	/** Enable a popup menu on the window bar icon. By default <code>true</code> */
	public void setEnabledActionPopup( boolean enabled ) {
		this.enabledActionPopup = enabled;
	}

	/** @return <code>true</code> if a popup menu is activated while clicking on the window icon */
	public boolean isEnabledActionPopup() {
		return enabledActionPopup;
	}
	
	/**
	 * Update the minimal frame size. By default 10 x 10. This minimal value is
	 * used when resizing a frame
	 * 
	 * @param dim
	 *            A minimal frame size
	 */
	public void setMinimalInnerWindowSize(Dimension dim) {
		this.minimalInnerWindowSize = dim;
	}

	private Dimension minimalInnerWindowSize = null;

	/** @return the minimal size for the frames */
	public Dimension getMinimalInnerWindowSize() {
		if (minimalInnerWindowSize == null)
			minimalInnerWindowSize = new Dimension(DEFAULT_MINSIZE, DEFAULT_MINSIZE);
		return minimalInnerWindowSize;
	}

	/**
	 * This action MUST only be used with inner windows having an ID
	 * 
	 * @return the current inner windows state (size and location)
	 */
	public State getState() {
		DefaultState state = new DefaultState();
		state.read(this);
		return state;
	}

	/**
	 * Reset the inner windows state (size and location). This action MUST only
	 * called when you have inner window with an ID.
	 */
	public void setState(State state) {
		if (state instanceof DefaultState) {
			((DefaultState) state).write(this);
			panel.doLayout();
			panel.repaint();
		} else
			throw new JDockException("Unknown state class");
	}

	Windowable getInnerWindowForId(String id) {
		if (id == null)
			throw new JDockException("Invalid id : " + id + " ?");
		for (int i = 0; i < panel.getComponentCount(); i++) {
			if (panel.getComponent(i) instanceof Windowable) {
				Windowable dp = (Windowable) panel.getComponent(i);
				if (id.equals(dp.getId()))
					return dp;
			}
		}

		// Check for name
		for (int i = 0; i < panel.getComponentCount(); i++) {
			Component c = panel.getComponent(i);
			if (c instanceof JComponent) {
				JComponent jc = (JComponent) c;
				if (id.equals(jc.getName())) {

					if (htComponent2ComponentIdable == null)
						htComponent2ComponentIdable = new Hashtable();

					ComponentIdable ci = (ComponentIdable) htComponent2ComponentIdable
							.get(jc);
					if (ci == null) {
						htComponent2ComponentIdable.put(jc,
								ci = new ComponentIdable(jc));
					}

					return ci;
				}
			}
		}

		return null;
	}

	private Hashtable htComponent2ComponentIdable = null;

	/**
	 * This method will return <code>null</code> for a wrong id.
	 * 
	 * @return a title for an inner window with the following id
	 */
	public String getInnerWindowTitleForId(String id) {
		Windowable dp = getInnerWindowForId(id);
		if (dp != null)
			return dp.getTitle();
		return null;
	}

	/**
	 * @param id
	 *            Inner window id
	 * @return <code>true</code> if the bound inner window cannot be moved
	 */
	public boolean isInnerWindowFixed(String id) {
		Windowable dp = getInnerWindowForId(id);
		if (dp == null)
			return false;
		else
			return dp.isFixed();
	}

	/**
	 * It will fixe an inner window, thus it will not able to move to another
	 * inner window space. A <code>JDockException</code> will be thrown if the
	 * id is unknown
	 * 
	 * @param id
	 *            Inner window id
	 * @param fixed
	 *            if <code>true</code> the inner window is static
	 */
	public void setInnerWindowFixed(String id, boolean fixed) {
		Windowable w = getInnerWindowForId(id);
		if (w == null)
			throw new JDockException("Unknown inner window " + id);
		w.setFixed(fixed);
	}

	/**
	 * Enable or disable resizing of a component
	 * inner window space. A <code>JDockException</code> will be thrown if the
	 * id is unknown
	 * 
	 * @param id
	 *            Inner window id
	 * @param resize
	 *            if <code>true</code> the inner window can be resize
	 */	
	public void setInnerWindowResizable(String id, boolean resize) {
		Windowable w = getInnerWindowForId(id);
		if (w == null)
			throw new JDockException("Unknown inner window " + id);
		w.setResizable( resize );
	}

	/** @retur <code>true</code> is the inner window with this id can be resized */
	public boolean isInnerWindowResizable( String id ) {
		Windowable w = getInnerWindowForId(id);
		if (w == null)
			throw new JDockException("Unknown inner window " + id);
		return w.isResizable();
	}
	
	/**
	 * This method will return <code>null</code> for a wrong id.
	 * 
	 * @return the set of actions bar for an inner window with the following id
	 */
	public ActionModel getInnerWindowActionsForId(String id) {
		Windowable dp = getInnerWindowForId(id);
		if (dp != null)
			return dp.getActionModel();
		return null;
	}

	/**
	 * Update a title for an inner window with the following id. A
	 * <code>JDockException</code> will be thrown if the id is wrong
	 * 
	 * @param id
	 *            Id of the inner window
	 * @param title
	 *            New title of the inner window (can be <code>null</code>)
	 */
	public void setInnerWindowTitleForId(String id, String title) {
		Windowable dp = getInnerWindowForId(id);
		if (dp != null)
			dp.setTitle(title);
		else
			throw new JDockException("Unknown inner window " + id);
	}

	/**
	 * Update an icon for an inner window with the following id. A
	 * <code>JDockException</code> will be thrown if the id is wrong
	 * 
	 * @param id
	 *            Id of the inner window
	 * @param icon
	 *            New icon of the inner window (can be <code>null</code>)
	 */
	public void setInnerWindowIconForId(String id, Icon icon) {
		if (icon != null
				&& (icon.getIconHeight() != ICON_WIDTH_HEIGHT && icon.getIconWidth() != ICON_WIDTH_HEIGHT)) {
			throw new JDockException("Invalid icon must be 16x16 !");
		}
		Windowable dp = getInnerWindowForId( id );
		if (dp != null) {
			dp.setIcon( icon );
		}
	}

	/**
	 * @param id
	 *            Inner window id
	 * @return the main icon for this inner window
	 */
	public Icon getInnerWindowIconForId(String id) {
		Windowable dp = getInnerWindowForId(id);
		if (dp != null)
			return dp.getIcon();
		return null;
	}

	/**
	 * Update the background color of the header for this inner window id. Use
	 * <code>null</code> for restoring the default color
	 * 
	 * @param id
	 *            An inner window id
	 * @param color
	 *            An inner window header background
	 */
	public void setInnerWindowBackgroundForId(String id, Color color) {
		Windowable dp = getInnerWindowForId(id);
		if (dp != null) {
			dp.setBackground(color);
		}
	}

	/**
	 * Update the foreground color of the header for this inner window id. Use
	 * <code>null</code> for restoring the default color
	 * 
	 * @param id
	 *            An inner window id
	 * @param color
	 *            An inner window header foreground
	 */
	public void setInnerWindowForegroundForId(String id, Color color) {
		Windowable dp = getInnerWindowForId(id);
		if (dp != null) {
			dp.setForeground(color);
		}
	}

	/**
	 * This method will return <code>null</code> for a wrong id.
	 * 
	 * @return The component of the frame with the following id
	 */
	public JComponent getInnerWindowViewForId(String id) {
		Windowable dp = getInnerWindowForId(id);
		if (dp != null)
			return dp.getUserView();
		return null;
	}

	/**
	 * Remove definitly the frame with the following id. Use the <code>hideInnerWindoww</code>
	 * method for temporary removing
	 * @param id an inner window Id */
	public void removeInnerWindow(String id) {
		if ( id == null )
			throw new JDockException("Invalid null id ");

		Windowable dp = getInnerWindowForId(id);
		if (htHiddenWindow != null) {
			if (htHiddenWindow.containsKey(id))
				dp = (Windowable) htHiddenWindow.get(id);
			htHiddenWindow.remove(id);
		}

		if (dp != null) {

			if (htComponent2ComponentIdable != null
					&& htComponent2ComponentIdable.get(dp.getView()) != null) {
				htComponent2ComponentIdable.remove(dp.getView());
			}

			getInnerLayout().disposeComponent(dp.getView());
			panel.remove(dp.getView());

			fireJDockEvent(dp.getId(), JDockEvent.INNERWINDOW_REMOVED);
		}

		if (menuWindow != null) {
			for (int i = 0; i < menuWindow.getItemCount(); i++) {

				try {
					JCheckBoxMenuItem item = (JCheckBoxMenuItem) menuWindow
							.getItem(i);
					HideShowAction action = (HideShowAction) item.getAction();
					if (action.id.equals(id)) {
						menuWindow.remove(item);
						break;
					}
				} catch (ClassCastException exc) {
					// User override ?
				}
			}
		}
		panel.doLayout();
	}

	///////////////////////////////////////////////////////////

	private Hashtable htHiddenWindow = null;

	boolean hasHiddenWindows() {
		return htHiddenWindow != null && htHiddenWindow.size() > 0;
	}

	Enumeration hiddenWindows() {
		if (htHiddenWindow != null)
			return htHiddenWindow.elements();
		return null;
	}

	/**
	 * Hide the inner window with the following id. A
	 * <code>JDockException</code> will be thrown if the id is unknown.
	 * 
	 * @param id an inner window Id */
	public void hideInnerWindow( String id ) {
		Windowable dp = getInnerWindowForId( id );
		if (dp == null)
			throw new JDockException( "Unknown inner window " + id );
			
		if ( !isInnerWindowHidden( id ) ) {
			getInnerLayout().resetConstraintResizeForHiddenComponent( dp.getView() );
			panel.remove( dp.getView() );
			updateView();
			
			if ( htHiddenWindow == null )
				htHiddenWindow = new Hashtable();
			htHiddenWindow.put( id, dp );
			fireJDockEvent(dp.getId(), JDockEvent.INNERWINDOW_HIDDEN);
		}
	}

	/**
	 * Show the inner window with the following id. A
	 * <code>JDockException</code> will be thrown if the id is unknown.
	 * 
	 * @param id
	 *            an inner window Id */
	public void showInnerWindow(String id) {
		if (htHiddenWindow == null)
			throw new JDockException( "Unknown inner window " + id );

		Windowable dp = ( Windowable )htHiddenWindow.get( id );

		if ( dp == null )
			throw new JDockException( "Unknown inner window " + id );

		if (isInnerWindowHidden(id)) {
			panel.add(dp.getView());
			updateView();
			
			htHiddenWindow.remove(id);

			fireJDockEvent(dp.getId(), JDockEvent.INNERWINDOW_SHOWN);
		}
	}


	private void updateView() {
		panel.invalidate();
		panel.validate();
		panel.repaint();
	}
	
	/** @return true if the innerWindow with the following id is hidden */
	public boolean isInnerWindowHidden(String id) {		
		if ( htHiddenWindow == null )
			return false;
		return htHiddenWindow.containsKey( id );
	}

	/**
	 * Highlight this window with the following id.
	 * 
	 * @param id
	 *            An inner window id
	 */
	public void selectInnerWindow(String id) {
		Windowable iw = getInnerWindowForId(id);
		if (iw != null)
			iw.requestFocus();
	}

	/**
	 * Extract and content of this inner window and put it inside a frame. A
	 * <code>JDockException</code> will be thrown for an unknown inner window.
	 * 
	 * @param id
	 *            An inner window id
	 */
	public void extractInnerWindow(String id) {
		Windowable iw = getInnerWindowForId(id);
		if (iw == null)
			throw new JDockException("Unknown inner window " + id);

		Action a = null;
		// Search the extractAction from the action model

		a = iw.getActionModel().getActionByClass(ExtractAction.class);
		fireJDockEvent(id, JDockEvent.INNERWINDOW_EXTRACTED);

		final JFrame frame = ComponentFactory.getComponentFactory()
				.buildDockedFrame(a, iw);
		frame.setVisible(true);
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				frame.toFront();
			}
		});
	}

	/////////////////////////////////////////////////

	/**
	 * Put this inner window in a "full-window" mode. A
	 * <code>JDockException</code> will be thrown for an unknown id
	 */
	public void maximizeInnerWindow(String id) {
		Windowable dp = getInnerWindowForId(id);
		if (dp == null)
			throw new JDockException("Unknown inner window " + id);

		// Check if another one was maximized
		for (int i = 0; i < panel.getComponentCount(); i++) {

			Component c = panel.getComponent(i);
			if (c instanceof Windowable) {
				Windowable w = (Windowable) c;
				w.setMaximized(false);
			} else {
				if (htComponent2ComponentIdable != null) {
					Windowable w = (Windowable) htComponent2ComponentIdable
							.get(c);
					if (w != null)
						w.setMaximized(false);
				}
			}

		}

		dp.setMaximized(true);
		getInnerLayout().setMaximizedComponent(dp.getView());
		panel.doLayout();
		panel.repaint();
		fireJDockEvent(id, JDockEvent.INNERWINDOW_MAXIMIZED);		
	}

	/**
	 * @param id
	 *            an Inner window id
	 * @return <code>true</code> if the inner window with the following id is
	 *         maximized. If the id is unknown it will return always
	 *         <code>false</code>
	 */
	public boolean isMaximizedInnerWindow(String id) {
		Windowable dp = getInnerWindowForId(id);
		if (dp == null)
			return false;
		return dp.isMaximized();
	}

	/**
	 * Restore this inner window size if it has been maximized previously. A
	 * <code>JDockException</code> will be thrown for an unknown id
	 */
	public void restoreInnerWindow(String id) {
		Windowable dp = getInnerWindowForId(id);
		if (dp == null)
			throw new JDockException("Unknown inner window " + id);
		getInnerLayout().setMaximizedComponent(null);
		panel.doLayout();
		panel.repaint();
		dp.setMaximized(false);
		fireJDockEvent(id, JDockEvent.INNERWINDOW_RESTORED);		
	}

	/**
	 * Maximized or restored this inner window depending on its state. A <code>
	 * JDockException</code>
	 * will be thrown for an unknown id
	 * 
	 * @param id
	 *            An inner window id
	 */
	public void maximizedRestoredInnerWindow(String id) {
		Windowable dp = getInnerWindowForId(id);
		if (dp == null)
			throw new JDockException("Unknown inner window " + id);
		if (dp.isMaximized()) {
			restoreInnerWindow(id);
		} else
			maximizeInnerWindow(id);
	}

	////////////////////////////////////////////////

	private Point lastPoint;

	private boolean horizontal = false;

	void showHorizontalResize(Point p) {
		lastPoint = p;
		horizontal = true;
		if (showResizeShadow)
			getView().repaint();
	}

	void showVerticalResize(Point p) {
		lastPoint = p;
		horizontal = false;
		if (showResizeShadow)
			getView().repaint();
	}

	////////////////////////////////////////////////

	// Interaction from the InnerFrame

	private BasicInnerWindow draggingFrame = null;
	private Rectangle draggingBorder = null;
	private Rectangle matchingBorder = new Rectangle( -1, -1, -1, -1 );
	private Component selectedComponent = null;
	private Component lastSelectedComponent = null;
	private Point initialPoint = null;
	private boolean showDraggingShadow = true;

	/**
	 * if <code>true</code> it will draw a shadow while dragging an inner
	 * window. By default <code>true</code>
	 */
	public void setEnabledDraggingShadow(boolean enabled) {
		this.showDraggingShadow = enabled;
	}

	/**
	 * @return <code>true</code> if a shadow is drawn while dragging an inner
	 *         window. By default <code>true</code>
	 */
	public boolean isEnabledDraggingShasow() {
		return showDraggingShadow;
	}

	void startDrag(MouseEvent e, BasicInnerWindow frame) {
		draggingFrame = frame;
		if (draggingBorder == null)
			draggingBorder = new Rectangle();
		initialPoint = e.getPoint();
	}

	private String getId(Component component) {
		if (component instanceof Windowable)
			return ((Windowable) component).getId();
		else
			return component.getName();
	}

	Windowable componentToWindowable(Component component) {
		if (component instanceof Windowable)
			return (Windowable) component;
		if (htComponent2ComponentIdable != null)
			return (Windowable) htComponent2ComponentIdable.get(component);
		return null;
	}

	void stopDrag() {

		if (selectedComponent != null) {

			boolean ok = true;

			if (selectedComponent instanceof Windowable) {
				Windowable w = (Windowable) selectedComponent;
				ok = !w.isFixed();
			}

			if (ok) {

				fireJDockEvent(getId(draggingFrame),
						JDockEvent.INNERWINDOW_MOVED);
				fireJDockEvent(getId(selectedComponent),
						JDockEvent.INNERWINDOW_MOVED);

				getInnerLayout().swap(draggingFrame, selectedComponent);
				panel.doLayout();
			}
		}

		if (lastCursor != null)
			draggingFrame.setCursor(lastCursor);
		draggingFrame = null;
		initialPoint = null;
		lastCursor = null;
		selectedComponent = null;
		lastSelectedComponent = null;

		if (showDraggingShadow)
			panel.repaint();
	}

	private Cursor lastCursor = null;

	void workingDrag( Windowable source, MouseEvent e ) {
		if ( source.isFixed() )
			return;

		if (lastCursor == null) {
			if ( draggingFrame == null )
				return; // Unknown problem ??
			lastCursor = draggingFrame.getCursor();
			draggingFrame.setCursor( Cursor
					.getPredefinedCursor( Cursor.HAND_CURSOR ) );
		}

		draggingBorder.x = ( draggingFrame.getX() + ( e.getX() - initialPoint.x ) );
		draggingBorder.y = ( e.getY() + draggingFrame.getY() );
		draggingBorder.width = draggingFrame.getWidth();
		draggingBorder.height = draggingFrame.getHeight();

		// Find the nearest component that doesn't match the draggingFrame

		selectedComponent = null;

		for ( int i = 0; i < panel.getComponentCount(); i++ ) {
			Component c = panel.getComponent( i );
			Rectangle r = c.getBounds();
			if ( c != draggingFrame ) {
				if ( r.contains(
						draggingFrame.getX() + e.getX(), draggingFrame
						.getY()
						+ e.getY() ) ) {
					selectedComponent = c;
					break;
				}
			}
		}

		if ( showDraggingShadow )
			panel.repaint();
		else {
			if ( selectedComponent != null
					&& selectedComponent != lastSelectedComponent ) {
				panel.repaint();
				lastSelectedComponent = selectedComponent;
			}
		}
	}

	DockableLayout getInnerLayout() {
		if (panel.getLayout() instanceof DockableLayout)
			return ((DockableLayout) panel.getLayout());
		else {
			setLayout(new BorderLayout());
			return getInnerLayout();
		}
	}

	//////////////////////////////////// UI
	///////////////////////////////////////

	/**
	 * Reset the jdock background
	 * 
	 * @param color
	 *            Background color
	 */
	public void setBackground(Color color) {
		panel.setBackground(color);
	}

	/** @return the jdock background */
	public Color getBackground() {
		return panel.getBackground();
	}

	private Color draggingShadowColor = Color.black;

	/**
	 * Reset the color while dragging an inner window. By default BLACK
	 * 
	 * @param color
	 *            Color for the shadow while dragging an inner window
	 */
	public void setDraggingShadowColor(Color color) {
		this.draggingShadowColor = color;
	}

	/** @return the dragging shadow color. By default BLACK */
	public Color getDraggingShadowColor() {
		return draggingShadowColor;
	}

	private Color draggingActiveWindow = Color.red;

	/**
	 * Reset the color while dragging an inner window under an active inner
	 * window. By default RED
	 * 
	 * @param color
	 *            Color while dragging an inner window for an active inner
	 *            window
	 */
	public void setDraggingActiveWindow(Color color) {
		this.draggingActiveWindow = color;
	}

	/** @return the dragging active inner window color. By default RED */
	public Color getDraggingActiveWindow() {
		return draggingActiveWindow;
	}

	private Color resizingShadowColor = Color.black;

	/**
	 * Reset the shadow color while resizing an inner window. By default BLACK
	 * 
	 * @param color
	 *            A color while resizing an inner window
	 */
	public void setResizingShadowColor(Color color) {
		this.resizingShadowColor = color;
	}

	/**
	 * @return the color of the shadow while resizing an inner window. By
	 *         default BLACK
	 */
	public Color getResizingShadowColor() {
		return resizingShadowColor;
	}

	/**
	 * This method can be used for dynamically resize an inner window
	 * @param id An inner window id
	 * @param addedWidth This is the value <u>added</u> to the width of the window 
	 * @param addedHeight This is the value <u>added</u> to the height of the window
	 * @return <code>true</code> only if the operation is possible
	 */
	public boolean resize( String id, int addedWidth, int addedHeight ) {
		Windowable w = getInnerWindowForId( id );
		if ( w == null )
			throw new JDockException( "Unknown window " + id );
		Dimension d = w.getView().getPreferredSize();
		if ( d == null )
			return false;
		if ( d.width + addedWidth < DEFAULT_MINSIZE )
			return false;
		if ( d.height + addedHeight < DEFAULT_MINSIZE )
			return false;
		w.getView().setPreferredSize( new
				Dimension( d.width + addedWidth, d.height + addedHeight ) );
		getView().doLayout();

		// Check for all component size

		for ( int i = 0; i < getView().getComponentCount(); i++ ) {
			Component c = getView().getComponent( i );
			if ( c.getWidth() >= 0 && c.getHeight() >= 0 )	// For avoiding Stackoverflow
				if ( c.getWidth() < DEFAULT_MINSIZE || 
						c.getHeight() < DEFAULT_MINSIZE ) {
					// Backtrack
					resize( id, -addedWidth, -addedHeight );
					return false;
				}
		}
		return true;
	}


	
	ArrayList listeners = null;

	/**
	 * Add a listener for the inner window state (selected, maximized, moved,
	 * removed...)
	 */
	public void addJDockListener(JDockListener listener) {
		if (listeners == null)
			listeners = new ArrayList();
		listeners.add(listener);
	}

	/** Remove a listener for the inner window state */
	public void removeJDockListener(JDockListener listener) {
		if (listeners != null)
			listeners.remove(listener);
	}

	void fireJDockEvent(String id, int type) {

		if (type == JDockEvent.INNERWINDOW_HIDDEN
				|| type == JDockEvent.INNERWINDOW_SHOWN) {
			if (statusBarView != null)
				statusBarView.prepare(this);
		}

		if (listeners != null && listeners.size() > 0) {
			JDockEvent event = new JDockEvent(this, type, id);
			for (int i = 0; i < listeners.size(); i++) {
				JDockListener listener = (JDockListener) listeners.get(i);
				listener.jdockAction(event);
			}
		}
	}

	///////////////////////////////////////////////////////////////////////////////

	// Actions for the menu

	class HideShowAction extends AbstractAction {

		private String id;

		public HideShowAction(String title, String id) {
			putValue(Action.NAME, title);
			this.id = id;
		}

		public void actionPerformed(ActionEvent e) {
			if (isInnerWindowHidden(id)) {
				showInnerWindow(id);
			} else {
				hideInnerWindow(id);
			}
		}

	}

	// --------------------------------------------------------------
	// MANAGER FOR RESIZING -----------------------------------------
	// --------------------------------------------------------------

	private boolean showResizeShadow = true;

	/**
	 * if <code>true</code> it will draw a shadow while resizing an inner
	 * window. By default <code>true</code>
	 */
	public void setEnabledResizeShadow( boolean enabled ) {
		this.showResizeShadow = enabled;
	}

	/**
	 * @return <code>true</code> if a shadow is drawn while resizing an inner
	 *         window. By default <code>true</code>
	 */
	public boolean isEnabledResizeShasow() {
		return showResizeShadow;
	}
	
	class CustomMouseListener extends MouseAdapter implements
			MouseMotionListener {
		Point initialPoint;

		public void mousePressed(MouseEvent e) {
			super.mousePressed(e);

			if (getInnerLayout().hasMaximizedComponent())
				return;

			initialPoint = e.getPoint();
			lastPoint = null;
			if (showResizeShadow)
				getView().repaint();
		}

		public void mouseReleased(MouseEvent e) {
			super.mouseReleased(e);

			if (getInnerLayout().hasMaximizedComponent())
				return;

			// Complet the leftComponent list
			if (leftComponent != null) {
				Component ref = (Component) leftComponent.get(0);
				for (int i = 0; i < panel.getComponentCount(); i++) {
					Component c = panel.getComponent(i);
					if (!leftComponent.contains(c)
							&& ( c.getX() + c.getWidth()) == (ref.getX() + ref
									.getWidth())) {
						leftComponent.add(c);
					}
				}
			}

			if (rightComponent != null) {
				Component ref = (Component) rightComponent.get(0);
				for (int i = 0; i < panel.getComponentCount(); i++) {
					Component c = panel.getComponent(i);
					if (!rightComponent.contains(c) && (c.getX() == ref.getX())) {
						rightComponent.add(c);
					}
				}
			}

			if (topComponent != null) {
				Component ref = (Component) topComponent.get(0);
				for (int i = 0; i < panel.getComponentCount(); i++) {
					Component c = panel.getComponent(i);
					if (!topComponent.contains(c)
							&& ( c.getY() + c.getHeight() ) == (ref.getY() + ref
									.getHeight()))
						topComponent.add(c);
				}
			}

			if (bottomComponent != null) {
				Component ref = (Component) bottomComponent.get(0);
				for (int i = 0; i < panel.getComponentCount(); i++) {
					Component c = panel.getComponent(i);
					if (!bottomComponent.contains(c)
							&& (c.getY() == ref.getY()))
						bottomComponent.add(c);
				}
			}

			boolean ok = false;

			if (leftComponent != null && rightComponent != null) {
				ok = true;

				int dx = (e.getX() - initialPoint.x);

				dx = rescaleDeltaX(dx, leftComponent, rightComponent);

				getInnerLayout().resize(leftComponent, 0, 0, dx, 0);
				getInnerLayout().resize(rightComponent, dx, 0, -dx, 0);

			} else if (topComponent != null && bottomComponent != null) {
				ok = true;

				int dy = (e.getY() - initialPoint.y);

				dy = rescaleDeltaY(dy, topComponent, bottomComponent);

				getInnerLayout().resize(topComponent, 0, 0, 0, dy);
				getInnerLayout().resize(bottomComponent, 0, dy, 0, -dy);

			}
			
			if (ok) {
				doLayout();
				updateComponent(leftComponent);
				updateComponent(rightComponent);
				updateComponent(bottomComponent);
				updateComponent(topComponent);
			}

			initialPoint = null;
			lastPoint = null;

			if (showResizeShadow)
				getView().repaint();
		}

		private void doLayout() {
			getView().revalidate();
		}

		private int rescaleDeltaY(int dy, ArrayList topComponent,
				ArrayList bottomComponent) {

			int rescale = 0;

			for (int i = 0; i < topComponent.size(); i++) {

				Component c = (Component) topComponent.get(i);
				int height = c.getHeight() + dy - getInnerLayout().borderY;

				if (height < 20) {
					rescale = Math.max(rescale, 20 - height);
				}
			}

			if (rescale > 0)
				return dy + rescale;

			for (int i = 0; i < bottomComponent.size(); i++) {

				Component c = (Component) bottomComponent.get(i);
				int height = c.getHeight() - dy - getInnerLayout().borderY;

				if (height < 20) {
					rescale = Math.max(rescale, 20 - height);
				}
			}

			return dy - rescale;
		}

		private int rescaleDeltaX(int dx, ArrayList leftComponent,
				ArrayList rightComponent) {

			int rescale = 0;

			for (int i = 0; i < leftComponent.size(); i++) {

				Component c = (Component) leftComponent.get(i);
				int width = c.getWidth() + dx - getInnerLayout().borderX;

				if (width < 20) {
					rescale = Math.max(rescale, 20 - width);
				}
			}

			if (rescale > 0)
				return dx + rescale;

			for (int i = 0; i < rightComponent.size(); i++) {

				Component c = (Component) rightComponent.get(i);
				int width = c.getWidth() - dx - getInnerLayout().borderX;

				if (width < 20) {
					rescale = Math.max(rescale, 20 - width);
				}
			}

			return dx - rescale;
		}

		private void updateComponent(ArrayList list) {
			if (list != null)
				for (int i = 0; i < list.size(); i++) {
					Component c = (Component) list.get(i);
					c.invalidate();
					c.validate();
					c.repaint();
				}
		}

		public void mouseDragged(MouseEvent e) {

			if (getInnerLayout().hasMaximizedComponent())
				return;

			if (leftComponent != null && rightComponent != null) {
				showVerticalResize(e.getPoint());
			} else if (topComponent != null && bottomComponent != null) {
				showHorizontalResize(e.getPoint());
			}
		}

		Cursor lastCursor = null;

		public void mouseMoved(MouseEvent e) {

			if (getInnerLayout().hasMaximizedComponent())
				return;

			if (initialPoint != null)
				return;

			int x = e.getX();
			int y = e.getY();

			leftComponent = null;
			rightComponent = null;
			topComponent = null;
			bottomComponent = null;

			// Find two components between ( x, y )
			for (int i = 0; i < panel.getComponentCount(); i++) {
				JComponent comp = (JComponent) panel.getComponent(i);
				Rectangle rect = comp.getBounds();

				if (rect.contains(x + 8, y)) {
					if (rightComponent == null)
						rightComponent = new ArrayList();
					rightComponent.add(comp);
				} else if (rect.contains(x - 8, y)) {
					if (leftComponent == null)
						leftComponent = new ArrayList();
					leftComponent.add(comp);
				}
				if (rect.contains(x, y - 8)) {
					if (topComponent == null)
						topComponent = new ArrayList();
					topComponent.add(comp);
				} else if (rect.contains(x, y + 8)) {
					if (bottomComponent == null)
						bottomComponent = new ArrayList();
					bottomComponent.add(comp);
				}
			}

			if (leftComponent != null && rightComponent != null) {

				if ( checkForNonResizable( leftComponent ) ||
						checkForNonResizable( rightComponent ) ) {
					leftComponent = null;
					rightComponent = null;
					return;
				}
				
				if (lastCursor == null)
					lastCursor = panel.getCursor();
												
				panel.setCursor(Cursor
						.getPredefinedCursor(Cursor.E_RESIZE_CURSOR));

			} else if (topComponent != null && bottomComponent != null) {

				if ( checkForNonResizable( topComponent ) ||
						checkForNonResizable( bottomComponent ) ) {
					topComponent = null;
					bottomComponent = null;
					return;
				}

				if (lastCursor == null)
					lastCursor = panel.getCursor();
				
				panel.setCursor(Cursor
						.getPredefinedCursor(Cursor.N_RESIZE_CURSOR));
			} else {
				if (lastCursor != null)
					panel.setCursor(lastCursor);
			}
		}

		private boolean checkForNonResizable( ArrayList l ) {
			for ( int i = 0; i < l.size(); i++ ) {
				if ( l.get( i ) instanceof Windowable ) {
					if ( !( ( Windowable )l.get( i ) ).isResizable() ) {
						return true;
					}
				}
			}
			return false;
		}
		
		public void mouseExited(MouseEvent e) {
			super.mouseExited(e);

			if (getInnerLayout().hasMaximizedComponent())
				return;

			if (lastCursor != null)
				panel.setCursor(lastCursor);
		}

	}
	
}
