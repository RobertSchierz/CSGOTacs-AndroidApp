package app.black0ut.de.gotacs.tasks;

/**
 * Created by Jan-Philipp Altenhof on 13.02.2016.
 */

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

import java.lang.ref.WeakReference;

/**
 * Diese Klasse ist mit Hilfe einer Anleitung von Google entstanden.
 * Sie ermöglicht das Laden von Bitmaps in einem Hintergrund-Thread.
 * Quelle: http://developer.android.com/training/displaying-bitmaps/load-bitmap.html
 */
public class BitmapWorkerTask extends AsyncTask<Integer, Void, Bitmap> {
    private final WeakReference<ImageView> imageViewReference;
    private int data = 0;
    private Resources mResources;
    private int BITMAP_WIDHT = 1024;
    private int BITMAP_HEIGHT = 1024;


    public BitmapWorkerTask(ImageView imageView, Resources resources) {
        // Use a WeakReference to ensure the ImageView can be garbage collected
        imageViewReference = new WeakReference<>(imageView);
        mResources = resources;
    }

    // Decode image in background.
    @Override
    protected Bitmap doInBackground(Integer... params) {
        data = params[0];
        return decodeSampledBitmapFromResource(mResources, data, BITMAP_WIDHT, BITMAP_HEIGHT);
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

    /**
     * Kalkuliert den Skalierungsfaktor.
     * @param options Options Objekt.
     * @param reqWidth Breite, zu der skaliert werden soll.
     * @param reqHeight Höhe, zu der skaliert werden soll.
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
     * Dekodiert die Bitmap.
     * @param res Anwendungsresourcen.
     * @param resId Id des zu dekodierenden Bildes.
     * @param reqWidth Breite, zu der skaliert wird.
     * @param reqHeight Höhe, zu der skaliert wird.
     * @return Skalierte Bitmap.
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
}
