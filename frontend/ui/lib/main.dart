import 'package:flutter/material.dart';
import 'package:tripmeout/router/router.dart';
import 'package:tripmeout/services/rest_api_trip_service.dart';
import 'package:tripmeout/services/rest_api_place_visit_service.dart';
import 'package:tripmeout/services/trip_service.dart';
import 'package:tripmeout/services/in_memory_trip_service.dart';
import 'package:tripmeout/services/trip_service.dart';
import 'package:tripmeout/services/place_visit_service.dart';
import 'package:tripmeout/themes/default_theme.dart';

void main() {
  TripService tripService = RestApiTripService();
  PlaceVisitService placeVisitService = RestApiPlaceVisitService();
  Router router = Router(tripService, placeVisitService);
  runApp(TripMeOut(tripService, placeVisitService, router));
}

class TripMeOut extends StatelessWidget {
  TripService tripService;
  PlaceVisitService placeVisitService;
  Router router;

  TripMeOut(this.tripService, this.placeVisitService, this.router);

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Trip Me Out',
      theme: defaultTheme,
      onGenerateRoute: router.generateRoute,
      initialRoute: Router.tripListRoute,
    );
  }
}
