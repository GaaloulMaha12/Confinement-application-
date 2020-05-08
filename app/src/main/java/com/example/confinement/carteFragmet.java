package com.example.confinement;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.confinement.model.VeloAPI;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Queue;

public class carteFragmet extends Fragment implements OnMapReadyCallback {
    GoogleMap mapAPI;
    SupportMapFragment mapFragment;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_carte, container, false);

        if (ActivityCompat.checkSelfPermission(
                getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 0);
        } else {
            SupportMapFragment  mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.mapAPI);
            mapFragment.getMapAsync(this);
        }
        return view;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
      if (grantResults.length> 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
          SupportMapFragment  mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.mapAPI);
          mapFragment.getMapAsync(this);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mapAPI = googleMap;
        mapAPI.setMyLocationEnabled(true);

        googleMap.setMyLocationEnabled(true);
         LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        String  provider = locationManager.getBestProvider(criteria, true);
        Location location   =  null;
        if (provider  != null) {
            location = locationManager.getLastKnownLocation(provider);

            if (location!= null) {
                CameraUpdate yourLocation = CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 10f);
                mapAPI.animateCamera(yourLocation);
            }

        }

        //TODO WS Reader MAP API , recommndation ( retrofit )


        RequestQueue queue = Volley.newRequestQueue(getActivity());
        String url ="https://public.opendatasoft.com/api/records/1.0/search/?dataset=station-velov-grand-lyon&facet=name&facet=commune&facet=bonus&facet=status&facet=available&facet=availabl_1&facet=availabili&facet=availabi_1&facet=last_upd_1";
        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        Log.d("API response ","Response is: "+ response);
                        JSONObject jsonObject = null;
                        try {
                            jsonObject = new JSONObject(response);
                            Gson gson = new Gson();
                            VeloAPI veloAPI= gson.fromJson(response,VeloAPI.class);
                            for (int i=0;i<veloAPI.getRecords().size();i++)
                            {
                                Double lat = Double.valueOf(veloAPI.getRecords().get(i).getFields().getLat());
                                Double lng = Double.valueOf(veloAPI.getRecords().get(i).getFields().getLng());
                                String title = veloAPI.getRecords().get(i).getFields().getAddress();
                                LatLng sydney = new LatLng(lat, lng);
                                mapAPI.addMarker(new
                                    MarkerOptions().position(sydney).title(title));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("API response ","That didn't work!");
            }
        });

// Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

}
