package com.feup.superslimefootball.controller;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ai.steer.behaviors.Seek;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.feup.superslimefootball.controller.entities.BallBody;
import com.feup.superslimefootball.controller.entities.GdxAI.B2dSteeringBody;
import com.feup.superslimefootball.controller.entities.GoalBody;
import com.feup.superslimefootball.controller.entities.PowerBody;
import com.feup.superslimefootball.controller.entities.SlimeBody;
import com.feup.superslimefootball.controller.entities.WallsBody;
import com.feup.superslimefootball.model.GameModel;
import com.feup.superslimefootball.model.entities.BallModel;
import com.feup.superslimefootball.model.entities.EntityModel;
import com.feup.superslimefootball.model.entities.GoalModel;
import com.feup.superslimefootball.model.entities.PowerModel;
import com.feup.superslimefootball.model.entities.SlimeModel;
import com.feup.superslimefootball.network.NetworkManager;
import com.feup.superslimefootball.view.utilities.GameConfig;
import com.feup.superslimefootball.view.utilities.MoveEvent;

import java.util.List;
import java.util.Random;

import static com.feup.superslimefootball.view.GameView.PPM;

/**
 * The type Game controller.
 */
public class GameController implements ContactListener {


    /**
     * The singleton instance of this controller
     */
    private static GameController instance;

    /**
     * The arena width in meters.
     */
    public static final int GAME_WIDTH = 800;

    /**
     * The arena height in meters.
     */
    public static final int GAME_HEIGHT = 400;

    /**
     * The physics world controlled by this controller.
     */
    private final World world;

    /**
     * The slime body.
     */
    private SlimeBody slimeBody;

    /**
     * The slime body.
     */
    private SlimeBody opponentSlimeBody;


    /**
     * The ball body.
     */
    private BallBody ballBody;


    /**
     * Accumulator used to calculate the simulation step.
     */
    private float accumulator;


    /**
     * Bodys used on AI
     */
    B2dSteeringBody entity, /**
     * The Target.
     */
    target;


    /**
     * Creates a new GameController that controls the physics of a certain GameModel.
     *
     */
    private GameController() {
        world = new World(new Vector2(0f, -9.8f), false);

        if(NetworkManager.getInstance().isServer() || !NetworkManager.getInstance().isConnected()) {
            new WallsBody(world, GameModel.getInstance().getWallsModel());
            slimeBody = new SlimeBody(world, GameModel.getInstance().getSlimeModel());

            opponentSlimeBody = new SlimeBody(world, GameModel.getInstance().getOpponentSlimeModel());
            ballBody = new BallBody(world, GameModel.getInstance().getBallModel());


            entity = new B2dSteeringBody(opponentSlimeBody.getBody(), 30f);
            target = new B2dSteeringBody(ballBody.getBody(), 13f);

            Seek<Vector2> seek = new Seek<Vector2>(entity, target);
            entity.setBehavior(seek);


            List<GoalModel> goals = GameModel.getInstance().getGoals();
            boolean flip = true;
            for (GoalModel goal : goals) {
                flip = !flip;
                new GoalBody(world, goal, flip);
            }

            world.setContactListener(this);
        }
    }

    /**
     * Returns a singleton instance of a game controller
     *
     * @return the singleton instance
     */
    public static GameController getInstance() {
        if (instance == null)
            instance = new GameController();
        return instance;
    }


    /**
     * Calculates the next physics step of duration delta (in seconds).
     *
     * @param delta The size of this physics step in seconds.
     */
    public void update(float delta) {

        if(NetworkManager.getInstance().isServer() || !NetworkManager.getInstance().isConnected()) {
            GameModel.getInstance().update(delta);

            moveOpponentPlayer(delta);

            updatePowers();
            updateState();
            updateBall();

            float frameTime = Math.min(delta, 0.25f);
            accumulator += frameTime;
            while (accumulator >= 1 / 60f) {
                world.step(1 / 60f, 6, 2);
                accumulator -= 1 / 60f;
            }

            Array<Body> bodies = new Array<Body>();
            world.getBodies(bodies);

            for (Body body : bodies) {
                ((EntityModel) body.getUserData()).setPosition(body.getPosition().x * PPM, body.getPosition().y * PPM);
            }

        }
    }

    /**
     * Returns the world controlled by this controller. Needed for debugging purposes only.
     *
     * @return The world controlled by this controller.
     */
    public World getWorld() {
        return world;
    }

