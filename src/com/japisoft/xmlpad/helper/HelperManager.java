package com.japisoft.xmlpad.helper;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import javax.swing.SwingUtilities;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

import com.japisoft.framework.job.JobAdapter;
import com.japisoft.framework.job.JobManager;
import com.japisoft.framework.preferences.Preferences;
import com.japisoft.framework.xml.parser.node.FPNode;
import com.japisoft.xmlpad.Debug;
import com.japisoft.xmlpad.XMLContainer;
import com.japisoft.xmlpad.editor.XMLEditor;
import com.japisoft.xmlpad.editor.XMLPadDocument;
import com.japisoft.xmlpad.helper.handler.AbstractHelperHandler;
import com.japisoft.xmlpad.helper.handler.schema.AbstractTagHandler;
import com.japisoft.xmlpad.helper.model.Descriptor;
import com.japisoft.xmlpad.helper.model.TagDescriptor;
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
public class HelperManager {
	private XMLContainer container;

	private List<Descriptor> result = null;

	public HelperManager(XMLContainer container) {
		this.container = container;
	}
	
	private String getPreviousSequence( XMLEditor editor, int offset ) {
		try {
			Document doc = editor.getDocument();
			StringBuffer sb = null;
			while ( offset > 0 ) {
				offset--;
				String str = doc.getText( offset, 1 );
				char ch = str.charAt( 0 );
				if ( !( Character.isLetter( ch ) || ch == ':' || ch == '-' ) )
					break;
				if ( sb == null )
					sb = new StringBuffer();
				sb.insert( 0, ch );
			}
			if ( sb != null )
				return sb.toString();
		} catch( BadLocationException ble ) {
			
		}
		return null;
	}
	

	public void dispose() {
		this.container = null;
		this.result = null;
		if (uiHelper != null) {
			uiHelper.dispose();
			uiHelper = null;
		}
		for (int i = 0; i < getHelperHandlerCount(); i++) {
			getHelperHandler(i).dispose();
		}
		handlers = null;
		if (managerJob != null) {
			managerJob.dispose();
			managerJob = null;
		}
		activeHelperHandler = null;
		fasterTask = null;
	}

	/**
	 * @param currentNode
	 *            the current node
	 * @param title
	 *            a Title for the assistant
	 * @param insertBefore
	 *            <code>true</code> if the added part must be inserted before.
	 *            This is useful when activating an helper by a shortkey
	 * @param offset
	 *            The current location
	 * @param activatorString
	 *            The element added by the user like '<'
	 * @return <code>true</code> if a content assistant is shown */
	public boolean activateContentAssistant(
			FPNode currentNode,
			String title, 
			boolean insertBefore, 
			int offset,
			String activatorString ) {
		
		if ( !enabled )
			return false;

		// Inside comment ?

		if (container.getXMLDocument().isInsideComment(offset)
				|| container.getXMLDocument().isInsideCDATA(offset))
			return false;

		boolean mustWaitForNoJob = false;
		
		activeHelperHandler = null;
		
		// Check for active helper handlers
		for (int i = (getHelperHandlerCount() - 1); i >= 0; i--) {

			if (getHelperHandler(i).isEnabled()) {

				if (getHelperHandler(i).haveDescriptors(currentNode,
						container.getXMLDocument(), insertBefore, offset,
						activatorString)) {

					Debug.debug( "Match " + getHelperHandler( i ) );
					
					if (getHelperHandler(i).mustBeJobSynchronized()) {
						mustWaitForNoJob = true;
					}
					if (activeHelperHandler == null)
						activeHelperHandler = new ArrayList();

					activeHelperHandler.add( 0, getHelperHandler( i ) );

				}

			}
		}

		if ( activeHelperHandler == null || 
				activeHelperHandler.size() == 0 )
			return false;

		// /////////// ACTIVE ////////////

		this.currentNode = currentNode;
		this.currentTitle = title;
		this.currentInsertBefore = insertBefore;
		this.currentOffset = offset;
		this.currentActivatorString = activatorString;

		if (managerJob == null) {
			managerJob = new HelperManagerJob();
		}
		
		if ( Preferences.getPreference( "editor", "assistantAsync", false ) ) {
		
			managerJob.asynchronousMode = mustWaitForNoJob;
			if (!mustWaitForNoJob) {
				managerJob.run();
				managerJob.dispose();
				return ( result.size() > 0 );
			} else {
				if (fasterTask == null) {
					fasterTask = new Runnable() {
						public void run() {
							JobManager.addJob( managerJob );
						}
					};
				}
				SwingUtilities.invokeLater(fasterTask);
				return true;
			}
		
		} else {
		
			this.container.getEditor().forceLocationJob();
			managerJob.run();
			activeHelperHandler = null;
			return ( result.size() > 0 );
			
		}
	}

