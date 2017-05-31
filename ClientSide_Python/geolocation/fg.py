import GeoIP
gi = GeoIP.open("GeoLiteCity.dat", GeoIP.GEOIP_INDEX_CACHE | GeoIP.GEOIP_CHECK_CACHE)
print gi.record_by_name("66.249.79.72") # a www.google.com IP