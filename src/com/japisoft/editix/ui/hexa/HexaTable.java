package com.japisoft.editix.ui.hexa;

import java.awt.Color;
import java.awt.Component;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.PlainDocument;

import org.json.XML;

import com.japisoft.framework.ui.FastLabel;
import com.japisoft.framework.ui.table.ExportableTable;
import com.japisoft.framework.xml.XMLChar;

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
public class HexaTable extends ExportableTable implements MouseMotionListener {

	private HexaTableMode mode = HexaTableMode.HEX;
	private Document doc = null;

	public HexaTable( Document doc, int currentLine ) {
		this.doc = doc;
		setModel( new AsciiTableModel() );
		setSelectionMode( ListSelectionModel.SINGLE_SELECTION );
		if ( currentLine > -1 ) {
			getSelectionModel().setSelectionInterval( currentLine, currentLine );
		}
	}

	@Override
	public void addNotify() {
		super.addNotify();
		addMouseMotionListener( this );
		if ( getSelectionModel().getMinSelectionIndex() > -1 ) {
			scrollRectToVisible( new Rectangle( 
					getCellRect( 
						getSelectionModel().getMinSelectionIndex(), 0, true ) ) );
		}
	}

	@Override
	public void removeNotify() {
		super.removeNotify();
		removeMouseMotionListener( this );
		dispose();
	};

	public void mouseDragged(MouseEvent e) {
	}

	public boolean repair() {
		boolean hasRepaired = false;
		try {
			for ( int i = 0; i < doc.getLength(); i++ ) {
				String str = doc.getText( i, 1 );
				if ( XMLChar.isInvalid( str.charAt( 0 ) ) ) {
					doc.remove( i, 1 );
					doc.insertString( i, " ", null );
					hasRepaired = true;
				}
			}
		} catch( BadLocationException ble ) {
			
		}
		
		if ( hasRepaired ) {
			( ( AsciiTableModel )getModel() ).update();
		}
		
		return hasRepaired;
	}
	
	public void mouseMoved(MouseEvent e) {
		int rowIndex = rowAtPoint( e.getPoint() );
		int columnIndex = columnAtPoint( e.getPoint() );
		if ( columnIndex > 0 ) {
			String value = ( String )getModel().getValueAt( rowIndex, columnIndex);
			if ( value == null )
				setToolTipText( null );
			else {
				try {
					Element root = doc.getDefaultRootElement();
					Element row = root.getElement( rowIndex );
					int charOffset = ( row.getStartOffset() + ( columnIndex - 1 ) );
					String c = doc.getText( charOffset, 1 );
					if ( mode == HexaTableMode.CHAR ) {
						c = Integer.toHexString( c.charAt( 0 ) );
						if ( c.length() == 1 )
							c = "0" + c;
						c = c.toUpperCase();
					}
					setToolTipText( c );
				} catch( BadLocationException exc ) {
					
				}
			}
		}
	}

	public void setMode( HexaTableMode mode ) {
		this.mode = mode;
		repaint();
	}

	private AsciiTableRenderer atr = null;
	
	@Override
	public TableCellRenderer getDefaultRenderer(Class<?> columnClass) {
		if ( atr == null )
			atr = new AsciiTableRenderer();
		return atr;
	}
	
	public void dispose() {
		this.doc = null;
	}

	private int columnCache = -1;
	
	int getColumCount() {
		if ( columnCache == -1 ) {
			Element root = doc.getDefaultRootElement();
			for ( int i = 0; i < root.getElementCount(); i++ ) {
				Element rowElement = root.getElement( i );
				int startOffset = rowElement.getStartOffset();
				int endOffset = rowElement.getEndOffset();
				columnCache = Math.max( columnCache, endOffset - startOffset );
			}
		}
		return columnCache;
	}

	private char stringToChar( String value ) {
		if ( mode == HexaTableMode.CHAR ) {
			return value.charAt( 0 );
		} else
		if ( mode == HexaTableMode.HEX ) {
			try {
				return ( char )Integer.parseInt( value, 16 );
			} catch( NumberFormatException exc ) {
			}
		} else
		if ( mode == HexaTableMode.INT ) {
			try {
				return ( char )Integer.parseInt( value );
			} catch( NumberFormatException exc ) {
			}			
		}
		return 0;
	}

