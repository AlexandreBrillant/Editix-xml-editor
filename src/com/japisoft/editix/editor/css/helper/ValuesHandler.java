// Editix XML Editor
// https://www.editix.com
// Copyright (c) 2025 Alexandre Brillant
// 
// For non-commercial usage :
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
// For commercial use or integration into proprietary software :
// A commercial license is required. Visit https://www.editix.com for details.

package com.japisoft.editix.editor.css.helper;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.JColorChooser;
import javax.swing.text.BadLocationException;

import com.japisoft.framework.xml.parser.node.FPNode;
import com.japisoft.xmlpad.editor.XMLPadDocument;
import com.japisoft.xmlpad.helper.handler.AbstractHelperHandler;
import com.japisoft.xmlpad.helper.model.BasicDescriptor;

public class ValuesHandler extends AbstractHelperHandler {

	static String[] htmlColors = null;
	
	static {

		htmlColors = new String[] {
			"#000000	Black",
			"#646D7E	Light Steel Blue4",
			"#6D7B8D	Light Slate Gray",
			"#4C787E	Cadet Blue4",
			"#4C7D7E	Dark Slate Gray4",
			"#806D7E	Thistle4",
			"#5E5A80	Medium Slate Blue",
			"#4E387E	Medium Purple4",
			"#151B54	Midnight Blue",
			"#2B3856	Dark Slate Blue",
			"#25383C	Dark Slate Gray",
			"#463E41	Dim Gray",
			"#151B8D	Cornflower Blue",
			"#15317E	Royal Blue4",
			"#342D7E	Slate Blue4",
			"#2B60DE	Royal Blue",
			"#306EFF	Royal Blue1",
			"#2B65EC	Royal Blue2",
			"#2554C7	Royal Blue3",
			"#3BB9FF	Deep Sky Blue",
			"#38ACEC	Deep Sky Blue2",
			"#3574EC7	Slate Blue",
			"#3090C7	Deep Sky Blue3",
			"#25587E	Deep Sky Blue4",
			"#1589FF	Dodger Blue",
			"#157DEC	Dodger Blue2",
			"#1569C7	Dodger Blue3",
			"#153E7E	Dodger Blue4",
			"#2B547E	Steel Blue4",
			"#4863A0	Steel Blue",
			"#6960EC	Slate Blue2",
			"#8D38C9	Violet",
			"#7A5DC7	Medium Purple3",
			"#8467D7	Medium Purple",
			"#9172EC	Medium Purple2",
			"#9E7BFF	Medium Purple1",
			"#728FCE	Light Steel Blue",
			"#488AC7	Steel Blue3",
			"#56A5EC	Steel Blue2",
			"#5CB3FF	Steel Blue1",
			"#659EC7	Sky Blue3",
			"#41627E	Sky Blue4",
			"#737CA1	Slate Blue",
			"#737CA1	Slate Blue",
			"#98AFC7	Slate Gray3",
			"#F6358A	Violet Red",
			"#F6358A	Violet Red1",
			"#E4317F	Violet Red2",
			"#F52887	Deep Pink",
			"#E4287C	Deep Pink2",
			"#C12267	Deep Pink3",
			"#7D053F	Deep Pink4",
			"#CA226B	Medium Violet Red",
			"#C12869	Violet Red3",
			"#800517	Firebrick",
			"#7D0541	Violet Red4",
			"#7D0552	Maroon4",
			"#810541	Maroon",
			"#C12283	Maroon3",
			"#E3319D	Maroon2",
			"#F535AA	Maroon1",
			"#FF00FF	Magenta",
			"#F433FF	Magenta1",
			"#E238EC	Magenta2",
			"#C031C7	Magenta3",
			"#B048B5	Medium Orchid",
			"#D462FF	Medium Orchid1",
			"#C45AEC	Medium Orchid2",
			"#A74AC7	Medium Orchid3",
			"#6A287E	Medium Orchid4",
			"#8E35EF	Purple",
			"#893BFF	Purple1",
			"#7F38EC	Purple2",
			"#6C2DC7	Purple3",
			"#461B7E	Purple4",
			"#571B7e	Dark Orchid4",
			"#7D1B7E	Dark Orchid",
			"#842DCE	Dark Violet",
			"#8B31C7	Dark Orchid3",
			"#A23BEC	Dark Orchid2",
			"#B041FF	Dark Orchid1",
			"#7E587E	Plum4",
			"#D16587	Pale Violet Red",
			"#F778A1	Pale Violet Red1",
			"#E56E94	Pale Violet Red2",
			"#C25A7C	Pale Violet Red3",
			"#7E354D	Pale Violet Red4",
			"#B93B8F	Plum",
			"#F9B7FF	Plum1",
			"#E6A9EC	Plum2",
			"#C38EC7	Plum3",
			"#D2B9D3	Thistle",
			"#C6AEC7	Thistle3",
			"#EBDDE2	Lavender Blush2",
			"#C8BBBE	Lavender Blush3",
			"#E9CFEC	Thistle2",
			"#FCDFFF	Thistle1",
			"#E3E4FA	Lavender",
			"#FDEEF4	Lavender Blush",
			"#C6DEFF	Light Steel Blue1",
			"#ADDFFF	Light Blue",
			"#BDEDFF	Light Blue1",
			"#E0FFFF	Light Cyan",
			"#C2DFFF	Slate Gray1",
			"#B4CFEC	Slate Gray2",
			"#B7CEEC	Light Steel Blue2",
			"#52F3FF	Turquoise1",
			"#00FFFF	Cyan",
			"#57FEFF	Cyan1",
			"#50EBEC	Cyan2",
			"#4EE2EC	Turquoise2",
			"#48CCCD	Medium Turquoise",
			"#43C6DB	Turquoise",
			"#9AFEFF	Dark Slate Gray1",
			"#8EEBEC	Dark slate Gray2",
			"#78c7c7	Dark Slate Gray3",
			"#46C7C7	Cyan3",
			"#43BFC7	Turquoise3",
			"#77BFC7	Cadet Blue3",
			"#92C7C7	Pale Turquoise3",
			"#AFDCEC	Light Blue2",
			"#3B9C9C	Dark Turquoise",
			"#307D7E	Cyan4",
			"#3EA99F	Light Sea Green",
			"#82CAFA	Light Sky Blue",
			"#A0CFEC	Light Sky Blue2",
			"#87AFC7	Light Sky Blue3",
			"#82CAFF	Sky Blue",
			"#79BAEC	Sky Blue2",
			"#566D7E	Light Sky Blue4",
			"#6698FF	Sky Blue",
			"#736AFF	Light Slate Blue",
			"#CFECEC	Light Cyan2",
			"#AFC7C7	Light Cyan3",
			"#717D7D	Light Cyan4",
			"#95B9C7	Light Blue3",
			"#5E767E	Light Blue4",
			"#5E7D7E	Pale Turquoise4",
			"#617C58	Dark Sea Green4",
			"#348781	Medium Aquamarine",
			"#306754	Medium Sea Green",
			"#4E8975	Sea Green",
			"#254117	Dark Green",
			"#387C44	Sea Green4",
			"#4E9258	Forest Green",
			"#347235	Medium Forest Green",
			"#347C2C	Spring Green4",
			"#667C26	Dark Olive Green4",
			"#437C17	Chartreuse4",
			"#347C17	Green4",
			"#348017	Medium Spring Green",
			"#4AA02C	Spring Green",
			"#41A317	Lime Green",
			"#4AA02C	Spring Green",
			"#8BB381	Dark Sea Green",
			"#99C68E	Dark Sea Green3",
			"#4CC417	Green3",
			"#6CC417	Chartreuse3",
			"#52D017	Yellow Green",
			"#4CC552	Spring Green3",
			"#54C571	Sea Green3",
			"#57E964	Spring Green2",
			"#5EFB6E	Spring Green1",
			"#64E986	Sea Green2",
			"#6AFB92	Sea Green1",
			"#B5EAAA	Dark Sea Green2",
			"#C3FDB8	Dark Sea Green1",
			"#00FF00	Green",
			"#87F717	Lawn Green",
			"#5FFB17	Green1",
			"#59E817	Green2",
			"#7FE817	Chartreuse2",
			"#8AFB17	Chartreuse",
			"#B1FB17	Green Yellow",
			"#CCFB5D	Dark Olive Green1",
			"#BCE954	Dark Olive Green2",
			"#A0C544	Dark Olive Green3",
			"#FFFF00	Yellow",
			"#FFFC17	Yellow1",
			"#FFF380	Khaki1",
			"#EDE275	Khaki2",
			"#EDDA74	Goldenrod",
			"#EAC117	Gold2",
			"#FDD017	Gold1",
			"#FBB917	Goldenrod1",
			"#E9AB17	Goldenrod2",
			"#D4A017	Gold",
			"#C7A317	Gold3",
			"#C68E17	Goldenrod3",
			"#AF7817	Dark Goldenrod",
			"#ADA96E	Khaki",
			"#C9BE62	Khaki3",
			"#827839	Khaki4",
			"#FBB117	Dark Goldenrod1",
			"#E8A317	Dark Goldenrod2",
			"#C58917	Dark Goldenrod3",
			"#F87431	Sienna1",
			"#E66C2C	Sienna2",
			"#F88017	Dark Orange",
			"#F87217	Dark Orange1",
			"#E56717	Dark Orange2",
			"#C35617	Dark Orange3",
			"#C35817	Sienna3",
			"#8A4117	Sienna",
			"#7E3517	Sienna4",
			"#7E2217	Indian Red4",
			"#7E3117	Dark Orange3",
			"#7E3817	Salmon4",
			"#7F5217	Dark Goldenrod4",
			"#806517	Gold4",
			"#805817	Goldenrod4",
			"#7F462C	Light Salmon4",
			"#C85A17	Chocolate",
			"#C34A2C	Coral3",
			"#E55B3C	Coral2",
			"#F76541	Coral",
			"#E18B6B	Dark Salmon",
			"#F88158	Pale Turquoise4",
			"#E67451	Salmon2",
			"#C36241	Salmon3",
			"#C47451	Light Salmon3",
			"#E78A61	Light Salmon2",
			"#F9966B	Light Salmon",
			"#EE9A4D	Sandy Brown",
			"#F660AB	Hot Pink",
			"#F665AB	Hot Pink1",
			"#E45E9D	Hot Pink2",
			"#C25283	Hot Pink3",
			"#7D2252	Hot Pink4",
			"#E77471	Light Coral",
			"#F75D59	Indian Red1",
			"#E55451	Indian Red2",
			"#C24641	Indian Red3",
			"#FF0000	Red",
			"#F62217	Red1",
			"#E41B17	Red2",
			"#F62817	Firebrick1",
			"#E42217	Firebrick2",
			"#C11B17	Firebrick3",
			"#FAAFBE	Pink",
			"#FBBBB9	Rosy Brown1",
			"#E8ADAA	Rosy Brown2",
			"#E7A1B0	Pink2",
			"#FAAFBA	Light Pink",
			"#F9A7B0	Light Pink1",
			"#E799A3	Light Pink2",
			"#C48793	Pink3",
			"#C5908E	Rosy Brown3",
			"#B38481	Rosy Brown",
			"#C48189	Light Pink3",
			"#7F5A58	Rosy Brown4",
			"#7F4E52	Light Pink4",
			"#7F525D	Pink4",
			"#817679	Lavendar Blush4",
			"#817339	Light Goldenrod4",
			"#827B60	Lemon Chiffon4",
			"#C9C299	Lemon Chiffon3",
			"#C8B560	Light Goldenrod3",
			"#ECD672	Light Golden2",
			"#ECD872	Light Goldenrod",
			"#FFE87C	Light Goldenrod1",
			"#ECE5B6	Lemon Chiffon2",
			"#FFF8C6	Lemon Chiffon",
			"#FAF8CC	Light Goldenrod Yellow",
			"#150517	Gray0",
			"#250517	Gray18",
			"#2B1B17	Gray21",
			"#302217	Gray23",
			"#302226	Gray24",
			"#342826	Gray25",
			"#34282C	Gray26",
			"#382D2C	Gray27",
			"#3b3131	Gray28",
			"#3E3535	Gray29",
			"#413839	Gray30",
			"#41383C	Gray31",
			"#463E3F	Gray32",
			"#4A4344	Gray34",
			"#4C4646	Gray35",
			"#4E4848	Gray36",
			"#504A4B	Gray37",
			"#544E4F	Gray38",
			"#565051	Gray39",
			"#595454	Gray40",
			"#5C5858	Gray41",
			"#5F5A59	Gray42",
			"#625D5D	Gray43",
			"#646060	Gray44",
			"#666362	Gray45",
			"#696565	Gray46",
			"#6D6968	Gray47",
			"#6E6A6B	Gray48",
			"#726E6D	Gray49",
			"#747170	Gray50",
			"#736F6E	Gray",
			"#616D7E	Slate Gray4",
			"#657383	Slate Gray",			
		};
	}

