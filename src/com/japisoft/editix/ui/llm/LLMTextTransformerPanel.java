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

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;
import javax.swing.text.BadLocationException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
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
import com.japisoft.editix.ui.llm.config.LLMRunner;
import com.japisoft.framework.dialog.DialogManager;
import com.japisoft.framework.dialog.actions.DialogActionModel;
import com.japisoft.framework.llm.LLM;
import com.japisoft.framework.ui.toolkit.FileManager;
import com.japisoft.xmlpad.XMLContainer;
import com.japisoft.xmlpad.dialog.XMLPadDialogManager;

import net.miginfocom.swing.MigLayout;

public class LLMTextTransformerPanel extends JPanel implements TableModel, ActionListener, ListSelectionListener, DocumentListener, TableCellRenderer {

	private List<Node> nodes = null;
	private Map<Node,Node> updates = null;

	private JButton btRun = null;
	private JTextField txXPath = null;
	private JTable tbNodes = null;
	private JTextArea txtSource = null;
	private JTextArea txtUpdate = null;
	private JButton btApply = null;
	private JButton btLLM = null;
	private JButton btCancel = null;
	private JButton btExport = null;
	private JButton btImport = null;
	private JButton btCopy = null;
	
	public LLMTextTransformerPanel() {
		setLayout( new MigLayout( 
			"fill, insets 5", 
			"[grow][]", 
			"[][][][grow 50][][grow][][grow][]" ) 
		);
		add( new JLabel( "XPath text selection" ), "wrap" );
		add( txXPath = new JTextField(), "span,grow" );add( btRun = new JButton( "Run" ), "wrap" );
		add( new JScrollPane( tbNodes = new JTable( this ) ), "span, grow, wrap, height 200" );
		add( new JSeparator(), "wrap" );
		add( new JLabel( "-> Source (Read only)" ), "wrap" );
		add( new JScrollPane( txtSource = new JTextArea(5,40) ), "grow, span, wrap, pushy" );
		add( new JLabel( "<- Update" ), "wrap" );
		add( new JScrollPane( txtUpdate = new JTextArea(5,40) ), "grow, span, wrap, pushy" );
		JToolBar tb = new JToolBar();
		tb.setFloatable( false );
		add( tb, "wrap" );
				
		tb.add( btLLM = new JButton( "Ask to LLM..." ) );
		tb.addSeparator();
		tb.add( btCancel = new JButton( "Cancel" ) );
		tb.add( btCopy = new JButton( "Copy" ) );		
		JToolBar tb2 = new JToolBar();
		tb2.setFloatable( false );
		
		add( new JSeparator(), "span, grow, wrap" );
		add( tb2, "wrap" );
		
		tb2.add( btApply = new JButton( "Apply All" ) );
		tb2.addSeparator();
		tb2.add( btExport = new JButton( "Export All" ) );
		tb2.add( btImport = new JButton( "Import All" ) );

		tbNodes.getSelectionModel().setSelectionMode( ListSelectionModel.SINGLE_SELECTION );
		txtSource.setEditable( false );
		
		tbNodes.getColumnModel().getColumn(0).setCellRenderer( this );
		tbNodes.getColumnModel().getColumn(1).setCellRenderer( this );
		
		txtSource.setLineWrap( true );
		txtUpdate.setLineWrap( true );				
	}

	@Override
	public void addNotify() {
		super.addNotify();
		btApply.addActionListener(this);
		btRun.addActionListener( this );
		btLLM.addActionListener( this );
		btCancel.addActionListener( this );
		btCopy.addActionListener( this );
		
		btExport.addActionListener( this );
		btImport.addActionListener( this );
		
		tbNodes.getSelectionModel().addListSelectionListener( this );
		txtUpdate.getDocument().addDocumentListener( this );

		this.getInputMap( JComponent.WHEN_IN_FOCUSED_WINDOW ).put( KeyStroke.getKeyStroke(KeyEvent.VK_UP, KeyEvent.CTRL_DOWN_MASK), "previousone" );
		this.getInputMap( JComponent.WHEN_IN_FOCUSED_WINDOW ).put( KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, KeyEvent.CTRL_DOWN_MASK), "previousnext" );

