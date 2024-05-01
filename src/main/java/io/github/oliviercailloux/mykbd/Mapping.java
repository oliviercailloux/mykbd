package io.github.oliviercailloux.mykbd;

public class Mapping {
  public static ImmutableMap<KeysymEntry, Representation> representations() {
    final ImmutableMap.Builder<KeysymEntry, Representation> reprsBuilder =
        new ImmutableMap.Builder<>();
    reprsBuilder.put(KeysymEntry.mnemonic("comma"), Representation.fromString(","));
    return reprsBuilder.build();
  }
}
