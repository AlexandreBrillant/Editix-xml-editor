package com.japisoft.editix.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;

import com.japisoft.framework.preferences.Preferences;
import com.japisoft.framework.ui.FastLabel;
import com.japisoft.framework.ui.table.ExportableTable;
import com.japisoft.framework.ui.toolkit.FileManager;
import com.japisoft.xmlpad.error.ErrorSelectionListener;
import com.japisoft.xmlpad.error.ErrorView;

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
public class EditixErrorPanel extends JPanel 
		implements 
			ErrorView, 
			ListSelectionListener,
			MouseListener,
			MouseMotionListener {

	private JTable table = null;
	private DefaultTableModel model = null;
	private ArrayList listeners = null;
	private ArrayList errorData = null;

	public EditixErrorPanel() {}

	private void buildit() {
		table = new ExportableTable() {
			public boolean isCellEditable(int row, int column) {
				return false;
			}
			public TableCellRenderer getDefaultRenderer(Class columnClass) {
				return getRenderer();
			}
		};
		table.getSelectionModel().setSelectionMode(
				ListSelectionModel.SINGLE_SELECTION );
		
		table.getActionMap().put( "select-up", new SelectUp() );
		table.getActionMap().put( "select-down", new SelectDown() );
		table.getInputMap( table.WHEN_IN_FOCUSED_WINDOW ).put( 
				KeyStroke.getKeyStroke( KeyEvent.VK_UP,  
						KeyEvent.ALT_DOWN_MASK ), "select-up" );
		table.getInputMap( table.WHEN_IN_FOCUSED_WINDOW ).put( 
				KeyStroke.getKeyStroke( KeyEvent.VK_DOWN,  
						KeyEvent.ALT_DOWN_MASK ), "select-down" );
		resetModel();
		setLayout( new BorderLayout() );
		add( new JScrollPane( table ) );
		
		int maxLine = Preferences.getPreference( "editor", "maxErrorLines", 5 );

		setPreferredSize( new Dimension( 0, 
			table.getTableHeader().getPreferredSize().height + 
			table.getRowHeight() * maxLine ) 
		);	
		table.getTableHeader().setReorderingAllowed(false);
	}

	public void addNotify() {
		super.addNotify();
		table.getSelectionModel().addListSelectionListener( this );
		table.addMouseListener( this );
		table.addMouseMotionListener( this );
		table.getTableHeader().addMouseListener( this );
	}

	public void removeNotify() {
		super.removeNotify();
		table.getSelectionModel().removeListSelectionListener( this );
		table.removeMouseListener( this );
		table.removeMouseMotionListener( this );
		table.getTableHeader().removeMouseListener( this );
	}

	public void dispose() {
		listeners = null;
	}	
	
	public void valueChanged( ListSelectionEvent e ) {
		displayError();
	}

	private void displayError() {
		int selectedRow = table.getSelectedRow();
		if ( selectedRow != -1 ) {
			int dataLocation = selectedRow * 3;
			int line = ( ( Integer )errorData.get( dataLocation + 2 ) ).intValue();
			boolean local = ( ( Boolean )errorData.get( dataLocation ) ).booleanValue();
			String source = ( String )errorData.get( dataLocation + 1 );
			String message = ( String )table.getValueAt( selectedRow, 1 );
			
			if ( errorSelectionListeners != null ) {
				for ( int i = 0; i < errorSelectionListeners.size(); i++ ) {
					ErrorSelectionListener esl = 
						( ErrorSelectionListener )errorSelectionListeners.get( i );
					esl.errorSelected( source, line, message );
				}
			}
		}
	}

	public JComponent getView() {
		if ( table == null )
			buildit();
		return this;
	}

	public boolean isShownForOnTheFly() {
		return false;
	}
	
	private ArrayList errorSelectionListeners = null;
	
	public void addErrorSelectionListener(ErrorSelectionListener listener) {
		if ( errorSelectionListeners == null )
			errorSelectionListeners = new ArrayList();
		errorSelectionListeners.add( listener );
	}

	public void removeErrorSelectionListener(ErrorSelectionListener listener) {
		if ( errorSelectionListeners != null )
			errorSelectionListeners.remove( listener );
	}

	public void initErrorProcessing() {
		model = null;
		errorData = null;
	}

	private void resetModel() {
		if ( model != null && table != null ) {
			table.setModel( model );
			table.getColumnModel().getColumn( 0 ).setMaxWidth( 25 );
			table.getColumnModel().getColumn( 1 ).setHeaderValue(		
					"<html><body><b>" +
					table.getModel().getRowCount() + 
					" Message(s)</b> / Click on each error for highlighting <b>( alt-up / alt-down )</b> / Double click on this header for maximazing </body></html>" 
			);
			table.getColumnModel().getColumn( 0 ).setResizable( false );
			table.getColumnModel().getColumn( 1 ).setResizable( false );
		}
	}

	public void stopErrorProcessing() {
		resetModel();
		if ( table != null ) {
			model = null;
		}
		this.lastErrorLocation = null;
		
	}

	public void selectFirstError( ) {
		if (  table != null ) {
				SwingUtilities.invokeLater(
						new Runnable() {
							public void run() {
								table.getSelectionModel().setSelectionInterval( 
										0, 
										0 
								);
							}
						}
				);
			}		
	}

	public void initOnceAdded() {
		selectFirstError();
	}	

	private String lastErrorLocation = null;
	
	public void notifyError(Object context,boolean localError, String sourceLocation,
			int line, int col, int offset, String message, boolean onTheFly) {
		if ( model == null && !onTheFly ) {
			model = new DefaultTableModel(
					new Object[] { "In", "Message" }, 0 );	 
		}
		if ( !onTheFly ) {
			if ( errorData == null )
				errorData = new ArrayList();
			errorData.add(
					new Boolean( localError ) );
			
			if ( sourceLocation != null )
				lastErrorLocation = sourceLocation;
			if ( !localError && sourceLocation == null )
				sourceLocation = lastErrorLocation;
			
			errorData.add(
					sourceLocation );
			errorData.add(
					new Integer( line ) );
			model.addRow(
					new Object[] { 
							new Boolean( 
									localError ), 
									message } );
		}
	}

	public void notifyNoError( boolean onTheFly ) {}

	static Icon INPUT_ERROR, OUTPUT_ERROR;
	static CustomTableRenderer RENDERER = null;
	
	static CustomTableRenderer getRenderer() {
		if ( RENDERER == null )
			RENDERER = new CustomTableRenderer();
		return RENDERER;		
	}

	static class CustomTableRenderer implements TableCellRenderer {

		private FastLabel error = new FastLabel();
		private JTextArea msg = new JTextArea();
		
		public CustomTableRenderer() {
			if ( INPUT_ERROR == null ) {
				INPUT_ERROR = new ImageIcon( ClassLoader.getSystemResource( "images/element_into.png" ) );
			}
			if ( OUTPUT_ERROR == null ) {
				OUTPUT_ERROR = new ImageIcon( ClassLoader.getSystemResource( "images/element_previous.png" ) );
			}
			msg.setLineWrap( true );
		}

		public Component getTableCellRendererComponent(
				JTable table, 
				Object value, 
				boolean isSelected, 
				boolean hasFocus, 
				int row, 
				int column) {
			if ( isSelected ) {
				error.setBackground( table.getSelectionBackground() );
				error.setForeground( table.getSelectionForeground() );
				msg.setBackground( table.getSelectionBackground() );
				msg.setForeground( table.getSelectionForeground() );
			} else {
				error.setBackground( table.getBackground() );
				error.setForeground( table.getForeground() );
				msg.setBackground( table.getBackground() );
				msg.setForeground( table.getForeground() );
			}
			if ( column == 1 ) {
				if ( value instanceof String ) {
					msg.setText( ( String )value );
				}
				return msg;
			}
			else {
				if ( value instanceof Boolean ) {
					Boolean b = ( Boolean )value;
					if ( b.booleanValue() )
						error.setIcon( INPUT_ERROR );
					else
						error.setIcon( OUTPUT_ERROR );
				}
				return error;
			}
		}
	}

	private boolean maximize = false;
	
	void toggleMaximize() {
		maximize = !maximize;
		if ( maximize ) {
			setPreferredSize( new Dimension( 0, getHeight() * 2 ) );
		} else {
			setPreferredSize( new Dimension( 0, getHeight() / 2 ) );
		}
		getParent().invalidate();
		getParent().validate();
		getParent().repaint();
	}

	public void mouseClicked(MouseEvent e) {
		if ( e.getSource() instanceof JTableHeader && e.getClickCount() > 1 ) {
			toggleMaximize();
		} else {
		
			if ( e.getButton() > 1 ) {
	
				Point p = e.getPoint();
				int row = table.rowAtPoint( p );
				if ( row > -1 ) {
					table.getSelectionModel().setSelectionInterval( row, row );
					JPopupMenu pm = new JPopupMenu();
					pm.add( new CopyErrorAction( table.getSelectedRow() ) );
					pm.add( new CopyErrorsAction() );
					pm.addSeparator();
					pm.add( new ExportErrorsAction() );
					pm.show( this, e.getX(), e.getY() );			
				}
			} else
				displayError();
			
		}
	}

	public void mouseEntered(MouseEvent e) {
	}

	public void mouseExited(MouseEvent e) {
	}

	public void mousePressed(MouseEvent e) {
	}

	public void mouseReleased(MouseEvent e) {
	}

	public void mouseDragged(MouseEvent e) {
	}

	public void mouseMoved(MouseEvent e) {
		Point p = e.getPoint();
		int row = table.rowAtPoint( p );
		if ( row > -1 ) {
			String message = ( String )table.getModel().getValueAt( row, 1 );
			message = message.replaceAll( "<", "&lt;");
			message = message.replaceAll( ">", "&gt;");
			// Force a cut
			table.setToolTipText( getErrorTooltip( row, message ) );
		}
	}

	static String getErrorTooltip( int row, String message ) {
		return "<html><body><div width='80%'><b>Error " + ( row + 1 ) + "</b><br>"+ message + "</div></body><html>"; 
	}

	class CopyErrorAction extends AbstractAction {

		private int row;
		
		CopyErrorAction( int row ) {
			putValue( Action.NAME, "Copy this error to the clipboard" );
			this.row = row;
		}
		
		public void actionPerformed(ActionEvent e) {
			if ( row > -1 ) {
				StringSelection ss = new StringSelection( "" + table.getModel().getValueAt( row, 1 ) );
				Toolkit.getDefaultToolkit().getSystemClipboard().setContents(
						ss, 
						ss );
			}
		}
	}

	private String getErrors() {
		StringBuffer sb = new StringBuffer();
		for ( int i = 0; i < table.getModel().getRowCount(); i++ ) {
			if ( i > 0 )
				sb.append( System.getProperty( "line.separator" ) );
			sb.append( table.getModel().getValueAt( i, 1 ) );
		}
		return sb.toString();
	}
	
	class CopyErrorsAction extends AbstractAction {

		CopyErrorsAction() {
			putValue( Action.NAME, "Copy all the errors to the clipboard" );
		}
		
		public void actionPerformed(ActionEvent e) {
			StringSelection ss = new StringSelection( getErrors() );
			Toolkit.getDefaultToolkit().getSystemClipboard().setContents(
					ss, 
					ss );			
		}		
	}

	class ExportErrorsAction extends AbstractAction {
		ExportErrorsAction() {
			putValue( Action.NAME, "Save all the errors in a file" );
		}
		public void actionPerformed(ActionEvent e) {
			File f = FileManager.getSelectedFile(
					false,"txt","Errors file" );
			if ( f != null ) {
				try {
					FileWriter fw = new FileWriter( f );
					try {
						fw.write( getErrors() );
					} finally {
						fw.close();
					}
				} catch (IOException e1) {
					EditixFactory.buildAndShowErrorDialog( "Can't save to " + f );
				}
			}
		}
	}

	class SelectDown extends AbstractAction {
		public void actionPerformed( ActionEvent e ) {
			int i = table.getSelectedRow();
			if ( i < table.getRowCount() - 1 ) {
				table.getSelectionModel().setSelectionInterval( i + 1, i + 1 );
			}
		}
	}

	class SelectUp extends AbstractAction {
		public void actionPerformed( ActionEvent e ) {
			int i = table.getSelectedRow();
			if ( i > 0 ) {
				table.getSelectionModel().setSelectionInterval( i - 1, i - 1 );
			}
		}
	}
	
	
}
