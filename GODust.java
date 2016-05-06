package com.ancientlore.alspacer;

import java.util.Random;

class GODust {
    private int x,y;
    private int speed;

    private int minY, maxY;
    private int minX, maxX;

    public GODust(int screenX, int screenY){
        minX = 0; maxX = screenX;
        minY = 0; maxY = screenY;

        Random rand = new Random();
        speed = rand.nextInt(10);
        x = rand.nextInt(maxX);
        y = rand.nextInt(maxY);
    }
    public void update(int playerSpeed){
        x -= playerSpeed;
        x -= speed;

        if (x < 0){
            x = maxX;
            Random rand = new Random();
            y = rand.nextInt(maxY);
            speed = rand.nextInt(15);
        }
    }
    public  int getX(){ return x; }
    public  int getY(){ return y; }
}
