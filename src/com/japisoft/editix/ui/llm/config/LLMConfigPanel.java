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

package com.japisoft.editix.ui.llm.config;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.DefaultCellEditor;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import javax.swing.table.TableModel;

import com.japisoft.editix.ui.EditixFactory;
import com.japisoft.framework.ApplicationModel;
import com.japisoft.framework.llm.AbstractLLM;
import com.japisoft.framework.llm.LLM;
import com.japisoft.framework.llm.LLMConfig;
import com.japisoft.framework.llm.LLMFactory;
import com.japisoft.framework.llm.LLMManager;
import com.japisoft.framework.llm.provider.OllamaLLM;

import net.miginfocom.swing.MigLayout;

public class LLMConfigPanel extends JPanel implements ActionListener, TableModel, ListSelectionListener, DocumentListener, MouseListener {
	
	private JButton btnNew;
	private JButton btnDelete;
	private JButton btnRename;
	
	private JButton btMoveUp;
	private JButton btMoveDown;

	private JButton btnTest;
	
	private JTable tbLLM;
	private JTextArea txtSysPrompt;
	private JCheckBox cbThink;
	
	private JPasswordField txtAPIKey;
	private JButton btnCopyAPIKey;
	private JButton btnMaxTokens;
	
	
	private JTextField txtMaxTokens;

	public LLMConfigPanel() {
		setLayout( new MigLayout( "fill, insets 5", "[grow][]", "[][][grow 100][][grow 200][][][][][]" ) );
		
		JToolBar tb = new JToolBar();
		add( tb, "cell 0 0, left, wrap" );
		
		tb.setFloatable( false );
		tb.add( btnNew = new JButton( "New" ) );
		tb.add( btnDelete = new JButton( "Delete" ) );
		tb.add( btnRename = new JButton( "Rename" ) );
		tb.add( btnMaxTokens = new JButton( "Max tokens" ) );
		tb.addSeparator();
		tb.add( btMoveUp = new JButton( "Up" ) );
		tb.add( btMoveDown = new JButton( "Down" ) );
		tb.addSeparator();
		tb.add( btnTest = new JButton( "Test") );
		
		add( new JLabel( "Parameters" ), "wrap" );	
		add( new JScrollPane( tbLLM = new JTable( this ) ), "cell 0 2,span,grow, wrap" );

		add( new JLabel( "System prompt" ), "cell 0 3, wrap" );

		JScrollPane sp = null;
		add( sp = new JScrollPane( txtSysPrompt = new JTextArea() ), "cell 0 4, span, grow, wrap" );
		sp.setPreferredSize( new Dimension( 0, 300 ));
		
		txtSysPrompt.setRows( 3 );

		add( new JLabel( "API Key" ), "cell 0 7" );
		add( txtAPIKey = new JPasswordField(), "cell 0 8,grow" );
		
		add( btnCopyAPIKey = new JButton( "Paste" ), "cell 1 8,wrap" );
		
		add( new JLabel( "Think mode (if available)" ), "cell 0 9" );

		add( cbThink = new JCheckBox( "True"), "cell 0 9" );
		
		add( new JLabel( " / Max tokens" ), "cell 0 9" );
		
		add( txtMaxTokens = new JTextField(), "cell 0 9, wrap" );

		txtMaxTokens.setPreferredSize( new Dimension( 150, 0 ) );
		

		DefaultCellEditor tmp = null;
		
		// Type
		tbLLM.getColumnModel().getColumn( 1 ).setCellEditor( tmp = new DefaultCellEditor( new JComboBox<String>( LLMFactory.instance().getTypes() ) ) );
		tmp.setClickCountToStart( 2 );
		
		// URL
		tbLLM.getColumnModel().getColumn( 2 ).setCellEditor( tmp = new DefaultCellEditor( new JTextField() ) );
		tmp.setClickCountToStart( 2 );
		
		// models
		tbLLM.getColumnModel().getColumn( 3 ).setCellEditor( new LLMModelTableCellEditor() );
		
		tbLLM.getSelectionModel().setSelectionMode( ListSelectionModel.SINGLE_SELECTION );

		txtMaxTokens.setEditable( false );

	}

	@Override
	public void addNotify() {
		super.addNotify();
		JButton[] tmp = {
			btnNew,
			btnDelete,
			btnRename,
			btnMaxTokens,
			btnTest,
			btMoveUp,
			btMoveDown,
			btnCopyAPIKey
		};
		for ( JButton bt : tmp )
			bt.addActionListener( this );

		tbLLM.getSelectionModel().addListSelectionListener( this );
		txtSysPrompt.getDocument().addDocumentListener( this );
		txtAPIKey.getDocument().addDocumentListener( this );
		cbThink.addActionListener( this );
		
		if ( tbLLM.getModel().getRowCount() > 0 ) {
			SwingUtilities.invokeLater( () -> tbLLM.getSelectionModel().setSelectionInterval( 0, 0 ) );
		}


		tbLLM.addMouseListener( this );

	}

