package com.japisoft.xmlpad.tree.parser;

import java.io.StringReader;

import javax.swing.JTree;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import com.japisoft.framework.job.BasicJob;
import com.japisoft.framework.job.KnownJob;
import com.japisoft.framework.job.SwingEventSynchro;

import com.japisoft.framework.xml.parser.document.Document;
import com.japisoft.framework.xml.parser.node.FPNode;
import com.japisoft.framework.xml.parser.tools.XMLToolkit;
import com.japisoft.xmlpad.XMLContainer;
import com.japisoft.xmlpad.editor.XMLPadDocument;
import com.japisoft.xmlpad.editor.XMLEditor;
import com.japisoft.xmlpad.tree.TreeListeners;

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
public class ParsingJob extends BasicJob implements KnownJob, SwingEventSynchro {
	private Parser p;
	private boolean running = false;
	private ParsingErrorListener parsingErrorListener;
	private XMLContainer container;
	private TreeListeners listeners;
	
	public ParsingJob( XMLContainer container, TreeListeners listeners ) {
		if ( container == null )
			throw new NullPointerException( "The container cannot be null !" );
		this.container = container;
		this.parsingErrorListener = 
			new ParsingErrorListener( container.getErrorManager() );
		this.listeners = listeners;
	}

	private XMLEditor getEditor() { 
		return container.getUIAccessibility().getEditor(); 
	}

	private JTree getTree() { 
		return listeners.getTree(); 
	}

	public void dispose() {
		d = null;
		if (p != null && !p.isInterrupted()) {
			if (getEditor() != null) {
				getEditor().setEnabledXPathLocation(true);
			}
		}
	}

	public void disposeAll() {
		dispose();
		parsingErrorListener.dispose();
		parsingErrorListener = null;
		this.container = null;
		this.listeners = null;
	}

	public String getName() {
		return "parsing";
	}

	private boolean lockedLocation = false;

	/**
	 * @return true if a parsing process is working avoiding the real time
	 *         location
	 */
	public boolean isLocationLocked() {
		return lockedLocation;
	}	

	public void stopIt() {
		if ((p != null) && running)
			p.interruptParsing();
		lockedLocation = false;
	}

	public Object getSource() {
		return this;
	}

	public boolean isAlone() {
		return true;
	}
	
	Document d = null;

	public boolean preRun() {
		d = null;
		lockedLocation = true;
		
		try {
			Thread.sleep( 800 );
		} catch( InterruptedException e ) {}
		
		try {

			try {

				running = true;
				p = container.createNewParser();
				p.setFlatView(true);
				p.setBackgroundMode(true);
				p.setParsingMode(Parser.CONTINUE_PARSING_MODE);
				p.setErrorSignal( parsingErrorListener );
				String content = getEditor().getText();
				if (!p.isInterrupted())
					d = p.parse(new StringReader(content));
				if (!p.hasError() && !p.isInterrupted()) {
					container.getErrorManager().notifyNoError( true );
					if (getTree() != null
							&& (d == null || d.getRoot() == null)) {
						getTree().setModel(new DefaultTreeModel(null));
						return false;
					}
					return true;
				}
			} catch ( Throwable th ) {
				if ("true".equals(System.getProperty("xmlpad.debug")))
					th.printStackTrace();
			}

		} finally {
			container.getErrorManager().stopErrorProcessing();
			lockedLocation = false;
			running = false;
		}

		return false;
	}

	public String getErrorMessage() {
		return null;
	}

	public void run() {
		if ( d == null )
			return;
		boolean notifyLocation = false;

		if (getTree() != null) {
			if (!p.isInterrupted())
				getTree().setModel(
						new DefaultTreeModel((TreeNode) d.getRoot()));
		}

		// Store the current location
		
		TreePath tp = null;
		if (getTree() != null)
			tp = getTree().getSelectionPath();

		String xp = null;

		if (tp != null) {
			FPNode n = (FPNode) tp.getLastPathComponent();
			xp = n.getXPathLocation();
		}

		container.setLastNodeParsed( ( FPNode ) d.getRoot() );

		if ( d.getRoot() != null ) {
			if (xp != null) {
				FPNode n = ((FPNode) d.getRoot())
						.getNodeForXPathLocation(xp, true);
				tp = XMLToolkit.getTreePath(n);
				if (tp != null)
					getTree().setSelectionPath(tp);
			}
			lockedLocation = false;
			notifyLocation = true;
		} else {
			if (getTree() == null
					|| ((XMLPadDocument)getEditor().getDocument())
							.forceLocation()) {
				lockedLocation = false;
				notifyLocation = true;
			}
		}

		if (notifyLocation) {
			if (getEditor() != null) {
				getEditor().setEnabledXPathLocation(true);
				getEditor().notifyCurrentLocation();
			}
		}
	}

}
