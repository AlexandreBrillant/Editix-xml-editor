package com.japisoft.editix.document.xslfo;

import java.io.ByteArrayOutputStream;
import java.io.StringReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.transform.Result;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.fop.apps.Fop;
import org.apache.fop.apps.FopFactory;
import org.apache.fop.apps.MimeConstants;

import com.japisoft.editix.action.fop.EditixFOPFactory;
import com.japisoft.framework.xml.XSLTTransformer;
import com.japisoft.xmlpad.XMLContainer;
import com.japisoft.xmlpad.xml.validator.Validator;

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
public class FOValidator implements Validator {

	public int validate(XMLContainer container, boolean silentMode) {
		
		container.getErrorManager().initErrorProcessing();
		try {
		
			try {
				
				FopFactory factory = EditixFOPFactory.newInstance( container.getCurrentDocumentLocationURL() );
				Fop fop = factory.newFop( MimeConstants.MIME_PLAIN_TEXT, new ByteArrayOutputStream() );
				TransformerFactory factory2 = XSLTTransformer.getTransformerFactory();
				Transformer transformer = factory2.newTransformer(); // identity transformer
				Result res = new SAXResult(fop.getDefaultHandler());
				transformer.transform( new StreamSource( new StringReader( container.getText() )), res);
			} catch( Exception exc ) {
				String message = exc.getMessage();
				
				if ( message != null && !"".equals( message ) ) {
					Pattern p = Pattern.compile( "position\\s(\\d+):(\\d+)" );
					Matcher m = p.matcher( message );			
					int row = -1;
					int col = -1;

					if ( m.find() ) {
						row = Integer.parseInt( m.group( 1 ) );
						col = Integer.parseInt( m.group( 2 ) );
					}
	
					container.getErrorManager().notifyError(
						this,
						false,
						container.getCurrentDocumentLocation(),
						row,
						col,
						-1,
						message,
						false );	
				}

				return ERROR;
			}
			return OK;
			
		} finally {
			container.getErrorManager().stopErrorProcessing();
		}
	}

}
