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

package com.japisoft.xmlpad.error;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * Very simple view with one label containg the last notified error
 * @author Alexandre Brillant (https://github.com/AlexandreBrillant/Editix-xml-editor)
 * @version 1.0
 * */
public class DefaultErrorView extends JPanel implements ErrorView {

	private JLabel lbl = new JLabel( "?" );

	public DefaultErrorView() {
		super();
		setLayout( new BorderLayout() );
		add( lbl );
		lbl.setForeground( Color.red );
		lbl.setFont( new Font( null, 0, 12 ) );
	}

	void setErrorMessage( String error ) {
		lbl.setText( error );
		lbl.setToolTipText( error );
	}	

	public JComponent getView() {
		return this;
	}

	/** Don't show by default */
	public boolean isShownForOnTheFly() {
		return false;
	}	

	/** Not managed */
	public void addErrorSelectionListener( ErrorSelectionListener listener ) {
	}

	/** Not managed */
	public void removeErrorSelectionListener( ErrorSelectionListener listener ) {
	}

	/** Not managed */
	public void initErrorProcessing() {}

	/** Not managed */
	public void stopErrorProcessing() {
	}	
	
	public void notifyError(
			Object context,
			boolean localError,
			String sourceLocation, 
			int line, 
			int column,
			int offset,
			String message,
			boolean onTheFly ) {
		if ( !onTheFly )
			setErrorMessage( message );
	}

	/** Not managed */
	public void notifyNoError( boolean onTheFly ) {
	}
	
	public void dispose() {
	}
	
	public void initOnceAdded() {
	}	
}
