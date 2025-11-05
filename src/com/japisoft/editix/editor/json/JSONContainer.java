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

package com.japisoft.editix.editor.json;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.EventObject;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.event.CellEditorListener;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.text.BadLocationException;
import javax.swing.text.EditorKit;
import javax.swing.text.PlainDocument;

import org.json.JSONKey;
import org.json.JSONObject;
import org.json.JSONString;

import com.japisoft.editix.editor.json.helper.KeyValueHandler;
import com.japisoft.editix.editor.json.kit.JSONDocument;
import com.japisoft.editix.editor.json.kit.JSONEditorKit;
import com.japisoft.editix.ui.EditixErrorPanel;
import com.japisoft.editix.ui.EditixFactory;
import com.japisoft.editix.ui.EditixStatusBar;
import com.japisoft.editix.ui.container.EditixXMLContainer;
import com.japisoft.editix.ui.container.locationbar.EditixNodeLocationBar;
import com.japisoft.framework.ApplicationModel;
import com.japisoft.framework.job.Job;
import com.japisoft.framework.job.JobManager;
import com.japisoft.framework.preferences.Preferences;
import com.japisoft.framework.xml.XMLToolkit;
import com.japisoft.framework.xml.parser.node.FPNode;
import com.japisoft.xmlpad.ComponentFactory;
import com.japisoft.xmlpad.IXMLPanel;
import com.japisoft.xmlpad.LocationEvent;
import com.japisoft.xmlpad.LocationListener;
import com.japisoft.xmlpad.PopupModel;
import com.japisoft.xmlpad.XMLContainer;
import com.japisoft.xmlpad.editor.EditorContext;
import com.japisoft.xmlpad.editor.XMLEditor;
import com.japisoft.xmlpad.elementview.ElementView;
import com.japisoft.xmlpad.elementview.ElementViewContext;
import com.japisoft.xmlpad.tree.parser.Parser;

public class JSONContainer extends XMLContainer implements LocationListener {

	static boolean STANDALONE = false;
	
	private Action parseAction = null;
	
	public JSONContainer( Action parseAction ) {
		super( new ComponentFactoryForJSONEditor() );
		this.parseAction = parseAction;
		getUIAccessibility().setToolBarAvailable( false );
		setStatusBarAvailable( false );
		getUIAccessibility().setPopupAvailable( false );
		getUIAccessibility().setTreePopupAvailable( false );
		getUIAccessibility().setTreeToolBarAvailable( false );
		setAutoNewDocument( false );
		setErrorPanelAvailable( true );
		getDocumentInfo().setRealTimeTree( true );		
		setEnabledTreeLocationForCaret( true );
		setTreeDragDrop( false );
		
		setDisposeAction( false );
		setTreeAvailable( true );
		setTreePopupAvailable( true );
		setTreeElementViewAvailable( true );

		setPopupAvailable( true );
		
		getDocument().putProperty(PlainDocument.tabSizeAttribute,
				new Integer(Preferences.getPreference("file", "tab-size", 2)));

		ArrayList assistant = new ArrayList();
		assistant.add( new KeyValueHandler() );
		getHelperManager().resetHandlers( assistant, false );

		getUIAccessibility().setErrorView(new EditixErrorPanel());
		setDisposeAction(false);		
	}
	
	public void setUIReady(boolean ok) {
		super.setUIReady( ok );
		if ( ok )
			addNotify();
		else
			removeNotify();
	}	
	
	public void addNotify() {
		setLocationListener( this );
	}
	
	public void removeNotify() {
		unsetLocationListener();
	}
	
	public void locationChanged(LocationEvent e) {
		EditixStatusBar.ACCESSOR.setXPathLocation(
			e.getXPathLocation()
		);	
	}
	
	public String getPreferenceGroupe() {
		return "JSONEditor";
	}
	
	public Action getAction(String actionId) {
		if ( "parse".equals( actionId ) ) {
			return parseAction;
		} else
			return super.getAction( actionId );
	}

	// Update the JSON content
	public void refresh() {
		
		FPNode n = getCurrentNode();
		if ( n == null )
			return;
		
		// Store the tree location
		String xpath = n.getXPathLocation();
		
		updateContent();
		
		// Restore the tree location
		
		JobManager.addJob( new RestoreLocation( xpath ) );
		
	}

