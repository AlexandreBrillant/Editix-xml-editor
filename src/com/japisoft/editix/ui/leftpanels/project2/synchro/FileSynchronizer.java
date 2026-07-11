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

package com.japisoft.editix.ui.leftpanels.project2.synchro;

import java.io.File;
import java.io.IOException;
import org.apache.commons.io.FileUtils;

import com.japisoft.editix.ui.leftpanels.project2.Node;

public class FileSynchronizer extends AbstractSynchronizer {

	public String getName() {
		return "LOCAL/NETWORK";
	}

	public void uploadIt( File rootPath, Node node ) throws IOException {
		String relativePath = getRelativePath( rootPath, node.getPath() );
		File source = new File( rootPath, relativePath );
		File target = new File( getRemotePath() + relativePath );
		if ( source.isDirectory() ) {
			if ( !target.exists() )
				target.mkdirs();
			FileUtils.copyDirectoryToDirectory( source, target.getParentFile() );
		} else {
			File parentPath = target.getParentFile();
			if ( !parentPath.exists() )
				parentPath.mkdirs();
			FileUtils.copyFile( source, target );
		}		
	}

	public void downloadIt( File rootPath, Node node ) throws IOException {
		String relativePath = getRelativePath( rootPath, node.getPath() );
		File target = new File( rootPath, relativePath );
		File source = new File( getRemotePath() + relativePath );
		if ( source.isDirectory() ) {
			if ( !target.exists() )
				target.mkdirs();
			FileUtils.copyDirectoryToDirectory( source, target.getParentFile() );
		} else {
			File parentPath = target.getParentFile();
			if ( !parentPath.exists() )
				parentPath.mkdirs();			
			FileUtils.copyFile( source, target );
		}
	}

	public String getRemotePath() {
		String tmp = getProperty( "path" );
		if ( !tmp.endsWith( "/" ) )
			tmp += "/";
		return tmp;
	}

	public boolean test() {
		return false;
	}

	@Override
	public String toString() {
		return super.toString() + " [" + getRemotePath() + "]";
	}

}
