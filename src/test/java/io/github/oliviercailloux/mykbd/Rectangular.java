package io.github.oliviercailloux.mykbd;

import com.google.common.io.CharSource;
import com.google.common.io.MoreFiles;
import io.github.oliviercailloux.jaris.xml.DomHelper;
import io.github.oliviercailloux.keyboardd.keyboard.json.JsonRectangularKeyboardReader;
import io.github.oliviercailloux.keyboardd.keyboard.json.JsonRectangularRowKeyboard;
import io.github.oliviercailloux.keyboardd.representable.RectangularKeyboard;
import io.github.oliviercailloux.keyboardd.representable.SvgKeyboard;
import io.github.oliviercailloux.svgb.PositiveSize;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Rectangular {
  @SuppressWarnings("unused")
  private static final Logger LOGGER = LoggerFactory.getLogger(Rectangular.class);
  
  private static final DomHelper DOM_HELPER = DomHelper.domHelper();

  public static void main(String[] args) throws Exception {
    CharSource source =
        MoreFiles.asCharSource(Path.of("Keyboards").resolve("1 - Elite K70.json"), StandardCharsets.UTF_8);
    JsonRectangularRowKeyboard layout = JsonRectangularKeyboardReader.rowKeyboard(source);

    RectangularKeyboard physicalKeyboard = layout
        .toPhysicalKeyboard(PositiveSize.given(1.656d, 1.6d), PositiveSize.given(0.207d, 0.28d));
    SvgKeyboard svgK = SvgKeyboard.zonedFrom(physicalKeyboard);
    Files.writeString(Path.of("Keyboards").resolve("2 - Rectangular Elite K70 unlabeled.svg"),
        DOM_HELPER.toString(svgK.document()));
  }
}