	private String newTitleTmp = null;

	/**
	 * Prepared all the available descriptors at this location. This is useful
	 * for getting the completion possibilities without showing an assistant */
	private List<Descriptor> prepareDescriptors(FPNode node, XMLPadDocument doc,
			boolean insertBefore, int offset, String activator) {

		newTitleTmp = null;

		if (result == null)
			result = new ArrayList();
		else
			result.removeAll(result);
		
		//for (int i = activeHelperHandler.size() - 1; i >= 0; i--) {
		
		for ( int i = 0; i < activeHelperHandler.size(); i++ ) {

			Descriptor[] resTmp = ((AbstractHelperHandler) activeHelperHandler
					.get(i)).resolveContentAssistant(currentNode, container
					.getXMLDocument(), currentInsertBefore, currentOffset,
					currentActivatorString);
			
			// Complete with the good prefix

			if (newTitleTmp == null && resTmp != null && resTmp.length > 0)
				newTitleTmp = getHelperHandler(i).getTitle();

			if (resTmp != null) {
				addResult((AbstractHelperHandler) activeHelperHandler.get(i),
						resTmp);
			}
		}

		return result;
	}

	// /////////////////////////////////
	Runnable fasterTask = null;

	HelperManagerJob managerJob = null;

	private FPNode currentNode;

	private String currentTitle;

	private boolean currentInsertBefore;

	private int currentOffset;

	private String currentActivatorString;

	private ArrayList activeHelperHandler = null;

	// /////////////////////////////////

	static boolean HELPER_WORKING = false;
	
	// For asynchronous JOB when requiring to know the current location
	class HelperManagerJob extends JobAdapter {

		boolean asynchronousMode = false;
		
		public boolean preRun() {
			HELPER_WORKING = true;
			return false;
		}

		public void run() {
			prepareDescriptors(currentNode, container.getXMLDocument(),
					currentInsertBefore, currentOffset, currentActivatorString);

			if ( newTitleTmp == null ) {
				// Extract it from the current element name
				if (container.getCurrentElementNode() != null)
					newTitleTmp = container.getCurrentElementNode()
							.getNodeContent();
			}

			boolean mustBeShown = (result.size() > 0);

			if ( mustBeShown ) {
					showContentAssistant(null, newTitleTmp == null ? currentTitle
						: newTitleTmp, currentOffset, currentActivatorString);
			}
			else {
				// Don't loose the activator string
				if ( currentActivatorString != null && asynchronousMode ) {
						container.getXMLDocument().insertStringWithoutHelper( 
							container.getEditor().getCaretPosition(),
							currentActivatorString, null, true );
				}
			}				
		}

		public void dispose() {
			HELPER_WORKING = false;
			currentNode = null;
			try {
				activeHelperHandler.removeAll(activeHelperHandler);
			} catch( NoSuchElementException e ) {}
		}
	}

	private ContentAssistantUI uiHelper = null;

	void showContentAssistant(ContentAssistantUIListener listener, String title, int offset, String activatorString) {

		String previousSequence = getPreviousSequence(container.getEditor(), offset );
		
		if ( result != null && result.size() > 0 ) {
			
			List<Descriptor> descriptors2 = null;
			
			for (int i = 0; i < result.size(); i++) {
				if ( result.get( i ).startsWith( previousSequence ) ) {
					if ( descriptors2 == null )
						descriptors2 = new ArrayList<Descriptor>();
					descriptors2.add( result.get( i ) );
					result.get( i ).setSequence( previousSequence );
				}
			}
			
			if ( descriptors2 != null )
				result = descriptors2;

		}
		
		if ( result != null && result.size() == 1 ) {
			getUIHelper().activeDescriptor( container.getEditor(), container.getEditor().getCaretPosition(), result.get( 0 ) );
		} else 
			getUIHelper().show(listener, title, container.getUIAccessibility().getEditor(),
				offset, activatorString, result);
	}

	private ContentAssistantUI getUIHelper() {
		if (uiHelper == null) {
			uiHelper = new ContentAssistantUI();
		}
		return uiHelper;
	}
	
