// Editix XML Editor
// https://www.editix.com
// Copyright (c) 2026 Alexandre Brillant
//
// This program is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
// See the GNU General Public License for more details: https://www.gnu.org/licenses/gpl-3.0
//
// AI Training Restriction :
// This source code is provided for human use only.
// Using this code to train, fine-tune, or develop AI models,
// machine learning systems, or similar technologies is
// STRICTLY PROHIBITED. Violations will terminate all rights
// under the applicable license.

package com.japisoft.editix.editor.xsd.view;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.EventListener;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JTable;

import javax.swing.event.EventListenerList;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;

import org.w3c.dom.Element;

import com.japisoft.editix.editor.xsd.Changeable;
import com.japisoft.editix.editor.xsd.toolkit.SchemaHelper;

public class AnnotationViewImpl extends JTable implements View, MouseListener, Changeable {
	private Element initE;
	
	public AnnotationViewImpl() {
	
	}

	private boolean changed = false;
	
	public void init( Element schemaNode ) {
		if ( schemaNode != null )
			if ( "annotation".equals( schemaNode.getLocalName() ) )
				schemaNode = ( Element )schemaNode.getParentNode();

		this.initE = schemaNode;
		changed = false;
		setEnabled( initE != null );
		if ( initE != null ) {
			setModel( new CustomTableModel() );

			getColumnModel().getColumn(2).setCellRenderer( new DeleteRenderer() );
			getColumnModel().getColumn(0).setMaxWidth( 300 );
			getColumnModel().getColumn(2).setMaxWidth( 20 );

		}	

	}
	
	public boolean isChanged() { return changed; }
	
	public JComponent getView() {
		return this;
	}

	public void dispose() {
		initE = null;
		setModel( null );
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

	@Override
	public void addNotify() {
		super.addNotify();
		addMouseListener( this );
	}
	
	@Override
	public void removeNotify() {
		super.removeNotify();
		removeMouseListener( this );
	}
	
	@Override
	public void mouseClicked(MouseEvent e) {
		int row = rowAtPoint( e.getPoint() );
		int col = columnAtPoint( e.getPoint() );
		if ( col == 2 ) {
			( ( CustomTableModel )getModel() ).removeRow( row );
		}
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
	}

	@Override
	public void mousePressed(MouseEvent e) {
	}

	@Override
	public void mouseReleased(MouseEvent e) {
	}

	public void stopEditing() {}

	class CustomTableModel implements TableModel {

		private List<Element> documentation = null;
		private Element annotation = null;
		
		CustomTableModel() {
			annotation = SchemaHelper.getChildAt( 
					initE, 
					0, 
					new String[] { "annotation" } );

			if ( annotation != null )			
				documentation = SchemaHelper.getChildren( annotation, "documentation" );
			else
				documentation = new ArrayList<Element>();
		}
		
		public void removeRow( int row ) {
			if ( row < documentation.size() ) {
				Element doc = documentation.get( row );
				doc.getParentNode().removeChild( doc );
				documentation.remove( row );
				fireChange( new TableModelEvent( this ) );
			}
		}
		
		@Override
		public Class<?> getColumnClass(int columnIndex) {
			return String.class;
		}

		public int getColumnCount() {
			return 3;
		}

		public String getColumnName(int columnIndex) {
			if ( columnIndex == 0 ) {
				return "Source";
			} else			
			if ( columnIndex == 1 )
				return "Documentation";
			else
				return "x";
		}

		@Override
		public int getRowCount() {
			return documentation.size() + 1;
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			if ( rowIndex < documentation.size() ) {
				Element e = documentation.get( rowIndex );
				if ( columnIndex == 0 )
					return e.getAttribute( "source" );
				if ( columnIndex == 1 )
					return e.getTextContent();
			}
			return null;
		}

		@Override
		public boolean isCellEditable(int rowIndex, int columnIndex) {
			if ( columnIndex == 2 )
				return false;
			return true;
		}

		@Override
		public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
			Element doc = null;
			if ( rowIndex == documentation.size() ) {
				// New documentation
				doc = SchemaHelper.createTag( initE, "documentation" );
				if ( annotation == null ) {
					annotation = SchemaHelper.createTag( initE, "annotation" );
					if ( initE.getFirstChild() != null )
						initE.insertBefore( annotation, initE.getFirstChild() );
					else
						initE.appendChild( annotation );
				}
				annotation.appendChild( doc );
				documentation.add( doc );
			} else {
				doc = documentation.get( rowIndex );
			}
			if ( columnIndex == 0 ) {
				// Source
				doc.setAttribute( "source", aValue.toString() );
			} else {
				doc.setTextContent( aValue.toString() );
			}	
			changed = true;
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
			changed = true;
		}
		
		
	}
	
	private ImageIcon deleteIcon = new ImageIcon( getClass().getResource( "element_delete.png" ) );
	
	class DeleteRenderer extends JButton implements TableCellRenderer {
		public DeleteRenderer() {
			setIcon( deleteIcon );
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
	
	
	
	/*
	class CustomPlainDocument extends PlainDocument {
		public void insertString(int offs, String str, AttributeSet a) 
				throws BadLocationException {
			super.insertString(offs, str, a);
			resetTexts();
		}
		public void remove(int offs, int len) 
				throws BadLocationException {
			super.remove(offs, len);
			if ( getLength() == 0 ) {
				// Remove documentation node
				Element annotation = SchemaHelper.getChildAt( initE, 0, new String[] { "annotation" } );
				if ( annotation != null ) {
					Element documentation = SchemaHelper.getChildAt( annotation, 0, new String[] { "documentation" } );
					if ( documentation != null ) {
						annotation.removeChild( documentation );
						if ( !SchemaHelper.hasDOMElementChild( annotation ) ) {
							initE.removeChild( annotation );
						}
					}
				}
			} else
				resetTexts();
		}
		private void resetTexts() {
			Element annotation = SchemaHelper.getChildAt( initE, 0, new String[] { "annotation" } );
			if ( annotation == null ) {
				annotation = SchemaHelper.createTag( initE, "annotation" );
				if ( initE.hasChildNodes() ) {
					initE.insertBefore( annotation, initE.getChildNodes().item( 0 ) );
				} else
					initE.appendChild( annotation );
				Element documentation = SchemaHelper.createTag( initE, "documentation" );
				Text t = initE.getOwnerDocument().createTextNode( AnnotationViewImpl.this.getText() );
				documentation.appendChild( t );
				annotation.appendChild( documentation );
			} else {
				Element documentation = SchemaHelper.getChildAt( 
						annotation, 
						0, 
						new String[] { "documentation" } );
				if ( documentation == null ) {
					documentation = SchemaHelper.createTag( initE, "documentation" );
					Text t = initE.getOwnerDocument().createTextNode( AnnotationViewImpl.this.getText() );
					documentation.appendChild( t );
					annotation.appendChild( documentation );					
				} else {
					// Update text
					SchemaHelper.removeChildren( documentation );
					Text t = initE.getOwnerDocument().createTextNode( AnnotationViewImpl.this.getText() );
					documentation.appendChild( t );					
				}
			}
		}
		
		
	}
	*/

}
