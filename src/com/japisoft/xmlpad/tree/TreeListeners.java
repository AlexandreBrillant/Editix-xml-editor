package com.japisoft.xmlpad.tree;

import com.japisoft.xmlpad.SharedProperties;
import com.japisoft.xmlpad.XMLContainer;
import com.japisoft.xmlpad.action.ActionModel;
import com.japisoft.xmlpad.editor.StructureDamagedListener;
import com.japisoft.xmlpad.editor.XMLEditor;
import com.japisoft.xmlpad.tree.parser.ParsingJob;
import com.japisoft.xmlpad.tree.renderer.FastTreeRenderer;

import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.text.BadLocationException;
import javax.swing.tree.*;
import javax.swing.*;

import java.io.*;
import java.awt.Point;
import java.awt.event.*;

import com.japisoft.framework.event.GlobalMouseAdapter;
import com.japisoft.framework.job.Job;
import com.japisoft.framework.job.JobManager;

import com.japisoft.framework.job.SwingEventSynchro;
import com.japisoft.framework.xml.parser.node.*;
import com.japisoft.framework.xml.parser.tools.XMLToolkit;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;

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
public class TreeListeners extends GlobalMouseAdapter implements
		StructureDamagedListener,
		TreeSelectionListener {

	private XMLContainer container;

	private ParsingJob parsingJob;

	private TreeErrorListener errorListener;

	public TreeListeners(XMLContainer container) {
		super();
		this.container = container;
		parsingJob = new ParsingJob(container, this);
	}

	private JTree treeDelegate;

	public TreeListeners(XMLContainer container, JTree tree) {
		this(container);
		this.treeDelegate = tree;
	}

	private boolean dispose = false;

	public void dispose() {
		if (dispose) { // ?
			return;
		}
		dispose = true;
		container.getUIAccessibility().getTree().removeMouseListener(this);
		container.getUIAccessibility().getTree()
				.removeMouseMotionListener(this);
		container.getUIAccessibility().getTree().getSelectionModel().removeTreeSelectionListener( this );
		this.container = null;
		this.treeDelegate = null;
		parsingJob.disposeAll();
	}

	private JPopupMenu popupDD = null;

	private ActionCopy actionCopy = null;

	private ActionMove actionMove = null;

	public TreeErrorListener getTreeErrorListener() {
		if (errorListener == null) {
			if (getTree() != null)
				errorListener = new TreeErrorListener(getTree());
		}
		return errorListener;
	}

	public void init() {
		getTree().getSelectionModel().addTreeSelectionListener( this );
		getTree().addMouseListener(this);
		getTree().addMouseMotionListener(this);
		getTree().setTransferHandler( new NodeDragDrop() );
	}
	
	private String errorMessage = null;

	// Show an error
	void setError(String message, int location, int line) {
		if (getTree() != null) {
			errorMessage = XMLContainer.getLocalizedMessage("LINE", "Line")
					+ " " + line + "," + message;
			// Force a scrolling to the root node
			if (getTree().getModel().getRoot() != null)
				getTree().scrollPathToVisible(
						new TreePath(getTree().getModel().getRoot()));
			else {
				getTree()
						.setModel(
								new DefaultTreeModel(new FPNode(
										FPNode.TAG_NODE, container
												.getLocalizedMessage("ERROR",
														"Error"))));
			}
			getTree().setToolTipText(message);
			if (getTree().getCellRenderer() instanceof FastTreeRenderer) {
				FastTreeRenderer ftr = (FastTreeRenderer) getTree()
						.getCellRenderer();
				ftr.activateError(errorMessage);
			}
			getTree().repaint();
		}
	}

	public JTree getTree() {
		if (treeDelegate != null)
			return treeDelegate;
		return container.getTree();
	}

	private XMLEditor getEditor() {
		return container.getEditor();
	}

	public void mouseMoved(MouseEvent e) {
		if (SharedProperties.TOOLTIP_TREE) {
			// Force a selection before
			TreePath tpt = getTree().getPathForLocation(e.getX(), e.getY());
			if (tpt != null) {
				if (getTree().getCellRenderer() instanceof FastTreeRenderer) {
					FastTreeRenderer ftr = (FastTreeRenderer) getTree()
							.getCellRenderer();
					FPNode sn = (FPNode) tpt.getLastPathComponent();
					String error = sn.errorMessage;
					if (error != null) {
						ftr.getInnerComponent().setToolTipText(error);
					} else
						ftr.getInnerComponent().setToolTipText(
								getToolTipForNode(sn));
				}
			}
		}
	}

	private String getToolTipForNode(FPNode n) {
		StringBuffer sb = new StringBuffer();
		sb.append("<html><body>");

		if (n.getType() == FPNode.TAG_NODE) {
			sb.append("<b>");
			if (n.getNameSpacePrefix() != null) {
				sb.append(n.getNameSpacePrefix()).append(":");
			}

			sb.append(n.getContent());
			if (n.getNameSpaceURI() != null) {
				sb.append("<i> (").append(n.getNameSpaceURI()).append(") </i>");
			}
			sb.append("</b>");
			sb.append("<br>");
		}

		sb.append("Loc: <i>").append(n.getXPathLocation()).append("</i>");

		if (n.getType() == FPNode.TEXT_NODE)
			sb.append("<br><div style='width:300px'>").append(
					n.getNodeContent()).append("</div>");
		else {
			if ( n.getViewAttributeCount() > 0 )
				sb.append( "<br>Attributes:" );
			for (int i = 0; i < n.getViewAttributeCount(); i++) {
				sb.append("<br>");
				String attName = n.getViewAttributeAt(i);
				String attVal = n.getAttribute(attName);
				sb.append("-<b>").append(attName).append("</b> ")
						.append(attVal);
			}
		}
		sb.append("</body></html>");
		return sb.toString();
	}

	public void mouseEntered(MouseEvent e) {
		ToolTipManager.sharedInstance().registerComponent(getTree());
	}

	public void mouseExited(MouseEvent e) {
		ToolTipManager.sharedInstance().unregisterComponent(getTree());
	}

	private boolean popupEnabled = true;

	public void setPopupEnabled(boolean enabled) {
		this.popupEnabled = enabled;
	}

	private Point lastMousePressed = null;
	
	public void mousePressed(MouseEvent e) {
		lastMousePressed = e.getPoint();
		ActionModel.resetActionState(container);
		if (!container.hasFocus())
			return;

		if ( ( e.getModifiers() & KeyEvent.CTRL_MASK ) != 0 ) {
			copyMode = true;
		}
			else {
			copyMode = false;
		}
		
		// Force a selection before
		TreePath tpt = getTree().getPathForLocation(e.getX(), e.getY());

		if (tpt != null) {
			getTree().setSelectionPath(tpt);
		}

		if (popupEnabled && e.isPopupTrigger() && container.isEditable()) {
			if (container.getCurrentTreePopup() != null) {
				container.getCurrentTreePopup().show(e.getComponent(),
						e.getX(), e.getY());
			}
		} else {
			// Is there a not ?
			if (tpt != null) {
				FPNode nodeToMove = (FPNode) tpt.getLastPathComponent();
				if (nodeToMove.isRoot()) {
					// Support for drag-and-drop

/*					TransferHandler handler = getTree().getTransferHandler();
					handler.exportAsDrag(getTree(), e, TransferHandler.MOVE); */
				}
			}
		}
	}



	public void mouseReleased(MouseEvent e) {
		mousePressed(e);
	}

	private boolean notifiedErrorNonTemporary = false;

	/** For inner usage only */
	public void notifiedErrorNonTemporary() {
		notifiedErrorNonTemporary = true;
	}

	public void mouseClicked(MouseEvent e) {
		if (!container.hasFocus()) {
			return;
		}
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				if (getTree().getSelectionPath() != null) {
					selectNode(getTree().getSelectionPath());
				}
			}
		});
	}

	public void selectNodeFromPath(String xpath) {
		if (getTree().getModel() instanceof DefaultTreeModel) {
			DefaultTreeModel dtm = (DefaultTreeModel) getTree().getModel();
			if (dtm.getRoot() instanceof FPNode) {
				FPNode root = (FPNode) dtm.getRoot();
				FPNode node = root.getNodeForXPathLocation(xpath, true);
				if (node != null) {
					disableHistory = true;
					resetTreeLocation(node);
					disableHistory = false;
					// getEditor().highlightLine( node.getStartingLine() );
				}
			}
		}
	}

	private boolean disableHistory = false;

	private void selectNode(TreePath tp) {
		FPNode access = (FPNode) tp.getLastPathComponent();

		if (access != null) {
			int line = access.getStartingLine();
			boolean loc = locationUsed;
			locationUsed = false;
			int offset = access.getStartingOffset(); // JPF

			container.updateNodeLocation(access);

			getEditor().setCaretPositionWithoutNotification(offset);
			// if ( !access.isText() )
			getEditor().highlightLine(line);
			locationUsed = loc;
		}
	}
	
	public void valueChanged(TreeSelectionEvent e) {
		if ( !disableHistory && 
				container.getUIAccessibility().getTree().hasFocus() )
			selectNode( e.getPath() );
	}	

	/**
	 * @return true if a parsing process is working avoiding the real time
	 *         location
	 */
	public boolean isLocationLocked() {
		return parsingJob.isLocationLocked();
	}

	public void notifyStructureChanged() {

		// For text underline without a tree
		if (getEditor() == null)
			return;

		// Parse it with FastParser
		JobManager.addJob(parsingJob);
	}

	private FPNode lastTreeLocation;

	private boolean locationUsed = true;

	/** @return the current tree state */
	public TreeState getState() {
		if (getTree() == null)
			return null;
		TreeState state = new TreeState();
		state.model = getTree().getModel();
		state.selected = getTree().getSelectionPath();
		state.expanded = false;
		if (state.selected != null)
			state.expanded = getTree().isExpanded(state.selected);
		return state;
	}

	/** Update the current tree state */
	public void setState(TreeState state) {
		if (getTree() != null) {
			if (state.model != getTree().getModel()) {
				getTree().setModel(state.model);
				if (state.selected != null) {
					getTree().setSelectionPath(state.selected);
					if (state.expanded)
						getTree().expandPath(state.selected);
				}
			}
		}
	}

	public void resetTreeLocation(FPNode content) {
		if (!locationUsed)
			return;
		if (getTree() == null)
			return;

		if (tlJob == null)
			tlJob = new TreeLocationJob();

		tlJob.setContent(content);
		JobManager.addJob(tlJob);
	}

	TreeLocationJob tlJob = null;

	class TreeLocationJob implements Job, SwingEventSynchro {
		public void stopIt() {
		}
		
		public boolean hasErrors() {
			return false;
		}

		public void dispose() {
			content = null;
			tp = null;
		}

		public Object getSource() {
			return container;
		}

		public boolean isAlone() {
			return true;
		}

		private FPNode content = null;

		private TreePath tp = null;

		public void setContent(FPNode content) {
			this.content = content;
		}

		public boolean preRun() {
			if (content == null)
				return false;
			else {
				tp = XMLToolkit.getTreePath(content);
				return true;
			}
		}

		public void run() {
			try {
				getTree().setSelectionPath(tp);
				getTree().scrollPathToVisible(tp);
				lastTreeLocation = content;
			} catch (Throwable th) {
			}
		}
	}

	// //////////////////////////////////////////////////////////

	public static Class _parsingJobClass = ParsingJob.class;

	// ////////////////////// Drag'n drop support

	// ////////////////////// DRAG N' DROP ////////////////////////////

	class ActionCopy extends AbstractAction {
		public ActionCopy() {
			super("Copy");
		}

		public void actionPerformed(ActionEvent e) {
			if (DDSource != null && DDTarget != null)
				insertNodeInto(DDSource, DDTarget, true);
		}
	}

	class ActionMove extends AbstractAction {
		public ActionMove() {
			super("Move");
		}

		public void actionPerformed(ActionEvent e) {
			if (DDSource != null && DDTarget != null) {
				// Check if DDSource is not an ancestor of DDTarget
				FPNode parent = DDTarget.getFPParent();
				while (parent != null) {
					if (parent == DDSource) {
						// Illegal operation
						JOptionPane.showMessageDialog(getTree(),
								"Can't move a parent to a child");
						return;
					}
					parent = parent.getFPParent();
				}

				insertNodeInto(DDSource, DDTarget, false);
			}
		}
	}

	class ModalDDPopup extends Thread {
		public synchronized void run() {
			try {
				while (popupDD.isVisible()) {
					Thread.sleep(40);
				}
				TreeListeners.this.notify();
			} catch (Throwable th) {
			}

			DDSource = null;
			DDTarget = null;
		}
	}

	FPNode DDSource = null;

	FPNode DDTarget = null;

	private void actionForDrop(FPNode source, FPNode target, int x,
			int y) {

		DDSource = source;
		DDTarget = target;

		if (popupDD == null) {
			popupDD = new JPopupMenu();
			popupDD.add(actionCopy = new ActionCopy());
			popupDD.add(actionMove = new ActionMove());
		}

		if (target.isTag()) {
			if (!target.isAutoClose() || target.isRoot()) {
				actionCopy.putValue(Action.NAME, "Copy "
						+ getLabelForNode(source) + " into "
						+ getLabelForNode(target));
				actionMove.putValue(Action.NAME, "Move "
						+ getLabelForNode(source) + " into "
						+ getLabelForNode(target));
			} else {
				try {
					actionCopy.putValue(Action.NAME, "Copy "
							+ getLabelForNode(source) + " into "
							+ getLabelForNode(target));
					actionMove.putValue(Action.NAME, "Move "
							+ getLabelForNode(source) + " into "
							+ getLabelForNode(target));
				} catch (NullPointerException exc) {
					// ?
				}
			}
		} else {
			actionCopy.putValue(Action.NAME, "Copy");
			actionMove.putValue(Action.NAME, "Move");
		}

		popupDD.show(getTree(), x, y);

		// Wait for the popup to terminate for freeing resource

		ModalDDPopup modal = new ModalDDPopup();
		modal.start();
	}

	private String getLabelForNode(FPNode node) {
		if (node.isText())
			return "your text...";
		else
			return "<" + node.getQualifiedContent() + ">";
	}

	private void insertNodeInto(
			FPNode source, 
			FPNode target,
			boolean copy) {

		if ( source == target )
			return;
		
		FPNode ancestorCheck = target;
		while ( ancestorCheck != null ) {
			ancestorCheck = ancestorCheck.getFPParent();
			if ( ancestorCheck == source )
				return;
		}
		
		int start = source.getStartingOffset();
		int stop = source.getStoppingOffset();

		if (source.isText())
			stop--;

		try {

			// Remove from source
			String text = container.getDocument().getText(
					start,
					stop - start + 1 );

			int start2 = target.getStartingOffset();

			// Insert in the new parent node
			start2 = container.getText().indexOf( '>', start2 ) + 1;

			if ( target.isAutoClose() ) {
				container.getEditor().select( start2 - 2, start2 );
				String addEndPart = "></" + target.getContent() + ">";
				container.getEditor().replaceSelection( addEndPart );
				start2--;
				if ( start > start2 ) {
					start += 2 + target.getContent().length();
					stop += 2 + target.getContent().length();
				}
			}
			
			// Disable the tree update
			boolean oldStructureDamagedState = container
					.isRealTimeTreeOnTextChange();
			container.setRealTimeTreeOnTextChange( false );

			try {

				container.getDocument().insertString(start2, text, null);

				if (start2 < start) {
					start += text.length();
					stop += text.length();
				}

				if ( !copy )
					container.getDocument().remove( start, stop - start + 1 );
				
			} finally {
				container.setRealTimeTreeOnTextChange(oldStructureDamagedState);
			}

			notifyStructureChanged();

		} catch (BadLocationException exc) {
			// exc.printStackTrace();
		}
	}

	private boolean copyMode = false; 
	
	public void mouseDragged(MouseEvent e) {
		if ( lastMousePressed.distance( e.getPoint() ) > 4 && container.hasTreeDragDrop() ) {
			getTree().getTransferHandler().exportAsDrag( 
					getTree(),
					e, 
					copyMode ? TransferHandler.COPY : TransferHandler.MOVE );
		}
	}		

	class NodeDragDrop extends TransferHandler {
		
		protected Transferable createTransferable( JComponent c ) {
			TreePath tp = getTree().getSelectionPath();
			return new TransferableTreeNode( ( FPNode )tp.getLastPathComponent() );
		}

		public boolean canImport(JComponent comp, DataFlavor[] transferFlavors) {
			for ( int i = 0; i < transferFlavors.length; i++ ) {
				if ( transferFlavors[ i ].getRepresentationClass() == FPNode.class )
					return true;
			}
			return false;
		}
		
		public void exportAsDrag(JComponent comp, InputEvent e, int action) {
			TreePath tp = getTree().getSelectionPath();
			if ( !( ( FPNode )tp.getLastPathComponent() ).isRoot() )
				super.exportAsDrag(comp, e, action);
		}
		
		public boolean importData(JComponent comp, Transferable t) {
			try {
				TreePath tp = 
					getTree().getSelectionPath();
				if ( tp != null ) {					
					insertNodeInto( 
							( FPNode )t.getTransferData( NODE_FLAVOR ), 
							( FPNode )tp.getLastPathComponent(), copyMode );
				}
				return super.importData(comp, t);
			} catch (UnsupportedFlavorException e) {
				return false;
			} catch (IOException e) {
				return false;
			}
		}
		public int getSourceActions(JComponent c) {
			return copyMode ? COPY : MOVE;
		}
	}
	
	public static DataFlavor NODE_FLAVOR = new DataFlavor(FPNode.class,
			"Simple node");

	class TransferableTreeNode implements Transferable {
		DataFlavor flavors[] = { NODE_FLAVOR };

		FPNode node;

		public TransferableTreeNode(FPNode node) {
			this.node = node;
		}

		public synchronized DataFlavor[] getTransferDataFlavors() {
			return flavors;
		}

		public boolean isDataFlavorSupported(DataFlavor flavor) {
			return (flavor.getRepresentationClass() == FPNode.class);
		}

		public synchronized Object getTransferData(DataFlavor flavor)
				throws UnsupportedFlavorException, IOException {
			if (isDataFlavorSupported(flavor)) {
				return (Object) node;
			} else {
				throw new UnsupportedFlavorException(flavor);
			}
		}

	}

