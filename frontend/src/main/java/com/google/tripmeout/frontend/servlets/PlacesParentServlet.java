package com.google.tripmeout.frontend.servlets;

import com.google.tripmeout.frontend.PlaceVisitModel;
import com.google.tripmeout.frontend.storage.PlaceVisitStorage;
import com.google.tripmeout.frontend.TripModel;
import com.google.tripmeout.frontend.storage.TripStorage;
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
  private final PlaceVisitStorage placeStorage;
  private final TripStorage tripStorage;
  private final Gson gson;

  private static final String RADIUS_REQUEST_PARAM = "radius"; 
  private static final String SORT_REQUEST_PARAM = "sort";
  private static final String TRIPID_REQUEST_PARAM = "tripId"
  private static final String NEXT_PAGE_REQUEST_PARAM = "next-page"
  private static final String PLACE_NAME_REQUEST_PARAM = "place-name";
  private static final String PLACEID_REQUEST_PARAM = "placeId";
  private static final String USER_MARK_REQUEST_PARAM = "user-mark";

  private final GeoApiContext CONTEXT = new GeoApiContext.Builder()
    .apiKey("AIzaSyCJ5nD1n3osPyQHjdY1bCE6i887N32UTLM")
    .build();

  private static final String NEXT_PAGE_TOKEN;

  public PlacesParentServlet(PlaceVisitStorage placeStorage, TripStorage tripStorage, Gson gson) {
    this.placeStorage = placeStorage;
    this.tripStorage = tripStorage;
    this.gson = gson;
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String tripId = request.getParameter(TRIPID_REQUEST_PARAM);
    String placeId = request.getParameter(PLACEID_REQUEST_PARAM);
    PlaceVisitModel.UserMark status = request.getParameter(USER_MARK_REQUEST_PARAM);

    PlaceDetailsRequest place = new PlaceDetailsRequest(CONTEXT).placeId(placeId);
    PlaceDetails details = place.await().getResult();

    PlaceVisitModel newPlace = new PlaceVisitModel.builder()
      .setTripId(tripId)
      .setPlaceId(placeId)
      .setName(details.name)
      .setLocationLat(details.geometry.location.lat)
      .setLocationLong(details.geometry.location.lng)
      .build();

    placeStorage.update

  }

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) 
      throws IOException {

    String tripId = request.getParameter(TRIPID_REQUEST_PARAM);
    String name = request.getParameter(PLACE_NAME_REQUEST_PARAM);
    boolean getNextPage = request.getParameter(NEXT_PAGE_TOKEN).equals("true");
    String sortMethod = request.getParameter(SORT_REQUEST_PARAM);
    String radiusString = request.getParameter(RADIUS_REQUEST_PARAM);
    int radius;

    TripModel trip = tripStorage.getTrip(tripId);
    LatLng location = new LatLng(trip.locationLat(), trip.locationLong());

    try {
      List<PlacesSearchResult> nearbyPlaces = getNearbyPlaces(tripId, getNextPage, location, radius, sortMethod, name);
      response.setContentType("application/json");
      response.getWriter().println(gson.toJson(nearbyPlaces));
    } catch (Exception e) {
      response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
      response.setContentType("application/json");
      response.getWriter().println(gson.toJson("Error getting nearby places"));
      return;
    }

  }

  public List<PlacesVisitModel> getNearbyPlaces(String tripId, boolean getNextPage, LatLng location, int radius, String sortMethod, String name) 
      throws ApiException, InterruptedException, IOException {

    NearbySearchRequest nearby = new NearbySearchRequest(CONTEXT)
      .location(location)
      .type("point_of_interest");

    if (sortMethod.equals("distance")) {
      nearby = nearby.rankby(RankBy.DISTANCE);
    } else {
      nearby = nearby.radius(radius);
    }

    if (name != null && name != "") {
      nearby = nearby.name(name);
    }

    if (getNextPage) {
      nearby = nearby.pageToken(NEXT_PAGE_TOKEN);
    }

    List<PlacesSearchResult> nearbyPlaces = new ArrayList<>();
    PlacesSearchResponse response = nearby.await();
    PlacesSearchResult[] nearbyPlacesResults = response.results;

    String currentToken = "first-time";

    for (PlacesSearchResult place: nearbyPlacesResults) {
      if (!place.permanentlyClosed) {
        Optional<PlaceVisitModel> nearPlace = placeStorage.get(tripId, place.placeId);
        if (nearPlace.isPresent()) {
          nearbyPlaces.add(nearPlace.get());
        } else {
          PlaceVisitModel newPlace = PlaceVisitModel.builder()
            .setTripId(tripId);
            .setPlaceId(place.placeId)
            .setName(place.name)
            .setLocationLat(location.lat)
            .setLocationLong(location.lng)
            .build();
          nearbyPlaces.add(newPlace);
        }
      }
    }

    NEXT_PAGE_TOKEN = response.nextPageToken;

    return nearbyPlaces;
  }
}
