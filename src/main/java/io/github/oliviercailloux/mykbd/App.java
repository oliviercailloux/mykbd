package io.github.oliviercailloux.mykbd;

import com.google.common.io.CharSource;
import com.google.common.io.Resources;
import io.github.oliviercailloux.jaris.xml.DomHelper;
import io.github.oliviercailloux.keyboardd.keyboard.RectangularKeyboard;
import io.github.oliviercailloux.keyboardd.keyboard.json.JsonRectangularKeyboardReader;
import io.github.oliviercailloux.keyboardd.keyboard.json.JsonRectangularRowKeyboard;
import io.github.oliviercailloux.keyboardd.representable.SvgKeyboard;
import io.github.oliviercailloux.svgb.PositiveSize;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class App {
  @SuppressWarnings("unused")
  private static final Logger LOGGER = LoggerFactory.getLogger(App.class);

  public static void main(String[] args) throws Exception {
    new App().proceed();
  }

  private DomHelper domHelper;

  App() {
    domHelper = DomHelper.domHelper();
  }

  public void proceed() throws Exception {
    LOGGER.info("Hello World!");
    CharSource source = Resources.asCharSource(Path.of("Keyboard layout Elite K70.json").toUri().toURL(),
        StandardCharsets.UTF_8);

    JsonRectangularRowKeyboard layout = JsonRectangularKeyboardReader.rowKeyboard(source);
    RectangularKeyboard physicalKeyboard =
        layout.toPhysicalKeyboard(PositiveSize.square(2d), PositiveSize.square(1d));
    SvgKeyboard svgK = SvgKeyboard.zonedFrom(physicalKeyboard);
    String svg = domHelper.toString(svgK.document());
    Files.writeString(Path.of("out.svg"), svg);
  }
}
