package com.github.burgerguy.recordable.client.render.blockentity;

import com.github.burgerguy.recordable.shared.Recordable;
import com.github.burgerguy.recordable.shared.block.RecorderBlockEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import software.bernie.geckolib3q.model.AnimatedGeoModel;

public class RecorderModel extends AnimatedGeoModel<RecorderBlockEntity> {

    @Override
    public ResourceLocation getModelResource(RecorderBlockEntity recorderBlockEntity) {
        return new ResourceLocation(Recordable.MOD_ID, "geo/recorder.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(RecorderBlockEntity recorderBlockEntity) {
        ItemStack record = recorderBlockEntity.getRecordItem();
        if (record != null) {
            return new ResourceLocation(Recordable.MOD_ID, "textures/block/recorder_model_written.png");
        } else {
            return new ResourceLocation(Recordable.MOD_ID, "textures/block/recorder_model_unwritten.png");
        }
    }

    @Override
    public ResourceLocation getAnimationResource(RecorderBlockEntity recorderBlockEntity) {
        return new ResourceLocation(Recordable.MOD_ID, "animations/recorder.animation.json");
    }
}
