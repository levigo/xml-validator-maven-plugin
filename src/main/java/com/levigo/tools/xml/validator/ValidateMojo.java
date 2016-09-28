package com.levigo.tools.xml.validator;

import java.io.File;
import java.io.IOException;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.thaiopensource.validate.SchemaReader;
import com.thaiopensource.validate.ValidationDriver;
import com.thaiopensource.validate.auto.AutoSchemaReader;

/**
 * Validates a given XML file against a schema which can be a RelaxNG, XML Schema, Schematron or
 * NVDL file.
 */
@Mojo(name = "validate", defaultPhase = LifecyclePhase.VERIFY)
public class ValidateMojo extends AbstractMojo {

  private final XmlParserProperties xmlParserProperties = new XmlParserProperties();

  /**
   * The schema file to validate against.
   * 
   * Note: We don't support URL schema so that the maven plugin enforces to support the offline
   * mode.
   */
  @Parameter(required = true)
  private File schema;

  /**
   * The XML file to validate
   */
  @Parameter(required = true)
  private File source;

  /**
   * Skips the plugin execution, i.e. no validation will be performed.
   */
  @Parameter(property = "skipXMLValidation")
  private boolean skip = false;

  public void execute() throws MojoExecutionException, MojoFailureException {
    if (skip) {
      getLog().info("Skipping XML Validation.");
      return;
    }

    xmlParserProperties.set();
    try {
      validate();
    } catch (SAXException e) {
      throw new MojoFailureException(e.getMessage(), e);
    } catch (IOException e) {
      throw new MojoExecutionException(e.getMessage(), e);
    } finally {
      xmlParserProperties.restore();
    }
  }

  private void validate() throws IOException, SAXException, MojoFailureException {
    final ValidationDriver validationDriver = createValidationDriver();
    final InputSource xmlSource = ValidationDriver.fileInputSource(source);

    final boolean isValid = validationDriver.validate(xmlSource);
    if (isValid) {
      getLog().info("XML file validates. No problems detected.");
    } else {
      throw new MojoFailureException("File is NOT VALID. File: " + source);
    }
  }

  private ValidationDriver createValidationDriver() throws IOException, SAXException {
    final SchemaReader schemaReader = new AutoSchemaReader();
    final ValidationDriver validationDriver = new ValidationDriver(schemaReader);

    final InputSource schemaSource = ValidationDriver.fileInputSource(schema);
    validationDriver.loadSchema(schemaSource);

    return validationDriver;
  }
}
