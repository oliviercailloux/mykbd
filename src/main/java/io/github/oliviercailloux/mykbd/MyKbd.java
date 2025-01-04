package io.github.oliviercailloux.mykbd;

import static com.google.common.base.Verify.verify;

import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.io.MoreFiles;
import com.google.common.io.Resources;
import io.github.oliviercailloux.geometry.Displacement;
import io.github.oliviercailloux.geometry.Point;
import io.github.oliviercailloux.keyboardd.mapping.KeyboardMap;
import io.github.oliviercailloux.keyboardd.mapping.XkbSymbolsReader;
import io.github.oliviercailloux.keyboardd.mnemonics.CanonicalKeyboardMap;
import io.github.oliviercailloux.keyboardd.mnemonics.CanonicalKeysymEntry;
import io.github.oliviercailloux.keyboardd.mnemonics.CanonicalMnemonic;
import io.github.oliviercailloux.keyboardd.mnemonics.ImplicitUcp;
import io.github.oliviercailloux.keyboardd.mnemonics.Mnemonics;
import io.github.oliviercailloux.keyboardd.representable.CanonicalKeyboardMapRepresenter;
import io.github.oliviercailloux.keyboardd.representable.SvgKeyboard;
import io.github.oliviercailloux.keyboardd.representable.SvgRepresentedKeyboard;
import io.github.oliviercailloux.keyboardd.representable.SvgXKey;
import io.github.oliviercailloux.keyboardd.representable.XKeyNamesAndRepresenter;
import io.github.oliviercailloux.keyboardd.xkeys.Xkeys;
import io.github.oliviercailloux.mykbd.Pimper.Position;
import io.github.oliviercailloux.svgb.RectangleElement;
import io.github.oliviercailloux.svgb.StyleElement;
import io.github.oliviercailloux.svgb.SvgDocumentHelper;
import io.github.oliviercailloux.svgb.TextElement;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;

public class MyKbd {
  @SuppressWarnings("unused")
  private static final Logger LOGGER = LoggerFactory.getLogger(MyKbd.class);
  

  public static MyKbd create() throws IOException {
    return new MyKbd();
  }

  private CanonicalKeyboardMap canonMap;
  private SvgRepresentedKeyboard representedKeyboard;
  private Point sizeFirstRect;
  private double up;
  private double middle;
  private double down;

  private MyKbd() throws IOException {
    KeyboardMap keyboardMap = MyKbd.keyboardMap();

    SvgKeyboard inputSvg = SvgKeyboard.using(App.DOM_HELPER.asDocument(
        Resources.asByteSource(MyKbd.class.getResource("Elite K70 unlabeled styled.svg"))));
    inputSvg.setFontSize(16d);

    KeyboardMap map = XkbSymbolsReader.common().overwrite(keyboardMap);
    canonMap = CanonicalKeyboardMap
        .canonicalize(map.canonicalize(Xkeys.latest().canonicalByAlias()), Mnemonics.latest());
        CanonicalKeyboardMapRepresenter representer =
        CanonicalKeyboardMapRepresenter.from(canonMap, XKeyNamesAndRepresenter::defaultRepresentation);
        representedKeyboard = inputSvg.withCanonicalRepresentations(representer);
    sizeFirstRect =
        representedKeyboard.svgXKeysToXKeyName().keySet().iterator().next().keyZone().size();
    up = -5d;
    middle = sizeFirstRect.y() / 2d + 3d;
    down = sizeFirstRect.y() + 5d;
  }

  private SvgDocumentHelper svgHelper() {
    return SvgDocumentHelper.using(representedKeyboard.document());
  }

  public void write() throws IOException {
    String result = App.DOM_HELPER.toString(representedKeyboard.document());
    Files.writeString(Path.of("Elite K70 French.svg"), result);
  }

  private Element root() {
    return representedKeyboard.document().getDocumentElement();
  }