		this.getActionMap().put( "previousone", 
				new AbstractAction() {
					@Override
					public void actionPerformed(ActionEvent e) {
						int currentRow = tbNodes.getSelectedRow();
						if ( currentRow > 0 ) {
							tbNodes.getSelectionModel().setSelectionInterval( currentRow - 1, currentRow - 1 );
							copyCurrentUpdate();
						}
					}
				}
		);

		this.getActionMap().put( "previousnext", 
				new AbstractAction() {
					@Override
					public void actionPerformed(ActionEvent e) {
						int currentRow = tbNodes.getSelectedRow();
						if ( currentRow < tbNodes.getModel().getRowCount() ) {
							tbNodes.getSelectionModel().setSelectionInterval( currentRow + 1, currentRow + 1 );
							copyCurrentUpdate();
						}
					}
				}
		);

	}

	@Override
	public void removeNotify() {
		super.removeNotify();
		btApply.removeActionListener(this);		
		btRun.removeActionListener( this );
		btLLM.removeActionListener( this );
		btCancel.removeActionListener( this );
		btCopy.removeActionListener( this );
		
		btExport.removeActionListener( this );
		btImport.removeActionListener( this );
		
		tbNodes.getSelectionModel().removeListSelectionListener( this );
		txtUpdate.getDocument().removeDocumentListener( this );

		this.getInputMap( JComponent.WHEN_IN_FOCUSED_WINDOW ).remove( KeyStroke.getKeyStroke( KeyEvent.VK_UP, KeyEvent.CTRL_DOWN_MASK ) );
		this.getActionMap().remove( "previousone" );
		this.getInputMap( JComponent.WHEN_IN_FOCUSED_WINDOW ).remove( KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, KeyEvent.CTRL_DOWN_MASK ) );
		this.getActionMap().remove( "previousnext" );		
		
	}

	private PrompterPanel pp = null;

	@Override
	public void actionPerformed(ActionEvent e) {
		if ( e.getSource() == btRun ) {
			runXPath();
		} else
		if ( e.getSource() == btApply ) {
			if ( updates == null || nodes == null || updates.size() == 0 ) {
				EditixFactory.buildAndShowWarningDialog( "No update ?" );
			} else {
				int nbUpdate = updates.size();
				if ( EditixFactory.buildAndShowConfirmDialog( "Apply " + nbUpdate + " update(s) to your document ?" )  ) {
					for ( Node source : updates.keySet() ) {
						Node target = updates.get( source );
						source.getParentNode().replaceChild( target, source );
					}
					try {
						Transformer t = TransformerFactory.newInstance().newTransformer();
						t.setOutputProperty( OutputKeys.INDENT, "yes" );
						StringWriter writer = new StringWriter();
						t.transform( new DOMSource( doc ), new StreamResult( writer ) );
						EditixFrame.THIS.getSelectedContainer().setText( writer.toString() );
						runXPath();
					} catch( Exception exc ) {
						EditixFactory.buildAndShowErrorDialog( "Can't process your document [" + exc.getMessage() + "] ?" );
					}						
					updates = null;
				}															
			}
		} else
		if ( e.getSource() == btLLM ) {
			if ( pp == null )
				pp = new PrompterPanel();
			if ( DialogManager.showDialog(
				SwingUtilities.getWindowAncestor( this ), 
				"Prompter",
				"Prompt a request",
				"Ask to your LLM (go to [Options] to configure it), your source text is already inside the LLM context",
				null,
				pp,
				DialogActionModel.getDefaultDialogOkActionModel(),
				new Dimension( 600, 400 )
			) == XMLPadDialogManager.OK ) {
				LLM llm = pp.getSelectedLLM();
				if ( llm == null ) {
					EditixFactory.buildAndShowWarningDialog( "LLM is required ?" );
					return;
				}
				String prompt = pp.getPrompt();
				if ( prompt == null || "".equals( prompt ) ) {
					EditixFactory.buildAndShowConfirmDialog( "A prompt is required !" );
					return;
				}

				String instruction = "- Modify **only the selected text**.\n" +
                        "- Return **only the modified text**, no extra text or tags.";
				
				String contextType = "selection";
				
				String finalPrompt = String.format(
	                    "[CONTEXT: %s]%n" +
	                    "[CONTENT:%n%s%n]%n" +
	                    "[USER PROMPT: %s]%n" +
	                    "[INSTRUCTIONS:%n%s%n]",
	                    contextType,
	                    txtSource.getText(),
	                    prompt,
	                    instruction
	                );

				btLLM.setEnabled( false );
				new LLMRunner( llm, ( response ) -> { 
					btLLM.setEnabled( true );
					txtUpdate.setText( response );
				}).run( 
					this,
					finalPrompt
				);

			}
		} else
		if ( e.getSource() == btCancel ) {
			if ( EditixFactory.buildAndShowConfirmDialog( "Cancel the update ?" ) ) {
				txtUpdate.setText( txtSource.getText() );
			}
		} else
		if ( e.getSource() == btCopy ) {
			copyCurrentUpdate();
		} else
		if ( e.getSource() == btImport ) {
			importAll();
		} else
		if ( e.getSource() == btExport ) {
			exportAll();
		}
	}
	
	private void copyCurrentUpdate() {
		SwingUtilities.invokeLater( () -> {
			txtUpdate.requestFocus();
			txtUpdate.selectAll();
			txtUpdate.copy();
			txtUpdate.setCaretPosition( 0 );
		} );		
	}

	private void exportAll() {
		if ( nodes == null || nodes.size() == 0 ) {
			EditixFactory.buildAndShowWarningDialog( "No nodes, must run XPath ?" );
		} else {
			try {
				DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
				Document doc = builder.newDocument();
				Element root = doc.createElement( "fragment" );
				doc.appendChild(root);
				root.setAttribute( "xpath", txXPath.getText() );
				for ( int row = 0; row < tbNodes.getRowCount(); row++ ) {
					Element text = doc.createElement( "text" );
					text.setAttribute( "row", Integer.toString( row ) );
					text.setTextContent( (String)tbNodes.getModel().getValueAt( row, 1 ));
					root.appendChild( text );
				}
				File f = FileManager.getSelectedFile( false, "xml", "XPath fragment export" );
				if ( f != null ) {
					Transformer t = TransformerFactory.newInstance().newTransformer();
					t.setOutputProperty(OutputKeys.INDENT, "yes" );
					t.transform( new DOMSource( doc ), new StreamResult( f ) ); 
				}
			} catch( Exception exc ) {
				EditixFactory.buildAndShowErrorDialog( "Can't export your texts [" + exc.getMessage() );
			}					
		}
	}

	private void importAll() {
		File f = FileManager.getSelectedFile( true, "xml", "XPath import" );
		if ( f!= null ) {
			try {
				DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
				Document document = db.parse( f );
				Element root = document.getDocumentElement();
				String xpath = root.getAttribute( "xpath" );
				if ( "".equals( xpath ) || xpath == null )
					throw new Exception( "Can't find your XPath request (attribute xpath is required at the root) ?" );
				txXPath.setText( xpath );
				if ( !runXPath() ) {
					throw new Exception( "Invalid xpath query" );
				} else {
					// Import each row
					String line = null;

					NodeList nl = root.getElementsByTagName( "text" );

					for ( int i = 0; i < nl.getLength(); i++ ) {
						Element text = (Element)nl.item( i );
						int rowNumber = Integer.parseInt( text.getAttribute( "row" ) );
						tbNodes.getSelectionModel().setSelectionInterval( rowNumber, rowNumber );
						txtUpdate.setText( text.getTextContent() );
					}
				}
			} catch( Exception exc ) {
				EditixFactory.buildAndShowConfirmDialog( "Can't import your texts [" + exc.getMessage() + "]" );
			}
		}
	}
	
	// TableCellRenderer

	private JLabel tbRenderer = null;
	private Color updatedColor = Color.GREEN.darker();

	@Override
	public Component getTableCellRendererComponent(
			JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
		if ( tbRenderer == null ) {
			tbRenderer = new JLabel();
			tbRenderer.setOpaque( true );
		}
		if ( value == null )
			tbRenderer.setText( "Error??" );
		else
			tbRenderer.setText( value.toString() );
		if ( isSelected ) {
			tbRenderer.setBackground( table.getSelectionBackground() );
			tbRenderer.setForeground( table.getSelectionForeground() );
		} else {
			tbRenderer.setBackground( table.getBackground() );
			tbRenderer.setForeground( table.getForeground() );

			if ( updates != null ) {
				Node sourceNode = nodes.get( row );
				if ( updates.containsKey( sourceNode ) ) {
					tbRenderer.setBackground( updatedColor );
					tbRenderer.setForeground( Color.WHITE );
				}
			}
		}
		return tbRenderer;
	}

	// Document listener

	boolean isUpdating = false;
	
	@Override
	public void changedUpdate(DocumentEvent e) {
	}
	
	private void updateMode( DocumentEvent e ) {
		if ( !isUpdating ) {
			int currentRow = tbNodes.getSelectedRow();
			Node n = nodes.get( currentRow );
			if ( updates == null )
				updates = new HashMap<Node, Node>();

			Node updateNode = updates.get( n );
			if ( updateNode != null ) {
			} else {
				updateNode = n.cloneNode( true );
				updates.put( n, updateNode );
			}

			try {
				String newText = e.getDocument().getText( 0, e.getDocument().getLength() );
				String currentText = txtSource.getText();

				if ( !newText.equals( currentText ) ) {
					updateNode.setTextContent( newText );
				} else {
					updates.remove( n );
				}
				SwingUtilities.invokeLater( () -> tbNodes.repaint() );
			} catch( BadLocationException exc ) {				
			}
		}		
	}

	@Override
	public void insertUpdate(DocumentEvent e) {
		updateMode( e );
	}

	@Override
	public void removeUpdate(DocumentEvent e) {
		updateMode( e );
	}	

	// Table selection

	@Override
	public void valueChanged(ListSelectionEvent e) {		
		int row = tbNodes.getSelectedRow();
		if ( nodes.size() < row || row == -1 ) return;
		
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
		
		isUpdating = false;
	}

	private Document doc;
	
	private boolean runXPath() {
		XMLContainer container = EditixFrame.THIS.getSelectedContainer();
		if ( container == null ) {
			EditixFactory.buildAndShowErrorDialog( "Can't find your document ?" );
			return false;
		}

		XPathFactory xpathFactory = new net.sf.saxon.xpath.XPathFactoryImpl();
        XPath xpath = xpathFactory.newXPath();
		
		try {
			
			if ( doc == null ) {
				try {
					DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
					doc = db.parse( new InputSource( new StringReader( container.getText() )) );
				} catch( Exception exc ) {
					EditixFactory.buildAndShowErrorDialog( "Can't use your XML document [" + exc.getMessage() + "] ?" );
					return false;
				}
			}

			NodeList nl = (NodeList)xpath.evaluate( 
				txXPath.getText(),
				new DOMSource( doc ),
				XPathConstants.NODESET
			);

			if ( nl.getLength() == 0 ) {
				
				EditixFactory.buildAndShowWarningDialog(  "No result" );
				
			} else {
			
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

				SwingUtilities.invokeLater( () -> l.tableChanged( new TableModelEvent( this ) ) );
				tbNodes.getColumnModel().getColumn( 0 ).setMaxWidth( 100 );

			}
			
			return true;
			
		} catch( XPathExpressionException exc ) {
			EditixFactory.buildAndShowWarningDialog( "Invalid xpath expression : " + exc.getMessage() );
			return false;
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
		return "Source text";
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
				return "@" + n.getNodeName();
			} else {
				if ( n instanceof Text ) {
					if ( n.getParentNode() != null )
						return n.getParentNode().getNodeName();
					else
						return "??";
				}
				return n.getNodeName();
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
