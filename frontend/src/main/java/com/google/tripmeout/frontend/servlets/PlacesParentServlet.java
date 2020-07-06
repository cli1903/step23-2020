package com.google.tripmeout.frontend.servlets;

import com.google.tripmeout.frontend.PlaceVisitModel;
import com.google.tripmeout.frontend.storage.PlaceVisitStorage;
import com.google.gson.Gson;
import com.google.maps.GeoApiContext;
import com.google.maps.NearbySearchRequest;
import com.google.maps.model.PlacesSearchResponse;
import com.google.maps.model.PlacesSearchResult;
import com.google.maps.model.LatLng;
import com.google.maps.model.RankBy;
import com.google.maps.errors.ApiException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.InterruptedException;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Set;
import java.util.HashSet;

public class PlacesParentServlet extends HttpServlet {
  private final PlaceVisitStorage storage;
  private final Gson gson;

  private static final String RADIUS_REQUEST_PARAM = "radius"; 
  private static final String SORT_REQUEST_PARAM = "sort";
  private static final String LATITUDE_REQUEST_PARAM = "latitude";
  private static final String LONGITUDE_REQUEST_PARAM = "longitude";

  private static final Set<String> FOOD_PLACE_TYPES = new HashSet<>(
    Arrays.asList("bakery", "bar", "cafe", "lodging", "meal_delivery", 
      "meal_takeaway", "restaurant", "supermarket"));

  private static final Set<String> TRANSPORT_PLACE_TYPES = new HashSet<>(
    Arrays.asList("airport", "bus_station", "car_rental", "subway_station", 
      "train_station", "transit_station"));

  private static final Set<String> SHOPPING_PLACE_TYPES = new HashSet<>(
    Arrays.asList("bookstore", "clothing_store", "convenience_store", 
      "department_store", "drugstore", "shopping_mall", "store"));

  private static final Set<String> PLACES_OF_WORSHIP_TYPES = new HashSet<>(
    Arrays.asList("church", "hindu_temple", "mosque", "synagogue"));

  private static final Set<String> ACTIVITY_PLACE_TYPES = new HashSet<>(
    Arrays.asList( "amusement_park", "aquarium", "art_gallery", "bar", 
      "bowling_alley", "campground", "casino", "city_hall", "embassy", 
      "library", "movie_theater", "museum", "night_club", "painter", "park", 
      "spa", "stadium", "tourist_attraction", "university", "zoo"));

  private static final Set<String> ALL_VACATION_PLACE_TYPES = new HashSet<>(
    Arrays.asList("amusement_park", "aquarium", "art_gallery", "bar", 
      "bowling_alley", "campground", "casino", "city_hall", "embassy", 
      "library", "movie_theater", "museum", "night_club", "painter", "park", 
      "spa", "stadium", "tourist_attraction", "university", "zoo", "church", 
      "hindu_temple", "mosque", "synagogue", "bookstore", "clothing_store", 
      "convenience_store", "department_store", "drugstore", "shopping_mall", 
      "store", "bakery", "bar", "cafe", "lodging", "meal_delivery", 
      "meal_takeaway", "restaurant", "supermarket"));

  private final GeoApiContext context = new GeoApiContext.Builder()
    .apiKey("AIzaSyCJ5nD1n3osPyQHjdY1bCE6i887N32UTLM")
    .build();

  public PlacesParentServlet(PlaceVisitStorage storage, Gson gson) {
    this.storage = storage;
    this.gson = gson;
  }

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) 
      throws IOException {
        
    double latitude = Double.valueOf(request.getParameter(LATITUDE_REQUEST_PARAM));
    double longitude = Double.valueOf(request.getParameter(LONGITUDE_REQUEST_PARAM));
    LatLng location = new LatLng(latitude, longitude);

    String sortMethod = request.getParameter(SORT_REQUEST_PARAM);
    String radiusString = request.getParameter(RADIUS_REQUEST_PARAM);
    int radius;

    try {
      radius = Integer.parseInt(radiusString);
    } catch (NumberFormatException e) {
      System.err.println("Could not convert to int: " + radiusString);
      response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      response.setContentType("application/json");
      response.getWriter().println(gson.toJson("Invalid input for radius parameter"));
      return;
    }

    try {
      List<PlacesSearchResult> nearbyPlaces = getNearbyPlaces(location, radius, sortMethod);
    } catch (Exception e) {
      response.setContentType("application/json");
      response.getWriter().println(gson.toJson("Error getting nearby places"));
      return;
    }

  }

  public List<PlacesSearchResult> getNearbyPlaces(LatLng location, int radius, String sortMethod) 
      throws ApiException, InterruptedException, IOException {

    NearbySearchRequest nearby = new NearbySearchRequest(context)
      .location(location);

    if (sortMethod.equals("distance")) {
      nearby = nearby.rankby(RankBy.DISTANCE);
    } else {
      nearby = nearby.radius(radius);
    }

    if (sortMethod.equals("popular")) {
      nearby = nearby.rankby(RankBy.PROMINENCE);
    }

    List<PlacesSearchResult> nearbyPlaces = new ArrayList<>();

    PlacesSearchResponse response = nearby.await();

    while (response.nextPageToken != null) {
      PlacesSearchResult[] nearbyPlacesResults = response.results;
      for (PlacesSearchResult place: nearbyPlacesResults) {
        for (String placeType: place.types) {
          if (ALL_VACATION_PLACE_TYPES.contains(placeType)) {
            nearbyPlaces.add(place);
            break;
          }
        }
      }

      response = nearby.pageToken(response.nextPageToken).await();
    }
    return nearbyPlaces;
  }
}
