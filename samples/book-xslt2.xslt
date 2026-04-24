<?xml version="1.0" encoding="UTF-8" ?>

<!-- New XSLT document created with EditiX XML Editor (https://www.editix.com) at Thu Apr 23 10:29:38 CEST 2026 -->

<xsl:stylesheet version="2.0" 
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
	xmlns:xs="http://www.w3.org/2001/XMLSchema"
	xmlns:fn="http://www.w3.org/2005/xpath-functions"
	xmlns:xdt="http://www.w3.org/2005/xpath-datatypes"
	xmlns:err="http://www.w3.org/2005/xqt-errors"
	exclude-result-prefixes="xs xdt err fn">

	<xsl:output method="xml" indent="yes"/>
	
	<xsl:template match="/">
		<xsl:for-each select="//book">
			<xsl:variable  name="title" select="title"/>
			<div>
				<xsl:value-of select="$title"></xsl:value-of>
			</div>
		</xsl:for-each>
	</xsl:template>

</xsl:stylesheet>
