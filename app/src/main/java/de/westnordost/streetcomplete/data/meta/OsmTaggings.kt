package de.westnordost.streetcomplete.data.meta

/** Definitions/meanings of certain OSM taggings  */

val ANYTHING_UNPAVED = setOf(
    "unpaved", "compacted", "gravel", "fine_gravel", "pebblestone", "grass_paver",
    "ground", "earth", "dirt", "grass", "sand", "mud", "ice", "salt", "snow", "woodchips"
)

val ANYTHING_PAVED = setOf(
    "paved", "asphalt", "cobblestone", "cobblestone:flattened", "sett",
    "concrete", "concrete:lanes", "concrete:plates", "paving_stones",
    "metal", "wood", "unhewn_cobblestone"
)
val ALL_ROADS = setOf(
    "motorway", "motorway_link", "trunk", "trunk_link", "primary", "primary_link",
    "secondary", "secondary_link", "tertiary", "tertiary_link",
    "unclassified", "residential", "living_street", "pedestrian",
    "service", "track", "road"
)

val ALL_PATHS = setOf(
    "footway", "cycleway", "path", "bridleway", "steps"
)

val MAXSPEED_TYPE_KEYS = setOf(
    "source:maxspeed",
    "zone:maxspeed",
    "maxspeed:type",
    "zone:traffic"
)

const val SURVEY_MARK_KEY = "check_date"

// generated from https://github.com/mnalis/StreetComplete-taginfo-categorize/
val KEYS_THAT_SHOULD_BE_REMOVED_WHEN_SHOP_IS_REPLACED = listOf(
    "shop_?[1-9]?(:.*)?", "craft_?[1-9]?", "amenity_?[1-9]?", "old_amenity", "old_shop", "information", "leisure", "office", "tourism",
    // obsoleted information
    "abandoned(:.*)?", "disused(:.*)?", "was:.*", "damage", "source:damage", "created_by", "check_date", "last_checked", "checked_exists:date",
    // classifications / links to external databases
    "fhrs:.*", "old_fhrs:.*", "fvst:.*", "ncat", "nat_ref", "gnis:.*", "winkelnummer", "type:FR:FINESS", "type:FR:APE", "kvl_hro:amenity", "ref:DK:cvr(:.*)?",
    // names and identifications
    "name_?[1-9]?(:.*)?", ".*_name(:.*)?", "noname", "branch(:.*)?", "brand(:.*)?", "network", "operator(:.*)?", "ref", "ref:vatin", "designation",
    // contacts
    "contact:.*", "phone(:.*)?", "phone_?[1-9]?", "mobile", "fax", "facebook", "instagram", "twitter", "youtube", "telegram", "email", "website_?[1-9]?", "url", "source_ref:url", "owner",
    // payments
    "payment(:.*)?", "currency:.*", "cash_withdrawal(:.*)?", "fee", "money_transfer",
    // generic shop/craft attributes
    "seasonal", "time", "opening_hours(:.*)?", "check_date:opening_hours", "wifi", "internet", "internet_access(:.*)?", "second_hand", "self_service", "automated", "license:.*", "bulk_purchase", ".*:covid19", "language:.*", "baby_feeding",
    "description(:.*)?", "min_age", "max_age", "supermarket(:.*)?", "social_facility(:.*)?", "operational_status", "functional", "trade", "wholesale", "sale", "smoking", "zero_waste", "origin", "attraction", "strapline", "dog", "showroom",
    // food and drink details
    "bar", "cafe", "coffee", "microroasting", "microbrewery", "brewery", "real_ale", "taproom", "training", "distillery", "drink(:.*)?", "cocktails", "alcohol", "wine([:_].*)?", "happy_hours", "diet:.*", "cuisine", "tasting", "breakfast", "lunch",
    "organic", "produced_on_site", "restaurant", "food", "pastry", "pastry_shop", "product", "produce", "chocolate", "fair_trade", "butcher", "reservation", "takeaway(:.*)?", "delivery(:.*)?", "caterer", "real_fire",
    // related to repair shops/crafts
    "service(:.*)?", "motorcycle:.*", "repair", ".*:repair", "electronics_repair(:.*)?",
    // shop=hairdresser, shop=clothes
    "unisex", "male", "female", "gender",
    // healthcare like optician
    "healthcare(:.*)?", "health_.*", "medical_.*",
    // accomodation & layout
    "rooms", "stars", "accommodation", "beds", "capacity(:persons)?", "laundry_service",
    // misc specific attributes
    "clothes", "shoes", "tailor", "beauty", "tobacco", "carpenter", "furniture", "lottery", "sport", "leisure", "dispensing", "tailor:.*", "gambling", "material", "raw_material", "stonemason", "studio", "scuba_diving(:.*)?", "polling_station",
    "club", "collector", "books", "agrarian", "musical_instrument", "massage", "parts", "post_office(:.*)?", "religion", "denomination", "rental", ".*:rental", "tickets:.*", "public_transport", "goods_supply", "pet", "appliance", "artwork_type", "charity", "company", "crop", "dry_cleaning", "factory", "feature",
).map { it.toRegex() }

/** ~ tenant of a normal retail shop area.
 *  So,
 *  - no larger or purpose-built things like malls, cinemas, theatres, car washes, fuel stations,
 *    museums, galleries, zoos, aquariums, bowling alleys...
 *  - no things that are usually not found in normal retail shop areas but in offices:
 *    clinics, doctors, fitness centers, dental technicians...
 *  - nothing that is rather located in an industrial estate like car repair and other types
 *    of workshops (most craft=* other than those where people go to have something repaired or so)
 *
 *  It is possible to specify a prefix for the keys here, e.g. "disused", to find disused shops etc.
 *
 *  Note: When this function is modified, please update and rerun this too:
 *  https://github.com/mnalis/StreetComplete-taginfo-categorize/blob/master/Makefile
 *  */
fun isKindOfShopExpression(prefix: String? = null): String {
    val p = if(prefix != null) "$prefix:" else ""
    return ("""
        ${p}shop and ${p}shop !~ no|vacant|mall
        or ${p}tourism = information and ${p}information = office
        or """ +
        mapOf(
            "amenity" to arrayOf(
                "restaurant", "cafe", "ice_cream", "fast_food", "bar", "pub", "biergarten", "nightclub",
                "bank", "bureau_de_change", "money_transfer", "post_office", "internet_cafe",
                "pharmacy",
                "driving_school",
            ),
            "leisure" to arrayOf(
                "amusement_arcade", "adult_gaming_centre", "tanning_salon",
            ),
            "office" to arrayOf(
                "insurance", "travel_agent", "tax_advisor", "estate_agent", "political_party",
            ),
            "craft" to arrayOf(
                "shoemaker", "tailor", "photographer", "watchmaker", "optician",
                "electronics_repair", "key_cutter",
            )
        ).map { p + it.key + " ~ " + it.value.joinToString("|") }.joinToString("\n  or ") + "\n"
        ).trimIndent()
}