  public void patch() {
    StyleElement style = svgHelper().style().setContent("""
        text.annotation_top {
          font-size: 50%;
          text-anchor: middle;
          dominant-baseline: alphabetic;
        }
        text.annotation_bottom {
          font-size: 50%;
          text-anchor: middle;
        }
        text.ucp {
          text-transform: lowercase;
          font-size: 50%;
        }
        text.ucp::first-letter {
          text-transform: uppercase;
        }
        """);

        representedKeyboard.document().getDocumentElement().insertBefore(style.element(), root().getFirstChild());

    Pimper pimper = Pimper.create(representedKeyboard);
    pimper.linkAll();
    // pimper.highlightAll();
    pimper.annotate(pimper.find("TLDE", 3));

    pimper.annotate(pimper.find("AE01", 3));
    pimper.annotate(pimper.find("AE01", 2));
    pimper.annotate(pimper.find("AE02", 2));
    pimper.annotate(pimper.find("AE03", 3));
    pimper.annotate(pimper.find("AE04", 3));
    pimper.annotate(pimper.find("AE04", 4));
    pimper.annotate(pimper.find("AE05", 3));
    pimper.annotate(pimper.find("AE06", 3));
    pimper.annotate(pimper.find("AE06", 0));
    pimper.annotate(pimper.find("AE07", 2));
    pimper.annotate(pimper.find("AE11", 1));
    pimper.annotate(pimper.find("AD03", 3));
    pimper.annotate(pimper.find("AD05", 2), Position.TOP, 0);
    pimper.annotate(pimper.find("AD11", 1));
    pimper.annotate(pimper.find("AD11", 3), Position.BOTTOM, 0);
    pimper.annotate(pimper.find("AD11", 2));
    pimper.annotate(pimper.find("AD12", 3));
    pimper.annotate(pimper.find("AC02", 3));
    pimper.annotate(pimper.find("AC04", 3));
    pimper.annotate(pimper.find("AC04", 2));
    // pimper.annotate(pimper.find("AC05", 2));
    pimper.annotate(pimper.find("AC11", 2));
    pimper.annotate(pimper.find("BKSL", 3));
    pimper.annotate(pimper.find("BKSL", 2));
    pimper.annotate(pimper.find("AB01", 3));
    pimper.annotate(pimper.find("AB01", 2));
    // pimper.annotate(pimper.find("AB02", 3));
    // pimper.annotate(pimper.find("AB02", 2));
    pimper.annotate(pimper.find("AB04", 2));
    pimper.annotate(pimper.find("AB07", 5), Position.BOTTOM, 0);
    pimper.annotate(pimper.find("AB08", 2), Position.BOTTOM, -30);
    pimper.annotate(pimper.find("AB08", 3), Position.TOP, 0);
    pimper.annotate(pimper.find("AB08", 4), Position.BOTTOM, 40);
    pimper.annotate(pimper.find("AB08", 5), Position.BOTTOM, -5);
    pimper.annotate(pimper.find("AB09", 1));
    pimper.annotate(pimper.find("AB09", 3), Position.BOTTOM, 2);
    pimper.annotate(pimper.find("AB09", 5), Position.TOP, 17);
    pimper.annotate(pimper.find("AB09", 6), Position.BOTTOM, 12);
    pimper.annotate(pimper.find("AB10", 3), Position.BOTTOM, -1);
    pimper.annotate(pimper.find("SPCE", 3), Position.BOTTOM, 0);
  }

  private void annotate(String x, int symOrderNumber, double xShift) {
    if (symOrderNumber % 2 == 0) {
      annotate(x, symOrderNumber, Displacement.given(xShift, up), "");
    } else {
      annotate(x, symOrderNumber, Displacement.given(xShift, down), "");
    }
  }

  private void annotate(String x, int symOrderNumber, Displacement move, String style) {
    ImmutableSet<SvgXKey> keys = representedKeyboard.svgXKeys(x);
    SvgXKey key = Iterables.getOnlyElement(keys);
    RectangleElement rect = key.rectangle();
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
        svgHelper().text().setBaselineStart(rect.zone().start().plus(move)).setContent(sym);
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

  public SvgRepresentedKeyboard representedKeyboard() {
    return representedKeyboard;
  }
}
