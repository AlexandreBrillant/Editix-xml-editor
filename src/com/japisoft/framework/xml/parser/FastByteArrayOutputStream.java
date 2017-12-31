package com.japisoft.framework.xml.parser;

import java.io.*;

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
final class FastByteArrayOutputStream extends OutputStream {
    byte buf[];
    int count;

    public FastByteArrayOutputStream() {
	this(512);
    }

    FastByteArrayOutputStream(int size) {
	buf = new byte[size];
    }

    public  void write(int b) {
	int newcount = count + 1;
	if (newcount > buf.length) {
	    byte newbuf[] = new byte[Math.max(buf.length << 1, newcount)];
	    System.arraycopy(buf, 0, newbuf, 0, count);
	    buf = newbuf;
	}
	buf[count] = (byte)b;
	count = newcount;
    }

    public  void write(byte b[], int off, int len) {
	if (len == 0) {
	    return;
	}
        int newcount = count + len;
        if (newcount > buf.length) {
            byte newbuf[] = new byte[Math.max(buf.length << 1, newcount)];
            System.arraycopy(buf, 0, newbuf, 0, count);
            buf = newbuf;
        }
        System.arraycopy(b, off, buf, count, len);
        count = newcount;
    }

    public void writeTo(OutputStream out) throws IOException {
	out.write(buf, 0, count);
    }

    public void reset() {
	count = 0;
    }

    public byte toByteArray()[] {
	byte newbuf[] = new byte[count];
	System.arraycopy(buf, 0, newbuf, 0, count);
	return newbuf;
    }

    public int size() {
	return count;
    }

    public String toString() {
	return new String(buf, 0, count);
    }

    public String toString(String enc) throws UnsupportedEncodingException {
	return new String(buf, 0, count, enc);
    }

    public String toString(int hibyte) {
	return new String(buf, hibyte, 0, count);
    }

    public void close() throws IOException {
    }

}

// FastByteArrayOutputStream ends here
