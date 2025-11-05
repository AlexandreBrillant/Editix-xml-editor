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

package com.japisoft.framework.xml.parser;

import java.io.*;

/**
 * Non synchronized FastBufferedInputStream
 * @author Alexandre Brillant (https://github.com/AlexandreBrillant/Editix-xml-editor)
 * @version 1.0 */
final class FastBufferedInputStream extends FilterInputStream {

    private static int defaultBufferSize = 4096;

    byte buf[];
    int count;
    int pos;
    int markpos = -1;
    int marklimit;

    public FastBufferedInputStream(InputStream in) {
	this(in, defaultBufferSize);
    }

    FastBufferedInputStream(InputStream in, int size) {
	super(in);
        if (size <= 0) {
            throw new IllegalArgumentException("Buffer size <= 0");
        }
	buf = new byte[size];
    }

    private void fill() throws IOException {
	if (markpos < 0)
	    pos = 0;		/* no mark: throw away the buffer */
	else if (pos >= buf.length)	/* no room left in buffer */
	    if (markpos > 0) {	/* can throw away early part of the buffer */
		int sz = pos - markpos;
		System.arraycopy(buf, markpos, buf, 0, sz);
		pos = sz;
		markpos = 0;
	    } else if (buf.length >= marklimit) {
		markpos = -1;	/* buffer got too big, invalidate mark */
		pos = 0;	/* drop buffer contents */
	    } else {		/* grow buffer */
		int nsz = pos * 2;
		if (nsz > marklimit)
		    nsz = marklimit;
		byte nbuf[] = new byte[nsz];
		System.arraycopy(buf, 0, nbuf, 0, pos);
		buf = nbuf;
	    }
        count = pos;
	int n = in.read(buf, pos, buf.length - pos);
        if (n > 0)
            count = n + pos;
    }

    public int read() throws IOException {
	if (pos >= count) {
	    fill();
	    if (pos >= count)
		return -1;
	}
	return buf[pos++] & 0xff;
    }

    private int read1(byte[] b, int off, int len) throws IOException {
	int avail = count - pos;
	if (avail <= 0) {
	    if (len >= buf.length && markpos < 0) {
		return in.read(b, off, len);
	    }
	    fill();
	    avail = count - pos;
	    if (avail <= 0) return -1;
	}
	int cnt = (avail < len) ? avail : len;
	System.arraycopy(buf, pos, b, off, cnt);
	pos += cnt;
	return cnt;
    }

    public int read(byte b[], int off, int len)
	throws IOException
    {
	if (len == 0) {
	    return 0;
	}

	int n = read1(b, off, len);
	if (n <= 0) return n;
	while ((n < len) && (in.available() > 0)) {
	    int n1 = read1(b, off + n, len - n);
	    if (n1 <= 0) break;
	    n += n1;
	}
	return n;
    }

    public long skip(long n) throws IOException {
	if (n <= 0) {
	    return 0;
	}
	long avail = count - pos;
     
        if (avail <= 0) {
            if (markpos <0) 
                return in.skip(n);
            fill();
            avail = count - pos;
            if (avail <= 0)
                return 0;
        }
        
        long skipped = (avail < n) ? avail : n;
        pos += skipped;
        return skipped;
    }

    public int available() throws IOException {
	return (count - pos) + in.available();
    }

    public void mark(int readlimit) {
	marklimit = readlimit;
	markpos = pos;
    }

    public void reset() throws IOException {
	if (markpos < 0)
	    throw new IOException("Resetting to invalid mark");
	pos = markpos;
    }

    public boolean markSupported() {
	return true;
    }

    public void close() throws IOException {
        if (in == null)
            return;
        in.close();
        in = null;
        buf = null;
    }
  
}


