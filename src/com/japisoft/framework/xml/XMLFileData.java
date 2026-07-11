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

package com.japisoft.framework.xml;

import java.util.List;
import java.util.Map;

/** Simple data structure for a file content and its encoding */
public class XMLFileData {
	private String encoding = null;
	private String content = null;
	public long modifiedDate = 0;
	public String uri;
	
	public XMLFileData( String encoding, String content ) {
		this.encoding = encoding;
		this.content = content;
	}

	public String getEncoding() { return encoding; }
	public String getContent() { return content; }
	public long getModifiedDate() { return modifiedDate; }
	public String getURI() { return uri; }
	
	// CHECK SPECIFIC SYMBOL FOR UPDATING THE FONT
	
	private static final Map<String, List<int[]>> CJK_RANGES = Map.of(
        "jp", List.of(
            new int[]{0x3000, 0x303F}, // Symboles japonais
            new int[]{0x3040, 0x309F}, // Hiragana
            new int[]{0x30A0, 0x30FF}, // Katakana
            new int[]{0x4E00, 0x9FFF}, // Kanji (partagé avec le chinois)
            new int[]{0xF900, 0xFAFF}, // Kanji supplémentaires
            new int[]{0x31F0, 0x31FF}  // Katakana étendus
        ),
        "kr", List.of(
            new int[]{0xAC00, 0xD7AF}, // Hangul (syllabes coréennes)
            new int[]{0x1100, 0x11FF}, // Jamo (composants Hangul)
            new int[]{0x3130, 0x318F}, // Compatibilité Hangul
            new int[]{0xA960, 0xA97F}, // Hangul étendus
            new int[]{0xD7B0, 0xD7FF}  // Hangul supplémentaires
        ),
        "sc", List.of(
            new int[]{0x4E00, 0x9FFF}, // Han (chinois simplifié)
            new int[]{0x3400, 0x4DBF}, // CJK Extension A
            new int[]{0x20000, 0x2A6DF}, // CJK Extension B
            new int[]{0x2A700, 0x2B73F}, // CJK Extension C
            new int[]{0x2B740, 0x2B81F}, // CJK Extension D
            new int[]{0x2B820, 0x2CEAF}  // CJK Extension E/F
        ),
        "tc", List.of(
            new int[]{0x4E00, 0x9FFF}, // Han (chinois traditionnel)
            new int[]{0x3400, 0x4DBF}, // CJK Extension A
            new int[]{0x20000, 0x2A6DF}, // CJK Extension B
            new int[]{0x2F00, 0x2FDF}, // Kanbun (chinois classique)
            new int[]{0x2E80, 0x2EFF}  // CJK Radicals Supplement
        ),
        "hk", List.of(
            new int[]{0x4E00, 0x9FFF}, // Han (Hong Kong)
            new int[]{0x3400, 0x4DBF}, // CJK Extension A
            new int[]{0x20000, 0x2A6DF}, // CJK Extension B
            new int[]{0x2F00, 0x2FDF}  // Kanbun
        )
    );

	public String getCharactersType() {
		if ( content == null ) return null;
		for (char c : content.toCharArray()) {
			int codePoint = c;
            for (Map.Entry<String, List<int[]>> entry : CJK_RANGES.entrySet()) {
                for (int[] range : entry.getValue()) {
                    if (codePoint >= range[0] && codePoint <= range[1]) {
                        return entry.getKey();
                    }
                }
            }
		}
		return null;
	}

}
