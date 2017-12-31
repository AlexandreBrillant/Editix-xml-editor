package com.japisoft.xmlpad.helper;

import java.awt.BorderLayout;
import java.awt.Color;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.DefaultListModel;

import javax.swing.JList;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JWindow;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

import javax.swing.UIManager;

import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

import com.japisoft.framework.xml.parser.node.FPNode;

import com.japisoft.xmlpad.Debug;
import com.japisoft.xmlpad.SharedProperties;
import com.japisoft.xmlpad.editor.XMLPadDocument;
import com.japisoft.xmlpad.editor.XMLEditor;
import com.japisoft.xmlpad.helper.model.AbstractDescriptor;
import com.japisoft.xmlpad.helper.model.CommonDescriptorRenderer;
import com.japisoft.xmlpad.helper.model.Descriptor;
import com.japisoft.xmlpad.helper.ui.TitledPanelHelper;

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
public class ContentAssistantUI implements 
		FocusListener, 
		MouseListener,
		ListSelectionListener, 
		KeyListener,
		WindowListener {

	private static final int COMMENT_DIST = 5;
	private static final String NO_SELECTION = "noSelection";
	private static final String ITEM_SELECTION = "itemSelection";
	private XMLEditor currentEditor = null;
	private int currentOffset;
	private String currentActivatorString = null;

	private ContentAssistantUIListener uiListener = null;
	private Point editorScreenLocation = null;

	
	public void show(
			ContentAssistantUIListener listener,
			String title, 
			XMLEditor editor, 
			int offset,
			String activatorString, 
			List<Descriptor> descriptors ) {

		try {

			this.uiListener = listener;
			
			Rectangle r = editor.modelToView( offset );
			int x = r.x;
			int y = r.y + r.height;
			editorScreenLocation = editor.getLocationOnScreen();

			window = getWindow(title, descriptors );

			currentActivatorString = activatorString;
			currentEditor = editor;
			currentOffset = offset;

			int dx = ((Integer) editor.getXMLContainer().getProperty(
					"dxHelper", new Integer(0))).intValue();
			int dy = ((Integer) editor.getXMLContainer().getProperty(
					"dyHelper", new Integer(0))).intValue();

			int rowHeight = list.getFontMetrics( list.getFont() ).getHeight();

			int height = window.getPreferredSize().height;

			height += dy + ( ( Math.min( 8, descriptors.size() ) ) * rowHeight );

			window.setSize(
					list.getPreferredSize().width + 25 + dx,
					window.getPreferredSize().height  );

			if ( window.getWidth() < COMMENT_DIST
					&& window.getHeight() < COMMENT_DIST ) {
				window.setSize(list.getPreferredSize().width + 30, 100);
			}

			int yp = y;
			x += editor.getFontMetrics(editor.getFont()).charWidth(' ');
			r = editor.getVisibleRect();

			if ( window.getHeight() + yp > r.y + r.height ) {
				yp = r.y + r.height - window.getHeight();
			}

			if ( window.getWidth() + x > r.x + r.width ) {
				x = r.x + r.width - window.getWidth();
			}

			window.setLocation(
				x + editorScreenLocation.x, 
				yp + editorScreenLocation.y 
			);

			disposeHelpPanel();

			selectTheFirst();
			window.setVisible( true );

		} catch (BadLocationException e) {

			// ?

		}
	}

	private void selectTheFirst() {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				int selection = 0;
				for (int i = 0; i < list.getModel().getSize(); i++) {
					Descriptor d = (Descriptor) list.getModel().getElementAt(i);
					if (d.isEnabled()) {
						selection = i;
						break;
					}
				}
				list.setSelectedIndex(selection);
			}
		});
	}

	private JFrame window = null;
	private CustomList list = null;
	private TitledPanelHelper label = null;
	private HelpPanel helpPanel = null;
	private AdaptingListModel listModel = null;

	private void showHelpPanel( String message ) {
		if ( !SharedProperties.HELPER_PANEL )
			return;
		
		if ( helpPanel == null ) {
			helpPanel = new HelpPanel();
			helpPanel.setSize( 200, window.getHeight() );
			if ( currentEditor == null ) {
				Debug.debug( "Wrong state for the ContentAssistantUI : no current editor for help" );
				return;
			}
			Rectangle r = currentEditor.getVisibleRect();
			int x = 0;
			int y = window.getY();

			if ( window.getX() + window.getWidth() + COMMENT_DIST
					+ helpPanel.getWidth() > r.width ) {
				x = window.getX() - helpPanel.getWidth() - COMMENT_DIST;
			} else {
				x = window.getX() + window.getWidth() + COMMENT_DIST;
			}

			if ( x < 0 ) {
				helpPanel.setSize(
						helpPanel.getWidth() - Math.abs( x ),
						helpPanel.getHeight() );
				if ( helpPanel.getWidth() < 50 )
					return;
				x = 0;
			}

			helpPanel.setLocation( x, y );
		}
		helpPanel.updateComment( message );
		helpPanel.setVisible( true );
	}

	private void hideHelpPanel() {
		if ( !SharedProperties.HELPER_PANEL )
			return;
		if ( helpPanel != null )
			helpPanel.setVisible( false );
	}

	private JFrame getWindow(String title, List<Descriptor> descriptors ) {
		if ( window == null ) {
			window = new JFrame();
			window.setUndecorated(true);
			window.setFocusableWindowState(true);
			window.addWindowListener( this );
			
			// window.setOpacity( 0.9f );

			window.getContentPane().setLayout(
				new BorderLayout()
			);

			list = new CustomList();
			list.setCellRenderer( 
				CommonDescriptorRenderer.getRenderer() 
			);
			window.add( new JScrollPane( list ) );

			String key = "xmlpad.helper.";
			Color tmp;
			if ((tmp = UIManager.getColor(key + "backgroundColor")) != null) {
				list.setBackground(tmp);
			}
			if ((tmp = UIManager.getColor(key + "foregroundColor")) != null) {
				list.setForeground(tmp);
			}
			if ((tmp = UIManager.getColor(key + "selectionBackgroundColor")) != null) {
				list.setSelectionBackground(tmp);
			}
			if ((tmp = UIManager.getColor(key + "selectionForegroundColor")) != null) {
				list.setSelectionForeground(tmp);
			}

			list.setCellRenderer(
				CommonDescriptorRenderer.getRenderer()
			);

			list.getInputMap().put(
					KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),
					ITEM_SELECTION);
			list.getActionMap().put(ITEM_SELECTION, new SelectionAction());
			list.getInputMap()
					.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
							NO_SELECTION);
			list.getInputMap().put(
					KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0), NO_SELECTION);
			list.getActionMap().put(NO_SELECTION, new NoSelectionAction());

			list.addFocusListener(this);
			list.addMouseListener(this);
			list.addListSelectionListener(this);
			list.addKeyListener(this);
		}

		// label.setTitle(title);
		listModel = new AdaptingListModel();
		
		for (int i = 0; i < descriptors.size(); i++) {
			Descriptor d = descriptors.get( i );
			listModel.addElement( d );
		}
		
		list.setVisibleRowCount( 
			Math.min( 
				8, 
				descriptors.size() 
			) 
		);

		list.setModel( listModel );
		return window;
	}

	public void dispose() {
		removePanel();
		list.getActionMap().remove( ITEM_SELECTION );
		list.getActionMap().remove( NO_SELECTION );
		list.removeFocusListener( this );
		list.removeMouseListener( this );
		list.removeListSelectionListener( this );
		list.removeKeyListener( this );
		window.removeWindowListener( this );
		list = null;
		window = null;
		label = null;
		listModel = null;
		uiListener = null;
		disposeHelpPanel();
	}

	public void disposeHelpPanel() {
		this.helpPanel = null;
	}

	public void removePanel() {
		if ( currentEditor != null ) {
			window.setVisible( false );
			if ( helpPanel != null )
				helpPanel.setVisible( false );
		}
		disposeDescriptors();
		currentEditor = null;
	}

	private void disposeDescriptors() {
		// Dispose all the descriptors
		for ( int i = 0; i < listModel.size(); i++ ) {
			( ( Descriptor )listModel.get( i ) ).dispose();
		}
	}

	public void focusGained(FocusEvent e) {}

	public void focusLost(FocusEvent e) {
		if (e.getComponent() == list)
			removePanel();
	}
	
	public void valueChanged(ListSelectionEvent e) {}

	public void mouseClicked(MouseEvent e) {
		list.getActionMap().get(ITEM_SELECTION).actionPerformed(null);
	}

	public void mouseEntered(MouseEvent e) {}
	public void mouseExited(MouseEvent e) {}
	public void mousePressed(MouseEvent e) {}
	public void mouseReleased(MouseEvent e) {}
	
	public void windowActivated(WindowEvent e) {
	}

	public void windowClosed(WindowEvent e) {
	}

	public void windowClosing(WindowEvent e) {
	}

	public void windowDeactivated(WindowEvent e) {
	}

	public void windowDeiconified(WindowEvent e) {
	}

	public void windowIconified(WindowEvent e) {
	}

	public void windowOpened(WindowEvent e) {
		list.requestFocus();						
	}

	public void keyPressed(KeyEvent e) {
		// Special sequence for increasing the size of the list
		if ( e.getKeyChar() == '+' && 
				e.isControlDown()) {
			// Increase the size
			window.setSize(window.getWidth(), window.getHeight() + 20);
			window.invalidate();
			window.validate();
			window.repaint();

			// Save it

			Integer val = (Integer) currentEditor.getXMLContainer()
					.getProperty("dyHelper", new Integer(0));
			currentEditor.getXMLContainer().setProperty("dyHelper",
					new Integer(val.intValue() + 20));

			return;
		}

		if ( e.getKeyChar() == '-' && 
				e.isControlDown() ) {
			// Decrease the size
			window.setSize(window.getWidth(), Math
					.max(20, window.getHeight() - 20));
			window.invalidate();
			window.validate();
			window.repaint();

			// Save it

			Integer val = (Integer) currentEditor.getXMLContainer()
					.getProperty("dyHelper", new Integer(0));
			currentEditor.getXMLContainer().setProperty("dyHelper",
					new Integer(val.intValue() - 20));
			return;
		}

		if ( e.getKeyCode() == KeyEvent.VK_DELETE
				|| e.getKeyCode() == KeyEvent.VK_UNDO
				|| e.getKeyCode() == KeyEvent.VK_CLEAR 
				|| e.getKeyCode() == 8 ) {
			listModel.removingLastFilteringChar();
			selectTheFirst();
		} else {
			if (e.getKeyCode() != KeyEvent.VK_DOWN
					&& e.getKeyCode() != KeyEvent.VK_UP && 
						( e.getKeyCode() > 40 || e.getKeyChar() == ':' ) ) {
				listModel.addFilteringChar(e.getKeyChar());
				selectTheFirst();
			}
		}
	}

	public void keyReleased(KeyEvent e) {}
	public void keyTyped(KeyEvent e) {}

	void activeDescriptor( XMLEditor currentEditor, int currentOffset, Descriptor d ) {
		// Particular case when another processing is requiring like
		// A color dialog box...
		if ( d instanceof AbstractDescriptor ) {
			AbstractDescriptor ad = ( AbstractDescriptor )d;
			if ( ad.getSpecificAction() != null ) {
				ad.getSpecificAction().putValue( "descriptor", ad );
				XMLEditor preservedCurrentEditor = currentEditor;
				ad.getSpecificAction().actionPerformed( null );
				// Due to dialog box
				currentEditor = preservedCurrentEditor;
				if ( ad.getSpecificAction().getValue( "descriptor" ) != null ) {
					d = ( Descriptor )ad.getSpecificAction().getValue( "descriptor" );
				}
			}
		}

		FPNode currentNode = currentEditor.getXMLContainer().getCurrentElementNode();

		if ( d != null ) {
			String toInsert = d.toExternalForm();

			// Check for marker : cursor location
			int cursorLocation = toInsert.indexOf( '¤' );
			if (cursorLocation > -1) {
				toInsert = toInsert.replaceAll( "¤", "" );
			}

			if ( d.getSource() != null &&
					d.getSource().hasDelegateForInsertingResult() ) {
				d.getSource().insertResult( 
						currentEditor.getXMLDocument(), 
						currentOffset, 
						toInsert );
			} else
				currentEditor.getXMLDocument().insertStringWithoutHelper(
						currentOffset, 
						toInsert, 
						null );

			if ( d.getSequence() != null ) { // Matching sequence, remove it
				int length = d.getSequence().length();
				try {
					currentEditor.getXMLDocument().remove( currentOffset - length, length );
					currentOffset = currentOffset - length;
				} catch( BadLocationException ble ) {
				}
			}
			
			int location = 
				currentOffset + 
				toInsert.length();

			if ( cursorLocation == -1 )
				for (int i = 0; i < toInsert.length(); i++) {
					if ( Character.isWhitespace(
							toInsert.charAt( i ) ) ) {
						location = currentOffset + i;
						break;
					}
				}
			else
				location = currentOffset + cursorLocation;

			currentEditor.setCaretPosition( location );
			
		}

		HelperManager hm = currentEditor.getXMLContainer().getHelperManager();
		int caret = currentEditor.getCaretPosition();
		XMLPadDocument doc = currentEditor.getXMLDocument();

		removePanel();
		
		if ( d != null && d.hasAutomaticNextHelper() ) {
			
			// Force a new panel
			// Use the previous current Node because a new tree can be updated
			window = null;

			doc.manageCompletion( 
					currentNode,
					true, 
					caret, 
					null );

		}		
	}
	
	// -------------------------------------------------------

	class AdaptingListModel extends DefaultListModel {
		private StringBuffer filteringBuffer = null;

		public AdaptingListModel() {}

		public void addFilteringChar(char c) {
			if (filteringBuffer == null)
				filteringBuffer = new StringBuffer();
			if (getSize() > 0) {
				filteringBuffer.append(c);
				fireContentsChanged(this, 0, getSize());
			}
		}

		public void removingLastFilteringChar() {
			if (filteringBuffer != null && filteringBuffer.length() > 0)
				filteringBuffer.deleteCharAt(filteringBuffer.length() - 1);
			fireContentsChanged(this, 0, getSize());
		}

		public Object getElementAt(int index) {
			int realSize = super.getSize();
			int aSize = 0;
			for (int i = 0; i < realSize; i++) {
				if (filteringBuffer == null
						|| super.getElementAt(i).toString().startsWith(
								filteringBuffer.toString())) {
					if (aSize == index)
						return super.getElementAt(i);
					aSize++;
				}
			}
			return null;
		}

		public int getSize() {
			int realSize = super.getSize();
			int aSize = 0;
			for (int i = 0; i < realSize; i++) {
				if (filteringBuffer == null
						|| super.getElementAt(i).toString().startsWith(
								filteringBuffer.toString()))
					aSize++;
			}
			return aSize;
		}
	}

	
	
	
	class NoSelectionAction extends AbstractAction {
		public void actionPerformed(ActionEvent e) {
			currentEditor.getXMLDocument().insertStringWithoutHelper(
					currentOffset, currentActivatorString, null);
			removePanel();
		}
	}

	/////////////////////////// CHOICE ///////////////////////////

	class SelectionAction extends AbstractAction {
		public void actionPerformed( ActionEvent e ) {
			Descriptor d = ( Descriptor ) list.getSelectedValue();
			activeDescriptor( currentEditor, currentOffset, d );
		}
		
	}

	class CustomList extends JList {
		public void setSelectedIndex( int index ) {
			super.setSelectedIndex( index );
			ensureIndexIsVisible( index );

			// Show the helper dialog
			
	 		int selection = list.getSelectedIndex();
			Descriptor d = ( Descriptor ) list.getModel().getElementAt( selection );		
			if ( d != null && 
					d.getComment() != null )
				showHelpPanel(
						d.getComment() );
			else
				hideHelpPanel();	
		}
	}

	class HelpPanel extends JWindow {
		JTextArea ta;

		HelpPanel() {
			// setOpacity(0.8f);
			getContentPane().setLayout(new BorderLayout());
			ta = new JTextArea();
			ta.setWrapStyleWord(true);
			ta.setLineWrap(true);
			ta.setBackground(new Color(250, 252, 199));
			ta.setForeground(Color.BLACK);
			ta.setEditable( false );
			getContentPane().add( ta );
		}

		public void updateComment(String comment) {
			ta.setText(comment);
		}
	}

}
