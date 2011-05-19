<?xml version="1.0" encoding="utf-8"?>

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	<xsl:template match="json">
		<xsl:copy>
			<xsl:copy-of select="@*"/>
			<xsl:call-template name="json2xml">
				<xsl:with-param name="text" select="."/>
			</xsl:call-template>
		</xsl:copy>
	</xsl:template>

	<xsl:template name="json2xml">
		<xsl:param name="text"/>
		<xsl:variable name="mode0">
			<xsl:variable name="regexps" select="'//(.*?)\n', '/\*(.*?)\*/', '(''|&quot;)(.*?)\3', '(-?\d+(\.\d+)?)', '([:,\{\}\[\]])', '(true|false)'"/>
			<xsl:analyze-string select="$text" regex="{string-join($regexps,'|')}" flags="s">
				<xsl:matching-substring>
					<xsl:choose>
						<!-- single line comment -->
						<xsl:when test="regex-group(1)">
							<xsl:comment>
								<xsl:value-of select="regex-group(1)"/>
							</xsl:comment>
							<xsl:text>&#10;</xsl:text>
						</xsl:when>
						<!-- multi line comment -->
						<xsl:when test="regex-group(2)">
							<xsl:comment>
								<xsl:value-of select="regex-group(2)"/>
							</xsl:comment>
						</xsl:when>
						<!-- string -->
						<xsl:when test="regex-group(3)">
							<string>
								<xsl:value-of select="regex-group(4)"/>
							</string>
						</xsl:when>
						<!-- number -->
						<xsl:when test="regex-group(5)">
							<number>
								<xsl:value-of select="regex-group(5)"/>
							</number>
						</xsl:when>
						<!-- symbol -->
						<xsl:when test="regex-group(7)">
							<symbol>
								<xsl:value-of select="regex-group(7)"/>
							</symbol>
						</xsl:when>
						<!-- boolean -->
						<xsl:when test="regex-group(8)">
							<boolean>
								<xsl:value-of select="regex-group(8)"/>
							</boolean>
						</xsl:when>
						<xsl:otherwise>
							<xsl:message terminate="yes" select="'internal error'"/>
						</xsl:otherwise>
					</xsl:choose>
				</xsl:matching-substring>
				<xsl:non-matching-substring>
					<xsl:if test="normalize-space()!=''">
						<xsl:message select="concat('unknown token: ', .)"/>
						<xsl:value-of select="."/>
					</xsl:if>
				</xsl:non-matching-substring>
			</xsl:analyze-string>
		</xsl:variable>
		<xsl:variable name="mode1">
			<xsl:apply-templates mode="json2xml1" select="$mode0/node()[1]"/>
		</xsl:variable>
		<xsl:variable name="mode2">
			<xsl:apply-templates mode="json2xml2" select="$mode1"/>
		</xsl:variable>
		<xsl:variable name="mode3">
			<xsl:apply-templates mode="json2xml3" select="$mode2"/>
		</xsl:variable>
		<xsl:copy-of select="$mode3"/> <!-- change $mode3 to $mode[0-2] for easy debug -->
	</xsl:template>

	<!-- json2xml1 mode: group content between {} and [] into object and array elements -->

	<xsl:template mode="json2xml1" match="node()" priority="-9">
		<xsl:copy-of select="."/>
		<xsl:apply-templates mode="json2xml1" select="following-sibling::node()[1]"/>
	</xsl:template>

	<xsl:template mode="json2xml1" match="symbol[.=('}',']')]"/>

	<xsl:template mode="json2xml1" match="symbol[.=('{','[')]">
		<xsl:element name="{if (.='{') then 'object' else 'array'}">
			<xsl:apply-templates mode="json2xml1" select="following-sibling::node()[1]"/>
		</xsl:element>
		<xsl:variable name="level" select="count(preceding-sibling::symbol[.=('{','[')])-count(preceding-sibling::symbol[.=('}',']')])+1"/>
		<xsl:variable name="ender"
			select="following-sibling::symbol[.=('}',']') and count(preceding-sibling::symbol[.=('{','[')])-count(preceding-sibling::symbol[.=('}',']')])=$level][1]"/>
		<xsl:apply-templates mode="json2xml1" select="$ender/following-sibling::node()[1]"/>
	</xsl:template>

	<!-- json2xml2 mode: group <string>:<string|number|object|array> into field element -->

	<xsl:template priority="-9" mode="json2xml2" match="@*|node()">
		<xsl:copy>
			<xsl:apply-templates mode="json2xml2" select="@*|node()"/>
		</xsl:copy>
	</xsl:template>

	<xsl:template mode="json2xml2"
		match="string[following-sibling::*[1]/self::symbol[.=':'] and following-sibling::*[2]/(self::string|self::number|self::boolean|self::object|self::array)]"/>

	<xsl:template mode="json2xml2"
		match="symbol[.=':'][preceding-sibling::*[1]/self::string and following-sibling::*[1]/(self::string|self::number|self::boolean|self::object|self::array)]">
		<field name="{preceding-sibling::*[1]}">
			<xsl:for-each select="following-sibling::*[1]">
				<xsl:copy>
					<xsl:apply-templates mode="json2xml2" select="@*|node()"/>
				</xsl:copy>
			</xsl:for-each>
		</field>
	</xsl:template>

	<xsl:template mode="json2xml2"
		match="*[self::string|self::number|self::boolean|self::object|self::array][preceding-sibling::*[2]/self::string and preceding-sibling::*[1]/self::symbol[.=':']]"/>

	<!-- json2xml3 mode: drop comma between consecutive field and object elements -->

	<xsl:template priority="-9" mode="json2xml3" match="@*|node()">
		<xsl:copy>
			<xsl:apply-templates mode="json2xml3" select="@*|node()"/>
		</xsl:copy>
	</xsl:template>

	<xsl:template mode="json2xml3" match="object/symbol[.=','][preceding-sibling::*[1]/self::field and following-sibling::*[1]/self::field]"/>

	<xsl:template mode="json2xml3" match="array/symbol[.=','][preceding-sibling::*[1]/(self::string|self::number|self::boolean|self::object|self::array) and following-sibling::*[1]/(self::string|self::number|self::boolean|self::object|self::array)]"/>

</xsl:stylesheet>