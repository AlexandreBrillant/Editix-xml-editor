package com.japisoft.xmlpad.helper;

import com.japisoft.dtdparser.*;
import com.japisoft.framework.job.JobManager;
import com.japisoft.framework.job.KnownJob;
import com.japisoft.framework.xml.SchemaLocator;
import com.japisoft.framework.xml.parser.document.Document;
import com.japisoft.framework.xml.parser.node.FPNode;
import com.japisoft.xmlpad.Debug;
import com.japisoft.xmlpad.SharedProperties;
import com.japisoft.xmlpad.editor.XMLEditor;
import com.japisoft.xmlpad.error.ErrorListener;
import com.japisoft.xmlpad.error.ErrorManager;
import com.japisoft.xmlpad.helper.handler.AbstractHelperHandler;
import com.japisoft.xmlpad.helper.handler.relaxng.RelaxNGTagHelperHandler;
import com.japisoft.xmlpad.helper.handler.schema.AbstractEntityHandler;
import com.japisoft.xmlpad.helper.handler.schema.AbstractTagHandler;
import com.japisoft.xmlpad.helper.handler.schema.AttributeHandler;
import com.japisoft.xmlpad.helper.handler.schema.AttributeValueHandler;
import com.japisoft.xmlpad.helper.handler.schema.dtd.DTDEntityHandler;
import com.japisoft.xmlpad.helper.handler.schema.dtd.DTDTagHandler;
import com.japisoft.xmlpad.helper.handler.schema.w3c.W3cTagHandler;
import com.japisoft.xmlpad.helper.model.AbstractTagHelper;
import com.japisoft.xmlpad.helper.model.TagDescriptor;
import com.japisoft.xmlpad.helper.model.TagHelper;
import com.japisoft.xmlpad.toolkit.XMLFileData;
import com.japisoft.xmlpad.toolkit.XMLToolkit;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import javax.swing.DefaultListModel;

import org.xml.sax.EntityResolver;
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
public class SchemaHelperManager implements ErrorListener {
	public static final String SCHEMA_ENTITIES = "schema-entities";
	public static final String SCHEMA_ATTRIBUTES = "schema-attributes";
	public static final String SCHEMA_ATTRIBUTE_VALUES = "schema-attribute-values";
	public static final String SCHEMA_ELEMENTS = "schema-elements";

	public static final String ENTITY_TYPE = "entity";

	private ErrorManager errorManager;
	private HelperManager helperManager;

	public SchemaHelperManager(ErrorManager manager, HelperManager hm) {
		this.errorManager = manager;
		this.helperManager = hm;
	}

	public HelperManager getHelperManager() {
		return helperManager;
	}
	
	private boolean hasError = false;

	public void initErrorProcessing() {
		hasError = false;
	}

	public void notifyError(Object context,boolean localError, String sourceLocation,
			int line, int col, int offset, String message, boolean onTheFly) {
		hasError = true;
	}

	public void notifyNoError(boolean onTheFly) {
		hasError = false;
	}

	public void stopErrorProcessing() {
		helperManager.setEnabled(
				SCHEMA_ELEMENTS, !hasError );
		helperManager.setEnabled(
				SCHEMA_ATTRIBUTES, !hasError );
		helperManager.setEnabled(
				SCHEMA_ATTRIBUTE_VALUES, !hasError );
	}

	public void dispose() {
		uninstallSchemaHandlers();
		errorManager = null;
		helperManager = null;
	}

	private void uninstallSchemaHandlers() {
		if ( helperManager == null )
			return;	// ??
		helperManager.removeHelperHandler(SCHEMA_ELEMENTS);
		helperManager.removeHelperHandler(SCHEMA_ATTRIBUTES);
		helperManager.removeHelperHandler(SCHEMA_ATTRIBUTE_VALUES);
		helperManager.removeHelperHandler(SCHEMA_ENTITIES);
	}

	private AbstractHelperHandler[] installTagHelperHandler(AbstractTagHandler handler) {
		AbstractHelperHandler a1, a2;
		if ( helperManager == null )
			return new AbstractHelperHandler[] {};	// ??
		helperManager.addHelperHandler(handler);
		helperManager.addHelperHandler( a1 = new AttributeHandler(handler));
		helperManager.addHelperHandler( a2 = new AttributeValueHandler(handler));
		return new AbstractHelperHandler[] { 
				handler, 
				a1, 
				a2 };
	}

	private void installEntitiesHandler(AbstractEntityHandler handler) {
		helperManager.addHelperHandler(handler);
	}