	void addResult( AbstractHelperHandler handler, Descriptor[] descriptors ) {
		for ( int i = 0; i < descriptors.length; i++ ) {
			result.add( descriptors[ i ] );

			// Manager bound tagDescriptor due to substitutionGroup from the XML
			// Schema
			if (descriptors[i] instanceof TagDescriptor) {
				TagDescriptor td = (TagDescriptor) descriptors[i];
				for (int j = 0; j < td.getSynonymousTagDescriptorCount(); j++) {
					result.add(i + 1, td.getSynonymousTagDescriptor(j));
				}
			}
		}
	}

	private boolean enabled = true;

	/**
	 * Activate or disable the helper. It means you can stop with this property
	 * all the content assistant
	 * 
	 * @param enabled
	 *            <code>false</code> for disabling the helper */
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	/**
	 * @return the status of the helper manager. Theorically it should return
	 *         <code>true</code> most of the time */
	public boolean isEnabled() {
		return enabled;
	}

	private ArrayList handlers = null;

	/** For debugging only the content */
	public void dump() {
		System.out.println( "Dump HelperManager :" );
		if ( handlers == null )
			System.out.println( "No content" );
		else
			for ( int i = 0; i < handlers.size(); i++ )
				System.out.println( handlers.get( i ) );
	}	

	/** Update all the handlers 
	 * @param handlers List of helpers for the content assistant
	 * @param appendMode Decide to maintain the current assistants or not
	 * */
	public void resetHandlers(ArrayList handlers, boolean appendMode ) {
		if ( !appendMode || this.handlers == null ) {
			this.handlers = handlers;
		} else {
			for ( int i = handlers.size() - 1; i >= 0; i-- ) {
				addHelperHandler( ( AbstractHelperHandler )handlers.get( i ) );
			}
		}
	}

	/** Take into account the priority for ordering */
	private void addWithPriority( AbstractHelperHandler handler ) {
		// Use the priority
		boolean added = false;
		for ( int i = 0; i < handlers.size(); i++ ) {
			if ( ( ( AbstractHelperHandler )handlers.get( i ) ).getPriority()
					< handler.getPriority() ) {
				added = true;
				handlers.add( i, handler );
				break;
			}
		}
		if ( !added )
			handlers.add( handler );
	}
	
	/** Add an handler managing a content assistant part */
	public void addHelperHandler(AbstractHelperHandler handler) {
		
		if (handlers == null)
			handlers = new ArrayList();
		
		if ( handler instanceof AbstractTagHandler ) {
			if ( container != null )
				( ( AbstractTagHandler )handler ).setNamespace( 
						container.getDocumentInfo().getDefaultNamespace() );
		}

		// Group it by handler type
		if ( handler.getType() == null ) {
			addWithPriority( handler );
		}
		else {
			boolean added = false;
			for ( int i = 0; i < handlers.size(); i++ ) {
				String type = ( ( AbstractHelperHandler )handlers.get( i ) )
						.getType();
				if ( type != null && 
						type.equalsIgnoreCase( handler.getType() ) ) {
					handlers.add( i + 1, handler );
					added = true;
					break;
				}
			}
			if ( !added )
				addWithPriority( handler );
		}
	}

	/** Remove an handler managing a content assistant part */
	public void removeHelperHandler(AbstractHelperHandler handler) {
				
		if (handlers != null) {
			handlers.remove(handler);
			handler.dispose();
		}
	}

	/**
	 * Remove an helper handler for a name
	 * 
	 * @return true if the operation is a success */
	public boolean removeHelperHandler(String name) {
		for (int i = 0; i < getHelperHandlerCount(); i++) {
			AbstractHelperHandler ahh = getHelperHandler(i);
			if (ahh.getName().equalsIgnoreCase(name)) {
				removeHelperHandler(ahh);
				return true;
			}
		}
		return false;
	}

	/** @return the available helper handler */
	public int getHelperHandlerCount() {
		if (handlers == null)
			return 0;
		return handlers.size();
	}

	/** @return the available helper handler by index starting from 0 */
	public AbstractHelperHandler getHelperHandler(int index) {
		if (handlers == null)
			return null;
		return (AbstractHelperHandler) handlers.get(index);
	}

	/** @return the available helper handler by name */
	public AbstractHelperHandler getHelperHandler(String name) {
		for (int i = 0; i < getHelperHandlerCount(); i++) {
			if (getHelperHandler(i).getName() != null
					&& getHelperHandler(i).getName().equalsIgnoreCase(name))
				return getHelperHandler(i);
		}
		return null;
	}

	/** Disabled/Enabled an handler */
	public void setEnabled( String name, boolean enabled ) {
		AbstractHelperHandler handler = getHelperHandler( name );
		if  ( handler != null )
			handler.setEnabled( enabled );
	}

}