    /**
     * Verifies in witch state SlimeOne is
     */
    public void updateState() {

        if(slimeBody.getLinearVelocity().y != 0)
            ((SlimeModel)slimeBody.getUserData()).setSlimeState(SlimeModel.SlimeState.JUMPING);
        else
            ((SlimeModel)slimeBody.getUserData()).setSlimeState(SlimeModel.SlimeState.RUNNING);

        if(opponentSlimeBody.getLinearVelocity().y != 0)
            ((SlimeModel)opponentSlimeBody.getUserData()).setSlimeState(SlimeModel.SlimeState.JUMPING);
        else
            ((SlimeModel)opponentSlimeBody.getUserData()).setSlimeState(SlimeModel.SlimeState.RUNNING);
    }

    /**
     * Verifies if the ball has stopped
     */
    public void updateBall(){
        float xVelocity = ballBody.getLinearVelocity().x;
        float yVelocity = ballBody.getLinearVelocity().y;
        BallModel ballModel = (BallModel)ballBody.getUserData();
        if(xVelocity == 0.0 && yVelocity == 0.0)
            ballModel.setTimer(1);
        else
            ballModel.setTimer(0);
        if(ballModel.getTimer() >= 200) {
            Random rand = new Random();
            ballBody.applyForceToCenter(rand.nextFloat() * (600 - 100) + 100, rand.nextFloat() * (600 - 100) + 100, true);
        }
    }

    /**
     * Wrapper of moveRight
     */
    public void moveRight() {
        moveRight(slimeBody);
    }

    /**
     * Wrapper of moveLeft
     */
    public void moveLeft() {
        moveLeft(slimeBody);
    }

    /**
     * Wrapper of Jump
     */
    public void jump() {
        jump(slimeBody);
    }

    /**
     * Wrapper of powerUP
     */
    public void powerUP() {
        powerUP(slimeBody);
    }

    /**
     * Moves the Slime to the right.
     *
     * @param body of the slime to move
     */
    public void moveRight(SlimeBody body) {
        body.moveRight();
    }

    /**
     * Moves the Slime to the left.
     *
     * @param body of the slime to move
     */
    public void moveLeft(SlimeBody body) {
        body.moveLeft();
    }

    /**
     * Moves the Slime up.
     *
     * @param body of the slime to move
     */
    public void jump(SlimeBody body) {
        body.jump();
    }

    /**
     * Uses the Slime power.
     *
     * @param body of the slime to move
     */
    public void powerUP(SlimeBody body) {
        SlimeModel slimeModel = (SlimeModel)body.getUserData();

        if(slimeModel.getPowerType() != null){
            if(slimeModel.getPowerType() == PowerModel.PowerType.SPEED)
                body.setSlimeBodyBehaviour(slimeModel.getPowerType());
            ((SlimeModel)body.getUserData()).setPowerType(null);
        }

    }

    @Override
    public void beginContact(Contact contact) {

        Body bodyA = contact.getFixtureA().getBody();
        Body bodyB = contact.getFixtureB().getBody();


        if (bodyA.getUserData() instanceof SlimeModel && bodyB.getUserData() instanceof PowerModel)
            slimePowerCollision(bodyA, bodyB);
        else if (bodyA.getUserData() instanceof PowerModel && bodyB.getUserData() instanceof SlimeModel)
            slimePowerCollision(bodyB, bodyA);

        else if (bodyA.getUserData() instanceof BallModel && bodyB.getUserData() instanceof GoalModel)
            ballGoalCollision(bodyA, contact.getFixtureB());
        else if (bodyA.getUserData() instanceof GoalModel && bodyB.getUserData() instanceof BallModel)
            ballGoalCollision(bodyB, contact.getFixtureA());



    }