	@Override
	public void removeNotify() {
		super.removeNotify();
		JButton[] tmp = {
				btnNew,
				btnDelete,
				btnRename,
				btnMaxTokens,
				btnTest,
				btMoveUp,
				btMoveDown,
				btnCopyAPIKey
			};

		for ( JButton bt : tmp )
			bt.removeActionListener( this );		

		tbLLM.getSelectionModel().removeListSelectionListener( this );
		txtSysPrompt.getDocument().removeDocumentListener( this );
		txtAPIKey.getDocument().removeDocumentListener( this );
		cbThink.removeActionListener( this );
		tbLLM.removeMouseListener( this );
	}

	////////////////////////////////////////////////////////////////	
	
	@Override
	public void mouseClicked(MouseEvent e) {
		if ( e.getClickCount() > 1 ) {
			int col = tbLLM.columnAtPoint( e.getPoint() );
			int row = tbLLM.rowAtPoint( e.getPoint() );
			if ( col == 0 ) {
				rename();
			}
		}
	}

	@Override
	public void mousePressed(MouseEvent e) {
	}

	@Override
	public void mouseReleased(MouseEvent e) {
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
	}

	////////////////////////////////////////////////////////////////
	
	@Override
	public void changedUpdate(DocumentEvent e) {
	}

	@Override
	public void insertUpdate(DocumentEvent e) {
		if ( e.getDocument() == txtSysPrompt.getDocument() )
			updateSystemPrompt();
		else
		if ( e.getDocument() == txtAPIKey.getDocument() ) {
			updateAPIKey();
		}
	}

	@Override
	public void removeUpdate(DocumentEvent e) {
		if ( e.getDocument() == txtSysPrompt.getDocument() )
			updateSystemPrompt();
		else
		if ( e.getDocument() == txtAPIKey.getDocument() )
			updateAPIKey();
	}

	private void updateAPIKey() {
		if ( currentLLM != null ) {
			currentLLM.setProperty( LLMConfig.APIKEY_PROPERTY, txtAPIKey.getText() ); 
		}
	}

	private void updateSystemPrompt() {
		if ( currentLLM != null ) {
			currentLLM.setProperty( LLMConfig.SYSTEM_PROPERTY, txtSysPrompt.getText() );
		}
	}

	private AbstractLLM currentLLM;

	@Override
	public void valueChanged(ListSelectionEvent e) {
		int row = tbLLM.getSelectedRow();
		if ( row == -1 )
			return;

		currentLLM = LLMManager.instance().get( row );		
		txtSysPrompt.getDocument().removeDocumentListener( this );
		txtSysPrompt.setText( currentLLM.getProperty( LLMConfig.SYSTEM_PROPERTY, "" ) );
		txtSysPrompt.getDocument().addDocumentListener( this );

		txtAPIKey.getDocument().removeDocumentListener( this );
		txtAPIKey.setText( currentLLM.getProperty( LLMConfig.APIKEY_PROPERTY, "" ) );
		txtAPIKey.getDocument().addDocumentListener( this );

		cbThink.removeActionListener( this );
		cbThink.setSelected( "true".equalsIgnoreCase( currentLLM.getProperty( LLMConfig.THINK_PROPERTY, "false" ) ) );
		cbThink.addActionListener( this );
		
		txtMaxTokens.setText( currentLLM.getProperty( LLMConfig.MAX_TOKENS_PROPERTY, "" ) );
	}

	private void rename() {
		int index = tbLLM.getSelectedRow();
		if ( index == -1 )
			EditixFactory.buildAndShowWarningDialog( "No selection ?" );
		else {
			String currentName = LLMManager.instance().get( index ).getName();
			String newName = null;
			if ( ( newName = EditixFactory.buildAndShowInputDialog( "New name", currentName) ) != null ) {
				LLMManager.instance().renameAt( index, newName );
				updateTable();
			}
		}		
	}
	
