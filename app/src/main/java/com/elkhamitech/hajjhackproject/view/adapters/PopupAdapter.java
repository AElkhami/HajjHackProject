package com.elkhamitech.hajjhackproject.view.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.elkhamitech.hajjhackproject.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

public class PopupAdapter implements GoogleMap.InfoWindowAdapter {

    private View popup=null;
    private LayoutInflater inflater=null;

    public PopupAdapter(LayoutInflater inflater) {
        this.inflater=inflater;
    }

    @Override
    public View getInfoWindow(Marker marker) {
        return(null);
    }

    @Override
    public View getInfoContents(Marker marker) {
        if (popup == null) {
            popup=inflater.inflate(R.layout.map_popup, null);
        }

        TextView tv=(TextView)popup.findViewById(R.id.title);

        tv.setText(marker.getTitle());
//        tv=(TextView)popup.findViewById(R.id.snippet);
//        tv.setText(marker.getSnippet());

        return(popup);
    }
}
