/* Alter the current DOM document and format the result. You must put it inside the script manager for running it from the options menu */ 

// Return the current document
var document = EditiXManager.getDocumentModel().getCurrentDocument();

var domDoc = document.getDOMContent();
if ( domDoc ) {
	// Alter the current DOM document
	var root = domDoc.getDocumentElement();
	// Put a new attribute at the root
	root.setAttribute( "msg", "hello" );
	// Replace the current document by this DOM tree
	document.setDomContent( domDoc );
	// Format it
	EditixManager.activeAction( "format" );	// Format the result
}


