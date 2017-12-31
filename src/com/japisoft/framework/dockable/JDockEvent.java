package com.japisoft.framework.dockable;

import java.util.EventObject;

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
public class JDockEvent extends EventObject {

	private int type;
	private String id;

	public static final int INNERWINDOW_SELECTED = 0;
	public static final int INNERWINDOW_MAXIMIZED = 1;
	public static final int INNERWINDOW_MOVED = 2;
	public static final int INNERWINDOW_REMOVED = 3;
	public static final int INNERWINDOW_HIDDEN = 4;
	public static final int INNERWINDOW_SHOWN = 5;
	public static final int INNERWINDOW_EXTRACTED = 6;
	public static final int INNERWINDOW_RESTORED = 7;
	
	/**
	 * @param source The JDock source
	 * @param type look at the INNERWINDOW_... const
	 * @param id The inner window id */
	public JDockEvent( Object source, int type, String id ) {
		super( source );
		this.type = type;
		this.id = id;
	}

	/** @return the JDock container */
	public JDock getJDockSource() { return ( JDock )getSource(); }

	/** @return the inner window id */
	public String getId() { return id; }

	/**
	 * @return <code>INNERWINDOW_SELECTED</code>,<code>INNERWINDOW_MAXIMIZED</code>,<code>INNERWINDOW_MOVED</code>... */
	public int getType() { 
		return type;
	}

	public String toString() {
		String msg = id + " ";
		switch( type ) {
			case INNERWINDOW_SELECTED : msg += "selected"; break;
			case INNERWINDOW_MAXIMIZED : msg += "maximized"; break;
			case INNERWINDOW_MOVED : msg += "moved"; break;
			case INNERWINDOW_REMOVED : msg += "removed"; break;
			case INNERWINDOW_HIDDEN : msg += "hidden"; break;
			case INNERWINDOW_SHOWN : msg += "shown"; break;
			case INNERWINDOW_EXTRACTED : msg += "extracted"; break;
			case INNERWINDOW_RESTORED : msg += "restored"; break;
		}
		return msg;
	}

}
