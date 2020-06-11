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

/**
 * Adds a random greeting to the page.
 */
function addRandomFact() {
  const facts = [
    'Fact 1 : I am 6\'2 ', 'Fact 2: I am allergic to milk/dairy',
    'Fact 3: I was born and raised in Baltiomore, MD ',
    'Fact 4: I have 3 siblings ',
    'Fact 5: I am learning French so I can travel to Paris ',
    'Fact 6: I\'ve broke 2 bones due to sports'
  ];

  // Pick a random fact.
  const fact =
      facts[Math.floor(Math.random() * facts.length)] + '!!!\t ~ Anthony';

  // Add it to the page.
  const factContainer = document.getElementById('fact-container');
  factContainer.innerText = fact;
}

var slideIndex = 0;
function showSlides() {
  var i;
  var slides = document.getElementsByClassName('mySlides');
  for (i = 0; i < slides.length; i++) {
    slides[i].style.display = 'none';
  }
  slideIndex++;
  if (slideIndex > slides.length) {
    slideIndex = 1;
  }
  slides[slideIndex - 1].style.display = 'block';
  setTimeout(showSlides, 7000);
}

async function setHelloContainerWithServlet() {
  const response = await fetch('/Hello');
  const quote = await response.text();
  document.getElementById('hello-container').innerText = quote;
}

async function setTester() {
  fetch('/login').then(response => response.json()).then((informationLinks) => {
    var title = 'This the test link for user authentication';
    var theButton =
        document.getElementById('theButton');
    var linkHome = document.getElementById('smallContent4Right');

    if (!informationLinks.loggedIn) {
      theButton.style.visibility = 'hidden';
      var linkText = 'Use this link to Log In';
      var aLink =
          createLinkElement(linkText, informationLinks.returnLink, title);

      linkHome.appendChild(aLink);
    } else {
      theButton.style.visibility = 'visible';
      var linkText = 'Use this link to Log Out';
      var aLink =
          createLinkElement(linkText, informationLinks.returnLink, title);

      linkHome.appendChild(aLink);
    }
  });
}

function forOnLoad() {
  showSlides();
  setTester();
}

function setMyTeamConatainerWithServerlet() {
  fetch('/Team').then(response => response.json()).then((theTeam) => {
    var teamParagraph =
        'Here are the members of my team and their position:\n\n';

    for (i = 0; i < theTeam.members.length; i++) {
      teamParagraph = teamParagraph + theTeam.members[i].name + '     ' +
          theTeam.members[i].status;
    }

    document.getElementById('team-container').innerText = teamParagraph;
  });
}

function setCommentContainerWithServlet() {
  fetch('/comments')
      .then(response => response.json())
      .then((commentPackage) => {
        const commentContainer = document.getElementById('comment-container');
        var validateComments = commentPackage.flatMap(
            comment => validateComment(comment, x => [x], x => {
              console.log('Invalid comment ', x);
              return [];
            }));
        const commentEl = document.getElementById('comments');
        validateComments.map(formatComment)
            .forEach(
                formatted =>
                    commentEl.appendChild(createListElement(formatted)));
      });
}

function createListElement(text) {
  const liElement = document.createElement('li');
  liElement.innerText = text;
  return liElement;
}

function createParagraphElement(text) {
  const pElement = document.createElement('p');
  pElement.innerText = text;
  return pElement;
}

function createLinkElement(text, website, title) {
  const aElement = document.createElement('a');
  var innerText = document.createTextNode(text);
  aElement.appendChild(innerText);
  aElement.href = website;
  aElement.title = title;

  return aElement;
}

function formatComment(comment) {
  var formatted = '';
  formatted += 'Name: ' + comment.name;
  formatted += ' Comment: ' + comment.payload;
  formatted += ' Stars: ';
  if (comment.stars == 0) {
    formatted += 'NO STARS';
  }
  while (comment.stars) {
    formatted += '*';
    comment.stars -= 1;
  }
  return formatted + '      ';
}

function validateComment(comment, onSuccess, onFailure) {
  if (comment.name == null) {
    return onFailure(comment)
  } else if (comment.payload == null) {
    return onFailure(comment)
  }
  return onSuccess(comment)
}

google.charts.load('current', {'packages': ['corechart']});
google.charts.setOnLoadCallback(drawChart);

function drawChart() {
  fetch('/charts').then(response => response.json()).then((movieVotes) => {
    const data = new google.visualization.DataTable();
    data.addColumn('string', 'Movie');
    data.addColumn('number', 'Votes');
    Object.keys(movieVotes).forEach((movie) => {
      data.addRow([movie, movieVotes[movie]]);
    });

    const options = {
      'title': 'Favorite Movie',
      'width': 625,
      'height': 500,
      'vAxis': {'gridlines': {'multiple': 1}, 'minorGridlines': {'count': 0}}
    };

    const chart = new google.visualization.ColumnChart(
        document.getElementById('chart-container'));
    chart.draw(data, options);
  });
}
