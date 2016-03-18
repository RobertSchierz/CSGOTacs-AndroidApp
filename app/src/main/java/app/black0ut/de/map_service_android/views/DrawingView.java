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
import app.black0ut.de.map_service_android.data.LocalStrategy;
import app.black0ut.de.map_service_android.fragments.MapsDetailFragment;
import app.black0ut.de.map_service_android.jsoncreator.JSONCreator;
import io.socket.client.IO;
import io.socket.client.Socket;

/**
 * Created by Jan-Philipp Altenhof on 08.01.2016.
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
    private boolean lastDrag;

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
            mSocket = IO.socket("https://p4dme.shaula.uberspace.de/");
            //mSocket = IO.socket("http://chat.socket.io");
        } catch (URISyntaxException e) {
            Log.d("FEHLER", "mSocket nicht verbunden!");
        }
    }

    /**
     * Konstruktor
     *
     * @param c Kontext der instanziierenden Klasse
     */
    public DrawingView(Context c) {
        super(c);
        context = c;
        mPath = new Path();
        mLivePath = new Path();
        lastDrag = false;
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

    public void clearDrawingView() {
        mLocalStrategy.clearListX();
        mLocalStrategy.clearListY();
        mLocalStrategy.clearDragList();

        mBitmap.eraseColor(Color.TRANSPARENT);
        mPath.reset();
        invalidate();
    }

    public static int dpToPx(int dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }

    public static int pxToDp(int px) {
        return (int) (px / Resources.getSystem().getDisplayMetrics().density);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        mBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        mCanvas = new Canvas(mBitmap);

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
        if (isLiveMode) {
            mSocket.connect();
        }
    }

    public void drawLiveContent(boolean drag, double x, double y, double startX, double startY) {
        Log.d("liveContent", "------- drag: " + drag + " x: " + x + " y: " + y + " startX: " + startX + " startY: " + startY);

        if (!drag) {
            touchStartLive((float) x * mCanvas.getWidth(), (float) y * mCanvas.getHeight(),
                    (float) startX * mCanvas.getWidth(), (float) startY * mCanvas.getHeight());
            invalidate();
        } else if (lastDrag == true && drag == false) {
            touchUpLive();
        } else {
            touchMoveLive((float) x * mCanvas.getWidth(), (float) y * mCanvas.getHeight(),
                    (float) startX * mCanvas.getWidth(), (float) startY * mCanvas.getHeight());
            invalidate();
        }
        lastDrag = drag;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawBitmap(mBitmap, 0, 0, mBitmapPaint);
        canvas.drawPath(mPath, sPaint);
        canvas.drawPath(mLivePath, mLivePaint);
        canvas.drawPath(circlePath, circlePaint);
    }

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

    private void touchUp() {
        mLocalStrategy.addListX((mX / mCanvas.getWidth()));
        mLocalStrategy.addListY((mY / mCanvas.getHeight()));
        mLocalStrategy.addDragList(true);

        mPath.lineTo(mX, mY);
        circlePath.reset();
        // commit the path to our offscreen
        mCanvas.drawPath(mPath, sPaint);
        // kill this so we don't double draw
        mPath.reset();
    }

    //Live Drawing
    private void touchStartLive(float x, float y, float startY, float startX) {
        if ((x < mCanvas.getWidth() && x >= 0) && (y < mCanvas.getHeight() && y >= 0)) {

            mLivePath.moveTo(x, y);
            mLivePath.lineTo(x, y);

            mLiveX = x;
            mLiveY = y;

        }
    }

    private void touchMoveLive(float x, float y, float startX, float startY) {
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

    private void touchUpLive() {
        mPath.lineTo(mLiveX, mLiveY);
        circlePath.reset();
        // commit the path to our offscreen
        mCanvas.drawPath(mLivePath, mLivePaint);
        // kill this so we don't double draw
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

    public void closeSocket() {
        mSocket.disconnect();
    }
}

