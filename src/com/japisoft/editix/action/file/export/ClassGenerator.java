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

package com.japisoft.editix.action.file.export;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Stack;
import java.util.Map.Entry;

import javax.xml.parsers.SAXParser;

import org.apache.xerces.jaxp.SAXParserFactoryImpl;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

public class ClassGenerator implements ContentHandler {

	private HashMap mapMetaClass = new HashMap();

	public ClassGenerator(String xmlFile, String outputDir, String pack)
			throws Throwable {
		SAXParserFactoryImpl impl = new SAXParserFactoryImpl();
		SAXParser p = impl.newSAXParser();
		p.getXMLReader().setContentHandler(this);
		p.getXMLReader().parse(xmlFile);

		File f = new File(outputDir);
		Iterator itkeys = mapMetaClass.entrySet().iterator();
		while (itkeys.hasNext()) {
			String key = (String) ((Entry) itkeys.next()).getKey();
			JavaMetaClass mc = (JavaMetaClass) mapMetaClass.get(key);
			File _f = new File(outputDir, Tools.toClassName(key) + ".java");
			if (!_f.exists()) {
				PrintWriter pw = new PrintWriter(new FileWriter(_f));
				try {
/*					System.out.println("Process " + Tools.toClassName(key)
							+ ".java"); */
					mc.write(pw, pack);
				} finally {
					pw.close();
				}
			} /* else
				System.out.println("Ignoring " + _f); */
		}
	}

	private JavaMetaClass currentMetaClass = null;

	private Stack stackOfMetaClass = new Stack();

	public void characters(char[] arg0, int arg1, int arg2) throws SAXException {
		// Check for non empty content
		String s = new String(arg0, arg1, arg2);
		for (int i = 0; i < s.length(); i++) {
			if (!Character.isWhitespace(s.charAt(i)))
				currentMetaClass.setText();
		}
	}

	public void startElement(String arg0, String arg1, String arg2,
			Attributes arg3) throws SAXException {

		String localName = Tools.getLocalNameForQName(arg2);

		if (currentMetaClass != null) {
			currentMetaClass.addContentElement(localName);
			stackOfMetaClass.push(currentMetaClass);
		}
		currentMetaClass = (JavaMetaClass) mapMetaClass.get(localName);
		if (currentMetaClass == null) {
			currentMetaClass = new JavaMetaClass(localName);
			mapMetaClass.put(localName, currentMetaClass);
		}
		for (int i = 0; i < arg3.getLength(); i++) {
			currentMetaClass.setAttribute(Tools.getLocalNameForQName(arg3
					.getQName(i)), arg3.getValue(i));
		}
	}

	public void endElement(String arg0, String arg1, String arg2)
			throws SAXException {
		try {
			currentMetaClass = (JavaMetaClass) stackOfMetaClass.pop();
		} catch (RuntimeException e) {
		}
	}

	public void endDocument() throws SAXException {
	}

	public void endPrefixMapping(String arg0) throws SAXException {
	}

	public void ignorableWhitespace(char[] arg0, int arg1, int arg2)
			throws SAXException {
	}

	public void processingInstruction(String arg0, String arg1)
			throws SAXException {
	}

	public void setDocumentLocator(Locator arg0) {
	}

	public void skippedEntity(String arg0) throws SAXException {
	}

	public void startDocument() throws SAXException {
	}

	public void startPrefixMapping(String arg0, String arg1)
			throws SAXException {
	}

	public static void main(String[] args) throws Throwable {
		//ClassGenerator cg = new ClassGenerator("src/multiple_textfield.xdp",
		//		"src/com/crionics/xfa/elements", "com.crionics.xfa.elements");
	}
}