	private String charToString( int rowIndex, int columnIndex ) {
		Element root = doc.getDefaultRootElement();
		Element row = root.getElement( rowIndex );

		if ( row != null ) {
			int charOffset = ( row.getStartOffset() + ( columnIndex - 1 ) );
			if ( charOffset > row.getEndOffset() )
				return null;

			try {
				String c = doc.getText( charOffset, 1 );
				char cc = c.charAt( 0 );
				
				if ( mode == HexaTableMode.CHAR )
					return "" + cc;
				else
				if ( mode == HexaTableMode.HEX ) {
					String tmp = Integer.toHexString( cc );
					if ( tmp.length() == 1 )
						tmp = "0" + tmp;
					tmp = tmp.toUpperCase();
					return tmp;
				} else
				if ( mode == HexaTableMode.INT ) {
					return Integer.toString( cc );
				}
			} catch( BadLocationException exc ) {
			}
		}

		return null;
	}

	private char getCharAt( int rowIndex, int columnIndex ) {
		Element root = doc.getDefaultRootElement();
		Element row = root.getElement( rowIndex );
		if ( row != null ) {
			int charOffset = ( row.getStartOffset() + ( columnIndex - 1 ) );
			if ( charOffset > row.getEndOffset() )
				return '0';
			try {
				String c = doc.getText( charOffset, 1 );
				char cc = c.charAt( 0 );
				return cc;
			} catch( BadLocationException exc ) {
			}
		}
		return '0';
	}

	// --------------------------------------------------------
	
	class AsciiTableModel implements TableModel {

		private TableModelListener l = null;
		
		public void addTableModelListener( TableModelListener l ) {
			this.l = l;
		}

		public void update() {
			this.l.tableChanged( new TableModelEvent( this ) );
		}
		
		public Class<?> getColumnClass( int columnIndex ) {			
			return String.class;
		}

		public int getColumnCount() {
			return getColumCount() + 1;
		}

		public String getColumnName(int columnIndex) {
			if ( columnIndex == 0 )
				return "Row";
			return "" + ( columnIndex );
		}

		public int getRowCount() {
			return doc.getDefaultRootElement().getElementCount();
		}

		public Object getValueAt(int rowIndex, int columnIndex) {
			if ( columnIndex == 0 )
				return Integer.toString( rowIndex + 1 );
			return charToString( rowIndex, columnIndex );
		}

		public boolean isCellEditable(int rowIndex, int columnIndex) {
			return columnIndex > 0 && getValueAt( rowIndex, columnIndex) != null;
		}

		public void removeTableModelListener(TableModelListener l) {
			this.l = null;
		}

		public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
			Element root = doc.getDefaultRootElement();
			Element row = root.getElement( rowIndex );
			if ( row != null ) {
				try {
					doc.remove( row.getStartOffset() + columnIndex - 1, 1 );
					doc.insertString( row.getStartOffset() + columnIndex - 1, "" + stringToChar( ( String ) aValue ), null );
				} catch( BadLocationException exc ) {
				}
			}
		}

	}

	class AsciiTableRenderer implements TableCellRenderer {
		
		FastLabel fl = null;

		public AsciiTableRenderer() {
			fl = new FastLabel();
		}

		public Component getTableCellRendererComponent(JTable table,
				Object value, boolean isSelected, boolean hasFocus, int row,
				int column) {
			if ( isSelected ) {
				fl.setBackground( UIManager.getColor( "Table.selectionBackground" ) );
				fl.setForeground( UIManager.getColor( "Table.selectionForeground" ) );
			} else {
				fl.setBackground( UIManager.getColor( "Table.background" ) );
				fl.setForeground( UIManager.getColor( "Table.foreground" ) );				
			}
			if ( column == 0 ) {
				fl.setBackground( Color.LIGHT_GRAY );
			}
			if ( value == null ) {
				fl.setBackground( Color.DARK_GRAY );
				fl.setForeground( Color.WHITE );
			}
			
			fl.setText( ( String )value );
			
			if ( XMLChar.isInvalid( getCharAt(row, column ) ) ) {
				fl.setBackground( Color.RED );
			}
			
			
			return fl;
		}

	}

	public static void main( String[] args ) throws Exception {
		
		PlainDocument pd = new PlainDocument();
		pd.insertString( 0, "ABCD\ndfsdddddddddddddddddddsdfkljhdfskjh\ndfskljdfsldfksj\nsdfklhjdflskj", null );
		HexaTable at = new HexaTable( pd, -1 );
		at.setMode( HexaTableMode.INT );
		JFrame f = new JFrame();
		f.add( new JScrollPane( at ) );
		f.setVisible( true );
		
	}

}
