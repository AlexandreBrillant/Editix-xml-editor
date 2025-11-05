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

package com.japisoft.framework.wizard;

/**
 * Model for storing the ste of wizard step
 * @author Alexandre Brillant (https://github.com/AlexandreBrillant/Editix-xml-editor)
 */
public interface WizardStepModel {

	/** Add a new wizard step */
	public void addWizardStep( WizardStep step );

	/** Insert a wizard step */
	public void insertWizardStep( int index, WizardStep step );

	/** Remove a wizard step */
	public void removeWizardStep( WizardStep step );

	/** @return an index starting from 0 for this wizard step */
	public int getWizardStepIndex( WizardStep step );

	/** @return a wizard by its name */
	public WizardStep getWizardByName( String name );

	/** @return the number of wizard step */
	public int getWizardStepCount();

	/** @return a wizard step at this index from 0 to the maximum size - 1 */
	public WizardStep getWizardStep( int index );

}

