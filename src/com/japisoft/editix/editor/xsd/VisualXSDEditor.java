package com.japisoft.editix.editor.xsd;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.io.File;


import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.japisoft.editix.editor.xsd.toolkit.SchemaHelper;
import com.japisoft.editix.editor.xsd.view.AnnotationViewImpl;
import com.japisoft.editix.editor.xsd.view.XSDSelectionListener;
import com.japisoft.editix.editor.xsd.view2.DesignerViewImpl;

import com.japisoft.editix.editor.xsd.view.MainTableViewImpl;
import com.japisoft.editix.editor.xsd.view.element.ElementViewImpl;
import com.japisoft.editix.editor.xsd.view.element.PropertiesViewListener;
import com.japisoft.framework.dockable.InnerWindowProperties;
import com.japisoft.framework.dockable.JDock;


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
public class VisualXSDEditor extends JDock 
		implements 
		ListSelectionListener, 
		XSDSelectionListener, 
		PropertiesViewListener {
	private static final String DESIGNER_MODE = "designer";
	private static final String TABLE_MODE = "table";
	private MainTableViewImpl tv = null;
	private ElementViewImpl ev = null;
	private AnnotationViewImpl av = new AnnotationViewImpl();
	private DesignerViewImpl dv = null;
	
	private Factory factory = null;
	private JPanel mainPanel = new JPanel();

	private CopyAction ca = new CopyAction();
	private CutAction cua = new CutAction();
	private PasteAction pa = new PasteAction();
	
	public VisualXSDEditor( Factory factory ) {
		this.factory = factory;	
		dv = new DesignerViewImpl( factory );
		tv = new MainTableViewImpl( factory );
		ev = new ElementViewImpl( factory, this );
		JScrollPane spv = new JScrollPane( tv.getView() );

		mainPanel.setLayout( new CardLayout() );
		mainPanel.add( spv, TABLE_MODE );
		
		JPanel panelDesigner = new JPanel();
		JToolBar tb = new JToolBar();
		tb.add( new BackToTableAction() );
		tb.addSeparator();
		tb.add( ca );
		tb.add( cua );
		tb.add( pa );
		tb.addSeparator();
		tb.add( new ExportImageAction() );
		ca.setEnabled( false );
		cua.setEnabled( false );
		pa.setEnabled( false );
		
		panelDesigner.setLayout( new BorderLayout() );
		panelDesigner.add(
				tb,
				BorderLayout.NORTH );
		panelDesigner.add( 
				new JScrollPane( dv ), 
				BorderLayout.CENTER );
		mainPanel.add( panelDesigner, DESIGNER_MODE );

		tv.setCommonSelectionListener( this );
		dv.setCommonSelectionListener( this );

		addInnerWindow(
				new InnerWindowProperties(
						"1",
						"Schema definitions",
						null,
						null,
						mainPanel ), BorderLayout.CENTER );

		JComponent sev = ev.getView();
		sev.setPreferredSize( new Dimension( 200, 0 ) );		

		addInnerWindow(
				new InnerWindowProperties(
						"2",
						"Properties",
						null,
						null,
						sev ), BorderLayout.EAST );

		JScrollPane spd = new JScrollPane( av.getView() );
		spd.setPreferredSize( new Dimension( 0, 50 ) );

		addInnerWindow(
				new InnerWindowProperties(
						"3",
						"Documentation (Text only)",
						null,
						null,
						spd ), BorderLayout.SOUTH );
		
		getView().getActionMap().put( 
			"cut", 
			cua 
		);

		getView().getInputMap( JComponent.WHEN_IN_FOCUSED_WINDOW ).put(
			KeyStroke.getKeyStroke( KeyEvent.VK_DELETE, 0 ), 
			"cut" 
		);

	}

	private CustomActionListener actionListener = null;
	
	public void setCustomActions( CustomActionListener ca ) {
		this.actionListener = ca;
	}

	public Action getCopyAction() { return ca; }
	public Action getCutAction() { return cua; }
	public Action getPasteAction() { return pa; }

	public void copy() {
		if ( ev.isDesignerMode() ) {
			if ( ev.isDesignerMode() ) {
				dv.copy();
			}
		}			
	}
	
	public void cut() {
		if ( ev.isDesignerMode() ) {
			dv.cut();
		}
	}
	
	public void paste() {
		if ( ev.isDesignerMode() ) {
			dv.paste();
		}
	}
	
	public Object print() {
		if ( dv.getGraphics() != null )
			return dv;
		else
			return tv;
	}

	public void resetAttribute(String name, String value) {
		if ( 
				"name".equals( name ) || 
				"ref".equals( name ) ||
				"minOccurs".equals( name ) ||
				"maxOccurs".equals( name ) ||
				"use".equals( name )
		) {
			dv.repaintSelection();
		}
	}

	public void closeDesigner() {
		cleanHistory();
		( ( CardLayout )mainPanel.getLayout() ).show( mainPanel, TABLE_MODE );
		ev.setDesignerMode( false );
		tv.select( selectedElement );
	}

	public void openDesigner(Element e) {
		dv.init( e );
		( ( CardLayout )mainPanel.getLayout() ).show( 
				mainPanel,
				DESIGNER_MODE );
		ev.setDesignerMode( true );
		// Reforce selection for properties
		select( e );
		/*
		resetStackActionsState();
		SwingUtilities.invokeLater(
			new Runnable() {
				public void run() {
					mainPanel.repaint();
					dv.invalidate();
					dv.revalidate();
					dv.repaint();
				}
			} );*/
	}

	private void resetStackActionsState() {
	}

	private void cleanHistory() {
		resetStackActionsState();		
	}

	public void select(Element e) {
		// Selection from the designer
		notifyCurrentElement( e );
		ca.setEnabled( true );
		cua.setEnabled( true );
	}

	private Element selectedElement = null;

	public Element getSelectedElement() {
		return selectedElement;
	}
	
	public DesignerViewImpl getDesignerView() {
		return dv;
	}

	public boolean delete(Element e) {
		return false;
	}

	protected void setUIReady( boolean added ) {
		if ( added ) {
			tv.getSelectionModel().addListSelectionListener( this );
			tv.getSelectionModel().setSelectionInterval( 0, 0 );
		} else {
			tv.getSelectionModel().removeListSelectionListener( this );
		}
	}

	private Element schemaRoot = null;

	public void init( Element schemaRoot ) {
		
		SchemaHelper.resolveIncludeRedefineImport( ( String )schemaRoot.getUserData( "source" ), schemaRoot );
		
		this.schemaRoot = schemaRoot;
		tv.init( schemaRoot );
		closeDesigner();
	}

	void select( String xpathExpression ) {
		if ( schemaRoot != null ) {
			Document d = schemaRoot.getOwnerDocument();
			Element selectedElement = SchemaHelper.resolveXPathExpression( xpathExpression, d );
			if ( selectedElement != null ) {
				Node ancestor = selectedElement;
				while ( ancestor != null ) {
					String name = ancestor.getLocalName();
					if ( "schema".equals( name ) ) {
						// Select this element inside the table
						tv.select( selectedElement );
						break;
					} else 
					if ( "complexType".equals( name ) ) {
						selectedElement = ( Element )ancestor;
						openDesigner( selectedElement );
						break;
					} else 
					if ( "element".equals( name ) ) {
						selectedElement = ( Element )ancestor;
					}
					ancestor = ancestor.getParentNode();
				}
			}
		}
	}

	public void valueChanged(ListSelectionEvent e) {
		int selection = tv.getSelectedRow();
		boolean found = false;
		if ( selection > -1 && 
				schemaRoot != null ) {
			ev.stopEditing();
			NodeList children = schemaRoot.getChildNodes();
			for ( int i = 0; i < children.getLength(); i++ ) {
				if ( children.item( i ) instanceof Element ) {
					if ( selection == 0 ) {
						// Notify to all the XSDView
						notifyCurrentElement( ( Element )children.item( i ) );
						found = true;
						break;
					}
					selection--;
				}
			}
		}
		if ( !found )
			notifyCurrentElement( null );
	}
	
	private void notifyCurrentElement( Element e ) {
		ev.init( e );
		av.init( e );
		this.selectedElement = e;
	}

	public void stopEditing() {
		tv.stopEditing();
		ev.stopEditing();
		this.selectedElement = null;
	}

	public void dispose() {
		schemaRoot = null;
		selectedElement = null;
		ev.dispose();
		av.dispose();
		tv.dispose();
		dv.dispose();
		ev = null;
		av = null;
		tv = null;
		super.dispose();
	}

	class BackToTableAction extends AbstractAction {
		public BackToTableAction() {
			putValue( Action.SHORT_DESCRIPTION, "Back to the main table" );
			putValue( Action.SMALL_ICON,
					new ImageIcon( VisualXSDEditor.class.getResource( "table.png" ) ) );
		}

		public void actionPerformed(ActionEvent e) {
			closeDesigner();
		}
	}
		
	class CopyAction extends AbstractAction {
		CopyAction() {
			putValue( Action.SHORT_DESCRIPTION, "Copy the selected element" );
			putValue( Action.SMALL_ICON,
				new ImageIcon( VisualXSDEditor.class.getResource( "copy.png" ) ) );
			putValue( 
					Action.ACCELERATOR_KEY, 
					KeyStroke.getAWTKeyStroke( KeyEvent.VK_C, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask() ) );
			putValue(
					Action.NAME,
					"Copy the XSD part" );
		}
		public void actionPerformed(ActionEvent e) {
			dv.copy();
			pa.setEnabled( true );
		}	
	}

	class CutAction extends AbstractAction {
		CutAction() {
			putValue( Action.SHORT_DESCRIPTION, "Cut the selected element" );
			putValue( Action.SMALL_ICON,
				new ImageIcon( VisualXSDEditor.class.getResource( "cut.png" ) ) );
			putValue( 
					Action.ACCELERATOR_KEY, 
					KeyStroke.getAWTKeyStroke( KeyEvent.VK_X, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask() ) );
			putValue(
					Action.NAME,
					"Cut the XSD part" );			
		}
		public void actionPerformed(ActionEvent e) {
			dv.cut();
			pa.setEnabled( true );
		}	
	}

	class PasteAction extends AbstractAction {
		PasteAction() {
			putValue( Action.SHORT_DESCRIPTION, "Paste the last element cut or copy" );
			putValue( Action.SMALL_ICON,
				new ImageIcon( VisualXSDEditor.class.getResource( "paste.png" ) ) );
			putValue( 
					Action.ACCELERATOR_KEY, 
					KeyStroke.getAWTKeyStroke( KeyEvent.VK_V, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask() ) );
			putValue(
					Action.NAME,
					"Paste the XSD part" );
		}
		public void actionPerformed(ActionEvent e) {
			dv.paste();
		}	
	}
	
	class ExportImageAction extends AbstractAction {
		public ExportImageAction() {
			putValue( Action.SHORT_DESCRIPTION, "Export this diagram to an external image" );
			putValue( Action.SMALL_ICON,
				new ImageIcon( VisualXSDEditor.class.getResource( "photo_scenery.png" ) ) );
		}
		public void actionPerformed(ActionEvent e) {
			if ( actionListener != null ) {
				actionListener.action( 
					"exportimage", 
					VisualXSDEditor.this );
			}
		}
	}

	public static void main( String[] args ) throws Exception {
		File debug = new File( "D:/travail/soft/japisoft-editix-2015/test/debug/simple.xsd" );

		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setNamespaceAware( true );
		DocumentBuilder db = dbf.newDocumentBuilder();
		
		Document doc = db.parse( debug );
		Element root = doc.getDocumentElement();
		
		VisualXSDEditor vxe = new VisualXSDEditor( null );
		JFrame f = new JFrame();
		f.add( vxe.getView() );
		
		root.setUserData( "source", debug.toString(), null );
		
		vxe.init( root );
		f.setSize( 300, 300 );
		f.setVisible( true );
	}
	
}
