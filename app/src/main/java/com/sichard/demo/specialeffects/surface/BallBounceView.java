package com.sichard.demo.specialeffects.surface;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.sichard.demo.R;

/**
 * <br>类描述：小球跳动View
 * <br>详细描述：
 * <br><b>Author sichard</b>
 * <br><b>Date 2018-9-15</b>
 */
public class BallBounceView extends SurfaceView implements SurfaceHolder.Callback {
    BallBounceView.GameThread thread;
    //Device's screen width.
    int screenW;
    //Devices's screen height.
    int screenH;
    //Ball x position.
    int ballX;
    //Ball y position.
    int ballY;
    int initialY;
    //Ball vertical speed.
    float dY;
    int ballW;
    int ballH;
    int bgrW;
    int bgrH;
    int angle;
    int bgrScroll;
    //Background scroll speed.
    int dBgrY;
    float acc;
    Bitmap ball, bgr, bgrReverse;
    boolean reverseBackroundFirst;
    boolean ballFingerMove;

    //Measure frames per second.
    long now;
    int framesCount = 0;
    int framesCountAvg = 0;
    long framesTimer = 0;
    Paint fpsPaint = new Paint();

    //Frame speed
    long timeNow;
    long timePrevFrame = 0;
    long timeDelta;
    private Rect mFromRect1 = new Rect();
    private Rect mToRect1 = new Rect();
    private Rect mFromRect2 = new Rect();
    private Rect mToRect2 = new Rect();


