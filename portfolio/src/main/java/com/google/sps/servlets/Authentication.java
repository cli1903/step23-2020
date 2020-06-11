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

import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.gson.Gson;
import com.google.sps.data.AuthenticatorInfo;
import com.google.sps.data.Comment;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/login")
public class Authentication extends HttpServlet {
  private static final String DEFAULT_LOGOUT_URL_REDIRECT = "/";
  private static final String DEFAULT_LOGIN_URL_REDIRECT = "/";

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    response.setContentType("application/json;");

    UserService userService = UserServiceFactory.getUserService();
    if (userService.isUserLoggedIn()) {
      String greeting = String.format("Hello %s!", userService.getCurrentUser().getEmail());
      String logoutUrl = userService.createLogoutURL(DEFAULT_LOGOUT_URL_REDIRECT);

      AuthenticatorInfo returnInfo = new AuthenticatorInfo(greeting, logoutUrl, true);
      Gson gson = new Gson();
      String json = gson.toJson(returnInfo);

      response.getWriter().println(json);
    } else {
      String greeting = "Hello stranger.";
      String loginUrl = userService.createLoginURL(DEFAULT_LOGIN_URL_REDIRECT);

      AuthenticatorInfo returnInfo = new AuthenticatorInfo(greeting, loginUrl, false);
      Gson gson = new Gson();
      String json = gson.toJson(returnInfo);

      response.getWriter().println(json);
    }
  }
}
