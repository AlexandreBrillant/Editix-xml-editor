package com.japisoft.xflows.task;

import com.japisoft.framework.xml.parser.node.FPNode;

/**
This program is available under two licenses : 

1. For non commercial usage : 

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.

2. For commercial usage :

You need to get a commercial license for source usage at : 

http://www.editix.com/buy.html

Copyright (c) 2018 Alexandre Brillant - JAPISOFT SARL - http://www.japisoft.com

@author Alexandre Brillant - abrillant@japisoft.com
@author JAPISOFT SARL - http://www.japisoft.com

*/
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


