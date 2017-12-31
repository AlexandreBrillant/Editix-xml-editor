package com.japisoft.editix.ui.container;

import java.awt.BorderLayout;
import java.awt.Color;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.util.Iterator;

import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Element;

import org.w3c.dom.Document;
import com.japisoft.editix.action.panels.PanelAction;
import com.japisoft.editix.main.EditixApplicationModel;
import com.japisoft.editix.ui.EditixFactory;
import com.japisoft.editix.ui.container.xpath.XPathInnerView;
import com.japisoft.framework.ApplicationModel;
import com.japisoft.framework.application.descriptor.ActionModel;
import com.japisoft.framework.xml.DOMToolkit;
import com.japisoft.framework.xml.parser.tools.XMLToolkit;

import com.japisoft.xmlpad.DocumentStateListener;
import com.japisoft.xmlpad.IView;
import com.japisoft.xmlpad.IXMLPanel;
import com.japisoft.xmlpad.XMLContainer;
import com.japisoft.xmlpad.XMLDocumentInfo;
import com.japisoft.xmlpad.bookmark.BookmarkContext;
import com.japisoft.xmlpad.bookmark.BookmarkModel;
import com.japisoft.xmlpad.bookmark.BookmarkPosition;
import com.japisoft.xmlpad.editor.XMLEditor;
import com.japisoft.xmlpad.tree.parser.Parser;
import com.japisoft.xmlpad.xml.validator.DefaultValidator;

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
public class EditixXMLContainerInnerView extends JPanel 
		implements IView, 
					IXMLPanel, 
						ActionListener,
								PopupMenuListener {

	private IView defaultView;

	public EditixXMLContainerInnerView( IView defaultView ) {
		this.defaultView = defaultView;
		this.defaultView.getContainer().setAutoDisposeMode( false );		
		initComponents();

		sourceBtn.setIcon( 
			new ImageIcon( 
					EditixXMLContainerInnerView.class.getResource( 
							"document_edit.png" ) 
			)
		);
        sourceBtn.setToolTipText( "Editing from the source document" );
        sourceBtn.setContentAreaFilled( false );
        sourceBtn.setBorderPainted( false );
        sourceBtn.setActionCommand( "Source" );

        FilterBtn.setIcon( 
    		new ImageIcon( 
    				EditixXMLContainerInnerView.class.getResource( 
    						"table2.png" )
    		)
        );

        FilterBtn.setToolTipText( "Editing from the xpath selection" );
        FilterBtn.setContentAreaFilled( false );
        FilterBtn.setActionCommand( "Filter" );
        FilterBtn.setBorderPainted( false );

        currentView.setLayout( new BorderLayout() );
        lblSearch.setForeground( Color.DARK_GRAY );
        lblSearch.setToolTipText( "Search using XPath (/...) or simple expression like element name, attribute name (@...)" );
        cbSearch.setToolTipText( lblSearch.getToolTipText() );

        lbBookmark.setForeground( Color.DARK_GRAY );
        lbBookmark.setToolTipText( "Select your bookmarks" );
        cbBookmark.setToolTipText( "Select a bookmark" );
        cbBookmark.setEditable( false );
        
		switchTo( "Source" );	
	}

	public IXMLPanel getPanelParent() {
		return null;
	}
	
	public Parser createNewParser() {
		return null;
	}
	
	public void copy() {
		boolean sourceMode = "Source".equals( 
			getCurrentView() 
		);
		if ( sourceMode ) {
			getContainer().copy();
		} else {
			lastFilterView.copy();
		}
	}

	public void cut() {
		boolean sourceMode = "Source".equals( 
			getCurrentView() 
		);
		if ( sourceMode ) {
			getContainer().cut();
		} else {
			lastFilterView.cut();
		}
	}
	
	public void paste() {
		boolean sourceMode = "Source".equals( 
			getCurrentView() 
		);
		if ( sourceMode ) {
			getContainer().paste();
		} else {
			lastFilterView.paste();
		}		
	}
	
	@Override
	public Object print() {
		return getContainer().print();
	}

	public void addDocumentStateListener(DocumentStateListener listener) {
		this.defaultView.addDocumentStateListener( listener );
	}

	public void dispose() {
		this.defaultView.dispose();
	}

	public XMLContainer getContainer() {
		return this.defaultView.getContainer();
	}

	public Object[] getDocumentStateListeners() {
		return this.defaultView.getDocumentStateListeners();
	}

	public JComponent getFinalView() {
		return this;
	}

	public void removeDocumentStateListener(DocumentStateListener listener) {
		this.defaultView.removeDocumentStateListener( listener );
	}

	public void setFocusView(boolean focusView) {
		this.defaultView.setFocusView( focusView );
	}

	private JButton[] viewBtns() {
		return new JButton[] {
			sourceBtn,
			FilterBtn
		};
	}

	@Override
	public void addNotify() {
		super.addNotify();
		for ( JButton btn : viewBtns() )
			btn.addActionListener( this );
		cbSearch.addActionListener( this );
		String source = ( String )getProperty( "lastView" );
		if ( source != null ) {
			switchTo( source );
		}
		cbBookmark.addActionListener( this );
		cbBookmark.addPopupMenuListener( this );
		cbGenerator.addActionListener( this );
	}

	@Override
	public void removeNotify() {
		super.removeNotify();
		for ( JButton btn : viewBtns() ) {
			btn.removeActionListener( this );
		}

		cbSearch.removeActionListener( this );
		cbBookmark.removeActionListener( this );
		cbBookmark.removePopupMenuListener( this );
		cbGenerator.removeActionListener( this );

		try {
			( ( JTextField )cbSearch.getEditor().getEditorComponent() ).removeActionListener( this );
			( ( JTextField )cbGenerator.getEditor().getEditorComponent() ).removeActionListener( this );
		} catch( Throwable th ) {
			ApplicationModel.debug( th );
		}
	}

	public void popupMenuCanceled(PopupMenuEvent e) {}
	public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {}

	public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
		cbBookmark.removeActionListener( this );
		cbBookmark.removeAllItems();
		XMLContainer container = getMainContainer();
		BookmarkContext bmc = container.getBookmarkContext();
		BookmarkModel bkm = bmc.getModel();
		for ( int i = 0; i < bkm.getBookmarkCount(); i++ ) {
			BookmarkPosition bk = bkm.getBookmarkPositionAt( i );
			int offset = bk.getOffset();
			int index = container.getEditor().getDocument().getDefaultRootElement().getElementIndex( offset );
			Element ee = container.getEditor().getDocument().getDefaultRootElement().getElement( index );
			int start = ee.getStartOffset();
			int stop = ee.getEndOffset();
			try {
				String line = 
					container.getEditor().getDocument().getText( start, stop - start );
				cbBookmark.addItem( ( index + 1 ) + ":" + line.substring( 0, Math.min( line.length(), 20 ) ) + "..." );
			} catch( BadLocationException ble ) {
				ble.printStackTrace();
			}
		}
		cbBookmark.addActionListener( this );
	}

	private void activateSearch() {
		
		String content = ( String )cbSearch.getSelectedItem();
		if ( content != null ) {
			
			String type = getContainer().getDocumentInfo().getType();
			boolean pureText = getContainer().getDocumentInfo().isTEXT();
	
			if ( pureText ) {
				
				// Basic search
				ActionModel.activeActionById( "find", new ActionEvent( 
					this, 
					0, 
					null ), 
					content 
				);

				boolean found = false;
				for ( int i = 0; i < cbSearch.getItemCount(); i++ ) {
					if ( content.equals( cbSearch.getItemAt( i ) ) ) {
						found = true;
						break;
					}
				}
	
				if ( !found ) {
					cbSearch.addItem( content );
				}
				
			} else {
	
				PanelAction xpathAction = ( PanelAction )ActionModel.restoreAction( "xpath" );
				PanelAction criteriaAction = ( PanelAction )ActionModel.restoreAction( "criteria" );
	
				PanelAction panel = null;
	
				boolean found = false;
				for ( int i = 0; i < cbSearch.getItemCount(); i++ ) {
					if ( content.equals( cbSearch.getItemAt( i ) ) ) {
						found = true;
						break;
					}
				}
	
				if ( !found ) {
					cbSearch.addItem( content );
				}
	
				if ( content.contains( "/" ) ) {
					panel = xpathAction;
				} else {
					if ( content.startsWith( "@" ) )
						content = "a" + content.substring( 1 );
					else
						content = "e" + content;
	
					panel = criteriaAction;
				}
							
				panel.putValue( "param2", content );
				panel.setForceAlwaysShown( true );
				panel.actionPerformed( new ActionEvent( this, 0, null ) );
				panel.putValue( "param2", null );
				panel.setForceAlwaysShown( false );
	
			}
		}
	}

	private void activateGenerator() {

		String xpath = ( String )cbGenerator.getSelectedItem();
		XMLEditor editor = getContainer().getEditor();
		int offset = editor.getCaretPosition();
		int index = editor.getDocument().getDefaultRootElement().getElementIndex( offset );
		Element se = editor.getDocument().getDefaultRootElement().getElement( index );
		int start = se.getStartOffset();
		int end = editor.getCaretPosition() - start;
		StringBuffer sb = new StringBuffer();
		try {
			for ( int i = start; i < se.getEndOffset() - 1; i++ ) {
				String tmp = editor.getDocument().getText( i, 1 ); 
				if ( Character.isWhitespace( tmp.charAt( 0 ) ) )
					sb.append( tmp );
				else
					break;
			}
			editor.getDocument().insertString( 
				se.getStartOffset(),
				com.japisoft.framework.xml.generator.XMLGenerator.getInstance().generator( 
					xpath, 
					sb.toString(), 
					EditixApplicationModel.getIndentString() ),
				null
			);
			// Already ?
			cbGenerator.removeItem( xpath );
			cbGenerator.addItem( xpath );
		} catch( BadLocationException ble ) {
		}				
				
	}
	
	public void actionPerformed(ActionEvent e) {
		
		if ( "comboBoxEdited".equals( e.getActionCommand() ) )	// Avoid twice event
			return;	
		
		String cbName = null;
		
		if ( e.getSource() instanceof JComboBox ) {
			JComboBox cb = ( JComboBox )e.getSource();
			if ( cb.getEditor() != null ) {
				if ( cb.getEditor().getEditorComponent() instanceof JTextField ) {
					cbName = cb.getEditor().getEditorComponent().getName();
				}
			}
		}

		if ( e.getSource() == cbBookmark ) {
			String s = ( String )cbBookmark.getSelectedItem();
			if ( s != null ) {
				int i = s.indexOf( ":" );
				int line = Integer.parseInt( s.substring( 0, i ) );
				getMainContainer().getEditor().setLineNumber( line );	
			}
			return;
		} else
		if ( e.getSource() == cbSearch || 
				"cbSearch".equals( 
					cbName ) ) {
			activateSearch();
			return;
		} else
		if ( e.getSource() == cbGenerator || 
				"cbGenerator".equals( cbName ) ) {
			activateGenerator();
			return;
		}

		switchTo( 
			( ( JButton )e.getSource() ).getActionCommand() 
		);
	}

	private FilterView lastFilterView = null;
	
	private FilterView xpathView = null;
	private FilterView cssView = null; 
	
	private String currentViewName = null;

	private Document getDOMDocument() throws Exception {
		return DOMToolkit.parse(
			defaultView.getContainer().getText(), 
			defaultView.getContainer().getCurrentDocumentLocation() 
		);
	}

	private Document currentDocument = null;

	public String getCurrentView() {
		return ( String )getProperty( "lastView" );
	}
	
	private String lastXMLHeader = null;

	private void switchTo( String target ) {

		if ( target.equals( 
				currentViewName ) )
			return;

		setProperty( "lastView", target );

		this.currentViewName = target;
		JButton[] btns = viewBtns();
		for ( JButton btn : btns ) {
			btn.setEnabled( false );
		}

		boolean sourceMode = "Source".equals( target );

		lblSearch.setEnabled( sourceMode );
		cbSearch.setEnabled( sourceMode );

		cbBookmark.setEnabled( sourceMode );
		lblGenerator.setEnabled( sourceMode );
		lbBookmark.setEnabled( sourceMode );
		cbGenerator.setEnabled( sourceMode );

		// Force no dispose mode
		boolean disposeModeState = this.defaultView.getContainer().isAutoDisposeMode();
		this.defaultView.getContainer().setAutoDisposeMode( false );

		currentView.removeAll();

		if ( lastFilterView != null ) {
			if ( lastFilterView.isModified() ) {
				try {
					this.defaultView.getContainer().setText( 
						lastXMLHeader +
						DOMToolkit.DOM2String( 
							currentDocument, 
							EditixApplicationModel.getIndentSpace() )
					);
				} catch( Exception exc ) {
					EditixFactory.buildAndShowErrorDialog( 
						"Can't get the full text : " + exc.getMessage()
					);
					if ( !"Source".equals( target ) ) {	
						// Force a Source Target
						switchTo( "Source" );
					}
					return;
				}
			}
			String state = lastFilterView.serializeState();
			this.defaultView.getContainer().setProperty( 
				"filter" + lastFilterView.getName(), 
				state
			);
			lastFilterView.dispose();
			lastFilterView = null;
			currentDocument = null;
		}

		if ( "Source".equals( target ) ) {
			currentView.add( 
				this.defaultView.getFinalView()
			);
		} else
		if ( "Filter".equals( target ) ) {
			if ( xpathView == null ) {
				xpathView = new XPathInnerView();
			}
			lastFilterView = xpathView;
		}		
		if ( !sourceMode ) {

			String oldState = 
				( String )this.defaultView.getContainer().getProperty( 
						"filter" + lastFilterView.getName() );
			if ( oldState != null ) {
				lastFilterView.restoreState( oldState );
			}

			try {
				
				DefaultValidator v = new DefaultValidator();
				v.setDomBuilderMode( true );
				v.validate( this.defaultView.getContainer(), false );
				lastFilterView.init(
					this.defaultView.getContainer().getSyntaxHelper().getHelperManager(),
					this.defaultView.getContainer().getCurrentDocumentLocation(),
					currentDocument = v.getDocument()
				);

				lastXMLHeader = com.japisoft.framework.xml.XMLToolkit.getFullProlog( 
					this.defaultView.getContainer().getText() 
				);
								
			} catch( Exception exc ) {
				exc.printStackTrace();
				EditixFactory.buildAndShowErrorDialog( 
					"Your XML document is invalid, please fix it : " +
						exc.getMessage()
				);
				lastFilterView = null;
				switchTo( "Source" );
				return;
			}

			currentView.add(
				this.lastFilterView.getView()
			);

			this.lastFilterView.requestFocus();
		}

		currentView.invalidate();
		currentView.validate();
		
		for ( JButton btn : btns ) {
			if ( !target.equals( 
					btn.getActionCommand() ) ) {
				btn.setEnabled( true );
			}
		}

		this.defaultView.getContainer().setAutoDisposeMode( 
			disposeModeState 
		);

		currentView.repaint();

		// Force Focus

		if ( !sourceMode ) {
			SwingUtilities.invokeLater(
				new Runnable() {
					public void run() {
						lastFilterView.requestFocus();
					}
				}
			);
		}
	}

	public Action getAction(String actionId) {
		return defaultView.getContainer().getAction( actionId );
	}

	public BookmarkContext getBookmarkContext() {
		return defaultView.getContainer().getBookmarkContext();
	}

	public XMLContainer getMainContainer() {
		return defaultView.getContainer();
	}

	public Iterator getProperties() {
		return defaultView.getContainer().getProperties();
	}

	public Object getProperty(String name, Object def) {
		return defaultView.getContainer().getProperty( name, def );
	}

	public Object getProperty(String name) {
		return defaultView.getContainer().getProperty( name );
	}

	public XMLContainer getSelectedContainer() {
		return defaultView.getContainer().getSelectedContainer();
	}

	public XMLContainer getSubContainer(String type) {
		return defaultView.getContainer().getSubContainer( type );
	}

	public XMLContainer getSubContainerAt(int index) {
		return defaultView.getContainer().getSubContainerAt( index );
	}

	public int getSubContainerCount() {
		return defaultView.getContainer().getSubContainerCount();
	}

	public JComponent getView() {
		return this;
	}

	public void postLoad() {
		defaultView.getContainer().postLoad();
	}

	public void prepareToSave() {
		switchTo( "Source" );
		defaultView.getContainer().prepareToSave();		
	}

	public boolean reload() {
		return defaultView.getContainer().reload();
	}

	public void selectSubContainer(IXMLPanel panel) {
		defaultView.getContainer().selectSubContainer( panel );
	}

	public void setAutoDisposeMode(boolean disposeMode) {
		defaultView.getContainer().setAutoDisposeMode( disposeMode );
	}

	public void setDocumentInfo(XMLDocumentInfo info) {
		defaultView.getContainer().setDocumentInfo( info );
	}
	
	public void initForDocumentInfo(XMLDocumentInfo info ) {
		FilterBtn.setEnabled( info.isXML() );
		cssBtn.setEnabled( info.isXML() );
	}

	public void setProperty(String name, Object content) {
		defaultView.getContainer().setProperty( name, content );
	}

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">
    private void initComponents() {

        sourceBtn = new javax.swing.JButton();
        FilterBtn = new javax.swing.JButton();
        currentView = new javax.swing.JPanel();
        lblSearch = new javax.swing.JLabel();
        cbSearch = new javax.swing.JComboBox();
        lbBookmark = new javax.swing.JLabel();
        cbBookmark = new javax.swing.JComboBox();
        cssBtn = new javax.swing.JButton();
        lblGenerator = new javax.swing.JLabel();
        cbGenerator = new javax.swing.JComboBox();

        org.jdesktop.layout.GroupLayout currentViewLayout = new org.jdesktop.layout.GroupLayout(currentView);
        currentView.setLayout(currentViewLayout);
        currentViewLayout.setHorizontalGroup(
            currentViewLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 937, Short.MAX_VALUE)
        );
        currentViewLayout.setVerticalGroup(
            currentViewLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 273, Short.MAX_VALUE)
        );

        lblSearch.setText("Search");

        cbSearch.setEditable(true);


        lbBookmark.setText("Bookmarks");

        cbBookmark.setEditable(true);


        lblGenerator.setText("Generator");

        cbGenerator.setEditable(true);

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(currentView, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(sourceBtn, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 27, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(FilterBtn, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 27, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(8, 8, 8)
                .add(cssBtn, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 27, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 48, Short.MAX_VALUE)
                .add(lblGenerator)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(cbGenerator, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 186, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(18, 18, 18)
                .add(lbBookmark)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(cbBookmark, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 186, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(25, 25, 25)
                .add(lblSearch)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(cbSearch, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 186, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                .add(currentView, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(sourceBtn)
                    .add(FilterBtn)
                    .add(cbSearch, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(lblSearch)
                    .add(cbBookmark, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(lbBookmark)
                    .add(cssBtn)
                    .add(cbGenerator, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(lblGenerator)))
        );
    }// </editor-fold>


    // Variables declaration - do not modify
    private javax.swing.JButton FilterBtn;
    private javax.swing.JComboBox cbBookmark;
    private javax.swing.JComboBox cbGenerator;
    private javax.swing.JComboBox cbSearch;
    private javax.swing.JButton cssBtn;
    private javax.swing.JPanel currentView;
    private javax.swing.JLabel lbBookmark;
    private javax.swing.JLabel lblGenerator;
    private javax.swing.JLabel lblSearch;
    private javax.swing.JButton sourceBtn;
    // End of variables declaration
	
}
