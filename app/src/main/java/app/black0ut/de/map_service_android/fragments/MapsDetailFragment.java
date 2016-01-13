package app.black0ut.de.map_service_android.fragments;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.widget.ToggleButton;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import java.lang.ref.WeakReference;

import app.black0ut.de.map_service_android.DrawingView;
import app.black0ut.de.map_service_android.R;
import app.black0ut.de.map_service_android.data.Map;

/**
 * Created by Jan-Philipp Altenhof on 03.01.16.
 *
 * Diese Klasse beschreibt das Fragment, welches für das Darstellen einer ausgewählten Karte zuständig ist.
 * Es beinhaltet ein Layout mit zwei 'ImageViews' welche zum einen das Bild der Karte ohne Callouts
 * und zum Anderen die Karte inklusive Callouts anzeigen.
 * Die 'ImageView' für die Callouts kann bei Bedarf durch den Nutzer an- und abgeschaltet werden.
 */

@EFragment(R.layout.fragment_maps_detail)
public class MapsDetailFragment extends Fragment{

    @ViewById(R.id.map_image)
    ImageView mapImage;

    @ViewById(R.id.map_callouts)
    ImageView mapCallouts;

    @ViewById(R.id.canvas)
    LinearLayout canvas;

    @ViewById(R.id.show_callouts_button)
    ToggleButton showCallouts;

    private int BITMAP_WIDHT = 256;
    private int BITMAP_HEIGHT = 256;

