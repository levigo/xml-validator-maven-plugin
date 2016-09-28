package com.levigo.tools.xml.validator;

/**
 * Configures the XML and SAX factories and restores the orignal values afterwards.
 */
public class XmlParserProperties {

  public static final String SAX_PARSER_KEY = "javax.xml.parsers.SAXParserFactory";

  public static final String SAX_PARSER_VALUE = "org.apache.xerces.jaxp.SAXParserFactoryImpl";

  public static final String XML_PARSER_KEY = "org.apache.xerces.xni.parser.XMLParserConfiguration";

  public static final String XML_PARSER_VALUE = "org.apache.xerces.parsers.XIncludeParserConfiguration";

  private String originalXmlParserValue;

  private String originalSaxParserValue;

  /**
   * Configures the XML and SAX factories for our validation mojo
   */
  public void set() {
    originalXmlParserValue = System.getProperty(XML_PARSER_KEY);
    originalSaxParserValue = System.getProperty(SAX_PARSER_KEY);

    System.setProperty(XML_PARSER_KEY, XML_PARSER_VALUE);
    System.setProperty(SAX_PARSER_KEY, SAX_PARSER_VALUE);

  }


  /**
   * Restores to the previous values
   */
  public void restore() {
    setPropertySafely(XML_PARSER_KEY, originalXmlParserValue);
    setPropertySafely(SAX_PARSER_KEY, originalSaxParserValue);
  }
  
  
  /**
   * Null-safe setter for a java system properties.
   * 
   * @param key the property key
   * @param value the property value (might be {@code null}
   */
  private static void setPropertySafely(String key, String value) {
    if (value == null) {
      System.clearProperty(key);
    } else {
      System.setProperty(key, value);
    }
  }


}
