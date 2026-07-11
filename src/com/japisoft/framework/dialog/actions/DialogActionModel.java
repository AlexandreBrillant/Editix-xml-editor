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

package com.japisoft.framework.dialog.actions;

import java.util.ArrayList;

/**
 * This is a model containing available actions
 * @author Alexandre Brillant (https://github.com/AlexandreBrillant/Editix-xml-editor)
 * @version 1.0 */
public class DialogActionModel {

	private ArrayList list;
	private DialogActionModelListener listener;

	public static DialogAction DEFAULT_OKACTION = new OKAction();
	public static DialogAction DEFAULT_CANCELACTION = new CancelAction();

	/** This is a 'OK', 'Cancel' dialog model */
	public static DialogActionModel getDefaultDialogActionModel() {
		return new DialogActionModel(
				new DialogAction[] {
						DEFAULT_CANCELACTION,
						DEFAULT_OKACTION,
				} );
	}

	/** This is an 'OK' dialog model */
	public static DialogActionModel getDefaultDialogOkActionModel() {
		return new DialogActionModel(
				new DialogAction[] {
						DEFAULT_OKACTION,
				} );
	}

	/** This is an 'Close' dialog model */
	public static DialogActionModel getDefaultDialogCloseActionModel() {
		return new DialogActionModel(
				new DialogAction[] {
						new ClosingAction(),
				} );
	}
		
	/** Empty model */
	public DialogActionModel() {}

	/** Unique action model */
	public DialogActionModel( DialogAction action ) {
		addDialogAction( action );
	}
	
	/** Multiple actions model */
	public DialogActionModel( DialogAction[] actions ) {
		if ( actions != null )
			for ( int i = 0; i < actions.length; i++ ) {
				addDialogAction( actions[ i ] );
			}
	}

	public Object clone() {
		DialogActionModel model = new DialogActionModel();
		model.list = new ArrayList();
		if ( list != null )
			for ( int i = 0; i < list.size(); i++ )
				model.list.add( list.get( i ) );
		return model;
	}
	
	/** Listener for each change inside this model */
	public void setModelListener( DialogActionModelListener listener ) {
		this.listener = listener;
	}

	/** @return a dialog action for this id */
	public DialogAction getDialogActionForId( int id ) {
		for ( int i = 0; i < getDialogActionCount(); i++ ) {
			DialogAction action = getDialogActionAt( i );
			if ( action.getActionId() == id )
				return action;
		}
		return null;
	}
	
	/** Check for the following action */
	public boolean hasDialogActionForId( int id ) {
		return ( getDialogActionForId( id ) != null );
	}

	/** 
	 * Add a new dialog action */
	public DialogActionModel addDialogAction( DialogAction action ) {
		if ( list == null )
			list = new ArrayList();
		if ( list.contains( action ) )
			throw new RuntimeException( "Error : You have added several times the same action object" );
		checkForUniqueAction( action );
		list.add( action );
		fireModelUpdated();
		return this;
	}

	private void checkForUniqueAction( DialogAction action ) {
		if ( list != null ) {
			for ( int i = 0;i < list.size(); i++ ) {
				if ( 
						( ( DialogAction )list.get( i ) ).getActionId() ==
								action.getActionId() ) {
					throw new RuntimeException( "Error : Found two actions with the same id " + action.getActionId() );
				}
			}
		}
	}
	
	/**
	 * Add a new dialog action at this index
	 * @param index Value starting from 0
	 * @param action A swing action
	 */
	public void addDialogAction( int index, DialogAction action ) {
		if ( list == null )
			list = new ArrayList();
		checkForUniqueAction( action );		
		list.add( index, action );
		fireModelUpdated();
	}

	/**
	 * Remove this action
	 */
	public void removeDialogAction( DialogAction action ) {
		if ( list != null ) {
			list.remove( action );
			fireModelUpdated();
		}
	}
		
	private void fireModelUpdated() {
		if ( listener != null )
			listener.modelUpdated();
	}
	
	/**
	 * @return The number of dialog action
	 */
	public int getDialogActionCount() { 
		if ( list == null )
			return 0;
		return list.size();
	}
	
	/**
	 * @param index value starting from 0
	 * @return a dialog action at this index
	 */
	public DialogAction getDialogActionAt( int index ) {
		if ( list == null )
			return null;
		return ( DialogAction )list.get( index );
	}

}