	public JSONObject getCurrentObject() {
		FPNode node = getCurrentNode();
		if ( node == null )
			node = getRootNode();
		if ( node == null )
			return null;
		
		if  ( node.getApplicationObject() instanceof JSONKey ) {
			// Check if the value is an object
			JSONKey k = ( JSONKey )node.getApplicationObject();
			Object obj = k.getParent().get( k.getKey() );
			if ( obj instanceof JSONObject )
				return ( (JSONObject)obj );
		}
		
		while ( !( node.getApplicationObject() instanceof JSONObject ) ) {
			node = node.getFPParent();
			if ( node == null )
				return null;
		}
		return ( JSONObject )node.getApplicationObject();
	}
	
	class RestoreLocation implements Job {

		private String xpath;
		
		public RestoreLocation( String xpath ) {
			this.xpath = xpath;
		}
		
		@Override
		public void run() {
			String[] parts = xpath.split( "/" );
			FPNode node = getRootNode();
			for ( String p : parts ) {
				if ( p.endsWith( "[1]" ) )
					p = p.substring( 0, p.length()  - 3 );
				i:for ( int i = 0; i < node.getChildCount(); i++ ) {
					if ( node.childAt( i ).isTag() && p.equals( node.childAt( i ).getContent() ) ) {
						node = node.childAt( i );
						break i;
					}
				}
			}
			setCaretPosition( node.getStartingOffset() + 1 );
		}

		@Override
		public boolean isAlone() {
			return false;
		}

		@Override
		public void stopIt() {
		}

		@Override
		public Object getSource() {
			return null;
		}

		@Override
		public void dispose() {
		}

		@Override
		public boolean hasErrors() {
			return false;
		}
		
	}
	
	
	@Override
	public Parser createNewParser( boolean lightweight ) {
		return new JSONParser();
	}

	private NewMemberAction nma = null;
	private EditJSONAction jsa = null;
	private RemoveAction ra = null;
	
	protected PopupModel createTreePopupModel() {
		PopupModel model = new PopupModel( this );
		model.addAction( nma = new NewMemberAction() );
		model.addAction( jsa = new EditJSONAction() );
		model.addAction( ra = new RemoveAction() );
		return model;
	}
	
	public void fireAction( String action, ActionEvent evt ) {
		if ( "new".equals( action ) )
			nma.actionPerformed( evt );
		if ( "edit".equals( action ) )
			jsa.actionPerformed( evt );
		if ( "remove".equals( action ) )
			ra.actionPerformed( evt );
	}
	
	protected JPopupMenu createPopupMenu() {
		JPopupMenu jpm = new JPopupMenu();
		jpm.add( new NewObjectAction() );
		jpm.add( new NewArrayAction() );
		jpm.add( new NewStringPropertyAction() );
		jpm.add( new NewNumberPropertyAction() );
		jpm.add( new NewTruePropertyAction() );
		jpm.add( new NewFalsePropertyAction() );
		return jpm;
	}

	protected boolean useCustomPopupMenu() {
		return true;
	}
	
	private void resetContent( JSONObject root ) {
		setText( root.toString( 1 ) );
	}
	
	private void updateContent() {
		resetContent( getRoot() );		
	}

	private JSONObject getRoot() {
		FPNode root = ( FPNode )getRootNode();
		JSONObject all = ( JSONObject )root.getApplicationObject();
		return all;
	}
	
	
	//////////////////////////////////////////////////
	
	class EditJSONAction extends AbstractAction {
		public EditJSONAction() {
			putValue( Action.NAME, "Edit..." );
		}
		public void actionPerformed( ActionEvent evt ) {
			FPNode node = ( FPNode )getTree().getLastSelectedPathComponent();
			JSONKey key = null;
			
			if ( node.getType() == FPNode.TEXT_NODE ) {
				key = ( JSONKey )( ( FPNode )node.getParent() ).getApplicationObject();
			} else
			if ( node.getApplicationObject() instanceof JSONKey ) {
				key = ( JSONKey )node.getApplicationObject();
			}

			if ( key != null ) {
				JSONObject obj = key.getParent();
				Object value = obj.get( key.getKey() );
				String newValue = EditixFactory.buildAndShowInputDialog( "Edit a value", value.toString() );
				if ( newValue != null ) {
					obj.reput( key.getKey(), Toolkit.textToObject( newValue ) );				
					updateContent();
				}
			} else {
				EditixFactory.buildAndShowWarningDialog( "Not a value" );
			}
		}
	}
	
