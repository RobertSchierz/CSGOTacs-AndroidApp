package app.black0ut.de.map_service_android.views;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.net.URISyntaxException;
import java.util.HashMap;

import app.black0ut.de.map_service_android.R;
import app.black0ut.de.map_service_android.data.Connect;
import app.black0ut.de.map_service_android.data.LocalStrategy;
import app.black0ut.de.map_service_android.fragments.MapsDetailFragment;
import app.black0ut.de.map_service_android.jsoncreator.JSONCreator;
import io.socket.client.IO;
import io.socket.client.Socket;

/**
 * Created by Jan-Philipp Altenhof on 08.01.2016.
 *
 * Bei dieser Klasse wurde die FingerPaint Demo von Google zur Hilfe genommen.
 * Quelle: https://android.googlesource.com/platform/development/+/master/samples/ApiDemos/src/com/example/android/apis/graphics/FingerPaint.java
 */

/**
 * Klasse, welche das Zeichnen per Finger auf dem Bildschirm ermöglicht.
 */
public class DrawingView extends View {
    public Paint sPaint = new Paint();
    public static boolean isStrategy = false;
    public static boolean isLiveMode = false;
    private static final float TOUCH_TOLERANCE = 4;

    public static boolean[] sDrag;
    public static double[] sX;
    public static double[] sY;

    public int width;
    public int height;

    private float mX, mY;
    private float mLiveX, mLiveY;

    public LocalStrategy mLocalStrategy;

    private Bitmap mBitmap;
    public Canvas mCanvas;
    public Path mPath;
    public Path mLivePath;
    private Paint mBitmapPaint;
    private Paint mLivePaint;
    Context context;
    private Paint circlePaint;
    private Path circlePath;

    private Socket mSocket;

    {
        try {
            IO.Options opts = new IO.Options();
            opts.forceNew = true;
            opts.query = "name=" + Connect.c97809177;
            opts.timeout = 5000;
            mSocket = IO.socket("https://dooku.corvus.uberspace.de/", opts);
        } catch (URISyntaxException e) {
            Log.d("FEHLER", "mSocket nicht verbunden!");
        }
    }

    /**
     * Konstruktor
     *
     * @param c Kontext der instanziierenden Klasse.
     */
    public DrawingView(Context c) {
        super(c);
        context = c;
        mPath = new Path();
        mLivePath = new Path();
        mBitmapPaint = new Paint(Paint.DITHER_FLAG);
        circlePaint = new Paint();
        circlePath = new Path();
        circlePaint.setAntiAlias(true);
        circlePaint.setColor(Color.BLUE);
        circlePaint.setStyle(Paint.Style.STROKE);
        circlePaint.setStrokeJoin(Paint.Join.MITER);
        circlePaint.setStrokeWidth(4f);

        sPaint.setAntiAlias(true);
        sPaint.setDither(true);
        sPaint.setColor(ContextCompat.getColor(getContext(), R.color.orangePrimary));
        sPaint.setStyle(Paint.Style.STROKE);
        sPaint.setStrokeJoin(Paint.Join.ROUND);
        sPaint.setStrokeCap(Paint.Cap.ROUND);
        sPaint.setStrokeWidth(pxToDp(14));

        mLivePaint = new Paint();
        mLivePaint.setAntiAlias(true);
        mLivePaint.setColor(0xFFdf4b26);
        mLivePaint.setStyle(Paint.Style.STROKE);
        mLivePaint.setStrokeJoin(Paint.Join.MITER);
        mLivePaint.setStrokeWidth(4f);

        mLocalStrategy = LocalStrategy.getInstance();
    }

    /**
     * Leert den Inhalt der DrawingView und des LocalStrategy Objekts.
     */
    public void clearDrawingView() {
        mLocalStrategy.clearListX();
        mLocalStrategy.clearListY();
        mLocalStrategy.clearDragList();

        mBitmap.eraseColor(Color.TRANSPARENT);
        mPath.reset();
        invalidate();
    }

    /**
     * Wandelt dp in px um.
     * @param dp Der dp-Wert als int, welcher in px umgewandelt werden soll.
     * @return
     */
    public static int dpToPx(int dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }

    /**
     * Wandelt px in dp um.
     * @param px Der px-Wert als int, welcher in dp umgewandelt werden soll.
     * @return
     */
    public static int pxToDp(int px) {
        return (int) (px / Resources.getSystem().getDisplayMetrics().density);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        mBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        mCanvas = new Canvas(mBitmap);

        //Wenn es sich um eine Strategie handelt, wird die DrawingView geleert
        //und die entsprechende Strategie gezeichnet.
        if (isStrategy) {
            clearDrawingView();
            for (int i = 0; i < sDrag.length; i++) {
                if (!sDrag[i]) {
                    touchStart((float) sX[i] * w, (float) sY[i] * h);
                    invalidate();
                } else {
                    touchMove((float) sX[i] * w, (float) sY[i] * h);
                    invalidate();
                }
            }
        }
        //Wenn es sich um den Livemodus handelt wird die Server Verbindung hergestellt
        if (isLiveMode) {
            mSocket.connect();
        }
    }

