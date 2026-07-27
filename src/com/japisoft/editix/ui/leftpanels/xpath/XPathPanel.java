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

package com.japisoft.editix.ui.leftpanels.xpath;

import javax.swing.JComponent;

import com.japisoft.editix.ui.leftpanels.AbstractLeftPanel;

public class XPathPanel extends AbstractLeftPanel {

	protected JComponent buildView() {
		return new XPathUI();
	}

	protected String getTitle() {
		return "XPath";
	}

	public void stop() {
	}

	@Override
	protected void preShow() {
			// Run directly XPath expression
			if ( params != null && params.length > 0 ) {
				runXPath( (String)params[0] );
			}
		super.preShow();
	}

	@Override
	public void setParams( Object... params ) {
		super.setParams( params );

		if ( params != null && params.length > 0 )
			runXPath( (String)params[0] );
		
		
	}

	private void runXPath( String xpath ) {
		( ( XPathUI )getView() ).setXPath( xpath, 1 );
		( ( XPathUI )getView() ).runFromRoot();
	}

}
