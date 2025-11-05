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

package com.japisoft.xmlpad.action;

import com.japisoft.xmlpad.action.edit.CopyAction;
import com.japisoft.xmlpad.action.edit.CutAction;
import com.japisoft.xmlpad.action.edit.FastCommentUncommentAction;
import com.japisoft.xmlpad.action.edit.PasteAction;
import com.japisoft.xmlpad.action.edit.RedoAction;
import com.japisoft.xmlpad.action.edit.UndoAction;
import com.japisoft.xmlpad.action.file.InsertAction;
import com.japisoft.xmlpad.action.file.LoadAction;
import com.japisoft.xmlpad.action.file.NewAction;
import com.japisoft.xmlpad.action.file.SaveAction;
import com.japisoft.xmlpad.action.file.SaveAsAction;
import com.japisoft.xmlpad.action.other.SplitAction;
import com.japisoft.xmlpad.action.other.SplitActionHorizontal;
import com.japisoft.xmlpad.action.search.SearchAction;
import com.japisoft.xmlpad.action.xml.CommentAction;
import com.japisoft.xmlpad.action.xml.FormatAction;
import com.japisoft.xmlpad.action.xml.ParseAction;
import com.japisoft.xmlpad.tree.action.AddHistoryAction;
import com.japisoft.xmlpad.tree.action.CleanHistoryAction;
import com.japisoft.xmlpad.tree.action.CommentNode;
import com.japisoft.xmlpad.tree.action.CopyNode;
import com.japisoft.xmlpad.tree.action.CutNode;
import com.japisoft.xmlpad.tree.action.EditNode;
import com.japisoft.xmlpad.tree.action.NextAction;
import com.japisoft.xmlpad.tree.action.PreviousAction;
import com.japisoft.xmlpad.tree.action.SelectNode;

/**
 * This interface describes available known action. It can be used for
 * updating / changing an action
 * @author Alexandre Brillant (https://github.com/AlexandreBrillant/Editix-xml-editor)
 * @version 1.0
 * */
public interface ActionSet {

	/** New action */
	public static String NEW_ACTION =
		NewAction.ID;

	/** Undo action */
	public static String UNDO_ACTION =
		UndoAction.ID;

	/** Redo action */
	public static String REDO_ACTION =
		RedoAction.ID;

	/** Copy action */
	public static String COPY_ACTION =
		CopyAction.ID;

	/** Cut action */
	public static String CUT_ACTION =
		CutAction.ID;

	/** Paste action */
	public static String PASTE_ACTION =
		PasteAction.ID;

	/** Refresh action */
	public static String PARSE_ACTION =
		ParseAction.ID;

	/** Search action */
	public static String SEARCH_ACTION =
		SearchAction.ID;

	/** Comment action */
	public static String COMMENT_ACTION =
		CommentAction.ID;

	/** Fast comment/uncomment action */
	public static String FAST_COMMENT_ACTION =
		FastCommentUncommentAction.ID;

	/** SaveAs action */
	public static String SAVEAS_ACTION =
		SaveAsAction.ID;

	/** Save action */
	public static String SAVE_ACTION =
		SaveAction.ID;

	/** Insert file action */
	public static String INSERT_ACTION =
		InsertAction.ID;

	/** Load action */
	public static String LOAD_ACTION =
		LoadAction.ID;

	/** Split vertically action */
	public static String SPLIT_ACTION =
		SplitAction.ID;

	/** Split horizontally action */
	public static String SPLIT_ACTION_HOR =
		SplitActionHorizontal.ID;
		
	/** Format action */
	public static String FORMAT_ACTION =
		FormatAction.ID;

	// TREE

	/** Tree select node */
	public static String TREE_SELECTNODE_ACTION =
		SelectNode.ID;
		
	/** Tree copy node */
	public static String TREE_COPYNODE_ACTION =
		CopyNode.ID;

	/** Tree cut node */
	public static String TREE_CUTNODE_ACTION =
		CutNode.ID;
	
	/** Tree edit node */
	public static String TREE_EDITNODE_ACTION =
		EditNode.ID;

	/** Tree comment node */
	public static String TREE_COMMENTNODE_ACTION =
		CommentNode.ID;

	/** Retreive the previous history node */
	public static String TREE_PREVIOUS_ACTION =
		PreviousAction.ID;

	/** Retreive the next history node */
	public static String TREE_NEXT_ACTION =
		NextAction.ID;
	
	/** Clean the history */
	public static String TREE_CLEANHISTORY_ACTION =
		CleanHistoryAction.ID;
	
	/** Add the current node in the history */
	public static String TREE_ADDHISTORY_ACTION =
		AddHistoryAction.ID;

}

