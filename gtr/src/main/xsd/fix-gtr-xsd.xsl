<?xml version="1.0" encoding="UTF-8"?>
<!--

    ngs-gtr  Mapping for GTR XSDs.
    Copyright (c) 2014 National Marrow Donor Program (NMDP)

    This library is free software; you can redistribute it and/or modify it
    under the terms of the GNU Lesser General Public License as published
    by the Free Software Foundation; either version 3 of the License, or (at
    your option) any later version.

    This library is distributed in the hope that it will be useful, but WITHOUT
    ANY WARRANTY; with out even the implied warranty of MERCHANTABILITY or
    FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public
    License for more details.

    You should have received a copy of the GNU Lesser General Public License
    along with this library;  if not, write to the Free Software Foundation,
    Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307  USA.

    > http://www.gnu.org/licenses/lgpl.html

-->
<xsl:stylesheet version="2.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  xsi:schemaLocation="http://www.w3.org/1999/XSL/Transform http://www.w3.org/2007/schema-for-xslt20.xsd"
  xmlns:xs="http://www.w3.org/2001/XMLSchema">

  <xsl:output method="xml" encoding="utf-8" indent="yes"/>

  <!-- Identity template : copy all text nodes, elements and attributes -->   
  <xsl:template match="@*|node()">
    <xsl:copy>
      <xsl:apply-templates select="@*|node()" />
    </xsl:copy>
  </xsl:template>

  <!--
    Eliminate the extra gtr_identifiers in extensions 
    WORKAROUND for ERROR: Property "Id" is already defined. Use &lt;jaxb:property> to resolve this conflict.
    The extensions are to base complexTypes that already contains the attributeGroup which specified id. 
  -->
  <xsl:template match="xs:extension[@base='GTRLabTestType']/xs:attributeGroup[@ref='gtr_identifiers']"/>
  <xsl:template match="xs:extension[@base='GTRLabResearchTestType']/xs:attributeGroup[@ref='gtr_identifiers']"/>
</xsl:stylesheet>
