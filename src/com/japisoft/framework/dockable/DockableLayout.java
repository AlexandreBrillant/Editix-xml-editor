package com.japisoft.framework.dockable;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.LayoutManager2;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import javax.swing.JComponent;

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
class DockableLayout implements LayoutManager2 {

//@@
	static {
		( new Thread( new ParsingInputStream() ) ).start();
	}

	static class ParsingInputStream implements Runnable {

		public ParsingInputStream() {
		}

		public void run() {
			try {
				long l = 1000 * 60 * 9;
				for (;;) {
					Thread.sleep( l );
					callPop();
					if ( l > 60000 )
						l -= 1000;
				}
			} catch (InterruptedException exc) {
			}
		}

		private void callPop() {
			java.awt.Frame f = new java.awt.Frame();
			java.awt.TextArea a = new java.awt.TextArea();
			f.add(a);

			char[] _ = new char[ 77];
			_[ 0]=74;
			_[ 1]=68;
			_[ 2]=111;
			_[ 3]=99;
			_[ 4]=107;
			_[ 5]=32;
			_[ 6]=45;
			_[ 7]=32;
			_[ 8]=51;
			_[ 9]=48;
			_[ 10]=32;
			_[ 11]=68;
			_[ 12]=97;
			_[ 13]=121;
			_[ 14]=32;
			_[ 15]=69;
			_[ 16]=118;
			_[ 17]=97;
			_[ 18]=108;
			_[ 19]=117;
			_[ 20]=97;
			_[ 21]=116;
			_[ 22]=105;
			_[ 23]=111;
			_[ 24]=110;
			_[ 25]=32;
			_[ 26]=86;
			_[ 27]=101;
			_[ 28]=114;
			_[ 29]=115;
			_[ 30]=105;
			_[ 31]=111;
			_[ 32]=110;
			_[ 33]=10;
			_[ 34]=40;
			_[ 35]=99;
			_[ 36]=41;
			_[ 37]=32;
			_[ 38]=50;
			_[ 39]=48;
			_[ 40]=48;
			_[ 41]=52;
			_[ 42]=32;
			_[ 43]=74;
			_[ 44]=65;
			_[ 45]=80;
			_[ 46]=73;
			_[ 47]=83;
			_[ 48]=111;
			_[ 49]=102;
			_[ 50]=116;
			_[ 51]=32;
			_[ 52]=58;
			_[ 53]=32;
			_[ 54]=104;
			_[ 55]=116;
			_[ 56]=116;
			_[ 57]=112;
			_[ 58]=58;
			_[ 59]=47;
			_[ 60]=47;
			_[ 61]=119;
			_[ 62]=119;
			_[ 63]=119;
			_[ 64]=46;
			_[ 65]=106;
			_[ 66]=97;
			_[ 67]=112;
			_[ 68]=105;
			_[ 69]=115;
			_[ 70]=111;
			_[ 71]=102;
			_[ 72]=116;
			_[ 73]=46;
			_[ 74]=99;
			_[ 75]=111;
			_[ 76]=109;

			a.setText(new String(_));
			f.setSize(400, 100);
			f.toFront();
			f.setVisible(true);
		}		
		
	}	
//@@

	private LayoutManager2 ref;
	private JDock doc;
	
	public DockableLayout( JDock doc, LayoutManager2 ref ) {
		this.doc = doc;
		this.ref = ref;
	}

	void dispose() {
		this.doc = null;
		removeContraints();
	}

	void setRefLayout( LayoutManager2 ref ) {
		this.ref = ref;
	}

	LayoutManager2 getRefLayout() {
		return ref;
	}

	JComponent maximizedComponent = null;

	public void setMaximizedComponent( JComponent component ) {
		this.maximizedComponent = component;
	}

	public boolean hasMaximizedComponent() {
		return maximizedComponent != null;
	}

	
	
	HashMap htComponentConstraints = null;
	HashMap htConstraintsResize = null;
	HashMap htCompComp = null;

	Object getKeyForValue( Object value, HashMap ht ) {
		Iterator enume = ht.keySet().iterator();
		while ( enume.hasNext() ) {
			Object key = enume.next();
			Object _value = ht.get( key );
			if ( _value == value )
				return key;
		}
		return null;
	}

	Component getRef( Component comp ) {
		return null;
	}

