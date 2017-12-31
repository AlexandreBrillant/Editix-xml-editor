package com.japisoft.editix.action.search;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.ByteArrayInputStream;
import java.io.StringReader;

import java.util.List;

import javax.swing.DefaultCellEditor;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;

import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.JTextComponent;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.japisoft.editix.ui.EditixDialog;
import com.japisoft.editix.ui.EditixFrame;
import com.japisoft.editix.ui.EditixFactory;
import com.japisoft.framework.dialog.actions.DialogActionModel;
import com.japisoft.framework.dialog.actions.StoringLocationAction;
import com.japisoft.framework.preferences.Preferences;
import com.japisoft.framework.ui.table.ExportableTable;
import com.japisoft.framework.xml.parser.FPParser;
import com.japisoft.framework.xml.parser.dom.DocumentImpl;
import com.japisoft.framework.xml.parser.dom.DomNodeFactory;
import com.japisoft.framework.xml.parser.node.FPNode;
import com.japisoft.xmlpad.XMLContainer;

import org.jaxen.dom.DOMXPath;
import org.jaxen.*;

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
public class XPathDialog
	extends EditixDialog
	implements ActionListener, ListSelectionListener {

	private boolean searchMode;

	public XPathDialog( 
		java.lang.String title, 
		boolean searchMode, 
		DefaultTableModel modelV, 
		DefaultTableModel modelN ) {
		super(
			"XPath",
			searchMode ? "XPath finder" : "XPath builder",
			title, 
			DialogActionModel.getDefaultDialogCloseActionModel().addDialogAction(
					new StoringLocationAction()		
			)
		);
		initUI();
		initModel( modelV, modelN );
		this.searchMode = searchMode;
	}
	
	JPanel contentPane;
	JComboBox cbXPath = new JComboBox();
	JLabel lblXpath = new JLabel();
	JLabel lblVars = new JLabel();
	JScrollPane spVariables = new JScrollPane();
	JTable tbVariables = new ExportableTable();
	JLabel lblNamespaces = new JLabel();
	JScrollPane spNamespaces = new JScrollPane();
	JTable tbNamespaces = new ExportableTable();
	JButton btnApplyFromRoot = new JButton();
	JButton cBtnApplyFromCurrentNode = new JButton();
	JButton btnCopy = new JButton();
	JLabel lblResult = new JLabel();
	JScrollPane spResult = new JScrollPane();
	JTable tbResult = new ExportableTable();
	GridBagLayout gridBagLayout1 = new GridBagLayout();

	public void beforeShowing() {
		super.beforeShowing();
		btnApplyFromRoot.addActionListener(this);
		cBtnApplyFromCurrentNode.addActionListener(this);
		btnCopy.addActionListener(this);
		tbResult.getSelectionModel().addListSelectionListener(this);
	}

	public void beforeClosing() {
		super.beforeClosing();
		btnApplyFromRoot.removeActionListener(this);
		cBtnApplyFromCurrentNode.removeActionListener(this);
		btnCopy.removeActionListener(this);
		tbResult.getSelectionModel().removeListSelectionListener(this);

		((CustomCellEditor2)tbVariables.getColumnModel().getColumn( 0 ).getCellEditor()).dispose();
		((CustomCellEditor2)tbVariables.getColumnModel().getColumn( 2 ).getCellEditor()).dispose();

		((CustomCellEditor2)tbNamespaces.getColumnModel().getColumn( 0 ).getCellEditor()).dispose();
		((CustomCellEditor2)tbNamespaces.getColumnModel().getColumn( 1 ).getCellEditor()).dispose();
	}

	//Component initialization
	private void initUI() {
		contentPane = (JPanel) this.getContentPane();
		contentPane.setLayout(gridBagLayout1);
		this.setSize(new Dimension(430, 500));
		cbXPath.setEditable(true);
		cbXPath.setModel( new DefaultComboBoxModel() );

		lblXpath.setText("XPath expression");
		lblVars.setText("Variables");
		lblNamespaces.setText("Namespaces");
		contentPane.setPreferredSize(new Dimension(379, 350));
		btnApplyFromRoot.setToolTipText(
			"Apply this XPath expression from the root node");
		btnApplyFromRoot.setText("Apply from root");
		cBtnApplyFromCurrentNode.setToolTipText(
			"Apply from the current document node");
		cBtnApplyFromCurrentNode.setText("Apply from current");
		btnCopy.setToolTipText("Copy the current XPath expression");
		btnCopy.setText("Copy");
		lblResult.setText("Result");
		tbResult.setToolTipText(
			"Click on a line for selecting the same one in the editor");
		contentPane.add(
			lblXpath,
			new GridBagConstraints(
				0,
				0,
				1,
				1,
				0.0,
				0.0,
				GridBagConstraints.WEST,
				GridBagConstraints.NONE,
				new Insets(10, 17, 0, 31),
				0,
				0));
		contentPane.add(
			cbXPath,
			new GridBagConstraints(
				0,
				1,
				3,
				1,
				1.0,
				0.0,
				GridBagConstraints.CENTER,
				GridBagConstraints.HORIZONTAL,
				new Insets(0, 17, 0, 17),
				0,
				0));
		contentPane.add(
			spVariables,
			new GridBagConstraints(
				0,
				3,
				3,
				1,
				1.0,
				1.0,
				GridBagConstraints.CENTER,
				GridBagConstraints.BOTH,
				new Insets(0, 17, 0, 17),
				0,
				0));
		contentPane.add(
			lblVars,
			new GridBagConstraints(
				0,
				2,
				1,
				1,
				0.0,
				0.0,
				GridBagConstraints.WEST,
				GridBagConstraints.NONE,
				new Insets(6, 17, 0, 81),
				0,
				0));
		spVariables.getViewport().add(tbVariables, null);
		contentPane.add(
			lblNamespaces,
			new GridBagConstraints(
				0,
				4,
				1,
				1,
				0.0,
				0.0,
				GridBagConstraints.WEST,
				GridBagConstraints.NONE,
				new Insets(0, 17, 0, 61),
				0,
				0));
		contentPane.add(
			spNamespaces,
			new GridBagConstraints(
				0,
				5,
				3,
				1,
				1.0,
				1.0,
				GridBagConstraints.CENTER,
				GridBagConstraints.BOTH,
				new Insets(0, 17, 0, 17),
				0,
				0));
		contentPane.add(
			btnApplyFromRoot,
			new GridBagConstraints(
				0,
				6,
				1,
				1,
				0.0,
				0.0,
				GridBagConstraints.CENTER,
				GridBagConstraints.NONE,
				new Insets(6, 17, 0, 0),
				0,
				0));
		spNamespaces.getViewport().add(tbNamespaces, null);
		contentPane.add(
			lblResult,
			new GridBagConstraints(
				0,
				7,
				1,
				1,
				0.0,
				0.0,
				GridBagConstraints.WEST,
				GridBagConstraints.NONE,
				new Insets(0, 17, 0, 99),
				0,
				0));
		contentPane.add(
			spResult,
			new GridBagConstraints(
				0,
				8,
				3,
				2,
				1.0,
				1.0,
				GridBagConstraints.CENTER,
				GridBagConstraints.BOTH,
				new Insets(0, 17, 17, 17),
				0,
				0));
		spResult.getViewport().add(tbResult, null);
		contentPane.add(
			btnCopy,
			new GridBagConstraints(
				2,
				6,
				1,
				1,
				0.0,
				0.0,
				GridBagConstraints.WEST,
				GridBagConstraints.NONE,
				new Insets(6, 7, 0, 17),
				25,
				-5));
		contentPane.add(
			cBtnApplyFromCurrentNode,
			new GridBagConstraints(
				1,
				6,
				1,
				1,
				0.0,
				0.0,
				GridBagConstraints.WEST,
				GridBagConstraints.NONE,
				new Insets(6, 6, 0, 0),
				0,
				-5));
	}

	private void initModel( DefaultTableModel modelV, DefaultTableModel modelN ) {
		initVariables( modelV );
		initNamespaces( modelN );
		initResults();
	}

	// VARIABLES

	private DefaultTableModel modelVariables;

	private void initVariables( DefaultTableModel variables ) {
		if ( variables == null )
			modelVariables =
				new DefaultTableModel(
					new String[] { "Name", "Type", "Value" },
					Preferences.getPreference("xpath", "maxVariables", 20));
		else
			modelVariables = variables;

		tbVariables.setModel( modelVariables );
		tbVariables.getColumnModel().getColumn(1).setCellEditor(
			new CustomCellEditor() );
		tbVariables.getColumnModel().getColumn(1).setCellRenderer(
			new CustomCellRenderer() );
		tbVariables.getColumnModel().getColumn(2).setCellEditor(
			new CustomCellEditor2() );
		tbVariables.getColumnModel().getColumn(0).setCellEditor(
			new CustomCellEditor2() );

		tbVariables.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		tbVariables.setSelectionBackground(Color.WHITE);
		tbVariables.setSelectionForeground(Color.BLACK);
	}

	// Editor for cell
	class CustomCellEditor2 extends DefaultCellEditor implements FocusListener {
		private JTextField tf = null;

		public CustomCellEditor2() {
			super(
				new JTextField() );
			tf = ( JTextField ) getComponent();
			tf.addFocusListener( this );
		}

		private int lastRow = 0;
		private int lastCol = 0;

		public void dispose() {
			tf.removeFocusListener( this );
		}

		public Component getTableCellEditorComponent(
			JTable table,
			Object value,
			boolean isSelected,
			int row,
			int column) {
			lastRow = row;
			lastCol = column;
			tf.setText( value == null ? "" : value.toString() );
			return tf;
		}

		public boolean stopCellEditing() {
			modelVariables.setValueAt(tf.getText(), lastRow, lastCol );
			return super.stopCellEditing();
		}

		public void focusGained(FocusEvent e) {
		}

		public void focusLost(FocusEvent e ) {
			stopCellEditing();			
		}

	}

	// Renderer for type

	class CustomCellRenderer extends DefaultTableCellRenderer {
		private JComboBox cb =
			new JComboBox(new String[] { "String", "Decimal", "Boolean" });

		public CustomCellRenderer() {
			super();
			cb.setSelectedIndex(0);
		}

		public Component getTableCellRendererComponent(
			JTable table,
			Object value,
			boolean b,
			boolean isSelected,
			int row,
			int column) {
			cb.setSelectedItem(value);
			return cb;
		}
	}

	class CustomCellEditor extends DefaultCellEditor {
		private JComboBox cb =
			null;

		public CustomCellEditor() {
			super(
				new JComboBox(new String[] { "String", "Decimal", "Boolean" }));
			cb = (JComboBox) getComponent();
			cb.setSelectedIndex(0);
		}

		private int lastRow = 0;

		public Component getTableCellEditorComponent(
			JTable table,
			Object value,
			boolean isSelected,
			int row,
			int column) {
			lastRow = row;
			cb.setSelectedItem(value);
			return cb;
		}

		public boolean stopCellEditing() {
			modelVariables.setValueAt(cb.getSelectedItem(), lastRow, 1);		
			return super.stopCellEditing();
		}

		public void cancelCellEditing() {
			stopCellEditing();
		}
	}

	// NAMESPACES

	private DefaultTableModel modelNamespaces = null;

	private void initNamespaces( DefaultTableModel model ) {
		if ( model == null )
			modelNamespaces =
				new DefaultTableModel(
					new String[] { "Alias", "Namespace" },
					Preferences.getPreference("xpath", "maxNamespaces", 10));
		else
			modelNamespaces = model;
		tbNamespaces.setModel(modelNamespaces);
		tbNamespaces.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		tbNamespaces.setSelectionBackground(Color.WHITE);
		tbNamespaces.setSelectionForeground(Color.BLACK);

		tbNamespaces.getColumnModel().getColumn( 0 ).setCellEditor( new CustomCellEditor2() );
		tbNamespaces.getColumnModel().getColumn( 1 ).setCellEditor( new CustomCellEditor2() );
	}

	// RESULTS

	private void initResults() {
		tbResult.setSelectionMode( ListSelectionModel.SINGLE_SELECTION );
	}

	// ACTIONS

	public void actionPerformed(ActionEvent e) {
		if ( e.getSource() == btnApplyFromRoot ) {
			applyFromRoot();
		} else if ( e.getSource() == cBtnApplyFromCurrentNode ) {
			applyFromCurrent();
		} else if ( e.getSource() == btnCopy ) {
			copy();
		}	
	}

	private Node getNodeFromRoot() throws Throwable {
		if (cbXPath.getSelectedItem() == null) {
			throw new RuntimeException("No expression found");
		}
		XMLContainer container = EditixFrame.THIS.getSelectedSubContainer( "XML" );
		if ( container == null )
			container = EditixFrame.THIS.getSelectedContainer();
		String document = container.getText();
		FPParser p = new FPParser();
		p.setNodeFactory(new DomNodeFactory());
		return ( Node )p.parse(new StringReader( document )).getRoot();
	}
	
	private void applyFromNode(Node n) {
		tbResult.setModel(new DefaultTableModel(new Object[] { "Result" }, 0));
		unshowSyntaxError();

		try {
			XPath xpath = buildXPath();
			cbXPath.setForeground(Color.BLACK);
			if (n == null)
				throw new RuntimeException("");
			try {
				try {
					Object o = xpath.evaluate(n);
					if (o instanceof List) {
						showResult((List) o);
					} else
						if ( !searchMode )
							EditixFactory.buildAndShowInformationDialog( "Result = " + o );
				} catch (NullPointerException exc) {
				}
			} catch (XPathSyntaxException se) {
				showSyntaxError(se.getPosition(), se.getMessage());
			}
		} catch (Throwable th) {
			if ( "true".equals( System.getProperty( "debug.editix" ) ) )
				th.printStackTrace();
			EditixFactory.buildAndShowErrorDialog(
				"Can't apply expression : " + th.getMessage());
		}
	}

	private void applyFromRoot() {
		try {
			Node n = getNodeFromRoot();
			DocumentImpl doc = new DocumentImpl((Element) n);
			applyFromNode(doc);
		} catch (Throwable th) {
			if ( "true".equals( System.getProperty( "debug.editix" ) ) )
				th.printStackTrace();
			EditixFactory.buildAndShowErrorDialog(
				"Can't apply expression : " + th.getMessage());
		}
	}

	private void applyFromCurrent() {
		try {
			XMLContainer container =
				EditixFrame.THIS.getSelectedSubContainer( "XML" );
			FPNode n = container.getCurrentNode();
			if (n == null) {
				applyFromRoot();
			} else {
				String xp = n.getXPathLocation();
				FPNode n2 = (FPNode) getNodeFromRoot();
				Node node = (Node) n2.getNodeForXPathLocation(xp, false);
				applyFromNode(node);
			}
		} catch (Throwable th) {
			EditixFactory.buildAndShowErrorDialog(
				"Can't apply expression : " + th.getMessage());
		}
	}

	private void copy() {
		if (cbXPath.getSelectedItem() == null)
			return;
		Toolkit.getDefaultToolkit().getSystemClipboard().setContents(
			new StringSelection("" + cbXPath.getSelectedItem()),
			null);
	}

	private void unshowSyntaxError() {
		cbXPath.setForeground(Color.BLACK);
		(
			(JTextComponent) cbXPath
				.getEditor()
				.getEditorComponent())
				.setCaretColor(
			Color.BLACK);
	}

	private void showSyntaxError(int position, String message) {
		cbXPath.setForeground(Color.RED);
		(
			(JTextComponent) cbXPath
				.getEditor()
				.getEditorComponent())
				.setCaretPosition(
			position);
		(
			(JTextComponent) cbXPath
				.getEditor()
				.getEditorComponent())
				.setCaretColor(
			Color.red);

		EditixFactory.buildAndShowErrorDialog(message);
		cbXPath.requestFocus();
	}

	private void showResult(List l) {
		String title = null;
		if ( l.size() == 0 )
			title = "No Result";
		else
			title = l.size() + " result(s)";
		
		DefaultTableModel model =
			new DefaultTableModel(new String[] { title }, 0);
		for (int i = 0; i < l.size(); i++) {			
			model.addRow(new Object[] { l.get(i)});
		}
		tbResult.setModel( model );
		boolean added = true;
		String xpath = ( String )cbXPath.getSelectedItem();
		for ( int i = 1; i < cbXPath.getItemCount(); i++ ) {
			if ( cbXPath.getItemAt( i ).equals( xpath ) ) {
				added = false;
				break;
			}
		}
		if ( added )
			cbXPath.addItem( xpath );
	}

	public String[] getItems() {
		String[] items = new String[ cbXPath.getItemCount() ];
		for ( int i = 0; i < cbXPath.getItemCount(); i++ )
			items[ i ] = ( String )cbXPath.getItemAt( i );
		return items;
	}

	public void setItems( String[ ] items ) {
		cbXPath.setModel(
			new DefaultComboBoxModel(
				items ) );
	}

	public DefaultTableModel getVariablesModel() { return modelVariables; }
	public DefaultTableModel getNamespacesModel() { return modelNamespaces; }
	public void setVariablesModel( DefaultTableModel model ) { tbVariables.setModel( model ); }
	public void setNamespacesModel( DefaultTableModel model ) { tbNamespaces.setModel( model ); } 

	private XPath buildXPath() throws Throwable {
		XPath path = new DOMXPath((String) cbXPath.getSelectedItem());
		SimpleVariableContext context = null;

		// Check for variables
		for (int i = 0; i < modelVariables.getRowCount(); i++) {
			String name = (String) modelVariables.getValueAt(i, 0);
			if (name != null && !"".equals(name) && name.length() > 0) {
				if (context == null) {
					context = new SimpleVariableContext();
					path.setVariableContext(context);
				}
				String type = (String) modelVariables.getValueAt(i, 1);
				String value = (String) modelVariables.getValueAt(i, 2);

				if (value == null)
					continue;

				if ( "String".equals( type ) )
					context.setVariableValue(name, value);
				else if ( "Decimal".equals( type ) ) {
					context.setVariableValue(name, new Double("" + value));
				} else if ( "Boolean".equals( type ) ) {
					String _ = ("" + value).toLowerCase();
					context.setVariableValue(
						name,
						new Boolean("true".equals(_)));
				}
			}
		}

		SimpleNamespaceContext context2 = null;

		// Check for namespaces
		for (int i = 0; i < modelNamespaces.getRowCount(); i++) {
			String alias = (String) modelNamespaces.getValueAt(i, 0);
			if (alias != null && !"".equals(alias) && alias.length() > 0) {
				String uri = (String) modelNamespaces.getValueAt(i, 1);
				if (uri == null)
					continue;
				if (context2 == null) {
					context2 = new SimpleNamespaceContext();
					path.setNamespaceContext(context2);
				}
				context2.addNamespace(alias, uri);
			}
		}
		return path;
	}

	public void valueChanged(ListSelectionEvent e) {
		int row = tbResult.getSelectedRow();
		if (row > -1) {
			FPNode n = (FPNode) tbResult.getModel().getValueAt(row, 0);
			XMLContainer container =
				EditixFrame.THIS.getSelectedSubContainer( "XML" );
			if (n.getStartingLine() > 0)
				container.getEditor().highlightLine(n.getStartingLine());
		}
	}

}
