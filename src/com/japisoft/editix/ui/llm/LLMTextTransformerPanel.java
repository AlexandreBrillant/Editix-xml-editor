// Editix XML Editor
// https://www.editix.com
// Copyright (c) 2025 Alexandre Brillant
// 
// For non-commercial usage :
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
// For commercial use or integration into proprietary software :
// A commercial license is required. Visit https://www.editix.com for details.

package com.japisoft.editix.ui.llm;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Node;
import org.xml.sax.InputSource;

import com.japisoft.editix.editor.jsx.domapi.NodeList;
import com.japisoft.editix.ui.EditixFactory;
import com.japisoft.editix.ui.EditixFrame;
import com.japisoft.xmlpad.XMLContainer;


public class LLMTextTransformerPanel extends JPanel implements TableModel, ActionListener, ListSelectionListener {

	private List<Node> nodes = null;
	private Map<Node,Node> updates = null;

	private JButton btRun = null;
	private JTextField txXPath = null;
	private JTable tbNodes = null;
	private JTextArea txtSource = null;
	private JTextArea txtUpdate = null;

	public LLMTextTransformerPanel() {
		
		add( new JLabel( "XPath text selection" ) );
		add( txXPath = new JTextField() );add( btRun = new JButton( "Run" ) );
		add( new JScrollPane( tbNodes = new JTable( this ) ) );
		add( new JSeparator() );
		add( new JLabel( "Source" ) );		
		add( new JScrollPane( txtSource = new JTextArea() ) );
		add( new JLabel( "Update" ) );
		add( new JScrollPane( txtUpdate = new JTextArea() ) );
		add( new JButton( "Apply" ) );
		add( new JButton( "Ask to LLM..." ) );

	}

	@Override
	public void addNotify() {
		super.addNotify();
		btRun.addActionListener( this );
		tbNodes.getSelectionModel().addListSelectionListener( this );
	}

	@Override
	public void removeNotify() {
		super.removeNotify();
		btRun.removeActionListener( this );
		tbNodes.getSelectionModel().removeListSelectionListener( this );
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if ( e.getSource() == btRun ) {
			runXPath();
		}
	}

	@Override
	public void valueChanged(ListSelectionEvent e) {		
		int row = tbNodes.getSelectedRow();
		Node sourceNode = nodes.get( row ); 
				
		txtSource.setText( (String)getValueAt( 1, row ) );
		if ( updates != null ) {
			Node newNode = updates.get( sourceNode );
			if ( newNode != null ) {
				txtUpdate.setText( newNode.getTextContent() );
			} else
				txtUpdate.setText( txtSource.getText() );
		} else
			txtUpdate.setText( txtSource.getText() );
	}

	private void runXPath() {
		XMLContainer container = EditixFrame.THIS.getSelectedContainer();
		if ( container == null ) {
			EditixFactory.buildAndShowErrorDialog( "Can't find your document ?" );
			return;
		}
		XPath xpath = XPathFactory.newInstance().newXPath();
				
		try {
			NodeList nl = (NodeList)xpath.evaluate( 
				txXPath.getText(),
				new InputSource( new StringReader( container.getText() ) ),
				XPathConstants.NODESET
			);

			nodes = new ArrayList<Node>();
			for ( int i = 0; i < nl.length; i++ )
				nodes.add( (Node)nl.item( i ) );

			l.tableChanged( new TableModelEvent( this ) );
		} catch( XPathExpressionException exc ) {
			EditixFactory.buildAndShowWarningDialog( "Invalid xpath expression : " + exc.getMessage() );
		}
	}

	/// Table Model

	@Override
	public int getRowCount() {
		if ( nodes == null )
			return 0;
		return nodes.size();
	}

	@Override
	public int getColumnCount() {
		return 2;
	}

	@Override
	public String getColumnName(int columnIndex) {
		if ( columnIndex == 0 )
			return "Parent";
		return "Text";
	}

	@Override
	public Class<?> getColumnClass(int columnIndex) {
		return String.class;
	}

	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return false;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		Node n = nodes.get( rowIndex );
		if ( columnIndex == 0 ) {
			if ( n instanceof org.w3c.dom.Attr ) {
				return "@" + n.getLocalName();
			} else
				return n.getLocalName();
		} else
		if ( columnIndex == 1 ) {
			return n.getTextContent();
		}
		return null;
	}

	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
	}

	private TableModelListener l;
	
	@Override
	public void addTableModelListener(TableModelListener l) {
		this.l = l;
	}

	@Override
	public void removeTableModelListener(TableModelListener l) {
		this.l = null;
	}

}
