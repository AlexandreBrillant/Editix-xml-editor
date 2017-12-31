package com.japisoft.editix.ui.panels.style;

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

import com.japisoft.editix.ui.EditixFactory;
import com.japisoft.editix.ui.EditixFrame;

import com.japisoft.framework.application.descriptor.ActionModel;
import com.japisoft.framework.xml.parser.node.FPNode;
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
public class StyleUI extends javax.swing.JPanel implements MouseListener,
		MouseMotionListener, ActionListener, TreeSelectionListener {

	static final String TEXT_TEMPLATE = "=T=";

	static final String INSERT_CMD = "insert";
	static final String REN_CMD = "ren";
	static final String DEL_CMD = "del";
	static final String ADDD_CMD = "addd";
	static final String ADDS_CMD = "add";

	static final String[] TOOLBAR_ACTIONS = { 
		INSERT_CMD, 
		ADDS_CMD, 
		ADDD_CMD,
		REN_CMD, 
		DEL_CMD 
	};

	static final String[] POPUP_ACTIONS = { 
		INSERT_CMD, 
		ADDS_CMD, 
		ADDD_CMD,
		REN_CMD, 
		DEL_CMD 
	};

	private JTree t;
	private JToolBar tb;
	private NodeTransfertHandler th;

	public StyleUI() {
		setLayout(new BorderLayout());

		tb = new JToolBar();

		for (int i = 0; i < TOOLBAR_ACTIONS.length; i++) {
			tb.add(EditixFrame.THIS.getBuilder().getActionById(
					"style." + TOOLBAR_ACTIONS[i]));
		}

		add(tb, BorderLayout.NORTH);

		add(new JScrollPane(t = new JTree()), BorderLayout.CENTER);

		clean();
		t.setCellRenderer(new StyleTreeRenderer());
		t.setTransferHandler(th = new NodeTransfertHandler());
		setEnabledAction(DEL_CMD, false);

	}

	private JPopupMenu buildPopupMenu() {
		JPopupMenu menu = new JPopupMenu();
		for (int i = 0; i < POPUP_ACTIONS.length; i++) {
			if (POPUP_ACTIONS[i] == null)
				menu.addSeparator();
			else
				menu.add(EditixFrame.THIS.getBuilder().getActionById(
						"style." + POPUP_ACTIONS[i]));
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
		ActionModel.setEnabled("style." + name, enabled);
	}

	public void valueChanged(TreeSelectionEvent e) {
		TreePath tp = t.getSelectionPath();
		if (tp != null) {
			FPNode node = (FPNode) tp.getLastPathComponent();
			setEnabledAction(DEL_CMD, !node.isRoot() || node.isAttribute());
			setEnabledAction(ADDS_CMD,
					node.isRoot()
							|| (node.matchContent("group") || node
									.matchContent("item")));
			setEnabledAction(ADDD_CMD, node.isRoot()
					|| node.matchContent("group"));
			setEnabledAction(REN_CMD,
					node.isRoot()
							|| (node.matchContent("group")
									|| node.matchContent("item") || node
									.isAttribute()));
			setEnabledAction(INSERT_CMD, node.matchContent("item") || ( node.matchContent("group") ));
		}
	}

	public void actionPerformed(ActionEvent e) {
		String cmd = e.getActionCommand();

		if (t.getSelectionPath() == null) {
			return;
		}
		if (ADDS_CMD.equals(cmd)) {
			addStyle();
		} else if (ADDD_CMD.equals(cmd)) {
			addGroup();
		} else if (DEL_CMD.equals(cmd)) {
			delete();
		} else if (REN_CMD.equals(cmd)) {
			rename((FPNode) t.getSelectionPath().getLastPathComponent());
		} else if (INSERT_CMD.equals(cmd)) {
			insertSelectedItem();
		} 

		refresh();
	}

	void refresh() {
		TreePath tp = t.getSelectionPath();
		((StyleTreeModel) t.getModel()).refresh();
		t.repaint();
		if (tp != null)
			t.setSelectionPath(tp);
	}

	private String getStyleContentFromCSS( String text, int caret ) {
		// Search { without }
		int startProperties = -1;
		for ( int i = caret; i >= 0; i-- ) {
			if ( text.charAt( i ) == '}' ) {	// Wrong location
				return null;
			} else
			if ( text.charAt( i ) == '{' ) {
				startProperties = i + 1;
				break;
			}
		}
		if ( startProperties == -1 )
			return null;
		int endProperties = -1;
		for ( int i = caret; i < text.length(); i++ ) {
			if ( text.charAt( i ) == '}' ) {
				endProperties = i - 1;
				break;
			}
		}
		if ( endProperties == -1 )
			return null;
		String properties = text.substring( startProperties, endProperties );
		return properties;
	}

	private String getStyleContentFromNode( FPNode element ) {
		if ( element.hasAttribute( "style" ) ) {
			return element.getAttribute( "style" );
		} else {
			String[] attributesScan = {
					"background-attachment",
					"background-color",
					"background-image",
					"background-repeat",
					"background-position-horizontal",
					"background-position-vertical",
					"border-before-color",
					"border-before-style",
					"border-before-width",
					"border-after-color",
					"border-after-style",
					"border-after-width",
					"border-start-color",
					"border-start-style",
					"border-start-width",
					"border-end-color",
					"border-end-style",
					"border-end-width",
					"border-top-color",
					"border-top-style",
					"border-top-width",
					"border-bottom-color",
					"border-bottom-style",
					"border-bottom-width",
					"border-left-color",
					"border-left-style",
					"border-left-width",
				    "border-right-color",
				    "border-right-style",
				    "border-right-width",
				    "padding-before",
				    "padding-after",
				    "padding-start",
				    "padding-end",
				    "padding-top",
				    "padding-bottom",
				    "padding-left",
				    "padding-right",
				    "font-family",
				    "font-selection-strategy",
				    "font-size",
				    "font-stretch",
				    "font-size-adjust",
				    "font-style",
				    "font-variant",
				    "font-weight",	
				    "margin-top",
				    "margin-bottom",
				    "margin-left",
				    "margin-right",
				    "top",
				    "right",
				    "bottom",
				    "left",
				    "relative-position",				        
				    "height",
				    "width",
				    "text-align",
				    "color",
				    "float",
				    "background",
				    "background-position",
				    "border",
				    "border-bottom",
				    "border-color",
				    "border-left",
				    "border-right",
				    "border-style",
				    "border-spacing",
				    "border-top",
				    "border-width",
				    "font",
				    "margin",
				    "padding"				        
			};
			StringBuffer sb = null;
			for ( String att : attributesScan ) {
				if ( element.hasAttribute( att ) ) {
					if ( sb == null )
						sb = new StringBuffer();
					else
						sb.append( ";" );
					sb.append( att ).append( ":" ).append( element.getAttribute( att ) );
				}
			}
			if ( sb != null )
				return sb.toString();
			return null;
		}
	}

	private void addStyle() {

		XMLContainer container = EditixFrame.THIS.getSelectedContainer();
		if ( container == null ) {
			EditixFactory.buildAndShowErrorDialog( "You need to have an opened XML document for adding a style !" );
		} else {
			String rawStyle = null;
			XMLDocumentInfo info = container.getDocumentInfo();
			if ( "CSS".equalsIgnoreCase( info.getType() ) ) {
				String text = container.getText();
				int caret = container.getCaretPosition();
				rawStyle = getStyleContentFromCSS( text, caret );
			} else {
				FPNode e = container.getCurrentElementNode();
				if ( e == null ) {
					EditixFactory.buildAndShowWarningDialog( "No current element for extracting style" );
				} else {
					rawStyle = getStyleContentFromNode( e );
				}
			}
			if ( rawStyle == null ) {
				EditixFactory.buildAndShowWarningDialog( "Can't find a style at this location ?" );
			} else {
				String[] propertyLines = rawStyle.split( ";" );
				
				FPNode currentGroup = (FPNode) t.getSelectionPath().getLastPathComponent();
				if ( currentGroup == null )
					currentGroup = ( FPNode )t.getModel().getRoot();

				while ( !"group".equals( currentGroup.getContent() ) ) {
					currentGroup = 
						currentGroup.getFPParent();
				}

				FPNode n = null;

				for ( String propertyLine : propertyLines ) {
					propertyLine = propertyLine.trim();
					String[] content = propertyLine.split( ":" );
					if ( content.length == 2 ) {
						n = new FPNode( FPNode.TAG_NODE, "item" );
						
						String name = content[ 0 ].trim().toLowerCase();

						n.setAttribute( "name", name );
						n.setAttribute( "param", content[ 1 ].trim() );

						// Avoid duplicated properties
						for ( int i = 0; i < currentGroup.childCount(); i++ ) {
							if ( name.equalsIgnoreCase( currentGroup.childAt( i ).getAttribute( "name") ) ) {
								currentGroup.removeChildNodeAt( i );
								break;
							}
						}

						currentGroup.appendChild( n );

					}
				}
				
				if ( n != null ) {
					selectNode( n );
				}
			}
		}

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
			t.setModel(new StyleTreeModel(StyleTool.getRoot()));
			t.setSelectionPath(new TreePath(StyleTool.getRoot()));
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
			((StyleTreeRenderer) t.getCellRenderer()).fastlabel
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
			if ( ( ( FPNode )t.getSelectionPath().getLastPathComponent() ).matchContent( "item" ) )
				insertSelectedItem();
		}
	}

	private void insertSelectedItem() {
		if (t.getSelectionPath() == null)
			return;
		FPNode node = (FPNode) t.getSelectionPath()
				.getLastPathComponent();

		XMLContainer container = EditixFrame.THIS
				.getSelectedContainer();
		if (container == null) {
			EditixFactory
					.buildAndShowErrorDialog("No document found for inserting a style");
			return;
		}

		String selectedText = container.getEditor().getSelectedText();
		String xmlToInsert = resolveNodeToXMLText(node);
		if (selectedText != null) {
			xmlToInsert = xmlToInsert.replaceFirst(TEXT_TEMPLATE,
					selectedText);
			xmlToInsert = xmlToInsert.replaceAll(TEXT_TEMPLATE, "");
			container.getEditor().replaceSelection(xmlToInsert);
		} else {
			xmlToInsert = xmlToInsert.replaceAll(TEXT_TEMPLATE, "");
			container.getEditor().insertText(xmlToInsert);
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
					if ( Character.isLetterOrDigit(newName.charAt(i)) || ( newName.charAt( i ) == '-' ) )
						sb.append( newName.charAt(i) );
				}
				if (sb.length() == 0) {
					if (node.matchContent("group"))
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
		return n.getAttribute( "name" ) + ":" + n.getAttribute( "param" ) + ";";
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
