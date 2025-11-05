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

package com.japisoft.xflows.task;

import com.japisoft.framework.xml.parser.node.FPNode;

/**
 * @author Alexandre Brillant (https://github.com/AlexandreBrillant/Editix-xml-editor)
 * @version 1.0
 * */
public class Task {

	private String name;
	private String type;
	private TaskParams params;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}
	
	public void setType(String type) {
		this.type = type;
	}

	public TaskParams getParams() {
		if ( params == null )
			params = new TaskParams();
		return params;
	}

	public void setParams(TaskParams params) {
		this.params = params;
	}
	
	public FPNode toXML() {
		FPNode task = new FPNode( FPNode.TAG_NODE, "task" );
		task.setAttribute( "name", name );
		task.setAttribute( "type", type );
		if ( params != null ) {
			FPNode sn = params.toXML();
			if ( sn != null )
				task.appendChild( sn );
		}
		return task;
	}

	public void updateFromXML( FPNode task ) {
		setName( task.getAttribute( "name" ) );
		setType( task.getAttribute( "type" ) );
		if ( task.childCount() > 0 ) {
			params = new TaskParams();
			params.updateFromXML( task.childAt( 0 ) );
		}
	}

	public String toString() {
		return "[" + getName() + "," + getType() + "," + params + "]";
	}
	
}



