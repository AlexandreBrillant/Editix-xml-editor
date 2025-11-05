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

package com.japisoft.xmlpad;

import java.util.Vector;

/**
 * This listener will notify any action change on the <code>XMLContainer</code> popup. Thus
 * each time an <code>XMLAction</code> will be added or removed the listener will be contacted. Note that
 * for receiving event, you must connect it to a PopupModel available from the <code>XMLContainer</code>.
 * @author Alexandre Brillant (https://github.com/AlexandreBrillant/Editix-xml-editor) */
public interface PopupModelListener {
	/** Notify a popup change for all the following actions and separators */
	public void updateActions( Vector v );
}

