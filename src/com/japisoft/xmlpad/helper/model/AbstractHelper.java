package com.japisoft.xmlpad.helper.model;

import java.awt.Color;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.event.ActionEvent;

import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;

import javax.swing.AbstractAction;
import javax.swing.DefaultListModel;

import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;
import javax.swing.ListCellRenderer;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.border.EtchedBorder;
import javax.swing.text.BadLocationException;

import com.japisoft.framework.xml.parser.node.FPNode;
import com.japisoft.xmlpad.ComponentFactory;
import com.japisoft.xmlpad.SharedProperties;
import com.japisoft.xmlpad.editor.XMLPadDocument;
import com.japisoft.xmlpad.editor.XMLEditor;
import com.japisoft.xmlpad.helper.ui.HelperUIContext;
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
public class AbstractHelper implements Helper {

	public String getTitle() {
		return null;
	}
	
	/** Prepare this helper for this location */
	public void setLocation(FPNode loc, int offset ) {
	}

	protected String namespace;
	
	public void setNamespace( String namespace ) {
		this.namespace = namespace;
	}

	public String getNamespace() { return namespace; };
	
	protected XMLEditor editor;

	/** For inner usage */
	public void setEditor( XMLEditor editor ) {
		this.editor = editor;
	}

	private String charToInsert = null;

	/** Show the helper on this editor at this offset location */
	public boolean show( int offset, String charToInsert ) {
		return show( offset, charToInsert, true );
	}
	
	/** Show the helper on this editor at this offset location */
	public boolean show( int offset, String charToInsert, boolean asynchronous ) {
		try {
			if ( !hasElements() )
				return false;
			this.charToInsert = charToInsert;
			Rectangle r = editor.modelToView( offset );
			showUI( offset + 1, r.x, r.y + r.height, asynchronous );
			return true;
		} catch ( BadLocationException ble ) {
			return false;
		}
	}

	public static JPanel WIN = null;
	private static TitledPanelHelper titleLabel = null;

	/** @return <code>true</code> if this helper has elements */
	protected boolean hasElements() {
		return false;
	}

	private static JList list;
	
	class HidePopupOnFocusLost extends FocusAdapter {
		public void focusLost(FocusEvent e) {
			if (editor != null) {
				try {
					releasePopup();
				} catch (Throwable th) {
					// Theorically could throw a Nullpointer Exception
				}
			}
		}
	}

	private Timer t = null;

	private void showUI( int offset, int x, int y ) {
		showUI( offset, x, y, true );
	}

	/** Show the popup window for this editor at this location */
	private void showUI( int offset, int x, int y, boolean asynchronous ) {
		if ( asynchronous ) {
			t = new Timer(
					SharedProperties.HELPER_DELAY,
					asynchronousHelper = new AsynchronousShowHelper( offset, x, y ) );
			t.setRepeats( false );
			t.start();
		} else {
			asynchronousHelper = new AsynchronousShowHelper( offset, x, y );
			asynchronousHelper.actionPerformed( new ActionEvent( this, 0, null ) );
//			asynchronousHelper.dispose();
		}
	}

	private void removePopup() {
		if (t != null) {
			t.stop();
			t.removeActionListener(asynchronousHelper);
			t = null;
		}

		WIN.setVisible(false);
		editor.remove(WIN);
		editor.invalidate();
		editor.validate();
	}

	private void releasePopup() {
		removePopup();
		editor.getXMLDocument().resetCompletionMode(false);
		editor.requestFocus();
		editor = null;
		asynchronousHelper.dispose();
		asynchronousHelper = null;
	}

	public void dispose() {
		try {
			((NoSelectionAction) list.getActionMap().get( "noSelection" ) )
					.dispose();
			((SelectionAction) list.getActionMap().get( "itemSelection" ) )
					.dispose();
			list.getActionMap().remove( "noSelection" );
			list.getActionMap().remove( "itemSelection" );
			editor = null;
		} catch (Throwable th) {
		}
	}

	/** @return a renderer for helper items */
	protected ListCellRenderer getListCellRenderer() {
		return null;
	}