    /**
     * Methode zum Zeichnen der Live Daten anderer Nutzer.
     * @param drag boolean Variable zum erkennen, ob es sich um einen Startpunkt handelt
     * @param x X-Koordinate
     * @param y Y-Koordinate
     */
    public void drawLiveContent(boolean drag, double x, double y) {
        if (!drag) {
            touchUpLive();
            touchStartLive((float) x * mCanvas.getWidth(), (float) y * mCanvas.getHeight());
            invalidate();
        } else {
            touchMoveLive((float) x * mCanvas.getWidth(), (float) y * mCanvas.getHeight());
            invalidate();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawBitmap(mBitmap, 0, 0, mBitmapPaint);
        canvas.drawPath(mPath, sPaint);
        canvas.drawPath(mLivePath, mLivePaint);
        canvas.drawPath(circlePath, circlePaint);
    }

    /**
     * Speichert die übergebenen Koordinaten normalisiert in das LocalStrategy Objekt.
     * Setzt den Startpunkt des Path Objekts und sendet, wenn es sich um den Livemodus handelt,
     * die Koordinaten an den Server.
     * @param x X-Koordinate vom Typ float.
     * @param y Y-Koordinate vom Typ float.
     */
    private void touchStart(float x, float y) {
        if ((x < mCanvas.getWidth() && x >= 0) && (y < mCanvas.getHeight() && y >= 0)) {
            mLocalStrategy.addListX((x / mCanvas.getWidth()));
            mLocalStrategy.addListY((y / mCanvas.getHeight()));
            mLocalStrategy.addDragList(false);

            mPath.moveTo(x, y);

            mX = x;
            mY = y;

            if (isLiveMode) {
                HashMap<String, Object> liveContent = new HashMap<>();
                liveContent.put("status", "liveContent");
                liveContent.put("room", MapsDetailFragment.getRoom());
                liveContent.put("user", MapsDetailFragment.getUsername());
                liveContent.put("startX", x / mCanvas.getWidth());
                liveContent.put("startY", y / mCanvas.getHeight());
                liveContent.put("x", x / mCanvas.getWidth());
                liveContent.put("y", y / mCanvas.getHeight());
                liveContent.put("drag", false);

                mSocket.emit("broadcastGroupLive", JSONCreator.createJSON("broadcastGroupLive", liveContent).toString());
            }
        }
    }

    /**
     * Speichert die übergebenen Koordinaten normalisiert in das LocalStrategy Objekt.
     * Zeichnet eine Kurve zwischen den übergebenen Koordinaten.
     * @param x X-Koordinate vom Typ float.
     * @param y Y-Koordinate vom Typ float.
     */
    private void touchMove(float x, float y) {
        if ((x < mCanvas.getWidth() && x >= 0) && (y < mCanvas.getHeight() && y >= 0)) {
            mLocalStrategy.addListX((x / mCanvas.getWidth()));
            mLocalStrategy.addListY((y / mCanvas.getHeight()));
            mLocalStrategy.addDragList(true);

            float dx = Math.abs(x - mX);
            float dy = Math.abs(y - mY);
            if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
                mPath.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2);

                if (isLiveMode) {
                    HashMap<String, Object> liveContent = new HashMap<>();
                    liveContent.put("status", "liveContent");
                    liveContent.put("room", MapsDetailFragment.getRoom());
                    liveContent.put("user", MapsDetailFragment.getUsername());
                    liveContent.put("startX", mX / mCanvas.getWidth());
                    liveContent.put("startY", mY / mCanvas.getHeight());
                    liveContent.put("x", x / mCanvas.getWidth());
                    liveContent.put("y", y / mCanvas.getHeight());
                    liveContent.put("drag", true);

                    mSocket.emit("broadcastGroupLive", JSONCreator.createJSON("broadcastGroupLive", liveContent).toString());
                }

                mX = x;
                mY = y;
            }
        }
    }

    /**
     * Speichert die Koordinaten aus den Variablen 'mX' und 'mY' in das LocalStrategy Objekt.
     * Zeichnet das Path Objekt auf das Canvas und setzt es danach zurück.
     */
    private void touchUp() {
        mLocalStrategy.addListX((mX / mCanvas.getWidth()));
        mLocalStrategy.addListY((mY / mCanvas.getHeight()));
        mLocalStrategy.addDragList(true);

        // commit the path to our offscreen
        mCanvas.drawPath(mPath, sPaint);
        // kill this so we don't double draw
        mPath.reset();
    }

    /**
     * Setzt den Startpunkt des Path Objektes auf die Koordinaten aus dem Livemodus.
     * @param x
     * @param y
     */
    private void touchStartLive(float x, float y) {
        if ((x < mCanvas.getWidth() && x >= 0) && (y < mCanvas.getHeight() && y >= 0)) {

            mLivePath.moveTo(x, y);

            mLiveX = x;
            mLiveY = y;

        }
    }

    /**
     * Zeichnet eine Kurve zwischen den übergebenen Koordinaten aus dem Livemodus.
     * @param x X-Koordinate vom Typ float.
     * @param y Y-Koordinate vom Typ float.
     */
    private void touchMoveLive(float x, float y) {
        if ((x < mCanvas.getWidth() && x >= 0) && (y < mCanvas.getHeight() && y >= 0)) {

            float dx = Math.abs(x - mLiveX);
            float dy = Math.abs(y - mLiveY);
            if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
                mLivePath.quadTo(mLiveX, mLiveY, (x + mLiveX) / 2, (y + mLiveY) / 2);

                mLiveX = x;
                mLiveY = y;
            }
        }
    }

    /**
     * Zeichnet das Path Objekt auf das Canvas und setzt es danach zurück.
     */
    private void touchUpLive() {
        mCanvas.drawPath(mLivePath, mLivePaint);
        mLivePath.reset();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                touchStart(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                touchMove(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                touchUp();
                invalidate();
                break;
        }
        return true;
    }

    /**
     * Trennt die Socket Verbindung.
     */
    public void closeSocket() {
        mSocket.disconnect();
    }
}

