package demo.junit;

import lombok.RequiredArgsConstructor;
import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

@RequiredArgsConstructor
public class EmailMatcher extends TypeSafeMatcher<String> {

  private final String expression;

  public static EmailMatcher isEmail() {
    return new EmailMatcher("^[\\w-.]+@([\\w-]+\\.)+[\\w-]{2,4}$");
  }

  @Override
  protected boolean matchesSafely(String string) {
    return string.matches(expression);
  }

  @Override
  public void describeTo(Description description) {
    description.appendText("value must be match email expression = " + expression);
  }
}
