<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0" xmlns:s="http://splitcells.net/sew.xsd"
                xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#" xmlns:dc="http://purl.org/dc/elements/1.1/"
                xmlns:skos="http://www.w3.org/2004/02/skos/core#" xmlns:svg="http://www.w3.org/2000/svg"
                xmlns="http://www.w3.org/1999/xhtml" xmlns:x="http://www.w3.org/1999/xhtml"
                xmlns:d="http://splitcells.net/den.xsd"
                xmlns:p="http://splitcells.net/private.xsd" xmlns:m="http://www.w3.org/1998/Math/MathML"
                xmlns:r="http://splitcells.net/raw.xsd" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:foaf="http://xmlns.com/foaf/0.1/" xmlns:n="http://splitcells.net/natural.xsd"
                xmlns:ns="http://splitcells.net/namespace.xsd"
                xmlns:office="urn:oasis:names:tc:opendocument:xmlns:office:1.0"
                xmlns:t="http://splitcells.net/text.xsd"
                xmlns:xl="http://www.w3.org/1999/xlink"
                xmlns:table="urn:oasis:names:tc:opendocument:xmlns:table:1.0">
    <!--
    Copyright (c) 2021 Mārtiņš Avots (Martins Avots) and others

    This program and the accompanying materials are made available under the
    terms of the Eclipse Public License 2.0, which is available at
    http://www.eclipse.org/legal/epl-2.0, or the MIT License,
    which is available at https://spdx.org/licenses/MIT.html.

    SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
    -->
    <xsl:template name="rdf-document-to-javascript-json">
        <xsl:param name="rdf-document"/>
        <xsl:text>[</xsl:text>
        <xsl:for-each select="$rdf-document/rdf:RDF/node()">
            <xsl:if test="position()!=1">
                <xsl:text>,</xsl:text>
            </xsl:if>
            <xsl:apply-templates select="." mode="rdf-document-to-javascript-json"/>
        </xsl:for-each>
        <xsl:text>]</xsl:text>
    </xsl:template>
    <xsl:template match="rdf:Description" mode="rdf-document-to-javascript-json">
        <xsl:text>{"resource":"</xsl:text>
        <xsl:value-of select="@rdf:resource"/>
        <xsl:text>","label":"</xsl:text>
        <xsl:value-of select="./rdf:label"/>
        <xsl:text>","comment":"</xsl:text>
        <xsl:value-of select="./rdf:comment"/>
        <xsl:text>"}</xsl:text>
    </xsl:template>
</xsl:stylesheet>