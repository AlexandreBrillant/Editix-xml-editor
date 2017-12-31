package com.japisoft.editix.editor.xsd;

import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.util.Date;
import java.util.Iterator;

import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.japisoft.editix.editor.xsd.toolkit.SchemaHelper;
import com.japisoft.framework.ApplicationModel;
import com.japisoft.framework.job.BasicJob;
import com.japisoft.framework.job.JobManager;
import com.japisoft.framework.preferences.Preferences;
import com.japisoft.framework.xml.parser.node.FPNode;
import com.japisoft.xmlpad.IXMLPanel;
import com.japisoft.xmlpad.XMLContainer;
import com.japisoft.xmlpad.XMLDocumentInfo;
import com.japisoft.xmlpad.action.ActionModel;
import com.japisoft.xmlpad.action.xml.FormatAction;
import com.japisoft.xmlpad.bookmark.BookmarkContext;
import com.japisoft.xmlpad.tree.parser.Parser;
import com.japisoft.xmlpad.xml.validator.DefaultValidator;
import com.japisoft.xmlpad.xml.validator.Validator;

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
public class XSDEditor extends JTabbedPane 
		implements 
			IXMLPanel, 
			ChangeListener {
	private XMLContainer container = null;
	private Factory factory = null;
	private VisualXSDEditor editor = null;
	private XSDEditorObserver observer = null;
	
	public XSDEditor( Factory factory ) {
		super( JTabbedPane.BOTTOM );
		
		this.factory = factory;
		container = factory.buildNewContainer();
		addTab( "Source Editor", 
				new ImageIcon( getClass().getResource( 
						"document_edit.png" ) ),
				container.getView() );
		addTab( "Visual Editor",
				new ImageIcon( getClass().getResource( 
						"flash.png" ) ),
				( editor = new VisualXSDEditor( factory ) ).getView() );
	}

	public IXMLPanel getPanelParent() {
		return null;
	}
	
	public Parser createNewParser() {
		return null;
	}
	
	public void setDocumentInfo(XMLDocumentInfo info) {
		container.setDocumentInfo( info );
	}	
	
	public void setCustomActionListener( CustomActionListener cal ) {
		editor.setCustomActions( cal );
	}
	
	public void setObserver( XSDEditorObserver observer ) {
		this.observer = observer;
	}
	
	public void copy() {
		if ( getSelectedIndex() == 0 ) {
			container.copy();
		} else {
			editor.copy();
		}
	}

	public void cut() {
		if ( getSelectedIndex() == 0 ) {
			container.cut();
		} else {
			editor.cut();
		}
	}

	public void paste() {
		if ( getSelectedIndex() == 0 ) {
			container.paste();
		} else {
			editor.paste();
		}
	}

	@Override
	public Object print() {
		if ( getSelectedIndex() == 0 ) {
			return container;
		} else {
			return editor.print();
		}
	}
	
	public Action getAction(String actionId) {
		if ( getSelectedIndex() == 1 ) {
			if ( "copy".equals( actionId ) )
				return editor.getCopyAction();
			else
			if ( "paste".equals( actionId ) )
				return editor.getPasteAction();
			else
			if ( "cut".equals( actionId ) )
				return editor.getCutAction();
		}
		return null;
	}

	public void addNotify() {
		super.addNotify();
		addChangeListener( this );
	}

	public void removeNotify() {
		super.removeNotify();
		removeChangeListener( this );
	}

	private Document schemaRoot = null;

	public void stateChanged(ChangeEvent e) {
		if ( getSelectedIndex() == 1 ) {
			
			// VISUAL MODE
			observer.switchToView( this, true );
			
			// Build DOM tree
			DefaultValidator dv = new DefaultValidator( true );

			if ( dv.validate( container, false ) == Validator.ERROR ) {
				factory.buildAndShowErrorDialog( "Error found inside the source\nPlease fix it before using the visual editor" );
				setSelectedIndex( 0 );
			} else {
				// Check the root element
				Document d = dv.getDocument();
				SchemaHelper.cleanEmptyTextNode( d );

				if ( d.getDocumentElement() == null ) {
					// Create it
					Element _ = d.createElementNS( SchemaHelper.SCHEMA_NS, "xs:schema" );
					d.appendChild( _ );
					// Create a initial element
					Element __ = SchemaHelper.createElement( 
							schemaRoot.getDocumentElement(), 
							"myElement" );
					d.getDocumentElement().appendChild( __ );
				} else {
					// Check the root node
					Element _ = d.getDocumentElement();
					if ( !SchemaHelper.SCHEMA_NS.equals( _.getNamespaceURI() ) ) {
						factory.buildAndShowErrorDialog( "Wrong namespace for the root element, wait for 'http://www.w3.org/2001/XMLSchema'\nPlease fix it" );
						setSelectedIndex( 0 );						
					} else {
						if ( !"schema".equals( _.getLocalName() ) ) {
							factory.buildAndShowErrorDialog( "Wrong name for the root element, wait for 'schema'\nPlease fix it" );
							setSelectedIndex( 0 );													
						} else {
							schemaRoot = d;
														
							if ( !SchemaHelper.hasChildrenExceptAnnotationAndText( d.getDocumentElement() ) ) {

								// Create a initial element
								Element __ = SchemaHelper.createElement( 
										schemaRoot.getDocumentElement(), 
										"myElement" );
								
								SchemaHelper.createNewAnnotation( d.getDocumentElement(), "Created with EditiX (http://www.editix.com) at " + new Date() );								
								d.getDocumentElement().appendChild( __ );

							}

							Element root = d.getDocumentElement();
							// For resolving include/import inside the visual editor
							root.setUserData( "source", container.getCurrentDocumentLocation(), null );
							
							editor.init( root );
							if ( container.getCurrentElementNode() != null )
								editor.select( container.getCurrentElementNode().getXPathLocation() );
						}
					}
				}
			}
		} else {

			// SOURCE
			stopEditing( false );

			observer.switchToView( this, false );
			
		}
	}	

	private void stopEditing( boolean keepRoot ) {		
		if ( schemaRoot != null ) {
			
			// Find selected element
			Element selectedElement = editor.getSelectedElement();
			
			editor.stopEditing();

			// Update the source
			FormatAction fa = ( FormatAction )ActionModel.getActionByName( 
			ActionModel.FORMAT_ACTION );
			XMLContainer oldContainer = fa.getXMLContainer();
			fa.setXMLContainer( container );
			fa.formatAction( schemaRoot );
			fa.setXMLContainer( oldContainer );
			if ( !keepRoot )
				schemaRoot = null;
			
			if ( selectedElement != null ) {
				// Select this element
				String xpath = SchemaHelper.getXPathExpression( selectedElement );
				JobManager.addJob( new SelectedElementJob( xpath ) );
			}
		}
	}

	public void dispose() {
		container.dispose();
	}

	public void postLoad() {
	}	
	
	public void prepareToSave() {
		setSelectedIndex( 0 );		
		stopEditing( true );
	}	

	public boolean reload() {
		setSelectedIndex( 0 );
		SwingUtilities.invokeLater(
				new Runnable() {
					public void run() {
						container.reload();
					}
				} );
		return true;
	}	

	public XMLContainer getMainContainer() {
		// Switch to the editor
		return container;
	}
	
	public BookmarkContext getBookmarkContext() {
		return null;
	}	

	public XMLContainer getSelectedContainer() {
		return null;
	}
	
	public XMLContainer getSubContainerAt(int index) {
		return null;
	}
	
	public int getSubContainerCount() {
		return 0;
	}

	public void selectSubContainer(IXMLPanel panel) {
	}	

	public Iterator getProperties() {
		return container.getProperties();
	}

	public Object getProperty(String name, Object def) {
		return container.getProperty( name, def );
	}

	public Object getProperty(String name) {
		return container.getProperty( name );
	}

	public XMLContainer getSubContainer(String type) {
		return container.getSubContainer( type );
	}

	public JComponent getView() {
		return this;
	}

	public void setAutoDisposeMode(boolean disposeMode) {
		container.setAutoDisposeMode( disposeMode );
	}

	public void setProperty(String name, Object content) {
		container.setProperty( name, content );
	}

	class SelectedElementJob extends BasicJob {
		private String xpath;
		SelectedElementJob( String xpath ) {
			this.xpath = xpath;
		}
		public void dispose() {
		}
		public Object getSource() {
			return this;
		}
		public boolean isAlone() {
			return false;
		}		
		public void run() {
			FPNode root = container.getRootNode();
			if ( root != null ) {
				FPNode result = root.getNodeForXPathLocation( xpath, true );
				if ( result != null ) {
					try {
						container.getEditor().requestFocus();
						container.getEditor().setCaretPosition( result.getStartingOffset() + 1 );
					} catch( Throwable th ) {}
				}
			}
		}
		public void stopIt() {
		}
	}

	public static void main( String[] args ) {
		JFrame f = new JFrame();
		f.setSize( 400, 400 );
		ApplicationModel.SHORT_APPNAME = "editix";
		Preferences.loadPreferences();
		XSDEditor container = new XSDEditor( new SingleFactoryImpl() );
		f.getContentPane().add( container.getView() );
		container.getMainContainer().setText( "<xs:schema xmlns:xs='http://www.w3.org/2001/XMLSchema'></xs:schema>" );
		f.setVisible( true );
	}

}
