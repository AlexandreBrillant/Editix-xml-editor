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

package com.japisoft.framework.xml.parser;

import java.io.*;

/**
 * Non synchronized BufferedOutputStream
 * @author Alexandre Brillant (https://github.com/AlexandreBrillant/Editix-xml-editor)
 * @version 1.0
 */
class FastBufferedOutputStream extends FilterOutputStream {

    protected byte buf[];
    protected int count;

    public FastBufferedOutputStream(OutputStream out) {
	this(out, 4096);
    }

    FastBufferedOutputStream(OutputStream out, int size) {
	super(out);
	buf = new byte[size];
    }

    private void flushBuffer() throws IOException {
        if (count > 0) {
	    out.write(buf, 0, count);
	    count = 0;
        }
    }

    public void write(int b) throws IOException {
	if (count >= buf.length) {
	    flushBuffer();
	}
	buf[count++] = (byte)b;
    }

    public void write(byte b[], int off, int len) throws IOException {
	if (len >= buf.length) {
	    flushBuffer();
	    out.write(b, off, len);
	    return;
	}
	if (len > buf.length - count) {
	    flushBuffer();
	}
	System.arraycopy(b, off, buf, count, len);
	count += len;
    }

    public void flush() throws IOException {
        flushBuffer();
	out.flush();
    }

}

