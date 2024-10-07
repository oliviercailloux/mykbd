package io.github.oliviercailloux.mykbd;

import com.google.common.collect.ImmutableList;
import com.google.common.io.ByteSource;
import com.google.common.io.MoreFiles;
import io.github.oliviercailloux.jaris.xml.DomHelper;
import io.github.oliviercailloux.keyboardd.keyboard.json.JsonRectangularKeyboardReader;
import io.github.oliviercailloux.keyboardd.keyboard.json.JsonRectangularRowKeyboard;
import io.github.oliviercailloux.keyboardd.representable.RectangularKeyboard;
import io.github.oliviercailloux.keyboardd.representable.Representation;
import io.github.oliviercailloux.keyboardd.representable.SvgKeyboard;
import io.github.oliviercailloux.svgb.PositiveSize;
import java.nio.file.Files;
import java.nio.file.Path;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

public class XKeyNamed {
  @SuppressWarnings("unused")
  private static final Logger LOGGER = LoggerFactory.getLogger(XKeyNamed.class);
  
  private static final DomHelper DOM_HELPER = DomHelper.domHelper();

  public static void main(String[] args) throws Exception {
    ByteSource source =
        MoreFiles.asByteSource(Path.of("Keyboards").resolve("3 - Elite K70 unlabeled.svg"));
    SvgKeyboard svgKeyboard = SvgKeyboard.using(DOM_HELPER.asDocument(source));
    Document out = svgKeyboard.withRepresentations(x -> ImmutableList.of(Representation.fromString(x)));
    Files.writeString(Path.of("Keyboards").resolve("4 - Elite K70 with X key names.svg"),
        DOM_HELPER.toString(out));
  }
}