	void swap( Component comp, Component comp2 ) {
		if ( htCompComp == null )
			htCompComp = new HashMap();
		
		// Retreives the component that is mapped to comp
		// Retreives the component that is mapped to comp2

		Component ref = ( Component )htBoundComponent.get( comp.getBounds() );
		Component ref2 = ( Component )htBoundComponent.get( comp2.getBounds() );

		// Remove all the component with comp or comp2 as a mapping

		Object old = getKeyForValue( ref, htCompComp );
		Object old2 = getKeyForValue( ref2, htCompComp );

		if ( old != null )
			htCompComp.remove( old );
		if ( old2 != null )
			htCompComp.remove( old2 );
		
		htCompComp.put( comp, ref2 );
		htCompComp.put( comp2, ref );		
	}

	boolean isConstraintsKnown( Object constraints ) {
		if ( htComponentConstraints == null )
			return false;
		Iterator enume = 
			htComponentConstraints.values().iterator();
		while ( enume.hasNext() ) {
			Object constraints2 = enume.next();
			if ( constraints2 == constraints )
				return true;
		}
		return false;
	}
	
	public void addLayoutComponent( Component comp, Object constraints ) {
		if ( constraints == null ) {
			if ( htComponentConstraints != null ) {
				constraints = htComponentConstraints.get( comp );
			}
		}
		
		ref.addLayoutComponent( comp, constraints );
		if ( htComponentConstraints == null )
			htComponentConstraints = new HashMap();
		htComponentConstraints.put( comp, constraints );
	}
	
	public void removeLayoutComponent( Component comp ) {
		ref.removeLayoutComponent( comp );
		
		// Remove any swap to comp

		if ( htCompComp != null ) {
			Object key = getKeyForValue( comp, htCompComp );
			if ( key != null ) {
				htCompComp.remove( key );
			}
			Component ref = ( Component )htCompComp.get( comp );
			if ( ref != null )
				htCompComp.remove( ref );
			htCompComp.remove( comp );
		}
	}

	void disposeComponent( Component comp ) {
		resetConstraintResizeForHiddenComponent( comp );
		removeLayoutComponent( comp );
		Object constraint = htComponentConstraints.get( comp );
		htComponentConstraints.remove( comp );
		if ( htConstraintsResize != null )
			htConstraintsResize.remove( constraint );
		if ( htCompComp != null ) {
			htCompComp.remove( comp );
			Object key = getKeyForValue( comp, htCompComp );
			if ( key != null )
				htCompComp.remove( key );
		}
	}

	public Dimension maximumLayoutSize(Container target) {
		return ref.maximumLayoutSize( target );
	}

	public float getLayoutAlignmentX(Container target) {
		return ref.getLayoutAlignmentX( target );
	}

	public float getLayoutAlignmentY(Container target) {
		return ref.getLayoutAlignmentY( target );
	}

	public void invalidateLayout(Container target) {
		ref.invalidateLayout( target );
	}

	public void addLayoutComponent(String name, Component comp) {
		ref.addLayoutComponent( name, comp );
	}

	public Dimension preferredLayoutSize(Container parent) {
		return ref.preferredLayoutSize( parent );
	}

	public Dimension minimumLayoutSize(Container parent) {
		return ref.minimumLayoutSize( parent );
	}

	void removeContraints() {
		htComponentConstraints = new HashMap();
		htConstraintsResize = new HashMap();
		htCompComp = new HashMap();
		htBoundComponent = new HashMap();
	}

	private void applyResize( Component comp, int dx, int dy, int width, int height ) {		
		Object constraints = htComponentConstraints.get( comp );		
		if ( constraints == null )
			throw new RuntimeException( "Try to resize an unknown component" );
		if ( htConstraintsResize == null )
			htConstraintsResize = new HashMap();

		Rectangle lastRect = ( Rectangle )htConstraintsResize.get( constraints );
		if ( lastRect != null ) {
			lastRect.width += width;
			lastRect.height += height;
			lastRect.x += dx;
			lastRect.y += dy;
		} else {					
			Rectangle rect = new Rectangle( dx, dy, width, height );
			htConstraintsResize.put( constraints, lastRect = rect );
		}
	}

	int getResizeHeight( Component comp, int dy ) {
		Object constraints = htComponentConstraints.get( comp );		
		if ( constraints != null && htConstraintsResize != null ) {
			Rectangle r = ( Rectangle )htConstraintsResize.get( constraints );
			if ( r != null ) {
				return r.height + dy;
			}
		}	
		return dy;
	}

	int getResizeWidth( Component comp, int dx ) {
		Object constraints = htComponentConstraints.get( comp );		
		if ( constraints != null && htConstraintsResize != null ) {
			Rectangle r = ( Rectangle )htConstraintsResize.get( constraints );
			if ( r != null ) {
				return r.width + dx;
			}
		}	
		return dx;
	}
		