	protected String getActivatorSequence() {
		return null;
	}

	public String getTitle() {
		return "CSS Values";
	}

	public boolean haveDescriptors(FPNode currentNode,
			XMLPadDocument document, boolean insertBefore, int offset,
			String activatorString) {
		
		if ( ":".equals( activatorString ) || 
				activatorString == null )
			return true;

		return false;
	}

	protected void installDescriptors(
		FPNode currentNode,
		XMLPadDocument document, int offset,
		String activatorString) {
		String previousWord = getPreviousWord( document, offset - 1 );

		String val = null;
		boolean colorMode = false;
		
		if ( "font-weight".equals( previousWord ) ) {

			val = "normal | bold | bolder | lighter | 100 | 200 | 300 | 400 | 500 | 600 | 700 | 800 | 900 | inherit";

		} else
		if ( "font-stretch".equals( previousWord ) ) {
			
			val = "normal | wider | narrower | ultra-condensed | extra-condensed | condensed | semi-condensed | semi-expanded | expanded | extra-expanded | ultra-expanded | inherit";
			
		} else
		if ( "font-style".equals( previousWord ) ) {
			
			val = "all | normal | italic | oblique"; 			
			
		} else
		if ( "font-family".equals( previousWord ) ) {
			
			val = "Arial, Helvetica, sans-serif|\"Times New Roman\", Times, serif|\"Courier New\", Courier, monospace|Georgia, \"Times New Roman\", Times, serif|Verdana, Arial, Helvetica, sans-serif|Geneva, Arial, Helvetica, sans-serif";
			
		} else
		if ( "text-align".equals( previousWord ) ) {
			
			val = "left | right | center | justify";
			
		} else
		if ( "vertical-align".equals( previousWord ) ) {
			
			val = "baseline | sub | super | top | text-top | middle | bottom | text-bottom";
			
		} else
		if ( "text-transform".equals( previousWord ) ) {
			
			val = "none | capitalize | uppercase | lowercase";
			
		} else
		if ( previousWord.startsWith( "border-" ) && previousWord.endsWith( "-style" ) ) {
			
			val = "none | dotted | dashed | solid | double | groove | ridge | inset | outset";
			
		} else
		if ( "float".equals( previousWord ) ) {
			
			val = "left | right | none";
			
		} else 
		if ( "clear".equals( previousWord ) ) {
			
			val = "none | left | right | both";
			
		} else
		if ( "list-style-type".equals( previousWord ) ) {
			
			val = "disc | circle | square | decimal | lower-roman | upper-roman | lower-alpha | upper-alpha | none";
			
		} else
		if ( "list-style-position".equals( previousWord ) ) {
			
			val = "inside | outside";
			
		} else
		if ( "display".equals( previousWord ) ) {
			
			val = "block | inline | list-item | none";
			
		} else
		if ( "color".equals( previousWord ) || previousWord.endsWith( "-color" ) ) {
			
			colorMode = true;
			
		}

		if ( val != null ) {
			String[] str = val.split( "\\|" );
			for ( int i = 0; i < str.length; i++ ) {
	
				BasicDescriptor rd = new BasicDescriptor( str[ i ].trim() );
				rd.setRawContent( activatorString + str[ i ].trim() + ";" );
				rd.setAddedPart( activatorString );
				addDescriptor( rd );

			}		
		} else
			if ( colorMode ) {
				
				BasicDescriptor rd = ( BasicDescriptor )addDescriptor( 
						new BasicDescriptor( "Other..." ) );

				if ( ca != null && ca.lastColor != null )
					rd.setIcon( new ColorIcon( ca.lastColor ) );
				else
					rd.setIcon( new ColorIcon( Color.WHITE ) );
				
				if ( ca == null )
					ca = new ColorAction( activatorString );
				else
					ca.activatorString = activatorString;
				
				rd.setSpecificAction( ca );
				rd.setAddedPart( activatorString );

				for ( int i = 0; i < htmlColors.length; i++ ) {
					String c = htmlColors[ i ];
					String color = c.substring( 1, 7 );
					Color cc = new Color( Integer.parseInt( color, 16 ) );
					rd = ( BasicDescriptor )addDescriptor( new BasicDescriptor( c.substring( 7 ).trim().toLowerCase() ) );
					rd.setRawContent(activatorString + c.substring( 7 ).trim().toLowerCase() + ";" );
					rd.setIcon( new ColorIcon( cc ) );
					rd.setAddedPart( activatorString );					
				}

			}
	}