	/** Reset the current RelaxNGLocation */
	public void setRelaxNGLocation(SchemaLocator locator) {
		
		uninstallSchemaHandlers();
		
		if ( locator == null ) {
			return;
		}

		com.japisoft.framework.xml.parser.FPParser p = new com.japisoft.framework.xml.parser.FPParser();

		try {
			Document d = p.parse(locator.getReader());
			installTagHelperHandler(
					new RelaxNGTagHelperHandler( ( FPNode )d.getRoot() ) );
				
		} catch ( Throwable th ) {
			errorManager.notifyUniqueError(true, null, 0, 0, 0,
					"Can't load the RelaxNG Schema", false);
		}
	}

	/** Reset the current Schema location */
	public void setSchemaLocation(String currentDocument, String[] namespaces,
			String[] locations, String rootElement, int documentLineForLocation, EntityResolver resolver ) {
		
		uninstallSchemaHandlers();
		
		if ( locations == null ) {
			return;
		}

		ArrayList thh = new ArrayList();

		for (int i = 0; i < locations.length; i++) {

			String location = locations[i];

			try {

				if ( resolver == null )
					resolver = SharedProperties.DEFAULT_ENTITY_RESOLVER;
				
				SchemaLocator locator = new SchemaLocator(
						currentDocument,
						location,
						resolver );

				// Wrong encoding management !
				// Reader reader = locator.getReader();

				XMLFileData data = 
					XMLToolkit.getContentFromInputStream( 
						locator.getInputStream(), null );

				com.japisoft.framework.xml.parser.FPParser p = new com.japisoft.framework.xml.parser.FPParser();
				Document d = p.parse(new StringReader(data.getContent()));
				
				installTagHelperHandler(
						new W3cTagHandler(rootElement,
						((FPNode) d.getRoot()), locator.getSource() ) );

			} catch (Throwable th) {

				String tmp = "?";
				if (locations != null && locations.length > 0)
					tmp = locations[0];

				errorManager.notifyUniqueError(true, null, 0, 0, 0,
						"Can't load the W3C XML Schema " + tmp, false);

			}

		}

	}

	/** Add support for completion using the DTD content */
	public void setDTDContent(String rootElement, String dtdContent,
			int dtdDeclarationLine) {
		StringReader reader = new StringReader(dtdContent);
		SchemaLocator schemaLocator = new SchemaLocator(reader);
		schemaLocator.schemaDeclarationLine = dtdDeclarationLine;
		setDTD(rootElement, schemaLocator);
	}

	/** Reset the local DTD */
	public void setDTDContent(String rootElement, SchemaLocator locator ) {
		setDTD(rootElement, locator);
	}

	/** Reset the current DTD location */
	public void setDTDLocation(String currentDocument, String location,
			String rootElement, int dtdDeclarationLine) {
		
		uninstallSchemaHandlers();
		
		if (location == null) {
			return;
		}

		try {
			if (DTDMapperFactory.getDTDMapper() != null) {
				InputStream input = DTDMapperFactory.getDTDMapper().getStream(
						location);
				if (input == null) {
					SchemaLocator locator = new SchemaLocator(currentDocument,
							location);
					locator.schemaDeclarationLine = dtdDeclarationLine;
					setDTD(rootElement, locator);
				} else {
					SchemaLocator locator = new SchemaLocator(input, location);
					locator.schemaDeclarationLine = dtdDeclarationLine;
					setDTD(rootElement, locator);
				}
			} else {
				SchemaLocator locator = new SchemaLocator(currentDocument,
						location);
				locator.schemaDeclarationLine = dtdDeclarationLine;
				setDTD(rootElement, locator);
			}
		} catch (Throwable th) {
			Debug.debug(th);
			errorManager.notifyUniqueError(true, null, 0, 0, 0,
					"Can't load the DTD " + location, false);
		}
	}

	private void setDTD(String rootElement, SchemaLocator input) {
		
		String source = input.getSource();

		if (input != null) {
			try {
				JobManager.addJob(new DTDParsingJob(rootElement, input));
			} catch (Throwable th) {
			}
		}
	}

	private String forcePrefix = null;

	public void setForcePrefix(String prefix) {
		forcePrefix = prefix;
	}

	private String defaultNamespace = null;

	/** Reset a default namespace for the content assistant */
	public void setDefaultNamespace(String namespace) {
		this.defaultNamespace = namespace;
	}

	private String externalDTDCommentFile = null;
	
