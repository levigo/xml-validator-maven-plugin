package com.levigo.tools.xml.validator;

import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.List;

import org.hamcrest.Matcher;
import org.junit.contrib.java.lang.system.SystemErrRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

/**
 * Extends the {@link SystemErrRule}, adding the capability to submit {@link Matcher}s as
 * expectations that will be checked after the test was run.
 */
public class ExpectedSystemErrRule extends SystemErrRule {

  private List<Matcher<? super String>> matchers = new ArrayList<>(5);

  @Override
  public Statement apply(final Statement base, final Description description) {
    return new Statement() {
      @Override
      public void evaluate() throws Throwable {
        enableLog();
        try {
          final Statement superBase = ExpectedSystemErrRule.super.apply(base, description);
          superBase.evaluate();
        } finally {
          validateExpectations();
        }
      }
    };
  }

  private void validateExpectations() {
    if (matchers.size() > 0) {
      for (Matcher<? super String> matcher : matchers) {
        assertThat(getLog(), matcher);
      }
    }
  }

  public void expectMessage(Matcher<? super String> matcher) {
    matchers.add(matcher);
  }

}
