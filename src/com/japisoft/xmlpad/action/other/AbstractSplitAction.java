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

package com.japisoft.xmlpad.action.other;

import com.japisoft.xmlpad.action.XMLAction;

public abstract class AbstractSplitAction extends XMLAction {

	public AbstractSplitAction() {
		super();
		setPopable( false );
		if ( !isToolbarEnabledByDefault() ) {
			setToolbarable( false );
		}
	}

	@Override
	public boolean notifyAction() {
		container.split( isVerticalSplit() );
		return VALID_ACTION;
	}

	protected abstract boolean isVerticalSplit();

	protected boolean isToolbarEnabledByDefault() {
		return true;
	}
}