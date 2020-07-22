package com.google.tripmeout.frontend.storage;

import com.google.tripmeout.frontend.PlaceVisitModel;
import com.google.tripmeout.frontend.error.PlaceVisitAlreadyExistsException;
import com.google.tripmeout.frontend.error.PlaceVisitNotFoundException;
import com.google.tripmeout.frontend.error.TripNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

public class InMemoryPlaceVisitStorage implements PlaceVisitStorage {
  // <tripId, placeId, PlaceVisitModel>
  Map<String, Map<String, PlaceVisitModel>> placesByTripIdByPlaceId = new ConcurrentHashMap<>();

  @Override
  public void addPlaceVisit(PlaceVisitModel placeVisit) throws PlaceVisitAlreadyExistsException {
    try {
      placesByTripIdByPlaceId.compute(placeVisit.tripId(), (tripKey, placesMap) -> {
        if (placesMap == null) {
          Map<String, PlaceVisitModel> newPlaceMap = new ConcurrentHashMap<>();
          newPlaceMap.put(placeVisit.placesApiPlaceId(), placeVisit);
          return newPlaceMap;
        }
        if (placesMap.get(placeVisit.placesApiPlaceId()) != null) {
          // cannot throw checked exception in here so wrap in RuntimeException
          throw new RuntimeException(new PlaceVisitAlreadyExistsException("PlaceVisit "
              + placeVisit.placeName() + " already exists for trip " + placeVisit.tripId()));
        }
        placesMap.put(placeVisit.placesApiPlaceId(), placeVisit);
        return placesMap;
      });
    } catch (RuntimeException e) {
      // check if RuntimeException is actually because of PlaceVisitAlreadyExistsException
      // if yes, throw PlaceVisitAlreadyExistsExceptions
      // else, throw original RuntimeExeption
      if (e.getCause() instanceof PlaceVisitAlreadyExistsException) {
        throw(PlaceVisitAlreadyExistsException) e.getCause();
      }
      throw e;
    }
  }

  @Override
  public void removePlaceVisit(String tripId, String placeId) throws PlaceVisitNotFoundException {
    Map<String, PlaceVisitModel> placesMap = placesByTripIdByPlaceId.get(tripId);
    if (placesMap == null) {
      throw new PlaceVisitNotFoundException(
          "PlaceVisit with id" + placeId + " not found for trip " + tripId);
    }

    if (placesMap.remove(placeId) == null) {
      throw new PlaceVisitNotFoundException(
          "PlaceVisit with id" + placeId + " not found for trip " + tripId);
    }
  }

  @Override
  public Optional<PlaceVisitModel> getPlaceVisit(String tripId, String placeId) {
    return Optional.ofNullable(placesByTripIdByPlaceId.get(tripId))
        .map(placesMap -> placesMap.get(placeId));
  }

  @Override
  public boolean updateUserMarkOrAddPlaceVisit(
      PlaceVisitModel placeVisit, PlaceVisitModel.UserMark newStatus) {
    AtomicBoolean alreadyInStorage = new AtomicBoolean(false);

    Map<String, PlaceVisitModel> placesMap = placesByTripIdByPlaceId.computeIfAbsent(
        placeVisit.tripId(), (tripKey) -> new ConcurrentHashMap<>());

    placesMap.compute(placeVisit.placesApiPlaceId(), (placeKey, place) -> {
      if (place != null) {
        PlaceVisitModel updatedPlace = place.toBuilder().setUserMark(newStatus).build();
        alreadyInStorage.set(true);
        return updatedPlace;
      } else {
        return placeVisit.toBuilder().setUserMark(newStatus).build();
      }
    });

    return alreadyInStorage.get();
  }

  @Override
  public List<PlaceVisitModel> getTripPlaceVisits(String tripId) {
    Map<String, PlaceVisitModel> placesMap = placesByTripIdByPlaceId.get(tripId);
    List<PlaceVisitModel> tripPlaceVisits = new ArrayList<>();
    if (placesMap == null) {
      return tripPlaceVisits;
    }
    for (PlaceVisitModel place : placesMap.values()) {
      if (place != null) {
        tripPlaceVisits.add(place);
      }
    }

    return tripPlaceVisits;
  }

  @Override
  public void removeTripPlaceVisits(String tripId) throws TripNotFoundException {
    if (placesByTripIdByPlaceId.remove(tripId) == null) {
      throw new TripNotFoundException("No PlaceVisitModel objects found with tripId: " + tripId);
    }
  }
}
