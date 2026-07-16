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

package com.japisoft.framework.ui.stylededitor.ui;

import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;

import com.japisoft.framework.ui.stylededitor.EditorByCSS;

public class ViewPartContainer implements ViewPart {

	private List<ViewPart> views = null;
	
	public void addView( ViewPart view ) {
		if ( views == null )
			views = new ArrayList<ViewPart>();
		views.add( view );
	}

	public void paint( EditorByCSS editor, Graphics gc ) {
		if ( views != null ) {
			for ( ViewPart vp : views ) {
				vp.paint( editor, gc );
			}
		}
	}

}
