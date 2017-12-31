package com.japisoft.editix.xslt.debug;

import java.io.File;

import javax.xml.transform.ErrorListener;
import javax.xml.transform.SourceLocator;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;

import com.icl.saxon.TransformerFactoryImpl;
import com.japisoft.framework.ApplicationModel;
import com.japisoft.framework.application.descriptor.ActionModel;
import com.japisoft.editix.action.xsl.XSLTAction;
import com.japisoft.editix.ui.EditixFactory;
import com.japisoft.editix.xslt.profiler.SaxonProfilerListener;
import com.japisoft.xmlpad.IXMLPanel;
import com.japisoft.xmlpad.XMLContainer;

import net.sf.saxon.lib.FeatureKeys;

import org.xml.sax.SAXParseException;

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
public class XSLTManager {

	static Thread ownerThread = null;
	static XSLTDebugThread thread = null;
	static TracableListener DEBUG_LISTENER = null;
	static SaxonProfilerListener PROFILER_LISTENER = null;

	/** Update listener for debugging */
	public static void updateDebugListenerForFactory( 
			IXMLPanel panel, 
			TransformerFactory factory ) {
		
			DEBUG_LISTENER = new SaxonTraceListener( panel );
			
			// SAXON V1.0
			if ( factory instanceof TransformerFactoryImpl ) {
				factory.setAttribute(
						com.icl.saxon.FeatureKeys.TRACE_LISTENER, DEBUG_LISTENER );				
				factory.setAttribute(
						com.icl.saxon.FeatureKeys.LINE_NUMBERING, Boolean.TRUE );
			} else {
				// SAXON V2.0
				factory.setAttribute( net.sf.saxon.lib.FeatureKeys.TRACE_LISTENER, DEBUG_LISTENER );
				factory.setAttribute( net.sf.saxon.lib.FeatureKeys.COMPILE_WITH_TRACING, Boolean.TRUE );				
			}
	}

	public static void updateProfilerListenerForFactory(
			IXMLPanel panel,
			TransformerFactory factory ) {

		PROFILER_LISTENER = new SaxonProfilerListener(panel);
		
		// SAXON V1.0
		if ( factory instanceof TransformerFactoryImpl ) {
			factory.setAttribute(
					com.icl.saxon.FeatureKeys.TRACE_LISTENER, PROFILER_LISTENER );				
			factory.setAttribute(
					com.icl.saxon.FeatureKeys.LINE_NUMBERING, Boolean.TRUE );
		} else {
			// SAXON V2.0
			factory.setAttribute( FeatureKeys.TRACE_LISTENER, PROFILER_LISTENER );
			factory.setAttribute( FeatureKeys.COMPILE_WITH_TRACING, Boolean.TRUE );				
		}		
	}

	public static void endProfiler() {
		PROFILER_LISTENER.stopResult();
		PROFILER_LISTENER = null;
	}

