package com.japisoft.xmlpad.editor;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.font.LineMetrics;
import java.awt.geom.Rectangle2D;
import java.text.CharacterIterator;

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
public class MinimalFontMetrics extends FontMetrics {

	private MinimalFontMetrics( Font font ) {
		super( font );
	}	

	private static FontMetrics instance = null;
	
	public static FontMetrics getInstance() {
		if ( instance == null )
			instance = new MinimalFontMetrics( new Font("",0,0) );
		return instance;
	}
	
	@Override
	public int bytesWidth(byte[] data, int off, int len) {
		// TODO Auto-generated method stub
		return super.bytesWidth(data, off, len);
	}

	@Override
	public int charsWidth(char[] data, int off, int len) {
		// TODO Auto-generated method stub
return 0;
	}

	@Override
	public int charWidth(char ch) {
		// TODO Auto-generated method stub
		return super.charWidth(ch);
	}

	@Override
	public int charWidth(int codePoint) {
		// TODO Auto-generated method stub
		return super.charWidth(codePoint);
	}

	@Override
	public int getAscent() {
		// TODO Auto-generated method stub
		return super.getAscent();
	}

	@Override
	public int getDescent() {
		// TODO Auto-generated method stub
		return super.getDescent();
	}

	@Override
	public Font getFont() {
		// TODO Auto-generated method stub
		return super.getFont();
	}

	@Override
	public int getHeight() {
		return 1;
	}

	@Override
	public int getLeading() {
		// TODO Auto-generated method stub
		return super.getLeading();
	}

	@Override
	public LineMetrics getLineMetrics(char[] chars, int beginIndex, int limit,
			Graphics context) {
		// TODO Auto-generated method stub
		return super.getLineMetrics(chars, beginIndex, limit, context);
	}

	@Override
	public LineMetrics getLineMetrics(CharacterIterator ci, int beginIndex,
			int limit, Graphics context) {
		// TODO Auto-generated method stub
		return super.getLineMetrics(ci, beginIndex, limit, context);
	}

	@Override
	public LineMetrics getLineMetrics(String str, Graphics context) {
		// TODO Auto-generated method stub
		return super.getLineMetrics(str, context);
	}

	@Override
	public LineMetrics getLineMetrics(String str, int beginIndex, int limit,
			Graphics context) {
		// TODO Auto-generated method stub
		return super.getLineMetrics(str, beginIndex, limit, context);
	}

	@Override
	public int getMaxAdvance() {
		// TODO Auto-generated method stub
		return super.getMaxAdvance();
	}

	@Override
	public int getMaxAscent() {
		// TODO Auto-generated method stub
		return super.getMaxAscent();
	}

	@Override
	public Rectangle2D getMaxCharBounds(Graphics context) {
		// TODO Auto-generated method stub
		return super.getMaxCharBounds(context);
	}

	@Override
	public int getMaxDecent() {
		// TODO Auto-generated method stub
		return super.getMaxDecent();
	}

	@Override
	public int getMaxDescent() {
		// TODO Auto-generated method stub
		return super.getMaxDescent();
	}

	@Override
	public Rectangle2D getStringBounds(char[] chars, int beginIndex, int limit,
			Graphics context) {
		// TODO Auto-generated method stub
		return super.getStringBounds(chars, beginIndex, limit, context);
	}

	@Override
	public Rectangle2D getStringBounds(CharacterIterator ci, int beginIndex,
			int limit, Graphics context) {
		// TODO Auto-generated method stub
		return super.getStringBounds(ci, beginIndex, limit, context);
	}

	@Override
	public Rectangle2D getStringBounds(String str, Graphics context) {
		// TODO Auto-generated method stub
		return super.getStringBounds(str, context);
	}

	@Override
	public Rectangle2D getStringBounds(String str, int beginIndex, int limit,
			Graphics context) {
		// TODO Auto-generated method stub
		return super.getStringBounds(str, beginIndex, limit, context);
	}

	@Override
	public int[] getWidths() {
		// TODO Auto-generated method stub
		return super.getWidths();
	}

	@Override
	public boolean hasUniformLineMetrics() {
		// TODO Auto-generated method stub
		return super.hasUniformLineMetrics();
	}

	@Override
	public int stringWidth(String str) {
		// TODO Auto-generated method stub
		return super.stringWidth(str);
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return super.toString();
	}

	
	
}
