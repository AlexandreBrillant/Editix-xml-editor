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

package com.japisoft.editix.script;

import java.io.File;

public class BasicScript implements Script {

	private String name = "MyScript";
	private File path;
	private String shortkey = "";

	public BasicScript( String name, File path, String shortkey ) {
		this.name = name;
		this.path = path;
		this.shortkey = shortkey;
	}

	public void setName( String name ) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}

	public File getPath() {
		return path;
	}
	
	public void setPath( File path ) {
		this.path = path;
	}

	public String getShortkey() {
		return shortkey;
	}
	
	public void setShortkey( String sk ) {
		this.shortkey = sk;
	}

}

