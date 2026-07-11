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

import javax.swing.JComponent;

/**
 * Here an interface for each JWizard UI part. The <code>start</code> method is here for
 * initializing the view component and the <code>stop</code> method for getting the view state.
 * @author Alexandre Brillant (https://github.com/AlexandreBrillant/Editix-xml-editor)
 * @version 1.0
 * */
public interface StepView {

	/** Called when the wizard step is started, so the final user can interact with the view provided by the <code>getView</code> method 
	 * @param context Information about the wizard step
	 * */
	public void start( WizardStepContext context );

	/** Called before a new wizard step is started
	 * @param context Information about the wizard step
	 */
	public void stop( WizardStepContext context );

	/** @return the final Step view */
	public JComponent getView();
	
}
