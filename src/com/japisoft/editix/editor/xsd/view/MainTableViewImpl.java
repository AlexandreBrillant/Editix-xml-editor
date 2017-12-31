package com.japisoft.editix.editor.xsd.view;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.EventListener;
import java.util.EventObject;

import javax.swing.AbstractCellEditor;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.JViewport;
import javax.swing.ListSelectionModel;
import javax.swing.TransferHandler;
import javax.swing.event.EventListenerList;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.japisoft.editix.editor.xsd.Factory;
import com.japisoft.editix.editor.xsd.toolkit.SchemaHelper;
import com.japisoft.editix.editor.xsd.view.designer.XSDAbstractComponentImpl;
import com.japisoft.framework.ui.table.ExportableTable;

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
public class MainTableViewImpl extends ExportableTable 
			implements View, MouseListener, MouseMotionListener {
	private Element schemaNode = null;
	private Factory factory = null;
	
	public MainTableViewImpl( Factory factory ) {
		this.factory = factory;
		setTransferHandler( new CustomTransferHandler() );
	}

	public void init( Element schemaNode ) {
		this.schemaNode = schemaNode;
		setModel( new CustomTableModel() );
		CustomIconRenderer renderer = new CustomIconRenderer();
		getColumnModel().getColumn(0).setCellRenderer(
				renderer );
		getColumnModel().getColumn(0).setMaxWidth( 20 );
		getColumnModel().getColumn(3).setCellRenderer(
				renderer );
		getColumnModel().getColumn(3).setMaxWidth( 20 );
		CustomLabelRenderer renderer2 = new CustomLabelRenderer();
		getColumnModel().getColumn(1).setCellRenderer(
				renderer2 );
		getSelectionModel().setSelectionMode(
				ListSelectionModel.SINGLE_SELECTION );
		getSelectionModel().setSelectionInterval( 0, 0 );
		setBackground( Color.WHITE );
		getColumnModel().getColumn( 1 ).setCellEditor(
				new CustomTypeEditor() );
		getSelectionModel().setSelectionInterval( 0, 0 );
	}

	public void valueChanged(ListSelectionEvent e) {
		super.valueChanged(e);
		// Be sure to scroll correctly
		if (getParent() instanceof JViewport) { // Scroll to visible
			try {
			    JViewport viewport = (JViewport) getParent();
			    java.awt.Rectangle rect = getCellRect(getSelectedRow(), 0, true);
			    java.awt.Point pos = viewport.getViewPosition();  // Shouldn't have to do this!
			    rect.translate(-pos.x, -pos.y);  // Shouldn't have to do this!
			    viewport.scrollRectToVisible(rect);
			} catch( Throwable th ) {}
		}		
	}	

	public void stopEditing() {
		if ( getCellEditor() != null )
			getCellEditor().stopCellEditing();
	}
	
	public JComponent getView() {
		return this;
	}

	public void dispose() {
		( ( CustomTypeEditor )(
				getColumnModel().getColumn( 1 ).getCellEditor()
				) ).dispose();
		schemaNode = null;
		designerListener = null;
	}	

	public void addNotify() {
		super.addNotify();
		// from the parent
		// addMouseListener( this );
		addMouseMotionListener( this );
	}

	public void removeNotify() {
		super.removeNotify();
		// removeMouseListener( this );
		removeMouseMotionListener( this );
	}

	public void select( Element e ) {
		for ( int i = 0; i < getRowCount(); i++ ) {
			Element s = ( Element )( ( CustomTableModel )getModel() ).searchElementAt( i );
			if ( s == e ) {
				getSelectionModel().setSelectionInterval( i, i );
				break;
			}
		}
	}

	@Override
	public void copy() {
	}

	@Override
	public void cut() {
	}

	@Override
	public void paste() {
	}
	
	private XSDSelectionListener designerListener = null;

	public void setCommonSelectionListener( XSDSelectionListener listener ) {
		this.designerListener = listener;
	}

	public void mouseClicked(MouseEvent e) {
		int row = rowAtPoint( e.getPoint() );
		int col = columnAtPoint( e.getPoint() );
		if ( row > -1 && col == 3 ) {
			String element = ( String )getValueAt( row, 1 );
			String name = ( String )getValueAt( row, 2 );
			if ( factory.confirmDialog( "Delete " + element + " with name '" + name + "' ?" ) ) {
				Element ee = SchemaHelper.getElementAtRow(
						schemaNode, row );
				if ( ee != null ) {
					schemaNode.removeChild( ee );
					refreshModel();
					if ( row > 0 )
						getSelectionModel().setSelectionInterval( row - 1, row - 1 );
					else
						getSelectionModel().setSelectionInterval( 0, 0 );
				}
			}
		} else
		if ( ( row > -1 && col == 0 ) || ( row > -1 && e.getClickCount() > 1 ) ) {
			
			String name = ( String )getModel().getValueAt( row, 1 );			
			if ( canOpenDesigner( name ) ) {
				designerListener.openDesigner( SchemaHelper.getElementAtRow(
						schemaNode, row ) );
			}
		}
	}

	public void mouseEntered(MouseEvent e) {}
	public void mouseExited(MouseEvent e) {}
	public void mousePressed(MouseEvent e) {}
	public void mouseReleased(MouseEvent e) {}
	public void mouseMoved(MouseEvent e) {
		int row = rowAtPoint( e.getPoint() );
		int column = columnAtPoint( e.getPoint() );
		setToolTipText( null );
		if ( column == 0 ) {
			setToolTipText( "Click here for the visual mode" );
		} else
		if ( column == 3 ) {
			setToolTipText( "Click here for deleting this element" );
		} else {
			CustomTableModel ctm = ( CustomTableModel )getModel();
			Element ee = ctm.searchElementAt( row );
			if ( ee != null ) {
				setToolTipText( XSDAbstractComponentImpl.getToolTip( 
						ee, 
						"Double click for the visual mode" ) );
			}
		}
	}

	public void mouseDragged( MouseEvent e ) {
		getTransferHandler().exportAsDrag( 
				this, 
				e, 
				TransferHandler.MOVE );
	}

	public void refreshModel() {
		int selection = getSelectedRow();
		((CustomTableModel)getModel()).fireChange(
				new TableModelEvent( getModel() ) );
		getSelectionModel().setSelectionInterval( selection, selection );
	}

	// -------------------------------------------------------------

	ImageIcon deleteIcon = new ImageIcon( getClass().getResource( "element_delete.png" ) );
	ImageIcon editIcon = new ImageIcon( getClass().getResource( "element_view.png" ) );

	class CustomTypeEditor extends AbstractCellEditor implements TableCellEditor,ItemListener {
		private JComboBox cb = new JComboBox() {
			public void firePopupMenuWillBecomeInvisible() {
				fireEditingStopped();
			}
		};
		public CustomTypeEditor() {
			cb.addItemListener( this );
		}
		public Component getTableCellEditorComponent(
				JTable table, 
				Object value, 
				boolean isSelected, 
				int row, 
				int column ) {
			cb.setModel(
					new DefaultComboBoxModel(
							SchemaHelper.getParts(
									schemaNode,
									row ) ) );
			cb.setSelectedItem( value );
			return cb;
		}
		public void itemStateChanged(ItemEvent e) {
			fireEditingStopped();
		}
		public Object getCellEditorValue() {
			return cb.getSelectedItem();
		}		
		public boolean isCellEditable(EventObject anEvent) {
			if ( anEvent instanceof MouseEvent ) {
				MouseEvent e = ( MouseEvent )anEvent;
				int row = rowAtPoint( e.getPoint() );
				if ( row == getRowCount() -1 )
					return true;
				else
					return false;				
			}
			return true;
		}
		public boolean shouldSelectCell(EventObject anEvent) {
			return true;
		}
		public boolean stopCellEditing() {
			return true;
		}
		public void dispose() {
			cb.removeItemListener( this );
		}
	}

	class CustomIconRenderer extends JButton implements TableCellRenderer {
		public Component getTableCellRendererComponent(
				JTable table, 
				Object value, 
				boolean isSelected, 
				boolean hasFocus, 
				int row, 
				int column ) {
			if ( column == 0 ) {
				setIcon( editIcon );
			} else
			if ( column == 3 ) {
				setIcon( deleteIcon );
			}
			setEnabled( row != ( getRowCount() - 1 ) );
			if ( column == 0 ) {
				String name = ( String )table.getModel().getValueAt( row, 1 );				
				setEnabled( canOpenDesigner( name ) );
			}
			return this;
		}
	}

	private boolean canOpenDesigner( String name ) {
		if ( name == null )
			return false;
		if ( !( name.endsWith( "element" ) || 
				name.endsWith( "complexType" ) || 
				name.endsWith( "group" ) ||
				name.endsWith( "attributeGroup" )
			) )
			return false;
		return true;
	}
	
	class CustomLabelRenderer extends DefaultTableCellRenderer {
		private Color BG = new Color( 200, 250, 200 );
		private Font FT = getFont().deriveFont( Font.BOLD );
		public Component getTableCellRendererComponent(
				JTable table, 
				Object value, 
				boolean isSelected, 
				boolean hasFocus, 
				int row, 
				int column ) {
			Component c = super.getTableCellRendererComponent(
					table, value, isSelected, hasFocus, row, column );
			if ( c instanceof JLabel ) {
				JLabel lbl = ( JLabel )c;
				if ( column == 1 ) {
//					if ( !isSelected )
//						lbl.setBackground( BG );
					lbl.setFont( FT );
				} 
			}
			return this;
		}
	}

	class CustomTableModel implements TableModel {
		public Class getColumnClass( int columnIndex ) {
			return String.class;
		}

		public int getColumnCount() {
			return 4;
		}

		public String getColumnName(int columnIndex) {
			if ( columnIndex == 0 ) {
				return "E";
			} else			
			if ( columnIndex == 1 )
				return "Type";
			else
			if ( columnIndex == 2 )
				return "Name";
			else
				return "x";
		}

		public int getRowCount() {
			NodeList nl = schemaNode.getChildNodes();
			int cpt = 0;
			for ( int i = 0; i < nl.getLength(); i++ ) {
				if ( nl.item( i ) instanceof Element )
					cpt++;
			}
			return ( cpt + 1 );
		}

		Element searchElementAt( int index ) {
			return SchemaHelper.getElementAtRow( schemaNode, index );
		}

		public Object getValueAt(int rowIndex, int columnIndex) {
			Element e = searchElementAt( rowIndex );
			if ( e == null )
				return null;
			if ( columnIndex == 1 ) {
				return e.getNodeName();
			} else 
			if ( columnIndex == 2 ) {
				return e.getAttribute( "name" );
			} else
				return null;
		}

		public boolean isCellEditable(
				int rowIndex, 
				int columnIndex ) {
			if ( columnIndex == 2 || 
					columnIndex == 1 )
				return true;
			return false;
		}

		private EventListenerList listenerList = new EventListenerList();

		public void removeTableModelListener(TableModelListener l) {
			listenerList.remove( TableModelListener.class, l );
		}

		public void addTableModelListener(TableModelListener l) {
			listenerList.add( TableModelListener.class, l );
		}

		private void fireChange( TableModelEvent ee ) {
			EventListener[] el = listenerList.getListeners( TableModelListener.class );
			if ( el != null ) {
				for ( int i = 0; i < el.length; i++ ) {
					( ( TableModelListener )el[ i ] ).tableChanged(
							ee );
				}
			}			
		}

		public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
			Element e = searchElementAt( rowIndex );
			
			if ( rowIndex == ( getRowCount() - 1 ) ) {
				// Last line case

				String type = ( String )getValueAt( rowIndex, 1 );
				String value = ( String )getValueAt( rowIndex, 2 );
				if ( columnIndex == 1 )
					type = ( String )aValue;
				else
					value = ( String )aValue;

				Element ne = null;
				
				if ( type == null || 
						"".equals( type ) ) {
					if ( value != null && value.length() > 0 ) {
						ne = SchemaHelper.createElement( schemaNode, "" + value ); 
					}
				} else {
					ne = SchemaHelper.createAnyPart(
							schemaNode,
							null,
							type );
				}

				if ( ne != null ) {
					schemaNode.appendChild( ne );
					refreshModel();
				}
				
 			} else
				if ( e != null ) {
					if ( columnIndex == 2 ) {	// Name attribute
						e.setAttribute( "name", "" + aValue );
					} else 
					if ( columnIndex == 1 ) {	// Type
						Element ne = SchemaHelper.createAnyPart( schemaNode, null, "" + aValue );
						NodeList nl = e.getChildNodes();
						ArrayList al = new ArrayList();
						for ( int i = 0; i < nl.getLength(); i++ )
							al.add( nl.item( i ) );
						for ( int i = 0; i < al.size(); i++ ) {
							e.removeChild( (Node)al.get( i ) );
							ne.appendChild( (Node)al.get( i ) );
						}
						schemaNode.replaceChild( ne, e );
						refreshModel();
					}
				}
		}
	}

	// ----------------------------------------------------------------------------
	
	private int currentDragSelection = -1;
	
	class CustomTransferHandler extends TransferHandler {

		public boolean canImport(JComponent comp, DataFlavor[] transferFlavors) {
			currentDragSelection = getSelectedRow();
			return ( comp == MainTableViewImpl.this ) && ( currentDragSelection > -1 );
		}

		protected Transferable createTransferable(JComponent c) {
			CustomTableModel model = ( CustomTableModel )getModel();
			Element e = model.searchElementAt( getSelectedRow() );
			if ( e == null )
				return new StringSelection( "No node" );
			else
				return new StringSelection( e.getNodeName() );
		}

		protected void exportDone(JComponent source, Transferable data,
				int action) {
			
			CustomTableModel model = ( CustomTableModel )getModel();
			int currentSelection = getSelectedRow();
			if ( currentSelection > -1 && currentSelection != currentDragSelection ) {
				
				Element oldElement = model.searchElementAt( currentDragSelection );
				Element currentElement = model.searchElementAt( currentSelection );

				Element parentNode = ( Element )oldElement.getParentNode();
				parentNode.removeChild( oldElement );
				
				if ( currentElement != null ) {
					( ( Element )currentElement.getParentNode() ).insertBefore( oldElement, currentElement );
				} else {
					// For adding at the end
					parentNode.appendChild( oldElement );
				}
				refreshModel();

			}

		}

		public int getSourceActions(JComponent c) {
			return MOVE;
		}
		
	}

}
