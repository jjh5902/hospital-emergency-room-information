package com.project.jsh.androidprj;


import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;


/**
 * A simple {@link Fragment} subclass.
 */
public class PagerOne extends Fragment
        implements OnMapReadyCallback, Button.OnClickListener {

    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 2002;
    //
    private MapView mapView = null; // 맵뷰
    private GoogleMap googleMap = null; // 구글맵
    private Marker currentMarker = null; // 지정 위치 마커
    //
    private String locInfo1 = null; //
    private String locInfo2 = null; //
    //
    private DocumentBuilderFactory t_dbf = null;
    private DocumentBuilder t_db = null;
    private Document t_doc = null;
    private NodeList t_nodes = null;
    private Node t_node = null;
    private Element t_element = null;
    private InputSource t_is = null;
    //
    private ArrayList<Marker> previous_marker = null; // 응급실 마커 리스트

    //
    public PagerOne() {
        // Required empty public constructor
    }

//===================================================================================================

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_pager_one, container, false);

        // MapView init
        mapView = (MapView) view.findViewById(R.id.map);
        mapView.getMapAsync(this);

        // AutoCompleteFragment init
        PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
                getActivity().getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);

        // 자동 완성 검색 시
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                LatLng latLng = new LatLng(place.getLatLng().latitude,
                        place.getLatLng().longitude);

                currentMarker.remove();
                MarkerOptions markerOptions = new MarkerOptions();

                markerOptions.position(latLng);
                markerOptions.title(place.getName().toString());
                markerOptions.snippet(place.getAddress().toString());
                markerOptions.draggable(true);
                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));

                currentMarker = googleMap.addMarker(markerOptions);
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 13));
            }

            @Override
            public void onError(Status status) {
                Log.d("AUTO_ERROR", "Error : " + status);
            }
        });

        // Button init
        Button btn = (Button) view.findViewById(R.id.SearchBtn);
        btn.setOnClickListener(this);

        // Thread init
        try {
            t_dbf = DocumentBuilderFactory.newInstance();
            t_db = t_dbf.newDocumentBuilder();
            t_is = new InputSource();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }

        apiThread = new APIThread(mHandler);
        apiThread.setDaemon(true);
        apiThread.start();

        // MarkerList init
        previous_marker = new ArrayList<Marker>();
        return view;
    }

    //===================================================================================================

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (mapView != null)
            mapView.onCreate(savedInstanceState);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    //===================================================================================================

    @Override
    public void onMapReady(final GoogleMap googleMap) {

        this.googleMap = googleMap;
        googleMap.getUiSettings().setCompassEnabled(true); // 나침반 설정

        if (PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION)) { // 권한 있는 경우
            googleMap.setMyLocationEnabled(true);

            googleMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() { // 현재 위치 버튼 클릭 시
                @Override
                public boolean onMyLocationButtonClick() {
                    Location location = googleMap.getMyLocation();

                    setCurrentLoc(location);

                    return true;
                }
            });

            googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() { // 맵 클릭시
                @Override
                public void onMapClick(LatLng latLng) {

                    currentMarker.remove();
                    MarkerOptions markerOptions = new MarkerOptions();

                    markerOptions.position(latLng);
                    markerOptions.title("기준 위치");
                    markerOptions.snippet(getAddress(latLng));
                    markerOptions.draggable(true);
                    markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));

                    currentMarker = googleMap.addMarker(markerOptions);
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 13));
                }
            });
        } else {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }

        setCurrentLoc(null);
    }

    public void setCurrentLoc(Location location) { // 위치 지정

        if (currentMarker != null) {
            currentMarker.remove();
        }

        if (location != null) {
            LatLng currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());

            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(currentLatLng);
            markerOptions.title("내 위치");
            markerOptions.snippet(getAddress(currentLatLng));
            markerOptions.draggable(true);
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));

            currentMarker = this.googleMap.addMarker(markerOptions);
            this.googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 13));

            return;
        }

        // 위치를 찾을 수 없는 경우
        LatLng SEOUL = new LatLng(37.55, 126.99);
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(SEOUL);
        markerOptions.title("서울");
        markerOptions.snippet(getAddress(SEOUL));
        markerOptions.draggable(true);
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));

        currentMarker = googleMap.addMarker(markerOptions);
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(SEOUL, 13));
    }

    public String getAddress(LatLng latLng) { // 좌표 -> 주소 변환
        Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());
        List<Address> addresses = null;

        try {

            addresses = geocoder.getFromLocation(
                    latLng.latitude,
                    latLng.longitude,
                    1
            );
        } catch (IOException e) {
            return "주소 변환 불가";
        } catch (IllegalArgumentException e) {
            return "잘못된 GPS 좌표";
        }

        if (addresses == null || addresses.size() == 0) {
            return "주소 식별 불가";
        }
        Address address = addresses.get(0);
        return address.getAddressLine(0).toString();
    }

    //===================================================================================================

    @Override
    public void onClick(View v) { // 검색 버튼

        LatLng latLng = new LatLng(
                currentMarker.getPosition().latitude,
                currentMarker.getPosition().longitude
        );

        String locInfo = getAddress(latLng); // 현재 마커의 주소를 가져옴
        String[] split = locInfo.split(" ");

        // 도,시,구 별 주소 저장
        locInfo1 = split[1];
        locInfo2 = split[2];
      //  if (split[3].charAt(split[3].length() - 1) == '구')
       //     locInfo2 += split[3];
        Log.d("LOC_INFO", locInfo1 + " " + locInfo2);
        //
        Message msg = new Message();
        msg.what = 1;
        msg.obj = locInfo1 + "&" + locInfo2 + "&" + latLng.latitude + "&" + latLng.longitude;
        apiThread.getFgHandler().sendMessage(msg);
    }

    //===================================================================================================

    private APIThread apiThread;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            try {
                // 전에 마크해두었던 응급실 마커 삭제
                if (previous_marker != null) {
                    for (int i = 0; i < previous_marker.size(); i++) {
                        previous_marker.get(i).remove();
                    }
                    previous_marker.clear();
                }

                t_is.setCharacterStream(new StringReader(msg.obj.toString()));
                t_doc = t_db.parse(t_is);
                t_nodes = t_doc.getElementsByTagName("item");

                for (int i = 0; i < t_nodes.getLength(); i++) {
                    t_node = t_nodes.item(i);
                    t_element = (Element) t_node;

                    String dutyName = t_element.getElementsByTagName("dutyName").item(0).getTextContent();
                    String dutyAddr = t_element.getElementsByTagName("dutyAddr").item(0).getTextContent();
                    Double Lat = Double.parseDouble(t_element.getElementsByTagName("wgs84Lat").item(0).getTextContent());
                    Double Lng = Double.parseDouble(t_element.getElementsByTagName("wgs84Lon").item(0).getTextContent());

                    // 마커 추가
                    LatLng latLng = new LatLng(Lat, Lng);
                    MarkerOptions markerOptions = new MarkerOptions();

                    markerOptions.position(latLng);
                    markerOptions.title(dutyName);
                    markerOptions.snippet(dutyAddr);
                    markerOptions.draggable(true);
                    markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));

                    previous_marker.add(googleMap.addMarker(markerOptions));
                }


            } catch (Exception e) {
                e.printStackTrace();
                Log.d("THREAD_ERR", "handler_error1");
            }
        }
    };
}