	private String getPreviousWord( XMLPadDocument document, int offset ) {
		StringBuffer sb = new StringBuffer();
		boolean flip = false;
		try {
			for ( int i = offset; i >= 0; i-- ) {
				char c = document.getText( i, 1 ).charAt( 0 );
				
				if ( c == ':' )	// Skip it
					continue;
				
				if ( c == '{' )
					break;

				if ( !Character.isWhitespace( c ) ) {
					sb.insert( 0,  c );
					flip = true;
				} else
					if ( flip )
						break;

			}
		} catch (BadLocationException e) {
		}
		return sb.toString().toLowerCase();
	}

	private ColorAction ca = null;
	
	class ColorIcon implements Icon {

		private Color c;
		
		ColorIcon( Color c ) {
			this.c = c;
		}
		
		public int getIconHeight() {
			return 16;
		}

		public int getIconWidth() {
			return 16;
		}

		public void paintIcon(Component c, Graphics g, int x, int y) {
			g.setColor( this.c );
			g.fillRect( 0, 0, getIconWidth(), getIconHeight() );
			g.setColor( Color.LIGHT_GRAY );
			g.draw3DRect( 0, 0, getIconWidth(), getIconHeight(), true );
		}
		
	}
	
	class ColorAction extends AbstractAction {

		private Color lastColor = null;
		String activatorString = null;
		
		ColorAction( String activatorString ) {
			this.activatorString = activatorString;
		}
		
		public void actionPerformed(ActionEvent e) {

			lastColor = JColorChooser.showDialog( null, "CSS color", lastColor );

			if ( lastColor != null ) {

				int r = lastColor.getRed();
				int g = lastColor.getGreen();
				int b = lastColor.getBlue();
				
				String rs = Integer.toHexString( r );
				if ( rs.length() == 1 )
					rs = "0" + rs;
				String gs = Integer.toHexString( g );
				if ( gs.length() == 1 )
					gs = "0" + gs;
				String bs = Integer.toHexString( b );
				if ( bs.length() == 1 )
					bs = "0" + bs;

				String res = rs + gs + bs;

				BasicDescriptor rd = new BasicDescriptor( "#" + res );
				rd.setRawContent(":#" + res + ";" );
				putValue( "descriptor", rd );
				rd.setAddedPart( activatorString );

			} else {

				BasicDescriptor rd = new BasicDescriptor( "" );
				rd.setAddedPart( activatorString );
				putValue( "descriptor", rd );

			}

		}		

	}

}

