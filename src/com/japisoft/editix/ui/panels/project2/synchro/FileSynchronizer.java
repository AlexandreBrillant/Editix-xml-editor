package com.japisoft.editix.ui.panels.project2.synchro;

import java.io.File;
import java.io.IOException;
import org.apache.commons.io.FileUtils;
import com.japisoft.editix.ui.panels.project2.Node;

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
