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

package com.japisoft.editix.ui.leftpanels.project2.synchro;

public class SynchronizerFactory {

	private static SynchronizerFactory INSTANCE = null;
	
	private SynchronizerFactory() {
		INSTANCE = this;
	}
	
	public static SynchronizerFactory getInstance() {
		if ( INSTANCE == null ) {
			new SynchronizerFactory();
		}
		return INSTANCE;
	}

	public Synchronizer getSynchronizer( String name ) {
		if ( "LOCAL/NETWORK".equalsIgnoreCase( name ) )
			return new FileSynchronizer();
		else
		if ( "ftp".equalsIgnoreCase( name ) )
			return new FTPSynchronizer();
		return null;
	}

	public String[] getTypes() {
		return new String[] { "LOCAL/NETWORK", "FTP" };
	}

}

