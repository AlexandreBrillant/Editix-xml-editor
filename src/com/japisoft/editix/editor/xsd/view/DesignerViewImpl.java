package com.japisoft.editix.editor.xsd.view;

import java.awt.Color;
import java.awt.Container;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.japisoft.editix.editor.xsd.Factory;
import com.japisoft.editix.editor.xsd.toolkit.SchemaHelper;
import com.japisoft.editix.editor.xsd.view.designer.LeftToRightLayout;
import com.japisoft.editix.editor.xsd.view.designer.XSDComponent;
import com.japisoft.editix.editor.xsd.view.designer.XSDComponentFactory;
import com.japisoft.editix.editor.xsd.view.designer.XSDComponentListener;
import com.japisoft.editix.editor.xsd.view.designer.container.XSDContainerComponentImpl;

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
public class DesignerViewImpl 
	extends JComponent 
		implements View,
		XSDComponentListener {
			// MouseListener, XSDComponentListener {

	private Factory factory;

	public DesignerViewImpl( Factory factory ) {
		this.factory = factory;
		setLayout( new LeftToRightLayout() );
	}

	private Element initE;
	private XSDComponent ui;
	private boolean canInit = true;

	public void init( Element schemaNode ) {
				
		// RAZ
		disposeAll();
		
		this.initE = schemaNode;
		ui = XSDComponentFactory.getComponent( schemaNode, this );
		canInit = true;
		
		if ( ui != null ) {					
			add( ui.getView() );
			ui.setOpened( true );
			restoreOpenClose( ui );			
			select( ui );
		} else canInit = false;
		
		repaint();					
	}

	@Override
	public void copy() {
	}
	@Override
	public void cut() {
	}
	@Override
	public void paste() {
	}
	
	public boolean canInit() { return canInit; }
	
	private void refreshContainerState( Container n ) {
		n.invalidate();
		n.validate();
		n.repaint();		
	}

	public void refreshCurrentElementUI() {
		if ( selectedComponent != null ) {
			selectedComponent.updateUIContentCache();
		}
		invalidate();
		validate();
		repaint();
	}

	public void goInto(XSDComponent source) {
		if ( !openReference( source ) ) 
			if ( !openType( source ) ) {
				if ( !source.isOpened() )
					openClose( source, true );
			}
	}

	private void restoreOpenClose( XSDComponent source ) {
		
		if ( source.isOpened() ) {
			source.setOpened( false );
			openClose( source, false );
			// Go deeper
			NodeList nl = source.getElement().getChildNodes();
			for ( int i = 0; i < nl.getLength(); i++ ) {
				if ( nl.item( i ) instanceof Element ) {
					Element e = ( Element )nl.item( i );
					if ( e.getUserData( "ui" ) instanceof XSDComponent ) {
						restoreOpenClose( ( XSDComponent )e.getUserData( "ui" ) );
					}
				}
			}
		}

	}

	public void openClose(XSDComponent source, boolean validateMode ) {
		Container parent = source.getView().getParent();
		if ( source instanceof XSDContainerComponentImpl ) {
			parent = source.getView();
		}
		if ( source.isOpened() ) {
			// Remove the children
			Element e = source.getElement();
			NodeList nl = e.getChildNodes();
			for ( int i = 0 ; i < nl.getLength(); i++ ) {
				Node n = nl.item( i );
				if ( n instanceof Element ) {
					Element e2 = ( Element )n;
					XSDComponent component = ( XSDComponent )e2.getUserData( "ui" );
					if ( component != null ) {
						component.remove(false);
					}
				}
			}
			
			source.setOpened( false );			
			source.getView().invalidate();

		} else {
			
			ArrayList bufferMustOpen = null;
			
			// Add the children
			Element e = source.getElement();
			NodeList nl = e.getChildNodes();
			for ( int i = 0 ; i < nl.getLength(); i++ ) {
				Node n = nl.item( i );
				if ( n instanceof Element ) {
					Element e2 = ( Element )n;
					XSDComponent component = XSDComponentFactory.getComponent( e2, this );
					if ( component != null ) {
						parent.add( component.getView() );
						if ( component.isOpened() ) {
							if ( bufferMustOpen == null )
								bufferMustOpen = new ArrayList();
							bufferMustOpen.add( component );
						}
						component.getView().invalidate();
					}
				}
			}

			if ( bufferMustOpen != null ) {
				for ( int i = 0; i < bufferMustOpen.size(); i++ ) {
					( ( XSDComponent )bufferMustOpen.get( i ) ).setOpened( false );
					openClose( ( XSDComponent )bufferMustOpen.get( i ), validateMode );
				}
			}

			source.setOpened( true );
		}
		
		if ( validateMode ) {
			validate();
			revalidate();
			repaint();
		}
	}
	
	public void openAll( XSDComponent source ) {
		if ( !source.isOpened() ) {
			openClose( source, true );
		}
		// Analysis children
		Element e = source.getElement();
		NodeList nl = e.getChildNodes();
		
		for ( int i = 0;i < nl.getLength(); i++ ) {
			
			if ( nl.item( i ) instanceof Element ) {
				
				Element ee = ( Element )nl.item( i );
				if ( ee.getUserData( "ui" ) instanceof XSDComponent ) {
					
					openAll( ( XSDComponent )ee.getUserData( "ui" ) );

				}
				
			}
			
		}
		
	}

	private XSDComponent selectedComponent;

	public void select( XSDComponent source ) {
		if ( selectedComponent != null ) {
			selectedComponent.setSelected( false );
		}
		selectedComponent = source;
		selectedComponent.setSelected( true );
		if ( designerListener != null )
			designerListener.select( source.getElement() );
	}

	public boolean openReference( XSDComponent source ) {
		Element e = source.getElement();
		return openReference( e );
	}
	
	public boolean openReference( Element e ) {
		Element re = SchemaHelper.getAnyByName( 
				e.getOwnerDocument().getDocumentElement(),
				e.getAttribute( "ref" )
		);
		if ( re != null ) {
			designerListener.openDesigner( re );
			return true;
		}
		return false;
	}

	public boolean openType( XSDComponent source ) {
		Element e = source.getElement();
		return openType( e );
	}

	public boolean openType( Element e ) {
		Element re = SchemaHelper.getComplexTypeByName( 
				e.getOwnerDocument().getDocumentElement(),
				e.getAttribute( "type" )
		);
		if ( re != null ) {
			designerListener.openDesigner( re );
			return true;
		}
		return false;
	}
		
	public boolean open( Element e ) {
		if ( !openReference( e ) ) {
			return openType( e );
		} else
			return true;
	}
	
	public void moveDown( XSDComponent source ) {
		Element e = source.getElement();
		Element p = ( Element )e.getParentNode();
		NodeList nl = p.getChildNodes();
		boolean ok = false;
		boolean next = false;
		for ( int n = 0; n < nl.getLength(); n++ ) {
			if ( nl.item( n ) == e ) {
				next = true;
				continue;
			}
			if ( nl.item( n ) instanceof Element ) {
				if ( next ) {
					if ( ( n + 1 ) < nl.getLength() ) {
						p.removeChild( e );
						p.insertBefore( e, nl.item( n ) );
						ok = true;
						break;
					}
				}					
			}
		}
		// Put it at end
		if ( !ok ) {
			p.removeChild( e );
			p.appendChild( e );
		}
		// Refresh
		XSDComponent c = ( XSDComponent )p.getUserData( "ui" );
		c.setOpened( true );
		openClose( c, true );
		openClose( c, true );
	}

	public void moveUp( XSDComponent source ) {
		Element e = source.getElement();
		Element p = ( Element )e.getParentNode();
		NodeList nl = p.getChildNodes();
		Node pe = null;

		for ( int n = 0; n < nl.getLength(); n++ ) {
			if ( nl.item( n ) == e ) {
				if ( pe != null ) {
					p.removeChild( e );
					p.insertBefore( e, pe );
				}
				break;
			}
			if ( nl.item( n ) instanceof Element ) {
				pe = nl.item( n );
			}
		}

		// Refresh
		refreshContent( p );
	}

	public void refreshContent( Element p ) {
		XSDComponent c = ( XSDComponent )p.getUserData( "ui" );
		if ( c != null ) {
			c.setOpened( true );
			openClose( c, true );
			openClose( c, true );
		}
	}

	// DRAG 'n DROP

	public void moveAfter( XSDComponent source, XSDComponent target ) {
		Element s = source.getElement();
		source.remove( false );
		s.setUserData( "ui", null, null );	
		s.getParentNode().removeChild( s );

		Element t = target.getElement();
		Element p = ( Element )t.getParentNode();

		NodeList nl = p.getChildNodes();
		Element nextElement = null;
		boolean next = false;

		for ( int i = 0; i < nl.getLength(); i++ ) {
			if ( nl.item( i ) instanceof Element ) {
				Element e = ( Element )nl.item( i );
				if ( e == t ) {
					next = true;
					break;
				} else
					if ( next ) {
						nextElement = e;
						break;
					}
			}
		}
		if ( nextElement == null ) {
			p.appendChild( s );
		} else {
			p.insertBefore( s, nextElement );
		}
		
		refreshContent( s );
		refreshContent( p );
		
	}

	public void moveBefore( XSDComponent source, XSDComponent target ) {
		Element s = source.getElement();
		source.remove( false );
		s.setUserData( "ui", null, null );		
		s.getParentNode().removeChild( s );
		
		Element t = target.getElement();
		Element p = ( Element )t.getParentNode();
		
		p.insertBefore( s, t );
		refreshContent( s );
		refreshContent( p );

		
/*		NodeList nl = p.getChildNodes();
		for ( int i = 0; i < nl.getLength(); i++ ) {
			if ( nl.item( i ) instanceof Element ) {
				Element e = ( Element )nl.item( i );
				if ( e == t ) {
					p.insertBefore( , arg1)
					break;
				}
			}
		} */
	}

	public void moveInto( XSDComponent source, XSDComponent target ) {
		Element s = source.getElement();
		source.remove( false );
		s.setUserData( "ui", null, null );		

		Element p = ( Element )s.getParentNode();
		p.removeChild( s );
		Element t = target.getElement();
		t.appendChild( s );
		refreshContent( t );
		refreshContent( p );
	}

	public void addXMLChild(XSDComponent source, String newChild) {
		Element newElement = SchemaHelper.createTag( source.getElement(), newChild );
		
		// Choose a default name for element or attribute
		String n = newElement.getLocalName();
		if ( "element".equals( n ) ) {
			newElement.setAttribute( "name", "myElement" );
		} else
		if ( "attribute".equals( n ) ) {
			newElement.setAttribute( "name", "myAttribute" );
		}

		boolean inserted = false;
		// Keep attributes or keys always at end
		Element parent = source.getElement();
		NodeList nl = parent.getChildNodes();
		for ( int i = 0; i < nl.getLength(); i++ ) {
			if ( nl.item( i ) instanceof Element ) {
				Element e = ( Element )nl.item( i );
				String n2 = e.getLocalName();
				if ( "attribute".equals( n2 ) ||
						"attributeGroup".equals( n2 ) ||
							"anyAttribute".equals( n2 ) ||
								"key".equals( n2 ) ||
									"keyref".equals( n2 ) ||
										"unique".equals( n2 )				
				) {
					parent.insertBefore( newElement, e );
					inserted = true;
					break;
				}
			}
		}

		if ( !inserted )
			source.getElement().appendChild( newElement );

		source.setOpened( true );
		openClose( source, true );	// Close it
		openClose( source, true );	// Open it with all the children
		XSDComponent c = ( XSDComponent )newElement.getUserData( "ui" );
		if ( c != null ) {
			select( c );
		}
	}

	public void deleteXML(XSDComponent source) {
		Element p = ( Element )source.getElement().getParentNode();
		p.removeChild( source.getElement() );
		source.remove(true);
		invalidate();
		validate();
		repaint();
	}

	public void popup(XSDComponent source, int x, int y) {
		ActionPopup p = new ActionPopup( source );
		p.show( source.getView(), x, y );
	}	

	private static Font backFont = new Font( "arial", Font.PLAIN, 10 ); 

	protected void paintComponent(Graphics g) {
		super.paintComponent( g );
		g.setColor( Color.WHITE );
		g.fillRect( 0, 0, getWidth(), getHeight() );
		// Draw lines between elements
		if ( initE != null ) {
			if ( !( initE.getUserData( "ui" ) instanceof XSDContainerComponentImpl ) ) {
				g.setColor( Color.DARK_GRAY );
				drawElementLines( initE, g );
			}
		}		
	}

	public JComponent getView() {
		return this;
	}

	private XSDSelectionListener designerListener = null;

	public void setCommonSelectionListener( XSDSelectionListener listener ) {
		this.designerListener = listener;
	}

	public void dispose() {
		initE = null;
		designerListener = null;
		lastSelectedElement = null;
		selectedComponent = null;
	}
	
	public void stopEditing() {}

	private Element lastSelectedElement;
	
	public void disposeAll() {
		for ( int i = 0; i < getComponentCount(); i++ ) {
			( ( XSDComponent )getComponent( i ) ).dispose();
		}
		removeAll();
	}

	public void mouseEntered(MouseEvent e) {}
	public void mouseExited(MouseEvent e) {}
	public void mousePressed(MouseEvent e) {}
	public void mouseReleased(MouseEvent e) {}

	private static Font cardinalFont = new Font("arial", Font.PLAIN, 8);	

	public static void drawElementLines( Element e, Graphics g ) {
		XSDComponent view = ( XSDComponent )e.getUserData( "ui" );
		if ( view != null ) {
			if ( view instanceof XSDContainerComponentImpl ) {
				drawElementLine( 
						e, 
						new Rectangle( 
								0,
								view.getView().getHeight() / 2,
								0,
								0
							), 
						g,
						true );
			} else {
				drawElementLine( 
						e, 
						view.getView().getBounds(),
						g,
						false );				
			}
		}
	}

	private static void drawElementLine(
			Element child, 
			Rectangle sourceView,
			Graphics g,
			boolean recurse ) {

		NodeList nl = child.getChildNodes();
						
		for ( int i = 0; i < nl.getLength(); i++ ) {
			if ( nl.item( i ) instanceof Element ) {
				Element schild = ( Element )nl.item( i );
				XSDComponent childView = ( XSDComponent )schild.getUserData( "ui" );

				if ( childView != null ) {
					JComponent childViewImpl = childView.getView();
					double x1 = ( sourceView.getWidth() + sourceView.getX() );
					double x2 = ( x1 + childViewImpl.getX() ) / 2;
					double y1 = ( sourceView.getHeight() / 2 ) + sourceView.getY();
					double y3 = childViewImpl.getY() + ( childViewImpl.getHeight() / 2 );
					if ( y3 == ( y1 + 0.5d ) )	// Round problem ??
						y1++;
					
					g.drawLine( (int)x1, (int)y1, (int)x2, (int)y1 );
					g.drawLine( (int)x2, (int)y1, (int)x2, (int)y3 );
					g.drawLine( (int)x2, (int)y3, childViewImpl.getX(), (int)y3 );
	
					drawElementCardinalities( 
							schild,
							childViewImpl, 
							g );

					if ( recurse ) {

						if ( !( childView instanceof XSDContainerComponentImpl ) ) {
						
							drawElementLine( 
								schild,
								childViewImpl.getBounds(),
								g,
								true );
						
						}
					}
				}
			}
		}
	}

	private static void drawElementCardinalities(  
			Element schild, 
			JComponent childViewImpl, 
			Graphics g ) {

		// Draw cardinalities
		if ( schild.hasAttribute( "minOccurs" ) || 
				schild.hasAttribute( "maxOccurs" ) ) {
			int min = 1;
			int max = 1;
			String smax = "" + max;
			try {
				min = Integer.parseInt( 
						schild.getAttribute( "minOccurs" ) );
			} catch( Throwable th ) {}
			String smin = "" + min;								
			if ( "unbounded".equals( 
					schild.getAttribute( "maxOccurs" ) ) ) {
				smax = "oo"; 
			} else {
				try {
					max = Integer.parseInt( 
							schild.getAttribute( "maxOccurs" ) );
					smax = "" + max;
				} catch( Throwable th ) {}
			}
			
			Font fTmp = g.getFont();
			g.setFont( cardinalFont );
			FontMetrics fm = g.getFontMetrics( cardinalFont );
			int width = fm.stringWidth( smin + ".." + smax );
			
			g.drawString(
					smin + ".." + smax,
					childViewImpl.getX() + childViewImpl.getWidth() - width - 15,
					childViewImpl.getY() + childViewImpl.getHeight() + fm.getAscent() + 1
			);
			
			g.setFont( fTmp );		
		}
	}

	/////////////////////////////////////////////////////////////////////////////////
	
	class ActionPopup extends JPopupMenu implements ActionListener {
	 
		private XSDComponent component;
		
		ActionPopup( XSDComponent component ) {
			this.component = component;
			JMenu item1 = new JMenu( "Add child" );
			add( item1 );
			String[] children = SchemaHelper.getChildrenForElement( component.getElement() );
			if ( children != null )
				for ( int i = 0; i < children.length; i++ ) {
					JMenuItem item3 = new JMenuItem( children[ i ] );
					item3.setActionCommand( "add" );
					item3.addActionListener( this );
					item1.add( item3 );
				}
			item1.setEnabled( children != null && children.length > 0 );
			
			Element e = component.getElement();
			if ( e.hasAttribute( "ref" ) ) {

				Element refFound = SchemaHelper.getAnyByName( 
						e.getOwnerDocument().getDocumentElement(),
						e.getAttribute( "ref" ) );

				if ( refFound != null ) {
				
					JMenuItem item3 = new JMenuItem( "Open Reference" );
					item3.setActionCommand( "openref" );
					item3.addActionListener( this );
					add( item3 );
					
				}

			}

			if ( e.hasAttribute( "type" ) ) {

				Element type = SchemaHelper.getComplexTypeByName( 
						e.getOwnerDocument().getDocumentElement(),
						e.getAttribute( "type" ) );
				
				if ( type != null ) {
				
					JMenuItem item3 = new JMenuItem( "Open Type" );
					item3.setActionCommand( "opentype" );
					item3.addActionListener( this );
					add( item3 );
					
				}
								
			}

			JMenuItem item4 = new JMenuItem( "Move up" );
			item4.setActionCommand( "moveup" );
			item4.addActionListener( this );
			add( item4 );
			if ( SchemaHelper.isFirstElement( component.getElement() ) ) {
				item4.setEnabled( false );
			}
			
			JMenuItem item5 = new JMenuItem( "Move down" );
			item5.setActionCommand( "movedown" );
			item5.addActionListener( this );
			add( item5 );
			if ( SchemaHelper.isLastElement( component.getElement() ) ) {
				item5.setEnabled( false );
			}

			JMenuItem item6 = new JMenuItem( "Open All" );
			item6.setActionCommand( "openall" );
			item6.addActionListener( this );
			add( item6 );

			addSeparator();
			
			JMenuItem item2 = new JMenuItem( "Delete" );
			item2.setActionCommand( "delete" );
			item2.addActionListener( this );
			
			add( item2 );
		}

		public void actionPerformed(ActionEvent e) {
			if ( "delete".equals( e.getActionCommand() ) ) {
				deleteXML( component );
			} else
			if ( "add".equals( e.getActionCommand() ) ) {
				addXMLChild( component, ( ( JMenuItem )e.getSource() ).getText() );
			} else
			if ( "moveup".equals( e.getActionCommand() ) ) {
				moveUp( component );
			} else
			if ( "movedown".equals( e.getActionCommand() ) ) {
				moveDown( component );
			} else
			if ( "openref".equals( e.getActionCommand() ) ) {
				openReference( component );
			} else
			if ( "opentype".equals( e.getActionCommand() ) ) {
				openType( component );
			} else
			if ( "openall".equals( e.getActionCommand() ) ) {
				openAll( component );
			}
			component = null;
		}
	}
}
