/* Interact with the editix context for putting a new node at the caret location. You must put it inside the script manager for running it from the options menu */ 

// Ask for a node name
var nodeName = EditiXManager.prompt( "Test", "YourNodeName" );
// If a response is available

if ( nodeName ) {

	// Return the current document
	var document = EditiXManager.getDocumentModel().getCurrentDocument();

	// Put the new node at the caret location
	document.insertTextAt( document.getCaretLocation(), "<" + nodeName + "></" + nodeName + ">" );
}

