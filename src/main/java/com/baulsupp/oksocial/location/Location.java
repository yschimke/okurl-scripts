package com.baulsupp.oksocial.location;

import com.baulsupp.oksocial.util.UsageException;
import com.baulsupp.oksocial.util.Util;
import java.io.File;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import org.zeroturnaround.exec.ProcessExecutor;

/**
 * https://github.com/fulldecent/corelocationcli
 */
public class Location {
  public static final String LOCATION_APP = "/usr/local/bin/CoreLocationCLI";

  public final double latitude;
  public final double longitude;

  public Location(double latitude, double longitude) {
    this.latitude = latitude;
    this.longitude = longitude;
  }

  @Override public String toString() {
    return latitude + "," + longitude;
  }

  public static Optional<Location> read() {
    if (Util.isOSX()) {
      if (!new File(LOCATION_APP).exists()) {
        throw new UsageException("Missing " + LOCATION_APP);
      }

      try {
        String line = new ProcessExecutor().command(LOCATION_APP, "-format",
            "%latitude,%longitude", "-once", "yes")
            .readOutput(true).timeout(5, TimeUnit.SECONDS).execute().outputUTF8();

        String[] parts = line.trim().split(",");

        return Location.latLong(Double.parseDouble(parts[0]), Double.parseDouble(parts[1]));
      } catch (Exception e) {
        e.printStackTrace();
        return Optional.empty();
      }
    } else {
      return Optional.empty();
    }
  }

  private static Optional<Location> latLong(double latitude, double longitude) {
    Location l = new Location(latitude, longitude);

    return Optional.of(l);
  }
}
