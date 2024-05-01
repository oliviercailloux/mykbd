package io.github.oliviercailloux.mykbd;

import com.google.common.collect.ImmutableMap;
import com.google.common.io.CharSource;
import com.google.common.io.MoreFiles;
import io.github.oliviercailloux.jaris.xml.DomHelper;
import io.github.oliviercailloux.keyboardd.keyboard.RectangularKeyboard;
import io.github.oliviercailloux.keyboardd.keyboard.json.JsonRectangularKeyboardReader;
import io.github.oliviercailloux.keyboardd.keyboard.json.JsonRectangularRowKeyboard;
import io.github.oliviercailloux.keyboardd.mapping.KeyboardMap;
import io.github.oliviercailloux.keyboardd.mapping.KeysymEntry;
import io.github.oliviercailloux.keyboardd.mapping.SimpleSymbolsReader;
import io.github.oliviercailloux.keyboardd.representable.Representation;
import io.github.oliviercailloux.keyboardd.representable.SvgKeyboard;
import io.github.oliviercailloux.keyboardd.representable.VisibleKeyboardMap;
import io.github.oliviercailloux.svgb.PositiveSize;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import javax.xml.transform.stream.StreamSource;
import org.w3c.dom.Document;

public class App {
  private static final DomHelper DOM_HELPER = DomHelper.domHelper();

  public static void main(String[] args) throws Exception {
    rectangular();
    french();
  }

  public static void rectangular() throws IOException {
    CharSource source =
        MoreFiles.asCharSource(Path.of("Keyboard layout Elite K70.json"), StandardCharsets.UTF_8);
    JsonRectangularRowKeyboard layout = JsonRectangularKeyboardReader.rowKeyboard(source);

    RectangularKeyboard physicalKeyboard = layout
        .toPhysicalKeyboard(PositiveSize.given(1.656d, 1.6d), PositiveSize.given(0.207d, 0.28d));
    SvgKeyboard svgK = SvgKeyboard.zonedFrom(physicalKeyboard);
    Files.writeString(Path.of("Rectangular Elite K70 unlabeled.svg"),
        DOM_HELPER.toString(svgK.document()));
  }

  private static void french() throws IOException {
    // Thanks to https://en.wikipedia.org/wiki/Whitespace_character
    CharSource source = MoreFiles.asCharSource(Path.of("pc-fr"), StandardCharsets.UTF_8);
    KeyboardMap map = SimpleSymbolsReader.read(source);
    final ImmutableMap.Builder<KeysymEntry, Representation> reprsBuilder =
        new ImmutableMap.Builder<>();
    reprsBuilder.put(KeysymEntry.mnemonic("comma"), Representation.fromString(","));
    ImmutableMap<KeysymEntry, Representation> reprs = reprsBuilder.build();
    VisibleKeyboardMap visible = VisibleKeyboardMap.from(map, reprs);
    SvgKeyboard svgK = SvgKeyboard.using(DOM_HELPER
        .asDocument(new StreamSource(Path.of("Elite K70 unlabeled.svg").toUri().toString())));
    svgK.setFontSize(16d);
    Document withRepresentations = svgK.withRepresentations(visible::representations);
    Files.writeString(Path.of("Elite K70 French.svg"), DOM_HELPER.toString(withRepresentations));
  }
}
