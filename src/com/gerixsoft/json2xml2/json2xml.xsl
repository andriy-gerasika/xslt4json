<?xml version="1.0" encoding="utf-8"?>

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	<xsl:template priority="-9" match="@*|node()">
		<xsl:copy>
			<xsl:apply-templates select="@*|node()" />
		</xsl:copy>
	</xsl:template>

	<xsl:template match="element/node()[1][name()='']">
		<xsl:attribute name="name">
			<xsl:value-of select="substring(., 2, string-length(.)-2)" />
		</xsl:attribute>
	</xsl:template>

	<xsl:template match="string/text()">
		<xsl:value-of select="substring(., 2, string-length(.)-2)" />
	</xsl:template>

</xsl:stylesheet>