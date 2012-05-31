initialize = () ->
  myOptions =
    zoom: 11,
    center: new google.maps.LatLng(-33.6, 150.8),
    mapTypeId: google.maps.MapTypeId.ROADMAP,
    mapTypeControl: false,
    panControl: false,
    zoomControl: false,
  map = new google.maps.Map(document.getElementById('map_canvas'), myOptions)

#google.maps.event.addDomListener(window, 'load', initialize)