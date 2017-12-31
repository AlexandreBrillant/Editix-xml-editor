package com.japisoft.xmlpad;

import java.awt.Color;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Vector;

import javax.swing.Icon;
import javax.swing.filechooser.FileFilter;

import com.japisoft.xmlpad.helper.handler.AbstractHelperHandler;
import com.japisoft.xmlpad.helper.model.SystemHelper;
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
public class XMLDocumentInfo {
	
	/** Check if the type name is a text type */ 
	public static boolean isTextType( String name ) {
		return name.equals( "DTD" ) || 
					name.equals( "XQR" ) || 
						name.equals( "TEXT" ) || 
							name.equals( "CSS" );
	}

	private String defaultDocument;

	/** Reset the default document. This document is used
	 * bu default for initializing the editor for the new action
	 * If no default document is specified a inner default one is
	 * used using the com.japisoft.xmlpad.bean.XMLTemplate value
	 * @param doc
	 */
	public void setDefaultDocument(String doc) {
		this.defaultDocument = doc;
	}

	/** @return the default document */
	public String getDefaultDocument() {
		return defaultDocument;
	}

	private boolean systemDocument = false;

	/** Special flag for multiple document usage */	
	public void setSystemDocument( boolean system ) {
		this.systemDocument = system;
	}

	public boolean isSystemDocument() {
		return systemDocument;
	}

	/** Reset parameters for template. A parameter is a "${param}" value. The value from
	 * the "param" key will be replaced in the current template usage
	 * @param params Set of parameters : name & value
	 */
	public void setParamValues(HashMap params) {
		this.params = params;
	}

	/** For template usage */
	public void setParam( String param, String value ) {
		if ( params == null )
			params = new HashMap();
		params.put( param, value );
	}

	private HashMap params;

	/** @return a parameter value. If the parameter is unknwon an empty string is returned */
	public String getParamValue(String paramName) {
		if ("date".equals(paramName))
			return (new Date()).toString();

		if (params == null)
			return "";
		String _ = (String) params.get(paramName);
		if (_ == null)
			return "";
		return _;
	}

	private Vector fileExts;

	/** @return <code>true</code> if this document matched this file extention */
	public boolean matchFileExt( String ext ) {
		if ( ext.equalsIgnoreCase( defaultFileExt ) )
			return true;
		if ( fileExts == null )
			return false;
		for ( int i = 0; i < fileExts.size(); i++ )
			if ( ext.equals( fileExts.get( i ) ) )
				return true;
		return false; 	
	}
	
	/** Add a file extension for loading/saving this document type */
	public void addFileExt(String ext) {
		if (fileExts == null)
			fileExts = new Vector();
		fileExts.add(ext.toLowerCase());
	}

	/** Remove this file extension for loading/saving this document type */
	public void removeFileExt(String ext) {
		if (fileExts == null)
			return;
		fileExts.remove(ext);
	}

	private String defaultFileExt;

	/** Reset the default file ext. If will be used to complete any
	 * file name without extention while loading/saving a file
	 * @param ext Default file extension
	 */
	public void setDefaultFileExt(String ext) {
		if ( ext != null )
			ext = ext.toLowerCase();
		this.defaultFileExt = ext;
	}

	/** @return the default file extention. If no default file ext has been
	 * specified, then 'xml' is returned
	 */
	public String getDefaultFileExt() {
		if (defaultFileExt == null)
			return "xml";
		return defaultFileExt;
	}
	
	static String[] DEFAULT_FILE_EXT = new String[] { "xml" };

	static String DEFAULT_DESCRIPTION = "XML file";

	/** @return set of file extension for loading/saving this document type */
	public String[] getSupportedFileExt() {
		if (fileExts == null)
			return DEFAULT_FILE_EXT;
		String[] r = new String[fileExts.size()];
		fileExts.copyInto(r);
		return r;
	}
	
	/** Reset a set of file extension for this document type */
	public void setSupportedFileExt( String[] content ) {
		if ( content.length == 0 ) 
			fileExts = null;
		else {
			fileExts = new Vector();
			for ( int i = 0; i < content.length; i++ )
				fileExts.add( content[ i ] );
		}
	}
	
	private FileFilter lastFileFilter;
	
