package app.black0ut.de.map_service_android.fragments;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.squareup.picasso.Picasso;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import app.black0ut.de.map_service_android.DrawingView;
import app.black0ut.de.map_service_android.R;
import app.black0ut.de.map_service_android.data.Map;

/**
 * Created by Jan on 03.01.16.
 */

@EFragment(R.layout.fragment_maps_detail)
public class MapsDetailFragment extends Fragment{

    @ViewById(R.id.map_image)
    ImageView mapImage;

    @ViewById(R.id.canvas)
    LinearLayout canvas;

    @AfterViews
    public void afterViews(){

        checkMapName();

        DrawingView.mPaint.setAntiAlias(true);
        DrawingView.mPaint.setDither(true);
        DrawingView.mPaint.setColor(Color.GREEN);
        DrawingView.mPaint.setStyle(Paint.Style.STROKE);
        DrawingView.mPaint.setStrokeJoin(Paint.Join.ROUND);
        DrawingView.mPaint.setStrokeCap(Paint.Cap.ROUND);
        DrawingView.mPaint.setStrokeWidth(12);

        //LinearLayout canvas = (LinearLayout)getView().findViewById(R.id.canvas);

        DrawingView mDrawingView = new DrawingView(getContext());
        canvas.addView(mDrawingView);
    }

    private void checkMapName(){
        switch(Map.clickedMapName){
            case Map.ASSAULT:
                Picasso.with(getContext()).load(R.drawable.cs_assault_radar).into(mapImage);
                //mapImage.setImageResource(R.drawable.cs_assault_radar);
                break;
            case Map.AZTEC:
                Picasso.with(getContext()).load(R.drawable.de_aztec_radar_spectate).into(mapImage);
                //mapImage.setImageResource(R.drawable.de_aztec_radar_spectate);
                break;
            case Map.CACHE:
                Picasso.with(getContext()).load(R.drawable.de_cache_radar_spectate).into(mapImage);
                //mapImage.setImageResource(R.drawable.de_cache_radar_spectate);
                break;
            case Map.COBBLESTONE:
                Picasso.with(getContext()).load(R.drawable.de_cbble_radar).into(mapImage);
                //mapImage.setImageResource(R.drawable.de_cbble_radar);
                break;
            case Map.DUST:
                Picasso.with(getContext()).load(R.drawable.de_dust_radar_spectate).into(mapImage);
                //mapImage.setImageResource(R.drawable.de_dust_radar_spectate);
                break;
            case Map.DUST2:
                Picasso.with(getContext()).load(R.drawable.de_dust2_radar_spectate).into(mapImage);
                //mapImage.setImageResource(R.drawable.de_dust2_radar_spectate);
                break;
            case Map.INFERNO:
                Picasso.with(getContext()).load(R.drawable.de_inferno_radar_spectate).into(mapImage);
                //mapImage.setImageResource(R.drawable.de_inferno_radar_spectate);
                break;
            case Map.ITALY:
                Picasso.with(getContext()).load(R.drawable.cs_italy_radar).into(mapImage);
                //mapImage.setImageResource(R.drawable.cs_italy_radar);
                break;
            case Map.MILITIA:
                Picasso.with(getContext()).load(R.drawable.cs_militia_radar_spectate).into(mapImage);
                //mapImage.setImageResource(R.drawable.cs_militia_radar_spectate);
                break;
            case Map.MIRAGE:
                Picasso.with(getContext()).load(R.drawable.de_mirage_radar_spectate).into(mapImage);
                //mapImage.setImageResource(R.drawable.de_mirage_radar_spectate);
                break;
            case Map.NUKE:
                Picasso.with(getContext()).load(R.drawable.de_nuke_radar_spectate).into(mapImage);
                //mapImage.setImageResource(R.drawable.de_nuke_radar_spectate);
                break;
            case Map.OFFICE:
                Picasso.with(getContext()).load(R.drawable.cs_office_radar).into(mapImage);
                //mapImage.setImageResource(R.drawable.cs_office_radar);
                break;
            case Map.OVERPASS:
                Picasso.with(getContext()).load(R.drawable.de_overpass_radar).into(mapImage);
                //mapImage.setImageResource(R.drawable.de_overpass_radar);
                break;
            case Map.TRAIN:
                Picasso.with(getContext()).load(R.drawable.de_train_radar_spectate).into(mapImage);
                //mapImage.setImageResource(R.drawable.de_train_radar_spectate);
                break;
            case Map.VERTIGO:
                Picasso.with(getContext()).load(R.drawable.de_vertigo_radar).into(mapImage);
                //mapImage.setImageResource(R.drawable.de_vertigo_radar);
                break;
            default:
                Log.d("MAP CLICK", "No image for the clicked Map.");
        }
    }

    @Click(R.id.edit_button)
    public void onEditButtonClick(){

    }

    /*
    @Override
    public View onCreateView (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){

        DrawingView.mPaint.setAntiAlias(true);
        DrawingView.mPaint.setDither(true);
        DrawingView.mPaint.setColor(Color.GREEN);
        DrawingView.mPaint.setStyle(Paint.Style.STROKE);
        DrawingView.mPaint.setStrokeJoin(Paint.Join.ROUND);
        DrawingView.mPaint.setStrokeCap(Paint.Cap.ROUND);
        DrawingView.mPaint.setStrokeWidth(12);

        LinearLayout canvas = (LinearLayout)getView().findViewById(R.id.canvas);

        DrawingView mDrawingView = new DrawingView(getContext());
        canvas.addView(mDrawingView);

        return new DrawingView(getContext());
    }*/

    /*
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dv = new DrawingView(this);
        setContentView(dv);
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setColor(Color.GREEN);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(12);
    }*/

}
