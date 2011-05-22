<?xml version="1.0" encoding="utf-8"?>

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	<xsl:template priority="-9" match="@*|node()">
		<xsl:copy>
			<xsl:apply-templates select="@*|node()" />
		</xsl:copy>
	</xsl:template>

	<xsl:template match="JSON">
		<json>
			<xsl:apply-templates select="@*|node()" />
		</json>
	</xsl:template>

	<xsl:template match="OBJECT">
		<object>
			<xsl:apply-templates select="@*|node()" />
		</object>
	</xsl:template>

	<xsl:template match="ARRAY">
		<array>
			<xsl:apply-templates select="@*|node()" />
		</array>
	</xsl:template>

	<xsl:template match="ELEMENT">
		<element>
			<xsl:apply-templates select="@*|node()" />
		</element>
	</xsl:template>

	<xsl:template match="ELEMENT/node()[1][name()='']">
		<xsl:attribute name="name">
			<xsl:value-of select="." />
		</xsl:attribute>
	</xsl:template>

	<xsl:template match="INTEGER">
		<integer>
			<xsl:apply-templates select="@*|node()" />
		</integer>
	</xsl:template>

	<xsl:template match="DOUBLE">
		<double>
			<xsl:apply-templates select="@*|node()" />
		</double>
	</xsl:template>

	<xsl:template match="BOOLEAN">
		<boolean>
			<xsl:apply-templates select="@*|node()" />
		</boolean>
	</xsl:template>

	<xsl:template match="STRING">
		<string>
			<xsl:apply-templates select="@*|node()" />
		</string>
	</xsl:template>

	<xsl:template match="STRING/text()">
		<xsl:value-of select="substring(., 2, string-length(.)-2)" />
	</xsl:template>

</xsl:stylesheet>