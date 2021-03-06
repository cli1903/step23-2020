package com.google.tripmeout.frontend.storage;

import com.google.tripmeout.frontend.TripModel;
import com.google.tripmeout.frontend.error.TripAlreadyExistsException;
import com.google.tripmeout.frontend.error.TripNotFoundException;
import com.google.tripmeout.frontend.storage.TripStorage;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryTripModelStorage implements TripStorage {
  Map<String, TripModel> storage = new ConcurrentHashMap<>();

  @Override
  public void addTrip(TripModel trip) throws TripAlreadyExistsException {
    TripModel previousTrip = storage.putIfAbsent(trip.id(), trip);
    if (previousTrip != null) {
      throw new TripAlreadyExistsException("Trip with id: " + trip.id() + " already exisits");
    }
  }

  @Override
  public void removeTrip(String tripId) throws TripNotFoundException {
    TripModel trip = storage.remove(tripId);
    if (trip == null) {
      throw new TripNotFoundException("Trip with id: " + tripId + " not found in storage");
    }
  }

  @Override
  public void updateTripName(String tripId, String name) throws TripNotFoundException {
    TripModel newTripObject =
        storage.computeIfPresent(tripId, (id, trip) -> trip.toBuilder().setName(name).build());
    if (newTripObject == null) {
      throw new TripNotFoundException("Trip with id: " + tripId + " not found in storage");
    }
  }

  @Override
  public TripModel getTrip(String tripId) throws TripNotFoundException {
    TripModel trip = storage.get(tripId);
    if (trip != null) {
      return trip;
    }
    throw new TripNotFoundException("Trip with id: " + tripId + " not found in storage");
  }

  @Override
  public List<TripModel> getAllUserTrips(String userId) {
    List<TripModel> trips = new ArrayList<>();
    storage.forEach((tripId, trip) -> {
      if (userId.equals(trip.userId())) {
        trips.add(trip);
      }
    });
    return trips;
  }
}
