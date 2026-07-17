package com.japisoft.editix.ui.bottompanels;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JToolBar;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

import com.japisoft.editix.main.EditixApplicationModel;
import com.japisoft.editix.ui.EditixFactory;
import com.japisoft.framework.llm.LLMContext;
import com.japisoft.framework.llm.LLMExchange;
import com.japisoft.framework.ui.toolkit.FileManager;

public class LLMContextPanel extends JPanel implements TableModel, ActionListener {

	private LLMContext context;
	private JTable tbContext;
	private JToolBar tb;

	private JButton tbClear;
	private JButton tbSave;
	private JButton tbExport;
	private JButton tbImport;
	
	public LLMContextPanel() {		
		setLayout( new BorderLayout() );
		add( new JScrollPane( tbContext = new JTable( this ) ) );
		add( tb = new JToolBar(), BorderLayout.SOUTH );

		tb.add( tbClear = new JButton( "Clear" ) );
		tb.add( tbSave = new JButton( "Save" ) );
		tb.addSeparator();
		tb.add( tbExport = new JButton( "Export" ) );
		tb.add( tbImport = new JButton( "Import" ) );

		tb.setFloatable( false );
	}

	public void updateContext( LLMContext context ) {
		this.context = context;
		update();
	}

	public void addSelectionListener( ListSelectionListener listener ) {
		tbContext.getSelectionModel().addListSelectionListener( listener );
	}
	
	public void removeSelectionListener( ListSelectionListener listener ) {
		tbContext.getSelectionModel().removeListSelectionListener( listener );
	}

	public LLMExchange getSelectedLLMExchange() {
		int row = tbContext.getSelectedRow();
		if ( row > -1 ) {
			return context.get( row );
		}
		return null;
	}
	
	public LLMContext getContext() { return context; }
	
	public void addLLMExchange( LLMExchange exchange ) {
		if ( context != null )
			context.add( exchange );
		update();
	}
	
	@Override
	public void addNotify() {
		super.addNotify();
		tbClear.addActionListener( this );
		tbSave.addActionListener( this );
		tbExport.addActionListener( this );
		tbImport.addActionListener( this );
	}

	@Override
	public void removeNotify() {
		super.removeNotify();
		tbClear.removeActionListener( this );
		tbSave.removeActionListener( this );
		tbExport.removeActionListener( this );
		tbImport.removeActionListener( this );		
	}

	public void update() {
		if ( l != null )
			l.tableChanged( new TableModelEvent( this ));		
	}
	
	@Override
	public void actionPerformed( ActionEvent e ) {
		if ( e.getSource() == tbClear ) {
			context.removeAll( context );
			update();
		} else 
		if ( e.getSource() == tbSave ) {
			try {
				context.save( EditixApplicationModel.getEditixLLMContext() );
			} catch( Exception exc ) {
				EditixFactory.buildAndShowErrorDialog( "Can't save your context ? [" + exc.getMessage() + "]" );
			}
		} else
		if ( e.getSource() == tbExport ) {
			File f = FileManager.getSelectedFile( false, "xml", "LLM context file" );
			if ( f != null ) {
				try {
					context.save( f );
				} catch( Exception exc ) {
					EditixFactory.buildAndShowErrorDialog( "Can't save this context ? [" + exc.getMessage() + "]" );
				}
			}
		} else
		if ( e.getSource() == tbImport ) {
			File f = FileManager.getSelectedFile( true, "xml", "LLM context file" );
			if ( f != null ) {
				try {
					context.load( f );
					update();
				} catch( Exception exc ) {
					EditixFactory.buildAndShowErrorDialog( "Can't load this context ? [" + exc.getMessage() + "]" );
				}
			}
		}
	}

	/////////////////////////////////////////////////
	
	private TableModelListener l;
	
	@Override
	public void addTableModelListener(TableModelListener l) {
		this.l = l;
	}

	@Override
	public Class<?> getColumnClass(int columnIndex) {
		// TODO Auto-generated method stub
		return String.class;
	}

	@Override
	public int getColumnCount() {
		return 2;
	}
	
	@Override
	public String getColumnName(int columnIndex) {
		if ( columnIndex == 0 )
			return "Query";
		return "Response";
	}

	@Override
	public int getRowCount() {
		if ( context == null )
			return 0;
		return context.size();
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		LLMExchange exchange = context.get( rowIndex );
		if ( columnIndex == 0 )
			return exchange.getPrompt();
		return exchange.getResponse();
	}
	
	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return false;
	}
	
	@Override
	public void removeTableModelListener(TableModelListener l) {
		this.l = null;
	}
	
	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
	}

}
