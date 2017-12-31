package com.japisoft.editix.document;

import java.io.File;

import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Icon;

import com.japisoft.editix.main.EditixApplicationModel;

import com.japisoft.framework.xml.XMLToolkit;
import com.japisoft.framework.xml.parser.FPParser;
import com.japisoft.framework.xml.parser.node.FPNode;
import com.japisoft.xmlpad.XMLDocumentInfo;

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
public class TemplateModel {

	private static final String TEMPLATE_USER = "template/user/";
	private static final String DEFAULT_ENCODING = "UTF-8";
	private static List<GroupTemplate> model = null;

	static {
		loadModel();
	}

	public static void loadModel() {
		model = null;
		InputStream input = 
			ClassLoader.getSystemResourceAsStream( "templates.xml" );
		if (input == null) {
			System.err.println( "Can't find templates.xml" );
			System.exit( 1 );
		}
		try {
			loadModel( new InputStreamReader( input, DEFAULT_ENCODING ) );
			// Search from the user path
			File file = getCustomTemplatesPath();
			if ( file.exists() ) {
				try {
					loadModel(
						new InputStreamReader(
							new FileInputStream( file ), DEFAULT_ENCODING )
					);
				} catch( Throwable th ) {
					th.printStackTrace();
				}
			}
		} catch( Exception exc ) {
			System.err.println( "Can't load templates.xml : " + exc.getMessage() );
		}
	}

	private static File getCustomTemplatesPath() {
		File userPath = EditixApplicationModel.getAppUserPath();
		File file = new File( userPath, "templates.xml" );
		return file;
	}

	static GroupTemplate getUserGroupTemplate() {
		for ( int i = 0; i < getGroupTemplateCount(); i++ ) {
			if ( "User".equalsIgnoreCase( getGroupTemplate( i ).getName() ) )
				return getGroupTemplate( i );
		}
		return new GroupTemplate( 
				"User", 
				"images/text.png", 
				"images/document_plain.png" 
		);
	}

	public static void loadModel( Reader input ) {

		FPParser p = new FPParser();

		try {
			FPNode sn = ( FPNode )p.parse(input).getRoot();

			if ( model == null )
				model = new ArrayList<GroupTemplate>();

			for ( int i = 0; i < sn.childCount(); i++ ) {
				
				FPNode c = sn.childAt( i );
				
				if ( c.matchContent( "group" ) ) {
					
					GroupTemplate gt = buildGroupTemplate( c ); 
					
					model.add( gt );
				}
			}

			GroupTemplate user = getUserGroupTemplate();

			// Old user template
			
			for ( int i = 0; i < sn.childCount(); i++ ) {
				if ( sn.childAt( i ).matchContent( "template" ) ) {
					TemplateInfo ti = buildTemplateInfo( sn.childAt( i ) );
					ti.icon = getGroupIconByType( ti.type );
					user.addTemplate( ti );
				}
			}
			
			// New user template
			
			File users = getUserTemplates();
			String[] content = users.list();
			if ( content != null ) {
				for ( String c : content ) {
					int i = c.lastIndexOf( "." );
					if ( i > -1 ) {
						String templateName = c.substring( 0, i );
						String type = c.substring( i + 1 );
						TemplateInfo ti = new TemplateInfo();
						ti.label = templateName;
						ti.type = type;
						ti.location = TEMPLATE_USER + c;
						ti.icon = getGroupIconByType( type );
						user.addTemplate( ti );
					}
				}
			}

		} catch ( Exception exc ) {
			System.err.println( "Can't parse templates.xml : "
					+ exc.getMessage() + " !");
		}
	}

	static GroupTemplate buildGroupTemplate( FPNode node ) {
		GroupTemplate gt = new GroupTemplate( 
			node.getAttribute( "label" ), 
			node.getAttribute( "icon" ), 
			node.getAttribute( "docIcon" ) 
		);
		for ( int i = 0; i < node.childCount(); i++ ) {
			FPNode sn = node.childAt( i );
			if ( sn.matchContent( "template" ) )
				gt.addTemplate( buildTemplateInfo( sn ) );
		}
		return gt;
	}
	
	static Icon getGroupIconByType( String type ) {
		for ( int i = 0; i < getGroupTemplateCount(); i++ ) {
			GroupTemplate gt = getGroupTemplate( i );
			for ( int j = 0; j < gt.getTemplateInfoCount(); j++ ) {
				if ( type.equalsIgnoreCase( gt.getTemplateInfo( j ).type ) ) {
					return gt.getDocIcon();
				}
			}
		}
		return null;
	}

	static TemplateInfo buildTemplateInfo( FPNode node ) {
		TemplateInfo ti = new TemplateInfo();
		ti.label = node.getAttribute( "label" );
		ti.location = node.getAttribute( "location" );
		ti.type = node.getAttribute( "type" );
		ti.system = "true".equals( node.getAttribute( "system" ) );
		ti.encoding = node.getAttribute( "encoding" );
		ti.defDTDLocation = node.getAttribute( "defDTDLocation" );
		ti.defDTDRoot = node.getAttribute( "defDTDRoot" );
		ti.help = node.getAttribute( "help" );

		XMLDocumentInfo info = DocumentModel.getDocumentForType( ti.type );
		if ( info != null )
			ti.icon = info.getDocumentIcon();

		return ti;
	}
	
	public static File getTemplatePath( String location ) {
		File userPath = EditixApplicationModel.getAppUserPath();
		File f = new File( userPath, location );
		if ( !f.getParentFile().exists() )
			f.getParentFile().mkdirs();
		return f;
	}
	
	public static File getUserTemplates() {
		File users = EditixApplicationModel.getAppUserPath();
		File templates = new File( users, TEMPLATE_USER );
		if ( !templates.exists() )
			templates.mkdirs();
		return templates;
	}

	public static File createNewTemplate( String name, String type, String content ) throws Exception {
		File templates = getUserTemplates();
		name = name.replace( " ", "_" );
		File template = new File( templates, name + "." + type );
		XMLToolkit.save( template, content );
		TemplateInfo ti = new TemplateInfo();
		ti.label = name;
		ti.type = type;
		ti.location = TEMPLATE_USER + name + "." + type;
		ti.icon = getGroupIconByType( type );
		getUserGroupTemplate().addTemplate( ti );
		return template;
	}

	public static void resolveTemplate( 
			String encoding, 
			String location, 
			XMLDocumentInfo info ) throws Throwable {

		File userPath = EditixApplicationModel.getAppUserPath();
		File f = new File( userPath, location );

		InputStream input = null;
		if ( f.exists() ) {
			input = new FileInputStream( f );
		} else {
			f = new File( location );
			if ( f.exists() ) {
				input = new FileInputStream( f );
			} else {			
				input = ClassLoader.getSystemResourceAsStream( location );
				if ( input == null ) {
					URL url = new URL( location );
					input = url.openStream();
				}
			}
		}
		info.setTemplate( 
				com.japisoft.framework.xml.XMLToolkit.getContentFromInputStream( 
						input, encoding ).getContent() );
	}
	
	public static int getGroupTemplateCount() {
		if ( model == null )
			return 0;
		return model.size();
	}

	public static GroupTemplate getGroupTemplate( int index ) {
		if ( model == null )
			return null;
		return model.get( index );
	}

}
