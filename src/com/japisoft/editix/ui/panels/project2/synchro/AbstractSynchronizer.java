package com.japisoft.editix.ui.panels.project2.synchro;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.swing.SwingUtilities;

import com.japisoft.editix.ui.panels.project2.Node;
import com.japisoft.framework.ApplicationModel;

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
public abstract class AbstractSynchronizer implements Synchronizer, SynchronizerListener, Runnable {

	private Map<String,String> properties = null;
	
	public Set<String> getProperties() {
		if ( properties == null )
			return null;
		return properties.keySet();
	}

	public String getProperty(String name) {
		if ( properties == null )
			return null;
		return properties.get( name );
	}

	private Task task = null;
	
	public void upload(final File rootPath, final Node node) throws IOException {
		if ( task != null ) {
			info( "A task is running" );
			return;
		}		
		task = new Task() {
			public void run() throws Exception {			
				start();
				try {
					uploadIt(rootPath, node );
				} finally {
					stop();
				}
			}
		};
		new Thread( this ).start();
	}

	public void run() {
		try {
			info( "Starting synchronization" );
			task.run();
			info( "End of the synchronization" );
		} catch( Exception exc ) {
			info( "Error : " + exc.getMessage() );
		}
		task = null;
		listener = null;
	}

	public void download( final File rootPath, final Node node ) throws IOException {
		if ( task != null ) {
			info( "A task is running" );
			return;
		}
		task = new Task() {
			public void run() throws Exception {
				start();
				try {
					downloadIt(rootPath, node );
				} finally {
					stop();
				}				
			}
		};
		new Thread( this ).start();
	}

	protected abstract void uploadIt(File rootPath, Node node) throws IOException;
	
	protected abstract void downloadIt(File rootPath, Node node) throws IOException;
		
	public void setProperty(String name, String value) {
		if ( properties == null )
			properties = new HashMap<String, String>();
		properties.put( name, value );
	}
	
	public void info(final String message) {
		SwingUtilities.invokeLater(
			new Runnable() {
				public void run() {
					ApplicationModel.fireApplicationValue( "message", message );
				}
			}
		);
	}

	private SynchronizerListener listener = null;
	
	public void setListener( SynchronizerListener listener ) {
		this.listener = listener;
	}
	
	public void start() {
		if ( listener != null )
			listener.start();
	}

	public void stop() {
		if ( listener != null )
			listener.stop();		
	}

	protected String getRelativePath( File rootPath, File file ) {
		String startPart = rootPath.toString();
		startPart = startPart.replace( "\\", "/" );
		if ( !startPart.endsWith( "/" ) )
			startPart += "/";
		String docPart = file.toString();
		docPart = docPart.replace( "\\", "/" );		
		if ( docPart.startsWith( startPart ) ) {
			return docPart.substring( startPart.length() );
		}
		return docPart;
	}
	
	protected String getRelativePath( File rootPath, Node file ) {
		return getRelativePath( rootPath, file.getPath() );
	}
	
	@Override
	public String toString() {
		return getName();
	}
	
	interface Task {
		public void run() throws Exception;
	}
	
}
