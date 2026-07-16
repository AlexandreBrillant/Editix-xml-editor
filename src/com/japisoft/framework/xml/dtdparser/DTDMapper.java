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

package com.japisoft.framework.xml.dtdparser;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import com.japisoft.framework.xml.dtdparser.node.RootDTDNode;

/**
 * Interface for using DTD in a non connected mode. It will convert
 * an URL path by another one that could be local.
 * @author Alexandre Brillant (https://github.com/AlexandreBrillant/Editix-xml-editor)
 * @version 1.0
 * */
public interface DTDMapper {
	/** @return a stream for this DTD url 
	 * @exception Exception if the stream cannot be gotten
	 * */
	public InputStream getStream( String url ) throws IOException;
	/** @return <code>true</code> if the DTD can be stored in a cache */
	public boolean isCachedEnabled();
	/** @return the real path for this url */
	public File updateCache( RootDTDNode root, String url );
}
