package com.index.medidor.rutas;
import com.google.android.gms.maps.model.LatLng;

import java.util.List;

/**
 * Created by oscar on 6/12/16.
 */

public class Route {
     Distance distance;
     Duration duration;
     String endAddress;
     LatLng endLocation;
     String startAddress;
     LatLng startLocation;

     public List<LatLng> points;
}
