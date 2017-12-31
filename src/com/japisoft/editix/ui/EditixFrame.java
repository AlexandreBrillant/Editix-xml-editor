package com.japisoft.editix.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Toolkit;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.ArrayList;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPopupMenu;
import javax.swing.JTabbedPane;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.netbeans.swing.tabcontrol.TabData;
import org.netbeans.swing.tabcontrol.TabbedContainer;
import org.netbeans.swing.tabcontrol.event.TabActionEvent;

import com.japisoft.framework.ApplicationModel;
import com.japisoft.framework.ApplicationModel.ApplicationModelListener;
import com.japisoft.framework.application.descriptor.ActionModel;
import com.japisoft.editix.action.file.OpenAction;
import com.japisoft.editix.document.DocumentModel;
import com.japisoft.editix.main.EditixApplicationModel;
import com.japisoft.editix.project.ProjectManager;
import com.japisoft.editix.toolkit.AddSystemFilesTransferHandler;
import com.japisoft.editix.ui.panels.EditixDocking;
import com.japisoft.editix.ui.xslt.XSLTEditor;
import com.japisoft.framework.application.descriptor.InterfaceBuilder;
import com.japisoft.framework.job.JobManager;
import com.japisoft.framework.preferences.Preferences;
import com.japisoft.p3.Manager;
import com.japisoft.xmlpad.IView;
import com.japisoft.xmlpad.IXMLPanel;
import com.japisoft.xmlpad.XMLContainer;
import com.japisoft.xmlpad.XMLDocumentInfo;
import com.japisoft.xmlpad.helper.model.*;

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
public class EditixFrame extends JFrame 
	implements ChangeListener,
					ActionListener, 
						ApplicationModelListener {
	private InterfaceBuilder builder;
	public static EditixFrame THIS = null;
	
	public EditixFrame( InterfaceBuilder builder ) {
		super( "EditiX XML Editor - Community Edition - For non commercial usage" );
		
		this.builder = builder;
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		if (builder.getAppIcon() != null)
			setIconImage(((ImageIcon) builder.getAppIcon()).getImage());

		if (builder.getMenuBar() != null)
			setJMenuBar(builder.getMenuBar());

		initUI();
		THIS = this;

		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				ApplicationModel.fireApplicationValue( "windowClosing", null );
			}
			public void windowOpened(WindowEvent e) {
				ApplicationModel.fireApplicationValue( "windowOpened", null );
			}
		});

		AddSystemFilesTransferHandler aft = new AddSystemFilesTransferHandler();
		getRootPane().setTransferHandler( aft );

		mainTabbedPane.setTransferHandler( aft );		

		mainTabbedPane.getSelectionModel().addChangeListener(this);
		mainTabbedPane.setActive(true);
		mainTabbedPane.addActionListener(this);

		for ( int i = 1; i <= 9; i++ ) {
			mainTabbedPane.getActionMap().put( "tab" + i, new SelectTabAction( i -1 ) );
			mainTabbedPane.getInputMap( JComponent.WHEN_IN_FOCUSED_WINDOW ).put( KeyStroke.getKeyStroke( KeyEvent.VK_0 + i, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask() ), "tab" + i );
		}
		
		MessagePanel mp = null;
		getRootPane().setGlassPane( mp = new MessagePanel( ) );
		mp.setVisible( true );
	}

	public void addNotify() {
		super.addNotify();
		if ( Preferences.getPreference( "interface", "checkFileChange", true ) ) {
			FileChangeChecker.start();
		}
		EditixApplicationModel.addApplicationModelListener( this );
		
	}	

	public void removeNotify() {
		super.removeNotify();
		FileChangeChecker.stop();
		EditixApplicationModel.removeApplicationModelListener( this );
	}	
	
	public void fireApplicationData(String key, Object... values) {
	}
		
	static ArrayList editixContainerListeners = null;

	public static void addEditixContainerListener(
			EditixContainerListener listener) {
		if (editixContainerListeners == null)
			editixContainerListeners = new ArrayList();
		editixContainerListeners.add(listener);
	}

	static void fireContainerClosed(XMLContainer container) {
		if (editixContainerListeners != null) {
			for (int i = 0; i < editixContainerListeners.size(); i++) {
				EditixContainerListener listener = ( EditixContainerListener )editixContainerListeners
						.get( i );
				listener.close( container );
			}
		}
	}

	// Event from the tabbedPane
	public void actionPerformed(ActionEvent e) {
		if (TabbedContainer.COMMAND_CLOSE.equals(e.getActionCommand())) {
			
			ProjectManager.synchronizeProjectContent();
			
			TabActionEvent se = (TabActionEvent) e;
			se.consume();

			int index = se.getTabIndex();

			// Particular case : Starting panel
			if ( ( ( IXMLPanel )( mainTabbedPane.getModel().getTab( index ).getComponent() ) ).getMainContainer() == null ) {
				closeContainer( mainTabbedPane.getModel().getTab( index ).getComponent() );
				return;
			}

			e.setSource(((IXMLPanel) mainTabbedPane.getModel().getTab(index)
					.getComponent()).getView());
			ActionModel.activeActionById("close", e);

			if ((mainTabbedPane.getTabCount() == 0) && autoNewDocument) {
				checkOneContainer();
				autoNewDocument = false;
			}
			if (mainTabbedPane.getTabCount() == 0)
				noContainer();

		} else if (TabbedContainer.COMMAND_POPUP_REQUEST.equals(e
				.getActionCommand())) {

			TabActionEvent se = (TabActionEvent) e;

			if (builder.hasPopup("TABBEDPANE")) {				
				JPopupMenu jpm = builder.getPopup("TABBEDPANE"); 
				jpm.show(
						mainTabbedPane,
						se.getMouseEvent().getX(), 
						se.getMouseEvent().getY() );
			}
		}
	}

	private int lastTabbedPaneSelection = -1;

	/** Transform the current editor to an independant window */
	public void extract() {
		IXMLPanel container = ( IXMLPanel )mainTabbedPane.getSelectedComponent();
		if ( container != null ) {
			if ( container.getMainContainer() == null )
				return;
			container.setAutoDisposeMode( false );
			removeContainer( mainTabbedPane.getSelectedComponent() );
			EditixEditorFrame e = 
				new EditixEditorFrame( container );
			e.setVisible( true );
			EditixEditorFrameModel.addEditixEditorFrame( e );
		}
	}

	public void refreshCurrentTabName() {
		String tabName = 
			getSelectedContainer().getDocumentInfo().getDocumentName();
		int i = getMainTabbedPane().getSelectedIndex();
		getMainTabbedPane().setTitleAt( i, tabName );
		getMainTabbedPane().setToolTipTextAt( i, getSelectedContainer().getCurrentDocumentLocation() );
	}

	private int getTabIndex( XMLContainer container ) {
		for ( int i = 0; i < getMainTabbedPane().getTabCount(); i++ ) {
			if ( ( (IXMLPanel)getMainTabbedPane().getComponentAt( i ) ).getMainContainer() ==
				container ) {
				return i;
			}
		}
		return -1;
	}

	// Update the file name for modified state
	public void documentModified( XMLContainer container ) {
		int tabIndex = getTabIndex( container );
		if ( tabIndex != -1 ) {
			TabData td = getMainTabbedPane().getModel().getTab( tabIndex );
			String title = td.getText();
			if ( !title.startsWith( "*" ) ) {
				getMainTabbedPane().setTitleAt( tabIndex, "*" + title );
			}
		}
	}

	// Update the file name for unmodified state : Save	
	public void documentUnModified( XMLContainer container ) {
		int tabIndex = getTabIndex( container );
		if ( tabIndex != -1 ) {
			TabData td = getMainTabbedPane().getModel().getTab( tabIndex );
			String title = td.getText();
			if ( title.startsWith( "*" ) ) {
				getMainTabbedPane().setTitleAt( tabIndex, title.substring( 1 ) );
			}
		}		
	}

	public int getCurrentXMLContainerIndex() {
		return mainTabbedPane.getSelectedIndex();
	}
	
	public int getXMLContainerCount() {
		return mainTabbedPane.getTabCount() + 
					EditixEditorFrameModel.getXMLContainerCount();
	}

	public XMLContainer getXMLContainer( int index ) {
		if ( index < mainTabbedPane.getTabCount() ) {
			return ( (IXMLPanel) mainTabbedPane.getComponentAt( index ) ).getMainContainer();
		} else {
			return EditixEditorFrameModel.getXMLContainer( index - mainTabbedPane.getTabCount() );
		}
	}
	
	public IXMLPanel getIXMLPanel( int index ) {
		if ( index < mainTabbedPane.getTabCount() ) {
			return ( IXMLPanel )mainTabbedPane.getComponentAt( index );
		} else
			return EditixEditorFrameModel.getIXMLPanel( index - mainTabbedPane.getTabCount() );
	}

	public void activeXMLContainer( int index ) {
		if ( index < mainTabbedPane.getTabCount() ) {
			mainTabbedPane.setSelectedIndex( index );
		} else {
			EditixEditorFrameModel.ACCESSOR.active( index - mainTabbedPane.getTabCount() );
		}
	}
	
	public boolean activeXMLContainer( String filePath ) {
		for ( int i = 0; i < EditixEditorFrameModel.getXMLContainerCount(); i++ ) {
			XMLContainer container = EditixEditorFrameModel.getXMLContainer( i );
			if ( filePath.equalsIgnoreCase(
					container.getCurrentDocumentLocation() ) ) {
				EditixEditorFrameModel.active( i );
				return true;
			}
		}
		for ( int i = 0; i < mainTabbedPane.getTabCount(); i++ ) {
			IXMLPanel panel = ( IXMLPanel )mainTabbedPane.getComponentAt( i );
			XMLContainer container = panel.getMainContainer();
			if ( container != null ) {
				if ( filePath.equalsIgnoreCase( container.getCurrentDocumentLocation() ) ) {
					mainTabbedPane.setSelectedIndex( i );
					return true;
				}
			}
		}
		return false;
	}
	
	public boolean activeXMLContainerOrOpenIt( String filePath ) {
		if ( !activeXMLContainer( filePath ) ) {
			String type = DocumentModel.getTypeForFileName( filePath );
			boolean ok = OpenAction.openFile(type, false, new File( filePath ), null );
			if ( ok )
				activeXMLContainer( filePath );
			return ok;
		} else
			return true;
	}

	public void stateChanged( ChangeEvent e ) {
		IXMLPanel container = null;
		if (mainTabbedPane.getSelectedComponent() != null)
			container = (IXMLPanel) mainTabbedPane.getSelectedComponent();
		else {
			builder.setEnabledActionForGroup("*", false);
		}

		if (AbstractHelper.WIN != null)
			AbstractHelper.WIN.setVisible(false);

		if (lastTabbedPaneSelection != -1
				&& lastTabbedPaneSelection < mainTabbedPane.getTabCount()) {
			XMLContainer lastOne = ((IXMLPanel) mainTabbedPane
					.getComponentAt(lastTabbedPaneSelection))
					.getMainContainer();
			if ( lastOne != null )
				updateMenuActionForGroup(
						lastOne.getDocumentInfo().getType(), 
						lastOne.getDocumentInfo().getParentType(), 
						false
				);
		}

		lastTabbedPaneSelection = mainTabbedPane.getSelectedIndex();

		if ( container != null ) {
			updateCurrentXMLContainer( container );
		}
	}

	/** Set of action that may be customized when selecting the editor */
	private String[] customActionId = new String[] { 
			"copy", 
			"cut", 
			"paste",
			"parse"
	};	

	public void updateCurrentXMLContainer( IXMLPanel panel ) {
		XMLContainer container = panel.getMainContainer();
		if ( container == null )
			return;	// Not an editable component
		PanelStateManager.fireCurrentXMLContainer( container );
		XMLDocumentInfo docInf = container.getDocumentInfo();
		String type = docInf.getType();
		updateToolBarAndMenuForType( type, docInf.getParentType() );
		com.japisoft.xmlpad.action.ActionModel.resetActionState( container );	
		ActionModel.synchronizeState( container );

		// Push custom action
		for ( int i = 0; i < customActionId.length; i++ ) {
			Action a = panel.getAction( customActionId[ i ] );
			if ( a != null )
				builder.pushAction( a, customActionId[ i ] );
			else
				builder.popAction( customActionId[ i ] );
		}
	}

	public void updateToolBarAndMenuForType( String type, String parentType ) {
		updateToolBarForDocumentType( type, parentType );
		updateMenuActionForGroup( type, parentType, true );
		XMLDocumentInfo info = DocumentModel.getDocumentForType( type );
		// Reset the icon if needed
		XMLContainer container = getSelectedContainer();
		if ( container != null && info != null ) {
			// Not the same type with the current one
			if ( !container.getDocumentInfo().getType().equals( 
					type ) ) {
				mainTabbedPane.setIconAt( 
						mainTabbedPane.getSelectedIndex(),
						info.getDocumentIcon() );
			}
		}
	}

	public void updateMenuActionForGroup(String type, String parentType, boolean enabled) {
		builder.setEnabledActionForGroup(type, enabled);
		if (parentType != null)
			builder.setEnabledActionForGroup(parentType, enabled);
	}

	private void updateToolBarForDocumentType(String type,String parentType) {
		JToolBar tb = builder.getToolBarByGroup(type);
		if ( tb == null ) {
			if ( parentType != null ) {
				tb = builder.getToolBarByGroup( parentType );
			}
		}

		if (tb == null)
			tb = builder.getToolBarByGroup("*");
		if (mainToolBar != tb) {
			remove(mainToolBar);
			mainToolBar = tb;
			getContentPane().add(mainToolBar, BorderLayout.NORTH);
			getContentPane().invalidate();
			getContentPane().validate();
			getContentPane().repaint();
		}
	}

	private JToolBar mainToolBar = null;
	private TabbedContainer mainTabbedPane = null;
	private EditixStatusBar mainStatusBar = null;

	public InterfaceBuilder getBuilder() {
		return builder;
	}

	public static EditixDocking dockingSpace;
	
	private void initUI() {

		getContentPane().setLayout(new BorderLayout());
		mainToolBar = builder.getToolBarByGroup("*");
		if (mainToolBar == null)
			mainToolBar = new JToolBar();
		getContentPane().add(mainToolBar, BorderLayout.NORTH);

		// Check for non empty tabbedPane
		mainTabbedPane = new TabbedContainer(TabbedContainer.TYPE_EDITOR);

		if (builder.hasPopup("TABBEDPANE")) {
			mainTabbedPane.addMouseListener(new TabbedPaneMouseAdapter());
		}

		dockingSpace = new EditixDocking();
		dockingSpace.add( mainTabbedPane, BorderLayout.CENTER );
		getContentPane().add( dockingSpace.getView(), BorderLayout.CENTER);
		
		mainStatusBar = new EditixStatusBar();
		dockingSpace.add( mainStatusBar, BorderLayout.SOUTH );

	}
	
	public JToolBar getMainToolBar() {
		return mainToolBar;
	}

	public TabbedContainer getMainTabbedPane() {
		return mainTabbedPane;
	}

	// Pour la selection
	public void updateToolTip() {
		XMLContainer container = getSelectedContainer();
		if (container != null && container.getCurrentDocumentLocation() != null) {
			mainTabbedPane.updateToolTip(container.getView(), container
					.getCurrentDocumentLocation());
		}
	}
	
	public XMLContainer getSelectedContainer() {
		if ( EditixEditorFrameModel.getSelectedXMLContainer() != null )
			return EditixEditorFrameModel.getSelectedXMLContainer();
		IXMLPanel panel = (IXMLPanel) mainTabbedPane.getSelectedComponent();
		if ( panel == null ) {
			if ( EditixEditorFrameModel.getXMLContainerCount() > 0 ) {
				// Select the first one
				EditixEditorFrameModel.active( 0 );
				XMLContainer container = EditixEditorFrameModel.getXMLContainer( 0 );
				if ( container.getSelectedContainer() != null )
					container = container.getSelectedContainer();
				return container;
			}
			return null;
		}
		if ( panel.getSelectedContainer() != null )
			return panel.getSelectedContainer();		
		return panel.getMainContainer();
	}

	public XMLContainer getSelectedSubContainer(String type) {
		IXMLPanel panel = (IXMLPanel) mainTabbedPane.getSelectedComponent();
		if (panel == null)
			return null;
		return panel.getSubContainer(type);
	}

	public IXMLPanel getSelectedPanel() {
		return (IXMLPanel) mainTabbedPane.getSelectedComponent();
	}

	public XMLContainer getSelectedContainer(String type) {
		IXMLPanel panel = (IXMLPanel) mainTabbedPane.getSelectedComponent();
		if (panel == null)
			return null;
		return panel.getSubContainer(type);
	}

	public IXMLPanel getContainerByFilePath(String filePath) {
		for (int i = 0; i < mainTabbedPane.getTabCount(); i++) {
			IXMLPanel panel = (IXMLPanel) mainTabbedPane.getComponentAt(i);
			XMLContainer container = panel.getMainContainer();
			if ( container != null ) {
				String file = container.getCurrentDocumentLocation();
				if (file != null && filePath.equals(file))
					return panel;
			}
		}
		return null;
	}

	public boolean hasContainer() {
		return mainTabbedPane.getTabCount() > 0;
	}

	/** Add a new XMLContainer */
	public void addContainer(IXMLPanel panel) {
				
		if (mainTabbedPane.getTabCount() == 0)
			builder.setEnabledActionForAllGroup(false);

		if (panel instanceof XSLTEditor) {
			XSLTEditor container = (XSLTEditor) panel;

			// Restore the last file location
			String previousPath = Preferences.getPreference("file",
					"defaultXSLTPath", "");
		}

		XMLContainer container = panel.getMainContainer();

		if ( container != null ) {
			panel.setAutoDisposeMode( true );
			XMLDocumentInfo info = container.getDocumentInfo();
	
			mainTabbedPane.addTab(info.getDocumentName(), info.getDocumentIcon(),
					panel.getView(), info.getCurrentDocumentLocation());
	
			mainTabbedPane.setSelectedComponent(panel.getView());
	
			//ProjectManager.setModified();
			builder.setEnabledActionForGroup("*", true);

			container.requestFocus();
			
			if ( getSelectedContainer() != container )
				updateCurrentXMLContainer( container );
		} else {
			mainTabbedPane.addTab( panel.toString(), null, panel.getView(), null );
		}
	}

	public void removeContainer(Component panel) {
		if (panel instanceof IView) {
			for (int i = 0; i < mainTabbedPane.getTabCount(); i++) {
				IXMLPanel _ = (IXMLPanel) (mainTabbedPane.getModel().getTab(i)
						.getComponent());
				if ( _.getMainContainer() != null )
					if (_.getMainContainer().getView() == panel) {
						panel = _.getView();
						break;
					}
			}
		}

		mainTabbedPane.removeComponent( panel );

		if (panel instanceof IXMLPanel) {
			IXMLPanel panel2 = (IXMLPanel) panel;
			if ( panel2.getMainContainer() != null ) {
				String type = panel2.getMainContainer().getDocumentInfo().getType();
				String parentType = panel2.getMainContainer().getDocumentInfo().getParentType();
				updateMenuActionForGroup(type, parentType, false);
				fireContainerClosed(panel2.getMainContainer());
				PanelStateManager.fireClose( panel2.getMainContainer() );
			}
		}
	}

	public void closeCurrentContainer() {

		// Remove task inside the JobManager
		try {
			int limit = 0;
			while ( ( JobManager.working ) && ( limit < 2000 ) ) {
				Thread.sleep( 10 );
				limit += 10;
			}
		} catch (InterruptedException e) {
		}
		
		//ProjectManager.setModified();
		removeContainer(mainTabbedPane.getSelectedComponent());
		if (mainTabbedPane.getTabCount() == 0)
			mainTabbedPane.requestFocus();
		if (mainTabbedPane.getTabCount() == 0)
			noContainer();
		else {
			stateChanged( null );
		}
	}

	public void closeContainer(Component component) {
		//ProjectManager.setModified();
		removeContainer(component);
		if (mainTabbedPane.getTabCount() == 0)
			mainTabbedPane.requestFocus();
		if (mainTabbedPane.getTabCount() == 0)
			noContainer();
		else {
			stateChanged(null);
		}
	}

	public void closeAllContainers() {
		closeAllContainers( false );
	}
	
	public void closeAllContainers( boolean exceptTheCurrent ) {
		stateChanged(null);
		int size = mainTabbedPane.getTabCount();
		
		if ( size == 0 )
			return;

		Component except = null;

		if ( exceptTheCurrent )
			except = mainTabbedPane.getComponentAt(
					mainTabbedPane.getSelectedIndex() );

		int lookAt = 0;
		
		for (int i = 0; i < size; i++) {
			
			if ( mainTabbedPane.getComponentAt( lookAt ) != except ) {
				removeContainer( mainTabbedPane.getComponentAt( lookAt ) );
			} else {
				lookAt = 1;
			}

		}

		mainTabbedPane.requestFocus();
		if (mainTabbedPane.getTabCount() == 0)
			noContainer();
		// ProjectManager.openProject( null );
	}

	// Popup for tabbedPane
	class TabbedPaneMouseAdapter extends MouseAdapter {
		public void mousePressed(MouseEvent e) {
			if (e.isPopupTrigger()) {
				if (builder.hasPopup("TABBEDPANE")) {
					builder.getPopup("TABBEDPANE").show(e.getComponent(),
							e.getX(), e.getY());
				}
			}
		}

		public void mouseReleased(MouseEvent e) {
			mousePressed(e);
		}
	}

	/** No tabbedPane available */
	public boolean isEmpty() {
		return mainTabbedPane.getTabCount() == 0 &&
			EditixEditorFrameModel.getXMLContainerCount() == 0;
	}

	private boolean autoNewDocument = false;

	/** Add a new document if no document is available */
	public void setAutoNewDocument(boolean autoNewDocument) {
		this.autoNewDocument = autoNewDocument;
	}

	public void checkOneContainer() {
		if (mainTabbedPane.getTabCount() == 0) {
			addContainer(EditixFactory.buildNewContainer(true));
		}
	}

	// Error selected, require to find the good file ??
	public void displaySelectedError(
			boolean local, 
			String source, 
			String message,
			int line) {
		if ( local ) {
			XMLContainer container = getSelectedContainer();
			if ( container != null ) {				
				// It must scroll to the ERROR !
				container.getEditor().highlightErrorLine( line, true );
			}
		} else {
			// Open it

			if ( source != null ) {
								
				if ( source.startsWith( "file" ) ) {
					source = source.substring( 5 );
				}
				
				File f = new File( source );

				if ( f.exists() ) {
				
					IXMLPanel container = getContainerByFilePath( f
							.toString() );
	
					if ( container == null ) {
						String type = null;
						if ( source.toLowerCase().endsWith( "dtd" ) )
							type = "DTD";
						else
						if ( source.toLowerCase().endsWith( "xml" ) )
							type= "XML";
						else
							type = "XSD";
						ActionModel.activeActionById( ActionModel.OPEN, null, f
								.toString(), type);
						container = getSelectedContainer();
					} else {
						mainTabbedPane.setSelectedComponent( container.getView() );
					}
	
					if (container != null && container.getMainContainer() != null ) {
						// Force error panel
						container.getMainContainer().getErrorManager().initErrorProcessing();
						container.getMainContainer().getErrorManager().notifyError(
								null,
								true,
								null,
								line,
								0,
								0,
								message,
								false );
						container.getMainContainer().getErrorManager().stopErrorProcessing();
					}
				
				}
				
			}
		}
	}

	// Select the first one
	public void selectDefaultContainer() {
		mainTabbedPane.setSelectedIndex( 0 );
	}
	
	private void noContainer() {		
		PanelStateManager.fireCurrentXMLContainer( null );
		mainStatusBar.clearState();
		lastTabbedPaneSelection = -1;
		builder.setEnabledActionForGroup("*", false);
	}

	// TabbedPane
	class CustomTabbedPane extends JTabbedPane {
		public void remove(Component c) {
			super.remove(c);
			if ( getTabCount() == 0 && 
					autoNewDocument ) {
				checkOneContainer();
				autoNewDocument = false;
			}
			if (getTabCount() == 0)
				noContainer();
			else
				stateChanged(null);
		}
	}
	
	class SelectTabAction extends AbstractAction {
		int index;
		public SelectTabAction( int index ) {
			this.index = index;
		}
		public void actionPerformed(ActionEvent e) {
			System.out.println( "SELECT TAB " + Math.min( this.index, mainTabbedPane.getTabCount() - 1 ) );
			mainTabbedPane.setSelectedIndex( Math.min( this.index, mainTabbedPane.getTabCount() - 1 ) );
		}
	}

}
