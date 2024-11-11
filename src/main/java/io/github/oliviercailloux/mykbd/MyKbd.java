package io.github.oliviercailloux.mykbd;

import static com.google.common.base.Verify.verify;

import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.io.MoreFiles;
import com.google.common.io.Resources;
import io.github.oliviercailloux.geometry.Displacement;
import io.github.oliviercailloux.jaris.xml.DomHelper;
import io.github.oliviercailloux.keyboardd.mapping.KeyboardMap;
import io.github.oliviercailloux.keyboardd.mapping.XkbSymbolsReader;
import io.github.oliviercailloux.keyboardd.mnemonics.CanonicalKeyboardMap;
import io.github.oliviercailloux.keyboardd.mnemonics.CanonicalKeysymEntry;
import io.github.oliviercailloux.keyboardd.mnemonics.CanonicalMnemonic;
import io.github.oliviercailloux.keyboardd.mnemonics.ImplicitUcp;
import io.github.oliviercailloux.keyboardd.mnemonics.Mnemonics;
import io.github.oliviercailloux.keyboardd.representable.CanonicalKeyboardMapRepresenter;
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
import java.util.Optional;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class MyKbd {
  private static final DomHelper DOM_HELPER = DomHelper.domHelper();

  public static MyKbd create() throws IOException {
    return new MyKbd();
  }

  private CanonicalKeyboardMap canonMap;
  private SvgKeyboard representedKeyboard;
  private SvgDocumentHelper svgHelper;
  private Displacement acrossFirstRect;
  private double up;
  private double middle;
  private double down;

  private MyKbd() throws IOException {
    KeyboardMap keyboardMap = MyKbd.keyboardMap();

    SvgKeyboard inputSvg = SvgKeyboard.using(DOM_HELPER.asDocument(
        Resources.asByteSource(MyKbd.class.getResource("Elite K70 unlabeled styled.svg"))));
    inputSvg.setFontSize(16d);

    KeyboardMap map = XkbSymbolsReader.common().overwrite(keyboardMap);
    canonMap = CanonicalKeyboardMap
        .canonicalize(map.canonicalize(Xkeys.latest().canonicalByAlias()), Mnemonics.latest());
    XKeyNamesAndRepresenter representer =
        CanonicalKeyboardMapRepresenter.from(canonMap, XKeyNamesAndRepresenter::defaultRepresentation);
    Document represented = inputSvg.withRepresentations(representer::representations);
    representedKeyboard = SvgKeyboard.using(represented);
    svgHelper = SvgDocumentHelper.using(represented);
    acrossFirstRect =
        representedKeyboard.keyBindingZonesToXKeyName().keySet().iterator().next().zone().across();
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
        }
        text.ucp {
          text-transform: lowercase;
          font-size: 40%;
        }
        text.ucp::first-letter {
          text-transform: uppercase;
        }
        """);
    root().insertBefore(style.element(), root().getFirstChild());
    annotate("TLDE", 4, 5d);
    annotate("AE01", 4, 0d);
    annotate("AE01", 3, 0d);
    annotate("AE03", 4, 10d);
    annotate("AE04", 4, 15d);
    annotate("AE04", 5, 35d);
    annotate("AE05", 4, 5d);
    annotate("AE06", 4, -10d);
    annotate("AE06", 1, 5d);
    annotate("AE07", 3, 20d);
    annotate("AE11", 2, 0d);
    annotate("AD03", 4, 20d);
    annotate("AD05", 4, -40d);
    annotate("AD05", 3, 10d);
    annotate("AD11", 2, -35d);
    annotate("AD11", 4, 35d);
    annotate("AD11", 3, 20d);
    annotate("AD12", 4, 45d);
    annotate("AD12", 3, Displacement.given(2d, middle), "font-size: 25%;");
    annotate("AC02", 4, -10d);
    annotate("AC04", 4, -40d);
    annotate("AC04", 3, -40d);
    annotate("AC05", 3, -10d);
    annotate("AC11", 3, 40d);
    annotate("BKSL", 4, 5d);
    annotate("BKSL", 3, 40d);
    annotate("AB01", 4, -45d);
    annotate("AB01", 3, -90d);
    annotate("AB02", 4, -15d);
    annotate("AB02", 3, -20d);
    annotate("AB04", 3, -10d);
    annotate("AB08", 4, 20d);
    annotate("AB09", 4, 10d);
    annotate("AB09", 6, 55d);
    annotate("AB10", 4, 30d);
    annotate("SPCE", 4, 280d);
  }

  private void annotate(String x, int symOrderNumber, double xShift) {
    if (symOrderNumber % 2 == 0) {
      annotate(x, symOrderNumber, Displacement.given(xShift, up), "");
    } else {
      annotate(x, symOrderNumber, Displacement.given(xShift, down), "");
    }
  }

  private void annotate(String x, int symOrderNumber, Displacement move, String style) {
    ImmutableSet<RectangleElement> rects = representedKeyboard.keyBindingZonesToXKeyName().asMultimap().inverse().get(x);
    RectangleElement rect = Iterables.getOnlyElement(rects);
    CanonicalKeysymEntry keysym = canonMap.entries(x).get(symOrderNumber - 1);
    String sym;
    String link;
    ImmutableList<String> classes;
    if (keysym instanceof ImplicitUcp u) {
      sym = Character.getName(u.ucp());
      link = "https://www.fileformat.info/info/unicode/char/" + Integer.toHexString(u.ucp()) + "/";
      classes = ImmutableList.of("annotation", "ucp");
    } else {
      CanonicalMnemonic mnemonic = (CanonicalMnemonic) keysym;
      Optional<Integer> ucp = mnemonic.ucp();
      if (ucp.isPresent()) {
        sym = Character.getName(ucp.get());
        link =
            "https://www.fileformat.info/info/unicode/char/" + Integer.toHexString(ucp.get()) + "/";
        classes = ImmutableList.of("annotation", "ucp");
      } else {
        sym = mnemonic.mnemonic();
        link = "";
        classes = ImmutableList.of("annotation");
      }
    }
    TextElement t =
        svgHelper.text().setBaselineStart(rect.zone().start().plus(move)).setContent(sym);
    t.element().setAttribute("class", String.join(" ", classes));
    t.element().setAttribute("style", style);
    verify(t.element().getOwnerDocument().equals(rect.element().getOwnerDocument()));
    Element toInsert;
    if (!link.isBlank()) {
      Element a = t.element().getOwnerDocument().createElement("a");
      a.setAttribute("href", link);
      a.appendChild(t.element());
      toInsert = a;
    } else {
      toInsert = t.element();
    }
    /* We insert after the rectangle to ensure that it is on top of the rectangle. */
    rect.element().getParentNode().insertBefore(toInsert, rect.element().getNextSibling());
  }

  private static KeyboardMap keyboardMap() throws IOException {
    return XkbSymbolsReader.read(MoreFiles.asCharSource(
        Path.of(System.getProperty("user.home")).resolve(".config/xkb/symbols/mir"),
        Charsets.UTF_8));
  }
}
