package io.github.oliviercailloux.mykbd;

import static com.google.common.base.Verify.verify;

import com.google.common.io.CharSource;
import com.google.common.io.Resources;
import com.google.common.math.DoubleMath;
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
    CharSource source = Resources.asCharSource(
        Path.of("Keyboard layout Elite K70.json").toUri().toURL(), StandardCharsets.UTF_8);

    JsonRectangularRowKeyboard layout = JsonRectangularKeyboardReader.rowKeyboard(source);

    double spacingWidth = 0.207d;
    double defaultWidth = 8d*spacingWidth;
    /* spacing width varies. Average is 29.6 cm for total length for 16 standard keys and 15 sep. */
    verify(DoubleMath.fuzzyEquals(29.601d, 16*defaultWidth+15*spacingWidth, 1e-4d));
    double defaultHeight = 1.6d;
    /* Total height is 11 cm (measured), that is 6*height + 5*spacingHeight. */
    double spacingHeight = 0.28d;
    verify(DoubleMath.fuzzyEquals(11d, 6*defaultHeight+5*spacingHeight, 1e-4d));
    RectangularKeyboard physicalKeyboard = layout.toPhysicalKeyboard(
        PositiveSize.given(defaultWidth, defaultHeight), PositiveSize.given(spacingWidth, spacingHeight));
    SvgKeyboard svgK = SvgKeyboard.zonedFrom(physicalKeyboard);
    String svg = domHelper.toString(svgK.document());
    Files.writeString(Path.of("Rectangular Elite K70.svg"), svg);
  }
}
