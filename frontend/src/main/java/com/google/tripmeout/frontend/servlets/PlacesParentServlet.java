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
  private static final String NEXT_PAGE_REQUEST_PARAM = "next-page"

  private final GeoApiContext CONTEXT = new GeoApiContext.Builder()
    .apiKey("AIzaSyCJ5nD1n3osPyQHjdY1bCE6i887N32UTLM")
    .build();

  private static final String NEXT_PAGE_TOKEN;

  public PlacesParentServlet(PlaceVisitStorage storage, Gson gson) {
    this.storage = storage;
    this.gson = gson;
  }

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) 
      throws IOException {

    boolean getNextPage = request.getParameter(NEXT_PAGE_TOKEN).equals("True");

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
      response.setContentType("application/json");
      response.getWriter().println(gson.toJson(nearbyPlaces));
    } catch (Exception e) {
      response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
      response.setContentType("application/json");
      response.getWriter().println(gson.toJson("Error getting nearby places"));
      return;
    }

  }

  public List<PlacesSearchResult> getNearbyPlaces(LatLng location, int radius, String sortMethod) 
      throws ApiException, InterruptedException, IOException {

    NearbySearchRequest nearby = new NearbySearchRequest(CONTEXT)
      .location(location)
      .type("point_of_interest");

    if (sortMethod.equals("distance")) {
      nearby = nearby.rankby(RankBy.DISTANCE);
    } else {
      nearby = nearby.radius(radius);
    }

    if (sortMethod.equals("popular")) {
      nearby = nearby.rankby(RankBy.PROMINENCE);
    }

    if (getNextPage) {
      nearby = nearby.pageToken(NEXT_PAGE_TOKEN);
    }

    List<PlacesSearchResult> nearbyPlaces = new ArrayList<>();

    PlacesSearchResponse response = nearby.await();

    String currentToken = "first-time";

    PlacesSearchResult[] nearbyPlacesResults = response.results;

    for (PlacesSearchResult place: nearbyPlacesResults) {
      if (!place.permanentlyClosed) {
        nearbyPlaces.add(place);
      }
    }

    NEXT_PAGE_TOKEN = response.nextPageToken;

    return nearbyPlaces;
  }
}
