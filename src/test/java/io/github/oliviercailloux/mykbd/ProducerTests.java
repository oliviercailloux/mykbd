package io.github.oliviercailloux.mykbd;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.github.oliviercailloux.geometry.Point;
import io.github.oliviercailloux.jaris.io.CloseablePathFactory;
import io.github.oliviercailloux.jaris.io.PathUtils;
import io.github.oliviercailloux.jaris.xml.DomHelper;
import io.github.oliviercailloux.keyboardd.keyboard.json.JsonRectangularKeyboardReader;
import io.github.oliviercailloux.keyboardd.keyboard.json.JsonRectangularRowKeyboard;
import io.github.oliviercailloux.keyboardd.representable.RectangularKeyboard;
import io.github.oliviercailloux.keyboardd.representable.SvgKeyboard;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProducerTests {
  @SuppressWarnings("unused")
  private static final Logger LOGGER = LoggerFactory.getLogger(ProducerTests.class);

  private static final DomHelper DOM_HELPER = DomHelper.domHelper();

  @Test
  void test1JsonToRectangular() throws Exception {
    CloseablePathFactory jsonFactory =
        PathUtils.fromResource(ProducerTests.class, "Elite K70.json");
    JsonRectangularRowKeyboard layout = JsonRectangularKeyboardReader
        .rowKeyboard(jsonFactory.asByteSource().asCharSource(StandardCharsets.UTF_8));

    RectangularKeyboard physicalKeyboard =
        layout.toPhysicalKeyboard(Point.given(1.656d, 1.6d), Point.given(0.207d, 0.28d));
    SvgKeyboard svgK = SvgKeyboard.zonedFrom(physicalKeyboard);
    CloseablePathFactory f =
        PathUtils.fromResource(ProducerTests.class, "Rectangular Elite K70 unlabeled.svg");
    String expected = PathUtils.read(f);
    String result = DOM_HELPER.toString(svgK.document());
    // Files.writeString(Path.of("Rectangular Elite K70 unlabeled.svg"), result);
    assertEquals(expected, result);
  }
}
