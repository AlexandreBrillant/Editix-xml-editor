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

package com.japisoft.editix.ui.panels.xpath;

import java.awt.Color;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.transform.SourceLocator;
import javax.xml.transform.stream.StreamSource;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import javax.xml.xpath.XPathVariableResolver;

import net.sf.saxon.Configuration;
import net.sf.saxon.om.NodeInfo;
import net.sf.saxon.om.SequenceIterator;
import net.sf.saxon.query.DynamicQueryContext;
import net.sf.saxon.query.StaticQueryContext;
import net.sf.saxon.query.XQueryExpression;
import net.sf.saxon.trans.XPathException;

/*import org.jaxen.SimpleNamespaceContext;
import org.jaxen.SimpleVariableContext;
import org.jaxen.XPath;
import org.jaxen.XPathSyntaxException;
import org.jaxen.dom.DOMXPath; */
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

import com.japisoft.editix.editor.xquery.XQueryEditor;
import com.japisoft.editix.ui.EditixFactory;
import com.japisoft.editix.ui.EditixFrame;
import com.japisoft.framework.ui.table.ExportableTable;
import com.japisoft.framework.ui.table.StringTableCellRenderer;
import com.japisoft.framework.ui.toolkit.FileManager;
import com.japisoft.framework.xml.parser.FPParser;
import com.japisoft.framework.xml.parser.document.Document;

import com.japisoft.framework.xml.parser.dom.DocumentImpl;
import com.japisoft.framework.xml.parser.dom.DomNodeFactory;
import com.japisoft.framework.xml.parser.node.FPNode;
import com.japisoft.xmlpad.XMLContainer;