	protected void fillList( FPNode node, DefaultListModel model ) {
	}

	protected void prepareDocumentBeforeInserting( XMLPadDocument document, int offset ) {
	}
	
	protected void insertResult( XMLPadDocument doc, int offset, String result, Descriptor descriptor, String added ) {
		doc.insertStringWithoutHelper(
				offset,
				result, null);		
	}
	
	class SelectionAction extends AbstractAction {

		public void dispose() {
		}

		public void actionPerformed(ActionEvent e) {

			XMLPadDocument doc = editor.getXMLDocument();
			String added = "";
			if ( asynchronousHelper.completionBuffer != null )
				added = asynchronousHelper.completionBuffer.toString();

			String element = null;
			Descriptor td;

			if (list.getModel().getSize() > 0) {

				element = ( td = ( Descriptor ) list.getSelectedValue() )
						.toExternalForm();

				if ( added.length() >= element.length() )
					element = "";
				else
					element = element.substring( added.length() );

				if ( charToInsert != null ) {
					element = ( charToInsert + added + element );
					asynchronousHelper.offset--;
				}

				prepareDocumentBeforeInserting( doc, asynchronousHelper.offset );				
				insertResult( doc, asynchronousHelper.offset, element, td, added );

				if (!td.isRaw()) {
					int i = element.indexOf("\"");

					if (i > -1) {
						((XMLPadDocument) doc).getCurrentEditor().setCaretPosition(
								asynchronousHelper.offset + i + 1);
					} else {
						i = element.indexOf("><");
						if (i > -1)
							((XMLPadDocument) doc).getCurrentEditor().setCaretPosition(
									asynchronousHelper.offset + i + 1);
					}
				} else {
					int i = element.indexOf(" ");
					if (i > -1) {
						((XMLPadDocument) doc).getCurrentEditor().setCaretPosition(
								asynchronousHelper.offset + i + 1);
					}
				}
			}
			
			releasePopup();
		}
	}

	/////////////////////////////////

	class NoSelectionAction extends AbstractAction {
		int offset;

		public NoSelectionAction(int offset) {
			this.offset = offset;
		}

		public void dispose() {
		}

		public void actionPerformed(ActionEvent e) {
			releasePopup();
		}
	}

	protected String getLostCharacter() {
		return " ";
	}

	//////////////////////////////////////////////////////////////////////
	////////// Asynchronous task /////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////

	private AsynchronousShowHelper asynchronousHelper = null;

