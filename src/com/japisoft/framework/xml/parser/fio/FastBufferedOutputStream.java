package com.japisoft.framework.xml.parser.fio;

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
public class FastBufferedOutputStream extends FilterOutputStream {

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

// FastBufferedOutputStream ends here