    @Override
    public void endContact(Contact contact) {

    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {

    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

    }

    /**
     * A ball collided with a goal.
     * @param ballBody the ball that collided
     * @param goalFixture the fixture of the goal that collided
     */
    private void ballGoalCollision(Body ballBody, Fixture goalFixture) {
        if(goalFixture.isSensor()) {
            if (goalFixture.getBody().getPosition().x * PPM > Gdx.graphics.getWidth() / 2){
                GameConfig.getInstance().updateScore(1, 0);
            }

            else {
                GameConfig.getInstance().updateScore(0, 1);

            }
            setFlagsToReset();
        }
    }

    /**
     * A slime collided with a Power.
     * @param slimeBody the slime that collided
     * @param powerBody the power that collided
     */
    private void slimePowerCollision(Body slimeBody, Body powerBody) {

        if(((SlimeModel)slimeBody.getUserData()).getPowerType() == null){
            ((SlimeModel)slimeBody.getUserData()).setPowerType(((PowerModel) powerBody.getUserData()).getPowerType());
            ((PowerModel)powerBody.getUserData()).setFlaggedForRemoval(true);
        }
    }


    /**
     * Manages the  objects that have been flagged for removal or reset on the
     * previous step.
     */
    public void manageFlagged() {
        Array<Body> bodies = new Array<Body>();
        world.getBodies(bodies);
        for (Body body : bodies) {

            EntityModel model = ((EntityModel)body.getUserData());

            if(model.isFlaggedToBeReseted()){
                body.setLinearVelocity(0,0);
                body.setTransform(model.getInicialPos(), 0);
                model.setFlaggedForReset(false);
            }

            if (model.isFlaggedToBeRemoved()) {
                GameModel.getInstance().remove((EntityModel) body.getUserData());
                world.destroyBody(body);
            }
        }
    }

    /**
     * Resets the Flags
     */
    private void setFlagsToReset(){
        ((EntityModel)slimeBody.getUserData()).setFlaggedForReset(true);
        ((EntityModel)ballBody.getUserData()).setFlaggedForReset(true);
        ((EntityModel)opponentSlimeBody.getUserData()).setFlaggedForReset(true);

        List<PowerModel> powers = GameModel.getInstance().getPowers();
        for (PowerModel power : powers)
            power.setFlaggedForRemoval(true);

    }

    /**
     * Updates the game's powers
     */
    private void updatePowers(){
        List<PowerModel> powers = GameModel.getInstance().getPowers();
        for (PowerModel power : powers)
                new PowerBody(world, power);
    }

    /**
     * Moves the opponent of the player
     *@param delta the time in seconds
     */
    private void moveOpponentPlayer(float delta){
        if(NetworkManager.getInstance().isConnected())
            moveOpponentPlayerNetwork();
        else
            moveOpponentPlayerAI(delta);

    }

    /**
     * Moves the AI opponent of the player
     *@param delta the time in seconds
     */
    private void moveOpponentPlayerAI(float delta){
        entity.update(delta);
    }

    /**
     * Moves the opponent of the player in Multiplayer
     */
    private void moveOpponentPlayerNetwork(){
        Object object = NetworkManager.getInstance().receiveData();

        if(object instanceof MoveEvent) {
            MoveEvent moveEvent = (MoveEvent) object;

            if (moveEvent == MoveEvent.LEFT) {
                GameController.getInstance().moveLeft(opponentSlimeBody);
            }
            if (moveEvent == MoveEvent.RIGHT) {
                GameController.getInstance().moveRight(opponentSlimeBody);
            }
            if (moveEvent == MoveEvent.JUMP) {
                GameController.getInstance().jump(opponentSlimeBody);
            }
            if (moveEvent == MoveEvent.POWER) {
                GameController.getInstance().powerUP(opponentSlimeBody);
            }
        }
    }

    /**
     * Updates Network
     */
    public void updateNetwork( ) {
        if (NetworkManager.getInstance().isServer()) {
            GameModel.getInstance().setScore(GameConfig.getInstance().getScore());
            NetworkManager.getInstance().sendData(GameModel.getInstance());
        } else {
            Object object = NetworkManager.getInstance().receiveData();

            if (object instanceof GameModel) {
                GameModel gameModel = (GameModel) object;
                if (gameModel != null) {
                    GameModel.setInstance(gameModel);
                }
            }
        }
    }

    /**
     * Returns the slimeBody
     *
     * @return slime body
     */
    public SlimeBody getSlimeBody() {
        return slimeBody;
    }

    /**
     * Returns the opponentSlimeBody
     *
     * @return opponent slime body
     */
    public SlimeBody getOpponentSlimeBody() {
        return opponentSlimeBody;
    }

    /**
     * Returns the ballBody
     *
     * @return ball body
     */
    public BallBody getBallBody() {
        return ballBody;
    }

    /**
     * Resets the singleton instance
     */
    public static void resetInstance(){
        instance = new GameController();
    }
}
