<?xml version="1.0" encoding="UTF-8"?>
<sch:schema xmlns:sch="http://www.ascc.net/xml/schematron">
  <sch:ns uri="http://jadice.org/tools/relaxng-validator-maven-plugin/test/todo" prefix="t"/>
  <sch:pattern name="pattern1">
    <sch:rule context="t:item">
      <sch:assert test="string-length(normalize-space()) > 0">[pattern1] An item must not be
        empty.</sch:assert>
    </sch:rule>
  </sch:pattern>
</sch:schema>