	/** @return a file chooser filter for this document. It will use
	 * the file ext data 
	 */
	public FileFilter getFileFilter() {
		if ( lastFileFilter == null )
			lastFileFilter = new XMLDocumentFileFilter(
					getDocumentDescription(), 
					getType(), 
					getSupportedFileExt(), 
					getDefaultFileExt() );
		return lastFileFilter;
	}
	
	private String documentDescription;
	
	/** Reset the document description. This description will be used
	 * inside file dialog box for filtering document */
	public void setDocumentDescription(String description) {
		this.documentDescription = description;
	}

	/** @return the document description */
	public String getDocumentDescription() {
		if (documentDescription == null)
			return DEFAULT_DESCRIPTION;
		return documentDescription;
	}

	private String defaultSchemaLocation;
	private String defaultSchemaRoot;

	/** Reset the default schema root and location for syntax completion */
	public void setDefaultSchema(String root, String location) {
		this.defaultSchemaLocation = location;
		this.defaultSchemaRoot = root;
	}

	/** @return the default schema URL location */
	public String getDefaultSchemaLocation() {
		return defaultSchemaLocation;
	}

	/** @return the default schema root tag */
	public String getDefaultSchemaRoot() {
		return defaultSchemaRoot;
	}

	private String defaultDTDLocation;
	private String defaultDTDRoot;
	private URL defaultDTD;

	public void setDefaultDTD( String root, URL location ) {
		this.defaultDTDRoot = root;
		this.defaultDTD = location;
	}

	/** Reset the defaultDTD location for syntax completion */
	public void setDefaultDTD(String root, String location) {
		// Detect it from the classpath
		if ( location != null ) {
			if ( location.indexOf( ":/" ) == -1 ) {
				try {
					URL url = ClassLoader.getSystemClassLoader().getResource( location );
					Debug.debug( "Test location from classpath " + location );
					if ( url != null ) {
						setOriginalDTDLocation( location );
						location = url.toExternalForm();
						// For Windows !
						location = location.replaceAll( "%20", " " );
						
						if ( location.startsWith( "file:" ) ) {
							// Local path
							location = location.substring( 5 );
							// For linux/macosX case, keep the first '/'
							if ( location.indexOf( ":" ) > -1 && location.startsWith( "/" ) )
								location = location.substring( 1 );
						}
						
						Debug.debug( "Find " + location );
					} else {
						Debug.debug( "Can't find " + location );
					}
				} catch( Throwable th ) {
					System.err.println( "ClassLoader not allowed for defaultDTD" );
				}
			}
		}
		this.defaultDTDLocation = location;
		this.defaultDTDRoot = root;
	}

	public String originalDTDLocation = null;

	/** Particular case when a mapping is done with the DTD location */
	public void setOriginalDTDLocation( String dtd ) {
		this.originalDTDLocation = dtd;
	}

	/** Particular case when a mapping is done with the DTD location */
	public String getOriginalDTDLocation() {
		if ( originalDTDLocation != null ) {
			return originalDTDLocation;
		}
		return getDefaultDTDLocation();
	}

	/** @return the default DTD location for syntax completion */
	public String getDefaultDTDLocation() {
		return defaultDTDLocation;
	}

	/** @return a stream with the default DTD content */
	public URL getDefaultDTDURL() {
		return defaultDTD;
	}

	/** @return the default DTD root for syntax completion */
	public String getDefaultDTDRoot() {
		return defaultDTDRoot;
	}

	private String workingDirectory;

	/** Reset the default working directory. It will be used by
	 * file dialog for initializing
	 * @param workingDirectory
	 */
	public void setWorkingDirectory(String workingDirectory) {
		this.workingDirectory = workingDirectory;
	}

	/** @return the default working directory */
	public String getWorkingDirectory() {
		return workingDirectory;
	}

	private String currentDocumentLocation;

	/** Reset the current document location */
	public void setCurrentDocumentLocation(String location) {
		this.currentDocumentLocation = location;
	}

	/** @return the current document location */
	public String getCurrentDocumentLocation() {
		return currentDocumentLocation;
	}

	private long currentDocumentModifiedDate;
	
	/** @return the last modified date */
	public long getCurrentDocumentModifiedDate() {
		return currentDocumentModifiedDate;
	}

