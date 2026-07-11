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

package com.japisoft.framework.dialog;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ActionEvent;

import javax.swing.AbstractButton;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JToggleButton;

import com.japisoft.framework.dialog.actions.DialogAction;
import com.japisoft.framework.dialog.actions.DialogActionModel;
import com.japisoft.framework.dialog.actions.DialogActionModelListener;
import com.japisoft.framework.dialog.actions.DialogActionProxy;
import com.japisoft.framework.dialog.actions.DialogVisibilityObserver;
import com.japisoft.framework.dialog.actions.ToggleActionTransformer;

/**
 * This is a simple footer for showing each action button
 * @author Alexandre Brillant (https://github.com/AlexandreBrillant/Editix-xml-editor)
 * @version 1.0 */
public class BasicDialogFooter extends JPanel implements 
	DialogFooter, 
	DialogActionModelListener {

	private boolean defaultButton;
	
	public BasicDialogFooter() {
		this( true );
	}

	public BasicDialogFooter( boolean defaultButton ) {
		this.defaultButton = defaultButton;
		init();
	}

	private void init() {
		setLayout( new ButtonLayout( 5 ) );
		setBorder( null );
	}

	private DialogActionModel model;

	public void setModel( DialogActionModel model ) {
		this.model = model;
	}

	public void dialogHidden() {
		for ( int i = 0; i < model.getDialogActionCount(); i++ ) {
			DialogAction action = model.getDialogActionAt( i );
			if ( action instanceof DialogVisibilityObserver ) {
				((DialogVisibilityObserver)action).dialogHidden( target, this );
			}
		}
	}

	public void dialogShown() {
		for ( int i = 0; i < model.getDialogActionCount(); i++ ) {
			DialogAction action = model.getDialogActionAt( i );
			if ( action instanceof DialogVisibilityObserver ) {
				((DialogVisibilityObserver)action).dialogShown( target, this );
			}
		}
	}

	public void addNotify() {
		super.addNotify();
		prepareButtons();
		model.setModelListener( this );
	}

	public void removeNotify() {
		super.removeNotify();
		model.setModelListener( null );
	}
	
	public JComponent getView() {
		return this;
	}

	public void modelUpdated() {
		prepareButtons();
	}

	private void prepareButtons() {
		removeAll();
		DialogActionProxy action = null;
		if ( model == null )
			throw new RuntimeException( 
				"Illegal DialogFooter usage, no model found" );
		for ( int i = 0; i < model.getDialogActionCount(); i++ ) {
			if ( model.getDialogActionAt( i ).isForDialogFooter() ) {
				AbstractButton button = ( AbstractButton )add( 
						getButtonForAction( 
								action = new DialogActionProxy(
										model.getDialogActionAt( i ),
										target ), model.getDialogActionAt( i ).isSpecial() ) );
				
				button.setBorderPainted( false );
				
				if ( action.getActionId() == DialogManager.getDefaultAction() ) {
					if ( button instanceof JButton && defaultButton )
						getRootPane().setDefaultButton( ( JButton )button );
				}
			}
		}
		invalidate();
		validate();
	}

	private AbstractButton getButtonForAction( DialogActionProxy a, boolean left ) {
				
		if ( a.delegate instanceof ToggleActionTransformer ) {
			if ( left )
				return new CustomToggleButton( a );
			else
				return new JToggleButton( a );
		} else {
			if ( left )
				return new CustomButton( a );
			else
				return new JButton( a );
		}
	}

	private DialogComponent target = null;
	
	public void setDialogTarget( DialogComponent dialog ) {
		this.target = dialog;
	}

	/*
	public void paintComponent( Graphics gc ) {
		super.paintComponent( gc );
		gc.setColor( Color.GRAY );
		gc.drawLine( 0, 1, getWidth(), 1 );
		gc.setColor( Color.WHITE );
		gc.drawLine( 0, 2, getWidth(), 2 );
	}
	*/
		
	/**
	 * @param actionId a dialog action
	 * @return <code>true</code> if the bound button is selected */
	public boolean isDialogActionSelected( int actionId ) {
		DialogAction action = model.getDialogActionForId( actionId );
		if ( action == null )
			return false;
		for ( int i = 0; i < getComponentCount(); i++ ) {
			if ( getComponent( i ) instanceof AbstractButton ) {
				AbstractButton b = ( AbstractButton )getComponent( i );
				if ( b.getAction() instanceof DialogAction ) {
					DialogAction da = ( DialogAction )b.getAction();
					if ( da.getActionId() == actionId )
						return b.isSelected();
				}
			}
		}
		return false;
	}

	public void setEnabled( int actionId, boolean enabled ) {
		DialogAction a = model.getDialogActionForId( actionId );
		if ( a == null )
			throw new RuntimeException( "Unkown action " + actionId );
		a.setEnabled( enabled );
	}	

	public void dispose() {
		model = null;
		target = null;
	}

	public void invokeAction( int actionId ) {
		DialogAction a = model.getDialogActionForId( actionId );
		if ( a == null )
			throw new RuntimeException( "Unkown action " + actionId );
		a.actionPerformed( new ActionEvent( target, 0, null ) );
	}	
	
	//////////////////////////////////////////////////////////////////

	class CustomButton extends JButton implements LeftOrientedButton {
		public CustomButton( Action a ) {
			super( a );
		}
	}

	class CustomToggleButton extends JToggleButton implements LeftOrientedButton {
		public CustomToggleButton( Action a ) {
			super( a );
		}
	}

}
