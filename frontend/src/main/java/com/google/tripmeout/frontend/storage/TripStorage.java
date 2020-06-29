package com.google.tripmeout.frontend.storage;

import com.google.tripmeout.frontend.TripModel;
import java.util.List;

/**
 * stores TripModel objects
 */
public interface TripStorage {
  /**
   * adds a TripModel object to storage
   *
   * @param trip the TripModel object to add to storage
   *
   * @throws a TripAlreadyExists exception if there is a TripModel object 
   *     already in storage with the same tripId as the trip
   */
  public void addTrip(TripModel trip);

  /**
   * removes from storage the TripModel object whose tripId matches the given tripId
   *
   * @param tripId the id to match TripModel object's tripId field on
   *
   * @throws a TripModelNotFound exception if there is no TripModel object with 
   *     the given tripId in storage
   */
  public void removeTrip(String tripId);

  /**
   * updates the locationLat and locationLong fields of the TripModel object
   * in storage whose tripId matches the given tripId
   *
   * @param tripId the id to match TripModel object's tripId field on
   * @param latitude the new latitude of the TripModel
   * @param longitude the new longitude of the TripModel
   *
   * @throws a TripModelNotFound exception if there is no TripModel object with 
   *     the given tripId in storage
   */
  public void updateTripLocation(String tripId, double latitude, double longitude);

  /**
   * returns the TripModel object in storage whose tripId matched the given tripId
   *
   * @param tripId the id to match TripModel object's tripId field on
   *
   * @throws a TripModelNotFound exception if there is no TripModel object with 
   *     the given tripId in storage
   */
  public TripModel getTrip(String tripId);

  /**
   * returns all TripModel objects in storage whose userId matches the given userId
   *
   * @param userId the id to match TripModel object's userId field on
   */
  public List<TripModel> getAllUserTrips(String userId);
}