	/** Reset the current document modified date */
	public void setCurrentDocumentModifiedDate( long cdm ) {
		currentDocumentModifiedDate = cdm;
	}

	/** @return the current document file name or null */
	public String getCurrentDocumentFileName() {
		if ( currentDocumentLocation == null )
			return null;
		int i = currentDocumentLocation.lastIndexOf( "/" );
		if ( i == -1 )
			i = currentDocumentLocation.lastIndexOf( "\\" );
		if ( i == -1 )
			return currentDocumentLocation;
		else
			return currentDocumentLocation.substring( i + 1 );
	}

	private Object currentDocumentLocationArg;

	/** Reset an optional argument for the document location */
	public void setCurrentDocumentLocationArg( Object arg ) {
		this.currentDocumentLocationArg = arg;
	}
	
	/** @return the current document location optional argument */
	public Object getCurrentDocumentLocationArg() {
		return currentDocumentLocationArg;
	}

	/** @return a document shortcut name */
	public String getDocumentName() {
		if (currentDocumentLocation == null) {
			return "NewDocument";
		} else {
			int i = currentDocumentLocation.lastIndexOf("/");
			if (i == -1)
				i = currentDocumentLocation.lastIndexOf("\\");
			if (i == -1)
				return currentDocumentLocation;
			return currentDocumentLocation.substring(i + 1);
		}
	}

	private Icon documentIcon;

	/** @return the current document icon */
	public Icon getDocumentIcon() {
		return documentIcon;
	}

	/** Reset the document icon */
	public void setDocumentIcon(Icon icon) {
		this.documentIcon = icon;
	}

	/** Information relative to the current document icon path */
	public void setDocumentIconPath( String iconPath ) {
		this.iconPath = iconPath;
	}

	/** @return information relative to the current document icon path */
	public String getDocumentIconPath() {
		return iconPath;
	}

	private String iconPath = null;

	private String documentTemplate;

	/** Set a template for this document type */
	public void setTemplate(String documentTemplate) {
		this.documentTemplate = documentTemplate;
	}

	/** @return a template for this document */
	public String getTemplate() {
		return documentTemplate;
	}

	private String id;

	/** Give an identifier */
	public void setId(String id) {
		this.id = id;
	}

	/** @return the document identifier */
	public String getId() {
		return id;
	}

	private String sysHelper = null;
	private SystemHelper helperResolved = null;

	/** @return a system helper for this document. This helper is provided
	 * thanks to the sysHelper property */
	public SystemHelper getSystemHelper() {
		if ( helperResolved != null )
			return helperResolved;
		if ( sysHelper == null )
			return null;
		try {
			return ( helperResolved = ( SystemHelper )Class.forName( sysHelper ).newInstance() );
		} catch( Throwable th ) {
			return null;
		}
	}

	/** @return a class name for the system helper */
	public String getSystemHelperClass() {
		return sysHelper;
	}

	/** Reset a class name for the system helper */
	public void setSystemHelperClass( String helper ) {
		this.sysHelper = helper;
	}

	private boolean treeAvailable = true;

	/** The tree will be available for this document */
	public void setTreeAvailable( boolean tree ) {
		this.treeAvailable = tree;
	}

	/** @return true if the <code>tree</code> is available. By default to <code>true</code> */
	public boolean isTreeAvailable() {
		return treeAvailable;
	}

	private boolean realTimeTree = true;

	/** The real time tree will be available : By default <code>true</code> */
	public void setRealTimeTree( boolean realTimeTree ) {
		this.realTimeTree = realTimeTree;
	}

	/** @return <code>true</code> if the real time tree is available for this document */
	public boolean isRealTimeTree() {
		return realTimeTree;
	}

	private boolean syntaxColor = true;

	/** @return <code>true</code> if an XML syntax color is available */
	public boolean hasSyntaxColor() {
		return syntaxColor;
	}

	/** @param syntaxColor define if a XML syntax color is available. By default <code>true</code> */
	public void setSyntaxColor( boolean syntaxColor ) {
		this.syntaxColor = syntaxColor;
	}

	private boolean dtdMode = false;

	
	private boolean autoClosing = true;
	
	/** @return <code>true</code> if the auto tag closing is available */
	public boolean hasAutoClosing() {
		return autoClosing;
	}

	private boolean syntaxHelper = true;
	
