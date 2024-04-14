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
  public static void main(String[] args) throws Exception {
    CharSource source = Resources.asCharSource(
        Path.of("Keyboard layout Elite K70.json").toUri().toURL(), StandardCharsets.UTF_8);
    JsonRectangularRowKeyboard layout = JsonRectangularKeyboardReader.rowKeyboard(source);

    RectangularKeyboard physicalKeyboard = layout.toPhysicalKeyboard(
        PositiveSize.given(1.656d, 1.6d), PositiveSize.given(0.207d, 0.28d));
    SvgKeyboard svgK = SvgKeyboard.zonedFrom(physicalKeyboard);
    String svg = DomHelper.domHelper().toString(svgK.document());
    Files.writeString(Path.of("Rectangular Elite K70 unlabeled.svg"), svg);
  }
}
