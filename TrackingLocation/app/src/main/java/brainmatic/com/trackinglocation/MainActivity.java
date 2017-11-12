package brainmatic.com.trackinglocation;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import fr.quentinklein.slt.LocationTracker;
import fr.quentinklein.slt.TrackerSettings;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.latitude)
    TextView latitude;

    @BindView(R.id.longitude)
    TextView longitude;

    LocationTracker tracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        TrackerSettings settings =
                new TrackerSettings()
                        .setUseGPS(true)
                        .setUseNetwork(false)
                        .setUsePassive(false)
                        .setTimeBetweenUpdates(5 * 1000)
                        .setMetersBetweenUpdates(100);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            askPermissions();
        } else {
            tracker = new LocationTracker(this,settings) {
                @Override
                public void onLocationFound(Location location) {
                    latitude.setText("Latitude: " +location.getLatitude());
                    longitude.setText("Longitude: "+location.getLongitude());
                }

                @Override
                public void onTimeout() {

                }
            };
        }

    }

    @TargetApi(23)
    protected void askPermissions() {
        String[] permissions = {
                "android.permission.ACCESS_FINE_LOCATION",
                "android.permission.ACCESS_COARSE_LOCATION",
                "android.permission.INTERNET"
        };
        int requestCode = 200;
        requestPermissions(permissions, requestCode);
    }

    @Override
    protected void onPause() {
        if(tracker != null) {
            tracker.stopListening();
        }
        super.onPause();
    }

    @Override
    protected void onResume() {
        if(tracker != null) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED
                    && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                askPermissions();
            }else {
                tracker.startListening();
            }
        }
        super.onResume();
    }
}
