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
