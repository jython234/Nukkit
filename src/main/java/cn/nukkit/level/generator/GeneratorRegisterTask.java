package cn.nukkit.level.generator;

import cn.nukkit.block.Block;
import cn.nukkit.level.Level;
import cn.nukkit.level.SimpleChunkManager;
import cn.nukkit.level.generator.biome.Biome;
import cn.nukkit.scheduler.AsyncTask;

import java.util.Map;
import java.util.Random;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class GeneratorRegisterTask extends AsyncTask {

    public Class<? extends Generator> generator;
    public Map<String, Object> settings;
    public int seed;
    public int levelId;

    public GeneratorRegisterTask(Level level, Generator generator) {
        this.generator = generator.getClass();
        this.settings = generator.getSettings();
        this.seed = level.getSeed();
        this.levelId = level.getId();
    }

    @Override
    public void onRun() {
        Block.init();
        Biome.init();
        SimpleChunkManager manager = new SimpleChunkManager(this.seed);
        this.saveToThreadStore("generation.level" + this.levelId + ".manager", manager);
        try {
            Generator generator = this.generator.getConstructor(Map.class).newInstance(this.settings);
            generator.init(manager, new Random(manager.getSeed()));
            this.saveToThreadStore("generation.level" + this.levelId + ".generator", generator);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
