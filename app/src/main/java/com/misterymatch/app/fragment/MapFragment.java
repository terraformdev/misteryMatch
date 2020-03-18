package com.misterymatch.app.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.misterymatch.app.R;

import com.misterymatch.app.activity.ProfileDetailedActivity;
import com.misterymatch.app.activity.SignInActivity;
import com.misterymatch.app.model.Profile;
import com.misterymatch.app.model.FindMatch;
import com.misterymatch.app.model.User;
import com.misterymatch.app.utils.GlobalData;
import com.misterymatch.app.utils.SharedHelper;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.misterymatch.app.utils.GlobalData.api;

public class MapFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnCameraMoveListener, GoogleMap.OnCameraIdleListener {
    SupportMapFragment mapFragment;
    GoogleMap googleMap;
    public static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private final LatLng mDefaultLocation = new LatLng(-33.8523341, 151.2106085);
    public static float DEFAULT_ZOOM = 15;
    private boolean mLocationPermissionGranted;
    private Location mLastKnownLocation;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    Unbinder unbinder;

    Context context;

    public MapFragment() {
    }

    public static MapFragment newInstance() {
        return new MapFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);
        context = getContext();
        unbinder = ButterKnife.bind(this, view);
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context);
        mapFragment = ((SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map));
        mapFragment.getMapAsync(this);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        //mapFragment.getMapAsync(this);
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        try {
            boolean success = googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(context, R.raw.style_json));
        } catch (Resources.NotFoundException e) {
            Log.d("Map:Style", "Can't find style. Error: ");
        }

        this.googleMap = googleMap;
        this.googleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                GlobalData.USER = (User) marker.getTag();
                startActivity(new Intent(getActivity(), ProfileDetailedActivity.class));
            }
        });
        getLocationPermission();
        updateLocationUI();
        getDeviceLocation();
    }

    void getDeviceLocation() {
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        try {
            if (mLocationPermissionGranted) {
                Task<Location> locationResult = mFusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(getActivity(), new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful() && task.getResult()!=null) {
                            // Set the map's camera position to the current location of the device.
                            mLastKnownLocation = task.getResult();
                            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                    new LatLng(mLastKnownLocation.getLatitude(),
                                            mLastKnownLocation.getLongitude()), DEFAULT_ZOOM));
                            getProfile(mLastKnownLocation);
                        } else {
                            Log.d("Map", "Current location is null. Using defaults.");
                            Log.e("Map", "Exception: %s", task.getException());
                            googleMap.moveCamera(CameraUpdateFactory
                                    .newLatLngZoom(mDefaultLocation, DEFAULT_ZOOM));
                            googleMap.getUiSettings().setMyLocationButtonEnabled(false);
                        }
                    }
                });
            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    private void getProfile(Location location) {
        HashMap<String, Object> map = new HashMap<>();
        if(location != null){
            map.put("latitude", location.getLatitude());
            map.put("longitude", location.getLongitude());
        }
        String accessToken = SharedHelper.getKey(context, "access_token");
        Call<Profile> call = GlobalData.api.getProfile(accessToken, map);
        call.enqueue(new Callback<Profile>() {
            @Override
            public void onResponse(@NonNull Call<Profile> call, @NonNull Response<Profile> response) {
                if (response.isSuccessful()) {
                    GlobalData.PROFILE = response.body();
                    Log.d("MMM", response.body().toString());
                    SharedHelper.putKey(context, "user_id", String.valueOf(GlobalData.PROFILE.getUser().getId()));
                }
            }

            @Override
            public void onFailure(@NonNull Call<Profile> call, @NonNull Throwable t) {
            }
        });
    }

    public void getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(getActivity().getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        //unbinder.unbind();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                }
            }
        }
        updateLocationUI();
    }

    private void updateLocationUI() {
        if (googleMap == null) {
            return;
        }
        try {
            if (mLocationPermissionGranted) {
                googleMap.setMyLocationEnabled(true);
                googleMap.getUiSettings().setMyLocationButtonEnabled(true);
                googleMap.setOnCameraMoveListener(this);
                googleMap.setOnCameraIdleListener(this);
                //getUsers();
                getFindMatch();
            } else {
                googleMap.setMyLocationEnabled(false);
                googleMap.getUiSettings().setMyLocationButtonEnabled(false);
                mLastKnownLocation = null;
                getLocationPermission();
            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    @Override
    public void onCameraIdle() {
        /*try {
            CameraPosition cameraPosition = googleMap.getCameraPosition();
            Log.d("LatLon", String.valueOf(cameraPosition.target.latitude));
            Log.d("LatLon", String.valueOf(cameraPosition.target.longitude));
            //getAddress(srcLat, srcLng);
        } catch (Exception e) {
            e.printStackTrace();
        }*/

    }

    @Override
    public void onCameraMove() {

    }

    private void getUsers() {
        googleMap.clear();
        View markerView = ((LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.marker_custom, null);
        LatLng latLng1 = new LatLng(13.057729177659835, 80.2531736344099);

        Marker marker = googleMap.addMarker(new MarkerOptions()
                .position(latLng1)
                .title("Santhosh")
                .snippet("Lorum Ipsum")
                .icon(BitmapDescriptorFactory.fromBitmap(createDrawableFromView(getContext(), markerView))));

    }

    private void getFindMatch() {

        String accessToken = SharedHelper.getKey(getActivity(), "access_token");
        Call<FindMatch> call = api.findMatch(accessToken);
        call.enqueue(new Callback<FindMatch>() {
            @Override
            public void onResponse(@NonNull Call<FindMatch> call, @NonNull Response<FindMatch> response) {
                if (response.isSuccessful()) {
                    googleMap.clear();
                    List<User> list = response.body().getUser();
                    for (User card : list) {
                        View markerView = ((LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.marker_custom, null);
                        CircleImageView picture = (CircleImageView) markerView.findViewById(R.id.picture);
                        Glide.with(getActivity()).load(card.getPicture()).apply(new RequestOptions().placeholder(R.drawable.user).error(R.drawable.user).dontAnimate()).into(picture);
                        LatLng latLng1 = new LatLng(card.getLatitude(), card.getLongitude());

                        Marker marker = googleMap.addMarker(new MarkerOptions()
                                .position(latLng1)
                                .title(card.getFirstName())
                                .snippet(String.format(Locale.getDefault(), "%.0f Km Away", card.getDistance()))
                                .icon(BitmapDescriptorFactory.fromBitmap(createDrawableFromView(getContext(), markerView))));
                        marker.setTag(card);
                    }

                } else {
                    if (response.code() == 401) {
                        SharedHelper.putKey(getActivity(), "logged_in", false);
                        startActivity(new Intent(getActivity(), SignInActivity.class));
                        getActivity().finish();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<FindMatch> call, @NonNull Throwable t) {
                //Toast.makeText(getActivity(), "500 Internal Server Error", Toast.LENGTH_LONG).show();
            }
        });
    }

    public static Bitmap createDrawableFromView(Context context, View view) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        view.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT));
        view.measure(displayMetrics.widthPixels, displayMetrics.heightPixels);
        view.layout(0, 0, displayMetrics.widthPixels, displayMetrics.heightPixels);
        view.buildDrawingCache();
        Bitmap bitmap = Bitmap.createBitmap(view.getMeasuredWidth(), view.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);

        return bitmap;
    }
}