	/** @return <code>true</code> if the syntax helper is available */
	public boolean hasSyntaxHelper() {
		return syntaxHelper;
	}

	/** Reset the syntax helper. By default to <code>true</code> */
	public void setSyntaxHelper( boolean sh ) {
		this.syntaxHelper = sh;
	}

	/** Set the auto tag closing feature. By default to <code>true</code> */
	public void setAutoClosing( boolean autoClosing ) {
		this.autoClosing = autoClosing;
	}

	private String type;

	/** Reset a known type for this document */	
	public void setType( String type ) {
		this.type = type;
	}
	
	private String metaType;
	
	public void setMetaType( String metaType ) {
		this.metaType = metaType;
	}

	public boolean isTEXT() {
		return "TEXT".equalsIgnoreCase( metaType );
	}
	
	public boolean isXML() {
		return "XML".equalsIgnoreCase( metaType ) || metaType == null;
	}
	
	public boolean isHTML() {
		return "HTML".equalsIgnoreCase( type ) || "HTML".equalsIgnoreCase( metaType );
	}

	/** @return the current document type, like XML, DTD... */
	public String getType() {
		return type;
	}
	
	private String parentType;
	
	/** Reset a parent type for this document */	
	public void setParentType( String parentType ) {
		this.parentType = parentType;
	}

	public String getParentType() {
		return parentType;
	}

	private ArrayList mappers;
	/**
	 * Set mappers for searching action (matching template for XSLT as sample)
	 * @param mappers */
	public void setMappers( ArrayList mappers ) {
		this.mappers = mappers;
	}
	public ArrayList getMappers() {
		return this.mappers;
	}

	private ArrayList handlers;
	private boolean assistantAppend = false;
	
	/** Set specific handlers for the content assistant 
	 *  @param handlers List of handlers
	 *  @param appendMode maintain or replace the current assistants */ 
	public void setHelperHandlers( ArrayList handlers, boolean appendMode ) {
		this.handlers = handlers;
		this.assistantAppend = appendMode;
	}
	
	/** Only when providing a set of handlers with the <code>setHelperHandlers</code> method */
	public boolean isAssistantAppendMode() {
		return assistantAppend;
	}
	
	/** @return specific handlers for the content assistant */ 
	public ArrayList getHelperHandlers() {
		return handlers;
	}

	/** Add a new handler */
	public void addHelperHandler( AbstractHelperHandler handler ) {
		if ( handlers == null )
			handlers = new ArrayList();
		handlers.add( handler );
	}

	private String encoding = null;

	/** Update the document encoding */
	public void setEncoding( String encoding ) {
		this.encoding = encoding;
	}

	/** Read the current document encoding */
	public String getEncoding() {
		return encoding;
	}

	private boolean defaultAssistant = true;
	
	/** Specify if a default assistant is required like the comment assistant */
	public void setDefaultAssistant( boolean assistant ) {
		this.defaultAssistant = assistant;
	}
	
	public boolean hasDefaultAssistant() {
		return defaultAssistant;
	}
	