    @AfterViews
    public void afterViews(){

        checkMapName();

        showCallouts.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mapCallouts.setVisibility(View.VISIBLE);
                    //Toast.makeText(getContext(), "Show clicked", Toast.LENGTH_SHORT).show();
                } else {
                    // The toggle is disabled
                    mapCallouts.setVisibility(View.GONE);
                }
            }
        });

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

    public void loadMapBitmap(int resId, ImageView imageView) {
        BITMAP_WIDHT = 256;
        BITMAP_HEIGHT = 256;
        BitmapWorkerTask task = new BitmapWorkerTask(imageView);
        task.execute(resId);
    }

    public void loadCalloutBitmap(int resId, ImageView imageView) {
        BITMAP_WIDHT = 1024;
        BITMAP_HEIGHT = 1024;
        BitmapWorkerTask task = new BitmapWorkerTask(imageView);
        task.execute(resId);
    }

    /**
     * Quelle: http://developer.android.com/training/displaying-bitmaps/load-bitmap.html
     * @param options
     * @param reqWidth
     * @param reqHeight
     * @return
     */
    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    /**
     * Quelle: http://developer.android.com/training/displaying-bitmaps/load-bitmap.html
     * @param res
     * @param resId
     * @param reqWidth
     * @param reqHeight
     * @return
     */
    public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId,
                                                         int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);
    }

    /**
     * Prüft den Namen der geklickten Karte und setzt dann die ImageResource der ImageView auf das passende Bild.
     */
    private void checkMapName(){
        switch(Map.clickedMapName){
            case Map.ASSAULT:
                //Picasso.with(getContext()).load(R.drawable.cs_assault_radar).into(mapImage);
                loadMapBitmap(R.drawable.cs_assault_radar, mapImage);
                break;
            case Map.AZTEC:
                //Picasso.with(getContext()).load(R.drawable.de_aztec_radar_spectate).into(mapImage);
                loadMapBitmap(R.drawable.de_aztec_radar_spectate, mapImage);
                break;
            case Map.CACHE:
                //Picasso.with(getContext()).load(R.drawable.de_cache_radar_spectate).into(mapImage);
                //Picasso.with(getContext()).load(R.drawable.de_cache_radar_spectate_callout).into(mapCallouts);
                loadMapBitmap(R.drawable.de_cache_radar_spectate, mapImage);
                loadCalloutBitmap(R.drawable.de_cache_radar_spectate_callout, mapCallouts);
                break;
            case Map.COBBLESTONE:
                //Picasso.with(getContext()).load(R.drawable.de_cbble_radar).into(mapImage);
                //Picasso.with(getContext()).load(R.drawable.de_cbble_radar_callout).into(mapCallouts);
                loadMapBitmap(R.drawable.de_cbble_radar, mapImage);
                loadCalloutBitmap(R.drawable.de_cbble_radar_callout, mapCallouts);
                break;
            case Map.DUST:
                //Picasso.with(getContext()).load(R.drawable.de_dust_radar_spectate).into(mapImage);
                loadMapBitmap(R.drawable.de_dust_radar_spectate, mapImage);
                break;
            case Map.DUST2:
                //Picasso.with(getContext()).load(R.drawable.de_dust2_radar_spectate).into(mapImage);
                //Picasso.with(getContext()).load(R.drawable.de_dust2_radar_spectate_callout).into(mapCallouts);
                loadMapBitmap(R.drawable.de_dust2_radar_spectate, mapImage);
                loadCalloutBitmap(R.drawable.de_dust2_radar_spectate_callout, mapCallouts);
                break;
            case Map.INFERNO:
                //Picasso.with(getContext()).load(R.drawable.de_inferno_radar_spectate).into(mapImage);
                //Picasso.with(getContext()).load(R.drawable.de_inferno_radar_spectate_callout).into(mapCallouts);
                loadMapBitmap(R.drawable.de_inferno_radar_spectate, mapImage);
                loadCalloutBitmap(R.drawable.de_inferno_radar_spectate_callout, mapCallouts);
                break;
            case Map.ITALY:
                //Picasso.with(getContext()).load(R.drawable.cs_italy_radar).into(mapImage);
                loadMapBitmap(R.drawable.cs_italy_radar, mapImage);
                break;
            case Map.MILITIA:
                //Picasso.with(getContext()).load(R.drawable.cs_militia_radar_spectate).into(mapImage);
                loadMapBitmap(R.drawable.cs_militia_radar_spectate, mapImage);
                break;
            case Map.MIRAGE:
                //Picasso.with(getContext()).load(R.drawable.de_mirage_radar_spectate).into(mapImage);
                //Picasso.with(getContext()).load(R.drawable.de_mirage_radar_spectate_callout).into(mapCallouts);
                loadMapBitmap(R.drawable.de_mirage_radar_spectate, mapImage);
                loadCalloutBitmap(R.drawable.de_mirage_radar_spectate_callout, mapCallouts);
                break;
            case Map.NUKE:
                //Picasso.with(getContext()).load(R.drawable.de_nuke_radar_spectate).into(mapImage);
                loadMapBitmap(R.drawable.de_nuke_radar_spectate, mapImage);
                break;
            case Map.OFFICE:
                //Picasso.with(getContext()).load(R.drawable.cs_office_radar).into(mapImage);
                loadMapBitmap(R.drawable.cs_office_radar, mapImage);
                break;
            case Map.OVERPASS:
                //Picasso.with(getContext()).load(R.drawable.de_overpass_radar).into(mapImage);
                //Picasso.with(getContext()).load(R.drawable.de_overpass_radar_callout).into(mapCallouts);
                loadMapBitmap(R.drawable.de_overpass_radar, mapImage);
                loadCalloutBitmap(R.drawable.de_overpass_radar_callout, mapCallouts);
                break;
            case Map.TRAIN:
                //Picasso.with(getContext()).load(R.drawable.de_train_radar_spectate).into(mapImage);
                //Picasso.with(getContext()).load(R.drawable.de_train_radar_spectate_callout).into(mapCallouts);
                loadMapBitmap(R.drawable.de_train_radar_spectate, mapImage);
                loadCalloutBitmap(R.drawable.de_train_radar_spectate_callout, mapCallouts);
                break;
            case Map.VERTIGO:
                //Picasso.with(getContext()).load(R.drawable.de_vertigo_radar).into(mapImage);
                loadMapBitmap(R.drawable.de_vertigo_radar, mapImage);
                break;
            default:
                Log.d("MAP CLICK", "No image for the clicked Map.");
        }
    }

    @Click(R.id.edit_button)
    public void onEditButtonClick(){
        Toast.makeText(getContext(), "Edit Strat clicked", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        mapImage.setImageDrawable(null);
        mapCallouts.setImageDrawable(null);
        mapCallouts.setVisibility(View.GONE);
    }

    /**
     * Quelle: http://developer.android.com/training/displaying-bitmaps/load-bitmap.html
     */
    class BitmapWorkerTask extends AsyncTask<Integer, Void, Bitmap> {
        private final WeakReference<ImageView> imageViewReference;
        private int data = 0;

        public BitmapWorkerTask(ImageView imageView) {
            // Use a WeakReference to ensure the ImageView can be garbage collected
            imageViewReference = new WeakReference<ImageView>(imageView);
        }

        // Decode image in background.
        @Override
        protected Bitmap doInBackground(Integer... params) {
            data = params[0];
            return decodeSampledBitmapFromResource(getResources(), data, BITMAP_WIDHT, BITMAP_HEIGHT);
        }

        // Once complete, see if ImageView is still around and set bitmap.
        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (imageViewReference != null && bitmap != null) {
                final ImageView imageView = imageViewReference.get();
                if (imageView != null) {
                    imageView.setImageBitmap(bitmap);
                }
            }
        }
    }

}