public class XPathUI extends JPanel implements 
		ActionListener,
		ListSelectionListener,
		MouseListener,
		MouseMotionListener {

	XPathUI() {
		initComponents();
		tbHistory.getSelectionModel().setSelectionMode(
				ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		tbNamespace.getSelectionModel().setSelectionMode(
				ListSelectionModel.SINGLE_SELECTION);
		tbResult.getSelectionModel().setSelectionMode(
				ListSelectionModel.SINGLE_SELECTION);
		tbVar.getSelectionModel().setSelectionMode(
				ListSelectionModel.SINGLE_SELECTION);
		unshowSyntaxError();
		
		getActionMap().put( "run-current", new RunFromCurrentAction() );
		getActionMap().put( "run-root", new RunFromRootAction() );
		
		getInputMap( JPanel.WHEN_IN_FOCUSED_WINDOW ).put( 
				KeyStroke.getKeyStroke( KeyEvent.VK_ENTER, KeyEvent.CTRL_DOWN_MASK), "run-root" );

		getInputMap( JPanel.WHEN_IN_FOCUSED_WINDOW ).put( 
				KeyStroke.getKeyStroke( KeyEvent.VK_ENTER, KeyEvent.CTRL_DOWN_MASK | KeyEvent.SHIFT_DOWN_MASK), "run-current" );

		taXPath.setDisplayRowHeader( false );
		
		Icon i = com.japisoft.framework.ui.Toolkit.getIconFromClasspath("images/media_play.png");
		// btRunFromCurrent.setIcon( i );
		btRunFromCurrent.setBorder( null );
		btRunFromCurrent.setEnabled( false );
		btRunFromRoot.setIcon( i );
		
	}

	public void addNotify() {
		super.addNotify();
		btRunFromCurrent.addActionListener(this);
		btRunFromRoot.addActionListener(this);
		btCopy.addActionListener(this);
		tbResult.getSelectionModel().addListSelectionListener(this);
		tbHistory.getSelectionModel().addListSelectionListener(
				hs = new HistorySelection());
		tbHistory.addMouseListener( this );
		tbHistory.addMouseMotionListener( this );
		tbResult.addMouseMotionListener( this );
		jRadioButton1.addActionListener( this );
		jRadioButton2.addActionListener( this );		
	}

	public void removeNotify() {
		super.removeNotify();
		btRunFromCurrent.removeActionListener(this);
		btRunFromRoot.removeActionListener(this);
		btCopy.removeActionListener(this);
		tbResult.getSelectionModel().removeListSelectionListener(this);
		tbHistory.getSelectionModel().removeListSelectionListener(hs);
		tbHistory.removeMouseListener( this );
		tbHistory.removeMouseMotionListener( this );
		tbResult.removeMouseMotionListener( this );
		jRadioButton1.removeActionListener( this );
		jRadioButton2.removeActionListener( this );
	}

	// <editor-fold defaultstate="collapsed" desc=" Generated Code ">
	private void initComponents() {
		bgVersion = new javax.swing.ButtonGroup();
		jLabel1 = new javax.swing.JLabel();
		// spXPath = new javax.swing.JScrollPane();
		taXPath = new XQueryEditor();
		btRunFromRoot = new javax.swing.JButton();
		btRunFromCurrent = new javax.swing.JButton();
		btCopy = new javax.swing.JButton();
		jRadioButton1 = new javax.swing.JRadioButton();
		jRadioButton2 = new javax.swing.JRadioButton();
		jTabbedPane1 = new javax.swing.JTabbedPane();
		pnResult = new javax.swing.JPanel();
		spResult = new javax.swing.JScrollPane();
		tbResult = new ExportableTable() {
			public boolean isCellEditable(int arg0, int arg1) {
				return false;
			}
		};
		pnVar = new javax.swing.JPanel();
		spVar = new javax.swing.JScrollPane();
		tbVar = new ExportableTable();
		pnNamespace = new javax.swing.JPanel();
		spNamespace = new javax.swing.JScrollPane();
		tbNamespace = new ExportableTable();
		pnHistory = new javax.swing.JPanel();
		spHistory = new javax.swing.JScrollPane();
		tbHistory = new ExportableTable() {
			public boolean isCellEditable(int arg0, int arg1) {
				return false;
			}
		};
		
		jLabel1.setText( "<html><body>XPath expression<br><font size='-2'>Use <b>Ctrl-enter</b> for running</font></body></html>" );
		
		//spXPath.setViewportView(taXPath.getView());

		btRunFromRoot.setText( "Run" );
		// btRunFromCurrent.setText("From current");
		// btCopy.setText("Copy");
		btCopy.setIcon( com.japisoft.framework.app.toolkit.Toolkit.getImageIcon( "images/copy.png" ) );

		bgVersion.add(jRadioButton1);
		jRadioButton1.setSelected(true);
		jRadioButton1.setText("1.0");
		jRadioButton1.setBorder(javax.swing.BorderFactory.createEmptyBorder(0,
				0, 0, 0));
		jRadioButton1.setMargin(new java.awt.Insets(0, 0, 0, 0));

		bgVersion.add(jRadioButton2);
		jRadioButton2.setText("2.0/3.0");
		jRadioButton2.setBorder(javax.swing.BorderFactory.createEmptyBorder(0,
				0, 0, 0));
		jRadioButton2.setMargin(new java.awt.Insets(0, 0, 0, 0));

		tbResult.setModel(new javax.swing.table.DefaultTableModel(new Object[] {
				"Node", "Value" }, 0));

		spResult.setViewportView(tbResult);

		org.jdesktop.layout.GroupLayout pnResultLayout = new org.jdesktop.layout.GroupLayout(
				pnResult);
		pnResult.setLayout(pnResultLayout);
		pnResultLayout.setHorizontalGroup(pnResultLayout.createParallelGroup(
				org.jdesktop.layout.GroupLayout.LEADING).add(spResult,
				org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 215,
				Short.MAX_VALUE));
		pnResultLayout.setVerticalGroup(pnResultLayout.createParallelGroup(
				org.jdesktop.layout.GroupLayout.LEADING).add(spResult,
				org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 104,
				Short.MAX_VALUE));
		jTabbedPane1.addTab("Result", pnResult);

		tbVar.setModel(new javax.swing.table.DefaultTableModel(new Object[] {
				"Variable", "Value" }, 20));
		spVar.setViewportView(tbVar);

		org.jdesktop.layout.GroupLayout pnVarLayout = new org.jdesktop.layout.GroupLayout(
				pnVar);
		pnVar.setLayout(pnVarLayout);
		pnVarLayout.setHorizontalGroup(pnVarLayout.createParallelGroup(
				org.jdesktop.layout.GroupLayout.LEADING).add(spVar,
				org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 215,
				Short.MAX_VALUE));
		pnVarLayout.setVerticalGroup(pnVarLayout.createParallelGroup(
				org.jdesktop.layout.GroupLayout.LEADING).add(spVar,
				org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 104,
				Short.MAX_VALUE));
		jTabbedPane1.addTab("Variable", pnVar);

		tbNamespace.setModel(new javax.swing.table.DefaultTableModel(
				new Object[] { "Prefix", "Namespace" }, 20));
		spNamespace.setViewportView(tbNamespace);

		org.jdesktop.layout.GroupLayout pnNamespaceLayout = new org.jdesktop.layout.GroupLayout(
				pnNamespace);
		pnNamespace.setLayout(pnNamespaceLayout);
		pnNamespaceLayout.setHorizontalGroup(pnNamespaceLayout
				.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
				.add(spNamespace, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
						215, Short.MAX_VALUE));
		pnNamespaceLayout.setVerticalGroup(pnNamespaceLayout
				.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
				.add(spNamespace, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
						104, Short.MAX_VALUE));
		jTabbedPane1.addTab("Namespace", pnNamespace);

		tbHistory.setModel(new javax.swing.table.DefaultTableModel(
				new Object[] { "Last" }, 30));
		spHistory.setViewportView(tbHistory);

		org.jdesktop.layout.GroupLayout pnHistoryLayout = new org.jdesktop.layout.GroupLayout(
				pnHistory);
		pnHistory.setLayout(pnHistoryLayout);
		pnHistoryLayout.setHorizontalGroup(pnHistoryLayout.createParallelGroup(
				org.jdesktop.layout.GroupLayout.LEADING).add(spHistory,
				org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 215,
				Short.MAX_VALUE));
		pnHistoryLayout.setVerticalGroup(pnHistoryLayout.createParallelGroup(
				org.jdesktop.layout.GroupLayout.LEADING).add(spHistory,
				org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 104,
				Short.MAX_VALUE));
		jTabbedPane1.addTab("History", pnHistory);

		org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(
				this);
		this.setLayout(layout);
		layout
				.setHorizontalGroup(layout
						.createParallelGroup(
								org.jdesktop.layout.GroupLayout.LEADING)
						.add(
								org.jdesktop.layout.GroupLayout.TRAILING,
								layout
										.createSequentialGroup()
										.addContainerGap()
										.add(
												layout
														.createParallelGroup(
																org.jdesktop.layout.GroupLayout.TRAILING)
														.add(
																org.jdesktop.layout.GroupLayout.LEADING,
																jTabbedPane1,
																org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
																220,
																Short.MAX_VALUE)
														.add(
																org.jdesktop.layout.GroupLayout.LEADING,
																taXPath.getView(),
																org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
																220,
																Short.MAX_VALUE)
														.add(
																org.jdesktop.layout.GroupLayout.LEADING,
																layout
																		.createSequentialGroup()
																		.add(
																				jLabel1)
																		.addPreferredGap(
																				org.jdesktop.layout.LayoutStyle.RELATED,
																				80,
																				Short.MAX_VALUE)
																		.add(
																				btCopy))
														.add(
																org.jdesktop.layout.GroupLayout.LEADING,
																layout
																		.createSequentialGroup()
																		.add(
																				btRunFromRoot)
																		.addPreferredGap(
																				org.jdesktop.layout.LayoutStyle.RELATED)
																		.add(
																				btRunFromCurrent))
														.add(
																org.jdesktop.layout.GroupLayout.LEADING,
																layout
																		.createSequentialGroup()
																		.add(
																				jRadioButton1)
																		.add(
																				14,
																				14,
																				14)
																		.add(
																				jRadioButton2)))
										.addContainerGap()));
		layout
				.setVerticalGroup(layout
						.createParallelGroup(
								org.jdesktop.layout.GroupLayout.LEADING)
						.add(
								layout
										.createSequentialGroup()
										.addContainerGap()
										.add(
												layout
														.createParallelGroup(
																org.jdesktop.layout.GroupLayout.BASELINE)
														.add(jLabel1).add(
																btCopy))
										.addPreferredGap(
												org.jdesktop.layout.LayoutStyle.RELATED)
										.add(
												taXPath.getView(),
												org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
												90,
												org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
										.addPreferredGap(
												org.jdesktop.layout.LayoutStyle.RELATED)
										.add(
												layout
														.createParallelGroup(
																org.jdesktop.layout.GroupLayout.BASELINE)
														.add(btRunFromRoot)
														.add(btRunFromCurrent))
										.addPreferredGap(
												org.jdesktop.layout.LayoutStyle.RELATED)
										.add(
												layout
														.createParallelGroup(
																org.jdesktop.layout.GroupLayout.BASELINE)
														.add(jRadioButton1)
														.add(jRadioButton2))
										.addPreferredGap(
												org.jdesktop.layout.LayoutStyle.RELATED)
										.add(
												jTabbedPane1,
												org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
												132, Short.MAX_VALUE)
										.addContainerGap()));
	}// </editor-fold>

	public void actionPerformed(ActionEvent arg0) {
		if ( arg0.getSource() == jRadioButton1 ) {
			btRunFromCurrent.setEnabled( jRadioButton1.isSelected() );
			jTabbedPane1.setEnabledAt( 1, jRadioButton1.isSelected() );
			jTabbedPane1.setEnabledAt( 2, jRadioButton1.isSelected() );
		} else
		if ( arg0.getSource() == jRadioButton2 ) {
			btRunFromCurrent.setEnabled( jRadioButton1.isSelected() );
			jTabbedPane1.setEnabledAt( 1, jRadioButton1.isSelected() );
			jTabbedPane1.setEnabledAt( 2, jRadioButton1.isSelected() );			
		} else
		if (arg0.getSource() == btCopy) {
			copy();
		} else {			
			currentResultList = null;
			jTabbedPane1.setSelectedIndex(0);
			unshowSyntaxError();
			if (arg0.getSource() == btRunFromCurrent) {
				runFromCurrent();
			} else if (arg0.getSource() == btRunFromRoot) {
				runFromRoot();
			}
			DefaultTableModel model = (DefaultTableModel) tbResult.getModel();
			if( model.getRowCount() > 0 ) {
				tbResult.getSelectionModel().setSelectionInterval( 0, 0 );
				tbResult.requestFocus();
			}
		}
	}

	private void cleanResultModel() {
		DefaultTableModel model = (DefaultTableModel) tbResult.getModel();
		while (model.getRowCount() > 0)
			model.removeRow(0);					
	}
	
	public void valueChanged(ListSelectionEvent arg0) {
		if ( tbResult.getSelectedRow() == -1 )
			return;
		if (currentResultList != null) {
			
			int row = tbResult.convertRowIndexToModel( tbResult.getSelectedRow() );
			
			Object o = currentResultList.get( row );
			if (currentFileLocation != null)
				EditixFrame.THIS
						.activeXMLContainer( currentFileLocation );			
			if (o instanceof Node) {
				Node n = (Node) o;
				FPNode sn = (FPNode) n;
				XMLContainer container = EditixFrame.THIS
						.getSelectedSubContainer( "XML" );
				if (container != null) {
					container.getEditor().highlightLine( sn.getStartingLine() );
					container.getEditor().setCaretPosition( sn.getStartingOffset() + 1 );
				}
			} else
			if ( o instanceof NodeInfo ) {
				NodeInfo ni = ( NodeInfo )o;
				XMLContainer container = EditixFrame.THIS
				.getSelectedContainer();
				if (container != null) {
					container.getEditor().highlightLine( ni.getLineNumber() );
					container.getEditor().setLineNumber( ni.getLineNumber() );
				}				
			}
		}
	}

	private void copy() {
		Toolkit.getDefaultToolkit().getSystemClipboard().setContents(
				new StringSelection("" + taXPath.getText()), null);
	}

	private void runFromCurrent() {		
		cleanResultModel();
		try {
			XMLContainer container = EditixFrame.THIS
					.getSelectedSubContainer( "XML" );
			if ( container == null )
				container = EditixFrame.THIS.getSelectedContainer();
			if ( container == null )
				throw new RuntimeException(
						"No selected document. Open a document" );
			FPNode n = container.getCurrentNode();
			if (n == null) {
				throw new RuntimeException( "No selected node" );
			} else {
				String xp = n.getXPathLocation();
				FPNode n2 = (FPNode) getNodeFromRoot();
				n = n2.getNodeForXPathLocation(xp, false);
				if ( n == null )
					n = n2;
			}
			if ( jRadioButton2.isSelected() ) {
				runForXPath2_0( ( Node ) n );
			} else {
				runForXPath1_0( ( Node ) n );
			}
		} catch (Throwable e) {
			e.printStackTrace();
			processError(e);
		}
	}

	public void runFromRoot() {

		cleanResultModel();
		try {
			Node n = new DocumentImpl( ( Element )getNodeFromRoot() );
			if ( jRadioButton2.isSelected() ) {
				runForXPath2_0( n );
			} else {
				runForXPath1_0( n );
			}
		} catch ( Throwable e ) {
			processError( e );
		}
	}

	private void processError( Throwable e ) {
		
		if ( e instanceof XPathExpressionException ) {
			
			XPathExpressionException xee = ( XPathExpressionException )e;
			
			if ( xee.getCause() != null )
				showSyntaxError( 0, xee.getCause().getMessage() );
			else
				showSyntaxError( 0, xee.getMessage() );

		} else
		
		if ( e instanceof XPathException ) {

			XPathException ex = ( XPathException )e;

			if ( ex.getLocator() != null ) {
			
				// Get the line offset
				javax.swing.text.Element ep = taXPath.getDocument().getDefaultRootElement().getElement(
						ex.getLocator().getLineNumber() );
				
				showSyntaxError( ( ep != null ? ep.getStartOffset() : 0 ) + ex.getLocator().getColumnNumber(), ex.getMessage() );
			
			} else
				
				showSyntaxError( 0, ex.getMessage() );
			
		} else
			
		if ( e instanceof org.apache.xpath.XPathException ) {
				
				org.apache.xpath.XPathException xpe = (org.apache.xpath.XPathException)e;
				SourceLocator locator = xpe.getLocator();
				if ( locator != null ) {
					javax.swing.text.Element ep = taXPath.getDocument().getDefaultRootElement().getElement(
							locator.getLineNumber() );
					
					showSyntaxError( locator.getColumnNumber() + ep.getStartOffset(), e.getMessage() );
					
				} else
					
					showSyntaxError( 0, e.getMessage() );
				
		} else
			
			showSyntaxError( 0, e.getMessage() );
			
	}

	private void runForXPath2_0( Node currentNode ) throws Throwable {
		Configuration config = new Configuration();
		config.setLineNumbering( true );
		config.setValidation( false );
		
		StaticQueryContext staticContext = 
		        new StaticQueryContext( config );		
		XQueryExpression exp = staticContext.compileQuery( taXPath.getText() );
		DynamicQueryContext dynamicContext = 
		        new DynamicQueryContext( config );
		
		XMLContainer container = EditixFrame.THIS
		.getSelectedSubContainer("XML");
		if (container == null)
			container = EditixFrame.THIS.getSelectedContainer();		
		
		dynamicContext.setContextNode( staticContext.buildDocument(
				new StreamSource( 
						new StringReader( 
								container.getAccessibility().getText() ), container.getCurrentDocumentLocation() )
		) );

		SequenceIterator res = exp.iterator( dynamicContext );
		ArrayList resLst = new ArrayList();
		currentResultList = resLst;
		while ( true ) {
		    Object node = res.next();
		    if ( node == null ) 
		    	break;
		    resLst.add( node );
		}

		showResult( resLst );
	}

	public void setXPath( String xpath, int version ) {
		if( version == 1 )
			jRadioButton1.setSelected( true );
		else 
			jRadioButton2.setSelected( true );
		taXPath.setText( xpath );
	}
	
	private void runForXPath1_0( Node currentNode ) throws Throwable {
		XPath xpath = XPathFactory.newInstance().newXPath();
		
		MapVariableResolver variables = new MapVariableResolver();
		TableModel modelVariables = tbVar.getModel();

		// Check for variables
		for (int i = 0; i < modelVariables.getRowCount(); i++) {
			String name = (String) modelVariables.getValueAt(i, 0);
			if (name != null && !"".equals(name) && name.length() > 0) {
				String value = (String) modelVariables.getValueAt(i, 1);
				if (value == null)
					continue;
				variables.addVariable( name, value );
			}
		}

		xpath.setXPathVariableResolver( variables );
		
		// Check for namespaces
		
		
		NamespaceContextResolver nsCtx = new NamespaceContextResolver();
		
		TableModel modelNamespaces = tbNamespace.getModel();

		for (int i = 0; i < modelNamespaces.getRowCount(); i++) {
			String alias = (String) modelNamespaces.getValueAt(i, 0);
			if (alias != null && !"".equals(alias) && alias.length() > 0) {
				String uri = (String) modelNamespaces.getValueAt(i, 1);
				if (uri == null)
					continue;
				nsCtx.addNamespace(alias, uri);
			}
		}
		
		xpath.setNamespaceContext( nsCtx );
		
		showResult( xpath.evaluate(
			taXPath.getText(),
			currentNode,
			XPathConstants.NODESET
		) );
		
		/*
		XPath xpath = new DOMXPath(taXPath.getText());
		SimpleVariableContext context = null;

		TableModel modelVariables = tbVar.getModel();

		// Check for variables
		for (int i = 0; i < modelVariables.getRowCount(); i++) {
			String name = (String) modelVariables.getValueAt(i, 0);
			if (name != null && !"".equals(name) && name.length() > 0) {
				if (context == null) {
					context = new SimpleVariableContext();
					xpath.setVariableContext(context);
				}
				String value = (String) modelVariables.getValueAt(i, 1);
				if (value == null)
					continue;
				context.setVariableValue(name, value);
			}
		}

		SimpleNamespaceContext context2 = null;
		TableModel modelNamespaces = tbNamespace.getModel();

		// Check for namespaces
		for (int i = 0; i < modelNamespaces.getRowCount(); i++) {
			String alias = (String) modelNamespaces.getValueAt(i, 0);
			if (alias != null && !"".equals(alias) && alias.length() > 0) {
				String uri = (String) modelNamespaces.getValueAt(i, 1);
				if (uri == null)
					continue;
				if (context2 == null) {
					context2 = new SimpleNamespaceContext();
					xpath.setNamespaceContext(context2);
				}
				context2.addNamespace(alias, uri);
			}
		}

		Object o = xpath.evaluate(currentNode);
		showResult(o);
		*/
	}

	private List currentResultList = null;

	private String currentFileLocation = null;

	private void showResult( Object result ) {
		if ( result instanceof NodeList ) {
			List tmp = new ArrayList();
			NodeList nl = ( NodeList )result;
			for ( int i = 0; i < nl.getLength(); i++ )
				tmp.add( nl.item( i ) );
			result = tmp;
		}
		
		if (result instanceof List) {
			DefaultTableModel model = (DefaultTableModel) tbResult.getModel();
			StringTableCellRenderer.fillIt( tbResult );
			List l = (List) result;
			currentResultList = (List) result;
			if ( l.size() == 0 ) {
				EditixFactory.buildAndShowInformationDialog( "No result" );
			} else {
				
				EditixFactory.buildAndShowInformationDialog( l.size() + " result(s)" );
				
				for (int i = 0; i < l.size(); i++) {
					Object o = l.get(i);
					
					if ( o instanceof NodeInfo ) {
	
						NodeInfo ni = ( NodeInfo )o;
						
						model.addRow(new Object[] { ni.getDisplayName(), ni.getStringValue() } );
						
					} else {
					
						if (o instanceof Node) {
							
							String name = ((Node) o).getNodeName();
							String value =((Node) o).getNodeValue(); 
							
							if ( value == null ) {
								// Can take the text node ?
								Node n = ( Node )o;
								if ( n.getFirstChild() != null ) {
									Node n2 = n.getFirstChild();
									if ( n2 instanceof Text ) {
										value = ( ( Text )n2 ).getNodeValue();
									}
								}
							}

							model.addRow(new Object[] {
									name,
									value });
						} else {
							model.addRow(new Object[] { "[Not a node]", o });
						}
					
					}
				}
			}
			
			TableRowSorter<TableModel> sorter = 
				new TableRowSorter<TableModel>( model );
			
			sorter.setComparator( 0, 
				new Comparator<Object>() {
					public int compare( Object o1, Object o2) {
						if ( o1 instanceof FPNode && o2 instanceof FPNode ) {
							o1 = ( ( FPNode )o1 ).getContent();
							o2 = ( ( FPNode )o2 ).getContent();
						}
						if ( o1 instanceof Comparable && o2 instanceof Comparable ) {
							return ( ( Comparable )o1 ).compareTo( o2 );
						}
						return 0;
					}
				} 
			);

			tbResult.setRowSorter( sorter ); 
			
		} else {
			EditixFactory.buildAndShowInformationDialog("Result = " + result);
		}
		addToHistory( taXPath.getText() );
	}

	private void addToHistory( String xpath ) {
		TableModel tm = 
			tbHistory.getModel();
		for ( int i = 0; i < tm.getRowCount(); i++ ) {
			String xpathStr = ( String )tm.getValueAt( i, 0 );
			if ( xpath.equals( xpathStr ) )
				return;
		}
		tbHistory.getSelectionModel().removeListSelectionListener( hs );

		// Add it in the history
		((DefaultTableModel) tbHistory.getModel()).insertRow(0,
				new Object[] { taXPath.getText() });
		if (tbHistory.getModel().getRowCount() > 30)
			((DefaultTableModel) tbHistory.getModel()).removeRow(30);
		
		tbHistory.getSelectionModel().addListSelectionListener( hs );		
	}
	
	private Node getNodeFromRoot() throws Throwable {
		if (taXPath.getText() == null) {
			throw new RuntimeException("No expression found");
		}
		XMLContainer container = EditixFrame.THIS
				.getSelectedSubContainer("XML");
		if (container == null)
			container = EditixFrame.THIS.getSelectedContainer();
		if (container == null)
			throw new RuntimeException("No selected document. Open a document");

		currentFileLocation = container.getCurrentDocumentLocation();
		String document = container.getText();
		FPParser p = new FPParser();
		p.setNodeFactory(new DomNodeFactory());	
		Document d = p.parse(new StringReader(document));
		return ( Node )d.getRoot();
	}

	private void unshowSyntaxError() {
		taXPath.getEditor().setCaretColor( oldColor );
	}

	private Color oldColor = null;
	
	private void showSyntaxError(int position, String message) {
		taXPath.getEditor().setCaretPosition(position);
		if ( oldColor == null )
			oldColor = taXPath.getEditor().getCaretColor();
		taXPath.getEditor().setCaretColor(Color.red);
		EditixFactory.buildAndShowErrorDialog(message);
		taXPath.requestFocus();
	}

	// Variables declaration - do not modify
	private javax.swing.ButtonGroup bgVersion;
	private javax.swing.JButton btRunFromRoot;
	private javax.swing.JButton btRunFromCurrent;
	private javax.swing.JButton btCopy;
	private javax.swing.JLabel jLabel1;
	private javax.swing.JRadioButton jRadioButton1;
	private javax.swing.JRadioButton jRadioButton2;
	private javax.swing.JTabbedPane jTabbedPane1;
	private javax.swing.JPanel pnHistory;
	private javax.swing.JPanel pnNamespace;
	private javax.swing.JPanel pnResult;
	private javax.swing.JPanel pnVar;
	private javax.swing.JScrollPane spHistory;
	private javax.swing.JScrollPane spNamespace;
	private javax.swing.JScrollPane spResult;
	private javax.swing.JScrollPane spVar;
	private XQueryEditor taXPath;
	private javax.swing.JTable tbHistory;
	private javax.swing.JTable tbNamespace;
	private javax.swing.JTable tbResult;
	private javax.swing.JTable tbVar;
	
	// End of variables declaration

	class RunFromCurrentAction extends AbstractAction {
		public void actionPerformed(ActionEvent e) {
			btRunFromCurrent.doClick();
		}
	}

	class RunFromRootAction extends AbstractAction {
		public void actionPerformed(ActionEvent e) {
			btRunFromRoot.doClick();
		}
	}

	private HistorySelection hs;

	class HistorySelection implements ListSelectionListener {
		public void valueChanged(ListSelectionEvent arg0) {
			try {
				taXPath.setText((String) tbHistory.getModel().getValueAt(
						tbHistory.getSelectedRow(), 0));
			} catch( Throwable th ) {}
		}
	}

	class ExportHistoryAction extends AbstractAction {
		ExportHistoryAction() {
			putValue( Action.NAME, "Export" );
			putValue( Action.SHORT_DESCRIPTION, "Export to a TXT file" );
		}

		public void actionPerformed(ActionEvent e) {
			File f = FileManager.getSelectedFile( false, "txt", "XPath list" );
			if ( f != null ) {
				TableModel model = tbHistory.getModel();
				try {
					FileWriter fw = new FileWriter( f );
					BufferedWriter bw = new BufferedWriter( fw );
					try {
						for ( int i = 0; i < model.getRowCount(); i++ ) {
							if ( model.getValueAt( i, 0 ) != null ) {
								if ( i > 0 )
									bw.newLine();								
								bw.write( ( String )model.getValueAt( i, 0 ) );
							}
						}
					} finally {
						bw.close();
					}
				} catch (IOException e1) {
				}
			}
		}
	}

	class ImportHistoryAction extends AbstractAction {
		
		ImportHistoryAction() {
			putValue( Action.NAME, "Import" );
			putValue( Action.SHORT_DESCRIPTION, "Import a TXT file with one expression by line. Lines with (: and :) are ignored" );
		}

		public void actionPerformed( ActionEvent e ) {
			File f = FileManager.getSelectedFile( 
					true, 
					"txt", 
					"XPath list" );
			if ( f != null ) {
				try {
					BufferedReader br = new BufferedReader(
							new FileReader( f ) );
					try {
						String line = null;
						DefaultTableModel model = new DefaultTableModel(
								new Object[] { "Last expressions" }, 0 );	
						while ( ( line = br.readLine() ) != null ) {
							
							// Skip comment
							if ( line.contains( "(:" ) && 
									line.contains( ":)" ) )
								continue;
							
							if ( "".equals( line ) )
								continue;
							
							model.addRow(
									new Object[] {
											line.trim()
									} );

						}
						tbHistory.setModel( model );
					} finally {
						br.close();
					}
				} catch (FileNotFoundException e1) {
				} catch (IOException e1) {
				}
			}			
		}
	}

	class CleanAllHistoryAction extends AbstractAction {

		CleanAllHistoryAction() {
			putValue( Action.NAME, "Clean all" );
			putValue( Action.SHORT_DESCRIPTION, "Remove all the expressions" );
		}

		public void actionPerformed(ActionEvent e) {	
			DefaultTableModel model = new DefaultTableModel(
					new Object[] { "Last expressions" }, 30 );	
			tbHistory.setModel( model );
		}
	}

	class CleanCurrentHistoryAction extends AbstractAction {
		CleanCurrentHistoryAction() {
			putValue( Action.NAME, "Clean the selected expression(s)" );
			putValue( Action.SHORT_DESCRIPTION, "Remove the selected expression(s)" );
		}

		public void actionPerformed(ActionEvent e) {
			int[] row = tbHistory.getSelectedRows();
			if ( row != null && row.length > 0 ) {
				for ( int j = row.length -1; j >= 0; j-- ) {
					((DefaultTableModel)tbHistory.getModel()).removeRow( row[ j ] );
				}
			}
		}
	}

	class CopyHistoryAction extends AbstractAction {
		public CopyHistoryAction() {
			putValue( Action.NAME, 
					"Copy" 
			);
			putValue( Action.SHORT_DESCRIPTION, 
					"Copy this expression to the clipboard" 
			);			
		}
		
		public void actionPerformed(ActionEvent e) {
			int row = tbHistory.getSelectedRow();
			if ( row > -1 ) {
				StringSelection stringSelection = new StringSelection(
						( String )tbHistory.getModel().getValueAt( row, 0 )	
				);
			    Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
			    clipboard.setContents( stringSelection, null );				
			}
		}
	}

	public void mouseClicked(MouseEvent e) {
		if ( e.getButton() > 1 ) {

			JPopupMenu pm = new JPopupMenu();
			pm.add( new ExportHistoryAction() );
			pm.add( new ImportHistoryAction() );
			pm.addSeparator();
			pm.add( new CopyHistoryAction() );
			pm.addSeparator();
			pm.add( new CleanCurrentHistoryAction() );
			pm.add( new CleanAllHistoryAction() );
			pm.show( tbHistory, e.getX(), e.getY() );
			
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

	public void mouseMoved(MouseEvent e) {
		if ( e.getSource() == tbHistory ) {
			int row = tbHistory.rowAtPoint( e.getPoint() );
			if ( row > -1 ) {
				String xp = ( String )tbHistory.getValueAt( row, 0 );
				if ( xp == null ) {
					tbHistory.setToolTipText( "No expression" );
				} else
					tbHistory.setToolTipText( xp );
			}
		} else {
			int row = tbResult.rowAtPoint( e.getPoint() );
			if ( row > -1 ) {
				Object name = tbResult.getValueAt( row, 0 );				
				Object value = tbResult.getValueAt( row, 1 );
				StringBuffer sb = new StringBuffer( "<html><body>" );
				sb.append( "<b>Name</b>" );
				sb.append( "<br>" );
				sb.append( name );
				sb.append( "<br>" );
				sb.append( "<b>Value</b><div style='width:200px'>" );
				if ( value == null )
					value = "<i>No value</i>";
				sb.append( value );
				sb.append( "</div>" );							
				sb.append( "</body></html>" );
				tbResult.setToolTipText( sb.toString() );
			}
 		}
	}

	public void mouseDragged(MouseEvent e) {
	}

	// ===============================================================
	
	class NamespaceContextResolver implements NamespaceContext {

		Map<String,String> ns = null;
		
		void addNamespace( String prefix, String uri ) {
			if ( ns == null )
				ns = new HashMap<String,String>();
			ns.put( prefix.toLowerCase(),  uri );
		}
		
		@Override
		public String getNamespaceURI(String prefix) {
			if ( ns != null ) {
				return ns.get( prefix.toLowerCase() );
			}
			return null;
		}

		@Override
		public String getPrefix(String namespaceURI) {
			if ( ns != null ) {
				Set<Entry<String,String>> all = ns.entrySet();
				for ( Entry<String,String> e : all ) {
					if ( e.getValue().equalsIgnoreCase( namespaceURI ) )
						return e.getKey();
				}
			}
			return null;
		}

		@Override
		public Iterator getPrefixes( String namespaceURI ) {
			List<String> res = null;
			
			if ( ns != null ) {
				Set<Entry<String,String>> all = ns.entrySet();
				for ( Entry<String,String> e : all ) {
					if ( e.getValue().equalsIgnoreCase( namespaceURI ) ) {
						if ( res == null )
							res = new ArrayList<String>();
						res.add( e.getKey() );
					}
						
				}
			}

			if ( res == null )
				return null;
			return res.iterator();
		}
		
	}
	
	class MapVariableResolver implements XPathVariableResolver {
		
		Map<String,String> variables = null;
		
		void addVariable( String name, String value ) {
			if ( variables == null )
				variables = new HashMap<String,String>();
			variables.put( name,  value );
		}
		
		@Override
		public Object resolveVariable(QName variableName) {
			if ( variables == null )
				return null;
			return variables.get( variableName.getLocalPart() );
		}
	}
	
}

