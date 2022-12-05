package com.example.savethebunny;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Handler;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;

import androidx.core.content.res.ResourcesCompat;

import java.util.ArrayList;
import java.util.Random;


public class GameView extends View {

    Bitmap background, ground, rabbit;
    Rect rectBackground, rectGround;
    Context context;
    Handler handler;
    final long UPDATE_MILLIS = 30;
    Runnable runnable;
    Paint textPaint = new Paint();
    Paint healthPaint = new Paint();
    float TEXT_SIZE = 120;
    int points = 0;
    int life = 3;
    static  int dWidth, dHeight;
    Random random;
    float rabbitX, rabbitY;
    float oldX;
    float oldRabbitX;
    ArrayList<Spike> spikes;

    public GameView(Context context) {
        super(context);
        this.context = context;
        background = BitmapFactory.decodeResource(getResources(), R.drawable.background);
        ground = BitmapFactory.decodeResource(getResources(), R.drawable.sand_mid);
        rabbit = BitmapFactory.decodeResource(getResources(), R.drawable.rabbit);
        Display display = ((Activity) getContext()).getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        dWidth = size.x;
        dHeight = size.y;
        rectBackground = new Rect(0,0, dWidth, dHeight);
        rectGround = new Rect(0, dHeight - ground.getHeight(), dWidth, dHeight);
        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                invalidate();
            }
        };

        textPaint.setColor(Color.rgb(255, 165, 0));
        textPaint.setTextSize(TEXT_SIZE);
        textPaint.setTextAlign(Paint.Align.LEFT);
        textPaint.setTypeface(ResourcesCompat.getFont(context, R.font.kenney_blocks));
        healthPaint.setColor(Color.GREEN);
        random = new Random();
        rabbitX = dWidth / 2 - rabbit.getWidth() / 2;
        rabbitY = dHeight - ground.getHeight() - rabbit.getHeight();
        spikes = new ArrayList<>();

        for (int i = 0; i < 3; i++) {
            Spike spike = new Spike(context);
            spikes.add(spike);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawBitmap(background, null, rectBackground, null);
        canvas.drawBitmap(ground, null, rectGround, null);
        canvas.drawBitmap(rabbit, rabbitX, rabbitY, null);

        for (int i = 0; i < spikes.size(); i++) {
            canvas.drawBitmap(spikes.get(i).getSpike(spikes.get(i).spikeFrame), spikes.get(i).spikeX, spikes.get(i).spikeY, null);
            spikes.get(i).spikeFrame++;

            if (spikes.get(i).spikeFrame > 2) {
                spikes.get(i).spikeFrame = 0;
            }

            spikes.get(i).spikeY += spikes.get(i).spikeVelocity;

            if (spikes.get(i).spikeY + spikes.get(i).getSpikeHeight() >= dHeight - ground.getHeight()) {
               points += 10;
               spikes.get(i).resetPosition();
            }
        }

        for (int i = 0; i < spikes.size(); i++) {
            if (spikes.get(i).spikeX + spikes.get(i).getSpikeWidth() >= rabbitX
                    && spikes.get(i).spikeX <= rabbitX + rabbit.getWidth()
                    && spikes.get(i).spikeY + spikes.get(i).getSpikeWidth() >= rabbitY
                    && spikes.get(i).spikeY + spikes.get(i).getSpikeWidth() <= rabbitY + rabbit.getHeight()) {
                life--;
                spikes.get(i).resetPosition();

                if (life == 0) {
                    Intent intent = new Intent(context, GameOver.class);
                    intent.putExtra("points", points);
                    context.startActivity(intent);
                    ((Activity) context).finish();
                }
            }
        }

        if (life == 2) {
            healthPaint.setColor(Color.YELLOW);
        } else if (life == 1) {
            healthPaint.setColor(Color.RED);
        }

        canvas.drawRect(dWidth - 200, 30, dWidth - 200 + 60 * life, 80, healthPaint);
        canvas.drawText("" + points, 20, TEXT_SIZE, textPaint);
        handler.postDelayed(runnable, UPDATE_MILLIS);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float touchX = event.getX();
        float touchY = event.getY();

        if (touchY >= rabbitY) {
            int action = event.getAction();

            if (action == MotionEvent.ACTION_DOWN) {
                oldX = event.getX();
                oldRabbitX = rabbitX;
            }

            if (action == MotionEvent.ACTION_MOVE) {
                float shift = oldX - touchX;
                float newRabbitX = oldRabbitX - touchX;
                if (newRabbitX <= 0) {
                    rabbitX = 0;
                } else if (newRabbitX >= dWidth - rabbit.getWidth()) {
                    rabbitX = dWidth - rabbit.getWidth();
                } else {
                    rabbitX = newRabbitX;
                }
            }
        }

        return true;
    }
}
