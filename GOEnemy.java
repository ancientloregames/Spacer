package com.ancientlore.alspacer;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Rect;

import java.util.Random;

class GOEnemy extends GameObject{
    private int speed = 1;
    private Rect hitBox;

    private int minY, maxY;
    private int minX, maxX;

    public GOEnemy(Context context, int screenX, int screenY){
        setX(50); setY(50);
        speed = 1;
        Random rand = new Random();
        int whichBitmap = rand.nextInt(3);
        switch (whichBitmap) {
            case 0:
                setBitmap(BitmapFactory.decodeResource
                        (context.getResources(), R.drawable.spacer_ship_enemy_1));
                break;
            case 1:
                setBitmap(BitmapFactory.decodeResource
                        (context.getResources(), R.drawable.spacer_ship_enemy_2));
                break;
            case 2:
                setBitmap(BitmapFactory.decodeResource
                        (context.getResources(), R.drawable.spacer_ship_enemy_3));
                break;
        }
        scaleBitmap(screenY);
        hitBox = new Rect(getX(), getY(), getBitmap().getWidth(), getBitmap().getHeight());

        minY = 0;
        maxY = screenY;
        minX = 0;
        maxX = screenX;

        speed = rand.nextInt(6)+10;
        setX(screenX);
        setY(rand.nextInt(maxY) - getBitmap().getHeight());
    }

    public void update(int playerSpeed){
        setX(getX()- playerSpeed);
        setX(getX()- speed);

        if(getX() < minX-getBitmap().getWidth()){
            Random rand = new Random();
            speed = rand.nextInt(10)+10;
            setX(maxX);
            setY(rand.nextInt(maxY)-getBitmap().getHeight());
        }

        hitBox.left = getX();
        hitBox.top = getY();
        hitBox.right = getX() + getBitmap().getWidth();
        hitBox.bottom = getY() + getBitmap().getHeight();
    }
    public Rect getHitBox(){
        return hitBox;
    }
}
