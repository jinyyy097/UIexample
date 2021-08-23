package com.example.uiexample;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;

import com.example.uiexample.databinding.FragmentLoginBinding;
import com.example.uiexample.databinding.FragmentMainBinding;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;


public class MainFragment extends Fragment implements OnMapReadyCallback {

    private FragmentMainBinding binding;
    private MapView mapView;
    private ArrayList<JSONObject> js;
    private GoogleMap mMap;
    private ArrayList<MarkerOptions> markeroptions;
    private JSONObject jsonObject;
    private JSONArray Array ;
    private JSONObject Object ;
    private JSONArray Array2 ;
    private CameraPosition cameraPosition;
    private ArrayList<Marker> makers;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentMainBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);

        mapFragment.getMapAsync(this);
        js = new ArrayList<JSONObject>();
        markeroptions = new ArrayList<MarkerOptions>();
        makers = new ArrayList<Marker>();

        try {
            InputStream is = getActivity().getAssets().open("jsons/test.json"); // json파일 이름
            int fileSize = is.available();

            byte[] buffer = new byte[fileSize];
            is.read(buffer);
            is.close();
            String json = "";
            json = new String(buffer, "UTF-8");

            jsonObject = new JSONObject(json);
            Array = jsonObject.getJSONArray("Ducklbrd");
            Object = Array.getJSONObject(1);

            Array2 = Object.getJSONArray("row");

            for(int i=0;i<Array2.length();i++) {
                JSONObject ob = Array2.getJSONObject(i);
                js.add(ob);
                MarkerOptions makerOptions = null;
                makerOptions = new MarkerOptions().position(new LatLng(Double.valueOf(ob.getString("REFINE_WGS84_LAT")),Double.valueOf(ob.getString("REFINE_WGS84_LOGT"))))
                        .title(ob.getString("BIZPLC_NM"));
                markeroptions.add(makerOptions);
            }
        }catch (IOException ex) {
            ex.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return view;
    }



    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {

        mMap = googleMap;

        for(int i=0;i<Array2.length();i++)
        {
            makers.add(mMap.addMarker(markeroptions.get(i)));
        }


        mMap.moveCamera(CameraUpdateFactory
                .newCameraPosition(cameraPosition));
        mMap.getUiSettings().setZoomControlsEnabled(true);

        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(@NonNull Marker marker) {
                try {
                    for (int i = 0; i < js.size(); i++) {
                        if (marker.getTitle().equals(js.get(i).getString("BIZPLC_NM")))
                        {

                            Bundle bundle = new Bundle();
                            bundle.putString("json",js.get(i).toString());
                            DetailFragment detailFragment = new DetailFragment();
                            detailFragment.setArguments(bundle);
                            ((MainActivity)getActivity()).goToFragment(detailFragment);
                        }
                    }
                }catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });


    }

    @Override
    public void onResume() {


        Intent intent = getActivity().getIntent();
        if(intent.getStringExtra("modifydata")!= null)
        {

            String value = intent.getStringExtra("modifydata");
            System.out.println("value:"+value);
            for (int i = 0; i < js.size(); i++) {
                if (markeroptions.get(i).getTitle().equals(value.split(",")[0]))
                {
                    markeroptions.get(i).position(new LatLng(Double.parseDouble(value.split(",")[1]),Double.parseDouble(value.split(",")[2])));

                    try {
                        js.get(i).put("REFINE_WGS84_LAT",value.split(",")[1]);
                        js.get(i).put("REFINE_WGS84_LOGT",value.split(",")[2]);
                        Geocoder geocoder = new Geocoder(getActivity());
                        List<Address> list = geocoder.getFromLocation(Double.parseDouble(value.split(",")[1]),Double.parseDouble(value.split(",")[2]),1);
                        System.out.println(list);
                        js.get(i).put("REFINE_ROADNM_ADDR",list.get(0).getAddressLine(0));
                        if(list.get(0).getThoroughfare() == null) {
                            markeroptions.get(i).title("unknown");
                            js.get(i).put("BIZPLC_NM","unknown");
                        }
                        else {
                            js.get(i).put("BIZPLC_NM",list.get(0).getThoroughfare());
                            markeroptions.get(i).title(list.get(0).getThoroughfare());
                        }
                        for(int j=0;j<makers.size();j++)
                        {
                            if(makers.get(j).getTitle().equals(value.split(",")[0]))
                            {

                                makers.get(j).remove();;
                                makers.remove(makers.get(j));
                            }
                        }
                        makers.add(mMap.addMarker(markeroptions.get(i)));
                        cameraPosition = new CameraPosition.Builder()
                                .target(markeroptions.get(i).getPosition()).zoom(15).build();
                        mMap.moveCamera(CameraUpdateFactory
                                .newCameraPosition(cameraPosition));

                        getActivity().getIntent().getExtras().clear();
                    } catch (JSONException | IOException e) {
                        e.printStackTrace();
                    }
                }
            }



        }
        else
        {
            cameraPosition = new CameraPosition.Builder()
                    .target(markeroptions.get(0).getPosition()).zoom(15).build();
        }

        super.onResume();
    }
}