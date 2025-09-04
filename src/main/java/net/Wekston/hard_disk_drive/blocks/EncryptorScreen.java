package net.Wekston.hard_disk_drive.blocks;

import net.Wekston.hard_disk_drive.event.EncryptTextPacket;
import net.Wekston.hard_disk_drive.event.NetworkHandler;
import net.Wekston.hard_disk_drive.items.EncryptedItem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

import static net.Wekston.hard_disk_drive.Config.encryptorNumber;

public class EncryptorScreen extends AbstractContainerScreen<EncryptorMenu> {
    private static final ResourceLocation TEXTURE = new ResourceLocation("hard_disk_drive", "textures/gui/encrypt_menu.png");
    private String currentText = "";
    private int cursorPosition = 0;
    private int maxLines = 5;
    private boolean isScrolling = false;
    private double scrollOffset = 0.0D;
    private List<String> visibleLines = new ArrayList<>();
    private int tickCount = 0;
    private boolean showCursor = true;
    private Button encryptButton;

    public EncryptorScreen(EncryptorMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        this.imageWidth = 176;
        this.imageHeight = 166;
    }



    private void sendTextToServer() {
        if (this.minecraft != null && this.minecraft.player != null) {
            NetworkHandler.INSTANCE.sendToServer(
                    new EncryptTextPacket(menu.getBlockEntity().getBlockPos(), currentText)
            );
        }
    }
    private void createEncryptButton() {
        this.removeWidget(encryptButton);

        encryptButton = Button.builder(Component.translatable("button.encrypt"), button -> {
            // Берем актуальный текст из BlockEntity
            String textToEncrypt = menu.getBlockEntity().getText();

            if (textToEncrypt != null && !textToEncrypt.trim().isEmpty()) {
                ItemStack stack = menu.getEncryptedItem();

                if (stack.getItem() instanceof EncryptedItem) {
                    // Отправляем пакет с шифрованием
                    NetworkHandler.INSTANCE.sendToServer(
                            new EncryptTextPacket(menu.getBlockEntity().getBlockPos(), textToEncrypt.trim())
                    );

                    // Очищаем текст после шифрования
                    menu.getBlockEntity().setText("");
                    currentText = "";
                    cursorPosition = 0;
                    updateVisibleText();
                }
                menu.broadcastChanges();
            }
        }).bounds(this.leftPos + 29, this.topPos + 23, 54, 17).build();

        this.addRenderableWidget(encryptButton);
    }
    private void insertText(String text) {
        if (currentText.length() + text.length() > encryptorNumber) {
            return;
        }

        String before = currentText.substring(0, cursorPosition);
        String after = currentText.substring(cursorPosition);
        currentText = before + text + after;
        cursorPosition += text.length();

        menu.getBlockEntity().setText(currentText);
        updateVisibleText();
    }
    @Override
    protected void init() {
        super.init();
        this.leftPos = (this.width - this.imageWidth) / 2;
        this.topPos = (this.height - this.imageHeight) / 2;

        this.currentText = menu.getBlockEntity().getText();
        if (currentText == null) currentText = "";
        this.cursorPosition = currentText.length();

        updateVisibleText();
        createEncryptButton();
        this.addRenderableWidget(Button.builder(Component.translatable("button.decrypt"), button -> {
            menu.decryptText();
            this.currentText = menu.getBlockEntity().getText();
            this.cursorPosition = currentText.length();
            updateVisibleText();
        }).bounds(this.leftPos + 29, this.topPos + 46, 54, 17).build());
    }
    private void updateVisibleText() {
        visibleLines.clear();

        if (!currentText.isEmpty()) {
            List<FormattedCharSequence> wrappedLines = this.font.split(Component.literal(currentText), 80);
            for (FormattedCharSequence line : wrappedLines) {
                StringBuilder lineBuilder = new StringBuilder();
                line.accept((index, style, codePoint) -> {
                    lineBuilder.appendCodePoint(codePoint);
                    return true;
                });
                visibleLines.add(lineBuilder.toString());
            }
        }

        maxLines = Math.max(0, visibleLines.size() - 8);
        scrollOffset = Mth.clamp(scrollOffset, 0, maxLines);
    }


    private void handleBackspace() {
        if (cursorPosition > 0) {
            String before = currentText.substring(0, cursorPosition - 1);
            String after = currentText.substring(cursorPosition);
            currentText = before + after;
            cursorPosition--;

            menu.getBlockEntity().setText(currentText);
            updateVisibleText();
        }
    }

    private void handleDelete() {
        if (cursorPosition < currentText.length()) {
            String before = currentText.substring(0, cursorPosition);
            String after = currentText.substring(cursorPosition + 1);
            currentText = before + after;

            menu.getBlockEntity().setText(currentText);
            updateVisibleText();
        }
    }

    @Override
    public void removed() {
        super.removed();
    }
    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        int x = this.leftPos;
        int y = this.topPos;

        graphics.blit(TEXTURE, x, y, 0, 0, 176, this.imageHeight);

