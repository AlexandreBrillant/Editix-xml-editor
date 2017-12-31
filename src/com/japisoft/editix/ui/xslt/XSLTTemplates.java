package com.japisoft.editix.ui.xslt;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JToolBar;
import javax.swing.ListSelectionModel;

import javax.swing.TransferHandler;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.japisoft.framework.preferences.Preferences;
import com.japisoft.framework.ui.table.ExportableTable;
import com.japisoft.framework.xml.parser.node.FPNode;
import com.japisoft.xmlpad.XMLContainer;
import com.japisoft.xmlpad.editor.XMLPadDocument;

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
public class XSLTTemplates extends JPanel 
		implements
			ListSelectionListener,
			ActionListener,
			MouseListener, 
			MouseMotionListener {

	private static final String INSERT_TEMPLATE_BEFORE = "insert-template-before";
	private static final String INSERT_TEMPLATE_AFTER = "insert-template-after";
	private static final String COPY_TEMPLATE = "copy-template";
	private static final String CUT_TEMPLATE = "cut-template";
	private static final String PASTE_TEMPLATE = "paste-template";

	private JTable table;
	private XSLTFiles files;
	
	private Action ntemplateBefore = null;
	private Action ntemplateAfter = null;
	private Action ntemplateCopy = null;
	private Action ntemplateCut = null;
	private Action ntemplatePaste = null;

	XSLTTemplates( XSLTFiles files ) {
		this.files = files;
		setLayout( 
			new BorderLayout() 
		);
		buildTb();
		add( new JScrollPane( table = new ExportableTable() ) );
		table.setTransferHandler( 
			new TemplateDrag() 
		);
	}

	private void buildTb() {
		ntemplateAfter = new NewTemplateAfter();
		ntemplateBefore = new NewTemplateBefore();
		ntemplateCopy = new CopyTemplate();
		ntemplateCut = new CutTemplate();
		ntemplatePaste = new PasteTemplate();

		ntemplateAfter.setEnabled( false );
		ntemplateBefore.setEnabled( false );
		ntemplateCopy.setEnabled( false );
		ntemplateCut.setEnabled( false );
		ntemplatePaste.setEnabled( false );

		JToolBar tb = new JToolBar();
		tb.add( ntemplateBefore );
		tb.add( ntemplateAfter );
		tb.addSeparator();
		tb.add( ntemplateCopy );
		tb.add( ntemplateCut );
		tb.add( ntemplatePaste );
		add( tb, BorderLayout.NORTH );
	}

	public void addNotify() {
		super.addNotify();
		table.addMouseListener( this );
		table.addMouseMotionListener( this );
		table.getSelectionModel().addListSelectionListener( this );
	}
	
	public void removeNotify() {
		super.removeNotify();
		table.removeMouseListener( this );
		table.removeMouseMotionListener( this );
		table.getSelectionModel().removeListSelectionListener( this );
	}
	
	public void valueChanged( ListSelectionEvent e ) {
		ntemplateAfter.setEnabled( table.getSelectedRow() > -1 );
		ntemplateBefore.setEnabled( table.getSelectedRow() > -1 );
		ntemplateCopy.setEnabled( table.getSelectedRow() > -1 );
		ntemplateCut.setEnabled( table.getSelectedRow() > -1 );
	}

	public void updateContent() {
		dispose();
		table.setModel( new CustomTableModel() );
		table.getColumnModel().getColumn( 0 ).setCellRenderer( new EditRenderer() );
		CustomRenderer renderer = new CustomRenderer();
		table.getColumnModel().getColumn( 1 ).setCellRenderer( 
				renderer );
		table.getColumnModel().getColumn( 2 ).setCellRenderer( 
				renderer );
		table.getColumnModel().getColumn( 3 ).setCellRenderer( 
				renderer );
		table.getColumnModel().getColumn( 4 ).setCellRenderer( 
				renderer );
		table.getColumnModel().getColumn( 5 ).setCellRenderer( 
				renderer );		
		table.getSelectionModel().setSelectionMode( 
			ListSelectionModel.SINGLE_SELECTION 
		);
		table.getColumnModel().getColumn( 0 ).setMaxWidth(16);
	}

	public void dispose() {
		if ( table.getModel() instanceof CustomTableModel ) {
			( ( CustomTableModel )table.getModel() ).dispose(); 
		}
	}
	
	public void mouseClicked( MouseEvent e ) {
		int col = table.columnAtPoint( 
				e.getPoint() );
		if ( col == 0 || e.getClickCount() > 1 ) {
		int row = table.rowAtPoint( 
				e.getPoint() );
			if ( row > -1 ) {				
				if ( table.getModel() instanceof CustomTableModel ) {
					CustomTableModel ctm = 
						( CustomTableModel )table.getModel();
					Templates t = 
						ctm.getTemplates( row );
					FPNode node = 
						ctm.getNode( row );					
					files.selectNode( t.source, node );
				}
			}
		}
	}

	public void mousePressed( MouseEvent e ) {
		maybeShowPopup( e );
	}

	public void mouseReleased( MouseEvent e ) {
		maybeShowPopup( e );		
	}

	private void insertTemplate( boolean before, org.w3c.dom.Element template, org.w3c.dom.Element node ) {
				
		org.w3c.dom.Element parent = ( org.w3c.dom.Element )node.getParentNode();
		if ( before ) {
			parent.insertBefore( template, node );
			parent.insertBefore( node.getOwnerDocument().createTextNode( "\n" ), node );
		} else {
			Node sibling = node.getNextSibling();
			boolean added = false;
			if ( sibling != null ) {
				if ( sibling.getParentNode() == parent ) {
					parent.insertBefore( template, sibling );
					parent.insertBefore( node.getOwnerDocument().createTextNode( "\n" ), sibling );						
					added = true;
				}
			}
			if ( !added ) {
				parent.appendChild( template );
				parent.appendChild( node.getOwnerDocument().createTextNode( "\n" ) );
			}
		}		
	}

	private int lastPopupRow = -1;
	private org.w3c.dom.Node copyNode = null;

	public void actionPerformed( ActionEvent e ) {
		
		boolean refreshModel = false;
		
		CustomTableModel model = ( CustomTableModel )table.getModel();
		
		if ( PASTE_TEMPLATE.equals( e.getActionCommand() ) ) {

			// Go to the last line
			if ( lastPopupRow == -1 ) {
				lastPopupRow = model.getRowCount() - 1;
			}

			Templates templates = model.getTemplates( lastPopupRow );
			org.w3c.dom.Element node = model.getDOMNode( lastPopupRow );
			
			org.w3c.dom.Element copyTmp = ( org.w3c.dom.Element )copyNode.cloneNode( true );
			copyTmp = ( org.w3c.dom.Element )node.getOwnerDocument().importNode( copyTmp, true );

			insertTemplate( true, copyTmp, node );
			
			templates.updateDOMStructure( node );
			refreshModel = true;
			
		}

		if ( lastPopupRow == -1 )	// ?
			return;

		if ( COPY_TEMPLATE.equals( e.getActionCommand() ) ) {

			org.w3c.dom.Element node = model.getDOMNode( lastPopupRow );
			copyNode = node.cloneNode( true );
			ntemplatePaste.setEnabled( true );
			
		} else
		if ( CUT_TEMPLATE.equals( e.getActionCommand() ) ) {

			Templates templates = model.getTemplates( lastPopupRow );
			org.w3c.dom.Element node = model.getDOMNode( lastPopupRow );
			copyNode = node.cloneNode( true );
			org.w3c.dom.Element parent = ( org.w3c.dom.Element )node.getParentNode();
			parent.removeChild( node );
			templates.updateDOMStructure( parent );
			refreshModel = true;
			ntemplatePaste.setEnabled( true );
			
		} else
		if ( INSERT_TEMPLATE_AFTER.equals( 
				e.getActionCommand() ) || 
					INSERT_TEMPLATE_BEFORE.equals( e.getActionCommand() ) ) {
			Templates templates = 
				model.getTemplates( lastPopupRow );
			
			XMLContainer source = templates.source;
			org.w3c.dom.Element node = model.getDOMNode( lastPopupRow );
			if ( node == null ) return;

			String p = node.getPrefix();
			if ( p == null )
				p = "";
			else
				p = p + ":";
			
			org.w3c.dom.Element template = node.getOwnerDocument().createElement( 
					p + "template" 
			);

			// Force a content
			template.appendChild( node.getOwnerDocument().createTextNode( " " ) );

			template.setAttribute( 
				"match", 
				"node()" 
			);

			boolean before = e.getActionCommand().endsWith( "before" );

			insertTemplate( before, template, node );

			templates.updateDOMStructure( node );

			refreshModel = true;
		}

		if ( refreshModel ) {
			( ( CustomTableModel )table.getModel() ).refresh();
		}

	}

	private void maybeShowPopup( MouseEvent e ) {
		if ( e.isPopupTrigger() ) {
			lastPopupRow = table.rowAtPoint( e.getPoint() );
			JPopupMenu popup = 
				new JPopupMenu();
			popup.add( ntemplateBefore );
			popup.add( ntemplateAfter );
			popup.addSeparator();
			popup.add( ntemplateCopy );
			popup.add( ntemplateCut );
			popup.add( ntemplatePaste );

			popup.show(
				e.getComponent(),
				e.getX(), 
				e.getY()
			);
		}
	}

	public void mouseEntered(MouseEvent e) {
	}

	public void mouseExited(MouseEvent e) {
	}

	public void mouseDragged(MouseEvent e) {
		table.getTransferHandler().exportAsDrag( table, e, TransferHandler.MOVE );
	}

	public void mouseMoved(MouseEvent e) {
		int row = 
			table.rowAtPoint( 
				e.getPoint() 
			);
		int col =
			table.columnAtPoint( 
				e.getPoint() 
			);
		if ( row > -1 && col > -1 ) {
			Templates t = 
				( ( CustomTableModel )table.getModel() ).getTemplates( row );
			table.setToolTipText(
				t.source.getCurrentDocumentLocation() 
			);
		}
	}

	/////////////////////////////// MODEL /////////////////////////////////////

	class CustomTableModel implements TableModel {

		private List templates = null;

		private CustomTableModel() {
			updateContent();
		}

		private void updateContent() {
			templates = null;
			for ( int i = 0; i < files.getXMLContainerCount(); i++ ) {
				if ( templates == null ) {
					templates = 
						new ArrayList();
				}
				templates.add( 
					new Templates( 
						i,
						files.getXMLContainer( i ), 
						files.getXMLContainerRootNode( i ),
						files.getXMLContainerDOMRootNode( i )
					)
				);
			}			
		}

		void refresh() {
			updateContent();
			if ( l != null )
				l.tableChanged( 
					new TableModelEvent( this ) 
			);
		}

		public void dispose() {
			if ( templates != null ) {
				for ( int i = 0; i < templates.size(); i++ ) {
					( ( Templates )templates.get( i ) ).dispose();
				}
				templates = null;
			}
		}
		
		private TableModelListener l;
		public void addTableModelListener( TableModelListener l ) {
			this.l = l;
		}
		public void removeTableModelListener( TableModelListener l ) {
			this.l = null;
		}
		public Class getColumnClass( int columnIndex ) {
			return String.class;
		}

		/*  edit
			file
			match = pattern 
			name = qname 
			priority = number 
			mode = qname */
		public int getColumnCount() {
			return 6;
		}

		/*  edit
		   	file
			match = pattern 
			name = qname 
			priority = number 
			mode = qname */
		public String getColumnName( int columnIndex ) {
			switch( columnIndex ) {
				case 0: return ">";
				case 1: return "file";
				case 2: return "match";
				case 3: return "mode";			
				case 4: return "name";
				case 5: return "priority";			
			}
			return null;
		}

		public int getRowCount() {
			if ( templates == null )
				return 0;
			int total = 0;
			for ( int i = 0; i < templates.size(); i++ ) {
				total += 
					( ( Templates )templates.get( i ) ).getTemplatesCount();
			}
			return total;
		}

		private FPNode getNode( int rowIndex ) {
			int position = rowIndex;
			for ( int i = 0; i < templates.size(); i++ ) {
				Templates t = 
					( Templates )templates.get( i );
				if ( position >= t.getTemplatesCount() ) {
					position = 
						( position - t.getTemplatesCount() );
				} else {
					return t.getTemplateNode( position );
				}					
			}
			return null;
		}
		
		private org.w3c.dom.Element getDOMNode( int rowIndex ) {
			int position = rowIndex;
			for ( int i = 0; i < templates.size(); i++ ) {
				Templates t = 
					( Templates )templates.get( i );
				if ( position >= t.getTemplatesCount() ) {
					position = 
						( position - t.getTemplatesCount() );
				} else {
					return t.getTemplateDOMNode( position );
				}					
			}
			return null;			
		}

		private Templates getTemplates( int rowIndex ) {
			int position = rowIndex;
			for ( int i = 0; i < templates.size(); i++ ) {
				Templates t = 
					( Templates )templates.get( i );
				if ( position >= t.getTemplatesCount() ) {
					position = 
						( position - t.getTemplatesCount() );
				} else {
					return t;
				}									
			}
			return null;
		}

		public Object getValueAt( int rowIndex, int columnIndex ) {
			org.w3c.dom.Element node = 
				getDOMNode( rowIndex );
			if ( node == null ) {
				// ??
				return null;
			}
			switch( columnIndex ) {
				case 0: return null;
				case 1: Templates t = 
							getTemplates(rowIndex);
						String location = t.source.getCurrentDocumentLocation();
						if ( location == null )
							return null;
						int i = location.lastIndexOf( "/" );
						if ( i == -1 )
							i = location.lastIndexOf( "\\" );
						if ( i == -1 )
							return location;
						return location.substring( i + 1 );
				case 2: return node.getAttribute( "match" );
				case 3: return node.getAttribute( "mode" );			
				case 4: return node.getAttribute( "name" );
				case 5: return node.getAttribute( "priority" );			
			}
			return null;
		}

		public boolean isCellEditable( int rowIndex, int columnIndex ) {
			if ( columnIndex == 0 || 
					columnIndex == 1 )
				return false;
			return true;
		}

		public void setValueAt( Object value, int rowIndex, int columnIndex ) {
			org.w3c.dom.Element node = getDOMNode( rowIndex );
			if ( node != null ) {
				String attributeName = null;
				switch( columnIndex ) {
					case 2: attributeName = "match";break;
					case 3: attributeName = "mode";break;			
					case 4: attributeName = "name";break;
					case 5: attributeName = "priority";break;			
				}
				if ( attributeName != null ) {
					Templates templates = 
						getTemplates( rowIndex );
					if ( templates != null ) {
						XMLContainer container = 
							templates.source;
						XMLPadDocument document = 
							container.getXMLDocument();
						if ( !"".equals( value ) ) {
							node.setAttribute( 
								attributeName, 
								( String )value 
							);
						} else {
							node.removeAttribute( attributeName );
						}

						templates.updateDOMStructure( node );
					}
				}
			}
		}
	}

	class Templates {
		XMLContainer source;
		List templates;
		List domTemplates;

		Color color;
		
		Templates(
				int location,
				XMLContainer source, 
				FPNode root,
				org.w3c.dom.Element domRoot ) {			
			this.source = source;
			if ( location % 2 == 0 ) {
				color = Color.WHITE;
			} else {
				color = Preferences.getPreference( 
						"interface", 
						"table-color-even", 
						new Color( Integer.parseInt( "c8eaa5", 16 ) ) 
				);
			}
			if ( root != null ) {
				for ( int i = 0; i < root.childCount(); i++ ) {
					FPNode child = 
						root.childAt( i );
					if ( child.matchContent( "template" ) ) {
						if ( templates == null )
							templates = new ArrayList();
						templates.add( child );
					}
				}
			}
			if ( domRoot != null ) {
				NodeList nl = domRoot.getChildNodes();
				for ( int i = 0; i < nl.getLength(); i++ ) {
					Node n = nl.item( i );
					if ( n instanceof org.w3c.dom.Element ) {
						org.w3c.dom.Element e = ( org.w3c.dom.Element )n;
						boolean ok = "template".equals( e.getLocalName() );
						if ( !ok ) {
							ok = ( e.getNodeName() != null && e.getNodeName().endsWith( ":template" ) );
						}
						if ( ok ) {
							if ( domTemplates == null )
								domTemplates = new ArrayList();
							domTemplates.add( e );
						}
					}
				}
			}
		}

		int getTemplatesCount() {
			if ( domTemplates == null ) {
				return 0;
			}
			return domTemplates.size();
		}

		org.w3c.dom.Element getTemplateDOMNode( int index ) {
			if ( domTemplates == null ) {
				return null;
			}
			if ( domTemplates.size() > index )
				return ( org.w3c.dom.Element )domTemplates.get( index );
			
			return null;
		}

		FPNode getTemplateNode( int index ) {
			if ( templates == null )
				return null;
			if ( templates.size() > index )
				return ( FPNode )templates.get( index );

			return null;
		}
		
		void updateDOMStructure( org.w3c.dom.Element node ) {
			// Store the result
			try {
				Transformer t = 
					TransformerFactory.newInstance().newTransformer();
				StringWriter sw = new StringWriter();
				t.transform( 
					new DOMSource( node.getOwnerDocument() ), 
					new StreamResult( sw ) 
				);
				source.setText( sw.toString() );
			} catch( Exception exc ) {
			}			
		}

		void dispose() {
			source = null;
			templates = null;
		}
	}

	////////////////////////////////////////////////////////////////////////////////////:
	
	class TemplateDrag extends TransferHandler {

		public boolean canImport(JComponent comp, DataFlavor[] transferFlavors) {
			boolean ok = false;
			for ( int i = 0;i < transferFlavors.length; i++ ) {
				DataFlavor fd = transferFlavors[ i ];
				if ( fd.equals( DataFlavor.stringFlavor ) ) {
					ok = true;
					break;
				}
			}
			return ok;
		}

		protected Transferable createTransferable(JComponent c) {
			return new StringSelection( "row:" + table.getSelectedRow() );
		}

		public int getSourceActions( JComponent c ) {
			return TransferHandler.MOVE;
		}

		public boolean importData(JComponent comp, Transferable t) {
			try {
				String str = 
					( String )t.getTransferData( DataFlavor.stringFlavor );
				if ( str != null && 
						str.startsWith( "row:" ) ) {
					int i = str.lastIndexOf( ":" );
					int rowToMove = Integer.parseInt( 
							str.substring( i + 1 ) 
					);
					int rowTarget = table.getSelectedRow();
					
					if ( rowToMove != rowTarget ) {
						if ( rowToMove >= 0 && rowTarget >= 0 ) {
							CustomTableModel m = ( CustomTableModel )table.getModel();
							Templates tSource = m.getTemplates( rowToMove );
							org.w3c.dom.Element nSource = m.getDOMNode( rowToMove );
							org.w3c.dom.Element nSourceParent = ( org.w3c.dom.Element )nSource.getParentNode();
							nSourceParent.removeChild( nSource );

							Templates tTarget = m.getTemplates( rowTarget );
							org.w3c.dom.Element nTarget = m.getDOMNode( rowTarget );
							org.w3c.dom.Element nTargetParent = ( org.w3c.dom.Element )nTarget.getParentNode();
							nSource = ( org.w3c.dom.Element )nTargetParent.getOwnerDocument().importNode( nSource, true );
							
							insertTemplate( true, nSource, nTarget );

							if ( tSource != tTarget ) {
								tSource.updateDOMStructure( nSourceParent );
							}

							tTarget.updateDOMStructure( nTarget );
							m.refresh();
						}
					}
					return true;
				}
			} catch ( UnsupportedFlavorException e ) {
			} catch ( IOException e ) {
			}
            return false;
		}

	}
	
	class EditRenderer extends JLabel implements TableCellRenderer {
		
		public EditRenderer() {
			setIcon( 
					new ImageIcon( 
							XSLTTemplates.class.getResource( "document_edit.png" ) ) 
			);
		}
		public Component getTableCellRendererComponent(
				JTable table,
				Object value, 
				boolean isSelected, 
				boolean hasFocus, 
				int row,
				int column ) {
			return this;
		}
	}
	
	class CustomRenderer extends JLabel implements TableCellRenderer {

		private Font boldFont = null;
		private Font plainFont = null;
		
		public CustomRenderer() {
			setOpaque( true );
			boldFont = ( plainFont = getFont() ).deriveFont( Font.BOLD );
			setForeground( Color.DARK_GRAY );
		}

		public Component getTableCellRendererComponent(
				JTable table,
				Object value, 
				boolean isSelected, 
				boolean hasFocus, 
				int row,
				int column ) {
			
			if ( table.getModel() instanceof CustomTableModel ) {
				CustomTableModel model = 
					( CustomTableModel )table.getModel();
				Templates t = model.getTemplates( row );
				if ( !isSelected ) {
					setBackground( 
						t.color 
					);
				} else
					setBackground( 
						table.getSelectionBackground() 
					);
			}
			if ( !isSelected ) {
				setForeground( Color.BLACK );
			} else {
				setForeground( 
					table.getSelectionForeground() 
				);
			}
			
			if ( column == 1 ) {
				setFont( boldFont );
			} else
				setFont( plainFont );
			
			if ( value != null )
				setText( value.toString() );
			else
				setText( "" );
			
			return this;
		}
	}

	// ------------------------------------------------------------------------------

	class CopyTemplate extends AbstractAction {
		public CopyTemplate() {
			putValue( Action.NAME, "Copy this template" );
			putValue( Action.SHORT_DESCRIPTION, "Copy the current template" );
			putValue( Action.SMALL_ICON, new ImageIcon( getClass().getResource( "copy.png" ) ) );
		}
		public void actionPerformed(ActionEvent e) {
			lastPopupRow = table.getSelectedRow();
			XSLTTemplates.this.actionPerformed( 
				new ActionEvent( this, e.getID(), XSLTTemplates.COPY_TEMPLATE ) 
			);
		}
	}

	class CutTemplate extends AbstractAction {
		public CutTemplate() {
			putValue( Action.NAME, "Cut this template" );
			putValue( Action.SHORT_DESCRIPTION, "Cut this template, you may copy it after" );
			putValue( Action.SMALL_ICON, new ImageIcon( getClass().getResource( "cut.png" ) ) );
		}
		public void actionPerformed(ActionEvent e) {
			lastPopupRow = table.getSelectedRow();
			XSLTTemplates.this.actionPerformed( 
				new ActionEvent( this, e.getID(), XSLTTemplates.CUT_TEMPLATE ) 
			);						
		}		
	}

	class PasteTemplate extends AbstractAction {
		public PasteTemplate() {
			putValue( Action.NAME, "Paste the last copy of a template" );
			putValue( Action.SHORT_DESCRIPTION, "Paste the last copy of a template" );
			putValue( Action.SMALL_ICON, new ImageIcon( getClass().getResource( "paste.png" ) ) );			
		}
		public void actionPerformed(ActionEvent e) {
			lastPopupRow = table.getSelectedRow();
			XSLTTemplates.this.actionPerformed( 
				new ActionEvent( this, e.getID(), XSLTTemplates.PASTE_TEMPLATE ) 
			);			
		}
	}

	class NewTemplateBefore extends AbstractAction {
		public NewTemplateBefore() {
			putValue( Action.NAME, "Insert a new template before" );
			putValue( Action.SHORT_DESCRIPTION, "Add a new template before the selected one" );
			putValue( Action.SMALL_ICON, new ImageIcon( getClass().getResource( "element_new_before.png" ) ) );
		}
		public void actionPerformed(ActionEvent e) {
			lastPopupRow = table.getSelectedRow();			
			XSLTTemplates.this.actionPerformed( 
				new ActionEvent( this, e.getID(), XSLTTemplates.INSERT_TEMPLATE_BEFORE ) 
			);
		}
	}

	class NewTemplateAfter extends AbstractAction {
		public NewTemplateAfter() {
			putValue( Action.NAME, "Insert a new template after" );
			putValue( Action.SHORT_DESCRIPTION, "Add a new template after the selected one" );
			putValue( Action.SMALL_ICON, new ImageIcon( getClass().getResource( "element_new_after.png" ) ) );
		}
		public void actionPerformed(ActionEvent e) {
			lastPopupRow = table.getSelectedRow();
			XSLTTemplates.this.actionPerformed( 
				new ActionEvent( this, e.getID(), XSLTTemplates.INSERT_TEMPLATE_AFTER ) 
			);
		}
	}

}
