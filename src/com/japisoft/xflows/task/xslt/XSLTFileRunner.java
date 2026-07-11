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

package com.japisoft.xflows.task.xslt;

import java.io.File;
import java.net.UnknownHostException;
import java.util.Iterator;

import javax.xml.transform.ErrorListener;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.xml.sax.SAXException;

import com.japisoft.editix.action.xsl.StreamSourceFactory;
import com.japisoft.editix.action.xsl.result.StreamResultFactory;
import com.japisoft.framework.xml.XSLTTransformer;
import com.japisoft.xflows.XFlowsApplicationModel;
import com.japisoft.xflows.task.TaskContext;
import com.japisoft.xflows.task.TaskParams;
import com.japisoft.xflows.task.TaskRunner;

/**
 * @author Alexandre Brillant (https://github.com/AlexandreBrillant/Editix-xml-editor)
 * @version 1.0
 */
public class XSLTFileRunner implements TaskRunner, ErrorListener {

	private TaskContext context = null;

	public void error(TransformerException exception)
			throws TransformerException {
		if (context != null)
			context.addError(exception.getMessageAndLocation());
	}

	public void fatalError(TransformerException exception)
			throws TransformerException {
		if (context != null)
			context.addError(exception.getMessageAndLocation());
	}

	public void warning(TransformerException exception)
			throws TransformerException {
		if (context != null)
			context.addWarning(exception.getMessageAndLocation());
	}

	public boolean run(TaskContext context) {
		this.context = context;
		try {
			String fstylesheet = context.getParam(XSLTUI.STYLESHEET);
			context.addInfo("Transforming " + context.getCurrentSourceFile());
			return applyTransformation(this, context, context
					.getCurrentSourceFile(), fstylesheet, context
					.getCurrentTargetFile());
		} finally {
			this.context = null;
		}
	}

	public static boolean applyTransformation(ErrorListener listener,
			TaskContext context, File data, String xslt, File res) {

		String version = "1.0";
		if (context.getParam(XSLTUI.STYLESHEET_VERSION) != null)
			version = context.getParam(XSLTUI.STYLESHEET_VERSION);

		double iversion = 1;

		try {
			iversion = Double.parseDouble(version);
		} catch (NumberFormatException exc) {
		}

		javax.xml.transform.TransformerFactory tFactory = null;

		if (iversion == 2.0) {
			tFactory = new net.sf.saxon.TransformerFactoryImpl();
		} else
			tFactory = XSLTTransformer.getTransformerFactory();

		/*
		try {
			tFactory.setAttribute(
					org.apache.xalan.processor.TransformerFactoryImpl.FEATURE_SOURCE_LOCATION,
					Boolean.TRUE);
		} catch (Throwable exc) {
		}

		try {
			tFactory.setAttribute(FeatureKeys.LINE_NUMBERING, Boolean.TRUE);
		} catch (Throwable exc) {
		}
		*/

		if (listener != null)
			tFactory.setErrorListener(listener);

		String media = null, title = null, charset = null;

		try {
			Transformer transformer = null;
			try {

				String encoding = XFlowsApplicationModel
						.getCurrentFileEncoding();
				if ( "AUTOMATIC".equals( encoding ) )
					charset = null;
				else
					charset = encoding;

				javax.xml.transform.Source stylesheet = null;

				StreamSource xsltSource = null;
				if (xslt.indexOf("://") > -1)
					xsltSource = new StreamSource(xslt);
				else
					xsltSource = new StreamSource(new File(xslt));
				
				try {
					stylesheet = tFactory.getAssociatedStylesheet(xsltSource,
							media, title, charset);
				} catch (TransformerConfigurationException exc) {
					// Ignore it like for SAXON
				}

				if (stylesheet == null) {
					transformer = tFactory
							.newTransformer( xsltSource );
				} else
					transformer = tFactory.newTransformer(stylesheet);

			} catch (TransformerException exc1) {
				if (xslt == null) {
					return ERROR;
				} else {

					SAXException exc2 = null;
					Throwable thTmp = null;
					thTmp = exc1.getException();
					while (thTmp != null) {
						if (thTmp instanceof SAXException) {
							exc2 = (SAXException) thTmp;
							break;
						} else if (thTmp instanceof TransformerException) {
							thTmp = ((TransformerException) thTmp)
									.getException();
						} else
							break;
					}

					if (exc2 != null) {
						if (exc2.getException() instanceof TransformerException) {
							exc1 = (TransformerException) exc2.getException();
						}
					}

					String message = exc1.getMessage();
					int loc = 0;
					if (exc1.getLocator() != null)
						loc = exc1.getLocator().getLineNumber();

					if ("true".equals(System.getProperty("xflows.debug"))) {
						exc1.printStackTrace();
					}

					context.addError(message + " at line " + loc);

					return ERROR;
				}
			}

			// Apply parameters

			Iterator it = context.getParams().getParams();
			while (it.hasNext()) {
				String name = (String) it.next();
				String value = context.getParams().getParamValue(name);
				int type = context.getParams().getParamType(name);
				if (type == TaskParams.XSLTPARAMS) {
					name = name.substring(6);
					transformer.setParameter(name, value);
				}
			}

			/*
			transformer.transform(new javax.xml.transform.stream.StreamSource(
					com.japisoft.framework.app.toolkit.Toolkit
							.getReaderForFile(data, charset)),
					new StreamResult(com.japisoft.framework.app.toolkit.Toolkit
							.getWriterForFile(res, charset)));
			*/

			String resTmp = res.toString();
			
			StreamSource source = StreamSourceFactory.Instance().getStreamSource( data.toString() );
			StreamResult result = StreamResultFactory.instance().streamResult( (int)iversion, resTmp );
			
			transformer.transform( source, result );

			StreamResultFactory.instance().endProcess( resTmp );

		} catch (TransformerException ex) {
			if ("true".equals(System.getProperty("xflows.debug")))
				ex.printStackTrace();
			String message = ex.getMessage();
			Throwable th = ex;
			while (true) {
				if (th.getCause() == null)
					break;
				th = th.getCause();
			}

			if (th instanceof UnknownHostException) {
				message = "Can't connect to " + th.getMessage();
			} else
				message = th.getMessage();

			if (ex.getLocator() != null) {
				context.addError(message + " at line "
						+ ex.getLocator().getLineNumber());
			} else {
				context.addError(message);
			}
			res.delete();
			return ERROR;
		} catch (Throwable th) {
			context.addError(th.getMessage());
			res.delete();
			return ERROR;
		}
		return OK;
	}

}