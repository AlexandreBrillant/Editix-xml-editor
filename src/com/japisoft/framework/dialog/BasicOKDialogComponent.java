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

import java.awt.Dialog;
import java.awt.Frame;

import javax.swing.Icon;
import javax.swing.JComponent;

import com.japisoft.framework.dialog.actions.DialogActionModel;
import com.japisoft.framework.dialog.actions.OKAction;

/**
 * @author Alexandre Brillant (https://github.com/AlexandreBrillant/Editix-xml-editor)
 * @version 1.0
 * */
public class BasicOKDialogComponent extends BasicDialogComponent {

	public BasicOKDialogComponent(
			Dialog owner,
			String dialogTitle,
			String title, 
			String comment, 
			Icon icon ) {
		super( owner, dialogTitle, true );
		init( title, comment, icon );
	}

	public BasicOKDialogComponent(
			Frame owner,
			String dialogTitle,
			String title, 
			String comment, 
			Icon icon ) {
		super( owner, dialogTitle, true );
		init( title, comment, icon );
	}

	DialogHeader header;
	DialogFooter footer;

	/** Defining the dialog actions */
	protected DialogActionModel prepareActionModel() {
		DialogActionModel actionModel = new DialogActionModel();
		actionModel.addDialogAction( new OKAction() );
		return actionModel;
	}

	private void init( String title, String comment, Icon icon ) {
		header = DialogManager.getDefaultDialogHeader();
		header.setComment( comment );
		header.setTitle( title );
		if  ( icon == null )
			icon = DialogManager.getDefaultDialogIcon();
		header.setIcon( icon );

		footer = DialogManager.getDefaultDialogFooter( true );
		DialogActionModel model = prepareActionModel();
		footer.setDialogTarget( this );
		footer.setModel( model );
	}

	private boolean first = true;
	
	public void addNotify() {
		super.addNotify();
		if ( !setUICalled && !first )
			throw new RuntimeException( "Invalid dialog usage, you must call setUI !" );
		first = false;
	}

	private boolean setUICalled = false;

	public JComponent setUI( JComponent pane ) {
		init( header, pane, footer );
		setUICalled = true;
		return pane;
	}
	
	public JComponent setUI( JComponent pane, boolean enabledHeader, boolean enabledFooter ) {
		init(
				enabledHeader ? header : null,
				pane,
				enabledFooter ? footer : null );
		setUICalled = true;
		return pane;
	}
	
}
