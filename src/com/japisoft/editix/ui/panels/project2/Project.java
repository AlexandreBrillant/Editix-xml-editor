package com.japisoft.editix.ui.panels.project2;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import com.japisoft.editix.ui.panels.project2.synchro.Synchronizer;

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
public interface Project {

	public void load() throws IOException;

	public void save() throws IOException;

	public Node getRoot();
	
	public File getPath();
	
	public boolean contains( File f );
	
	public NodeSortMode getSortMode();

	public boolean skip( String name );

	public void openFile( String path, String type );

	public String getLastType( String path );
	
	public void closeFile( String path, Map properties );

	public Map getProperties( String path );

	public void setSynchronizers( Synchronizer[] synchronizers );
	
	public Synchronizer[] getSynchronizers();
	
	public void setSelectedSynchronized( Synchronizer synchronizer );
	
	public int getSelectedSynchronizer();
	
	public void setNodeState( String[] expandedPath );
	
	public String[] getNodeState();

	public void setEncoding( String path, String encoding );
	
	public String getEncoding( String path );
	
}
