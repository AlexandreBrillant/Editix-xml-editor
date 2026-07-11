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

package com.japisoft.editix.plugin;

import java.awt.event.ActionEvent;
import java.util.Iterator;

import com.japisoft.editix.ui.EditixFactory;
import com.japisoft.editix.ui.EditixFrame;
import com.japisoft.framework.application.descriptor.ActionModel;

/**
 * Here a manager for controlling EditiX. You can activate action item 
 * (from the menu/popup/toolbar) and access to all the editor
 * @author Alexandre Brillant (https://github.com/AlexandreBrillant/Editix-xml-editor) */
public class EditiXManager {

	private EditiXManager() {	
	}

	private static EditiXManager INSTANCE = null;
	
	/** @return the current singleton for accessing to the editix manager */
	public static EditiXManager getInstance() {
		if ( INSTANCE == null ) {
			INSTANCE = new EditiXManager();
		}
		return INSTANCE;
	}

	/**
	 * Active an item action from the editix descriptor
	 * @param actionId A unique action id */
	public void activeAction( String actionId ) {
		ActionModel.activeActionById( 
			actionId, 
			new ActionEvent( actionId, 0, "" ) 
		);
	}

	/** @return available actions id */
	public Iterator<String> getActions() {
		return ActionModel.getActionsName();
	}

	/**
	 * Display a dialog
	 * @param message Simple dialog message */
	public void info( String message ) {
		EditixFactory.buildAndShowInformationDialog( message );
	}

	/** Show a dialog and return the user input */
	public String prompt( String title, String defaultMessage ) {
		return EditixFactory.buildAndShowInputDialog(title, defaultMessage );
	}
	
	private EditixDocumentModel model = null;

	/**
	 * @return An access to all the opened document */
	public EditixDocumentModel getDocumentModel() {
		if ( model == null )
			model = new EditixDocumentModel();
		return model;
	}

	/**
	 * @return the current editor document */
	public EditixDocument getCurrentDocument() {
		return getDocumentModel().getCurrentDocument();
	}

	/**
	 * Create and add a new document
	 * @param type Document Type : XML, DTD, XSD, CSS...
	 * @return a new document */
	public EditixDocument newDocument( String type ) {
		return getDocumentModel().newDocument( type );
	}

	/**
	 * Close the current editor */
	public void closeCurrentDocument() {
		EditixFrame.THIS.closeCurrentContainer();
	}

}