        if (maxLines > 0 && visibleLines.size() > 3) {
            int scrollbarHeight = Math.max(10, (int)((15.0 / visibleLines.size()) * 69));
            int scrollbarY = y + 20 + (int)((scrollOffset / maxLines) * (60 - scrollbarHeight));
            graphics.fill(x + 167, scrollbarY, x + 168, scrollbarY + scrollbarHeight, 0xFF808080);
        }
    }
    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        graphics.drawString(this.font, this.title, this.titleLabelX, this.titleLabelY, 0x404040, false);
        graphics.drawString(this.font, this.playerInventoryTitle, this.inventoryLabelX, this.inventoryLabelY, 0x404040, false);

        int textX = 88;
        int textY = 7;
        int startLine = (int) scrollOffset;
        int endLine = Math.min(startLine + 8, visibleLines.size());

        for (int i = startLine; i < endLine; i++) {
            if (i < visibleLines.size()) {
                graphics.drawString(this.font, visibleLines.get(i), textX, textY + (i - startLine) * 9, 0xFFFFFF, false);
            }
        }

        if (showCursor && isMouseOverTextArea(mouseX, mouseY)) {
            drawCursor(graphics, textX, textY, startLine);
        }

    }

    private void drawCursor(GuiGraphics graphics, int textX, int textY, int startLine) {
        if (visibleLines.isEmpty()) {
            graphics.fill(textX, textY, textX + 1, textY + 9, 0xFFFFFFFF);
            return;
        }

        int currentLine = 0;
        int currentPos = 0;

        for (int i = 0; i < visibleLines.size(); i++) {
            String line = visibleLines.get(i);
            int lineLength = line.length();

            if (currentPos + lineLength >= cursorPosition) {
                currentLine = i;
                break;
            }
            currentPos += lineLength + 1;

            if (i == visibleLines.size() - 1) {
                currentLine = i;
                currentPos -= 1;
            }
        }

        if (currentLine >= startLine && currentLine < startLine + 15 && currentLine < visibleLines.size()) {
            int column = cursorPosition - currentPos;
            String lineText = visibleLines.get(currentLine);

            column = Math.min(column, lineText.length());
            column = Math.max(column, 0);

            String beforeCursor = lineText.substring(0, column);
            int cursorX = textX + this.font.width(beforeCursor);
            int cursorY = textY + (currentLine - startLine) * 9;

            graphics.fill(cursorX, cursorY, cursorX + 1, cursorY + 9, 0xFFFFFFFF);
        }
    }

    private boolean isMouseOverTextArea(double mouseX, double mouseY) {
        return (mouseX >= this.leftPos + 87 && mouseX <= this.leftPos + 168 &&
                mouseY >= this.topPos + 6 && mouseY <= this.topPos + 77);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(graphics);
        super.render(graphics, mouseX, mouseY, partialTick);
        renderTooltip(graphics, mouseX, mouseY);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (isMouseOverTextArea(mouseX, mouseY)) {
            cursorPosition = currentText.length();
            return true;
        }

        // Прокрутка
        if (mouseX >= this.leftPos + 87 && mouseX <= this.leftPos + 168 &&
                mouseY >= this.topPos + 6 && mouseY <= this.topPos + 77) {
            isScrolling = true;
            return true;
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
            this.minecraft.player.closeContainer();
            return true;
        }

        switch (keyCode) {
            case GLFW.GLFW_KEY_ENTER:
            case GLFW.GLFW_KEY_KP_ENTER:
                insertText("\n");
                return true;

            case GLFW.GLFW_KEY_BACKSPACE:
                handleBackspace();
                return true;

            case GLFW.GLFW_KEY_DELETE:
                handleDelete();
                return true;

            case GLFW.GLFW_KEY_LEFT:
                if (cursorPosition > 0) cursorPosition--;
                return true;

            case GLFW.GLFW_KEY_RIGHT:
                if (cursorPosition < currentText.length()) cursorPosition++;
                return true;

            case GLFW.GLFW_KEY_UP:
                scrollOffset = Mth.clamp(scrollOffset - 1, 0, maxLines);
                return true;

            case GLFW.GLFW_KEY_DOWN:
                scrollOffset = Mth.clamp(scrollOffset + 1, 0, maxLines);
                return true;

            case GLFW.GLFW_KEY_HOME:
                cursorPosition = 0;
                return true;

            case GLFW.GLFW_KEY_END:
                cursorPosition = currentText.length();
                return true;
        }

        return false;
    }

    @Override
    public boolean charTyped(char codePoint, int modifiers) {
        // Ввод обычных символов
        if (codePoint >= 32 && codePoint != 127) {
            insertText(String.valueOf(codePoint));
            return true;
        }
        return false;
    }
    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        if (isMouseOverTextArea(mouseX, mouseY) && maxLines > 0) {
            scrollOffset = Mth.clamp(scrollOffset - delta * 3, 0, maxLines);
            return true;
        }
        return super.mouseScrolled(mouseX, mouseY, delta);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (isScrolling && maxLines > 0) {
            double scrollAreaHeight = 120;
            double relativeY = mouseY - (this.topPos + 20);
            double scrollPercent = Mth.clamp(relativeY / scrollAreaHeight, 0.0, 1.0);
            scrollOffset = scrollPercent * maxLines;
            return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        isScrolling = false;
        return super.mouseReleased(mouseX, mouseY, button);
    }
}