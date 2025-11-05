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

package com.japisoft.editix.wizard.link;

import java.util.List;

import javax.swing.JTabbedPane;

import com.japisoft.editix.wizard.Wizard;
import com.japisoft.editix.wizard.WizardContext;

import com.japisoft.framework.xml.parser.node.FPNode;

public class LinkWizard extends JTabbedPane implements Wizard, LinkWizardModel {

	private LinkPanel innerLinks = null;
	private LinkPanel externalLinks = null;
	
	public LinkWizard() {
		innerLinks = new LinkPanel( true );
		externalLinks = new LinkPanel();
		addTab( "External Links", externalLinks );
		addTab( "Inner Links", innerLinks );		
	}

	private WizardContext context;

	public FPNode getResult() {
		return context.getResult( this );
	}

	public void setContext(WizardContext context) {
		this.context = context;
	}
	
	public List<Link> getExternalLinks() {
		return externalLinks.getLinks();
	}

	public List<Link> getInternalLinks() {
		return innerLinks.getLinks();
	}

}

