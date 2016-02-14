package app.black0ut.de.map_service_android;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;

import app.black0ut.de.map_service_android.data.LocalStrategy;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

/**
 * Created by Jan-Philipp Altenhof on 08.01.2016.
 */

public class DrawingView extends View {

    //DrawingView dv ;
    public static Paint sPaint = new Paint();
    public static boolean isStrategy = false;
    public static boolean[] sDrag;
    public static double[] sX;
    public static double[] sY;

    public int width;
    public int height;

    public LocalStrategy localStrategy;

    private Bitmap mBitmap;
    public Canvas mCanvas;
    public Path mPath;
    private Paint mBitmapPaint;
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
        mBitmapPaint = new Paint(Paint.DITHER_FLAG);
        circlePaint = new Paint();
        circlePath = new Path();
        circlePaint.setAntiAlias(true);
        circlePaint.setColor(Color.BLUE);
        circlePaint.setStyle(Paint.Style.STROKE);
        circlePaint.setStrokeJoin(Paint.Join.MITER);
        circlePaint.setStrokeWidth(4f);

        localStrategy = LocalStrategy.getInstance();

        mSocket.on("json", json);
        mSocket.connect();
        mSocket.emit("appTest");
    }

    public void clearDrawingView() {
        localStrategy.clearListX();
        localStrategy.clearListY();
        localStrategy.clearDragList();

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
                    touch_start((float) sX[i] * w, (float) sY[i] * h);
                    Log.d("TEST", "onSizeChanged start");
                    invalidate();
                } else {
                    touch_move((float) sX[i] * w, (float) sY[i] * h);
                    Log.d("TEST", "onSizeChanged move");
                    invalidate();
                }
            }
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawBitmap(mBitmap, 0, 0, mBitmapPaint);
        canvas.drawPath(mPath, sPaint);
        canvas.drawPath(circlePath, circlePaint);
    }

    private float mX, mY;
    private static final float TOUCH_TOLERANCE = 4;

    private void touch_start(float x, float y) {
        if ((x < mCanvas.getWidth() && x >= 0) && (y < mCanvas.getHeight() && y >= 0)) {
            localStrategy.addListX((x / mCanvas.getWidth()));
            localStrategy.addListY((y / mCanvas.getHeight()));
            localStrategy.addDragList(false);
        }

        mPath.moveTo(x, y);
        Log.d("TEST", "touch_start: x " + x + " y " + y);
        mX = x;
        mY = y;

        String startCoords = "{x: " + x + ", y: " + y + ", startx: " + x + ", starty: " + y + "}";
        //Methode zum Koordinaten senden aufrufen
        attemptSend(startCoords);
    }

    private void touch_move(float x, float y) {
        if (x > mCanvas.getWidth() || x < 0) {
            mCanvas.drawPath(mPath, sPaint);
            return;
        } else if (y > mCanvas.getHeight() || y < 0) {
            mCanvas.drawPath(mPath, sPaint);
            return;
        }

        if ((x < mCanvas.getWidth() && x >= 0) && (y < mCanvas.getHeight() && y >= 0)) {
            localStrategy.addListX((x / mCanvas.getWidth()));
            localStrategy.addListY((y / mCanvas.getHeight()));
            localStrategy.addDragList(true);
        }

        float dx = Math.abs(x - mX);
        float dy = Math.abs(y - mY);
        if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
            mPath.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2);
            Log.d("TEST", "touch_move: x " + x + " y " + y);

            String moveCoords = "{x: " + x + ", y: " + y + ", startx: " + mX + ", starty: " + mY + "}";
            attemptSend(moveCoords);

            mX = x;
            mY = y;

            //String moveCoords = "{x: " + x + ", y: " + y + "}";

            //circlePath.reset();
            //circlePath.addCircle(mX, mY, 30, Path.Direction.CW);
        }
    }

    private void touch_up() {
        if ((mX < mCanvas.getWidth() && mX >= 0) && (mY < mCanvas.getHeight() && mY >= 0)) {
            localStrategy.addListX((mX / mCanvas.getWidth()));
            localStrategy.addListY((mY / mCanvas.getHeight()));
            localStrategy.addDragList(true);
        }
        mPath.lineTo(mX, mY);
        circlePath.reset();
        // commit the path to our offscreen
        mCanvas.drawPath(mPath, sPaint);
        // kill this so we don't double draw
        mPath.reset();
        String upCoords = "{x: " + mX + ", y: " + mY + "}";
        //attemptSend(upCoords);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        Log.d("TEST", "onTouchEvent x: " + x);
        Log.d("TEST", "onTouchEvent y: " + y);

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                touch_start(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                touch_move(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                touch_up();
                invalidate();
                break;
        }
        return true;
    }

    //TODO
    //Testnachricht senden
    private void attemptSend(String json) {
        if (TextUtils.isEmpty(json)) {
            return;
        }

        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject(json);
            //Log.d("TEST", "attemptSend: " + jsonObject.getDouble("x") + " y: " + jsonObject.getDouble("y"));
        } catch (JSONException e) {
            Log.d("TEST", "JSONObject Failed");
            return;
        }
        mSocket.emit("json", jsonObject);
    }

    private Emitter.Listener json = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            Activity activity = (Activity) context;
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    float x;
                    float y;
                    float startx;
                    float starty;
                    try {
                        x = (float) data.getDouble("x");
                        y = (float) data.getDouble("y");
                        startx = (float) data.getDouble("startx");
                        starty = (float) data.getDouble("starty");
                        Log.d("TEST xy", "x: " + x + " y: " + y);
                    } catch (JSONException e) {
                        Log.d("TEST", "Fehler beim Auslesen der Daten des JSONs");
                        return;
                    }

                    // add the message to view
                    //addCoords(x, y);
                    //mPath.quadTo(x, y, (x) / 2, (y) / 2);
                    mPath.moveTo(startx, starty);
                    //TODO eventuell noch quadTo draus machen
                    mPath.lineTo(x, y);
                    mCanvas.drawPath(mPath, sPaint);
                    invalidate();
                    mPath.reset();


                }
            });
        }
    };
}