	/** @return another XMLDocumentInfo instance from this one */
	public XMLDocumentInfo cloneDocument() {
		XMLDocumentInfo doc2 = new XMLDocumentInfo();

		if ( handlers != null )
			doc2.handlers = ( ArrayList )handlers.clone();

		if ( htColorForPrefix != null )
			doc2.htColorForPrefix = ( HashMap )htColorForPrefix.clone();

		if ( htBackgroundColorForPrefix != null )
			doc2.htBackgroundColorForPrefix = ( HashMap )htBackgroundColorForPrefix.clone();

		if ( htColorForAttribute != null )
			doc2.htColorForAttribute = ( HashMap )htColorForAttribute.clone();

		if  (htColorForTag != null )
			doc2.htColorForTag = ( HashMap )htColorForTag.clone();

		doc2.encoding = encoding;
		doc2.assistantAppend = assistantAppend;
		doc2.listOfAttributesWithAutoAssistant = listOfAttributesWithAutoAssistant;
		doc2.currentDocumentLocation = currentDocumentLocation;
		doc2.defaultDocument = defaultDocument;
		doc2.originalDTDLocation = originalDTDLocation;
		doc2.defaultDTDLocation = defaultDTDLocation;
		doc2.defaultDTDRoot = defaultDTDRoot;
		doc2.defaultFileExt = defaultFileExt;
		doc2.defaultSchemaLocation = defaultSchemaLocation;
		doc2.defaultSchemaRoot = defaultSchemaRoot;
		doc2.documentDescription = documentDescription;
		doc2.documentIcon = documentIcon;
		doc2.documentTemplate = documentTemplate;
		doc2.fileExts = fileExts;
		doc2.iconPath = iconPath;
		doc2.fileExts = fileExts;
		doc2.workingDirectory = workingDirectory;
		doc2.id = id;
		doc2.sysHelper = sysHelper;
		doc2.type = type;
		doc2.autoClosing = autoClosing;
		doc2.treeAvailable = treeAvailable;
		doc2.realTimeTree = realTimeTree;
		doc2.syntaxColor = syntaxColor;
		doc2.syntaxHelper = syntaxHelper;
		doc2.namespace = namespace;
		doc2.parentType = parentType;
		doc2.dtdMode = dtdMode;
		doc2.schemaXSDValid = schemaXSDValid;
		doc2.schemaXSDNSValid = schemaXSDNSValid;
		doc2.selectFirstTag = selectFirstTag;
		doc2.dtdExternalCommentURL = dtdExternalCommentURL;
		doc2.properties = properties;
		doc2.schemaRNGValid = schemaRNGValid;
		doc2.defaultAssistant = defaultAssistant;
		doc2.customValidator = customValidator;
		doc2.mappers = mappers;
		doc2.metaType = metaType;
		
		if ( params != null ) {
			doc2.params = ( HashMap )params.clone();
		}

		return doc2;
	}

	public String getDefaultNamespacePrefix() {
		if ( "XSD".equals( getType() ) )
			return "xsd"; 
		return null;
	}

	private String namespace;

	public void setDefaultNamespace( String namespace ) {
		this.namespace = namespace;
	}

	public String getDefaultNamespace() { 
		return namespace; 
	}

	/** For DTD coloration */
	public boolean isDtdMode() {
		return dtdMode;
	}
	/** For DTD coloration */
	public void setDtdMode(boolean dtdMode) {
		this.dtdMode = dtdMode;
	}

	private URL schemaRNGValid;

	/** Force a RELAXNG schema validation with this document url */
	public void setSchemaRNGValid( URL schema ) {
		this.schemaRNGValid = schema;
	}
	
	public URL getSchemaRNGValid() {
		return schemaRNGValid;
	}

	private URL schemaXSDValid;

	/** Force a W3C schema validation with this document url */
	public void setSchemaXSDValid( URL schema ) {
		this.schemaXSDValid = schema;
	}
	
	public URL getSchemaXSDValid() {
		return schemaXSDValid;
	}
	
	private String schemaXSDNSValid;

	/** Force a W3C schema validation with this document namespace */
	public void setSchemaXSDNSValid( String schemaNS ) {
		this.schemaXSDNSValid = schemaNS;
	}
	
	public String getSchemaXSDNSValid() {
		return schemaXSDNSValid;
	}

	private boolean selectFirstTag = true;
	
	/** When opening a new document the first element is chosen automatically */
	public void setSelectFirstTagAfterReading( boolean select ) {
		this.selectFirstTag = select;
	}

	/** When opening a new document the first element is chosen automatically */	
	public boolean isSelectFirstTagAfterReading() {
		return selectFirstTag;
	}

	private String[] listOfAttributesWithAutoAssistant;

	/** Particular usage for asking an assistant automatically when adding an attribute */
	public void setListOfAttributesWithAutoAssistant( String[] atts ) {
		this.listOfAttributesWithAutoAssistant = atts;
	}
	
	public String[] getListOfAttributesWithAutoAssistant() {
		return listOfAttributesWithAutoAssistant;
	}

	private String dtdExternalCommentURL;

	/** Special file for using external comment to a dtd. Should not be used */
	public void setDTDExternalCommentFile( String url ) {
		this.dtdExternalCommentURL = url;
	}
	
	/** Special file for using external comment to a dtd. Should not be used */
	public String getDTDExternalCommentFile() {
		return dtdExternalCommentURL;
	}

	// -------------------------------------------------------------

