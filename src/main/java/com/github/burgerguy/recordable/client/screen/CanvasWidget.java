package com.github.burgerguy.recordable.client.screen;

import com.github.burgerguy.recordable.client.render.util.ScreenRenderUtil;
import com.github.burgerguy.recordable.shared.menu.Canvas;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Matrix4f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.sounds.SoundEvents;
import org.lwjgl.glfw.GLFW;

public class CanvasWidget extends AbstractWidget {
    private static final int GRID_COLOR = 0xFF000000;
    private static final float GRID_HALF_LINE_WIDTH = 0.5f;

    private final ClientCanvas clientCanvas;
    private final int pixelSize;

    private int currentEventPixelIdx = Canvas.EMPTY_INDEX;
    private boolean pauseEvents;

    public CanvasWidget(int x, int y, int pixelSize, ClientCanvas clientCanvas) {
        super(
                x,
                y,
                clientCanvas.getWidth() * pixelSize,
                clientCanvas.getHeight() * pixelSize,
                new TranslatableComponent("screen.recordable.labeler.paint_area")
        );
        this.clientCanvas = clientCanvas;
        this.pixelSize = pixelSize;
    }

    private void tryApply(double mouseX, double mouseY) {
        if (this.pauseEvents) return;

        int x = (int) ((mouseX - this.x) / this.pixelSize);
        int y = (int) ((mouseY - this.y) / this.pixelSize);
        int pixelIdx = this.clientCanvas.coordsToIndex(x, y);
        if (this.currentEventPixelIdx != pixelIdx) {
            if (pixelIdx != Canvas.EMPTY_INDEX && pixelIdx != Canvas.OUT_OF_BOUNDS_INDEX) {
                boolean anyPaintEmpty = this.clientCanvas.apply(pixelIdx);
                if (anyPaintEmpty) {
                    this.playEmptySound();
                    this.pauseEvents = true;
                }
                this.currentEventPixelIdx = pixelIdx;
            }
        }
    }

    private void playEmptySound() {
        Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.DISPENSER_FAIL, 1.2f));
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        boolean superResult = super.keyPressed(keyCode, scanCode, modifiers);
        if (!superResult && Screen.hasControlDown() && keyCode == GLFW.GLFW_KEY_Z && this.clientCanvas.canUndo()) {
            this.clientCanvas.undo();
            return true;
        }
        return false;
    }

    @Override
    public void onRelease(double mouseX, double mouseY) {
        this.currentEventPixelIdx = Canvas.EMPTY_INDEX;
        this.pauseEvents = false;
    }

    @Override
    public void onClick(double mouseX, double mouseY) {
        tryApply(mouseX, mouseY);
    }

    @Override
    protected void onDrag(double mouseX, double mouseY, double dragX, double dragY) {
        tryApply(mouseX, mouseY);
    }

    @Override
    public void renderButton(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
        Matrix4f matrix = poseStack.last().pose();
        float widgetX = this.x;
        float widgetY = this.y;
        float pixelSize = this.pixelSize;
        int paHeight = this.clientCanvas.getHeight();
        int paWidth = this.clientCanvas.getWidth();

        // pixels
        for (int pxYCoord = 0; pxYCoord < paHeight; pxYCoord++) {
            for (int pxXCoord = 0; pxXCoord < paWidth; pxXCoord++) {
                int pxIndex = this.clientCanvas.coordsToIndex(pxXCoord, pxYCoord);
                if (pxIndex != Canvas.EMPTY_INDEX) {
                    ScreenRenderUtil.fill(
                            matrix,
                            widgetX + (pxXCoord * pixelSize),
                            widgetY + (pxYCoord * pixelSize),
                            widgetX + (pxXCoord * pixelSize) + pixelSize,
                            widgetY + (pxYCoord * pixelSize) + pixelSize,
                            this.clientCanvas.getColor(pxIndex) | 0xFF000000 // make opaque
                    );
                }
            }
        }

        // grid lines
        for (int pxYCoord = 0; pxYCoord <= paHeight; pxYCoord++) {
            for (int pxXCoord = 0; pxXCoord <= paWidth; pxXCoord++) {
                float lineX = pxXCoord * pixelSize;
                float lineY = pxYCoord * pixelSize;
                int currentIdx = this.clientCanvas.coordsToIndex(pxXCoord, pxYCoord);
                int prevIdxX = this.clientCanvas.coordsToIndex(pxXCoord - 1, pxYCoord);
                int prevIdxY = this.clientCanvas.coordsToIndex(pxXCoord, pxYCoord - 1);

                // horizontal lines, drawn in vertical order
                if ((currentIdx != Canvas.EMPTY_INDEX &&
                    currentIdx != Canvas.OUT_OF_BOUNDS_INDEX)
                    ||
                    (pxXCoord != paWidth &&
                    prevIdxY != Canvas.EMPTY_INDEX &&
                    prevIdxY != Canvas.OUT_OF_BOUNDS_INDEX)) {
                    ScreenRenderUtil.fill(
                            matrix,
                            widgetX + lineX,
                            widgetY + lineY - GRID_HALF_LINE_WIDTH,
                            widgetX + lineX + pixelSize,
                            widgetY + lineY + GRID_HALF_LINE_WIDTH,
                            GRID_COLOR
                    );
                }

                // vertical lines, drawn in horizontal order
                if ((currentIdx != Canvas.EMPTY_INDEX &&
                    currentIdx != Canvas.OUT_OF_BOUNDS_INDEX)
                    ||
                    (pxYCoord != paHeight &&
                    prevIdxX != Canvas.EMPTY_INDEX &&
                    prevIdxX != Canvas.OUT_OF_BOUNDS_INDEX)) {
                    ScreenRenderUtil.fill(
                            matrix,
                            widgetX + lineX - GRID_HALF_LINE_WIDTH,
                            widgetY + lineY,
                            widgetX + lineX + GRID_HALF_LINE_WIDTH,
                            widgetY + lineY + pixelSize,
                            GRID_COLOR
                    );
                }
            }
        }
    }

    @Override
    public void playDownSound(SoundManager handler) {
        // no sound should be played
    }

    @Override
    public void updateNarration(NarrationElementOutput narrationElementOutput) {
        this.defaultButtonNarrationText(narrationElementOutput);
    }
}
