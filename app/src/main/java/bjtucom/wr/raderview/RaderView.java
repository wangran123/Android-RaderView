package bjtucom.wr.raderview;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.os.Build;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;


/**
 * Created by wr on 4/10/2015.
 */
public class RaderView extends View {
    private int mRadius;
    private Canvas mCanvas;

    private final int solidLineCount = 3;
    private final int dashLineCount = 8;
    private final int lightLineCount = 8;

    private float solidLineWidth = 5.0f;
    private float dashLineWidth = 2.0f;
    private float lightLineWidth = 0.8f;

    private int startAngle = 0;
    private int mWidth, mHeight;
    int green = getResources().getColor(R.color.green);
    private int blipNum = 0;
    private Handler handler=new Handler();
    private boolean isRun = true;
    Thread waveThread;
    public RaderView(Context context) {
        super(context);
        ;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawRangeRings(canvas);
        drawWave(canvas);

    }

    public RaderView(Context context, AttributeSet attrs) {
        super(context, attrs);

    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public RaderView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

    }

    public RaderView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

    }

    protected void init() {
        mWidth = this.getWidth();
        mHeight = this.getHeight();
        mRadius = mWidth > mHeight ? mHeight / 2 : mWidth / 2;
    }

    public int getBlipNum() {
        return blipNum;
    }

    public void setBlipNum(int blipNum) {
        this.blipNum = blipNum;
    }

    private Paint createSolidPaint() {
        Paint p = new Paint(Paint.DITHER_FLAG);
        p.setStrokeWidth(solidLineWidth);
        p.setStyle(Paint.Style.STROKE);
        p.setColor(green);
        return p;
    }

    private Paint createDashPaint() {
        Paint p = new Paint(Paint.DITHER_FLAG);
        p.setStrokeWidth(dashLineWidth);
        p.setStyle(Paint.Style.STROKE);
        p.setColor(green);
        PathEffect pathEffect = new DashPathEffect(new float[]{mRadius / 20, mRadius / 20}, 1);
        p.setPathEffect(pathEffect);
        return p;
    }

    private Paint createLightPaint() {
        int lightgreen = getResources().getColor(R.color.lightgreen);
        Paint p = new Paint(Paint.DITHER_FLAG);
        p.setStrokeWidth(lightLineWidth);
        p.setStyle(Paint.Style.STROKE);
        p.setColor(green);
        return p;
    }

    private Paint createFillPaint() {
        int black = getResources().getColor(R.color.black);
        Paint p = new Paint(Paint.DITHER_FLAG);
        p.setStyle(Paint.Style.FILL);
        p.setColor(black);
        return p;
    }

    private Paint createWavePaint() {
        Paint p = new Paint(Paint.DITHER_FLAG);
        Shader linearShader = new LinearGradient(0, 0, mWidth, mHeight, Color.GREEN, Color.GRAY, Shader.TileMode.REPEAT);
        p.setShader(linearShader);
        p.setAlpha(60);
        p.setStrokeWidth(solidLineWidth);
        return p;
    }

    private void drawRangeRings(Canvas canvas) {
        int i = 0;
        int radiusSpace = mRadius / 3;
        Path solidPath = new Path();
        canvas.drawCircle(mHeight / 2, mWidth / 2, mRadius - 1, createFillPaint());
        //draw solid circle
        for (i = solidLineCount; i > 0; i--) {
            solidPath.addCircle(mWidth / 2, mHeight / 2, mRadius - (3 - i) * radiusSpace, Path.Direction.CCW);
        }
        canvas.drawPath(solidPath, createSolidPaint());

        //draw dash line
        canvas.save();
        Path dashPath = new Path();
        for (i = 0; i < dashLineCount; i++) {
            dashPath.moveTo(mWidth / 2, mHeight / 2);
            dashPath.lineTo(mWidth / 2, 0);
            canvas.drawPath(dashPath, createDashPaint());
            canvas.rotate(360 / dashLineCount, mWidth / 2, mHeight / 2);
        }
        canvas.rotate(360 / lightLineCount / 2, mWidth / 2, mHeight / 2);
        Path lightPath = new Path();
        //draw light line
        for (i = 0; i < lightLineCount; i++) {
            canvas.rotate(360 / lightLineCount, mWidth / 2, mHeight / 2);
            lightPath.moveTo(mWidth / 2, mHeight / 2);
            lightPath.lineTo(mWidth / 2, 0);
            canvas.drawPath(lightPath, createLightPaint());
        }
        canvas.restore();
        //draw light circle
        canvas.drawCircle(mHeight / 2, mWidth / 2, mRadius - mRadius / 8, createLightPaint());

    }

    private void drawWave(Canvas canvas) {
        RectF r = new RectF(0, 0, mWidth, mHeight);
        canvas.drawArc(r, startAngle, 60, true, createWavePaint());
    }

    public void shutdownWave() {
        isRun = false;
    }

    public void startWave(){
        waveThread = new WaveThread();
        isRun = true;
        waveThread.start();
    }
    private class WaveThread extends Thread {
        @Override
        public void run() {
            while (isRun) {
                startAngle += 5;
                try {
                    this.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        invalidate();
                    }
                });
            }
        }
    }
}