	public HashMap htColorForPrefix = null;
	
	/** Choose a particular color for a tag prefix. Use the color <code>null</code> for removing it */
	public void setColorForPrefix( String prefixName, Color c ) {
		if ( htColorForPrefix == null )
			htColorForPrefix = new HashMap();
		if ( c == null )
			htColorForPrefix.remove( prefixName );
		else
			htColorForPrefix.put( prefixName, c );
	}

	/** @return a custom color for this prefix name */
	public Color getColorForPrefix( String prefixName ) {
		if ( htColorForPrefix == null )
			return null;
		return ( Color )htColorForPrefix.get( prefixName );
	}

	/** @return <code>true</code> if a color exist for this prefixName */
	public boolean hasColorForPrefix( String prefixName ) {
		if ( htColorForPrefix == null )
			return false;
		return htColorForPrefix.containsKey( prefixName );
	}

	public HashMap htBackgroundColorForPrefix = null;

	/** Choose a particular background color for a tag prefix. Use the color <code>null</code> for removing it */
	public void setBackgroundColorForPrefix( String prefixName, Color c ) {
		if ( htBackgroundColorForPrefix == null )
			htBackgroundColorForPrefix = new HashMap();
		if ( c == null )
			htBackgroundColorForPrefix.remove( prefixName );
		else
			htBackgroundColorForPrefix.put( prefixName, c );
	}

	/** @return a custom background color for this prefix name */
	public Color getBackgroundColorForPrefix( String prefixName ) {
		if ( htBackgroundColorForPrefix == null )
			return null;
		return ( Color )htBackgroundColorForPrefix.get( prefixName );
	}

	/** @return <code>true</code> if a background color exist for this prefixName */
	public boolean hasBackgroundColorForPrefix( String prefixName ) {
		if ( htBackgroundColorForPrefix == null )
			return false;
		return htBackgroundColorForPrefix.containsKey( prefixName );
	}
	
	private HashMap htColorForTag = null;
	
	/** Define a color for this tagName. Use a <code>null</code> color for removing it */
	public void setColorForTag( String tagName, Color c ) {
		if ( htColorForTag == null )
			htColorForTag = new HashMap();
		if  ( c == null )
			htColorForTag.remove( tagName );
		else
			htColorForTag.put( tagName, c );
	}

	/** @return a custom color for this tagName */ 
	public Color getColorForTag( String tagName ) {
		if ( htColorForTag == null )
			return null;
		return ( Color )htColorForTag.get( tagName );
	}

	/** @return <code>true</code> if a custom color exists for this tagName */
	public boolean hasColorForTag( String tagName ) {
		if ( htColorForTag == null )
			return false;
		return htColorForTag.containsKey( tagName );
	}
	
	private HashMap htColorForAttribute = null;

	/** Choose a particular color for an attribute. Use the color <code>null</code> for removing it */
	public void setColorForAttribute( String attributeName, Color c ) {
		if ( htColorForAttribute == null )
			htColorForAttribute = new HashMap();
		if ( c == null )
			htColorForAttribute.remove( attributeName );
		else
			htColorForAttribute.put( attributeName, c );
	}

	/** @return the user custom color for this attribute */
	public Color getColorForAttribute( String attributeName ) {
		if ( htColorForAttribute == null )
			return null;
		return ( Color )htColorForAttribute.get( attributeName );
	}

	/** @return <code>true</code> if this attribute has a custom color */ 
	public boolean hasColorForAttribute( String attributeName ) {
		if ( htColorForAttribute == null )
			return false;
		return htColorForAttribute.containsKey( attributeName );
	}

	private HashMap properties;

	public void setProperty( String name, Object value ) {
		if ( properties == null )
			properties = new HashMap();
		properties.put( name, value );
	}

	public Object getProperty( String name ) {
		if ( properties != null )
			return properties.get( name );
		return null;
	}

	private Validator customValidator = null;

	/** Set a custom XML validator when parsing the document */
	public void setCustomValidator( Validator validator ) {
		this.customValidator = validator;
	}

	/** @return a custom XML validator */
	public Validator getCustomValidator() {
		return customValidator;
	}

	/** @return <code>true</code> is a tag can be auto close */
	public boolean isLegalAutoClose(String tagName) {
		return true;
	}

}