	/** Special case for specifying an external DTD file comment. Shouldn't be used */
	public void setExternalDTDCommentFile( String comment ) {
		this.externalDTDCommentFile = comment;
	}

	// ////////////////////////////////////////////////////////////////////////////////////

	class DTDParsingJob implements KnownJob {

		private String rootElement = null;
		
		private SchemaLocator input = null;

		public DTDParsingJob(String rootElement, SchemaLocator input) {
			this.rootElement = rootElement;
			this.input = input;
			//new RuntimeException().printStackTrace();
		}

		public Object getSource() {
			return SchemaHelperManager.this;
		}

		public boolean hasErrors() {
			return false;
		}		
		
		public void dispose() {
			rootElement = null;
			input = null;
		}

		public boolean isAlone() {
			return false;
		}

		public String getErrorMessage() {
			return null;
		}

		public void run() {
						
			try {

				try {

					DTDParser p = new DTDParser();
					boolean parsed = false;

					if ( externalDTDCommentFile != null ) {
						try {
							p.parseExternalDTDComment( new URL( externalDTDCommentFile ) );
						} catch (MalformedURLException e) {
							Debug.debug( e );
						} catch( RuntimeException re ) {
							Debug.debug( re );
						}
					}

					if (input.documentLocation != null
							&& !input.streamProvided()) {

						DTDMapper mapper = DTDMapperFactory.getDTDMapper();
						if (mapper != null) {
							try {
								InputStream resInput = mapper
										.getStream( input.location );
								if (resInput != null) {
									parsed = true;
									p.parse(resInput);
								}
							} catch (IOException exc) {
							}
						}
					}

					if ( !parsed )
						p.parse( input );

					if (rootElement == null)
						rootElement = p.getDTDDocumentBuilder()
								.getFirstElement();

					uninstallSchemaHandlers();
					
					installTagHelperHandler(new DTDTagHandler(rootElement, p
							.getDTDElement()));

					installEntitiesHandler(new DTDEntityHandler(p
							.getDTDElement()));

				} catch (Throwable th) {
					Debug.debug(th);
					errorManager.notifyUniqueError(true, null, 0, 0, 0,
							"Can't load the DTD " + input.getSource(), false);
				}

			} finally {
				rootElement = null;
				input = null;
			}
		}

		public void stopIt() {
		}

		public String getName() {
			return "DTD Parsing";
		}
	}

	class TagHelperComposite extends AbstractTagHelper {

		TagHelper[] ath = null;

		TagHelperComposite(TagHelper[] ath) {
			this.ath = ath;
		}

		public void dispose() {
			for (int i = 0; i < ath.length; i++)
				ath[i].dispose();
			super.dispose();
		}

		public String getSource() {
			return ath[0].getSource();
		}

		public TagDescriptor getTag(FPNode node) {
			for (int i = 0; i < ath.length; i++) {
				TagHelper th = ath[i];
				TagDescriptor td = th.getTag(node);
				if (td != null)
					return td;
			}
			return null;
		}

		public TagDescriptor[] getTags() {
			ArrayList res = new ArrayList();

			for (int i = 0; i < ath.length; i++) {
				TagDescriptor[] tds = ath[i].getTags();
				if (tds != null) {
					for (int j = 0; j < tds.length; j++)
						res.add(tds[j]);
				}
			}

			TagDescriptor[] td = new TagDescriptor[res.size()];
			for (int i = 0; i < res.size(); i++)
				td[i] = (TagDescriptor) res.get(i);
			return td;
		}

		public void setEditor(XMLEditor editor) {
			for (int i = 0; i < ath.length; i++)
				ath[i].setEditor(editor);
			super.setEditor(editor);
		}

		public boolean hasElements() {
			for (int i = 0; i < ath.length; i++) {
				if (ath[i] instanceof AbstractTagHelper) {
					AbstractTagHelper ah = (AbstractTagHelper) ath[i];
					if (ah.hasElements())
						return true;
				}
			}
			return false;
		}

		public void fillList(FPNode node, DefaultListModel model) {
			for (int i = 0; i < ath.length; i++) {
				if (ath[i] instanceof AbstractTagHelper) {
					AbstractTagHelper ah = (AbstractTagHelper) ath[i];
					ah.addedSystemTag = false;
					ah.fillList(node, model);
				}
			}
			super.fillList(node, model);
		}

		public void setLocation(FPNode locationPath, int offset) {
			for (int i = 0; i < ath.length; i++) {
				ath[ i ].setLocation( locationPath, offset );
			}
		}
	}

}
