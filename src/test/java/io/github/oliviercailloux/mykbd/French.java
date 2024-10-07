package io.github.oliviercailloux.mykbd;

import com.google.common.io.ByteSource;
import com.google.common.io.MoreFiles;
import io.github.oliviercailloux.jaris.xml.DomHelper;
import io.github.oliviercailloux.keyboardd.mapping.KeyboardMap;
import io.github.oliviercailloux.keyboardd.mapping.XkbSymbolsReader;
import io.github.oliviercailloux.keyboardd.mnemonics.CanonicalKeyboardMap;
import io.github.oliviercailloux.keyboardd.mnemonics.Mnemonics;
import io.github.oliviercailloux.keyboardd.representable.SvgKeyboard;
import io.github.oliviercailloux.keyboardd.representable.XKeyNamesAndRepresenter;
import io.github.oliviercailloux.keyboardd.xkeys.Xkeys;
import java.nio.file.Files;
import java.nio.file.Path;
import org.w3c.dom.Document;

public class French {
  private static final DomHelper DOM_HELPER = DomHelper.domHelper();

  public static void main(String[] args) throws Exception {
    ByteSource source =
        MoreFiles.asByteSource(Path.of("Keyboards").resolve("3 - Elite K70 unlabeled.svg"));
    SvgKeyboard inputSvg = SvgKeyboard.using(DOM_HELPER.asDocument(source));
    inputSvg.setFontSize(9d);
    
    KeyboardMap map = XkbSymbolsReader.common().overwrite(XkbSymbolsReader.us());
    CanonicalKeyboardMap canonMap = CanonicalKeyboardMap
        .canonicalize(map.canonicalize(Xkeys.latest().canonicalByAlias()), Mnemonics.latest());
    XKeyNamesAndRepresenter representer =
        XKeyNamesAndRepresenter.from(canonMap, XKeyNamesAndRepresenter::defaultRepresentation);
    Document out = inputSvg.withRepresentations(representer::representations);

    Files.writeString(Path.of("Keyboards").resolve("6 - Elite K70 mapped.svg"),
        DOM_HELPER.toString(out));
  }
}
