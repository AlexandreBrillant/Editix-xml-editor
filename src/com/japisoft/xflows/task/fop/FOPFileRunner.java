package com.japisoft.xflows.task.fop;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Reader;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.fop.apps.FOPException;
import org.apache.fop.apps.Fop;
import org.apache.fop.apps.FopFactory;
import org.apache.fop.apps.MimeConstants;

import com.japisoft.editix.action.fop.EditixFOPFactory;
import com.japisoft.framework.xml.XSLTTransformer;
import com.japisoft.xflows.task.TaskContext;
import com.japisoft.xflows.task.TaskRunner;

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
public class FOPFileRunner implements TaskRunner {

	public boolean run(TaskContext context) {

		File source = context.getCurrentSourceFile();
		File output = context.getCurrentTargetFile();
		String render = context.getParam(FOPUI.OUTPUT_TYPE);

		OutputStream out = null;
		try {
			out = new FileOutputStream(output);
		} catch (Throwable th) {
			context.addError("Can't write to " + output);
			return ERROR;
		}

		try {

			FopFactory fopfactory = null;
			
			try {
				fopfactory = EditixFOPFactory.newInstance( source.toURL() );
			} catch( Exception e ) {
				context.addError("FOP Error : Can't initialize fop, " + e.getMessage());
				return ERROR;				
			}

			/*
			// Support for relative image
			try {
				fopfactory.setBaseURL( 
						source.getParentFile().toURI().toURL().toExternalForm() 
				);
			} catch( Exception exc ) {
			}
			*/
			
			Fop fop = null;

			try {
				if ("PDF".equals(render)) {
					fop = fopfactory.newFop(MimeConstants.MIME_PDF, out);
				} else if ("PRINT".equals(render)) {
					fop = fopfactory.newFop(MimeConstants.MIME_FOP_PRINT);
				} else if ("PCL".equals(render)) {
					fop = fopfactory.newFop(MimeConstants.MIME_PCL, out);
				} else if ("PS".equals(render)) {
					fop = fopfactory.newFop(MimeConstants.MIME_POSTSCRIPT, out);
				} else if ("TXT".equals(render)) {
					fop = fopfactory.newFop(MimeConstants.MIME_PLAIN_TEXT, out);
				} else if ("SVG".equals(render)) {
					fop = fopfactory.newFop(MimeConstants.MIME_SVG, out);
				} else if ("RTF".equals(render)) {
					fop = fopfactory.newFop(MimeConstants.MIME_RTF, out);
				}
			} catch (FOPException e) {
				context.addError("FOP Error : " + e.getMessage());
				return ERROR;
			}

			try {
				Reader in = new FileReader(source);

				try {

					// Setup JAXP using identity transformer

					TransformerFactory factory = XSLTTransformer
							.getTransformerFactory();

					try {
						factory.setAttribute(
								org.apache.xalan.processor.TransformerFactoryImpl.FEATURE_SOURCE_LOCATION,
								Boolean.TRUE);
					} catch (Throwable exc) {
					}

					Transformer transformer = factory.newTransformer(); // identity
					// transformer

					// Setup input stream
					Source src = new StreamSource(in);

					// Resulting SAX events (the generated FO) must be piped
					// through to FOP
					Result res = new SAXResult(fop.getDefaultHandler());

					// Start XSLT transformation and FOP processing
					transformer.transform(src, res);

				} finally {
					in.close();
				}

			} catch (FileNotFoundException e) {
				context.addError(e.getMessage());
				return ERROR;
			} catch (IOException e) {
				context.addError(e.getMessage());
				return ERROR;
			} catch (TransformerConfigurationException e) {
				context.addError(e.getMessage());
				return ERROR;
			} catch (FOPException e) {
				context.addError(e.getMessage());
				return ERROR;
			} catch (TransformerException e) {
				context.addError(e.getMessage());
				return ERROR;
			} catch( Exception e ) {
				context.addError(e.getMessage());
				return ERROR;				
			}

		} finally {
			try {
				out.close();
			} catch (IOException e) {
			}
		}
		return OK;
	}

}
