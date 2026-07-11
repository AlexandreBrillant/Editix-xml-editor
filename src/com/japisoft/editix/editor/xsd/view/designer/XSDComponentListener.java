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

package com.japisoft.editix.editor.xsd.view.designer;

public interface XSDComponentListener {

	public void select( XSDComponent source );
	public void openClose( XSDComponent source, boolean validateMode );
	public void popup( XSDComponent source, int x, int y );
	public void goInto( XSDComponent source );
	
	// Drag'n drop
	public void moveInto( XSDComponent source, XSDComponent target );
	public void moveBefore( XSDComponent source, XSDComponent target );
	public void moveAfter( XSDComponent source, XSDComponent target );
	
}
