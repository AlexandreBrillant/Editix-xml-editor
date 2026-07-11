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

package com.japisoft.framework.wizard;

import java.util.ArrayList;

/**
 * Simple model for the set of wizardstep
 * @author Alexandre Brillant (https://github.com/AlexandreBrillant/Editix-xml-editor)
 * @see WizardStep
 */
public class DefaultWizardStepModel implements WizardStepModel {

	private ArrayList content;
	private JWizard wizard;

	public int getWizardStepIndex(WizardStep step) {
		if ( content == null )
			return -1;
		return content.indexOf( step );
	}

	public DefaultWizardStepModel( JWizard wizard ) {
		this.wizard = wizard;
	}

	public void addWizardStep(WizardStep step) {
		if ( content == null )
			content = new ArrayList();
		content.add( step );
	}

	public void insertWizardStep(int index, WizardStep step) {
		if ( content == null )
			content = new ArrayList();
		content.add( index, step );
	}

	public void removeWizardStep(WizardStep step) {
		if ( content != null ) {
			content.remove( step );
		}
	}

	public WizardStep getWizardByName(String name) {
		if ( name == null )
			throw new RuntimeException( "Illegal null name" );
		if ( content != null ) {
			for ( int i = 0; i < content.size(); i++ ) {
				WizardStep ws = ( WizardStep )content.get( i );
				if ( name.equals( ws.getName() ) )
					return ws;
			}
		}
		return null;
	}

	public WizardStep getWizardStep(int index) {
		if ( content == null )
			return null;
		return ( WizardStep )content.get( index );
	}

	public int getWizardStepCount() {
		if ( content == null )
			return 0;
		return content.size();
	}

}
