package com.kevinmazige.android.skies.api;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * This is the required retrofit network request interface.
 */

public interface WebApi {

    /*
     * requests all satellites currently above the observer's location
     */
    @GET("rest/v1/satellite/above/{lat}/{lng}/{alt}/{radius}/{category}/&apiKey={apiKey}")
    Call<WhatsUp> getWhatsUp(@Path("lat") double lat, @Path("lng") double lng,
                                    @Path("alt") double alt, @Path("radius") int radius, @Path("category") int category,
                                    @Path("apiKey") String apiKey);

    /*
     * requests future location data for a given satellite
     */
    @GET("rest/v1/satellite/positions/{id}/{lat}/{lng}/{alt}/{secs}/&apiKey={apiKey}")
    Call<SatellitePositions> getSatellitePositions(@Path("id") int id,
                                                          @Path("lat") double lat, @Path("lng") double lng, @Path("alt") double alt,
                                                          @Path("secs") int secs, @Path("apiKey") String apiKey);

    /*
     * requests the next visual passes for a given satellite
     */
    @GET("rest/v1/satellite/visualpasses/{id}/{lat}/{lng}/{alt}/{days}/{min_visibility}/&apiKey={apiKey}")
    Call<VisualPasses> getVisualPasses(
            @Path("id") int id, @Path("lat") double lat, @Path("lng") double lng, @Path("alt") double alt,
            @Path("days") int days, @Path("min_visibility") int minVisibility, @Path("apiKey") String apiKey);
}

