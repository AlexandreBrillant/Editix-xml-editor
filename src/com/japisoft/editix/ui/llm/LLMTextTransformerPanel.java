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
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
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
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;
import javax.swing.text.BadLocationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.InputSource;

import com.japisoft.editix.ui.EditixFactory;
import com.japisoft.editix.ui.EditixFrame;
import com.japisoft.xmlpad.XMLContainer;

import net.miginfocom.swing.MigLayout;

public class LLMTextTransformerPanel extends JPanel implements TableModel, ActionListener, ListSelectionListener, DocumentListener {

	private List<Node> nodes = null;
	private Map<Node,Node> updates = null;

	private JButton btRun = null;
	private JTextField txXPath = null;
	private JTable tbNodes = null;
	private JTextArea txtSource = null;
	private JTextArea txtUpdate = null;
	private JButton btApply = null;

	public LLMTextTransformerPanel() {
		setLayout( new MigLayout( 
			"fill, insets 5", 
			"[grow][]", 
			"[][][][grow 50][][][grow 100][][grow 100][]" ) 
		);
		add( new JLabel( "XPath text selection" ), "wrap" );
		add( txXPath = new JTextField(), "grow" );add( btRun = new JButton( "Run" ), "wrap" );
		add( new JScrollPane( tbNodes = new JTable( this ) ), "span, wrap, height 200" );
		add( new JSeparator(), "wrap" );
		add( new JLabel( "Source" ), "wrap" );
		add( new JScrollPane( txtSource = new JTextArea(5,40) ), "grow, span, wrap, pushy" );
		add( new JLabel( "Update" ), "wrap" );
		add( new JScrollPane( txtUpdate = new JTextArea(5,40) ), "grow, span, wrap, pushy" );
		add( btApply = new JButton( "Apply" ), "cell 0 9" );
		add( new JButton( "Ask to LLM..." ), "cell 0 9, wrap" );

		tbNodes.getSelectionModel().setSelectionMode( ListSelectionModel.SINGLE_SELECTION );
		txtSource.setEditable( false );
	}

	@Override
	public void addNotify() {
		super.addNotify();
		btApply.addActionListener(this);
		btRun.addActionListener( this );
		tbNodes.getSelectionModel().addListSelectionListener( this );
		txtUpdate.getDocument().addDocumentListener( this );
	}

	@Override
	public void removeNotify() {
		super.removeNotify();
		btApply.removeActionListener(this);		
		btRun.removeActionListener( this );
		tbNodes.getSelectionModel().removeListSelectionListener( this );
		txtUpdate.getDocument().removeDocumentListener( this );
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if ( e.getSource() == btRun ) {
			runXPath();
		} else
		if ( e.getSource() == btApply ) {
			if ( updates == null || nodes == null ) {
				EditixFactory.buildAndShowWarningDialog( "No update ?" );
			} else {
				int nbUpdate = updates.size();
				if ( EditixFactory.buildAndShowConfirmDialog( "Apply " + nbUpdate + " updates to your document ?" )  ) {
					Document doc = null;

					for ( int i = 0; i < nodes.size(); i++ ) {
						Node oldNode = nodes.get( i );
						if ( doc == null )
							doc = oldNode.getOwnerDocument();

						if ( updates.containsKey( oldNode ) ) {
							Node newNode = updates.get( oldNode );
							nodes.set( i, newNode );
							oldNode.getParentNode().replaceChild( newNode, oldNode );
						}
					}
					

					if ( doc != null ) {
						try {
							Transformer t = TransformerFactory.newInstance().newTransformer();
							t.setOutputProperty( OutputKeys.INDENT, "yes" );
							StringWriter writer = new StringWriter();
							t.transform( new DOMSource( doc ), new StreamResult( writer ) );

							EditixFrame.THIS.getSelectedContainer().setText( writer.toString() );
							
						} catch( Exception exc ) {
							EditixFactory.buildAndShowErrorDialog( "Can't process your document [" + exc.getMessage() + "] ?" );
						}						
						updates = null;
					} else
						EditixFactory.buildAndShowWarningDialog( "Unknown doc ?");

					
										
				}
			}
		}
	}

	// Document listener

	boolean isUpdating = false;
	
	@Override
	public void changedUpdate(DocumentEvent e) {
		if ( !isUpdating ) {
			int currentRow = tbNodes.getSelectedRow();
			Node n = nodes.get( currentRow );
			if ( updates == null )
				updates = new HashMap<Node, Node>();

			Node updateNode = updates.get( n );
			if ( updateNode != null ) {
			} else {
				updateNode = n.cloneNode(true);
				updates.put( n, updateNode );
			}

			try {
				updateNode.setTextContent( e.getDocument().getText( 0, e.getDocument().getLength() ) );
			} catch( BadLocationException exc ) {
				
			}
		}
		isUpdating = false;
	}

	@Override
	public void insertUpdate(DocumentEvent e) {
		changedUpdate( e );
	}

	@Override
	public void removeUpdate(DocumentEvent e) {
		changedUpdate( e );
	}	

	// Table selection

	@Override
	public void valueChanged(ListSelectionEvent e) {		
		int row = tbNodes.getSelectedRow();
		Node sourceNode = nodes.get( row ); 

		txtSource.setText( sourceNode.getTextContent() );
		if ( updates != null ) {
			Node newNode = updates.get( sourceNode );
			if ( newNode != null ) {
				isUpdating = true;
				txtUpdate.setText( newNode.getTextContent() );
			} else {
				isUpdating = true;
				txtUpdate.setText( txtSource.getText() );
			}
		} else {
			isUpdating = true;
			txtUpdate.setText( txtSource.getText() );
		}
		
		txtSource.setCaretPosition( 0 );
		txtUpdate.setCaretPosition( 0 );
		
		SwingUtilities.invokeLater( () -> txtUpdate.requestFocus() );
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

			for ( int i = 0; i < nl.getLength(); i++ ) {
				Node uNode = nl.item( i );
				if ( uNode instanceof Element ) {
					Element e = ( Element )uNode;
					NodeList children = e.getChildNodes();
					for ( int j = 0; j < children.getLength(); j++ ) {
						if ( children.item( j ) instanceof Text ) {
							nodes.add( children.item( j ) );
						}
					}
				} else
					nodes.add( ( Node )nl.item( i ) );
			}

			l.tableChanged( new TableModelEvent( this ) );
			
			tbNodes.getColumnModel().getColumn( 0 ).setMaxWidth( 100 );
			
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
			} else {
				if ( n instanceof Text ) {
					return n.getParentNode().getLocalName();
				}
				return n.getLocalName();
			}
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
