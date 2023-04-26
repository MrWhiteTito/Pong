package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;

public class PongGame extends ApplicationAdapter {
	float VW,VH;
	final float PADDLE_SPEED = 4;
	int scoreOne,scoreTwo;
	float player1Y,player2Y;
	float ballX,ballY;
	float ballDX,ballDY;
	Paddle p1,p2;
	Ball ball;
	String state = "start";
	SpriteBatch batch;
	ShapeRenderer shape;
	BitmapFont font;
	GlyphLayout layout;
	Sound paddleHit,score,wallHit;
	boolean won;

	@Override
	public void create () {
		batch = new SpriteBatch();
		FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("font.ttf"));
		FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
		parameter.size = 32;
		font = generator.generateFont(parameter);
		layout = new GlyphLayout();
		generator.dispose();
		VW = Gdx.graphics.getWidth();
		VH = Gdx.graphics.getHeight();
		shape = new ShapeRenderer();
		scoreOne = 0;
		scoreTwo = 0;
		player1Y = VH-30;
		player2Y = 50;
		ballX = VW/2 -2;
		ballY = VH/2 -2;
		ballDX = MathUtils.random(5);
		ballDY = MathUtils.random(5);
		p1 = new Paddle(10,player1Y,5,20);
		p2 = new Paddle(VW-10,player2Y,5,20);
		ball = new Ball(ballX,ballY,8,8);
		paddleHit = Gdx.audio.newSound(Gdx.files.internal("paddle_hit.wav"));
		score = Gdx.audio.newSound(Gdx.files.internal("score.wav"));
		wallHit = Gdx.audio.newSound(Gdx.files.internal("wall_hit.wav"));
		state = "start";
		won = false;
	}

	@Override
	public void render () {
		// setup
		Gdx.gl.glClearColor(40/255f,45/255f,52/255f,1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		//update

		update();

		// draw
		batch.begin();
		if(state.equals("play") || state.equals("start")) drawFont();
		else drawWinScreen();
		batch.end();

		shape.begin(ShapeRenderer.ShapeType.Filled);
		drawRectangles();
		drawDebug();
		shape.end();

		displayFPS();
	}

	//update method
	public void update(){

		if(state.equals("play")){
			if(ball.collides(p1)){
				ball.dx = -ball.dx * 1.03f;
				ball.x = p1.x + 5;

				if(ball.dy < 0) ball.dy = -MathUtils.random(0,5);
				else ball.dy = MathUtils.random(0,5);
				paddleHit.play(1f);
			}
			if(ball.collides(p2)){
				ball.dx = -ball.dx * 1.03f;
				ball.x = p2.x-8;

				if(ball.dy < 1){
					ball.dy = -MathUtils.random(0,5);
				}else ball.dy = MathUtils.random(0,5);
				paddleHit.play(1f);
			}

			if(ball.y <= 0){
				ball.y = 0;
				ball.dy = -ball.dy;
				wallHit.play(1f);
			}

			if(ball.y >= VH - 4){
				ball.y = VH -4;
				ball.dy = -ball.dy;
				wallHit.play(1f);
			}

			if(ball.x <= 0){
				scoreTwo++;
				score.play(1f);

				if(scoreTwo == 10){
					state = "done";
					won = false;
					scoreOne = 0;
					scoreTwo = 0;
				}
				else{
					state = "start";
					ball.reset();
				}
			}

			if(ball.x > VW){
				scoreOne++;
				score.play(1f);

				if(scoreOne == 10) {
					state = "done";
					won = true;
					scoreOne = 0;
					scoreTwo = 0;
				}
				else{
					state = "start";
					ball.reset();
				}
			}
		}

		if(Gdx.input.isKeyPressed(Input.Keys.W)) p1.dy = PADDLE_SPEED;
		else if(Gdx.input.isKeyPressed(Input.Keys.S)) p1.dy = -PADDLE_SPEED;
		else p1.dy = 0;
		if(Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) changeState();
		if(state.equals("play")){
			ball.update();
		}
		p1.update();
		updateAI();
	}

	public void changeState(){
		if(state.equals("start")) state = "play";
		else {
			state = "start";
			ball.reset();
		}
	}

	public void updateAI(){

		if(ball.y > p2.y + p2.height/2 ) {
			p2.dy = PADDLE_SPEED - 2;
			p2.update();
		}
		else if(ball.y < p2.y+p2.height/2){
			p2.dy = -(PADDLE_SPEED - 2);
			p2.update();
		}
	}

	//draw methods

	public void drawFont(){
		CharSequence scoreP1 = Integer.toString(scoreOne);
		CharSequence scoreP2 = Integer.toString(scoreTwo);
		layout.setText(font,scoreP1);
		float textWidth = layout.width;
		float x = VW / 2 - textWidth/2;
		font.draw(batch,scoreP1,x-50,VH-30);
		font.draw(batch,scoreP2,x+40,VH - 30);
	}

	public void drawWinScreen(){
		if(won){
			font.draw(batch,"Congrats you Won!",VW/2-VW/8,VH-30);
		}else font.draw(batch,"You Lost!",VW/2-VW/16+20,VH-30);
	}

	public void drawRectangles(){
		//left paddle
		p1.render(shape);
		//right paddle
		p2.render(shape);
		//ball
		ball.render(shape);
	}

	public void drawDebug(){
		shape.line(0,VH/2,VW,VH/2);
		shape.line(VW/2,0,VW/2,VH);
		shape.line(0,p2.y,VW,p2.y); // y is the bottom of the paddle
		shape.line(0,700,VW,700);
	}

	public void displayFPS(){
		System.out.println("FPS: " + Gdx.graphics.getFramesPerSecond());
	}
	
	@Override
	public void dispose () {
		batch.dispose();
		font.dispose();
		paddleHit.dispose();
		score.dispose();
		wallHit.dispose();
	}
}
