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

//		int offset2 = container.getEditor().viewToModel( e.getPoint() );
//		int rowNumber = container.getEditor().getDocument().getDefaultRootElement().getElementIndex( offset2 );
//		if ( container.getEditor().getXMLDocument().hasOpeningTag( rowNumber ) ) {
//			container.getEditor().setClosedElement( 
//				rowNumber, 
//				!container.getEditor().isClosedElement( rowNumber ) 
//			);
//			repaint();
//		}

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

	public void notifyError(Object context,boolean localError, String sourceLocation,
			int line, int col, int offset, String message, boolean onTheFly) {
		if ( localError ) {
			if ( line > 0 ) {
				
				if ( !onTheFly )
				
					line--;	// ??
				
				if (listOfErrors == null)
					listOfErrors = new ArrayList();
	
				FontMetrics fm = container.getEditor().getFontMetrics(
						container.getEditor().getFont());
	
				int lineY = (line * fm.getHeight())
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
	public void paintElement( int x, int y ) {
//		mapOpenElement.put( y, Boolean.TRUE );
	}

	// ViewPaintListener
	public void reset( int y ) {
//		mapOpenElement.put( y, Boolean.FALSE );
	}	

	private Font bf = null;
	
	private Font getBoldFont( Graphics gc ) {
		if ( bf == null )
			bf = gc.getFont().deriveFont( Font.BOLD );
		return bf;
	}
	
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
				gc.setColor( Color.red );
			else
				gc.setColor( Color.LIGHT_GRAY );
			gc.fillRect(clickableZone.x, clickableZone.y, clickableZone.width,
					clickableZone.height);
		}

		if (listOfErrors != null) {
			for (int i = 0; i < listOfErrors.size(); i += 2) {
				Rectangle rr = ( Rectangle ) listOfErrors.get(i);
				SharedProperties.getBugLittleIcon().paintIcon(this, gc,
						getWidth() - 6, rr.y);
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
		
/*
		Rectangle r = getVisibleRect();

		for ( int y = 0; y <= r.y + r.height; y++ ) {
			Boolean b = ( Boolean )mapOpenElement.get( y );
			if ( ( b != null ) ) {
				System.out.println( y + ":" + b );
				gc.setColor( b ? Color.GREEN : Color.WHITE );
				if  ( b ) {
					gc.drawRect( getWidth() - 10, y, 5, 5 );
				} else {
					mapOpenElement.remove( y );
					gc.drawRect( getWidth() - 10, y, 5, 5 );
					gc.drawRect( getWidth() - 10, y+1, 5, 5 );
				}
			}
		}
*/

		if ( container.getEditor() != null && 
				container.getEditor().getParent() instanceof JViewport ) {

			XMLEditor editor = container.getEditor();
			JViewport jvp = ( JViewport )editor.getParent();
			Rectangle r = jvp.getViewRect();

			int startOffset = editor.viewToModel( r.getLocation() );
			Point p = r.getLocation();
			p.translate( r.width, r.height );
			int endOffset = editor.viewToModel( p );

//			int startIndex = editor.getDocument().getDefaultRootElement().getElementIndex( startOffset );
//			int endIndex = editor.getDocument().getDefaultRootElement().getElementIndex( endOffset );
//			
//			for ( int i = startIndex; i < endIndex; i++ ) {
//				
//				if ( editor.getXMLDocument().hasOpeningTag( i ) ) {
//
//					// Mark it
//					Element e = editor.getDocument().getDefaultRootElement().getElement( i );
//					startOffset = e.getStartOffset();
//					endOffset = e.getEndOffset();
//					
//					try {
//
//						r = editor.modelToView( startOffset );
//
//						int width = ( int )( r.getHeight() ) - 8;
//						int height = ( int )( r.getHeight() ) - 8;
//						
//						if ( editor.getColorOpenCloseTipBackground() != null ) {
//							
//							gc.setColor( editor.getColorOpenCloseTipBackground() );
//							gc.fillOval( 
//								getWidth() - ( int )r.getHeight(), 
//								r.y + 4,
//								width, 
//								height
//							);
//
//						}
//
//						gc.setColor( editor.getColorOpenCloseTip() );
//						
//						if ( !editor.isClosedElement( i ) ) {
//							gc.setColor( Color.DARK_GRAY );
//						}
//
//						gc.drawOval( 
//							getWidth() - ( int )r.getHeight(), 
//							r.y + 4,
//							width, 
//							height
//						);
//
//						gc.drawLine(
//								getWidth() - ( int )r.getHeight() + 1, 
//								r.y + 4 + ( height / 2 ),
//								getWidth() - ( int )r.getHeight() + height - 2,
//								r.y + 4 + ( height / 2 ) 
//							);
//						
//						if ( editor.isClosedElement( i ) ) {
//							
//							gc.drawLine(
//									getWidth() - ( int )r.getHeight() + width / 2, 
//									r.y + 4 + 1,
//									getWidth() - ( int )r.getHeight() + width / 2,
//									r.y + 4 + height - 2 
//							);
//							
//						}
//						container.getEditor().repaint();
//					} catch( BadLocationException exc ) {
//					}
//				}				
//			}

		}
		
	}

	private int extraWidth = 0;
	private int dynamicWidth = 0;

}
