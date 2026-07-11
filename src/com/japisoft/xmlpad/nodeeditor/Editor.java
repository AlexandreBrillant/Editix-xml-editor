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

package com.japisoft.xmlpad.nodeeditor;

import com.japisoft.framework.xml.parser.node.FPNode;

/**
 * <p>This Editor is a contract for editing any node. An editor has two roles :
 * - Decide if this editor accept a node
 * - Edit it using an EditorContext state</p>
 * <p>
 * Important element : This editor is not an XMLEditor but only and add-one for
 * editing a particular node. This is useful when you want a custom editor better
 * adatper to user need
 * </p>
 * @author Alexandre Brillant (https://github.com/AlexandreBrillant/Editix-xml-editor)
 * @see EditorContext
 */
public interface Editor {

	/** @return true when this editor accepts to edit this node */
	public boolean accept( FPNode node );
	/** Edit a particular text context. This context contains the current
	 * text and node to edit. You must call the <code>setResult</code> once
	 * you terminate the custom editing part. If you don't want to edit it, just
	 * write a <code>null</code> result.
	 * @param context Editing context
	 */
	public void edit( EditorContext context );

}
