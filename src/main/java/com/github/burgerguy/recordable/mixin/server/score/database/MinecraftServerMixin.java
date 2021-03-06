package com.github.burgerguy.recordable.mixin.server.score.database;

import com.github.burgerguy.recordable.server.database.ScoreDatabase;
import com.github.burgerguy.recordable.server.database.ScoreDatabaseContainer;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(MinecraftServer.class)
public class MinecraftServerMixin implements ScoreDatabaseContainer {
    private ScoreDatabase scoreDatabase;

    @Override
    public ScoreDatabase getScoreDatabase() {
        return this.scoreDatabase;
    }

    @Override
    public void setScoreDatabase(ScoreDatabase database) {
        this.scoreDatabase = database;
    }
}
