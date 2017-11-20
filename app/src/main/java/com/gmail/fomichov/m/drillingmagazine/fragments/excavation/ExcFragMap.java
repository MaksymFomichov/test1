package com.gmail.fomichov.m.drillingmagazine.fragments.excavation;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatDelegate;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.gmail.fomichov.m.drillingmagazine.MainActivity;
import com.gmail.fomichov.m.drillingmagazine.R;
import com.gmail.fomichov.m.drillingmagazine.geology.Excavation;
import com.gmail.fomichov.m.drillingmagazine.utils.MyLog;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.ui.IconGenerator;

import java.util.ArrayList;
import java.util.List;

public class ExcFragMap extends Fragment implements OnMapReadyCallback {
    private static final String NAME_OBJECT = "name_object";
    private View view;
    private GoogleMap mMap;
    private static final int REQUEST_LOCATION = 2;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.frag_map_exc, container, false);
        setHasOptionsMenu(true); // без этого меню не показывается
        SupportMapFragment mMapFragment = (SupportMapFragment) this.getChildFragmentManager().findFragmentById(R.id.map);
        mMapFragment.getMapAsync(this);

        return view;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (ActivityCompat.checkSelfPermission(getContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setMapToolbarEnabled(false);

        if (ExcFrag.list.size() > 0) {
            try {
                mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(getBoundingBox(), 200));
            } catch (Exception e) {
                // сюда попадаем, когда в обьекте есть одна или все скважины без координат
                showCurrentPosition();
                e.printStackTrace();
            }
        } else {
            showCurrentPosition();
        }
    }


    private void showCurrentPosition() {
        LocationManager service = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        String provider = service.getBestProvider(criteria, false);
        // получаем разрешения на использования местоположения
        if (ActivityCompat.checkSelfPermission(getContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION,
                    android.Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
        } else {
            Location location = service.getLastKnownLocation(provider);
            if (location != null) {
                LatLng myLocation = new LatLng(location.getLatitude(), location.getLongitude());
                mMap.addMarker(new MarkerOptions().position(myLocation).title("latitude:" + location.getLatitude() + ", longitude:" + location.getLongitude()));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(myLocation));
            } else {
                LatLng myLocation = new LatLng(50.469926, 30.550516);
                mMap.addMarker(new MarkerOptions().position(myLocation));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(myLocation));
            }
        }
    }

    // получаем маркеры на карту
    private LatLngBounds getBoundingBox() {
        List<Marker> markers = new ArrayList<>();
        LatLngBounds.Builder llBuilder = LatLngBounds.builder();
        IconGenerator icnGenerator = new IconGenerator(getContext());
        for (Excavation value : ExcFrag.list) {


            // if проверяем на ввод значений
            if (!value.getLatitude().equals("") && !value.getLongitude().equals("")) {
                double latitude = Double.parseDouble(value.getLatitude());
                double longitude = Double.parseDouble(value.getLongitude());
                MarkerOptions options = new MarkerOptions();
                Bitmap iconBitmap = icnGenerator.makeIcon(value.getTypeExcavation() + " " + value.getNameExcavation());
                options.icon(BitmapDescriptorFactory.fromBitmap(iconBitmap));

//                if (value.getTypeExcavation().equals("ДУДКА")) {
//                    options.icon(getBitmapDescriptor(R.drawable.ic_exc_dudka_black_24dp));
//                } else if (value.getTypeExcavation().equals("СКВ")) {
//                    options.icon(getBitmapDescriptor(R.drawable.ic_exc_well_black_24dp));
//                } else if (value.getTypeExcavation().equals("ТСЗ")) {
//                    options.icon(getBitmapDescriptor(R.drawable.ic_exc_zond_stat_black_24dp));
//                } else if (value.getTypeExcavation().equals("ТДЗ")) {
//                    options.icon(getBitmapDescriptor(R.drawable.ic_exc_zond_din_black_24dp));
//                } else if (value.getTypeExcavation().equals("ШУРФ")) {
//                    options.icon(getBitmapDescriptor(R.drawable.ic_exc_pit_black_24dp));
//                }

                options.position(new LatLng(latitude, longitude));
                markers.add(mMap.addMarker(options));
                for (int i = 0; i < markers.size(); i++) {
                    llBuilder.include(markers.get(i).getPosition());
                }
            }
        }
        return llBuilder.build();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        inflater.inflate(R.menu.menu_map, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_map_type_normal:
                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                return true;
            case R.id.menu_map_type_hybrid:
                mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                return true;
            case R.id.menu_map_type_satellite:
                mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                return true;
            case R.id.menu_map_type_terrain:
                mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    // прячем меню загрузки выработки
    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        menu.findItem(R.id.menuDownload).setVisible(false);
    }

    // конвертируем VectorDrawable в BitmapDescriptor
    private BitmapDescriptor getBitmapDescriptor(@DrawableRes int id) {
        Drawable vectorDrawable = ResourcesCompat.getDrawable(getResources(), id, null);
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(),
                vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    public static ExcFragMap newInstance(String nameObject) {
        ExcFragMap fragment = new ExcFragMap();
        Bundle args = new Bundle();
        args.putString(NAME_OBJECT, nameObject);
        fragment.setArguments(args);
        return fragment;
    }
}
