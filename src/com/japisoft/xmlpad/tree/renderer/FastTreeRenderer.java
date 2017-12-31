package com.japisoft.xmlpad.tree.renderer;

import java.awt.*;

import javax.swing.*;
import javax.swing.tree.*;

import com.japisoft.framework.ui.ImageIconProxy;
import com.japisoft.framework.xml.parser.node.FPNode;
import com.japisoft.xmlpad.Debug;
import com.japisoft.xmlpad.SharedProperties;
import com.japisoft.xmlpad.XMLContainer;

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
public class FastTreeRenderer implements TreeCellRenderer {

	private TreeLabel tl;
	
	private ImageIcon i0;
	private ImageIconProxy i1;
	private ImageIconProxy i2;

	/** This is the default rendering mode */
	public static final int DEFAULT_MODE = 0;

	/** This is a rendering mode for showing the node prefix */
	public static final int PREFIX_MODE = 1;

	/** This is a rendering mode for showing the node namespace */
	public static final int NAMESPACE_MODE = 2;

	/** This is a rendering mode for showing the qualified node name */
	public static final int QUALIFIED_MODE = 3;

	private int mode = DEFAULT_MODE;

	private XMLContainer container = null;

	public FastTreeRenderer(XMLContainer container) {
		super();
		this.container = container;
		tl = new TreeLabel();
		try {

			i0 = SharedProperties.getBugLittleIcon();
			i1 = new ImageIconProxy( 
					new ImageIcon(
						getClass().getResource( "element.png" ) ), 1, 1, i0 );
			i2 = new ImageIconProxy( 
					new ImageIcon(
						getClass().getResource( "text.png" ) ) );

		} catch ( Throwable th ) {
			Debug.debug( th );
		}

		//JPF get the colors from the default look and feel
		defaultTextColor = UIManager.getColor("Tree.textForeground");
		defaultBackgroundSelectionColor = UIManager
				.getColor("Tree.selectionBackground");
		defaultBackgroundColor = UIManager.getColor("Tree.textBackground");
		defaultSelectionColor = UIManager.getColor("Tree.selectionForeground");
		
		//JPF

		initUI();
		
		currentRenderer = 
			new DefaultRenderer( 
				i1, 
				i2, 
				tl 
			);
	}

	public NodeRenderer getNodeRenderer() {
		return currentRenderer;
	}

	public void setAttribute( 
			String nodeName, 
			String attributeName ) {
		currentRenderer.setAttribute( 
			nodeName, 
			attributeName );
	}

	/** @return the component used for the rendering */
	public JComponent getInnerComponent() { return tl; } 
	
	/** Update the rendering mode :<code>DEFAULT_MODE</code> ... */
	public void setRenderingMode(int mode) {
		this.mode = mode;
		switch (mode) {
		case DEFAULT_MODE:
			currentRenderer = new DefaultRenderer( i1, i2, tl );
			break;
		case PREFIX_MODE:
			currentRenderer = new PrefixRenderer( i1, i2, tl );
			break;
		case NAMESPACE_MODE:
			currentRenderer = new NamespaceRenderer( i1, i2, tl );
			break;
		case QUALIFIED_MODE:
			currentRenderer = new QualifiedRenderer( i1, i2, tl );
			break;
		}
	}

	private void initUI() {
		String p = "xmlpad.tree.";
		Font f = UIManager.getFont(p + "font");
		if (f != null) {
			setTextFont(f);
		}
		Icon i = UIManager.getIcon(p + "elementIcon");
		if (i != null && i instanceof ImageIcon)
			setElementIcon((ImageIcon) i);
		i = UIManager.getIcon(p + "textIcon");
		if (i != null && i instanceof ImageIcon)
			setTextIcon((ImageIcon) i);
		Color c = UIManager.getColor(p + "textColor");
		if (c != null) {
			setTextColor(c);
			defaultSelectionColor = c.brighter().brighter();
		}
		c = UIManager.getColor(p + "selectionColor");
		if (c != null) {
			setSelectionColor(c);
			setDashUnderlineColor(c);
		}
	}

	Color defaultBackgroundSelectionColor;

	Color defaultBackgroundColor;

	/** Reset the default icon for tag element */
	public void setElementIcon(ImageIcon icon) {
		if ( icon != null )
			this.i1 = new ImageIconProxy( icon, 1, 1, i0 );
		else
			i1 = null;
	}

	/** Reset the default text icon */
	public void setTextIcon(ImageIcon icon) {
		if ( icon != null )
			this.i2 = new ImageIconProxy( icon );
		else
			i2 = null;
	}

	private Font defaultFont = new Font( 
			null, 
			0, 
			12 );

	/** Reset the default text font */
	public void setTextFont(Font font) {
		this.defaultFont = font;
		tl.setFont(font);
	}

	private Color defaultTextColor = Color.BLACK;
	private Color defaultTextAttributeColor = Color.GRAY;

	/** Reset the default text color */
	public void setTextColor(Color color) {
		this.defaultTextColor = color;
	}

	private Color defaultSelectionColor = Color.GRAY;

	/** Reset the default selection color */
	public void setSelectionColor(Color color) {
		this.defaultSelectionColor = color;
	}

	private String errorMessage;

