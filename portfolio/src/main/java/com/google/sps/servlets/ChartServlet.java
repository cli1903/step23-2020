// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.sps.servlets;

import com.google.gson.Gson;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/charts")
public class ChartServlet extends HttpServlet {
  private Map<String, Integer> movieVotes = new HashMap<>();
  private static final String ANIMATED_MOVIE_QUERY_PARAM = "Animated_Movies";

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    response.setContentType("application/json");
    Gson gson = new Gson();
    String json = gson.toJson(movieVotes);
    response.getWriter().println(json);
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String animatedMovie = request.getParameter(ANIMATED_MOVIE_QUERY_PARAM);
    int currentVotes = movieVotes.containsKey(animatedMovie) ? movieVotes.get(animatedMovie) : 0;
    movieVotes.put(animatedMovie, currentVotes + 1);

    response.sendRedirect("/index.html");
  }
}