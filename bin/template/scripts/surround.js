/* Interact with the editix context surrounding the selection by a new node. You must put it inside the script manager for running it from the options menu */ 

// Ask for a node name
var nodeName = EditiXManager.prompt( "Surround", "YourNodeName" );
// If a response is available

if ( nodeName ) {

	// Return the current document
	var document = EditiXManager.getDocumentModel().getCurrentDocument();

	// Current selection
	var selection = document.getTextSelection();
	if ( !selection ) {
		selection = " ";
	}

	// Put the new node at the caret location
	document.replaceTextSelection( "<" + nodeName + ">" + selection + "</" + nodeName + ">" );
}
