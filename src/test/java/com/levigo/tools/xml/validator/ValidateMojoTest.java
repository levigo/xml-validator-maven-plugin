package com.levigo.tools.xml.validator;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.isA;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.io.FileNotFoundException;

import org.apache.maven.plugin.Mojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.testing.MojoRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.xml.sax.SAXParseException;

public class ValidateMojoTest {

  @Rule
  public MojoRule mojoRule = new MojoRule();

  @Rule
  public ExpectedException trouble = ExpectedException.none();

  @Rule
  public ExpectedSystemErrRule systemErr = new ExpectedSystemErrRule();


  private ValidateMojo instantiateValidateMojo() throws Exception {
    // FIXME is there any way to look up a Mojo from scratch, without providing a fake POM file?
    final Mojo mojo = mojoRule.lookupConfiguredMojo(new File("src/test/resources/maven_plugin_testing_harness/"),
        "validate");
    assertThat(mojo, is(instanceOf(ValidateMojo.class)));
    return (ValidateMojo) mojo;
  }

  private void setSource(String sourceXmlFile, Mojo mojo) throws IllegalAccessException {
    mojoRule.setVariableValueToObject(mojo, "source", new File(sourceXmlFile));
  }

  private void setSchema(String schemaFile, Mojo mojo) throws IllegalAccessException {
    mojoRule.setVariableValueToObject(mojo, "schema", new File(schemaFile));
  }

  @Test
  public void testThat_validXml_validatesUsingRelaxNG() throws Exception {
    final Mojo mojo = instantiateValidateMojo();
    setSource("src/test/resources/testfiles/todo-valid.xml", mojo);
    setSchema("src/test/resources/rng/todo.rng", mojo);
    mojo.execute();
  }

  @Test
  public void testThat_validXml_validatesUsingXSD() throws Exception {
    final Mojo mojo = instantiateValidateMojo();
    setSource("src/test/resources/testfiles/todo-valid.xml", mojo);
    setSchema("src/test/resources/xsd/todo.xsd", mojo);
    mojo.execute();
  }

  @Test
  public void testThat_validXml_validatesUsingSchematron() throws Exception {
    final Mojo mojo = instantiateValidateMojo();
    setSource("src/test/resources/testfiles/todo-valid.xml", mojo);
    setSchema("src/test/resources/sch/todo.sch", mojo);
    mojo.execute();
  }

  @Test
  public void testThat_validXml_validatesUsingNVDL() throws Exception {
    final Mojo mojo = instantiateValidateMojo();
    setSource("src/test/resources/testfiles/todo-valid.xml", mojo);
    setSchema("src/test/resources/nvdl/todo.nvdl", mojo);
    mojo.execute();
  }

  @Test
  public void testThat_validXmlWithXInclude_validatesUsingNVDL() throws Exception {
    final Mojo mojo = instantiateValidateMojo();
    setSource("src/test/resources/testfiles/todo-valid-with-xinclude.xml", mojo);
    setSchema("src/test/resources/nvdl/todo.nvdl", mojo);
    mojo.execute();
  }

  @Test
  public void testThat_syntacticallyIncorrectXml_leadsToMojoFailureException() throws Exception {
    final Mojo mojo = instantiateValidateMojo();
    setSource("src/test/resources/testfiles/todo-invalid-xml-syntax.xml", mojo);
    setSchema("src/test/resources/nvdl/todo.nvdl", mojo);

    trouble.expect(MojoFailureException.class);
    trouble.expectCause(isA(SAXParseException.class));
    trouble.expectMessage("The element type \"item\" must be terminated by the matching end-tag \"</item>\".");
    mojo.execute();
  }

  @Test
  public void testThat_invalidFileAccordingToRelaxNG_leadsToMojoFailureException() throws Exception {
    final Mojo mojo = instantiateValidateMojo();
    setSource("src/test/resources/testfiles/todo-invalid-rng.xml", mojo);
    setSchema("src/test/resources/rng/todo.rng", mojo);

    trouble.expect(MojoFailureException.class);
    trouble.expectMessage(startsWith("File is NOT VALID."));
    systemErr.expectMessage(containsString("element \"todo\" incomplete; missing required element \"item\""));
    mojo.execute();
  }

  @Test
  public void testThat_invalidFileAccordingToXSD_leadsToMojoFailureException() throws Exception {
    final Mojo mojo = instantiateValidateMojo();
    setSource("src/test/resources/testfiles/todo-invalid-xsd.xml", mojo);
    setSchema("src/test/resources/xsd/todo.xsd", mojo);

    trouble.expect(MojoFailureException.class);
    trouble.expectMessage(startsWith("File is NOT VALID."));
    systemErr.expectMessage(containsString(
        "error: http://www.w3.org/TR/xml-schema-1#cvc-complex-type.2.4.b?todo&{\"http://jadice.org/tools/relaxng-validator-maven-plugin/test/todo\":item}"));
    mojo.execute();
  }

  @Test
  public void testThat_invalidFileAccordingToSchematron_leadsToMojoFailureException() throws Exception {
    final Mojo mojo = instantiateValidateMojo();
    setSource("src/test/resources/testfiles/todo-invalid-sch.xml", mojo);
    setSchema("src/test/resources/sch/todo.sch", mojo);

    trouble.expect(MojoFailureException.class);
    trouble.expectMessage(startsWith("File is NOT VALID."));
    systemErr.expectMessage(containsString("[pattern1] An item must not be empty."));
    mojo.execute();
  }

  @Test
  public void testThat_invalidFile_isIgnored_ifSkipIsTrue() throws Exception {
    final Mojo mojo = instantiateValidateMojo();
    mojoRule.setVariableValueToObject(mojo, "skip", Boolean.TRUE);

    // use a test file which has been recognized as broken in another test method
    setSource("src/test/resources/testfiles/todo-invalid-xml-syntax.xml", mojo);
    setSchema("src/test/resources/nvdl/todo.nvdl", mojo);

    // since we skip validation, no exception must occur
    mojo.execute();
  }

  @Test
  public void testThat_nonexistentSourceFile_leadsToMojoExecutionException() throws Exception {
    final String nonExistentFileName = "nonexistant-file.xml";

    final Mojo mojo = instantiateValidateMojo();
    setSource(nonExistentFileName, mojo);
    setSchema("src/test/resources/nvdl/todo.nvdl", mojo);

    trouble.expect(MojoExecutionException.class);
    trouble.expectCause(isA(FileNotFoundException.class));
    trouble.expectMessage(containsString(nonExistentFileName));
    mojo.execute();
  }

  @Test
  public void testThat_nonexistentSchemaFile_leadsToMojoExecutionException() throws Exception {
    final String nonExistentFileName = "nonexistant-file.nvdl";

    final Mojo mojo = instantiateValidateMojo();
    setSource("src/test/resources/testfiles/todo-valid.xml", mojo);
    setSchema(nonExistentFileName, mojo);

    trouble.expect(MojoExecutionException.class);
    trouble.expectCause(isA(FileNotFoundException.class));
    trouble.expectMessage(containsString(nonExistentFileName));
    mojo.execute();
  }

}