	class NewMemberAction extends AbstractAction {
		public NewMemberAction() {
			putValue( Action.NAME, "New member..." );
		}
		public void actionPerformed( ActionEvent evt ) {
			FPNode node = ( FPNode )getTree().getLastSelectedPathComponent();
			if ( node == null ) {
				EditixFactory.buildAndShowWarningDialog( "Select a node" );
			}
			if ( node.getType() == FPNode.TEXT_NODE ) {
				node = ( FPNode )node.getParent();
			}
			JSONObject obj = null;
			Object value = "";
			
			if ( node.getApplicationObject() instanceof JSONKey ) {
				JSONKey key = ( JSONKey )node.getApplicationObject();
				JSONObject parent = key.getParent();
				
				if ( parent.get( key.getKey() ) instanceof JSONObject ) {
					obj = parent.getJSONObject( key.getKey() );
				} else {
					// Get the old value
					value = parent.get( key.getKey() );
					// Force a new object
					parent.remove( key.getKey() );
					parent.put(key.getKey(), obj = new JSONObject() );
				}
			} else
				obj = ( JSONObject )node.getApplicationObject();
			
			String newMember = EditixFactory.buildAndShowInputDialog( "Edit member", "" );
			if ( newMember != null && !"".equals( newMember ) ) {
				obj.remove( newMember );
				obj.put( newMember, value );
			}
			
			updateContent();
			
		}
	}
	
	class RemoveAction extends AbstractAction {
		public RemoveAction() {
			putValue( Action.NAME, "Remove" );
		}		
		public void actionPerformed( ActionEvent evt ) {
			FPNode node = ( FPNode )getTree().getLastSelectedPathComponent();
			if ( node == null ) {
				EditixFactory.buildAndShowWarningDialog( "No selected node" );
			} else {
				if ( node.isRoot() ) {
					EditixFactory.buildAndShowWarningDialog( "Can't remove the root node" );
				} else {
					if ( node.getApplicationObject() instanceof JSONKey ) {
						JSONKey key = ( JSONKey )node.getApplicationObject();
						JSONObject parent = key.getParent();
						parent.remove( key.getKey() );
						updateContent();
					}
				}
			}
		}
	}
	
	//////////////////////////////////////////////////
	
	class NewObjectAction extends AbstractAction {
		public NewObjectAction() {
			putValue( Action.NAME, "New object" );
		}
		@Override
		public void actionPerformed(ActionEvent arg0) {
			try {
				boolean auto = ( ( JSONDocument )getDocument() ).isAutoIndent();
				( ( JSONDocument )getDocument() ).insertString( getCaretPosition(), "{", null );
				( ( JSONDocument )getDocument() ).setAutoIndent( auto );
			} catch( BadLocationException exc ) {	
			}
		}
	}

	class NewArrayAction extends AbstractAction {
		public NewArrayAction() {
			putValue( Action.NAME, "New array" );
		}
		@Override
		public void actionPerformed(ActionEvent arg0) {
			try {
				boolean auto = ( ( JSONDocument )getDocument() ).isAutoIndent();
				( ( JSONDocument )getDocument() ).insertString( getCaretPosition(), "[", null );
				( ( JSONDocument )getDocument() ).setAutoIndent( auto );
			} catch( BadLocationException exc ) {	
			}
		}
	}

	class NewStringPropertyAction extends AbstractAction {
		public NewStringPropertyAction() {
			putValue( Action.NAME, "New string property" );
		}
		@Override
		public void actionPerformed(ActionEvent arg0) {
			try {
				String pv = EditixFactory.buildAndShowInputDialog( "PROPERTY=VALUE ?" );
				if ( pv != null ) {
					String res = "";
					int i = pv.indexOf( "=" );
					if ( i > -1 ) {
						String[] parts = pv.split( "=" );
						res = "\"" + parts[ 0 ] + "\":" + "\"" + parts[ 1 ] + "\"";
					} else {
						res = "\"" + pv + "\":" + "\"" + "" + "\"";
					}
					( ( JSONDocument )getDocument() ).insertString( getCaretPosition(), res, null );
				}
			} catch( BadLocationException exc ) {	
			}
		}		
	}

