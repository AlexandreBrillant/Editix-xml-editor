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

package com.japisoft.framework.job;

/**
 * This element is consummed by the JobManager
 * @author Alexandre Brillant (https://github.com/AlexandreBrillant/Editix-xml-editor)
 * @version 1.0
 * @see JobManager
 */
public interface Job extends Runnable {

	/** @return true if the job must be alone. Meaning all queued Job
	 * for the same type will be killed
	 */
	public boolean isAlone();

	/** Stop the current job */
	public void stopIt();

	/** @return the invoker reference */
	public Object getSource();
	
	/** Free inner resource */
	public void dispose();
	
	/** Has errors after processing */
	public boolean hasErrors();

}
