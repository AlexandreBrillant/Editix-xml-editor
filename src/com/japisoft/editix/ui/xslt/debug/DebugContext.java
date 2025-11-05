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

package com.japisoft.editix.ui.xslt.debug;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Alexandre Brillant (https://github.com/AlexandreBrillant/Editix-xml-editor)
 * @version 1.0
 * */
public class DebugContext {

	public ArrayList nodeSet;
	public ArrayList callStack;
	public List<DebugVariable> variable;
	public List<DebugVariable> parameter;
	private String uri;
	
	/** @param nodeSet set of DOM nodes 
	 */
	public DebugContext(
			String uri,
			ArrayList callStack, 
			ArrayList nodeSet, 
			List<DebugVariable> variable,
			List<DebugVariable> parameter ) {
		this.uri = uri;
		this.callStack = callStack;
		this.nodeSet = nodeSet;
		this.variable = variable;
		this.parameter = parameter;
	}
	
}

