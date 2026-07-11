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

package com.japisoft.xmlpad.bookmark;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.Shape;

import javax.swing.Icon;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import javax.swing.text.Highlighter.HighlightPainter;

import com.japisoft.framework.toolkit.FileToolkit;
import com.japisoft.xmlpad.XMLContainer;
import com.japisoft.xmlpad.editor.XMLPadDocument;

/**
 * Default context for the bookmark manager
 * 
 * @author Alexandre Brillant (https://github.com/AlexandreBrillant/Editix-xml-editor)
 * @version 1.0
 * @see BookmarkContext
 */
public class DefaultBookmarkContext implements BookmarkContext {

	private Icon icon;
	private Color lineColor;

	/**
	 * @param icon
	 *            A small icon for each bookmark
	 * @param Color
	 *            lineColor color for each bookmarked line
	 */
	public DefaultBookmarkContext(Icon icon, Color lineColor) {
		this.icon = icon;
		this.lineColor = lineColor;
	}

	protected void updateLineColor( Color lineColor ) {
		this.lineColor = lineColor;
	}
	
	public Icon getIcon() {
		return icon;
	}

	public Color getColor() {
		return lineColor;
	}
	
	BookmarkModel model = null;

	public BookmarkModel getModel() {
		if (model == null)
			model = new DefaultBookmarkModel();
		return model;
	}

	/** @return <code>true</code> if this line (starting from 0) match one bookmark */
	public boolean matchLine( 
			String uri, 
			int line, 
			XMLContainer container ) {
		if ( model == null )
			return false;
		for ( int i = 0; i < model.getBookmarkCount(); i++ ) {
			BookmarkPosition pos = model.getBookmarkPositionAt( i );
			int offset = pos.getOffset();
			XMLContainer container2 = container;
			if ( pos.getSource() instanceof XMLContainer ) {
				container2 = ( XMLContainer )pos.getSource();
				if ( !FileToolkit.sameFileName( 
						container2.getCurrentDocumentLocation(), uri ) )
					continue;
			}
			XMLPadDocument document = 
				container2.getXMLDocument();
			int line2 = 
				document.getDefaultRootElement().getElementIndex( offset );
			if ( line == line2 )
				return true;
		}
		return false;
	}
	
	private DefaultHighlighter defaultHighlight;
	
	public HighlightPainter getHighlightPainter() {
		if ( defaultHighlight == null )
			defaultHighlight = new DefaultHighlighter();
		return defaultHighlight;
	}
	
	class DefaultHighlighter implements HighlightPainter {

		public void paint(Graphics g, int p0, int p1, Shape bounds,
				JTextComponent textComponent) {

			FontMetrics metrics = g.getFontMetrics();
			Document doc = textComponent.getDocument();
			int lineNo = doc.getDefaultRootElement().getElementIndex(p0);

			Rectangle rect = (Rectangle) bounds;
			int height = metrics.getHeight();
			int x = rect.x;
			int y = rect.y + height * lineNo;
			int width = textComponent.getWidth();

			g.setColor(lineColor);
			g.fillRect(x, y, width, height);

		}

	}

}