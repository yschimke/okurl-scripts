#!/usr/bin/env -S kotlinc-jvm -nowarn -script

@file:Repository("https://jitpack.io")
@file:DependsOn("com.github.yschimke:okurl-script:1.0.0")
@file:DependsOn("com.jakewharton.picnic:picnic:0.4.0")
@file:CompilerOptions("-jvm-target", "1.8")

import com.baulsupp.okscript.client
import com.baulsupp.okscript.query
import com.baulsupp.okscript.runScript
import com.baulsupp.okurl.location.Location
import com.jakewharton.picnic.BorderStyle
import com.jakewharton.picnic.renderText
import com.jakewharton.picnic.table

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
  val gender: String
  get() = buildString {
    if (Male != 0) {
      append("👨")
    }
    if (Female != 0) {
      append("👩")
    }
  }

  val location = Location(latitude = Latitude, longitude = Longitude)
}

data class ToiletResultSet(val rows: List<Toilet>)

val postcode = args.getOrElse(0) { "2001" }

runScript {
  val toilets = client.query<ToiletResultSet>(
    "https://australian-dunnies.now.sh/australian-dunnies/dunnies.json?_shape=objects&_Postcode=$postcode"
  ).rows

  val table = table {
    style {
      borderStyle = BorderStyle.Solid
    }
    cellStyle {
      // These options affect the style of all cells contained within the table.
      border = true
      paddingLeft = 1
      paddingRight = 1
    }
    header {
      row("Name", "Address", "Gender")
    }
    for (toilet in toilets) {
      row(toilet.Name, toilet.Address, toilet.gender)
    }
  }

  println(table.renderText())
}