	class NewNumberPropertyAction extends AbstractAction {
		public NewNumberPropertyAction() {
			putValue( Action.NAME, "New number property" );
		}
		@Override
		public void actionPerformed(ActionEvent arg0) {
			try {
				String pv = EditixFactory.buildAndShowInputDialog( "PROPERTY=VALUE ?" );
				if ( pv != null ) {
					String res = "";
					int i = pv.indexOf( "=" );
					if ( i > -1 ) {
						String[] parts = pv.split( "=" );
						
						parts[ 1 ] = parts[ 1 ].replace( ',', '.' );
						
						res = "\"" + parts[ 0 ] + "\":" + parts[ 1 ];
					} else {
						res = "\"" + pv + "\":" + "0";
					}
					( ( JSONDocument )getDocument() ).insertString( getCaretPosition(), res, null );
				}
			} catch( BadLocationException exc ) {	
			}
		}		
	}
	
	class NewTruePropertyAction extends AbstractAction {
		public NewTruePropertyAction() {
			putValue( Action.NAME, "New true property" );
		}
		@Override
		public void actionPerformed(ActionEvent arg0) {
			try {
				String pv = EditixFactory.buildAndShowInputDialog( "PROPERTY ?" );
				if ( pv != null ) {
					String res = "";
					res = "\"" + pv + "\":" + "true";
					( ( JSONDocument )getDocument() ).insertString( getCaretPosition(), res, null );
				}
			} catch( BadLocationException exc ) {	
			}
		}		
	}	

	class NewFalsePropertyAction extends AbstractAction {
		public NewFalsePropertyAction() {
			putValue( Action.NAME, "New false property" );
		}
		@Override
		public void actionPerformed(ActionEvent arg0) {
			try {
				String pv = EditixFactory.buildAndShowInputDialog( "PROPERTY ?" );
				if ( pv != null ) {
					String res = "";
					res = "\"" + pv + "\":" + "false";
					( ( JSONDocument )getDocument() ).insertString( getCaretPosition(), res, null );
				}
			} catch( BadLocationException exc ) {	
			}
		}		
	}	
	
	static class ComponentFactoryForJSONEditor extends ComponentFactory {
		public XMLEditor getNewXMLEditor( EditorContext context ) {
			return new CustomXMLEditorForJSON( context );
		}
		@Override
		public ElementView getNewElementView(XMLContainer container) {
			return new ElementViewEditor( container );
		}
		@Override
		public JToolBar getNewTreeToolBar() {
			return null;
		}
		
	}
	
	static class CustomXMLEditorForJSON extends XMLEditor {
		CustomXMLEditorForJSON( EditorContext context ) {
			super( context );
		}
		public EditorKit getEditorKit() {
			return new JSONEditorKit( "JSONEditor" );
		}
	}
	
	static class ElementViewEditor extends JTable implements ElementView, TableCellRenderer {

		private IXMLPanel container = null;
		
		public ElementViewEditor( IXMLPanel container ) {
			DefaultTableColumnModel m = new DefaultTableColumnModel();
			TableColumn c = new TableColumn( 0 );
			m.addColumn( c );
			c.setHeaderValue( "Name" );
			c = new TableColumn( 1 );
			c.setHeaderValue( "Value" );
			m.addColumn( c );
			setColumnModel( m );
			this.container = container;
		}
		
		@Override
		public void initErrorProcessing() {
		}

		@Override
		public void stopErrorProcessing() {
		}

		@Override
		public void notifyError(
			Object context, 
			boolean localError, 
			String sourceLocation, 
			int line, 
			int col,
			int offset, 
			String message, 
			boolean onTheFly ) {
		}
		
		@Override
		public void notifyNoError(boolean onTheFly) {
		}

		@Override
		public void init(ElementViewContext context) {
		}

		@Override
		public JComponent getView() {
			return this;
		}