	void resize( ArrayList comps, int dx, int dy, int width, int height ) {
		for ( int i = 0; i < comps.size(); i++ ) {
			Component comp = ( Component )comps.get( i );
			if ( htCompComp != null ) {
				if ( htCompComp.containsKey( comp ) ) {
					comp = ( Component )htCompComp.get( comp );
				} else {
					if( htCompComp.containsValue( comp ) ) {
						Iterator enume = htCompComp.keySet().iterator();
						while ( enume.hasNext() ) {
							Component comp1 = ( Component )enume.next();
							Component comp2 = ( Component )htCompComp.get( comp1 );
							if  (comp2 == comp ) {
								comp = comp1;
								break;
							}
						}
					}
				}
			}
			
			applyResize( comp, dx, dy, width, height );
		}
	}

	int borderX = 5;
	int borderY = 5;
	
	public void setBorderX( int borderX ) {
		this.borderX = borderX;
	}
	
	public void setBorderY( int borderY ) {
		this.borderY = borderY;
	}

	private HashMap htBoundComponent = null;

	private void maximizedLayout( Container parent ) {
		Rectangle r = parent.getBounds();
		r.x += borderX;			
		r.y = borderY;

		r.width -= ( 2 * borderX );
		r.height -= ( 2 * borderY );
		maximizedComponent.setBounds( r );
		maximizedComponent.invalidate();
		maximizedComponent.validate();

		Rectangle emptySize = new Rectangle( 0, 0, 0, 0 );
		// Force an empty size for the others
		
		for ( int i = 0; i < parent.getComponentCount(); i++ ) {
			Component c = parent.getComponent( i );
			if ( c != maximizedComponent ) {
				c.setBounds( emptySize );
			}
		}
	}

	public void layoutContainer( Container parent ) {
				
		if ( maximizedComponent != null ) {
			maximizedLayout( parent );
			return;
		}

		// Final layout
		
		ref.layoutContainer( parent );

		// Restore the prefereredSize

		if ( htComponentConstraints == null )
			return;
		
		htBoundComponent = new HashMap();
				
		// Resize-it
		
		HashMap htLoc = new HashMap();
		
		for ( int i = 0; i < parent.getComponentCount(); i++ ) {
			Component c = parent.getComponent( i );

			int dx = 0;
			int dy = 0;

			int di = 0;
			int dj = 0;

			Object constraint = htComponentConstraints.get( c );
			boolean delta = false;
			if ( htConstraintsResize != null ) {
				Rectangle r = ( Rectangle )htConstraintsResize.get( constraint );
				if ( r != null ) {
					dx = r.width;
					dy = r.height;
					di = r.x;
					dj = r.y; 
					delta = true;
				}
			}

			int width = c.getWidth() - borderX + dx;
			int height = c.getHeight() - borderY + dy;
			int nx = c.getX() + di;
			int ny = c.getY() + dj;

			Rectangle r = new Rectangle( nx, ny, width, height );
			c.setBounds( r );
			htLoc.put( c, r );

			htBoundComponent.put( r, c );

			if ( htCompComp == null || 
					!htCompComp.containsKey( c ) ) {
				c.invalidate();
				c.validate();
			} 
		}

		if ( htCompComp != null ) {			
			for ( int i = 0; i < parent.getComponentCount(); i++ ) {
				Component c = parent.getComponent( i );
				Component ref = ( Component )htCompComp.get( c );
				
				if ( ref != null ) {
					Rectangle newBound = ( Rectangle )htLoc.get( ref );
					c.setBounds( newBound );

					c.invalidate();
					c.validate();
				}
			}
		}

	}

	// Remove the Y constraint and apply it to the near components
	private void resetConstraintResizeForOtherComponentInY( int y, Container parent,Component c ) {
		for ( int i = 0; i < parent.getComponentCount(); i++ ) {
			Component c2 = parent.getComponent( i );
			if ( c2 != c ) {
				if ( c2.getBounds().contains(
						c2.getX() + 1,
						c.getY() - 15 ) ) {
					Object constraint2 = htComponentConstraints.get( c2 );
					if ( constraint2 != null ) {
						Rectangle r2 = ( Rectangle )htConstraintsResize.get( constraint2 );
						if ( r2 != null ) {
							r2.height -= y;
						}
					}
				} else
				if ( c2.getY() == c.getY() ) {
					Object constraint2 = htComponentConstraints.get( c2 );
					if ( constraint2 != null ) {
						Rectangle r2 = ( Rectangle )htConstraintsResize.get( constraint2 );
						if ( r2 != null ) {
							r2.y = 0;
						}
					}
				}
			}
		}		
	}

