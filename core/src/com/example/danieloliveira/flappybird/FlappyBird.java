package com.example.danieloliveira.flappybird;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.util.Random;

public class FlappyBird extends ApplicationAdapter {

    private SpriteBatch batch;
    private Texture[] birds;
    private Texture background;
    private Texture topPipe;
    private Texture bottonPipe;
    private Random randomNumber;
    private BitmapFont font;

    //Config attributes
    private int widthDisplay;
    private int heightDisplay;
    private int gameState = 0;
    private int score = 0;

    private float variation = 0;
    private float dropSpeed;
    private float verticalStartPosition;
    private float horizontalMovimentPipePosition;
    private float spaceBetweenPipes;
    private float deltaTime;
    private float randomHeightBetweenPipes;
    private boolean scoredOne;

    @Override
    public void create() {
        randomNumber = new Random();

        font = new BitmapFont();
        font.setColor(Color.WHITE);
        font.getData().setScale(6);

        batch = new SpriteBatch();

        birds = new Texture[3];
        birds[0] = new Texture("passaro1.png");
        birds[1] = new Texture("passaro2.png");
        birds[2] = new Texture("passaro3.png");
        background = new Texture("fundo.png");
        topPipe = new Texture("cano_topo_maior.png");
        bottonPipe = new Texture("cano_baixo_maior.png");

        widthDisplay = Gdx.graphics.getWidth();
        heightDisplay = Gdx.graphics.getHeight();
        verticalStartPosition = heightDisplay / 2;
        horizontalMovimentPipePosition = widthDisplay;
        spaceBetweenPipes = 300;
    }

    @Override
    public void render() {
        //makes the bird beat its wings
        deltaTime = Gdx.graphics.getDeltaTime();
        variation += deltaTime * 5;
        if (variation > 2) variation = 0;

        //game not started
        if (gameState == 0) {
            if (Gdx.input.justTouched()) {
                gameState = 1;
            }
        } else {
            horizontalMovimentPipePosition -= deltaTime * 200;
            dropSpeed++;

            if (Gdx.input.justTouched()) {
                dropSpeed = -15;
            }

            if (verticalStartPosition > 0 || dropSpeed < 0) {
                verticalStartPosition -= dropSpeed;
            }

            //Check if the pipe is out of the display
            if (horizontalMovimentPipePosition < -topPipe.getWidth()) {
                horizontalMovimentPipePosition = widthDisplay;
                randomHeightBetweenPipes = randomNumber.nextInt(400) - 200;
                scoredOne = false;
            }

            //check score
            if (horizontalMovimentPipePosition < 120) {
                if (!scoredOne) {
                    score++;
                    scoredOne = true;
                }
            }
        }

        batch.begin();

        batch.draw(background, 0, 0, widthDisplay, heightDisplay);
        batch.draw(topPipe, horizontalMovimentPipePosition, heightDisplay / 2 + spaceBetweenPipes / 2 + randomHeightBetweenPipes);
        batch.draw(bottonPipe, horizontalMovimentPipePosition, heightDisplay / 2 - bottonPipe.getHeight() - spaceBetweenPipes / 2 + randomHeightBetweenPipes);
        batch.draw(birds[(int) variation], 120, verticalStartPosition);
        font.draw(batch, String.valueOf(score), widthDisplay / 2, heightDisplay - 50);

        batch.end();
    }
}