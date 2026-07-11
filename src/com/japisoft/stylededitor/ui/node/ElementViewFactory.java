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

package com.japisoft.stylededitor.ui.node;

import com.japisoft.stylededitor.EditorByCSS;
import com.japisoft.stylededitor.model.DocumentElement;
import com.japisoft.stylededitor.model.NodeElement;
import com.japisoft.stylededitor.model.TextElement;

public class ElementViewFactory {

	private static ElementViewFactory INSTANCE = null;
	
	private ElementViewFactory() {
		super();
	}
	
	public static ElementViewFactory getInstance() {
		if ( INSTANCE == null )
			INSTANCE = new ElementViewFactory();
		return INSTANCE;
	}

	private DocumentElementView defaultRootView = null;
	private TextElementView defaultTextView = null;
	private NodeElementView defaultNodeView = null;

	private ElementView defaultNodeView2 = null;

	public ElementView getView( EditorByCSS editor, NodeElement element ) {
		if ( element instanceof TextElement ) {
			if ( defaultTextView == null )
				defaultTextView = new TextElementView();
			return defaultTextView;
		} else
		if ( element instanceof DocumentElement ) {
			if ( defaultRootView == null )
				defaultRootView = new DocumentElementView();
			return defaultRootView;
		} else {
			if ( defaultNodeView == null )
				defaultNodeView = new NodeElementView();

			if ( editor.displayTag() ) {
				if ( defaultNodeView2 == null )
					defaultNodeView2 = new NodeElementViewProxy( defaultNodeView );
				return defaultNodeView2;
			}

			return defaultNodeView;
		}
	}

}
