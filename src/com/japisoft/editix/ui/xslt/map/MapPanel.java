package com.japisoft.editix.ui.xslt.map;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JToolBar;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreePath;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.japisoft.editix.ui.xslt.XSLTFiles;
import com.japisoft.framework.ui.GradientPanel;

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
public class MapPanel extends JPanel implements ActionListener, TreeSelectionListener, ElementTableListener {

	public static final String HTTP_WWW_W3_ORG_1999_XSL_TRANSFORM = "http://www.w3.org/1999/XSL/Transform";

	private JTree xsltTree = null;
	private JTree outTree = null;
	
	private ElementTable xsltAttributes = null;
	private ElementTable outAttributes = null;

	private DrawLinkPanel dlPanel = null;
	private JComboBox cb = null;
	
	private XSLTFiles files = null;
	
	public MapPanel( XSLTFiles files ) {
		
		this.files = files;
		
		setLayout( new BorderLayout() );
		add( cb = new JComboBox(), BorderLayout.NORTH );

		if ( files != null ) // For testing case
		for ( int i = 0; i < files.getXMLContainerCount(); i++ ) {
			cb.addItem( 
				files.getXMLContainer( i ).getCurrentDocumentLocation()
			);
		}

		JComponent tmp = null;		
		GradientPanel gp = new GradientPanel( 
				"XSLT Tree",  
				new ToolbarPanel( 
					new Action[] { 
							new AddAction( true ), 
							new RemoveAction( true ) },
					new JScrollPane( xsltTree = new LinkTree() ),
					xsltAttributes = new ElementTable()					
				)
		);
		add( tmp = gp, BorderLayout.WEST );
		tmp.setPreferredSize( new Dimension( 300, 0 ) );
		gp = new GradientPanel( 
				"Output Tree",
				new ToolbarPanel(
					new Action[] { 
							new AddAction( false ), 
							new RemoveAction( false ) },
					new JScrollPane( outTree = new LinkTree() ),
					outAttributes = new ElementTable()
				)
		);
		add( tmp = gp, BorderLayout.EAST );
		tmp.setPreferredSize( new Dimension( 300, 0 ) );

		add( 
			new GradientPanel( "Mapping", dlPanel = new DrawLinkPanel( xsltTree, outTree ) ), 
			BorderLayout.CENTER 
		);

		TreeRenderer tr = new TreeRenderer();
		xsltTree.setCellRenderer( tr );
		outTree.setCellRenderer( tr );
	}
		
	public void dispose() {
		dlPanel.dispose();
	}
	
	@Override
	public void addNotify() {
		super.addNotify();
		cb.addActionListener( this );
		if ( cb.getItemCount() > 0 )
			cb.setSelectedIndex( 0 );
		xsltTree.addTreeSelectionListener( this );
		outTree.addTreeSelectionListener( this );
		xsltAttributes.setElementTableListener( this );
		outAttributes.setElementTableListener( this );
	}

	@Override
	public void removeNotify() {
		super.removeNotify();
		cb.removeActionListener( this );
		xsltTree.removeTreeSelectionListener( this );
		outTree.removeTreeSelectionListener( this );
		xsltAttributes.setElementTableListener( null );
		outAttributes.setElementTableListener( null );		
	}

	// ElementTable event
	public void tableUpdated() {
		updateXMLContainer();
	}

	public void valueChanged( TreeSelectionEvent e ) {
		TreePath tp = e.getPath();
		if ( tp != null ) {
			if ( e.getSource() == xsltTree ) {
				xsltAttributes.setElement( 
					( Element )( ( VirtualDomNode )tp.getLastPathComponent() ).getSource() 
				);
			} else {
				outAttributes.setElement( 
					( Element )( ( VirtualDomNode )tp.getLastPathComponent() ).getSource() 
				);				
			}
		}
	}

	public void actionPerformed( ActionEvent e ) {
		
		try {			
			Element root = files.getXMLContainerDOMRootNode( Math.max( 0, cb.getSelectedIndex() ) );
			setDocumentElement( root );
			expandAll();
			xsltTree.setSelectionRow( 0 );
			outTree.setSelectionRow( 0 );
		} catch( Exception exc ) {
			
		}

	}

	private void expandAll( ) {
		for (int i = xsltTree.getRowCount() - 1; i >= 0; i--) {
			xsltTree.expandRow( i );
		}		
	}

	void setDocumentElement( Element root ) throws Exception {
		SAXParserFactory spf = SAXParserFactory.newInstance();
		spf.setNamespaceAware( true );
		SAXParser sp = spf.newSAXParser();
		
		TransformerFactory tf = TransformerFactory.newInstance();
		Transformer t = tf.newTransformer();

		// XSLT element
		NamespaceSplitterSaxHandler nss = null;
		t.transform( new DOMSource( root ), new SAXResult( nss = new NamespaceSplitterSaxHandler( 
				HTTP_WWW_W3_ORG_1999_XSL_TRANSFORM, 
				false, 
				root ) ) );
		List<VirtualDomNode> l = nss.getAll();
		VirtualDomNode vdm = nss.getRoot();
		VirtualDomNodeModel model = new VirtualDomNodeModel( vdm );
		xsltTree.setModel( model );

		// Non XSLT element
		NamespaceSplitterSaxHandler nss2 = null;
		t.transform( new DOMSource( root ), new SAXResult( nss2 = new NamespaceSplitterSaxHandler( 
				HTTP_WWW_W3_ORG_1999_XSL_TRANSFORM, 
				true, 
				root ) ) );

		VirtualDomNode vdm2 = nss2.getRoot();
		List<VirtualDomNode> l2 = nss2.getAll();
		VirtualDomNodeModel model2 = new VirtualDomNodeModel( vdm2 );
		outTree.setModel( model2 );
	}

