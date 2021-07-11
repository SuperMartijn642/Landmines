package com.supermartijn642.structureblueprinter;

import com.supermartijn642.configlib.ModConfigBuilder;

import java.util.function.Supplier;

/**
 * Created 1/25/2021 by SuperMartijn642
 */
public class LandminesConfig {

    public static final Supplier<Integer> activationDelay;

    public static final Supplier<Boolean> explosionReusable;
    public static final Supplier<Boolean> explosionBreakBlocks;
    public static final Supplier<Boolean> explosionCausesFire;

    public static final Supplier<Boolean> potionReusable;

    public static final Supplier<Boolean> launchReusable;

    public static final Supplier<Boolean> teleportReusable;

    public static final Supplier<Boolean> fireReusable;

    public static final Supplier<Boolean> snowReusable;

    public static final Supplier<Boolean> zombieReusable;

    public static final Supplier<Boolean> levitationReusable;

    public static final Supplier<Boolean> lightningReusable;

    public static final Supplier<Boolean> arrowsReusable;

    public static final Supplier<Boolean> fakeReusable;


    static{
        ModConfigBuilder builder = new ModConfigBuilder("landmines");

        builder.push("General");

        activationDelay = builder.comment("How much time should there be after arming a landmine before it can be triggered in ticks? 1 second = 20 ticks.").define("activationDelay", 100, 0, 2400);

        builder.push("Explosion Land Mine");
        explosionReusable = builder.comment("Should the explosion land mine be reusable?").define("explosionReusable",false);
        explosionBreakBlocks = builder.comment("Should the explosion from an explosion landmine break blocks?").define("explosionBreakBlocks", true);
        explosionCausesFire = builder.comment("Should the explosion from an explosion landmine cause fire?").define("explosionCausesFire", false);
        builder.pop();

        builder.push("Potion Land Mine");
        potionReusable = builder.comment("Should the potion land mine be reusable?").define("potionReusable",true);
        builder.pop();

        builder.push("Launch Land Mine");
        launchReusable = builder.comment("Should the launch land mine be reusable?").define("launchReusable",true);
        builder.pop();

        builder.push("Teleport Land Mine");
        teleportReusable = builder.comment("Should the teleport land mine be reusable?").define("teleportReusable",true);
        builder.pop();

        builder.push("Fire Land Mine");
        fireReusable = builder.comment("Should the fire land mine be reusable?").define("fireReusable",true);
        builder.pop();

        builder.push("Snow Land Mine");
        snowReusable = builder.comment("Should the snow land mine be reusable?").define("snowReusable",true);
        builder.pop();

        builder.push("Zombie Land Mine");
        zombieReusable = builder.comment("Should the zombie land mine be reusable?").define("zombieReusable",false);
        builder.pop();

        builder.push("Levitation Land Mine");
        levitationReusable = builder.comment("Should the levitation land mine be reusable?").define("levitationReusable",true);
        builder.pop();

        builder.push("Lightning Land Mine");
        lightningReusable = builder.comment("Should the lightning land mine be reusable?").define("lightningReusable",true);
        builder.pop();

        builder.push("Arrow Land Mine");
        arrowsReusable = builder.comment("Should the arrow land mine be reusable?").define("arrowsReusable",true);
        builder.pop();

        builder.push("Fake Land Mine");
        fakeReusable = builder.comment("Should the fake land mine be reusable?").define("fakeReusable",true);
        builder.pop();

        builder.pop();

        builder.build();
    }

}
