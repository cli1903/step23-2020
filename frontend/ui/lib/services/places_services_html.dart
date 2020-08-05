import 'dart:async';
import 'dart:html';
import 'package:google_maps/google_maps.dart';
import 'package:google_maps/google_maps_places.dart';
import 'package:tripmeout/model/place.dart';

class PlacesApiServices {
  final AutocompleteService autocompleteService = AutocompleteService();
  final PlacesService placesService =
      PlacesService(document.getElementById("maps"));

  Future<List<PlaceWrapper>> getAutocomplete(
      String input, List<String> allowedTypes) {
    if (input == null || input == "") {
      return Future.sync(() => []);
    }

    Completer<List<PlaceWrapper>> completer = Completer();
    AutocompletionRequest request = AutocompletionRequest()..input = input;

    if (allowedTypes.length > 0) {
      request = request..types = allowedTypes;
    }

    autocompleteService.getPlacePredictions(request, (result, status) {
      if (status == PlacesServiceStatus.OK) {
        completer.complete(result.map(autocompleteToPlaceWrapper).toList());
      } else if (status == PlacesServiceStatus.ZERO_RESULTS) {
        completer.complete([]);
      } else {
        completer.completeError(status);
      }
    });

    return completer.future;
  }

  Future<List<String>> getPhotos(String placeId) {
    Completer<List<String>> completer = Completer();
    final request = PlaceDetailsRequest()..placeId = placeId;
    placesService.getDetails(request, (result, status) async {
      if (status == PlacesServiceStatus.OK) {
        final photoOptions = PhotoOptions()
          ..maxHeight = 500
          ..maxWidth = 500;

        List<String> images =
            result.photos.map((photo) => photo.getUrl(photoOptions));
        completer.complete(images);
      } else {
        completer.complete([]);
      }
    });
    return completer.future;
  }

  PlaceWrapper autocompleteToPlaceWrapper(AutocompletePrediction suggestion) {
    return PlaceWrapper(
        name: suggestion.description, placeId: suggestion.placeId);
  }
  
  Future<PlaceResult> getPlaceDetails(String placeId) {
    Completer<PlaceResult> completer = Completer();
    final request = PlaceDetailsRequest()..placeId = placeId;
    placesService.getDetails(request, (result, status) async {
      if (status == PlacesServiceStatus.OK) {
        completer.complete(result);
      } else {
        completer.completeError("errored getting place details with status code: $status");
      }
    });
    return completer.future;
  }
}
