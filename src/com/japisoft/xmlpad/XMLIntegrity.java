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

package com.japisoft.xmlpad;

/**
 * This class is used inside the XMLContainer for avoiding user to corrupt
 * the current XML document. For instance, you can disable tag update or
 * force a parsing before saving 
 * @author Alexandre Brillant (https://github.com/AlexandreBrillant/Editix-xml-editor)
 * @version 1.0
 */
public class XMLIntegrity {
	private boolean parseBeforeSaving;
	private boolean protectTag;

	/**
	 * @return true if the document is parsed before saving. If the document
	 * is not valid, the save action has no effect. By default false.
	 */
	public boolean isParseBeforeSaving() {
		return parseBeforeSaving;
	}

	/**
	 * Decide to parse the document before saving. If the document is wrong
	 * the saving action is canceled.
	 * @param b
	 */
	public void setParseBeforeSaving(boolean b) {
		parseBeforeSaving = b;
	}

	/**
	 * A protection mean user can't edit it
	 * @return true is a protection is available on tag. */
	public boolean isProtectTag() {
		return protectTag;
	}

	/**
	 * If true, it protects all tag from user insert or remove
	 * @param b true to protect all tag. */
	public void setProtectTag(boolean b) {
		protectTag = b;
	}

}
