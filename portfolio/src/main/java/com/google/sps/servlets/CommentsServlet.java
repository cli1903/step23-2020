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

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.gson.Gson;
import java.io.IOException;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Servlet that processes text. */
@WebServlet("/comments")
public final class CommentsServlet extends HttpServlet {

  private ArrayList<Comment> theComments = new ArrayList<Comment>(); 
  private static final String NAME_QUERY_PARAM = "name-input";
  private static final String COMMENT_QUERY_PARAM = "comment-input";

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
  
    Query query = new Query("Comment");

    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    PreparedQuery results = datastore.prepare(query);

        List<Comment> comments = new ArrayList<>();
        for (Entity entity : results.asIterable()) {
        
        String name = (String) entity.getProperty("name");
        String payload = (String) entity.getProperty("payload");
        Long stars = (Long) entity.getProperty("stars");
        
        int starValue = stars.intValue();

        Comment comment = new Comment(name, payload, starValue);
        comments.add(comment);
        }

        Gson gson = new Gson();

    response.setContentType("application/json;");
    response.getWriter().println(gson.toJson(comments));
  }
    
  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {

    String name = getParameter(request,NAME_QUERY_PARAM , "");
    String payload = getParameter(request,COMMENT_QUERY_PARAM, "");
    int stars = 0;

    if(Boolean.parseBoolean(getParameter(request,"one-star", "false"))){stars=1;}
    if( Boolean.parseBoolean(getParameter(request,"two-star", "false"))){stars=2;}
    if( Boolean.parseBoolean(getParameter(request,"three-star", "false"))){stars=3;}
    if( Boolean.parseBoolean(getParameter(request,"four-star", "false"))){stars=4;}
    if( Boolean.parseBoolean(getParameter(request,"five-star", "false"))){stars=5;}
    
    Comment comment = new Comment(name,payload,stars);
    theComments.add(comment);

    Entity commentEntity = new Entity("Comment");
    commentEntity.setProperty("name", name);
    commentEntity.setProperty("payload", payload);
    commentEntity.setProperty("stars", stars);

    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    datastore.put(commentEntity);

    response.sendRedirect("/index.html");

  }

  /**
   * @return the request parameter, or the default value if the parameter
   *         was not specified by the client
   */
  private String getParameter(HttpServletRequest request, String name, String defaultValue) {
    String value = request.getParameter(name);
    if (value == null) {
      return defaultValue;
    }
    return value;
  }

  static class Comment{
    String name;
    String payload;
    int stars;
    Comment(String name, String payload, int stars){
        this.name=name;
        this.payload = payload;
        this.stars=stars;
    }
  }
}
