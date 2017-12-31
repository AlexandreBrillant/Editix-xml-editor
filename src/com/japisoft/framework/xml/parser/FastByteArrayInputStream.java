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
final class FastByteArrayInputStream extends InputStream {
    byte buf[];
    int pos;
    int mark = 0;
    int count;

    public FastByteArrayInputStream(byte buf[]) {
	this.buf = buf;
        this.pos = 0;
	this.count = buf.length;
    }

    public int read() {
	return (pos < count) ? (buf[pos++] & 0xff) : -1;
    }

    public  int read(byte b[], int off, int len) {
	if (pos >= count) {
	    return -1;
	}
	if (pos + len > count) {
	    len = count - pos;
	}
	if (len <= 0) {
	    return 0;
	}
	System.arraycopy(buf, pos, b, off, len);
	pos += len;
	return len;
    }

    public  long skip(long n) {
	if (pos + n > count) {
	    n = count - pos;
	}
	if (n < 0) {
	    return 0;
	}
	pos += n;
	return n;
    }

    public  int available() {
	return count - pos;
    }

    public boolean markSupported() {
	return true;
    }

    public void mark(int readAheadLimit) {
	mark = pos;
    }

    public  void reset() {
	pos = mark;
    }

    public void close() throws IOException {
    }

}

// FastByteArrayInputStream ends here
