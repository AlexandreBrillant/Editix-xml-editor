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

package com.japisoft.editix.action.xsl;

import javax.xml.transform.TransformerException;

import org.xml.sax.Attributes;

import com.icl.saxon.output.Emitter;

public class XSLT1MessageReceiver extends Emitter implements MessageReceiver {

	private StringBuffer result = null;
	
	@Override
	public void characters(char[] arg0, int arg1, int arg2) throws TransformerException {
		// TODO Auto-generated method stub

		if ( result == null )
			result = new StringBuffer();
		if ( result.length() > 0 )
			result.append( "\n" );
		result.append( new String( arg0, arg1, arg2 ) );
		
	}

	public String getResult() {
		if ( result == null )
			return "";
		return result.toString();
	}	
	
	@Override
	public void comment(char[] arg0, int arg1, int arg2) throws TransformerException {
		// TODO Auto-generated method stub

	}

	@Override
	public void endDocument() throws TransformerException {
		// TODO Auto-generated method stub

	}

	@Override
	public void endElement(int arg0) throws TransformerException {
		// TODO Auto-generated method stub

	}

	@Override
	public void processingInstruction(String arg0, String arg1) throws TransformerException {
		// TODO Auto-generated method stub

	}

	@Override
	public void startDocument() throws TransformerException {
		// TODO Auto-generated method stub

	}

	@Override
	public void startElement(int arg0, Attributes arg1, int[] arg2, int arg3) throws TransformerException {
		// TODO Auto-generated method stub

	}

}
