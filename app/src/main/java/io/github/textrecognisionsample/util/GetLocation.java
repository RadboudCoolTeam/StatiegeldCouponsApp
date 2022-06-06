package io.github.textrecognisionsample.util;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import io.github.textrecognisionsample.activity.CameraX;

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

    private final String[] REQUIRED_PERMISSIONS = new String[]{
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
    };

    public String[] getLoc(){

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Location location = lm.getLastKnownLocation(provider);

            if(location != null) {
                longitude = String.valueOf(location.getLongitude());
                latitude = String.valueOf(location.getLatitude());
            } else {
                lm.requestLocationUpdates(provider, 1000, 20, (LocationListener) context);
            }

        } else {
            ActivityCompat.requestPermissions(context, REQUIRED_PERMISSIONS,);
        }

        return new String[]{latitude, longitude};
    }

//    private boolean allPermissionsGranted(Context context) {
//        for (String permission : REQUIRED_PERMISSIONS) {
//            if (ContextCompat.checkSelfPermission(context, permission) !=
//                    PackageManager.PERMISSION_GRANTED) {
//                return false;
//            }
//        }
//        return true;
//    }
}
