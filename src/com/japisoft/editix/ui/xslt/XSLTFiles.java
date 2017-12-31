package com.japisoft.editix.ui.xslt;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import javax.swing.ImageIcon;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Element;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import com.japisoft.editix.ui.xslt.map.MapPanel;
import com.japisoft.framework.xml.XMLFileData;
import com.japisoft.framework.xml.XMLToolkit;
import com.japisoft.framework.xml.parser.ParseException;
import com.japisoft.framework.xml.parser.FPParser;
import com.japisoft.framework.xml.parser.node.FPNode;
import com.japisoft.xmlpad.DocumentStateListener;
import com.japisoft.xmlpad.IView;
import com.japisoft.xmlpad.IXMLPanel;
import com.japisoft.xmlpad.XMLContainer;
import com.japisoft.xmlpad.action.xml.FormatAction;
import com.japisoft.xmlpad.editor.XMLPadDocument;
import com.japisoft.xmlpad.editor.XMLEditor;

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
public class XSLTFiles extends JTabbedPane implements
		ChangeListener, 
			DocumentStateListener,
				MouseListener, 
					ActionListener {

	private Factory factory;
	private XMLContainer mainContainer;
	private XSLTEditorListener listener;
	private IXMLPanel parent;
	
	public XSLTFiles(
			IXMLPanel parent,
			Factory factory, 
			XSLTEditorListener listener ) {
		this.parent = parent;
		this.factory = factory;
		this.listener = listener;
		initUI();
	}

	private HashMap sharedProperties = null;

	private XMLContainer buildNewContainer( boolean forceType ) {
		String type = null;
		
		XMLContainer tmpContainer = 
			factory.buildNewContainer( null );
		
		tmpContainer.setParentPanel( this.parent );
		
		// Force the same properties
		if ( sharedProperties == null )
			sharedProperties = new HashMap();
		tmpContainer.resetProperties( sharedProperties );
		
		tmpContainer.setTreePopupAvailable( 
				false );
		tmpContainer.getUIAccessibility().setTreeToolBarAvailable( 
				false );
		tmpContainer.setProperty(
			FormatAction.PREF_APOSENTITY, 
			Boolean.FALSE
		);
		if ( forceType )
			tmpContainer.getMainContainer().setBookmarkContext( 
				new XSLTBookmarkContext() 
			);
				
		return tmpContainer;
	}

	private void initUI() {
		mainContainer = 
			buildNewContainer( false );
		addTabXMLContainer( 
			"Main", 
			mainContainer 
		);		
		initListeners();
	}

	private boolean initListeners = false;
	
	private void initListeners() {
		if ( !initListeners ) {
			
			for ( int i = 0; i < getXMLContainerCount(); i++ ) {
				getXMLContainer( i ).addDocumentStateListener( 
					this 
				);
			}
			
			addChangeListener( this );
			initListeners = true;
		}
	}

	private void removeListeners() {
		if ( initListeners ) {
			initListeners = false;
			removeChangeListener( this );
			
			for ( int i = 0; i < getXMLContainerCount(); i++ ) {
				getXMLContainer( i ).removeDocumentStateListener( 
					this 
				);
			}
		}
	}

	public void addNotify() {
		super.addNotify();
		initListeners();
		addMouseListener( this );
	}

	public void removeNotify() {
		super.removeNotify();
		removeListeners();
		removeMouseListener( this );
	}

	private Object currentLineMarker = null;
	
	public void showCurrentLine( String uri, int line ) {
		
		cleanCurrentLineMarker();		
		
		if ( line >= 0 ) {		
			XMLContainer container = getContainerForUri( uri );
			XMLPadDocument doc = container.getXMLDocument();
			Element element = doc.getDefaultRootElement().getElement( line );
			XMLEditor editor = container.getEditor();
			try {
			
				currentLineMarker = editor.getHighlighter().addHighlight(
						element.getStartOffset(), element.getEndOffset(),
						new CurrentHighlighter());
	
			} catch (BadLocationException exc) {
			}
			int caret = element.getStartOffset();
			editor.setCaretPositionWithoutNotification(caret);
		}
		
	}
	
	private void cleanCurrentLineMarker() {
		if ( currentLineMarker != null ) {
			for ( int i = 0; i < getXMLContainerCount(); i++ ) {
				getXMLContainer( i ).getEditor().getHighlighter().removeHighlight( currentLineMarker );
			}
			currentLineMarker = null;
		}
	}

	private XMLContainer getContainerForUri( String uriSource ) {

		// Search the good container for this uri
		String fileToMatch = uriSource;
		int i = 0;
		if ( uriSource != null ) {
			i = 
				uriSource.lastIndexOf( "/" );
			if ( i == -1 )
				i = uriSource.lastIndexOf( "\\" );
			if ( i > -1 ) {
				fileToMatch = 
					fileToMatch.substring( i + 1 );
			}
		}
		XMLContainer container = 
			mainContainer.getMainContainer();
		for ( i = 0; i < getXMLContainerCount(); i++ ) {
			XMLContainer tmp = getXMLContainer( i );
			String uri = tmp.getCurrentDocumentLocation();
			if ( uri != null ) {
				int j = 
					uri.lastIndexOf( "/" );
				if ( j == -1 )
					j = uri.lastIndexOf( "\\" );
				if ( j > -1 ) {
					uri = uri.substring( j + 1 );
				}
				if ( uri.equalsIgnoreCase( fileToMatch ) ) {
					setSelectedComponent( tmp.getView() );
					container = tmp;
				}
			}
		}

		return container;
		
	}
	
	// For debugging
	public void showXSLTLine( 
			String uriSource, 
			int line ) {
		XMLContainer container = getContainerForUri( uriSource );
		container.getEditor().highlightLine( line );
	}

	// Switch to another tab
	public void stateChanged( ChangeEvent e ) {
		if ( getSelectedComponent() instanceof IView ) {
			if ( listener != null )
				listener.setCurrentContainer( 
					( ( IView )getSelectedComponent() ).getContainer() );	
		} else
			if ( getSelectedComponent() instanceof XSLTTemplates ) {
				( ( XSLTTemplates )getSelectedComponent() ).updateContent();
			} else {
				if ( getSelectedComponent() instanceof MapPanel ) {
					( ( MapPanel )getSelectedComponent() ).updateContent();
				}
			}
	}

	public int getXMLContainerCount() {
		int compteur = 0;
		for ( int i = 0; i < getTabCount(); i++ ) {
			if ( getComponentAt( i ) instanceof IView ) {
				compteur++;
			}
		}
		return compteur;
	}

	public XMLContainer getXMLContainer( int index ) {
		int compteur = index;
		for ( int i = 0; i < getTabCount(); i++ ) {
			if ( getComponentAt( i ) instanceof IView ) {
				if ( compteur == 0 )
					return ( ( ( IView )getComponentAt( i ) ).getContainer() );
				compteur--;
			}
		}		
		return null;
	}

	public FPNode getXMLContainerRootNode( int index ) {
		XMLContainer container =
			getXMLContainer( index );
		if ( container == null )
			return null;
		FPNode rootNode = 
			container.getRootNode();
		if ( rootNode == null ) {
			rootNode = getRoot( container );
		}
		return rootNode;
	}

	public org.w3c.dom.Element getXMLContainerDOMRootNode( int index ) {
		XMLContainer container =
			getXMLContainer( index );
		if ( container == null )
			return null;
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setNamespaceAware( true );
			DocumentBuilder db = factory.newDocumentBuilder();
			Document doc = db.parse( new InputSource( new StringReader( container.getText() ) ) );
			return doc.getDocumentElement();
		} catch( Exception th ) {
			return null;
		}
	}

	public void setXMLContainerDOMRootNode( int index, org.w3c.dom.Element root ) {
		XMLContainer container =
			getXMLContainer( index );
		if ( container == null )
			return;		
		try {
			Transformer t = 
				TransformerFactory.newInstance().newTransformer();
			StringWriter sw = new StringWriter();
			t.transform( 
				new DOMSource( root ), 
				new StreamResult( sw ) 
			);
			container.setText( sw.toString() );
		} catch( Exception exc ) {
		}
	}

	private FPNode getRoot( XMLContainer source ) {

		FPParser p = new FPParser();
		try {
			com.japisoft.framework.xml.parser.document.Document d = p.parse( new StringReader( source.getText() ));
			return ( FPNode )d.getRoot();
		} catch( ParseException e ) {			
		}
		return null;
	}

	public void documentModified( XMLContainer source ) {
		int i = 
			indexOfComponent( 
				source.getView() );
		if ( i > -1 ) {
			String title = getTitleAt( i );
			if ( title.indexOf( "*" ) == -1 )
				title = title + "*";
			setTitleAt( i, title );
			if ( source != mainContainer ) {
				// Force a modification on the main container
				mainContainer.setModifiedState( true );
			}
		}
	}

	public void resetVisualState( XMLContainer source ) {
		int i = indexOfComponent( 
					source.getView() );
		if ( i > -1 ) {
			String title = getTitleAt( i );
			if ( title.endsWith( "*" ) ) {
				setTitleAt(
					i,
					title.substring( 0, title.length() - 1 )
				);
			}
		}
	}

	public void newDocument( XMLContainer source ) {
		
		FPNode rootNode = 
			getRoot( source );
		if ( rootNode != null ) {
			resetIncludeImportTabs( source, rootNode );
		}
		
		removeListeners();
		initListeners();		
		initTemplatesTab();
		initMapPanel();
		
		source.getEditor().setTransferHandler( 
				new XSLTEditor.DropSimpleNodeTransferHandler( 
					source.getEditor().getTransferHandler() ) 
		);
		
	}

	public void dispose() {
		mainContainer.dispose();
		if ( templates != null )
			templates.dispose();
		if ( mapPanels != null )
			mapPanels.dispose();
	}

	public void selectNode( XMLContainer container, FPNode node ) {
		setSelectedComponent( container.getView() );
		// container.getEditor().highlightNode( node );
		container.getEditor().setCaretForLine( node.getStartingLine() );
	}
	
	XMLContainer getMainContainer() {
		return mainContainer;
	}

	XMLContainer getSelectedContainer() {
		Component c = getSelectedComponent();
		if ( c instanceof IView ) {
			return ( ( IView )c ).getContainer(); 
		}
		// For the main by default
		return mainContainer;
	}

	private void resetIncludeImportTabs( 
			XMLContainer source, 
			FPNode rootNode ) {
		for ( int i = 0; i < rootNode.childCount(); i++ ) {
			FPNode childNode = 
				rootNode.childAt( i );
			if ( childNode.matchContent( "include" ) ||
					childNode.matchContent( "import" ) ) {
				checkDocument( 
					source, 
					childNode.getAttribute( "href" ) );
			}
		}
	}
	
	private Map resolvedURI = null;

	private void checkDocument( 
			XMLContainer source, 
			String href ) {
		if ( href == null || 
				"".equals( href ) )	// Skip error in the XSLT document
			return;
		String currentLocation =
			source.getCurrentDocumentLocation();
		String finalUri = null;
		if ( href.indexOf( "://" ) > -1 ) {
			finalUri = href;
		} else {
			File f = new File( href );
			if ( !f.exists() ) {
				// Try a relative location
				if ( currentLocation != null ) {
					f = new File( 
						new File( currentLocation ).getParentFile(), 
							href 
					);
					if ( f.exists() ) {
						finalUri =
							f.toString();
					}
				}
			} else {
				finalUri = 
					f.toString();
			}
		}
		if ( finalUri != null ) {
			if ( resolvedURI == null )
				resolvedURI = new HashMap();
			if ( !resolvedURI.containsKey( 
					finalUri ) ) {
				resolvedURI.put( 
					finalUri, 
					Boolean.TRUE 
				);
				loadIncludeImportDocument( finalUri );
			}
		}
	}

	private void loadIncludeImportDocument( String uri ) {
		try {
			XMLFileData file = 
				XMLToolkit.getContentFromURI( 
					uri, 
					null 
				);
			XMLContainer container = 
				buildNewContainer( true );

			container.setDocumentInfo( 
				getMainContainer().getDocumentInfo().cloneDocument() 
			);

			container.setCurrentDocumentLocation( uri );
			container.setText( file.getContent() );
			addTabXMLContainer( null, container );

			// Scan the document content too
			newDocument( container );
			
		} catch( Throwable e ) {
			System.out.println( 
				"Can't load [" + uri + "]" 
			);
		}
	}

	private void addTabXMLContainer(
			String title,
			XMLContainer container ) {
		String uri = 
			container.getCurrentDocumentLocation();
		String tabName = uri;
		if ( uri != null ) {
				int i = 
					uri.lastIndexOf( "/" );
				if ( i == -1 )
					i = uri.lastIndexOf( "\\" );		
			if ( i > -1 ) { 
				tabName = 
					tabName.substring( i + 1 );
			}
		}

		String img = "pawn.png";

		if ( container == mainContainer ) {
			img = "pawn_glass_green.png";
		}

		if ( title != null ) {
			tabName = title;
		}

		int insertAt = Math.max( 0, getTabCount() );
		
		if ( container == mainContainer ) 
			insertAt = 0;
		if ( initTemplatesTab )
			insertAt--;
		if ( initMapPanel ) {
			insertAt--;
		}

		insertTab( 
			tabName,
			new ImageIcon( 
					getClass().getResource( img ) ),			
			container.getView(),
			container.getCurrentDocumentLocation(),
			insertAt
		);		
	}

	private boolean initTemplatesTab = false;
	private XSLTTemplates templates = null;

	private void initTemplatesTab() {
		if ( !initTemplatesTab ) {			
			addTab( "Templates",
				new ImageIcon( 
					getClass().getResource( 
						"pawn_view.png" ) ),
				templates =
					new XSLTTemplates( this )
			);
			initTemplatesTab = true;
		}
	}

	private boolean initMapPanel = false;
	private MapPanel mapPanels = null;

	private void initMapPanel() {
		if ( !initMapPanel ) {
			addTab(
				"Mapping",
				new ImageIcon(
					getClass().getResource(
						"link.png" ) ),
						mapPanels = new MapPanel( this )
			);
			initMapPanel = true;
		}
	}

	public void actionPerformed( ActionEvent e ) {
		if ( "update".equals( e.getActionCommand() ) ) {
			newDocument( 
				mainContainer 
			);
		}
	}	

	public void mouseClicked( MouseEvent e ) {
	}

	public void mouseEntered( MouseEvent e ) {
	}

	public void mouseExited( MouseEvent e ) {
	}

	public void mousePressed( MouseEvent e ) {
		maybeShowPopup( e );
	}

	public void mouseReleased( MouseEvent e ) {
		maybeShowPopup( e );		
	}

	private void maybeShowPopup( MouseEvent e ) {
		if ( e.isPopupTrigger() ) {
			JPopupMenu popup = 
				new JPopupMenu();
			JMenuItem item = new JMenuItem( "Update links" );
			item.setToolTipText( "Update included/imported documents" );
			item.setActionCommand( "update" );
			item.addActionListener( this );
			popup.add( item );
			popup.show(
				e.getComponent(),
				e.getX(), 
				e.getY()
			);
		}
	}

}