	VirtualDomNode getSelectedNode( boolean xslt ) {
		if ( xslt ) {
			
			if ( xsltTree.getSelectionPath() == null )
				return null;
			
			return ( VirtualDomNode )xsltTree.getSelectionPath().getLastPathComponent();
		}
		
		if ( outTree.getSelectionPath() == null )
			return null;

		return ( VirtualDomNode )outTree.getSelectionPath().getLastPathComponent();
	}
	
	void updateXMLContainer() {
		VirtualDomNode dmn = ( VirtualDomNode )xsltTree.getModel().getRoot();
		Element root = ( Element )dmn.getSource();
		int index = Math.max( 0, cb.getSelectedIndex() );
		files.setXMLContainerDOMRootNode( index, root );
	}
	
	public void updateContent() {
		actionPerformed( null );
	}
	
	Element getRoot() {
		return ( Element )( ( VirtualDomNode )xsltTree.getModel().getRoot() ).getSource();
	}
	
	class AddAction extends AbstractAction {
		private boolean xslt;
		
		public AddAction( boolean xslt ) {
			this.xslt = xslt;
			putValue( 
				Action.SMALL_ICON, 
				new ImageIcon( 
					XSLTFiles.class.getResource( "element_new_after.png" )
				)
			);
		}
		public void actionPerformed(ActionEvent e) {
			VirtualDomNode node = getSelectedNode(
				this.xslt
			);
			if ( node == null ) {
				JOptionPane.showMessageDialog( 
					MapPanel.this, 
					"Please select a node for this operation" 
				);
			} else {
				String name = JOptionPane.showInputDialog( "Choose an element name" );
				if ( name != null ) {
					Element ce = ( Element )node.getSource();
					Element ne = null;
					if ( this.xslt ) {
						ne = ce.getOwnerDocument().createElementNS(
							HTTP_WWW_W3_ORG_1999_XSL_TRANSFORM,
							"xsl:" + name
						);	
					} else {
						ne = ce.getOwnerDocument().createElementNS(
							ce.getNamespaceURI(),
							name
						);						
					}
					ce.appendChild( ne );
					updateXMLContainer();
					try {
						setDocumentElement( getRoot() );	
					} catch( Exception exc ) {
					}
					expandAll();
				}
			}
		}
	}

	class RemoveAction extends AbstractAction {
		private boolean xslt;

		public RemoveAction( boolean xslt ) {
			this.xslt = xslt;
			putValue(
				Action.SMALL_ICON,
				new ImageIcon(
					XSLTFiles.class.getResource( "element_delete.png" )
				)
			);
		}
		public void actionPerformed(ActionEvent e) {
			VirtualDomNode node = getSelectedNode(
				this.xslt
			);
			if ( node == null ) {
				JOptionPane.showMessageDialog( 
					MapPanel.this, 
					"Please select a node for this operation" 
				);
			} else {					
				VirtualDomNode parent = node.getParent();
				if ( parent == null ) {
					JOptionPane.showMessageDialog( 
						MapPanel.this, 
						"You can't remove this node" 
					);
				} else
					if ( JOptionPane.showConfirmDialog( 
						xsltTree, 
						"Delete this node ? " ) == JOptionPane.YES_OPTION ) {
						parent.removeChild( node );
						updateXMLContainer();
						try {
							setDocumentElement( getRoot() );	
						} catch( Exception exc ) {
						}
						expandAll();
					}
			}
		}
	}

	class LinkTree extends JTree {
		@Override
		protected void paintComponent( Graphics g ) {
			super.paintComponent( g );
			dlPanel.repaint();
		}
	}
	
	class ToolbarPanel extends JPanel {
		public ToolbarPanel( Action[] actions, JComponent content, JTable attributes ) {
			setLayout( new BorderLayout() );
			JToolBar tb = new JToolBar();
			tb.setFloatable( false );
			for ( Action a : actions ) {
				tb.add( a );
			}
			add( tb, BorderLayout.NORTH );
			add( content, BorderLayout.CENTER );

			JScrollPane sp = new JScrollPane( attributes );
			sp.setPreferredSize( new Dimension( 0, 200 ) );
			add( sp, BorderLayout.SOUTH );
		}
	}

	public static void main( String[] args ) throws Exception {

		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setNamespaceAware( true );

		Document doc = dbf.newDocumentBuilder().parse( 
			new File( "C:/travail/tmp/test.xsl" ) 
		);

		doc.getDocumentElement().setUserData( "test", "test", null );
		
		JFrame f = new JFrame();
		f.setSize( 400, 400 );
		
		MapPanel mp = new MapPanel( null );
		mp.setDocumentElement( doc.getDocumentElement() );
		f.getContentPane().add( mp );
		f.setVisible( true );
		
	}
	
}
