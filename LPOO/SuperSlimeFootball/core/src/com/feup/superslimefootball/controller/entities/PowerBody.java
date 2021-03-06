package com.feup.superslimefootball.controller.entities;

import com.badlogic.gdx.physics.box2d.World;
import com.feup.superslimefootball.model.entities.EntityModel;

/**
 * The type Power body.
 */
public class PowerBody extends EntityBody {
    /**
     * Constructs a body representing a model in a certain world.
     *
     * @param world The world this body lives on.
     * @param model The model representing the body.
     */
    public PowerBody(World world, EntityModel model) {
        super(world, model, false, 1.0f);

        float density = 1.0f;
        float restitution = 0.0f;

        createCircleFixture(body, 13, density, restitution, true);
    }
}
