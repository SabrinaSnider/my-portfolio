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

import structures.Comment;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/* Servlet that returns some example content. */
@WebServlet("/list-comments")
public class ListCommentsServlet extends HttpServlet {

  /* Converts the comments arraylist to a json String */
  private String convertToJson(List<Comment> data) {
    return (new Gson()).toJson(data);
  }

  /* Gets the query results from the datastore for Comments */
  private PreparedQuery queryComments(String sortBy, boolean ascending) {
    Query.SortDirection sortDirection = ascending ? SortDirection.ASCENDING : SortDirection.DESCENDING;
    Query queryForComments = new Query("Comment").addSort(sortBy, sortDirection);

    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    return datastore.prepare(queryForComments);
  }

  /* Returns a json String of the comments */
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String sortBy = request.getParameter("sort");
    boolean ascending = Boolean.parseBoolean(request.getParameter("ascending"));
    int maxComments = Integer.valueOf(request.getParameter("max"));

    PreparedQuery queriedComments = queryComments(sortBy, ascending);

    List<Comment> comments = new ArrayList<>();
    for (Entity commentEntity : queriedComments.asIterable()) {
      if (comments.size() >= maxComments) break;
      Comment comment = new Comment(commentEntity);
      comments.add(comment);
    }

    response.setContentType("application/json;");
    response.getWriter().println(convertToJson(comments));
  }
}
