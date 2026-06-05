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

package com.japisoft.xmlpad.tree.parser;

import java.io.StringReader;
import java.util.List;

import javax.swing.JTree;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import com.japisoft.framework.ApplicationModel;
import com.japisoft.framework.collection.FastArrayList;
import com.japisoft.framework.job.BasicJob;
import com.japisoft.framework.job.KnownJob;
import com.japisoft.framework.job.SwingEventSynchro;

import com.japisoft.framework.xml.parser.document.Document;
import com.japisoft.framework.xml.parser.node.FPNode;
import com.japisoft.framework.xml.parser.tools.XMLToolkit;
import com.japisoft.xmlpad.XMLContainer;
import com.japisoft.xmlpad.editor.XMLPadDocument;
import com.japisoft.xmlpad.toolkit.StringReader2;
import com.japisoft.xmlpad.editor.XMLEditor;
import com.japisoft.xmlpad.tree.TreeListeners;

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
				
				List<FPNode> oldVector = null;
				if ( container.getRootNode() != null ) {
					oldVector = container.getRootNode().getDocument().getFlatNodes();
				}
				
				boolean lightweightMode = !container.getXMLDocument().shouldReparse(); 
				
				p = container.createNewParser(
						lightweightMode
				);
								
				p.setFlatView(true);
				p.setBackgroundMode(true);
				p.setParsingMode(Parser.CONTINUE_PARSING_MODE);
				p.setErrorSignal( parsingErrorListener );
				String content = getEditor().getText();
				if (!p.isInterrupted())
					d = p.parse( new StringReader2( content ), container );
				if (!p.hasError() && !p.isInterrupted()) {
					container.getErrorManager().notifyNoError( true );
					if (getTree() != null
							&& (d == null || d.getRoot() == null)) {
						getTree().setModel(new DefaultTreeModel(null));
						return false;
					}
					
					// Force the update of the tree for a new content : first time...
					if ( d.getFlatNodes() != oldVector && p.isLightweightMode() )
						updateTree();
										
					container.getXMLDocument().checkReparse();
					
					return true;
				} else {
					container.getXMLDocument().forceReparse();
				}
								
			} catch ( Throwable th ) {
				if ("true".equals(System.getProperty("xmlpad.debug")))
					th.printStackTrace();
				container.getXMLDocument().forceReparse();
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
		// Don't update the model
		
		if ( ( d == null ) || ( p.isLightweightMode() ) ) {
			return;
		}

		updateTree();
	}
	
	private void updateTree() {
		boolean notifyLocation = false;
		
		if (getTree() != null) {
			if (!p.isInterrupted() ) {				
				DefaultTreeModel model = ( DefaultTreeModel )getTree().getModel();
				model.setRoot( ( TreeNode )d.getRoot() );
			}
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
		
		if (notifyLocation && !p.isLightweightMode()) {
			if (getEditor() != null) {
				getEditor().setEnabledXPathLocation(true);
				getEditor().notifyCurrentLocation();
			}
		}
	}

}