	public static void processError( XMLContainer container, TransformerException te ) {
		if ( container == null )
			return;
		
		String location = container.getCurrentDocumentLocation();
		SourceLocator sl = te.getLocator();
		String sourcePath = null;
 		int line = -1;
 		int column = -1;
		
		if ( sl != null ) {
			sourcePath = sl.getSystemId();
			if ( sourcePath != null ) {
				if ( sourcePath.startsWith( "file:///" ) ) {
					sourcePath = sourcePath.substring( 8 );
				} else
				if ( sourcePath.startsWith( "file://" ) ) {
					sourcePath = sourcePath.substring( 7 );
				}
			} else {
				// Local by default
				sourcePath = location;				
			}
		}
 		boolean localError = 
			( ( sourcePath != null ) && new File( sourcePath ).equals( new File( location ) ) ) || ( sl == null );
 		String message = te.getMessage();
 		
/* 		if ( te.getCause() instanceof WrappedRuntimeException ) {
 			WrappedRuntimeException wre = ( WrappedRuntimeException )te.getCause();
 			message = wre.getMessage();
 			if ( wre.getException() instanceof TransformerException ) {
 				TransformerException _te = ( TransformerException )wre.getException();
 				if ( sl == null )
 					sl = _te.getLocator();
 			}
 		} */
 		if ( te.getCause() instanceof SAXParseException ) {
 			SAXParseException spe = ( SAXParseException )te.getCause();
 			line = spe.getLineNumber();
 			column = spe.getColumnNumber();
 			message = spe.getMessage();
 			sl = null;
 		}

 		if ( sl != null ) {
	 		line = sl.getLineNumber();
	 		column = sl.getColumnNumber();
 		}
 		
 		if ( "java.lang.NullPointerException".equals( message ) ) {
 			message = "Unknown error - Inner Error from the transformer";
 		}

 		container.getErrorManager().notifyError(
 				null,
 				localError,
 				sourcePath,
 				line,
 				column,
 				-1,
 				message,
 				false );
 		
 		ApplicationModel.fireApplicationValue( 
 			"error", 
 			"Error(s) found while transforming" 
 		);
	}

	public static boolean hasBreakpoint( IXMLPanel container ) {
		return container.getBookmarkContext().getModel().getBookmarkCount() != 0;
	}

	public static void startDebug( IXMLPanel container ) {

		ownerThread = Thread.currentThread();

		if ( !hasBreakpoint( container ) ) {
			EditixFactory.buildAndShowErrorDialog( "No breakpoint found !" );
			return;
		}
		
		ActionModel.setEnabled( "lastTransform", false );		
		ActionModel.setEnabled( "transformFromXSLT", false );
		ActionModel.setEnabled( "xsltdebug1", false );
		ActionModel.setEnabled( "xsltdebug3", true );
		ActionModel.setEnabled( "xsltdebug4", true );
		ActionModel.setEnabled( "xsltdebug5", true );
		
		if ( thread != null ) {
			thread.continueBreakpoint();
		} else {
			thread = new XSLTDebugThread( container );
			thread.start();
		}

	}

	public static void continueToBreakPoint() {
		if ( thread != null )
			thread.continueBreakpoint();
	}
	
	public static void continueToNextElement() {
		if ( thread != null )
			thread.continueNextElement();
	}
	
	public static void terminate() {
		if ( thread != null )
			thread.terminateAll();
	}
	
	static class XSLTDebugThread extends Thread {
		
		IXMLPanel container = null;
		boolean status = XSLTAction.OK;
		
		public XSLTDebugThread( IXMLPanel container ) {
			this.container = container; 
		}
		
		// Proxy for the listener
		
		public void continueBreakpoint() {
			if ( DEBUG_LISTENER != null )
				DEBUG_LISTENER.continueBreakpoint();
		}
		
		public void continueNextElement() {
			if ( DEBUG_LISTENER != null )
				DEBUG_LISTENER.continueNextElement();
		}

		public void terminateAll() {
			if ( DEBUG_LISTENER != null )
				DEBUG_LISTENER.terminateAll();
		}
		
		public void run() {
			try {
				run2();
			} finally {
				container = null;
				thread = null;
				if ( DEBUG_LISTENER != null )
					DEBUG_LISTENER.dispose();
				DEBUG_LISTENER = null;
				ActionModel.setEnabled( "lastTransform", true );
				ActionModel.setEnabled( "transformFromXSLT", true );
				ActionModel.setEnabled( "xsltdebug1", true );
				ActionModel.setEnabled( "xsltdebug3", false );
				ActionModel.setEnabled( "xsltdebug4", false );
				ActionModel.setEnabled( "xsltdebug5", false );				
			}
		}

		public void run2() {
			ErrorListener xsltAction = ( ErrorListener )ActionModel.restoreAction( 
				"transformWithXSLT" 
			);
			XSLTAction.applyTransformation( 
				container, 
				true, 
				true, 
				false, 
				xsltAction 
			);
		}
	}

}
