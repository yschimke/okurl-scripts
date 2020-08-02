#!/usr/bin/env okscript

@file:Repository("https://jitpack.io")
@file:DependsOn("com.github.yschimke:okurl-script:1.0.0")

import com.baulsupp.okscript.client
import com.baulsupp.okscript.query
import com.baulsupp.okscript.runScript
import com.baulsupp.okscript.show
import com.baulsupp.okurl.location.Location
import com.baulsupp.okurl.services.mapbox.staticMap

data class Toilet(
  val AccessibleFemale: Int,
  val AccessibleMale: Int,
  val AccessibleNote: String?,
  val AccessibleParkingNote: String?,
  val AccessibleUnisex: Int,
  val AccessNote: String?,
  val Address: String?,
  val AddressNote: String?,
  val FacilityType: String?,
  val Female: Int,
  val IconURL: String?,
  val IsOpen: String,
  val KeyRequired: Int,
  val Latitude: Double,
  val Longitude: Double,
  val Male: Int,
  val Name: String,
  val Notes: String?,
  val OpeningHoursNote: String?,
  val OpeningHoursSchedule: String?,
  val Parking: Int,
  val ParkingAccessible: Int,
  val ParkingNote: String?,
  val PaymentRequired: Int,
  val Postcode: Int,
  val Showers: Int,
  val State: String,
  val Status: String,
  val ToiletType: String?,
  val Town: String,
  val Unisex: Int,
  val Url: String?
) {
  val location = Location(Latitude, Longitude)
}

data class ToiletResultSet(val rows: List<Toilet>)

val postcode = args[0]

runScript {
  val toilets = client.query<ToiletResultSet>(
    "https://australian-dunnies.now.sh/australian-dunnies/dunnies.jsono?Postcode=$postcode"
  ).rows

  show(staticMap {
    pinLocations(toilets.map { it.location }, "s-hospital")
  })
}
