package com.example.danieloliveira.flappybird;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.Random;

public class FlappyBird extends ApplicationAdapter {

    private SpriteBatch batch;
    private Texture[] birds;
    private Texture background;
    private Texture topPipe;
    private Texture bottomPipe;
    private Texture gameOver;
    private Random randomNumber;
    private BitmapFont font;
    private BitmapFont gameOverMessage;
    private Circle birdCircle;
    private Rectangle topRectanglePipe;
    private Rectangle bottomRectanglePipe;
    //private ShapeRenderer shapeRenderer;

    //Config attributes
    private float widthDisplay;
    private float heightDisplay;
    private int gameState = 0; // 0-> waiting start 1-> game started 2-> game over
    private int score = 0;

    private float variation = 0;
    private float dropSpeed;
    private float verticalStartPosition;
    private float horizontalMovementPipePosition;
    private float spaceBetweenPipes;
    private float deltaTime;
    private float randomHeightBetweenPipes;
    private boolean scoredOne;

    //Camera
    private OrthographicCamera camera;
    private Viewport viewport;
    private final float VIRTUAL_WIDTH = 768;
    private final float VIRTUAL_HEIGHT = 1024;


    @Override
    public void create() {
        randomNumber = new Random();

        gameOverMessage = new BitmapFont();
        gameOverMessage.setColor(Color.WHITE);
        gameOverMessage.getData().setScale(3);

        font = new BitmapFont();
        font.setColor(Color.WHITE);
        font.getData().setScale(6);

        batch = new SpriteBatch();
        birdCircle = new Circle();
        /*bottomRectanglePipe = new Rectangle();
        topRectanglePipe = new Rectangle();*/
        //shapeRenderer = new ShapeRenderer();

        birds = new Texture[3];
        birds[0] = new Texture("passaro1.png");
        birds[1] = new Texture("passaro2.png");
        birds[2] = new Texture("passaro3.png");
        background = new Texture("fundo.png");
        topPipe = new Texture("cano_topo_maior.png");
        bottomPipe = new Texture("cano_baixo_maior.png");
        gameOver = new Texture("game_over.png");

        //Camera configs
        camera = new OrthographicCamera();
        camera.position.set(VIRTUAL_WIDTH / 2, VIRTUAL_HEIGHT / 2, 0);
        viewport = new StretchViewport(VIRTUAL_WIDTH, VIRTUAL_HEIGHT, camera);

        widthDisplay = VIRTUAL_WIDTH;
        heightDisplay = VIRTUAL_HEIGHT;

        verticalStartPosition = heightDisplay / 2;
        horizontalMovementPipePosition = widthDisplay;
        spaceBetweenPipes = 300;
    }

    @Override
    public void render() {
        camera.update();

        //Clear previous frames
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        //makes the bird beat its wings
        deltaTime = Gdx.graphics.getDeltaTime();
        variation += deltaTime * 5;
        if (variation > 2) variation = 0;

        //game not started
        if (gameState == 0) {
            if (Gdx.input.justTouched()) {
                gameState = 1;
            }
        } else { //game started
            dropSpeed++;

            //keep the bird on the screen
            if (verticalStartPosition > 0 || dropSpeed < 0) {
                verticalStartPosition -= dropSpeed;
            }

            if (gameState == 1) {
                horizontalMovementPipePosition -= deltaTime * 200;

                if (Gdx.input.justTouched()) {
                    dropSpeed = -15;
                }

                //Check if the pipe is out of the screen
                if (horizontalMovementPipePosition < -topPipe.getWidth()) {
                    horizontalMovementPipePosition = widthDisplay;
                    randomHeightBetweenPipes = randomNumber.nextInt(400) - 200;
                    scoredOne = false;
                }

                //Check score
                if (horizontalMovementPipePosition < 120) {
                    if (!scoredOne) {
                        score++;
                        scoredOne = true;
                    }
                }
            } else { //game over screen
                if (Gdx.input.justTouched()) {
                    gameState = 0;
                    score = 0;
                    dropSpeed = 0;
                    verticalStartPosition = heightDisplay / 2;
                    horizontalMovementPipePosition = widthDisplay;
                }
            }
        }

        //Camera projection config
        batch.setProjectionMatrix(camera.combined);

        //Drawing and setting images
        batch.begin();

        batch.draw(background, 0, 0, widthDisplay, heightDisplay);
        batch.draw(topPipe, horizontalMovementPipePosition, heightDisplay / 2 + spaceBetweenPipes / 2 + randomHeightBetweenPipes);
        batch.draw(bottomPipe, horizontalMovementPipePosition, heightDisplay / 2 - bottomPipe.getHeight() - spaceBetweenPipes / 2 + randomHeightBetweenPipes);
        batch.draw(birds[(int) variation], 120, verticalStartPosition);
        font.draw(batch, String.valueOf(score), widthDisplay / 2, heightDisplay - 50);

        if (gameState == 2) {
            batch.draw(gameOver, widthDisplay / 2 - gameOver.getWidth() / 2, heightDisplay / 2);
            gameOverMessage.draw(batch, "Touch to Restart!", widthDisplay / 2 - 170, heightDisplay / 2 - gameOver.getHeight() / 2);
        }

        batch.end();

        birdCircle.set(120 + birds[0].getWidth() / 2, verticalStartPosition + birds[0].getHeight() / 2, birds[0].getWidth() / 2);
        bottomRectanglePipe = new Rectangle(
                horizontalMovementPipePosition, heightDisplay / 2 - bottomPipe.getHeight() - spaceBetweenPipes / 2 + randomHeightBetweenPipes,
                bottomPipe.getWidth(), bottomPipe.getHeight()
        );

        topRectanglePipe = new Rectangle(
                horizontalMovementPipePosition, heightDisplay / 2 + spaceBetweenPipes / 2 + randomHeightBetweenPipes,
                topPipe.getWidth(), topPipe.getHeight()
        );

        //Drawing shapes as line guides
        /*shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        shapeRenderer.circle(birdCircle.x, birdCircle.y, birdCircle.radius);
        shapeRenderer.rect(bottomRectanglePipe.x, bottomRectanglePipe.y, bottomRectanglePipe.width, bottomRectanglePipe.height);
        shapeRenderer.rect(topRectanglePipe.x, topRectanglePipe.y, topRectanglePipe.width, topRectanglePipe.height);
        shapeRenderer.setColor(Color.RED);

        shapeRenderer.end();*/

        //Colision test
        if (Intersector.overlaps(birdCircle, bottomRectanglePipe) || Intersector.overlaps(birdCircle, topRectanglePipe)
                || verticalStartPosition <= 0 || verticalStartPosition >= heightDisplay) {
            gameState = 2;
        }
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
    }
}