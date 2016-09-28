# This is a Maven Plugin for XML validation

The XML Validator Maven Plugin allows you to perform an XML validation during an Apache Maven build.

## Build Status
[![Build Status](https://travis-ci.org/levigo/xml-validator-maven-plugin.svg?branch=master)](https://travis-ci.org/levigo/xml-validator-maven-plugin)

## Features
The key features of this maven plugin are:

- Ensures that the given file is a well-formed XML document
- Ensures that it conforms a given grammar. Supported grammar languages:
  - XML Schema
  - RelaxNG
  - Schematron
  - NVDL
  
## License
This XML Validator Maven Plugin is licensed under the [BSD 3-Clause License](https://opensource.org/licenses/BSD-3-Clause). Alternatively see [here](LICENSE).

## Support
If you find a bug or have a comment, please use the github issue tracker. Pull requests are always welcome and appreciated. 

## Usage

### General
The XML Validator Maven Plugin is available from [Maven Central](http://search.maven.org/). To use the plugin within a Maven POM-based project, simply include a new entry to the following artifact to the `<build>` section in the appropriate `pom.xml`:

```XML
<build>
  <plugins>
    <plugin>
      <groupId>org.jadice.tools</groupId>
      <artifactId>xml-validator-maven-plugin</artifactId>
      <version>1.0.0</version>
     <!-- Configuration see below -->
    </plugin>
  </plugins>
</build>
```
    
### Goal: validate

The only goal this maven plugin supports is `validate`.

Parameters:

| Name | Type | required? | Description |
|------|------|-----------|-------------|
| source | File | yes | The XML file to validate |
| schema | File | yes | The schema file to validate against. |
| skip | boolean | no | Skips the plugin execution, i.e. no validation will be performed.<br>User property is: `skipXMLValidation` |

## References and Attributions

This plugin is build on the JING module of the [Wicket Stuff HTML5 Validator](https://github.com/dashorst/wicket-stuff-markup-validator/) and so the [JING Relax NG validator](http://www.thaiopensource.com/relaxng/jing.html).

 - Relax NG: http://relaxng.org/
 - XML Schema: https://www.w3.org/XML/Schema
 - Schematron: http://www.schematron.com/
 - NVDL: http://www.nvdl.org/