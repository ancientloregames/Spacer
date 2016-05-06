package com.ancientlore.alspacer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;

class GOPlayer extends GameObject{
    private int speed;
    private boolean boosting;
    private int shieldStrength;
    private Rect hitBox;

    private int GRAVITY;
    private int MIN_SPEED;
    private int MAX_SPEED;
    private final int STANDARD_SHIELD = 10;
    private int minY, maxY;

    public GOPlayer(final Context context, final int screenX, final int screenY){
        setX(50); setY(screenY/2);
        GRAVITY = -screenY/35;
        MIN_SPEED = screenX/144;
        MAX_SPEED = screenX/24;
        speed = MIN_SPEED;
        setBitmap(BitmapFactory.decodeResource
                (context.getResources(),R.drawable.spacer_ship_player));
        scaleBitmap(screenY);
        hitBox = new Rect(getX(),getY(),getBitmap().getWidth(),getBitmap().getHeight());

        minY = 0;
        maxY = screenY - getBitmap().getHeight();

        shieldStrength = STANDARD_SHIELD;
    }

    public void update(){
        if (boosting)   speed += 2;
        else speed -=5;

        if (speed < MIN_SPEED)  speed = MIN_SPEED;
        if (speed > MAX_SPEED)  speed = MAX_SPEED;

        setY(getY() - speed - GRAVITY);

        if (getY() < minY)   setY(minY);
        if (getY() > maxY)   setY(maxY);

        hitBox.left = getX();
        hitBox.top = getY();
        hitBox.right = getX() + getBitmap().getWidth();
        hitBox.bottom = getY() + getBitmap().getHeight();
    }

    public void startBoosting(){ boosting = true; }
    public void stopBoosting(){ boosting = false; }
    public void reduceShieldStrength(){ shieldStrength--; }
    public void regainShieldStrength(){ shieldStrength = STANDARD_SHIELD; }

    public int getSpeed(){
        return speed;
    }
    public Rect getHitBox(){
        return hitBox;
    }
    public int getShieldStrength(){ return shieldStrength; }
}
