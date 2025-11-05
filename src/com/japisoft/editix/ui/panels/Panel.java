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

package com.japisoft.editix.ui.panels;

import javax.swing.Icon;
import javax.swing.JComponent;

import com.japisoft.xmlpad.XMLContainer;

public interface Panel {
	public void init();
	public void setState( boolean shown );
	public void showHidePanel();
	public void setParams( String params );
	public void showPanel();
	public void hidePanel();
	public void stop();
	public void close();
	public boolean isShown();
	public void setCurrentXMLContainer( XMLContainer container );
	public void setIcon( Icon icon );
	public void setId( String id );
	public void select( Object path );
	public JComponent getView();
}

