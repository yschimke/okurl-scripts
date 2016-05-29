package com.baulsupp.oksocial.secrets;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.Set;

import static com.baulsupp.oksocial.util.Util.or;
import static com.google.common.collect.Sets.newHashSet;
import static java.util.Arrays.asList;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.joining;

public class Secrets {
  private static Secrets instance;

  private final Map<String, String> secrets;

  public Secrets(Map<String, String> secrets) {
    this.secrets = secrets;
  }

  public Optional<String> get(String key) {
    return ofNullable(secrets.get(key)).filter(s -> !s.isEmpty());
  }

  public static Secrets loadSecrets() {
    Properties p = new Properties();

    try {
      Path configFile =
          FileSystems.getDefault().getPath(System.getenv("HOME"), ".oksocial-secrets.properties");

      InputStream is;
      if (Files.exists(configFile)) {
        is = Files.newInputStream(configFile);
      } else {
        is = Secrets.class.getResourceAsStream("/oksocial-secrets.properties");
      }

      if (is != null) {
        p.load(is);
      }
    } catch (IOException e) {
      e.printStackTrace();
    }

    Secrets secrets = new Secrets(new HashMap(p));

    return secrets;
  }

  public static Optional<String> getDefined(String key) {
    if (instance == null) {
      instance = loadSecrets();
    }

    return instance.get(key);
  }

  public static String prompt(String name, String key, String defaultValue, boolean password) {
    Optional<String> defaulted = or(getDefined(key), () -> ofNullable(defaultValue));

    String prompt = name + defaultDisplay(defaulted, password) + ": ";

    String value = "";

    if (System.console() != null) {
      if (password) {
        value = new String(System.console().readPassword(prompt));
      } else {
        value = System.console().readLine(prompt);
      }
    }

    if (value.isEmpty()) {
      value = defaulted.orElse("");
    }

    return value;
  }

  public static Set<String> promptArray(String name, String key, Collection<String> defaults) {
    String valueString =
        prompt(name, key, defaults.stream().collect(joining(",")), false);
    return newHashSet(asList(valueString.split(",")));
  }

  private static String defaultDisplay(Optional<String> defaultValue, boolean password) {
    if (password) {
      defaultValue = defaultValue.map(s -> s.replaceAll(".", "\\*"));
    }

    return defaultValue.map(s -> " [" + s + "]").orElse("");
  }
}