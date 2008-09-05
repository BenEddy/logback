package ch.qos.logback.classic.pattern;

import static org.junit.Assert.assertEquals;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.LoggingEvent;

public class ExtendedThrowableProxyConverterTest {

  LoggerContext lc = new LoggerContext();
  ExtendedThrowableProxyConverter etpc = new ExtendedThrowableProxyConverter();
  StringWriter sw = new StringWriter();
  PrintWriter pw = new PrintWriter(sw);

  @Before
  public void setUp() throws Exception {
    etpc.setContext(lc);
    etpc.start();
  }

  @After
  public void tearDown() throws Exception {
  }

  private LoggingEvent createLoggingEvent(Throwable t) {
    LoggingEvent le = new LoggingEvent(this.getClass().getName(), lc
        .getLogger(LoggerContext.ROOT_NAME), Level.DEBUG, "test message", t,
        null);
    return le;
  }

  @Test
  public void smoke() {
    Exception t = new Exception("smoke");
    verify(t);
  }

  @Test
  @Ignore
  public void nested() {
    Throwable t = makeNestedException(1);
    verify(t);
  }

  void verify(Throwable t) {
    t.printStackTrace(pw);

    LoggingEvent le = createLoggingEvent(t);
    String result = etpc.convert(le);
    result = result.replace("common frames omitted", "more");
    result = result.replaceAll(" \\[.*\\]", "");
    assertEquals(sw.toString(), result);
  }

  Throwable makeNestedException(int level) {
    if (level == 0) {
      return new Exception("nesting level=" + level);
    }
    Throwable cause = makeNestedException(level - 1);
    return new Exception("nesting level =" + level, cause);
  }
}
