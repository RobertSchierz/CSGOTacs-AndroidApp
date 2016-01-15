package app.black0ut.de.map_service_android;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import org.androidannotations.annotations.EView;
import org.androidannotations.annotations.ViewById;

import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;

/**
 * Created by Jan-Philipp Altenhof on 08.01.2016.
 */

public class DrawingView extends View {

    //DrawingView dv ;
    public static Paint mPaint = new Paint();

    public int width;
    public  int height;

    private Bitmap mBitmap;
    private Canvas mCanvas;
    private Path mPath;
    private Paint mBitmapPaint;
    Context context;
    private Paint circlePaint;
    private Path circlePath;

    private Socket mSocket;
    {
        try {
            mSocket = IO.socket("https://p4dme.shaula.uberspace.de");
        } catch (URISyntaxException e) {}
    }

    //TODO
    //Testnachricht senden
    private void attemptSend(String coords) {
        //String json = "{x: 666, y: 69}";
        if (TextUtils.isEmpty(coords)) {
            return;
        }
        mSocket.emit("json", coords);
    }

    /**
     * Konstruktor
     * @param c Kontext der instanziierenden Klasse
     */
    public DrawingView(Context c) {
        super(c);
        context=c;
        mPath = new Path();
        mBitmapPaint = new Paint(Paint.DITHER_FLAG);
        circlePaint = new Paint();
        circlePath = new Path();
        circlePaint.setAntiAlias(true);
        circlePaint.setColor(Color.BLUE);
        circlePaint.setStyle(Paint.Style.STROKE);
        circlePaint.setStrokeJoin(Paint.Join.MITER);
        circlePaint.setStrokeWidth(4f);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        mBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        mCanvas = new Canvas(mBitmap);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawBitmap( mBitmap, 0, 0, mBitmapPaint);
        canvas.drawPath( mPath,  mPaint);
        canvas.drawPath( circlePath,  circlePaint);
    }

    private float mX, mY;
    private static final float TOUCH_TOLERANCE = 4;

    private void touch_start(float x, float y) {
        mPath.reset();
        mPath.moveTo(x, y);
        Log.d("TEST", "touch_start: x " + x + " y " + y);
        mX = x;
        mY = y;

        String startCoords = "{x: " + x + ", y: " + y + "}";

        //Socket verbinden
        mSocket.connect();

        //Methode zum Nachrichten senden aufrufen
        attemptSend(startCoords);
    }

    private void touch_move(float x, float y) {
        if(x > mCanvas.getWidth() || x < 0) {
            mCanvas.drawPath(mPath, mPaint);
            return;
        }else if(y > mCanvas.getHeight() || y < 0){
            mCanvas.drawPath(mPath,  mPaint);
            return;
        }

        float dx = Math.abs(x - mX);
        float dy = Math.abs(y - mY);
        if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
            mPath.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2);
            Log.d("TEST", "touch_move: x " + x + " y " + y);
            mX = x;
            mY = y;

            //circlePath.reset();
            //circlePath.addCircle(mX, mY, 30, Path.Direction.CW);
        }
    }

    private void touch_up() {
        mPath.lineTo(mX, mY);
        circlePath.reset();
        // commit the path to our offscreen
        mCanvas.drawPath(mPath,  mPaint);
        // kill this so we don't double draw
        mPath.reset();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

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
}

