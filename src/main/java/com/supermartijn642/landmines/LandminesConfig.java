package com.supermartijn642.landmines;

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
    public static final Supplier<Double> launchForce;

    public static final Supplier<Boolean> teleportReusable;
    public static final Supplier<Double> teleportRange;

    public static final Supplier<Boolean> fireReusable;

    public static final Supplier<Boolean> snowReusable;
    public static final Supplier<Integer> snowRange;

    public static final Supplier<Boolean> zombieReusable;
    public static final Supplier<Integer> zombieCount;
    public static final Supplier<Integer> zombieRange;

    public static final Supplier<Boolean> levitationReusable;
    public static final Supplier<Integer> levitationDuration;

    public static final Supplier<Boolean> lightningReusable;

    public static final Supplier<Boolean> arrowsReusable;
    public static final Supplier<Integer> arrowsCount;

    public static final Supplier<Boolean> fakeReusable;


    static{
        ModConfigBuilder builder = new ModConfigBuilder("landmines");

        builder.push("General");

        activationDelay = builder.comment("How much time should there be after arming a landmine before it can be triggered in ticks? 1 second = 20 ticks.").define("activationDelay", 100, 0, 2400);

        builder.push("Explosion Landmine");
        explosionReusable = builder.comment("Should the explosion landmine be reusable?").define("explosionReusable", false);
        explosionBreakBlocks = builder.comment("Should the explosion from an explosion landmine break blocks?").define("explosionBreakBlocks", true);
        explosionCausesFire = builder.comment("Should the explosion from an explosion landmine cause fire?").define("explosionCausesFire", false);
        builder.pop();

        builder.push("Potion Landmine");
        potionReusable = builder.comment("Should the potion landmine be reusable?").define("potionReusable", true);
        builder.pop();

        builder.push("Launch Landmine");
        launchReusable = builder.comment("Should the launch landmine be reusable?").define("launchReusable", true);
        launchForce = builder.comment("With what force should entities be launched? 1 ~= 4.5 blocks in height").define("launchForce", 1, 0.1, 2);
        builder.pop();

        builder.push("Teleport Landmine");
        teleportReusable = builder.comment("Should the teleport landmine be reusable?").define("teleportReusable", true);
        teleportRange = builder.comment("What is the max range entities can be teleported to?").define("teleportRange", 3d, 16, 100);
        builder.pop();

        builder.push("Fire Landmine");
        fireReusable = builder.comment("Should the fire landmine be reusable?").define("fireReusable", true);
        builder.pop();

        builder.push("Snow Landmine");
        snowReusable = builder.comment("Should the snow landmine be reusable?").define("snowReusable", true);
        snowRange = builder.comment("In what range should the snow landmine spread snow?").define("snowRange", 5, 3, 10);
        builder.pop();

        builder.push("Zombie Landmine");
        zombieReusable = builder.comment("Should the zombie landmine be reusable?").define("zombieReusable", false);
        zombieCount = builder.comment("How many zombies should be spawned?").define("zombieCount", 5, 1, 50);
        zombieRange = builder.comment("In what range should zombies spawn?").define("zombieRange", 4, 3, 10);
        builder.pop();

        builder.push("Levitation Landmine");
        levitationReusable = builder.comment("Should the levitation landmine be reusable?").define("levitationReusable", true);
        levitationDuration = builder.comment("How long should the levitation effect last in ticks?").define("levitationDuration", 100, 20, 1200);
        builder.pop();

        builder.push("Lightning Landmine");
        lightningReusable = builder.comment("Should the lightning landmine be reusable?").define("lightningReusable", true);
        builder.pop();

        builder.push("Arrow Landmine");
        arrowsReusable = builder.comment("Should the arrow landmine be reusable?").define("arrowsReusable", true);
        arrowsCount = builder.comment("How many arrows should be spawned?").define("arrowsCount", 16, 8, 100);
        builder.pop();

        builder.push("Fake Landmine");
        fakeReusable = builder.comment("Should the fake landmine be reusable?").define("fakeReusable", true);
        builder.pop();

        builder.pop();

        builder.build();
    }

}