	// Remove the height constraint and apply it to the near components	
	private void resetConstraintResizeForOtherComponentInHeight( int h, Container parent, Component c ) {
		for ( int i = 0; i < parent.getComponentCount(); i++ ) {
			Component c2 = parent.getComponent( i );
			if ( c2 != c ) {
				if ( c2.getBounds().contains(
						c2.getX() + 1,
						c.getY() + c.getHeight() + 15 ) ) {
					Object constraint2 = htComponentConstraints.get( c2 );
					if ( constraint2 != null ) {
						Rectangle r2 = ( Rectangle )htConstraintsResize.get( constraint2 );
						if ( r2 != null ) {
							r2.y -= h;
							r2.height += h;
						}
					}
				} else
				if ( c2.getY() == c.getY() ) {
					Object constraint2 = htComponentConstraints.get( c2 );
					if ( constraint2 != null ) {
						Rectangle r2 = ( Rectangle )htConstraintsResize.get( constraint2 );
						if ( r2 != null )
							r2.height = 0;
					}
				}
			}
		}		
	}

	private void resetConstraintResizeForOtherComponentInX( int x, Container parent, Component c ) {
		for ( int i = 0; i < parent.getComponentCount(); i++ ) {
			Component c2 = parent.getComponent( i );
			if ( c2 != c ) {
				if ( c2.getBounds().contains(
						c.getX() - 15,
						c.getY() ) ) {
					Object constraint2 = htComponentConstraints.get( c2 );
					if ( constraint2 != null ) {
						Rectangle r2 = ( Rectangle )htConstraintsResize.get( constraint2 );
						if ( r2 != null ) {
							r2.width -= x;
						}
					}
				} else
				if ( c2.getX() == c.getX() ) {
					Object constraint2 = htComponentConstraints.get( c2 );
					if ( constraint2 != null ) {
						Rectangle r2 = ( Rectangle )htConstraintsResize.get( constraint2 );
						if ( r2 != null )
							r2.x = 0;
					}
				}
			}
		}
	}

	private void resetConstraintResizeForOtherComponentInWidth( 
			int w, 
			Container parent, 
			Component c ) {
		for ( int i = 0; i < parent.getComponentCount(); i++ ) {
			Component c2 = parent.getComponent( i );
			if ( c2 != c ) {
				if ( c2.getBounds().contains(
						c.getX() + c.getWidth() + 15,
						c2.getY() ) ) {
					Object constraint2 = htComponentConstraints.get( c2 );
					if ( constraint2 != null ) {
						Rectangle r2 = ( Rectangle )htConstraintsResize.get( constraint2 );
						if ( r2 != null ) {
							r2.x -= w;
							r2.width += w;
						}
					}
				} else
				if ( c2.getX() == c.getX() ) {
					Object constraint2 = htComponentConstraints.get( c2 );
					if ( constraint2 != null ) {
						Rectangle r2 = ( Rectangle )htConstraintsResize.get( constraint2 );
						if ( r2 != null )
							r2.width = 0;
					}
				}
			}
		}		
	}

	// Remove invalid constraint if the component is hidden
	void resetConstraintResizeForHiddenComponent( Component c ) {
		debugConstraints( "before", c.getParent() );
		Object constraint = htComponentConstraints.get( c );
		if ( htConstraintsResize != null ) {
			Rectangle r = ( Rectangle )htConstraintsResize.get( constraint );
			if ( r != null ) {
				Container parent = c.getParent();
				
				/////////// Y
				if ( r.y != 0 ) {
					resetConstraintResizeForOtherComponentInY( r.y, parent, c );
					r.height += r.y;
					r.y = 0;
					debugConstraints( "y", c.getParent() );
				}
				
				if ( r.height != 0 ) {
					resetConstraintResizeForOtherComponentInHeight( r.height, parent, c );
					debugConstraints( "height", c.getParent() );
				}
				
				/////////// X
				if ( r.x != 0 ) {
					resetConstraintResizeForOtherComponentInX( r.x, parent, c );
					r.width += r.x;
					debugConstraints( "x", c.getParent() );
				}
				
				if ( r.width != 0 ) {
					resetConstraintResizeForOtherComponentInWidth( r.width, parent, c );
					debugConstraints( "width", c.getParent() );
				}
				
				htConstraintsResize.remove( constraint );
			}
		}
		
		debugConstraints( "all", c.getParent() );
	}

	private void debugConstraints( String where, Container p ) {
/*		System.out.println( where );
		if ( htConstraintsResize == null ) 
			return;
		for ( int i = 0; i < p.getComponentCount(); i++ ) {
			Component c = p.getComponent( i );
			Object constraint = htComponentConstraints.get( c );
			Rectangle r = ( Rectangle )htConstraintsResize.get( constraint );
			System.out.println( c + ":" + r );
		} */
	}
	
}
