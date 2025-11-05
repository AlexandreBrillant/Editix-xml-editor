// Editix XML Editor
// https://www.editix.com
// Copyright (c) 2025 Alexandre Brillant
// 
// For non-commercial usage :
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
// For commercial use or integration into proprietary software :
// A commercial license is required. Visit https://www.editix.com for details.

package com.japisoft.editix.ui.panels.xpath;

import javax.swing.JComponent;

import com.japisoft.editix.ui.panels.AbstractPanel;

public class XPathPanel extends AbstractPanel {

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
			if ( params != null ) {
				runXPath( params );
			}
		super.preShow();
	}

	@Override
	public void setParams( String params ) {
		super.setParams( params );
		
		if ( params != null && !"".equals( params ) )
			runXPath( params );
		
		
	}

	private void runXPath( String xpath ) {
		( ( XPathUI )getView() ).setXPath( xpath, 1 );
		( ( XPathUI )getView() ).runFromRoot();
	}

}

