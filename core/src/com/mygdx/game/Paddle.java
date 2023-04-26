package com.mygdx.game;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;

public class Paddle {
    float x,y,width,height,dy,speed;
    float VH = 720;
    public Paddle(float x,float y,float width, float height ){
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.dy = 0;
    }

    public void update(){
        y+= dy;
        if(y < 0) y = 0;
        else if(y > VH ) y = VH;
    }

    public void render(ShapeRenderer renderer){
        renderer.rect(x,y,width,height);
    }
}
