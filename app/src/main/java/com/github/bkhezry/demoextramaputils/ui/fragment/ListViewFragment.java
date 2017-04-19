package com.github.bkhezry.demoextramaputils.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.github.bkhezry.demoextramaputils.R;
import com.github.bkhezry.demoextramaputils.ui.MapsActivity;
import com.github.bkhezry.demoextramaputils.utils.AppUtils;
import com.github.bkhezry.extramaputils.builder.ViewOptionBuilder;
import com.github.bkhezry.extramaputils.model.ViewOption;
import com.github.bkhezry.extramaputils.utils.MapUtils;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;

import java.util.HashSet;

public class ListViewFragment extends AppCompatActivity {
    private ListFragment mList;
    private MapAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.fragment_list_view);

        // Set a custom list adapter for a list of locations
        mAdapter = new MapAdapter(this, LIST_OPTION_VIEW);
        mList = (ListFragment) getSupportFragmentManager().findFragmentById(R.id.list);
        mList.setListAdapter(mAdapter);

        // Set a RecyclerListener to clean up MapView from ListView
        AbsListView lv = mList.getListView();
        lv.setRecyclerListener(mRecycleListener);

    }

    private class MapAdapter extends ArrayAdapter<ViewOption> {

        private final HashSet<MapView> mMaps = new HashSet<>();
        private ViewOption[] viewOptions;
        private Context context;

        public MapAdapter(Context context, ViewOption[] viewOptions) {
            super(context, R.layout.list_item, R.id.titleTextView, viewOptions);
            this.viewOptions = viewOptions;
            this.context = context;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.list_item, null);
                holder = new ViewHolder();
                holder.mapView = (MapView) convertView.findViewById(R.id.mapLite);
                holder.title = (TextView) convertView.findViewById(R.id.titleTextView);
                holder.cardView = (CardView) convertView.findViewById(R.id.cardView);
                holder.context = context;
                convertView.setTag(holder);
                holder.initializeMapView();
                mMaps.add(holder.mapView);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            final ViewOption viewOption = viewOptions[position];
            holder.mapView.setTag(viewOption);
            if (holder.map != null) {
                setMapLocation(viewOption, holder.map, context);
            }
            holder.title.setText(viewOption.getTitle());
            holder.cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Bundle args = new Bundle();
                    args.putParcelable("optionView", viewOption);
                    Intent intent = new Intent(ListViewFragment.this, MapsActivity.class);
                    intent.putExtra("args", args);
                    startActivity(intent);
                }
            });
            return convertView;
        }

        public HashSet<MapView> getMaps() {
            return mMaps;
        }
    }

    private static void setMapLocation(ViewOption viewOption, GoogleMap googleMap, Context context) {
        MapUtils.showElements(viewOption, googleMap, context);
    }

    private static class ViewHolder implements OnMapReadyCallback {
        MapView mapView;
        TextView title;
        GoogleMap map;
        CardView cardView;
        Context context;

        private ViewHolder() {

        }

        @Override
        public void onMapReady(GoogleMap googleMap) {
            MapsInitializer.initialize(context);
            MapStyleOptions style = MapStyleOptions.loadRawResourceStyle(context, R.raw.mapstyle_night);
            map = googleMap;
            map.setMapStyle(style);
            final ViewOption viewOption = (ViewOption) mapView.getTag();
            if (viewOption != null) {
                setMapLocation(viewOption, map, context);
            }
        }

        private void initializeMapView() {
            if (mapView != null) {
                mapView.onCreate(null);
                mapView.getMapAsync(this);
            }
        }

    }

    private AbsListView.RecyclerListener mRecycleListener = new AbsListView.RecyclerListener() {

        @Override
        public void onMovedToScrapHeap(View view) {
            ViewHolder holder = (ViewHolder) view.getTag();
            if (holder != null && holder.map != null) {
                holder.map.clear();
                holder.map.setMapType(GoogleMap.MAP_TYPE_NONE);
            }

        }
    };

    private static ViewOption[] LIST_OPTION_VIEW = {
            new ViewOptionBuilder()
                    .withTitle("1")
                    .withCenterCoordinates(new LatLng(35.6892, 51.3890))
                    .withMarkers(AppUtils.getListExtraMarker())
                    .withForceCenterMap(false)
                    .withIsListView(true)
                    .build(),
            new ViewOptionBuilder()
                    .withTitle("2")
                    .withCenterCoordinates(new LatLng(35.6892, 51.3890))
                    .withPolygons(
                            AppUtils.getPolygon_1(),
                            AppUtils.getPolygon_2()
                    )
                    .withForceCenterMap(false)
                    .withIsListView(true)
                    .build(),
            new ViewOptionBuilder()
                    .withTitle("3")
                    .withCenterCoordinates(new LatLng(35.6892, 51.3890))
                    .withPolylines(
                            AppUtils.getPolyline_1(),
                            AppUtils.getPolyline_2()
                    )
                    .withForceCenterMap(false)
                    .withIsListView(true)
                    .build(),
            new ViewOptionBuilder()
                    .withTitle("Night Mode")
                    .withStyleName(ViewOption.StyleDef.NIGHT)
                    .withCenterCoordinates(new LatLng(35.6892, 51.3890))
                    .withMarkers(AppUtils.getListMarker())
                    .withPolylines(AppUtils.getPolyline_3())
                    .withForceCenterMap(false)
                    .withIsListView(true)
                    .build()
    };
}
