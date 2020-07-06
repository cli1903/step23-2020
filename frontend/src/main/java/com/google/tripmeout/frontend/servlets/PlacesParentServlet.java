package com.google.tripmeout.frontend.servlets;

import com.google.tripmeout.frontend.PlaceVisitModel;
import com.google.tripmeout.frontend.storage.PlaceVisitStorage;
import com.google.gson.Gson;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class PlacesParentServlet extends HttpServlet {
  private final PlaceVisitStorage storage;
  private final Gson gson;

  private static final String RADIUS_REQUEST_PARAM = "radius"; 
  private static final String SORT_REQUEST_PARAM = "sort"

  public PlacesParentServlet(PlaceVisitStorage storage, Gson gson) {
    this.storage = storage;
    this.gson = gson;
  }

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String sortMethod = request.getParameter(SORT_REQUEST_PARAM);
    String radiusString = request.getParameter(RADIUS_REQUEST_PARAM);
    int radius;

    try {
      radius = Integer.parseInt(radiusString);
    } catch (NumberFormatException e) {
      System.err.println("Could not convert to int: " + userNum);
      response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      response.setContentType("application/json");
      response.getWriter().println(gson.toJson("Invalid input for radius parameter"));
      return;
    }
  }
}