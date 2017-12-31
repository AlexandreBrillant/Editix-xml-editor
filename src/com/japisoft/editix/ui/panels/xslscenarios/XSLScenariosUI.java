package com.japisoft.editix.ui.panels.xslscenarios;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Properties;

import javax.swing.JComponent;

import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.TransferHandler;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import javax.xml.transform.ErrorListener;
import javax.xml.transform.TransformerException;

import com.japisoft.editix.action.fop.FOPAction;
import com.japisoft.editix.action.xquery.XQuery2Action;
import com.japisoft.editix.action.xquery.XQueryAction;
import com.japisoft.editix.action.xsl.XSLTAction;
import com.japisoft.editix.action.xsl.XSLTConfigPanel;
import com.japisoft.editix.ui.EditixFactory;
import com.japisoft.editix.ui.EditixFrame;

import com.japisoft.framework.application.descriptor.ActionModel;
import com.japisoft.framework.xml.parser.node.FPNode;
import com.japisoft.xmlpad.IXMLPanel;
import com.japisoft.xmlpad.XMLContainer;
import com.japisoft.xmlpad.XMLDocumentInfo;

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
public class XSLScenariosUI extends javax.swing.JPanel implements 
		MouseListener,
		MouseMotionListener, 
		ActionListener, 
		TreeSelectionListener {

	static final String TEXT_TEMPLATE = "=T=";

	static final String RUN_CMD = "run";
	static final String REN_CMD = "ren";
	static final String DEL_CMD = "del";
	static final String ADDD_CMD = "addd";
	static final String ADDS_CMD = "add";

	static final String[] TOOLBAR_ACTIONS = { 
		RUN_CMD,
		ADDS_CMD, 
		ADDD_CMD,
		REN_CMD, 
		DEL_CMD 
	};

	static final String[] POPUP_ACTIONS = {
		RUN_CMD,
		ADDS_CMD, 
		ADDD_CMD,
		REN_CMD, 
		DEL_CMD 
	};

	private JTree t;
	private JToolBar tb;
	private NodeTransfertHandler th;

	public XSLScenariosUI() {
		setLayout(new BorderLayout());

		tb = new JToolBar();

		for (int i = 0; i < TOOLBAR_ACTIONS.length; i++) {
			tb.add(EditixFrame.THIS.getBuilder().getActionById(
					"xa." + TOOLBAR_ACTIONS[i]));
		}

		add(tb, BorderLayout.NORTH);

		add(new JScrollPane(t = new JTree()), BorderLayout.CENTER);

		clean();
		t.setCellRenderer(new XSLScenariosTreeRenderer());
		t.setTransferHandler(th = new NodeTransfertHandler());
		setEnabledAction(DEL_CMD, false);
		setEnabledAction(RUN_CMD, false);
		
	}

	private JPopupMenu buildPopupMenu() {
		JPopupMenu menu = new JPopupMenu();
		for (int i = 0; i < POPUP_ACTIONS.length; i++) {
			if (POPUP_ACTIONS[i] == null)
				menu.addSeparator();
			else
				menu.add(EditixFrame.THIS.getBuilder().getActionById(
						"xa." + POPUP_ACTIONS[i]));
		}
		return menu;
	}

	void init() {
		t.setSelectionPath(new TreePath(t.getModel().getRoot()));
		valueChanged(null);
	}

	public void addNotify() {
		super.addNotify();
		t.addMouseListener(this);
		t.addMouseMotionListener(this);
		t.addTreeSelectionListener(this);
	}

	public void removeNotify() {
		super.removeNotify();
		t.removeMouseListener(this);
		t.removeMouseMotionListener(this);
		t.removeTreeSelectionListener(this);
	}

	void setEnabledAction(String name, boolean enabled) {
		ActionModel.setEnabled("xa." + name, enabled);
	}

	public void valueChanged(TreeSelectionEvent e) {
		TreePath tp = t.getSelectionPath();
		if (tp != null) {
			FPNode node = (FPNode) tp.getLastPathComponent();
			setEnabledAction( RUN_CMD, node.matchContent( "item" ) );
			setEnabledAction( DEL_CMD, !node.isRoot() || node.isAttribute());
			setEnabledAction( ADDS_CMD,node.matchContent("group"));
			setEnabledAction( ADDD_CMD, node.isRoot()
					|| node.matchContent("group"));
			setEnabledAction( REN_CMD,
					node.isRoot()
							|| (node.matchContent("group")
									|| node.matchContent("item") || node
									.isAttribute()));
		}
	}

	public void actionPerformed( ActionEvent e ) {
		String cmd = e.getActionCommand();
		if ( t.getSelectionPath() == null ) {
			return;
		}
		if ( ADDS_CMD.equals( cmd ) ) {
			addScenario();
		} else if ( ADDD_CMD.equals( cmd ) ) {
			addGroup();
		} else if ( DEL_CMD.equals( cmd ) ) {
			delete();
		} else if ( REN_CMD.equals( cmd ) ) {
			rename( (FPNode) t.getSelectionPath().getLastPathComponent() );
		} else if ( RUN_CMD.equals( cmd ) ) {
			runScenario();
		}
		refresh();
	}

	private void runScenario() {
		FPNode scenarioNode = ( FPNode )t.getSelectionPath().getLastPathComponent();
		String param = scenarioNode.getAttribute( "param" );
		// Rebuild all the parameters
		Properties p = new Properties();
		String[] content = param.split( "¤" );
		for ( String property : content ) {
			int i = property.indexOf( "=" );
			String key = property.substring( 0, i );
			String value = property.substring( i + 1 );
			p.setProperty( key, value );
		}

		String type = p.getProperty( "type" );		
		boolean ok = true;

		IXMLPanel panel = new XSLPropertiesXMLPanel( p );

		if ( panel.getProperty( "xquery.xslt.file" ) != null ) {
			ok = XQueryAction.xquery( 
					type, 
					panel, 
					false, 
					false, 
					new ErrorListener()  {
					public void warning(TransformerException exception)
					throws TransformerException {
					}
					public void fatalError(TransformerException exception)
							throws TransformerException {
					}			
					public void error(TransformerException exception)
							throws TransformerException {
					}
			}
			);
		} else
			
		if ( panel.getProperty( "fo.source" ) != null ) {

			ok = FOPAction.applyFO( panel );
			
		} else {

			// XSLT

			ok = XSLTAction.finalTransform( type, panel, false, false, new ErrorListener()  {
				public void warning(TransformerException exception)
						throws TransformerException {
				}
				public void fatalError(TransformerException exception)
						throws TransformerException {
				}			
				public void error(TransformerException exception)
						throws TransformerException {
				}
			}
			);

		}

		if ( !ok ) {
			EditixFactory.buildAndShowErrorDialog( "Can't run this scenario, error found" );
		} else {
			EditixFactory.buildAndShowInformationDialog( "Scenario terminated" );
		}

	}

	private void addScenario() {
		// Extract all the XSLT parameters
		XMLContainer container = EditixFrame.THIS.getSelectedContainer();
		boolean found = false;
		if ( container != null ) {
			String xslFilter = XSLTConfigPanel.PARAM_PREFIX;
			String foFilter = "fo";
			String xqueryFilter = "xquery";

			Iterator properties = container.getProperties();
			if ( properties != null ) {
				StringBuffer res = new StringBuffer();
				while ( properties.hasNext() ) {
					String property = ( String )properties.next();
					if ( property.startsWith( xslFilter ) || 
							property.startsWith( foFilter ) ||
								property.startsWith( xqueryFilter )) {
						if ( res.length() > 0 )
							res.append( "¤" );
						res.append( property ).append( "=" ).append( container.getProperty( property ) );
						found = true;
					}
				}
				if ( res.length() > 0 ) {
		
					// Add the document type
					String type = container.getDocumentInfo().getType();
					res.append( "¤" ).append( "type=" ).append( type );
					
					FPNode parentNode = (FPNode) t.getSelectionPath()
					.getLastPathComponent();
	
					FPNode g = new FPNode( FPNode.TAG_NODE, "item");
					
					String name = container.getCurrentDocumentLocation();
					if ( name != null ) {
						int i = name.lastIndexOf( "/" );
						if ( i == -1 ) {
							i = name.lastIndexOf( "\\" );
						}
						if ( i > -1 ) {
							name = name.substring( i + 1 );
						}
					}

					if ( name == null ) {
						name = "My scenario";
					}

					g.setAttribute( "name", name );
					g.setAttribute( "param", res.toString() );
					parentNode.appendChild( g );
					selectNode( g );

				}
			}
		}
		if ( !found ) {
			EditixFactory.buildAndShowWarningDialog( "No transformation scenario found ?" );
		}
	}

	void refresh() {
		TreePath tp = t.getSelectionPath();
		((StyleTreeModel) t.getModel()).refresh();
		t.repaint();
		if (tp != null)
			t.setSelectionPath(tp);
	}

	private void addGroup() {
		String name = EditixFactory.buildAndShowInputDialog("Group name ?");
		if (name != null) {
			FPNode parentNode = (FPNode) t.getSelectionPath()
					.getLastPathComponent();
			FPNode g = new FPNode( FPNode.TAG_NODE, "group");
			g.setAttribute("name", name);
			parentNode.appendChild(g);

			selectNode(g);
		}
	}

	private void delete() {
		TreePath[] tps = t.getSelectionPaths();
		if (tps != null) {
			for (int i = 0; i < tps.length; i++) {
				FPNode selectedNode = (FPNode) (tps[i]
						.getLastPathComponent());
				FPNode parentNode = selectedNode.getFPParent();
				if (selectedNode.matchContent("group")) {
					if (!EditixFactory
							.buildAndShowConfirmDialog("Please confirm you wish to delete "
									+ selectedNode.getAttribute("name"))) {
						continue;
					}
				} else {
					if (selectedNode.isAttribute()) {
						parentNode
								.setAttribute(selectedNode.getContent(), null);
					}
				}
				parentNode.removeChildNode(selectedNode);
				selectNode(parentNode);
			}
		}
	}

	private TreePath convertToTreePath(FPNode node) {
		ArrayList al = new ArrayList();
		while (node != null) {
			al.add(0, node);
			node = (node.getFPParent());
		}
		return new TreePath(al.toArray());
	}

	private void selectNode(FPNode node) {
		final FPNode test = node;
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				TreePath tp = convertToTreePath(test);
				t.setSelectionPath(tp);
			}
		});
	}

	private void expandNode(FPNode node) {
		final FPNode test = node;
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				TreePath tp = convertToTreePath(test);
				t.expandPath(tp);
			}
		});
	}

	public void clean() {
		try {
			t.setModel(new StyleTreeModel(XSLScenariosTool.getRoot()));
			t.setSelectionPath(new TreePath(XSLScenariosTool.getRoot()));
		} catch (IOException e) {
			EditixFactory
					.buildAndShowErrorDialog("Can't load the style file "
							+ e.getMessage());

		}
	}

	boolean dragAndDropInUse = false;

	public void mouseDragged(MouseEvent e) {
		if (!dragAndDropInUse) {
			TreePath tp = t.getPathForLocation(e.getX(), e.getY());
			if (tp != null) {
				if (!((FPNode) tp.getLastPathComponent()).isAttribute()) {
					th.exportAsDrag(t, e, TransferHandler.MOVE);
					if (tp != null)
						t.setSelectionPath(tp);
					dragAndDropInUse = true;
				}
			}
		}
	}

	public void mouseReleased(MouseEvent e) {
		dragAndDropInUse = false;
	}

	public void mouseMoved(MouseEvent e) {
		TreePath tp = t.getPathForLocation(e.getX(), e.getY());
		if (tp != null) {
			FPNode node = (FPNode) tp.getLastPathComponent();
			String path = node.getAttribute("path");
			((XSLScenariosTreeRenderer) t.getCellRenderer()).fastlabel
					.setToolTipText(path);
			t.setToolTipText(path);
			((JComponent) t.getParent()).setToolTipText(path);
		}
	}

	public void mouseClicked(MouseEvent e) {
		if (e.isPopupTrigger() || e.getButton() >= 2) {
			JPopupMenu jpm = buildPopupMenu();
			jpm.show((Component) e.getSource(), e.getX(), e.getY());
			return;
		}

		if (e.getClickCount() >= 2) {
			if ( ( ( FPNode )t.getSelectionPath().getLastPathComponent() ).matchContent( "item" ) ) {
				runScenario();
			}
		}
	}

	private void rename(FPNode node) {

		String oldName;
		if (node.isAttribute()) {
			oldName = node.getNodeValue();
		} else
			oldName = node.getAttribute("name");

		String newName = EditixFactory.buildAndShowInputDialog("New name",
				oldName);

		if (newName != null) {

			if (!node.isAttribute()) {
				StringBuffer sb = new StringBuffer();
				// Clean newname
				for (int i = 0; i < newName.length(); i++) {
					if ( Character.isLetterOrDigit(newName.charAt(i)) || 
							( newName.charAt( i ) == '-' ) || 
								( newName.charAt( i ) == ' ' ) || 
									( newName.charAt( i ) == '.' ) )
						sb.append( newName.charAt(i) );
				}
				if (sb.length() == 0) {
					if (node.matchContent( "group" ) )
						sb.append("My nodes");
					else
						sb.append("myElement");
				}
				newName = sb.toString();
			} else {
				if (newName == null || newName.indexOf("=") == -1)
					newName = "myAtt=myVal";
			}

			if (node.isAttribute()) {
				node.setNodeValue(newName);
			} else {
				if (!newName.equals(oldName)) {
					node.setAttribute("name", newName);
					refresh();
				}
			}

		}
	}

	public void mouseEntered(MouseEvent e) {
	}

	public void mouseExited(MouseEvent e) {
	}

	public void mousePressed(MouseEvent e) {
	}

	// ///////////////////////////////////////////////////////////////

	class StyleTreeModel implements TreeModel {

		private FPNode root;

		StyleTreeModel(FPNode root) {
			this.root = root;
		}

		private ArrayList listeners;

		public void addTreeModelListener(TreeModelListener l) {
			if (listeners == null)
				listeners = new ArrayList();
			listeners.add(l);
		}

		public void refresh() {
			if (listeners != null) {
				for (int i = 0; i < listeners.size(); i++) {
					TreeModelListener l = (TreeModelListener) listeners.get(i);
					l.treeStructureChanged(new TreeModelEvent(t, new TreePath(
							root)));
				}
			}
		}

		public Object getChild(Object parent, int index) {
			FPNode sn = (FPNode) parent;
			if (sn.matchContent("group")) {
				return sn.childAt(index);
			} else {
				if (sn.matchContent("item")) {

					if (index < getAttCount(sn)) {
						return sn.getViewAttributeNodes().get(index + 2);
					} else
						return sn.childAt(index - getAttCount(sn));
				}
			}
			return null;
		}

		public int getChildCount(Object parent) {
			FPNode sn = (FPNode) parent;
			if (sn.matchContent("group")) {
				return sn.childCount();
			} else if (sn.matchContent("item")) {
				return sn.childCount() + getAttCount(sn);
			}
			return 0;
		}

		private int getAttCount(FPNode sn) {
			if (sn.getViewAttributeCount() > 2) {
				return sn.getViewAttributeCount() - 2
						- (sn.hasAttribute("hlp") ? 1 : 0);
			} else
				return 0;
		}

		public int getIndexOfChild(Object parent, Object child) {
			FPNode sn = (FPNode) parent;
			if (sn.matchContent("group")) {
				return sn.childNodeIndex((FPNode) child);
			} else if (sn.matchContent("item")) {

				int i = -1;

				if (sn.getViewAttributeCount() > 0) {
					i = sn.getViewAttributeNodes().indexOf(child);
				}
				if (i == -1) {
					i = getAttCount(sn) + sn.childNodeIndex((FPNode) child);
				}
				return i;
			}
			return -1;
		}

		public Object getRoot() {
			return root;
		}

		public boolean isLeaf(Object node) {
			FPNode sn = (FPNode) node;
			return (sn.getChildCount() == 0 && getAttCount(sn) == 0);
		}

		public void removeTreeModelListener(TreeModelListener l) {
			if (listeners != null)
				listeners.remove(l);
		}

		public void valueForPathChanged(TreePath path, Object newValue) {
		}
	}

	// /////////////////////////////////////////////////////////////

	private String resolveNodeToXMLText( FPNode n ) {
		if ( "item".equals( n.getContent() ) ) {
			return resolveNodeToXMLTextForItem( n );
		}
		StringBuffer sb = new StringBuffer();
		for ( int i = 0;i < n.childCount(); i++ ) {
			sb.append( resolveNodeToXMLText( n.childAt( i ) ) );
		}
		return sb.toString();
	}

	private String resolveNodeToXMLTextForItem( FPNode n ) {
		return n.getAttribute( "name" );
	}

	// ///////////////////////////////////////////////////////////////////////:

	DataFlavor flavors[] = { DataFlavor.stringFlavor };

	class NodeTransfertHandler extends TransferHandler {

		public int getSourceActions(JComponent c) {
			return TransferHandler.MOVE;
		}

		public boolean canImport(JComponent comp, DataFlavor[] transferFlavors) {

			if (comp instanceof JTree) {
				
				if (transferedPath != null) { // Internal drag'n drop
					return true;
				} else {
					/*
					 * if ( transferFlavors.length > 0 ) { if (
					 * "Simple node".equals( transferFlavors[ 0
					 * ].getHumanPresentableName() ) ) { return true; } }
					 */
				}
			}

			return false;
		}

		private TreePath transferedPath = null;

		protected Transferable createTransferable(JComponent c) {

			transferedPath = t.getSelectionPath();
			
			if ( transferedPath == null ) {
				return null;
			}

			Transferable transferable = new Transferable() {
				String txt = null;

				public Object getTransferData(DataFlavor flavor)
						throws UnsupportedFlavorException, IOException {
					if (isDataFlavorSupported(flavor)) {

						if (txt != null)
							return txt;

						FPNode n = ((FPNode) transferedPath
								.getLastPathComponent());

						txt = resolveNodeToXMLText(n);

						// Delete the text marker
						txt = txt.replaceAll(TEXT_TEMPLATE, "");

						return txt;
					}
					return null;
				}

				public DataFlavor[] getTransferDataFlavors() {
					return flavors;
				}

				public boolean isDataFlavorSupported(DataFlavor flavor) {
					return flavor.equals(DataFlavor.stringFlavor);
				}
			};
			return transferable;
		}

		protected void exportDone(JComponent source, Transferable data,
				int action) {
			super.exportDone(source, data, action);
			dragAndDropInUse = false;
		}

		public boolean importData(JComponent arg0, Transferable arg1) {
			try {
				TreePath tp = t.getSelectionPath();
				if (tp == null) {
					return false;
				}
				FPNode currentNode = (FPNode) tp.getLastPathComponent();
				if ( "item".equals( currentNode.getContent() ) )
					return false;
				FPNode movingNode = null;

				if (transferedPath != null)
					movingNode = (FPNode) transferedPath
							.getLastPathComponent();

				if (!tp.equals(transferedPath)) {
					FPNode newParentNode = currentNode;
					if (transferedPath != null)
						movingNode.getFPParent()
								.removeChildNode(movingNode); // Detach
					newParentNode.appendChild(movingNode); // Attach
					refresh();
					selectNode(movingNode);
					return true;
				}
				return false;
			} finally {
				dragAndDropInUse = false;
				transferedPath = null;
			}
		}
	}

}
