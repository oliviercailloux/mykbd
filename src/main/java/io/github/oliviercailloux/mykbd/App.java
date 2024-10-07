package io.github.oliviercailloux.mykbd;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.google.common.io.CharSource;
import com.google.common.io.MoreFiles;
import io.github.oliviercailloux.jaris.xml.DomHelper;
import io.github.oliviercailloux.keyboardd.keyboard.json.JsonRectangularKeyboardReader;
import io.github.oliviercailloux.keyboardd.keyboard.json.JsonRectangularRowKeyboard;
import io.github.oliviercailloux.keyboardd.mapping.KeyboardMap;
import io.github.oliviercailloux.keyboardd.mapping.KeysymEntry;
import io.github.oliviercailloux.keyboardd.mapping.SimpleSymbolsReader;
import io.github.oliviercailloux.keyboardd.mnemonics.Mnemonics;
import io.github.oliviercailloux.keyboardd.representable.RectangularKeyboard;
import io.github.oliviercailloux.keyboardd.representable.Representation;
import io.github.oliviercailloux.keyboardd.representable.SvgKeyboard;
import io.github.oliviercailloux.keyboardd.representable.VisibleKeyboardMap;
import io.github.oliviercailloux.keyboardd.xkeys.Xkeys;
import io.github.oliviercailloux.svgb.PositiveSize;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import javax.xml.transform.stream.StreamSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

public class App {
  @SuppressWarnings("unused")
  private static final Logger LOGGER = LoggerFactory.getLogger(App.class);
  
  private static final DomHelper DOM_HELPER = DomHelper.domHelper();

  public static void main(String[] args) throws Exception {
    french();
  }

  private static void french() throws IOException {
    CharSource source = MoreFiles.asCharSource(Path.of("pc-fr"), StandardCharsets.UTF_8);
    KeyboardMap map = SimpleSymbolsReader.read(source);
    Xkeys xkeys = Xkeys.latest();
    Mnemonics mnemonics = Mnemonics.latest();
    UcpByCode byCode = UcpByCode.implicitAndExplicit(mnemonics);
    ImmutableCollection<KeysymEntry> entries = map.nameToEntries().values();
    ImmutableSet<String> entriesMn = entries.stream().filter(k -> k.kind() == Kind.MNEMONIC).map(k -> k.mnemonic().orElseThrow()).collect(ImmutableSet.toImmutableSet());
    ImmutableSet<String> mnemonicsKnown = mnemonics.byMnemonic().keySet();
    ImmutableSet<String> unknown = Sets.difference(entriesMn, mnemonicsKnown).immutableCopy();
    LOGGER.info("Unknown mnemonics: " + unknown);
    LOGGER.info("Unknown xkeys: " + Sets.difference(map.names(), xkeys.canonicals()));
    ImmutableMap<KeysymEntry, Representation> reprs = entries.stream().distinct().collect(ImmutableMap.toImmutableMap(e -> e, e -> App.represent(e, mnemonics, byCode)));
    VisibleKeyboardMap visible = VisibleKeyboardMap.from(map, reprs);
    SvgKeyboard svgK = SvgKeyboard.using(DOM_HELPER
        .asDocument(new StreamSource(Path.of("Elite K70 unlabeled.svg").toUri().toString())));
    svgK.setFontSize(16d);
    Document withRepresentations = svgK.withRepresentations(visible::representations);
    Files.writeString(Path.of("Elite K70 French.svg"), DOM_HELPER.toString(withRepresentations));
  }

  private static Representation represent(KeysymEntry entry, Mnemonics mnemonics, UcpByCode byCode) {
    if(entry.kind() == Kind.MNEMONIC) {
      String mn = entry.mnemonic().orElseThrow();
      LOGGER.info("Representing " + entry + " as mnemonic " + mn);
      if(!mnemonics.byMnemonic().containsKey(mn)) {
        return Representation.fromString("??");
      }
      int ucp =mnemonics.canonical(mn).ucp().orElse(0);
      LOGGER.info("Representing " + entry + " as UCP " + ucp);
      String s = new String(new int[] {ucp}, 0, 1);
      return Representation.fromString(s);
    }
    
    int code = entry.code().orElseThrow();
    if(byCode.hasUcp(code)) {
      int ucp = byCode.ucp(code);
      LOGGER.info("Representing " + entry + " as UCP " + ucp);
      String s = new String(new int[] {ucp}, 0, 1);
      return Representation.fromString(s);
    }
    return Representation.fromString("??");
  }

  private static void frenchManual() throws IOException {
    // Thanks to https://en.wikipedia.org/wiki/Whitespace_character
    CharSource source = MoreFiles.asCharSource(Path.of("pc-fr"), StandardCharsets.UTF_8);
    KeyboardMap map = SimpleSymbolsReader.read(source);
    final ImmutableMap.Builder<KeysymEntry, Representation> reprsBuilder =
        new ImmutableMap.Builder<>();
    reprsBuilder.put(KeysymEntry.mnemonic("comma"), Representation.fromString(","));
    ImmutableMap<KeysymEntry, Representation> reprs = Mapping.representations();
    VisibleKeyboardMap visible = VisibleKeyboardMap.from(map, reprs);
    SvgKeyboard svgK = SvgKeyboard.using(DOM_HELPER
        .asDocument(new StreamSource(Path.of("Elite K70 unlabeled.svg").toUri().toString())));
    svgK.setFontSize(16d);
    Document withRepresentations = svgK.withRepresentations(visible::representations);
    Files.writeString(Path.of("Elite K70 French.svg"), DOM_HELPER.toString(withRepresentations));
  }
}