	class AsynchronousShowHelper implements ActionListener, KeyListener,
			MouseListener, MouseMotionListener {

		private int ioffset;
		private int offset;
		private int x;
		private int y;

		StringBuffer completionBuffer = null;

		public AsynchronousShowHelper(int offset, int x, int y ) {
			this.offset = offset;
			this.ioffset = offset;
			this.x = x;
			this.y = y;
		}

		public void mouseDragged(MouseEvent e) {
			if ( resizeMode ) {
				int dx = e.getX() - xr;
				int dy = e.getY() - yr;
				if ( dx != 0 || dy != 0 ) {
					WIN.setSize( WIN.getWidth() + dx, WIN.getHeight() + dy );
					xr = e.getX();
					yr = e.getY();
					editor.getXMLContainer().setProperty( "dxHelper", new Integer( xr - ir) );
					editor.getXMLContainer().setProperty( "dyHelper", new Integer( yr - jr) );
				}
			}
		}
		
		public void mouseMoved(MouseEvent e) {
		}

		public void mouseClicked(MouseEvent e) {
			list.getActionMap().get( "itemSelection" ).actionPerformed( null );
		}

		public void mouseEntered(MouseEvent e) {
		}

		public void mouseExited(MouseEvent e) {
		}

		boolean resizeMode = false;
		int xr = 0;
		int yr = 0;
		int ir = 0;
		int jr = 0;
		
		public void mousePressed(MouseEvent e) {
			int x = e.getX();
			int y = e.getY();
			if ( x > list.getWidth() - 10 && y > list.getHeight() - 10 ) {
				resizeMode = true;
				xr = x;
				yr = y;
				ir = x;
				jr = y;
			} else
			resizeMode = false;
		}

		public void mouseReleased(MouseEvent e) {
			if ( resizeMode ) {
				WIN.invalidate();
				WIN.validate();
				WIN.repaint();
			}
			resizeMode = false;
		}
		
		public void keyPressed(KeyEvent e) {

			if (e.getKeyCode() == KeyEvent.VK_DELETE
					|| e.getKeyCode() == KeyEvent.VK_UNDO
					|| e.getKeyCode() == KeyEvent.VK_CLEAR
					|| e.getKeyCode() == 8 ) {
				if (completionBuffer.length() > 0) {
					completionBuffer
							.deleteCharAt(completionBuffer.length() - 1);
					if ( charToInsert == null ) {
						XMLPadDocument doc = editor.getXMLDocument();
						doc.removeWithoutStructureDamaged(offset - 1, 1);
					}
					if (offset > 0)
						offset--;
				}
			} else {
				if (e.getKeyChar() >= 'a' && e.getKeyChar() <= 'z'
						|| e.getKeyChar() >= 'A' && e.getKeyChar() <= 'Z'
							|| e.getKeyChar() >= '0' && e.getKeyChar() <= '9' 
								|| e.getKeyChar() == ':' || e.getKeyChar() == '_' ) {
					if ( charToInsert == null ) {
						XMLPadDocument doc = editor.getXMLDocument();
						doc.insertStringWithoutStructureDamaged(offset, ""
								+ e.getKeyChar(), null);
						offset++;
						
					}					
					completionBuffer.append( e.getKeyChar() );
				} else
					return;
			}
			synchronizeCompletionList();
		}

		public void keyReleased(KeyEvent e) {
		}

		public void keyTyped(KeyEvent e) {
		}

		private void synchronizeCompletionList() {
			DefaultListModel model = (DefaultListModel) list.getModel();
			model.removeAllElements();
			fillList(editor.getCurrentNodeLocation(), model);
			if ( completionBuffer != null && completionBuffer.length() > 0) {
				// remove all elements not starting with the completionBuffer

				String match = completionBuffer.toString();
				ArrayList toRemove = null;

				for (int i = 0; i < model.getSize(); i++) {
					Descriptor d = (Descriptor) model.get(i);

					if (!d.getNameForHelper().startsWith(match)) {
						if (toRemove == null)
							toRemove = new ArrayList();
						toRemove.add(d);
					}
				}

				if (toRemove != null) {
					for (int i = 0; i < toRemove.size(); i++)
						model.removeElement(toRemove.get(i));
				}
			}

			if ( model.getSize() == 0 )
				titleLabel.setTitle( "No completion" );
			else
				titleLabel.setTitle(getTitle());

			list.setSelectedIndex(0);
		}

		public void actionPerformed(ActionEvent e) {

			completionBuffer = ((XMLPadDocument) editor.getDocument())
					.getCompletionBuffer();
			if ( completionBuffer != null )
				offset += completionBuffer.length();

			if (WIN == null) {
				Window winTmp = null;
				WIN = new JPanel();
				
				list = new JList() {
					public void setSelectedIndex(int index) {
						super.setSelectedIndex(index);
						ensureIndexIsVisible(index);
					}
				};

				list.addFocusListener( new HidePopupOnFocusLost() );
				list.setModel( new DefaultListModel() );
				WIN.setLayout( new BorderLayout() );
				WIN.add( new JScrollPane( list ) );

				WIN.add( ( titleLabel = ComponentFactory.getFactory().getNewTitledPanelHelper() ).getView(), BorderLayout.NORTH);
				
				if ( titleLabel.getContext() != null )
					titleLabel.getContext().setDelegate( new NewHelperContext() );
				
				ListCellRenderer r = getListCellRenderer();

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

				Font tmp2;
				if ((tmp2 = UIManager.getFont(key + "font")) != null) {
					list.setFont(tmp2);
				} else
					tmp2 = list.getFont();

				titleLabel.getView().setFont(new Font(tmp2.getName(), Font.ITALIC, tmp2
						.getSize()));
				titleLabel.getView().setOpaque(true);
				titleLabel.getView().setBackground(list.getSelectionBackground());
				titleLabel.getView().setForeground(list.getSelectionForeground());
				titleLabel.getView().setBorder(new EtchedBorder(list
						.getSelectionForeground(), list
						.getSelectionBackground()));

				list.setCellRenderer(CommonDescriptorRenderer.getRenderer());
			}

			Point p = editor.getLocationOnScreen();

			synchronizeCompletionList();
		
			if (list.getModel().getSize() > 0) {
				// Force the firce enabled

				// Manager bound tagDescriptor due to substitutionGroup from the XML Schema

				for ( int i = 0; i < list.getModel().getSize(); i++ ) {
					Descriptor d = ( Descriptor )list.getModel().getElementAt( i );
					if ( d instanceof TagDescriptor ) {
						TagDescriptor td = ( TagDescriptor )d;
						for ( int j = 0; j < td.getSynonymousTagDescriptorCount(); j++ ) {
							( ( DefaultListModel )list.getModel() ).add( i + 1, td.getSynonymousTagDescriptor( j ) );
						}
					}
				}				

				int selection = 0;
				
				for ( int i = 0; i < list.getModel().getSize(); i++ ) {
					Descriptor d = ( Descriptor )list.getModel().getElementAt( i );
					if ( d.isEnabled() ) {
						selection = i;
						break;
					}
						
				}
				
				list.setSelectedIndex( selection );
			} else {
				releasePopup();
				dispose();
				return;
			}

			editor.add(WIN);

			int dx = ( ( Integer )editor.getXMLContainer().getProperty( "dxHelper", new Integer( 0 ) ) ).intValue();
			int dy = ( ( Integer )editor.getXMLContainer().getProperty( "dyHelper", new Integer( 0 ) ) ).intValue();
			
			WIN.setSize(list.getPreferredSize().width + 30 + dx, 100 + dy );
			
			if ( WIN.getWidth() < 20 && WIN.getHeight() < 20 ) {
				WIN.setSize(list.getPreferredSize().width + 30, 100 );
			}

			WIN.doLayout();

			//int yp = y + editor.getFontMetrics( editor.getFont()
			// ).getHeight();

			int yp = y;
			x += editor.getFontMetrics(editor.getFont()).charWidth(' ');

			Rectangle r = editor.getVisibleRect();

			if (WIN.getHeight() + yp > r.y + r.height) {
				yp = r.y + r.height - WIN.getHeight();
			}

			if ( WIN.getWidth() + x > r.x + r.width ) {
				x = r.x + r.width - WIN.getWidth();
			}
			
			WIN.setLocation(x, yp);

			list.getInputMap().put(
					KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),
					"itemSelection");
			list.getActionMap().put("itemSelection", new SelectionAction());
			list.getInputMap().put(
					KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
					"noSelection");
			list.getInputMap()
					.put(KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0),
							"noSelection");
			list.getActionMap().put("noSelection",
					new NoSelectionAction(offset));

			editor.invalidate();
			editor.validate();

			titleLabel.setTitle( getTitle() );
			list.addKeyListener( this );
			list.addMouseListener( this );
			list.addMouseMotionListener( this );
			
			WIN.setVisible( true );
			list.requestFocus();
		}

		private void dispose() {
			completionBuffer = null;
			list.removeKeyListener( AsynchronousShowHelper.this );
			list.removeMouseListener( AsynchronousShowHelper.this );
			list.removeMouseMotionListener( this );

			if (t != null) {
				t.stop();
				t.removeActionListener(asynchronousHelper);
			}
		}
	}

	//////////////////////////// SPECIFIC CONTEXT //////////////////////////////
	
	class NewHelperContext extends HelperUIContext {
		/** Close the helper */
		public void close() {
			((NoSelectionAction) list.getActionMap().get( "noSelection" ) ).actionPerformed( null );
		}
	}

}
