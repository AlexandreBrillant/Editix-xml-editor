package com.japisoft.editix.wizard.list;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.util.EventObject;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeCellEditor;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreePath;

import com.japisoft.editix.ui.EditixFactory;
import com.japisoft.editix.wizard.Wizard;
import com.japisoft.editix.wizard.WizardContext;
import com.japisoft.framework.dialog.AutoClosableDialog;
import com.japisoft.framework.dialog.AutoClosableListener;
import com.japisoft.framework.xml.parser.node.FPNode;

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
public class ListWizard extends JPanel implements Wizard, ListWizardModel, TreeCellRenderer, AutoClosableDialog {

	private JTree tree;
	private TreeEditor editor = null;
	
	public ListWizard() {
		setLayout( new BorderLayout() );
		JToolBar tb = new JToolBar();
		tb.setFloatable( false );
		tree = new JTree( 
					new DefaultTreeModel( 
							new FPNode( FPNode.TAG_NODE, "ul" ) ) );
		
		( ( FPNode )tree.getModel().getRoot() ).setApplicationObject( "root" );
		
		add( tb, BorderLayout.NORTH );
		add( new JScrollPane( tree ), BorderLayout.CENTER );
		
		tree.setEditable( true );
		tree.setCellEditor( editor = new TreeEditor() );
		tree.setCellRenderer( this );
		
		tb.add( new AddNode() );
		tb.add( new DeleteNode() );
		
		setSize( 200, 300 );
	}

	@Override
	public void addNotify() {
		super.addNotify();
		tree.setSelectionPath( new TreePath( tree.getModel().getRoot() ) );
	}
	
	public void setDialogListener(AutoClosableListener acd) {
	}
	
	public FPNode getResult() {
		if ( editor.editing )
			editor.stopCellEditing();
		return context.getResult( this );
	}

	private WizardContext context;

	public void setContext(WizardContext context) {
		this.context = context;
	}

	// Model
	public FPNode getList() {
		return ( FPNode )tree.getModel().getRoot();
	}

	private FPNode getSelection() {
		TreePath tp = tree.getSelectionPath();
		if ( tp == null ) {
			return ( FPNode )tree.getModel().getRoot();
		}
		return ( FPNode )tp.getLastPathComponent();
	}
	
	class AddNode extends AbstractAction {
		public AddNode() {
			putValue( Action.NAME, "New item" );
		}
		
		public void actionPerformed(ActionEvent e) {
			editor.fireStopEditing();
			FPNode n = getSelection();
			FPNode child = null;
			child = new FPNode( FPNode.TAG_NODE, "li" );
			child.setApplicationObject( "item" );
			n.appendChild( child );
			( ( DefaultTreeModel )tree.getModel() ).reload( n );
		}
	}

	class DeleteNode extends AbstractAction {
		public DeleteNode() {
			putValue( Action.NAME, "Remove item" );			
		}

		public void actionPerformed(ActionEvent e) {
			editor.fireStopEditing();
			FPNode n = getSelection();
			FPNode p = n.getFPParent();
			if ( p == null ) {
				EditixFactory.buildAndShowErrorDialog( "You can't delete this node" );
			} else {
				p.removeChildNode( n );
			}
			( ( DefaultTreeModel )tree.getModel() ).reload( p );
		}		
	}

	// -----------------------------------------------------------------------------------------------

	private JLabel lbl = null;
	
	public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
		if ( lbl == null ) {
			lbl = new JLabel();
			lbl.setOpaque(true);
			lbl.setIcon( new ImageIcon( getClass().getResource( "item.png" ) ) );
		}
		FPNode n = ( FPNode )value;
		if ( n.getApplicationObject() != null )
			lbl.setText( ( String )n.getApplicationObject() );
		else
			lbl.setText( "ul" );
		
		if ( selected ) {
			lbl.setForeground( UIManager.getColor( "Tree.selectionForeground" ) );
			lbl.setBackground( UIManager.getColor( "Tree.selectionBackground") ); 
		} else {
			lbl.setForeground( tree.getForeground() );
			lbl.setBackground( tree.getBackground() );
		}
		
		return lbl;
	};
	
	class TreeEditor implements TreeCellEditor, ActionListener {
	
		private JTextField editor = null;
		
		private CellEditorListener l;
		
		public void addCellEditorListener(CellEditorListener l) {
			this.l = l;
		}
		
		public void actionPerformed(ActionEvent e) {
			l.editingStopped( new ChangeEvent( editor ) );
		}		

		private void fireStopEditing() {
			if ( l != null )
				l.editingStopped( null );
		}
	
		public void cancelCellEditing() {	
		}
		public Object getCellEditorValue() {
			String text = editor.getText();
			getSelection().setApplicationObject( text );
			return text;
		}
		
		private boolean editing = false;
		
		public Component getTreeCellEditorComponent(JTree tree, Object value,
			boolean isSelected, boolean expanded, boolean leaf, int row) {
			if ( editor == null ) {
				editor = new JTextField();
				editor.addActionListener( this );
			}
			FPNode n = ( FPNode )value;
			editor.setText( ( String )n.getApplicationObject() );
			editor.setPreferredSize( new Dimension( 150, 15 ) );
			editing = true;
			return editor;
		}
		public boolean isCellEditable(EventObject anEvent) {
			if ( anEvent instanceof MouseEvent ) {
				MouseEvent me = ( MouseEvent )anEvent;
				if ( me.getClickCount() == 1 )
					return false;
			}
			return getSelection().getParent() != null && !getSelection().matchContent( "ul" );
		}
		public void removeCellEditorListener(CellEditorListener l) {
			this.l = null;
		}
		public boolean shouldSelectCell(EventObject anEvent) {
			return false;
		}
		public boolean stopCellEditing() {
			
			editing = false;
			return true;
		}
		
	}
	
}
