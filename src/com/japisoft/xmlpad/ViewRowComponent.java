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

package com.japisoft.xmlpad;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JViewport;
import javax.swing.UIManager;
import javax.swing.text.BadLocationException;
import javax.swing.text.Element;

import com.japisoft.framework.xml.parser.node.FPNode;
import com.japisoft.xmlpad.bookmark.BookmarkModel;
import com.japisoft.xmlpad.bookmark.BookmarkPosition;
import com.japisoft.xmlpad.editor.ViewPainterListener;
import com.japisoft.xmlpad.editor.XMLEditor;
import com.japisoft.xmlpad.error.ErrorListener;

/**
 * @author Alexandre Brillant (https://github.com/AlexandreBrillant/Editix-xml-editor)
 * @version 1.0 */
class ViewRowComponent extends JComponent implements 
		MouseListener,
		MouseMotionListener, 
		ErrorListener,
		ViewPainterListener {

	XMLContainer container;

	public ViewRowComponent( XMLContainer container ) {
		this.container = container;
		
		Color c = UIManager.getColor( "jxmlpad.rowcomponent.background" );
		if ( c == null )
			c = new Color( 240, 240, 240 );
		
		setBackground( c );
	}

	void dispose() {
		this.container = null;
	}

	private FPNode currentNode;

	int y1 = -1;

	int y2 = -1;

	Rectangle clickableZone = null;

	public Dimension getPreferredSize() {
		try {
			
			int width = extraWidth + 10;
			if ( container.getBookmarkContext() != null ) {
				if ( container.getBookmarkContext().getIcon() != null ) {
					width += container
					.getBookmarkContext().getIcon().getIconWidth();
				}
			}
			
			return new Dimension(
					width,
					container.getEditor().getPreferredSize().height);
		} catch (NullPointerException exc) {
			return new Dimension(10 + extraWidth,
					container.getEditor().getPreferredSize().height);
		}
	}

	public void mouseClicked(MouseEvent e) {


		boolean valid = true;

		if (clickableZone != null) {
			if (clickableZone.contains(e.getX(), e.getY())) {
				valid = false;
				if (errorMessage == null) {
					int inc = 0;
					if (container.getCurrentElementNode() == null)
						return;
					if (container.getCurrentElementNode().isTag())
						inc++;
					container.getEditor().select(
							container.getCurrentElementNode()
									.getStartingOffset(),
							container.getCurrentElementNode()
									.getStoppingOffset()
									+ inc);
				} else {
					container.getTreeListeners().notifiedErrorNonTemporary();
					// container.getEditor().notifyError(null, errorLine,
					// errorMessage);
					container.getEditor().highlightLine(errorLine);
				}
			}
		}

		if (valid && container.getBookmarkContext() != null) { // Bookmark the
			// selection
			Element root = container.getDocument().getDefaultRootElement();
			int offset = container.getEditor().viewToModel(
					new Point(1, e.getY()));
			int index = root.getElementIndex(offset);
			Element child = root.getElement(index);

			if (child != null) {

				// Find the current line
				try {

					BookmarkPosition oldOne = null;

					// Search if there's a known bookmark at this position

					for (int i = 0; i < container.getBookmarkContext()
							.getModel().getBookmarkCount(); i++) {
						BookmarkPosition position = container
								.getBookmarkContext().getModel()
								.getBookmarkPositionAt(i);
						int index2 = root.getElementIndex(position.getOffset());
						if (index2 == index) {
							oldOne = position;
							break;
						}
					}

					
					
					if (oldOne == null) {
						Object flag = container.getEditor().getHighlighter()
								.addHighlight(
										child.getStartOffset(),
										child.getStartOffset(),
										container.getBookmarkContext()
												.getHighlightPainter());
						container
								.getBookmarkContext()
								.getModel()
								.addBookmarkPosition(
										new BookmarkPosition(
												container
														.getDocument()
														.createPosition(
																child
																		.getStartOffset()),
												flag));
					} else {
						container.getEditor().getHighlighter().removeHighlight(
								oldOne.getHighlightFlag());
						container.getBookmarkContext().getModel()
								.removeBookmarkPosition(oldOne);
					}
				} catch (BadLocationException exc) {
				}

				repaint();
				container.getEditor().repaint();
			}
		}
	}

	public void mousePressed(MouseEvent e) {
	}

	public void mouseReleased(MouseEvent e) {
	}

	public void mouseEntered(MouseEvent e) {
	}

	public void mouseExited(MouseEvent e) {
	}

	public void mouseDragged(MouseEvent e) {
	}

	public void mouseMoved(MouseEvent e) {
		if (listOfErrors != null) {
			for (int i = 0; i < listOfErrors.size(); i += 2) {
				Rectangle r = (Rectangle) listOfErrors.get(i);
				if (r.contains(e.getX(), e.getY())) {
					setToolTipText((String) listOfErrors.get(i + 1));
				}
			}
		}
	}

	public void addNotify() {
		super.addNotify();
		addMouseListener(this);
		addMouseMotionListener(this);
	}

	public void removeNotify() {
		super.removeNotify();
		removeMouseListener(this);
		removeMouseMotionListener(this);
	}

	public void setCurrentNode(FPNode node) {
		this.currentNode = node;
		this.clickableZone = null;
		if (node == null) {
			y1 = -1;
			// JPF:dont show line at top of component
			y2 = -1;
		} else {
			y1 = 0;
			y2 = 0;
		}
	}

	// -------------------------------------

	private ArrayList listOfErrors = null;

	public void initErrorProcessing() {
	}

	public void stopErrorProcessing() {
	}

	private int fontHeight = 0;
	
	public void notifyError(Object context,boolean localError, String sourceLocation,
			int line, int col, int offset, String message, boolean onTheFly) {
		if ( localError ) {
			if ( line > 0 ) {
				
				if ( !onTheFly )
				
					line--;	// ??
				
				if (listOfErrors == null)
					listOfErrors = new ArrayList();
	
				if ( fontHeight == 0 ) {
					FontMetrics fm = container.getEditor().getFontMetrics(
							container.getEditor().getFont());
					fontHeight = fm.getHeight();
				}
				
	
				// System.out.println( "ViewRowComponent : ERROR AT LIGNE [" + line + "]" );
				
				int lineY = (line * fontHeight )
						+ SharedProperties.getBugLittleIcon().getIconHeight() / 2;
				listOfErrors.add( new Rectangle( 0, lineY, 10, 10 ) );
				listOfErrors.add( message );
			}
	
			if ( onTheFly )
				showOneError( message, line );
		}
	}

	public void notifyNoError(boolean onTheFly) {
		listOfErrors = null;
		unsetError();
	}

	// -------------------------------------

	private int errorLine = 0;

	private String errorMessage = null;

	private void unsetError() {
		showOneError(null, -1);
	}

	private void showOneError(String message, int line) {
		this.errorMessage = message;
		FontMetrics fm = container.getEditor().getFontMetrics(
				container.getEditor().getFont());
		this.errorLine = line * fm.getHeight();
		setToolTipText(errorMessage);
		repaint();
	}

	Map<Integer,Boolean> mapOpenElement = new HashMap<Integer, Boolean>();

	// ViewPaintListener
	public void paintElement( float x, float y ) {
	}

	// ViewPaintListener
	public void reset( float y ) {
	}	

	private Font bf = null;
	
	private Font getBoldFont( Graphics gc ) {
		if ( bf == null )
			bf = gc.getFont().deriveFont( Font.BOLD );
		return bf;
	}
	
	private static Color ERROR_COLOR = new Color( 0xEDC87E );
	
	public void paintComponent( Graphics gc ) {
		super.paintComponent(gc);
		Rectangle _r = getVisibleRect();
		gc.setColor( getBackground() );
		gc.fillRect( _r.x, _r.y, _r.width, _r.height );

		if (y1 == 0 && y2 == 0 && currentNode != null) {
			int start = currentNode.getStartingOffset();
			int stop = currentNode.getStoppingOffset();
			try {
				Rectangle r1 = container.getEditor().modelToView(start);
				Rectangle r2 = container.getEditor().modelToView(stop);
				JViewport p = (JViewport) container.getEditor().getParent();
				y1 = r1.y;
				y2 = r2.y + r2.height;

				int r = (y2 - y1) / 2 + y1;
				Point sp = p.getViewPosition();
				if (r < sp.y)
					r = sp.y + 10;
				if (r > sp.y + p.getHeight())
					r = sp.y + p.getHeight() - 10;

				clickableZone = new Rectangle(getWidth() - 7, r - 3, getWidth() - 6, 6);
			} catch (BadLocationException exc) {
			}
			// Remove temporary reference
			currentNode = null;
		}

		// Paint the container.getBookmarkContext()
		if (container.getBookmarkContext() != null) {
			BookmarkModel model = container.getBookmarkContext().getModel();
			for (int i = 0; i < model.getBookmarkCount(); i++) {
				BookmarkPosition position = model.getBookmarkPositionAt(i);
				try {
					if (container.getBookmarkContext().getIcon() != null) {
						Rectangle r = container.getEditor().modelToView(
								position.getOffset());
						container.getBookmarkContext().getIcon().paintIcon(
								this, gc, getWidth() - 20, r.y);
					} else {
						Color c = container.getBookmarkContext().getColor();
						
						int height = container.getEditor().getFontMetrics( container.getEditor().getFont() ).getHeight();
						
						Rectangle r = container.getEditor().modelToView(
								position.getOffset());		
						gc.setColor( c );
						gc.fillRect( r.x, r.y, getWidth(), height );
					}
				} catch (BadLocationException exc) {
				}
			}
		}

		if (container.getEditor().getColorForTagBorderLine() != null)
			gc.setColor(container.getEditor().getColorForTagBorderLine());

		gc.drawLine( getWidth() - 4, y1, getWidth() - 4, y2);
		gc.drawLine( getWidth() - 4, y1, getWidth() - 2, y1);
		gc.drawLine( getWidth() - 4, y2, getWidth() - 2, y2);

		if (clickableZone != null) {
			if (errorMessage != null)
				gc.setColor( ERROR_COLOR );
			else
				gc.setColor( Color.LIGHT_GRAY );
			gc.fillRect(clickableZone.x, clickableZone.y, clickableZone.width,
					clickableZone.height);
		}

		if (listOfErrors != null) {
			for (int i = 0; i < listOfErrors.size(); i += 2) {
				Rectangle rr = ( Rectangle ) listOfErrors.get(i);
				Icon icon = SharedProperties.getBugLittleIcon();
				icon.paintIcon(this, gc, getWidth() - 14, ( rr.y - icon.getIconHeight() / 2 ) + 2 );
			}
		}

		if ( SharedProperties.EDITOR_LINE_NUMBER ) {

			if ( container.getEditor().getParent() instanceof JViewport ) {
				gc.setColor( SharedProperties.LINE_NUMBER_COLOR );
				JViewport jvp = ( JViewport )container.getEditor().getParent();
				Rectangle r = container.getEditor().getVisibleRect();
				FontMetrics fm = container.getEditor().getFontMetrics(
						container.getEditor().getFont() );
				int h = fm.getHeight();
				int posInit = (int)Math.floor( r.y / h ) - 1;
				int posMax = ( ( r.y + r.height ) / h ) + 1;
				int posNl = ( posInit * h ) - 2;
				int width = (int)Math.log10( posMax );
				if ( width != dynamicWidth ) {
					// Force a new preferredSize
					extraWidth = ( fm.stringWidth( "" + posMax ) );
					revalidate();
					dynamicWidth = width;
				}
				for ( int i = posInit; i <= posMax; i++ ) {
					if ( posNl >= y1 && posNl <= y2 ) {
						
						gc.setColor( SharedProperties.LINE_NUMBER_COLOR_SELECTED );
					} else
						gc.setColor( SharedProperties.LINE_NUMBER_COLOR );
					gc.drawString( "" + i, 1, posNl );
					posNl += h;
				}
			}

		}
		
	}

	private int extraWidth = 0;
	private int dynamicWidth = 0;

}