	@Override
	public void actionPerformed( ActionEvent e ) {
		if ( e.getSource() == btnNew ) {
			String newName = EditixFactory.buildAndShowInputDialog( "Choose a name ?" );
			if ( newName != null ) {
				if ( LLMManager.instance().indexOf( newName ) != -1 )
					EditixFactory.buildAndShowWarningDialog( "This name exists" );
				else {
					try {
						LLMManager.instance().newLLM( newName, OllamaLLM.TYPE );
						l.tableChanged( new TableModelEvent( this ) );
						int row = tbLLM.getModel().getRowCount();
						tbLLM.getSelectionModel().setSelectionInterval( row - 1, row - 1 );
					} catch( Exception exc ) {
						EditixFactory.buildAndShowErrorDialog( "Can't add a new LLM ? [" + exc.getMessage() + "]" );
					}		
				}
			}
		} else
		if ( e.getSource() == btnDelete ) {
			int index = tbLLM.getSelectedRow();
			if ( index == -1 )
				EditixFactory.buildAndShowWarningDialog( "No selection ?" );
			else {
				LLMManager.instance().remove( index );
				l.tableChanged( new TableModelEvent( this ) );
				int row = tbLLM.getModel().getRowCount();
				tbLLM.getSelectionModel().setSelectionInterval( row - 1, row - 1 );				
			}
		} else
		if ( e.getSource() == btnRename ) {
			rename();
		} else
		if ( e.getSource() == btnTest ) {
			int index = tbLLM.getSelectedRow();
			if ( index == -1 )
				EditixFactory.buildAndShowWarningDialog( "No selection ?" );
			else {
				LLM currentLLM = LLMManager.instance().get( index );
				String prompt = EditixFactory.buildAndShowInputDialog( "Your prompt ?" );
				if ( prompt != null )
					new LLMRunner(currentLLM ).run( this, prompt );
			}
		} else
		if ( e.getSource() == cbThink ) {
			currentLLM.setProperty( LLMConfig.THINK_PROPERTY, Boolean.toString( cbThink.isSelected() ).toLowerCase() );
		} else
		if ( e.getSource() == btMoveUp ) {
			int row = tbLLM.getSelectedRow();
			LLMManager.instance().moveUp( row );
			updateTable();
			tbLLM.getSelectionModel().setSelectionInterval( Math.max( 0,  row - 1 ), Math.max( 0,  row - 1 ) ); 
		} else
		if ( e.getSource() == btMoveDown ) {
			int row = tbLLM.getSelectedRow();
			LLMManager.instance().moveDown( row );
			updateTable();
			tbLLM.getSelectionModel().setSelectionInterval( Math.min( tbLLM.getRowCount() - 1,  row + 1 ), Math.min( tbLLM.getRowCount() - 1,  row + 1 ) );
		} else
		if ( e.getSource() == btnCopyAPIKey ) {
			 Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
			 if (clipboard.isDataFlavorAvailable(DataFlavor.stringFlavor)) {
				 try {
					 String content = (String)clipboard.getData(DataFlavor.stringFlavor);
					 txtAPIKey.setText( content );
				 } catch( Exception exc ) {
					 exc.printStackTrace();
				 }				 
			 }
		} else
		if ( e.getSource() == btnMaxTokens ) {
			int index = tbLLM.getSelectedRow();
			if ( index == -1 )
				EditixFactory.buildAndShowWarningDialog( "No selection ?" );
			else {
				AbstractLLM al = LLMManager.instance().get( index );
				int maxTokens = al.getMaxTokens();
				String newValue = EditixFactory.buildAndShowInputDialog( "Choose a new value", Integer.toString( maxTokens ) );
				if ( newValue != null ) {
					try {
						if ( " ".equals( newValue ) || "".equals( newValue ) )
							newValue = "0";
						Integer.parseInt( newValue );
						al.setProperty( LLMConfig.MAX_TOKENS_PROPERTY, newValue );
						txtMaxTokens.setText( newValue );
					}  catch( NumberFormatException nfe ) {
						EditixFactory.buildAndShowErrorDialog( "Invalid number format ?" );
					}
				}
			}
		}

	}

	private void updateTable() {
		l.tableChanged( new TableModelEvent( this ) );
	}
	
	private TableModelListener l;
	
	@Override
	public void addTableModelListener(TableModelListener l) {
		this.l = l;
	}
	
	@Override
	public Class<?> getColumnClass(int columnIndex) {
		return String.class;
	}

	@Override
	public int getColumnCount() {
		// Name
		// Type
		// URL
		// Model
		return 4;
	}

	@Override
	public String getColumnName(int columnIndex) {
		switch ( columnIndex ) {
		case 0 : return "Name";
		case 1 : return "Type";
		case 2 : return "URL";
		case 3 : return "Model";
		}
		return null;
	}

	@Override
	public int getRowCount() {
		try {
			return LLMManager.instance().size();
		} catch( Exception exc ) {
			return 0;
		}
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		AbstractLLM llm = LLMManager.instance().get( rowIndex );
		switch ( columnIndex ) {
			case 0 : return llm.getName();
			case 1 : return LLMManager.instance().getType( llm );
			case 2 : return llm.getProperty( "url", "" );
			case 3 : return llm.getProperty( "model", "" );
		}			
		return null;
	}

	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		int startFrom = 1;
		if ( LLMFactory.instance().size() <= 1 )
			startFrom = 2;
		return columnIndex >= startFrom;
	}
	@Override
	public void removeTableModelListener(TableModelListener l) {
		this.l = null;
	}
	
	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		try {
			AbstractLLM llm = LLMManager.instance().get( rowIndex );
			if ( columnIndex == 1 ) {
				LLMManager.instance().updateTypeAt( rowIndex, (String)aValue );
				l.tableChanged( new TableModelEvent( this ) );
			}
			if ( columnIndex == 2 && aValue != null ) 
				llm.setProperty( LLMConfig.URL_PROPERTY, aValue.toString() );
			if ( columnIndex == 3 && aValue != null )
				llm.setProperty( LLMConfig.MODEL_PROPERTY, aValue.toString() );
		} catch( Exception exc ) {
			exc.printStackTrace();
		}
	}

	public static void main( String[] args ) {
		ApplicationModel.SHORT_APPNAME = "test";
		JFrame f = new JFrame();
		f.add( new LLMConfigPanel() );
		f.pack();
		f.setVisible( true );;
	}

}