		@Override
		public void updateView(FPNode node) {
			
			DefaultTableModel dtm = new DefaultTableModel( new String[] { "Name", "Value" }, 0 );
			
			if ( node != null )
			for ( int i = 0; i < node.childCount(); i++ ) {
				FPNode child = node.childAt( i );
				if ( child.getApplicationObject() instanceof JSONKey ) {
					JSONKey k = ( JSONKey )child.getApplicationObject();
					JSONObject obj = k.getParent();
					Object value = obj.get( k.getKey() );
					
					if ( value instanceof String || value instanceof Integer || value instanceof Boolean )
						dtm.addRow( new Object[] { k, k } );
				}
			}
			
			dtm.addRow( new Object[] { null, null } );
			
			setModel( dtm );
			getColumnModel().getColumn( 0 ).setCellRenderer( this );
			getColumnModel().getColumn( 1 ).setCellRenderer( this );
			
			CustomTableCellEditor editor = new CustomTableCellEditor( container );
			getColumnModel().getColumn( 0 ).setCellEditor( editor );
			getColumnModel().getColumn( 1 ).setCellEditor( editor );
		}
		
		@Override
		public boolean autoScroll() {
			return false;
		}
		
		@Override
		public void dispose() {
			( ( CustomTableCellEditor )getColumnModel().getColumn( 0 ).getCellEditor() ).dispose();
			container = null;
		}
		
		@Override
		public void setEditable(boolean editable) {
		}

		private JLabel rowView = null;
		
		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
				int row, int column) {
			if ( rowView == null )
				rowView = new JLabel();
			if ( value instanceof JSONKey ) {
				JSONKey k = ( JSONKey )value;
				if ( column == 0 ) {
					rowView.setText( k.getKey() );
				} else
					rowView.setText( k.getValue().getKey() );
			} else
				rowView.setText( null );
			return rowView;
		}
		
	}
	
	static class CustomTableCellEditor extends JTextField implements TableCellEditor, ActionListener {

		private IXMLPanel container;
		
		public CustomTableCellEditor( IXMLPanel container ) {
			this.container = container;
		}
		
		@Override
		public void addNotify() {
			super.addNotify();
			addActionListener( this );
		}
		
		@Override
		public void removeNotify() {
			super.removeNotify();
			removeActionListener( this );
		}
		
		@Override
		public Object getCellEditorValue() {
			return lastValue;
		}

		@Override
		public boolean isCellEditable(EventObject anEvent) {
			return true;
		}

		@Override
		public boolean shouldSelectCell(EventObject anEvent) {
			return false;
		}

		@Override
		public boolean stopCellEditing() {
			return false;
		}

		@Override
		public void cancelCellEditing() {
		}

		private CellEditorListener l = null;
		
		@Override
		public void addCellEditorListener(CellEditorListener l) {
			this.l = l;
		}

		@Override
		public void removeCellEditorListener(CellEditorListener l) {
			if ( this.l == l )
				this.l = null;
		}
		
		private Object lastValue;
		private int lastCol;
		
		@Override
		public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row,
				int column) {
			
			this.lastValue = value;
			this.lastCol = column;
			
			if ( value instanceof JSONKey ) {
				if ( column == 0 )
					setText( ( ( JSONKey )value ).getKey() );
				else
					setText( ( ( JSONKey )value ).getValue().getKey() );
			} else
				setText( null );
			return this;
		}
		

	
		@Override
		public void actionPerformed(ActionEvent e) {
			if ( this.l != null ) {
				if ( lastValue != null ) {
					if ( lastValue instanceof JSONKey ) {
						JSONKey k = ( JSONKey )lastValue;
						JSONObject parent = k.getParent();
						
						if ( lastCol == 0 ) {
							if ( !"".equals( getText() ) ) {
								// Key name updated
								parent.renameKey( k, getText() );
							} else {
								parent.remove( k.getKey() );	// Remove the key
							}
						} else {
							Object value = Toolkit.textToObject( getText() );							
							parent.reput( k.getKey(), value );
						}
						
						// Update the source
						
						refresh();
					}
				} else {
					// Add a new Key
					
					if ( lastCol == 0 ) {
						JSONObject obj = ( ( JSONContainer )container ).getCurrentObject();
						if ( obj != null ) {
							obj.put( getText(), "" );
							refresh();
						}
						
					}
					
				}
				if ( l != null )
					l.editingStopped( null );
			}
		}
		
		private void refresh() {
			( ( JSONContainer )this.container ).refresh();
		}
		
		public void dispose() {
			this.container = null;
			this.l = null;
		}
		
	}
	
	public static void main( String[] args ) {
		ApplicationModel.SHORT_APPNAME = "test";
		JFrame f = new JFrame();
		f.add( new JSONContainer( null ).getView() );
		f.setSize( 300, 300 );
		f.setVisible( true );
	}

}

