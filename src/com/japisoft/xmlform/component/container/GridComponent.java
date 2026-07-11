// Editix XML Editor
// https://www.editix.com
// Copyright (c) 2026 Alexandre Brillant
//
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
// AI Training Restriction :
// This source code is provided for human use only.
// Using this code to train, fine-tune, or develop AI models,
// machine learning systems, or similar technologies is
// STRICTLY PROHIBITED. Violations will terminate all rights
// under the applicable license.

package com.japisoft.xmlform.component.container;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.IOException;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.TransferHandler;

import com.japisoft.editix.ui.EditixFactory;
import com.japisoft.framework.preferences.Preferences;
import com.japisoft.framework.xml.XPathToolkit;
import com.japisoft.xmlform.component.AbstractXMLFormComponent;
import com.japisoft.xmlform.component.ComponentContext;
import com.japisoft.xmlform.component.XMLFormComponent;
import com.japisoft.xmlform.designer.data.GrammarNodeTreeNode;

public class GridComponent extends JPanel 
		implements 
			MouseListener, 
			MouseMotionListener,
			XMLFormComponent {

	private int gridSize = 0;	
	private boolean designMode = false;
	private ComponentContext context = null;

	GridComponent( boolean designMode, ComponentContext context ) {
		this.designMode = designMode;
		this.context = context;
		
		if ( context == null ) {
			EditixFactory.buildAndShowErrorDialog( "Invalid context, choose a schema first" );
			return;
		}
		
		setLayout( null );		
		if ( designMode ) {
			gridSize = getGridSize();
			setTransferHandler( new DragDrop() );
		}
	}

	public static int getGridSize() {
		return Math.max( 5, Preferences.getPreference( 
				"designer", 
				"grid-size", 
				10 ) );
	}

	public String getXPath() {
		if ( getParent() instanceof AbstractXMLFormComponent ) {
			AbstractXMLFormComponent axfc = 
				( AbstractXMLFormComponent )getParent();
			GrammarNodeTreeNode gntn = axfc.getGrammarNode();
			if ( gntn != null ) {
				return gntn.toXPath();
			}
		}
		return null;
	}

	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		if ( designMode ) {
			g.setColor( Color.GRAY );
			for ( int i = 0; i < getWidth(); i += gridSize ) {
				for ( int j = 0; j < getHeight(); j+= gridSize ) {
					g.drawRect( i, j, 1, 1 );
				}
			}
		}
	}

	@Override
	public void addNotify() {
		super.addNotify();
		addMouseMotionListener( this );
		addMouseListener( this );
	}
	
	@Override
	public void removeNotify() {
		super.removeNotify();
		removeMouseMotionListener( this );
		removeMouseListener( this );
	}	
	
	@Override
	public void setBackground(Color bg) {
		super.setBackground(bg);
		if ( designMode ) {
			for ( int i = 0; i < getComponentCount(); i++ ) {
				Component c = getComponent( i );
				if ( c instanceof XMLFormContainer ) {
					( ( XMLFormContainer )c ).setBackground( bg );
				}
			}
		}
	}

	public void mouseDragged(MouseEvent e) {
	}

	public void mouseClicked(MouseEvent e) {
	}

	public void mouseEntered(MouseEvent e) {
	}

	public void mouseExited(MouseEvent e) {
	}

	public void mousePressed(MouseEvent e) {
	}

	public void mouseReleased(MouseEvent e) {
	}

	public void mouseMoved(MouseEvent e) {
		if ( dragXPath != null ) {
			
			try {
			
				AbstractXMLFormComponent comp = null;
				
				if ( dragXPath.startsWith( "new" ) ) {
					comp = 
						context.getComponentFactory().newComponentFromDescriptor(
								dragXPath.substring( 4 ) );
				} else {
					
					String newXPath = context.getCurrentTreeNode().toXPath();
					String xpath = getXPath();
					if ( xpath != null ) {
						if ( !XPathToolkit.isXPathChild( xpath, newXPath ) ) {

							context.action( ComponentContext.WARNING_MESSAGE, "You can't drop it here. It must be a parent node" );

							return;
							
						}
					}
					
					comp =
						context.getComponentFactory().newComponentFromTreeNode( 
								context.getCurrentTreeNode()
						);
				}
	
				if ( comp != null && context.getCurrentTreeNode() != null ) {
					comp.setGrammarNode( context.getCurrentTreeNode() );
					Dimension dim = comp.getPreferredSize();
					comp.setBounds(
							e.getX(), 
							e.getY(), 
							( int )dim.getWidth(), 
							( int )dim.getHeight() );
		
					add( comp );
		
					invalidate();
					validate();
					repaint();			
				}
			
			} finally {
			
				dragXPath = null;
			
			}
		}
	}

	@Override
	public Component add(Component comp) {
		
		if ( designMode ) {
			resizeToGrid( comp );
			context.action( 
					ComponentContext.SELECT_ACTION, 
					( AbstractXMLFormComponent )comp );
		}

		return super.add( comp );
		
	}

	public void resizeToGrid( Component comp ) {
		// Align to the grid
		Rectangle r = comp.getBounds();
		r.x = ( r.x / gridSize ) * gridSize;
		r.y = ( r.y / gridSize ) * gridSize;
		r.width = ( r.width / gridSize ) * gridSize;

		if ( r.height <= gridSize )
			r.height = gridSize;
		if ( r.width <= gridSize )
			r.width = gridSize;

		comp.setBounds( r );
		comp.invalidate();
		comp.validate();
	}

	///////////////////////////////////////////////////////////////
	
	String dragXPath = null;
	
	class DragDrop extends TransferHandler {
		
		@Override
		public boolean canImport(
				JComponent comp, 
				DataFlavor[] transferFlavors ) {
			
			return true;		
		}

		@Override
		public boolean importData( JComponent comp, Transferable t ) {
			try {
				dragXPath = ( String )t.getTransferData( 
						DataFlavor.stringFlavor );
				return ( dragXPath.startsWith( "xpath:" ) || 
						dragXPath.startsWith( "new:" ) );
			} catch (UnsupportedFlavorException e) {
			} catch (IOException e) {
			}
			return false;
		}

		@Override
		public int getSourceActions(JComponent c) {
			return TransferHandler.MOVE;
		}

	}

}
