import 'package:tripmeout/services/place_visit_service.dart';
import 'package:tripmeout/widgets/create_place_visit_widget.dart';
import 'package:tripmeout/widgets/default_app_bar.dart';
import 'package:flutter/material.dart';

class CreatePlaceVisitPage extends StatelessWidget {
  final PlaceVisitService placeVisitService;
  final PlacesApiServices placesApiServices;

  CreatePlaceVisitPage(this.placeVisitService, this.placesApiServices,
      {Key key})
      : super(key: key);

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: defaultAppBar(context),
      body: Center(
          child: CreatePlaceVisitWidget(
              this.placeVisitService, this.placesApiServices)),
    );
  }
}
