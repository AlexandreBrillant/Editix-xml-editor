package com.japisoft.editix.action.xsl;

import net.sf.saxon.event.PipelineConfiguration;
import net.sf.saxon.event.Receiver;
import net.sf.saxon.om.NamespaceBinding;
import net.sf.saxon.om.NodeName;
import net.sf.saxon.trans.XPathException;
import net.sf.saxon.type.SchemaType;
import net.sf.saxon.type.SimpleType;

// XSLT 2 output message
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
public class XSLT2MessageReceiver implements Receiver, MessageReceiver {

	private StringBuffer result = null;
	
	@Override
	public String getSystemId() {
		return null;
	}

	@Override
	public void attribute(NodeName arg0, SimpleType arg1, CharSequence arg2, int arg3, int arg4) throws XPathException {
	}

	@Override
	public void characters(CharSequence arg0, int arg1, int arg2) throws XPathException {
		// if ( storeIt ) {
		if ( result == null )
			result = new StringBuffer();
		if ( result.length() > 0 )
			result.append( "\n" );
		result.append( arg0 );
		// }
	}

	public String getResult() {
		if ( result == null )
			return "";
		return result.toString();
	}
	
	@Override
	public void close() throws XPathException {
	}

	@Override
	public void comment(CharSequence arg0, int arg1, int arg2) throws XPathException {
	}

	@Override
	public void endDocument() throws XPathException {
	}

	@Override
	public void endElement() throws XPathException {
	}

	@Override
	public PipelineConfiguration getPipelineConfiguration() {
		return null;
	}

	@Override
	public void namespace(NamespaceBinding arg0, int arg1) throws XPathException {
	}

	@Override
	public void open() throws XPathException {
		result = new StringBuffer();
	}

	@Override
	public void processingInstruction(String arg0, CharSequence arg1, int arg2, int arg3) throws XPathException {
	}

	@Override
	public void setPipelineConfiguration(PipelineConfiguration arg0) {
	}

	@Override
	public void setSystemId(String arg0) {
	}

	@Override
	public void setUnparsedEntity(String arg0, String arg1, String arg2) throws XPathException {
	}

	@Override
	public void startContent() throws XPathException {
	}

	@Override
	public void startDocument(int arg0) throws XPathException {
		// TODO Auto-generated method stub

	}

	boolean storeIt = false;
	
	@Override
	public void startElement(NodeName arg0, SchemaType arg1, int arg2, int arg3) throws XPathException {
		storeIt = ( "message".equals( arg0.getLocalPart() ) );
	}

	@Override
	public boolean usesTypeAnnotations() {
		// TODO Auto-generated method stub
		return false;
	}

}
