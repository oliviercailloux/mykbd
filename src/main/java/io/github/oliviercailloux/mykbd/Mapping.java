package io.github.oliviercailloux.mykbd;

import com.google.common.collect.ImmutableMap;
import io.github.oliviercailloux.keyboardd.mapping.KeysymEntry;
import io.github.oliviercailloux.keyboardd.representable.Representation;

public class Mapping {
  public static ImmutableMap<KeysymEntry, Representation> representations() {
    final ImmutableMap.Builder<KeysymEntry, Representation> reprsBuilder =
        new ImmutableMap.Builder<>();
    reprsBuilder.put(KeysymEntry.mnemonic("Tab"), Representation.fromString("↹"));
    reprsBuilder.put(KeysymEntry.mnemonic("ISO_Left_Tab"), Representation.fromString("␉"));
    reprsBuilder.put(KeysymEntry.mnemonic("Return"), Representation.fromString("⏎"));
    reprsBuilder.put(KeysymEntry.mnemonic("comma"), Representation.fromString(","));
    return reprsBuilder.build();
  }
}
