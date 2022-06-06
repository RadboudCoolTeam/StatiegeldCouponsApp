package io.github.textrecognisionsample.util;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;

import androidx.core.app.ActivityCompat;

public class GetLocation {
    private String latitude;
    private String longitude;
    private LocationManager lm;
    private Context context;
    private String provider;

    public GetLocation(LocationManager lm, Context context, String provider){
        this.lm = lm;
        this.context = context;
        this.provider = provider;

    }

    public String[] getLoc(){



        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Location location = lm.getLastKnownLocation(provider);

            if(location != null) {
                longitude = String.valueOf(location.getLongitude());
                latitude = String.valueOf(location.getLatitude());
            } else {
                lm.requestLocationUpdates(provider, 1000, 20, (LocationListener) context);
            }

        }

        return new String[]{latitude, longitude};
    }
}