/*	
	public class TreeDragSource implements DragSourceListener,
			DragGestureListener {

		DragSource source;

		DragGestureRecognizer recognizer;

		TransferableTreeNode transferable;

		SimpleNode oldNode;

		JTree sourceTree;

		AutoScroll as = null;

		public TreeDragSource(JTree tree, int actions) {
			sourceTree = tree;
			source = new DragSource();
			recognizer = source.createDefaultDragGestureRecognizer(sourceTree,
					actions, this);
			as = new AutoScroll(tree, new Insets(10, 10, 10, 10));
		}

		public void dragGestureRecognized(DragGestureEvent dge) {

			if (!container.isEditable() || container.hasErrorMessage())
				return;

			TreePath path = sourceTree.getSelectionPath();
			if (path == null) {
				return;
			}

			oldNode = (SimpleNode) path.getLastPathComponent();

			if (oldNode.isRoot()) {
				if (!container.getUIAccessibility().isEnabledDragNDropForRoot())
					return;
			}

			transferable = new TransferableTreeNode(oldNode);

			try {

				source.startDrag(dge, DragSource.DefaultCopyDrop, transferable,
						this);

			} catch (Throwable th) {
			}
		}

		public void dragEnter(DragSourceDragEvent dsde) {
		}

		public void dragExit(DragSourceEvent dse) {
		}

		public void dragOver(DragSourceDragEvent dsde) {
			Point p = dsde.getLocation();
			SwingUtilities.convertPointFromScreen(p, getTree());
			as.autoscroll(p);
		}

		public void dropActionChanged(DragSourceDragEvent dsde) {
		}

		public void dragDropEnd(DragSourceDropEvent dsde) {
		}
	}

	class TreeDropTarget implements DropTargetListener {

		DropTarget target;

		JTree targetTree;

		public TreeDropTarget(JTree tree) {
			targetTree = tree;
			target = new DropTarget(targetTree, this);
		}

		private SimpleNode getNodeForEvent(DropTargetDragEvent dtde) {
			Point p = dtde.getLocation();
			DropTargetContext dtc = dtde.getDropTargetContext();
			JTree tree = (JTree) dtc.getComponent();
			TreePath path = tree.getClosestPathForLocation(p.x, p.y);
			if (path != null)
				return (SimpleNode) path.getLastPathComponent();
			else
				return null;
		}

		public void dragEnter(DropTargetDragEvent dtde) {
		}

		public void dragOver(DropTargetDragEvent dtde) {
		}

		public void dragExit(DropTargetEvent dte) {
		}

		public void dropActionChanged(DropTargetDragEvent dtde) {
		}

		public void drop(DropTargetDropEvent dtde) {
			Point pt = dtde.getLocation();
			DropTargetContext dtc = dtde.getDropTargetContext();
			JTree tree = (JTree) dtc.getComponent();
			TreePath parentpath = tree.getClosestPathForLocation(pt.x, pt.y);

			if (parentpath == null) {
				dtde.rejectDrop();
				return;
			}

			SimpleNode parent = (SimpleNode) parentpath.getLastPathComponent();

			try {
				Transferable tr = dtde.getTransferable();
				DataFlavor[] flavors = tr.getTransferDataFlavors();

				for (int i = 0; i < flavors.length; i++) {

					if (flavors[i].isFlavorJavaFileListType())
						continue;

					if (tr.isDataFlavorSupported(flavors[i])) {

						if (tr.getTransferData(flavors[i]) instanceof SimpleNode) {
							SimpleNode node = (SimpleNode) tr
									.getTransferData(flavors[i]);

							if (node == parent) {
								dtde.rejectDrop();
								return;
							}

							dtde.acceptDrop(dtde.getDropAction());

							if (node != null) {
								actionForDrop(node, parent, pt.x, pt.y);
								dtde.dropComplete(true);
								return;
							}
						}
					}
				}
				dtde.rejectDrop();
			} catch (Exception e) {
				e.printStackTrace();
				dtde.rejectDrop();
			}
		}
	}

	*/
	
}

// RealTimeTreeManager ends here