    public BallBounceView(Context context) {
        super(context);
        //Load a ball image.
        ball = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_surface_football);
        //Load a background.
        bgr = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_surface_sky);
        ballW = ball.getWidth();
        ballH = ball.getHeight();

        //Create a flag for the onDraw method to alternate background with its mirror image.
        reverseBackroundFirst = false;

        //Initialise animation variables.
        //Acceleration
        acc = 0.2f;
        //vertical speed
        dY = 0;
        //Initial vertical position
        initialY = 100;
        //Start value for the rotation angle
        angle = 0;
        //Background scroll position
        bgrScroll = 0;
        //Scrolling background speed
        dBgrY = 1;

        fpsPaint.setTextSize(30);

        //Set thread
        getHolder().addCallback(this);

        setFocusable(true);
    }

    @Override
    public void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        //This event-method provides the real dimensions of this custom view.
        screenW = w;
        screenH = h;

        //Scale background to fit the screen.
        bgr = Bitmap.createScaledBitmap(bgr, w, h, true);
        bgrW = bgr.getWidth();
        bgrH = bgr.getHeight();

        //Create a mirror image of the background (horizontal flip) - for a more circular background.
        //Like a frame or mould for an image.
        Matrix matrix = new Matrix();
        //Horizontal mirror effect.
        matrix.setScale(-1, 1);
        //Create a new mirrored bitmap by applying the matrix.
        bgrReverse = Bitmap.createBitmap(bgr, 0, 0, bgrW, bgrH, matrix, true);

        //Centre ball X into the centre of the screen.
        ballX = screenW / 2 - (ballW / 2);
        //Centre ball height above the screen.
        ballY = -50;
    }

    //***************************************
    //*************  TOUCH  *****************
    //***************************************
    @SuppressLint("ClickableViewAccessibility")
    @Override
    public synchronized boolean onTouchEvent(MotionEvent ev) {

        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                ballX = (int) ev.getX() - ballW / 2;
                ballY = (int) ev.getY() - ballH / 2;

                ballFingerMove = true;
                break;
            }

            case MotionEvent.ACTION_MOVE: {
                ballX = (int) ev.getX() - ballW / 2;
                ballY = (int) ev.getY() - ballH / 2;

                break;
            }

            case MotionEvent.ACTION_UP:
                ballFingerMove = false;
                dY = 0;
                break;

            default:
                break;
        }
        return true;
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (canvas == null) {
            return;
        }
        //Draw scrolling background.
        mFromRect1.set(0, 0, bgrW - bgrScroll, bgrH);
        mToRect1.set(bgrScroll, 0, bgrW, bgrH);

        mFromRect2.set(bgrW - bgrScroll, 0, bgrW, bgrH);
        mToRect2.set(0, 0, bgrScroll, bgrH);

        if (!reverseBackroundFirst) {
            canvas.drawBitmap(bgr, mFromRect1, mToRect1, null);
            canvas.drawBitmap(bgrReverse, mFromRect2, mToRect2, null);
        } else {
            canvas.drawBitmap(bgr, mFromRect2, mToRect2, null);
            canvas.drawBitmap(bgrReverse, mFromRect1, mToRect1, null);
        }

        //Next value for the background's position.
        if ((bgrScroll += dBgrY) >= bgrW) {
            bgrScroll = 0;
            reverseBackroundFirst = !reverseBackroundFirst;
        }

        //Compute roughly the ball's speed and location.
        if (!ballFingerMove) {
            ballY += (int) dY; //Increase or decrease vertical position.
            if (ballY > (screenH - ballH)) {
                dY = (-1) * dY; //Reverse speed when bottom hit.
            }
            dY += acc; //Increase or decrease speed.
        }

        //Increase rotating angle
        if (angle++ > 360) {
            angle = 0;
        }

        //DRAW BALL
        //Rotate method one
        /*
        Matrix matrix = new Matrix();
        matrix.postRotate(angle, (ballW / 2), (ballH / 2)); //Rotate it.
        matrix.postTranslate(ballX, ballY); //Move it into x, y position.
        canvas.drawBitmap(ball, matrix, null); //Draw the ball with applied matrix.

        */// Rotate method two

        canvas.save(); //Save the position of the canvas matrix.
        canvas.rotate(angle, ballX + (ballW / 2), ballY + (ballH / 2)); //Rotate the canvas matrix.
        canvas.drawBitmap(ball, ballX, ballY, null); //Draw the ball by applying the canvas rotated matrix.
        canvas.restore(); //Rotate the canvas matrix back to its saved position - only the ball bitmap was rotated not all canvas.

        //*/

        //Measure frame rate (unit: frames per second).
        now = System.currentTimeMillis();
        canvas.drawText(framesCountAvg + " fps", 40, 70, fpsPaint);
        framesCount++;
        if (now - framesTimer > 1000) {
            framesTimer = now;
            framesCountAvg = framesCount;
            framesCount = 0;
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        thread = new BallBounceView.GameThread(getHolder(), this);
        thread.setRunning(true);
        thread.start();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        boolean retry = true;
        thread.setRunning(false);
        while (retry) {
            try {
                thread.join();
                retry = false;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


    class GameThread extends Thread {
        private SurfaceHolder surfaceHolder;
        private BallBounceView gameView;
        private boolean run = false;

        GameThread(SurfaceHolder surfaceHolder, BallBounceView gameView) {
            this.surfaceHolder = surfaceHolder;
            this.gameView = gameView;
        }

        void setRunning(boolean run) {
            this.run = run;
        }

        public SurfaceHolder getSurfaceHolder() {
            return surfaceHolder;
        }

        @Override
        public void run() {
            Canvas canvas;
            while (run) {
                canvas = null;

                //limit frame rate to max 60fps
                timeNow = System.currentTimeMillis();
                timeDelta = timeNow - timePrevFrame;
                if (timeDelta < 16) {
                    try {
                        Thread.sleep(16 - timeDelta);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                timePrevFrame = System.currentTimeMillis();

                try {
                    canvas = surfaceHolder.lockCanvas(null);
                    synchronized (surfaceHolder) {
                        //call methods to draw and process next fame
                        gameView.onDraw(canvas);
                    }
                } finally {
                    if (canvas != null) {
                        surfaceHolder.unlockCanvasAndPost(canvas);
                    }
                }
            }
        }
    }
}
