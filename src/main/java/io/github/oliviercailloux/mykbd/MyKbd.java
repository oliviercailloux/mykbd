package io.github.oliviercailloux.mykbd;

import static com.google.common.base.Verify.verify;

import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.io.MoreFiles;
import com.google.common.io.Resources;
import io.github.oliviercailloux.geometry.Displacement;
import io.github.oliviercailloux.jaris.xml.DomHelper;
import io.github.oliviercailloux.keyboardd.mapping.KeyboardMap;
import io.github.oliviercailloux.keyboardd.mapping.KeysymEntry;
import io.github.oliviercailloux.keyboardd.mapping.XkbSymbolsReader;
import io.github.oliviercailloux.keyboardd.mnemonics.CanonicalKeyboardMap;
import io.github.oliviercailloux.keyboardd.mnemonics.Mnemonics;
import io.github.oliviercailloux.keyboardd.representable.SvgKeyboard;
import io.github.oliviercailloux.keyboardd.representable.XKeyNamesAndRepresenter;
import io.github.oliviercailloux.keyboardd.xkeys.Xkeys;
import io.github.oliviercailloux.svgb.RectangleElement;
import io.github.oliviercailloux.svgb.StyleElement;
import io.github.oliviercailloux.svgb.SvgDocumentHelper;
import io.github.oliviercailloux.svgb.TextElement;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class MyKbd {
  private static final DomHelper DOM_HELPER = DomHelper.domHelper();

  public static MyKbd create() throws IOException {
    return new MyKbd();
  }

  private KeyboardMap keyboardMap;
  private SvgKeyboard representedKeyboard;
  private SvgDocumentHelper svgHelper;
  private Displacement acrossFirstRect;
  private double up;
  private double middle;
  private double down;

  private MyKbd() throws IOException {
    keyboardMap = MyKbd.keyboardMap();

    SvgKeyboard inputSvg = SvgKeyboard.using(DOM_HELPER.asDocument(
        Resources.asByteSource(MyKbd.class.getResource("Elite K70 unlabeled styled.svg"))));
    inputSvg.setFontSize(16d);

    KeyboardMap map = XkbSymbolsReader.common().overwrite(keyboardMap);
    CanonicalKeyboardMap canonMap = CanonicalKeyboardMap
        .canonicalize(map.canonicalize(Xkeys.latest().canonicalByAlias()), Mnemonics.latest());
    XKeyNamesAndRepresenter representer =
        XKeyNamesAndRepresenter.from(canonMap, XKeyNamesAndRepresenter::defaultRepresentation);
    Document represented = inputSvg.withRepresentations(representer::representations);
    representedKeyboard = SvgKeyboard.using(represented);
    svgHelper = SvgDocumentHelper.using(represented);
    acrossFirstRect = representedKeyboard.keyBindingZonesByXKeyName().values().iterator().next().zone().across();
    up = -5d;
    middle = acrossFirstRect.y() / 2d + 3d;
    down = acrossFirstRect.y() + 5d;
  }

  public void write() throws IOException {
    String result = DOM_HELPER.toString(representedKeyboard.document());
    Files.writeString(Path.of("Elite K70 French.svg"), result);
  }

  private Element root() {
    return representedKeyboard.document().getDocumentElement();
  }

  public void patch() {
    StyleElement style = svgHelper.style().setContent("""
        text.annotation {
          font-size: 60%;
          text-anchor: start;
        }""");
    root().insertBefore(style.element(), root().getFirstChild());
    annotate("TLDE", 4, 5d);
    annotate("AE01", 4, 0d);
    annotate("AE01", 3, 0d);
    annotate("AE03", 4, 10d);
    annotate("AE04", 4, 15d);
    annotate("AE05", 4, 5d);
    annotate("AE06", 4, -10d);
    annotate("AE06", 1, 10d);
    annotate("AE07", 3, 20d);
    annotate("AE11", 2, 0d);
    annotate("AD03", 4, 20d);
    annotate("AD05", 4, 2d);
    annotate("AD05", 3, 10d);
    annotate("AD11", 2, -35d);
    annotate("AD11", 4, 35d);
    annotate("AD11", 3, 20d);
    annotate("AD12", 4, 45d);
    annotate("AD12", 3, Displacement.given(29d, middle));
    annotate("AC02", 4, -10d);
    annotate("AC04", 4, -40d);
    annotate("AC04", 3, -40d);
    annotate("AC05", 3, -10d);
    annotate("AC11", 3, 40d);
    annotate("BKSL", 4, 5d);
    annotate("BKSL", 3, 40d);
    annotate("AB01", 4, -45d);
    annotate("AB01", 3, -20d);
    annotate("AB02", 4, -15d);
    annotate("AB02", 3, -20d);
    annotate("AB04", 3, -40d);
    annotate("AB08", 4, 10d);
    annotate("AB09", 4, 15d);
    annotate("AB10", 4, 30d);
    annotate("SPCE", 4, 280d);
  }

  private void annotate(String x, int symOrderNumber, double xShift) {
    if (symOrderNumber % 2 == 0) {
      annotate(x, symOrderNumber, Displacement.given(xShift, up));
    } else {
      annotate(x, symOrderNumber, Displacement.given(xShift, down));
    }
  }
  
  private void annotate(String x, int symOrderNumber, Displacement move) {
    ImmutableSet<RectangleElement> rects = representedKeyboard.keyBindingZonesByXKeyName().get(x);
    RectangleElement rect = Iterables.getOnlyElement(rects);
    KeysymEntry keysym = keyboardMap.entries(x).get(symOrderNumber - 1);
    String sym;
    if (keysym instanceof KeysymEntry.Ucp u) {
      sym = Character.getName(u.ucp());
    } else {
      sym = keysym.asString();
    }
    TextElement t =
        svgHelper.text().setBaselineStart(rect.zone().start().plus(move)).setContent(sym);
    t.element().setAttribute("class", "annotation");
    verify(t.element().getOwnerDocument().equals(rect.element().getOwnerDocument()));
    rect.element().getParentNode().insertBefore(t.element(), rect.element());
  }

  private static KeyboardMap keyboardMap() throws IOException {
    return XkbSymbolsReader.read(MoreFiles.asCharSource(
        Path.of(System.getProperty("user.home")).resolve(".config/xkb/symbols/mir"),
        Charsets.UTF_8));
  }
}
