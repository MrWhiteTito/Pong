package com.mygdx.game;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;

public class Ball {
    float x,y,dx,dy,width,height;
    float VW = 1280;
    float VH = 720;
    public Ball(float x,float y,float width, float height){
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.dx = 0;
        this.dy = 0;
    }

    public boolean collides(Paddle paddle){
        if (x > paddle.x + paddle.width || paddle.x > x + width) return false;
        if(y > paddle.y + paddle.height || paddle.y > y + width) return false;
        return true;
    }

    public void reset(){
        this.x = VW / 2 -2;
        this.y = VH / 2 -2;
        this.dx = MathUtils.random(-5,5);
        this.dy = MathUtils.random(-5,5);
    }

    public void update(){
        x+=dx;
        y+=dy;
    }

    public void render(ShapeRenderer renderer){
        renderer.rect(x,y,width,height);
    }

}
