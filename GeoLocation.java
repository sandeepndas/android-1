package com.sandeepndas.whami;

import android.app.Activity;
import android.content.Intent;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;
import java.util.Locale;


public class GeoLocation extends Activity implements LocationListener {

    private TextView Locationview, Addressview, Pincodeview, Coordinatesview;
    private Button Getloc, Setting;
    private LocationManager locationManager;
    private ConnectivityManager CheckDataCon;
    private String bestProvider;
    Geocoder geocoder;
    List<Address> address = null;
    Address curaddres;
    private NetworkInfo info;
    private boolean netenabled=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_geo_location);
        Locationview = (TextView) findViewById(R.id.locid);
        Addressview = (TextView) findViewById(R.id.addressid);
        Pincodeview = (TextView) findViewById(R.id.pincodeid);
        Coordinatesview = (TextView) findViewById(R.id.coordinatesid);
        Getloc = (Button)findViewById(R.id.getlocid);
        Setting = (Button) findViewById(R.id.settingid);

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        bestProvider = locationManager.getBestProvider(criteria, true);
        locationManager.requestLocationUpdates(bestProvider, 4000, 0, this);
        CheckDataCon = (ConnectivityManager)getSystemService(CONNECTIVITY_SERVICE);

        geocoder = new Geocoder(GeoLocation.this, Locale.getDefault());

        Setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent set = new Intent(GeoLocation.this, Setting.class);
                startActivity(set);

            }
        });
        Getloc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!locationManager.isProviderEnabled((LocationManager.GPS_PROVIDER))){
                    Toast.makeText(GeoLocation.this, "Enable GPS", Toast.LENGTH_SHORT).show();
                }else{
                    CheckDataConnection();
                    if (netenabled == false){
                        Toast.makeText(GeoLocation.this, "Enable Net Connection", Toast.LENGTH_SHORT).show();
                    }
                    Location location = locationManager.getLastKnownLocation(bestProvider);
                    if(location != null){
                        onLocationChanged(location);
                    }else{
                        Toast.makeText(GeoLocation.this, "Retry", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    public void CheckDataConnection(){
        try{
            netenabled = CheckDataCon.getActiveNetworkInfo().isConnected();
        }catch (Exception e){
            netenabled = false;
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        CheckDataConnection();
        try{
            Coordinatesview.setText(location.getLatitude()+" "+location.getLongitude());

            if(netenabled) {
                address = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                curaddres = address.get(0);
                Locationview.setText(curaddres.getAddressLine(0));
                Addressview.setText(curaddres.getSubLocality()
                        + "\n" + curaddres.getLocality()
                        + "\n" +curaddres.getAdminArea()
                        + "\n" + curaddres.getCountryName());
                Pincodeview.setText(curaddres.getPostalCode());
            }
        }catch(Exception e){
            Toast.makeText(GeoLocation.this, "Error in GPS", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
