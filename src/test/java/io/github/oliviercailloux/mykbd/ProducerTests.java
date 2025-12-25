package io.github.oliviercailloux.mykbd;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.io.CharSource;
import com.google.common.io.Resources;
import io.github.oliviercailloux.geometry.Size;
import io.github.oliviercailloux.jaris.io.PathUtils;
import io.github.oliviercailloux.jaris.xml.DomHelper;
import io.github.oliviercailloux.keyboardd.keyboard.json.JsonRectangularKeyboardReader;
import io.github.oliviercailloux.keyboardd.keyboard.json.JsonRectangularRowKeyboard;
import io.github.oliviercailloux.keyboardd.mapping.KeyboardMap;
import io.github.oliviercailloux.keyboardd.mapping.XkbKeymapDecomposer;
import io.github.oliviercailloux.keyboardd.mapping.XkbSymbolsReader;
import io.github.oliviercailloux.keyboardd.mnemonics.CanonicalKeyboardMap;
import io.github.oliviercailloux.keyboardd.mnemonics.Mnemonics;
import io.github.oliviercailloux.keyboardd.representable.CanonicalKeyboardMapRepresenter;
import io.github.oliviercailloux.keyboardd.representable.RectangularKeyboard;
import io.github.oliviercailloux.keyboardd.representable.Representation;
import io.github.oliviercailloux.keyboardd.representable.SvgKeyboard;
import io.github.oliviercailloux.keyboardd.representable.XKeyNamesAndRepresenter;
import io.github.oliviercailloux.keyboardd.xkeys.Xkeys;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

public class ProducerTests {
  @SuppressWarnings("unused")
  private static final Logger LOGGER = LoggerFactory.getLogger(ProducerTests.class);

  private static final DomHelper DOM_HELPER = DomHelper.domHelper();

  @Test
  void testJsonToRectangular() throws Exception {
    CharSource jsonInput = Resources.asCharSource(ProducerTests.class.getResource("Elite K70.json"),
        StandardCharsets.UTF_8);
    JsonRectangularRowKeyboard layout = JsonRectangularKeyboardReader.rowKeyboard(jsonInput);

    RectangularKeyboard physicalKeyboard =
        layout.toPhysicalKeyboard(Size.given(1.656d, 1.6d), Size.given(0.207d, 0.28d));
    SvgKeyboard svgK = SvgKeyboard.zonedFrom(physicalKeyboard);
    // Files.writeString(Path.of("Rectangular Elite K70 unlabeled.svg"),
    // DOM_HELPER.toString(svgK.document()));
    String expected = Resources
        .asCharSource(ProducerTests.class.getResource("Rectangular Elite K70 unlabeled.svg"),
            StandardCharsets.UTF_8)
        .read();
    String result = DOM_HELPER.toString(svgK.document());
    assertEquals(expected, result);
  }

  @Test
  void testUnlabeledToX() throws Exception {
    SvgKeyboard svgK = SvgKeyboard.using(DOM_HELPER.asDocument(
        Resources.asByteSource(ProducerTests.class.getResource("Elite K70 unlabeled.svg"))));
    String expected = Files.readString(
        Path.of(ProducerTests.class.getResource("Elite K70 with X key names.svg").toURI()));
    Document out = svgK.withRepresentations(x -> ImmutableList.of(Representation.fromString(x)));
    String result = DOM_HELPER.toString(out);
    assertEquals(expected, result);
  }

  @Test
  void testUnlabeledToFrench() throws Exception {
    SvgKeyboard inputSvg = SvgKeyboard.using(DOM_HELPER.asDocument(
        Resources.asByteSource(ProducerTests.class.getResource("Elite K70 unlabeled.svg"))));
    inputSvg.setFontSize(16d);

    KeyboardMap map = XkbSymbolsReader.common().overwrite(keyboardMap("fr", "oss"));
    CanonicalKeyboardMap canonMap = CanonicalKeyboardMap
        .canonicalize(map.canonicalize(Xkeys.latest().canonicalByAlias()), Mnemonics.latest());
    XKeyNamesAndRepresenter representer = CanonicalKeyboardMapRepresenter.from(canonMap,
        XKeyNamesAndRepresenter::defaultRepresentation);
    Document out = inputSvg.withRepresentations(representer::representations);

    String result = DOM_HELPER.toString(out);
    // Files.writeString(Path.of("Elite K70 French.svg"), result);
    String expected =
        Files.readString(Path.of(ProducerTests.class.getResource("Elite K70 French.svg").toURI()));
    assertEquals(expected, result);
  }

  KeyboardMap keyboardMap(String file, String part) throws IOException {
    ImmutableMap<String, String> bySymbolsMap = XkbKeymapDecomposer.bySymbolsMap(
        Resources.asCharSource(ProducerTests.class.getResource(file), StandardCharsets.UTF_8));
    String symbolsMap = bySymbolsMap.get(part);
    KeyboardMap mapped = XkbSymbolsReader.read(CharSource.wrap(symbolsMap));
    return mapped;
  }
}
