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

package com.japisoft.framework.ui;

import java.awt.BorderLayout;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;

import com.japisoft.framework.ApplicationMain;
import com.japisoft.framework.ui.layout.ButtonLayout;

/**
 * A panel with a gradient title bar
 * @author Alexandre Brillant (https://github.com/AlexandreBrillant/Editix-xml-editor)
 * @version 1.0
 * */
public class GradientPanel extends JPanel {

	static {
		ApplicationMain.class.getName();
	}
	
	
	private GradientLabel lbl = new GradientLabel();

	public GradientPanel( String defaultTitle, JComponent target ) {
		setLayout( new BorderLayout() );
		lbl.setTitle( defaultTitle );
		add( lbl, BorderLayout.NORTH );
		add( target );
	}

	
	/** Add buttons to the gradient label */
	public void addButtons( JButton[] btns ) {
		lbl.setLayout( new ButtonLayout() );
		for ( int i = 0; i < btns.length; i++ )
			lbl.add( btns[ i ] );
	}

	public void updateTitle( String title ) {
		lbl.setTitle( title );
	}
}