	/**
	 * Show an error on the root. If the message is null the error is removed
	 */
	public void activateError(String message) {
		this.errorMessage = message;
		tl.setToolTipText(message);
	}

	private boolean dashUnderline = false;

	/** Add dask for showing selection */
	public void setDashUnderlineMode(boolean dashUnderline) {
		this.dashUnderline = dashUnderline;
	}

	/**
	 * @return <code>true</code> if the current selection is shown with an
	 *         underline mode. By default to <code>false</code>
	 */
	public boolean isDashUnderlineMode() {
		return dashUnderline;
	}

	Color dashUnderlineColor = Color.BLACK;

	public void setDashUnderlineColor(Color color) {
		this.dashUnderlineColor = color;
	}

	private FPNode sn;
	
	public Component getTreeCellRendererComponent(JTree tree, Object value,
			boolean selected, boolean expanded, boolean leaf, int row,
			boolean hasFocus) {
		tl.setColor( defaultTextColor );
		if ( value instanceof FPNode ) {

			sn = ( FPNode ) value;
			if ( i1 != null )
				i1.activeOverringImage = ( sn.errorMessage != null );

			String prefix = sn.getNameSpacePrefix();
						
			if ( prefix != null && container != null ) {
				Color c = ( Color ) container.getEditor().getColorForPrefix(
						prefix);
				if (c != null)
					tl.setColor( c );
			}
			
			if ( container != null ) {
				if ( container.getEditor().getColorForTag( sn.getContent() ) != null ) {
					tl.setColor( container.getEditor().getColorForTag( sn.getContent() ) );
				}
			}
			
			currentRenderer.prepare( sn, selected );
		} else
			tl.setContent( 
					value.toString(), 
					null, 
					false, 
					i2 );
		return tl;
	}

	private NodeRenderer currentRenderer = null;

	public void dispose() {
		container = null;
	}

	/////////////////////////////////////////

	/** Lightweight label component */
	final class TreeLabel extends JComponent {
		private static final String SIMPLE_WP = " ";
		private static final String ETC = "...";
		private String content;

		private String sub_content;

		private boolean selected;

		private Dimension d = new Dimension(100, 20);

		//JPF
		private int textY; //JPF

		private int fmHeight;

		FontMetrics fm;

		//JPF
		private Color fg = null;

		public TreeLabel() {
			super();
			setFont(defaultFont);
		}

		public void setColor(Color fg) {
			this.fg = fg;
		}

		public void setFont(Font font) {
			super.setFont(font);
			fm = getFontMetrics(defaultFont);

			fmHeight = fm.getHeight() + 2;
			textY = fm.getAscent();
		}

		public Dimension getPreferredSize() {
			return d;
		}

		// For optimizing rendering
		public boolean isDoubleBuffered() {
			return false;
		}
		// For optimizing rendering		
		public void invalidate() {
		}
		// For optimizing rendering		
		public void validate() {
		}
		// For optimizing rendering		
		public void repaint() {
		}

		private ImageIcon ii = null;

		private int deltaSubcontent = 0;
		
		public void setContent(
				String content, 
				String sub_content,
				boolean selected, 
				ImageIcon ii ) {
			
			if ( content == null )
				content = "?";
			
			this.content = content;			
			this.sub_content = sub_content;
			this.selected = selected;
			this.ii = ii;
			int w = 0;

			w = fm.stringWidth( content );
			if ( sub_content != null ) {				
				if ( sub_content.length() > SharedProperties.VISIBLE_TREENODE_TEXTE )
					this.sub_content = sub_content.substring( 0, SharedProperties.VISIBLE_TREENODE_TEXTE ) + ETC;
				w += fm.stringWidth( SIMPLE_WP );
				deltaSubcontent = w;
				w += fm.stringWidth( this.sub_content );
			}
			if ( ii != null ) {
				w = ( w + 2 + ii.getIconWidth() );
			}
			if ( selected )
				w += 6;
			d = new Dimension( w + 2, fmHeight + 2 );
		}

		public void paintComponent( Graphics gc ) {
			//super.paintComponent( gc );
			//JPF
			if (selected) {
				if (!isDashUnderlineMode()) {
					gc.setColor(defaultBackgroundSelectionColor);
					gc.fillRect(0, 0, d.width, d.height);
					gc.setColor(defaultSelectionColor);
				} else {
					gc.setColor( dashUnderlineColor );
					gc.fillRect( 
						1, 
						1, 
						(int)d.getWidth() - 2, 
						(int)d.getHeight() - 2
					);
					gc.setColor(fg);
				}
			} else {
				gc.setColor(fg);
			}
			
			Graphics2D g2 = ( Graphics2D )gc;

			int i = 1;
			if ( ii != null ) {
				ii.paintIcon( this, gc, 0, 0 );
				i = i + ( ii.getIconWidth() + 2 );
			}
			
			g2.setRenderingHint( 
					RenderingHints.KEY_TEXT_ANTIALIASING, 
					RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB 
			);					
			
			gc.drawString(content, i, textY);
			if (sub_content != null) {
				gc.setColor(defaultTextAttributeColor);
				gc.drawString(sub_content, i + deltaSubcontent, textY);
			}
		}
		
	}
	
}

// DefaultTreeRenderer ends here
