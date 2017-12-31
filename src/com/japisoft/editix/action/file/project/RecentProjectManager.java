package com.japisoft.editix.action.file.project;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JMenu;

import com.japisoft.framework.ApplicationModel;
import com.japisoft.framework.application.descriptor.ActionModel;
import com.japisoft.framework.application.descriptor.InterfaceBuilder;

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
public class RecentProjectManager {

	private static RecentProjectManager INSTANCE = null;
	
	private RecentProjectManager() {
		super();
		load();
	}
	
	public static RecentProjectManager getInstance() {
		if ( INSTANCE == null )
			INSTANCE = new RecentProjectManager();
		return INSTANCE;
	}
	
	private List<String> recentProjects = null;
	
	private void load() {
		recentProjects = new ArrayList<String>();
		File f = new File( 
			ApplicationModel.getAppUserPath(), 
			"recentProjects.dat" 
		);
		if ( f.exists() ) {
			try {
				BufferedReader br = new BufferedReader(
						new FileReader( f ) );
				try {
					String l = null;
					while ( ( l = br.readLine() ) != null ) {
						if ( new File( l ).exists() )
							recentProjects.add( l );
					}						
				} finally {
					br.close();
				}
			} catch( Exception exc ) {
				ApplicationModel.debug( exc );
			}
		}
	}

	public void addProject( File projectLocation ) {
		String str = projectLocation.toString();
		recentProjects.remove( str );
		recentProjects.add( 0, str );
		save();
		

		// Rebuild the recent menu builder
		RecentFileMenuBuilder builder = new RecentFileMenuBuilder();
		InterfaceBuilder ib = ApplicationModel.INTERFACE_BUILDER;
		builder.build( ib.getMenu( "openrp" ) );
	}

	public int getItemCount() {
		return recentProjects.size();
	}
	
	public String getItem( int index ) {
		return recentProjects.get( index );
	}

	private void save() {
		File f = new File( 
			ApplicationModel.getAppUserPath(),
			"recentProjects.dat"
		);
		try {
			BufferedWriter bf = 
				new BufferedWriter( 
					new FileWriter( f ) );
			try {
				boolean first = true;
				for ( String str : recentProjects ) {
					if ( !first ) {
						bf.newLine();
					}
					bf.write( str );
					first = false;
				}
			} finally {
				bf.close();
			}
		} catch( Exception exc ) {
			ApplicationModel.debug( exc );
		}
	}

}
