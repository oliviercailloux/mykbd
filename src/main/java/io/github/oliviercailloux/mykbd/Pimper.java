package io.github.oliviercailloux.mykbd;

import static com.google.common.base.Verify.verify;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import io.github.oliviercailloux.geometry.Point;
import io.github.oliviercailloux.keyboardd.mnemonics.CanonicalKeysymEntry;
import io.github.oliviercailloux.keyboardd.mnemonics.CanonicalMnemonic;
import io.github.oliviercailloux.keyboardd.mnemonics.ImplicitUcp;
import io.github.oliviercailloux.keyboardd.representable.SvgKeysymEntry;
import io.github.oliviercailloux.keyboardd.representable.SvgRepresentedKeyboard;
import io.github.oliviercailloux.keyboardd.representable.SvgXKey;
import io.github.oliviercailloux.svgb.RectangleElement;
import io.github.oliviercailloux.svgb.SvgDocumentHelper;
import io.github.oliviercailloux.svgb.SvgHelper;
import io.github.oliviercailloux.svgb.TextElement;
import java.util.Optional;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;

public class Pimper {
  public static enum Position {
    TOP, BOTTOM
  }

  @SuppressWarnings("unused")
  private static final Logger LOGGER = LoggerFactory.getLogger(Pimper.class);

  public static Pimper create(SvgRepresentedKeyboard representedKeyboard) {
    return new Pimper(representedKeyboard);
  }

  private final SvgRepresentedKeyboard representedKeyboard;
  private final SvgDocumentHelper svgHelper;

  private Pimper(SvgRepresentedKeyboard representedKeyboard) {
    this.representedKeyboard = representedKeyboard;
    svgHelper = SvgDocumentHelper.using(representedKeyboard.document());
  }

  public SvgKeysymEntry find(String xKeyName, int entryNumber) {
    ImmutableSet<SvgXKey> keys = representedKeyboard.svgXKeys(xKeyName);
    SvgXKey key = Iterables.getOnlyElement(keys);
    return key.svgKeysymEntries().asList().get(entryNumber);
  }

  public void pimp(SvgKeysymEntry entry, Set<String> classes, String style) {
    RectangleElement r = svgHelper.rectangle(entry.zone());
    if (!classes.isEmpty()) {
      SvgHelper.setClasses(r.element(), classes);
    }
    r.element().setAttribute("style", style);
    verify(r.element().getOwnerDocument().equals(entry.svgElement().getOwnerDocument()));
    Element xKeyElement = entry.xKey().rectangle().element();
    xKeyElement.getParentNode().insertBefore(r.element(), xKeyElement.getNextSibling());
  }

  public void highlightAll() {
    for (SvgXKey xKey : representedKeyboard.svgXKeysToXKeyName().keySet()) {
      int count = 0;
      for (SvgKeysymEntry entry : xKey.svgKeysymEntries()) {
        boolean primary = count % 4 == 0 || count % 4 == 3;
        String style = "fill: " + (primary ? "yellow" : "green") + "; fill-opacity: 0.7;";
        pimp(entry, ImmutableSet.of(), style);
        ++count;
      }
    }
  }

  public void linkAll() {
    for (SvgXKey xKey : representedKeyboard.svgXKeysToXKeyName().keySet()) {
      for (SvgKeysymEntry entry : xKey.svgKeysymEntries()) {
        if (entry.canonicalKeysymEntry().probeUcp().isPresent()) {
          link(entry);
        }
      }
    }
  }

  public void link(SvgKeysymEntry entry) {
    CanonicalKeysymEntry keysym = entry.canonicalKeysymEntry();
    int ucp = keysym.probeUcp().orElseThrow(IllegalArgumentException::new);
    Element a = entry.svgElement().getOwnerDocument().createElement("a");
    a.setAttribute("href", linkTo(ucp));
    entry.svgElement().getParentNode().insertBefore(a, entry.svgElement());
    a.appendChild(entry.svgElement());
  }

  public void annotate(SvgKeysymEntry entry) {
    int entryNumber = entry.xKey().svgKeysymEntries().asList().indexOf(entry);
    Position entryPosition = entryNumber % 2 == 0 ? Position.BOTTOM : Position.TOP;
    annotate(entry, entryPosition, 0);
  }

  public void annotate(SvgKeysymEntry entry, Position position, int xShift) {
    int entryNumber = entry.xKey().svgKeysymEntries().asList().indexOf(entry);
    Position entryPosition = entryNumber % 2 == 0 ? Position.BOTTOM : Position.TOP;
    boolean center = entryPosition != position;

    CanonicalKeysymEntry keysym = entry.canonicalKeysymEntry();
    final Optional<Integer> ucp = keysym.probeUcp();

    ImmutableSet.Builder<String> classesBuilder = ImmutableSet.<String>builder();
    classesBuilder.add("annotation_" + position.toString().toLowerCase());
    if (ucp.isPresent()) {
      classesBuilder.add("ucp");
    }
    final ImmutableSet<String> classes = classesBuilder.build();

    final String sym;
    if (ucp.isPresent()) {
      sym = Character.getName(ucp.orElseThrow());
    } else {
      sym = ((CanonicalMnemonic) keysym).mnemonic();
    }

    final Point textPos;
    double x = entry.zone().center().x() + xShift;
    double y;
    if (position == Position.TOP) {
      y = entry.zone().start().y() - (center ? -3 : 2);
    } else {
      y = entry.zone().end().y() + (center ? -2 : 4);
    }
    textPos = Point.given(x, y);

    TextElement t = svgHelper.text().setBaselineStart(textPos).setContent(sym);
    SvgHelper.setClasses(t.element(), classes);
    t.element().setAttribute("style", "");
    verify(t.element().getOwnerDocument().equals(entry.svgElement().getOwnerDocument()));
    Element toInsert;
    if (ucp.isPresent()) {
      Element a = t.element().getOwnerDocument().createElement("a");
      a.setAttribute("href", linkTo(ucp.orElseThrow()));
      a.appendChild(t.element());
      toInsert = a;
    } else {
      toInsert = t.element();
    }
    Element xKeyElement = entry.xKey().rectangle().element();
    xKeyElement.getParentNode().insertBefore(toInsert, xKeyElement.getNextSibling());
  }

  private String linkTo(int ucpNb) {
    return "https://www.fileformat.info/info/unicode/char/" + Integer.toHexString(ucpNb) + "/";
  }
}
