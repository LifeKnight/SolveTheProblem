package com.lifeknight.solvetheproblem.gui;

import com.lifeknight.solvetheproblem.gui.components.LifeKnightButton;
import com.lifeknight.solvetheproblem.gui.components.LifeKnightTextField;
import com.lifeknight.solvetheproblem.mod.Mod;
import com.lifeknight.solvetheproblem.utilities.Miscellaneous;
import com.lifeknight.solvetheproblem.utilities.Text;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.lifeknight.solvetheproblem.mod.Mod.*;
import static com.lifeknight.solvetheproblem.utilities.Video.getScaledHeight;
import static net.minecraft.util.EnumChatFormatting.DARK_BLUE;
import static net.minecraft.util.EnumChatFormatting.RED;

public class ProblemGui extends GuiScreen {
    private String problem = "";
    private String problem2 = "";
    private String message = "";
    private boolean isMathProblem;
    private String answer;
    private LifeKnightTextField textField;
    private final List<LifeKnightButton> answerButtons = new ArrayList<>();
    private LifeKnightButton enterButton;

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        this.drawCenteredString(fontRendererObj, DARK_BLUE + "Problem", this.width / 2, 5, 0xffffffff);
        this.drawCenteredString(fontRendererObj, problem, this.width / 2, getScaledHeight(50), 0xffffffff);
        this.drawCenteredString(fontRendererObj, problem2, this.width / 2, getScaledHeight(50) + 12, 0xffffffff);
        this.drawString(fontRendererObj, message, 5, 5, 0xffffffff);
        textField.drawTextBoxAndName();
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    public void initGui() {
        super.buttonList.clear();
        textField = new LifeKnightTextField(0, super.width / 2 - 50, getScaledHeight(100), 100, 24, "Unscramble the word.") {
            @Override
            public void handleInput() {
                this.lastInput = this.getText();
                this.setText("");
                checkForAnswer(this.lastInput);
            }
        };

        enterButton = new LifeKnightButton(">", 1, this.width / 2 + 105, getScaledHeight(102), 20) {
            @Override
            public void work() {
                textField.handleInput();
            }
        };

        answerButtons.add(new LifeKnightButton("", 2, this.width / 2 - 50, getScaledHeight(super.height / 2), 100) {
            @Override
            public void work() {
                checkForAnswer(this.displayString);
            }
        });

        answerButtons.add(new LifeKnightButton("", 3, this.width / 2 - 50, getScaledHeight(super.height / 2 + 50), 100) {
            @Override
            public void work() {
                checkForAnswer(this.displayString);
            }
        });

        answerButtons.add(new LifeKnightButton("", 4, this.width / 2 - 50, getScaledHeight(super.height / 2 + 100), 100) {
            @Override
            public void work() {
                checkForAnswer(this.displayString);
            }
        });

        answerButtons.add(new LifeKnightButton("", 5, this.width / 2 - 50, getScaledHeight(super.height / 2 + 150), 100) {
            @Override
            public void work() {
                checkForAnswer(this.displayString);
            }
        });

        super.buttonList.add(enterButton);
        super.buttonList.addAll(answerButtons);
        generateProblem();
    }

    public void checkForAnswer(String input) {
        if (input.equalsIgnoreCase(answer)) {
            this.mc.displayGuiScreen(null);
            if (this.mc.currentScreen == null) this.mc.setIngameFocus();
            onGuiClosed();
            return;
        }
        message = RED + "INCORRECT: " + answer;
        generateProblem();
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        ((LifeKnightButton) button).work();
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        textField.mouseClicked(mouseX, mouseY, mouseButton);
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if (keyCode == 1 && !hardLock.getValue()) {
            this.mc.displayGuiScreen(null);
            if (this.mc.currentScreen == null) this.mc.setIngameFocus();
            onGuiClosed();
            return;
        }
        textField.textboxKeyTyped(typedChar, keyCode);
        if (isMathProblem) {
            if (keyCode == 0x02) answerButtons.get(0).work();
            if (keyCode == 0x03) answerButtons.get(1).work();
            if (keyCode == 0x04) answerButtons.get(2).work();
            if (keyCode == 0x05) answerButtons.get(3).work();
        }
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    @Override
    public void onGuiClosed() {
        Mod.onProblemClose();
    }

    private void generateProblem() {
        isMathProblem = false;
        for (LifeKnightButton lifeKnightButton : answerButtons) {
            lifeKnightButton.visible = false;
        }
        enterButton.visible = false;
        textField.setVisible(false);
        problem2 = "";

        boolean scramble = Miscellaneous.getRandomTrueOrFalse();

        if (!mathProblems.getValue()) {
            scramble = true;
        }

        if (!scramble) {
            for (LifeKnightButton lifeKnightButton : answerButtons) {
                lifeKnightButton.visible = true;
            }
            isMathProblem = true;
            createMathProblem();
            return;
        }

        answer = Miscellaneous.getRandomElement(nounsListList.get(problemDifficulty.getValue()));
        problem = Text.scramble(answer);
        textField.setVisible(true);
        enterButton.visible = true;
    }

    private void createMathProblem() {
        switch (problemDifficulty.getValue()) {
            case 0:
            case 1:
                createEasyMathProblem();
                break;
            case 2:
                createDifficultMathProblem();
                break;
            case 3:
                createVeryDifficultMathProblem();
                break;
            case 4:
                createExtremelyDifficultMathProblem();
                break;
        }
    }

    private void createEasyMathProblem() {
        int correctIndex = Miscellaneous.getRandomIntBetweenRange(0, 3);
        int answerValue;
        boolean intermediate = problemDifficulty.getValue() == 1;
        switch (Miscellaneous.getRandomIntBetweenRange(0, 2)) {
            case 0: {
                int x = intermediate ? Miscellaneous.getRandomIntBetweenRange(25, 100) : Miscellaneous.getRandomIntBetweenRange(5, 20);
                int y = intermediate ? Miscellaneous.getRandomIntBetweenRange(25, 100) : Miscellaneous.getRandomIntBetweenRange(5, 20);
                answerValue = x + y;
                problem = x + " + " + y;
                answer = String.valueOf(answerValue);
                answerButtons.get(correctIndex).displayString = answer;
                break;
            }
            case 1: {
                int x = intermediate ? Miscellaneous.getRandomIntBetweenRange(25, 100) : Miscellaneous.getRandomIntBetweenRange(5, 20);
                int y = intermediate ? Miscellaneous.getRandomIntBetweenRange(25, 100) : Miscellaneous.getRandomIntBetweenRange(5, 20);
                answerValue = x - y;
                problem = x + " - " + y;
                answer = String.valueOf(answerValue);
                answerButtons.get(correctIndex).displayString = answer;
                break;
            }
            default: {
                int x = intermediate ? Miscellaneous.getRandomIntBetweenRange(7, 15) : Miscellaneous.getRandomIntBetweenRange(5, 20);
                int y = intermediate ? Miscellaneous.getRandomIntBetweenRange(7, 15) : Miscellaneous.getRandomIntBetweenRange(5, 20);
                answerValue = x * y;
                problem = x + " * " + y;
                answer = String.valueOf(answerValue);
                answerButtons.get(correctIndex).displayString = answer;
                break;
            }
        }
        for (int i = 0; i < 4; i++) {
            if (i != correctIndex) {
                int toAdd = Miscellaneous.getRandomTrueOrFalse() ?
                        Miscellaneous.getRandomIntBetweenRange(1, 5) : Miscellaneous.getRandomIntBetweenRange(-5, -1);
                answerButtons.get(i).displayString = String.valueOf(answerValue + toAdd);
            }
        }
    }

    private void createDifficultMathProblem() {
        StringBuilder newProblem = new StringBuilder();
        int rightValue;
        int x = Miscellaneous.getRandomTrueOrFalse() ?
                Miscellaneous.getRandomIntBetweenRange(2, 7) : Miscellaneous.getRandomIntBetweenRange(-7, -2);
        switch (Miscellaneous.getRandomIntBetweenRange(0, 2)) {
            case 0: {
                int a = Miscellaneous.getRandomTrueOrFalse() ?
                        Miscellaneous.getRandomIntBetweenRange(-12, -3) : Miscellaneous.getRandomIntBetweenRange(3, 12);
                rightValue = x + a;
                newProblem.append("x").append(" + ").append(a);
                break;
            } case 1: {
                int a = Miscellaneous.getRandomTrueOrFalse() ?
                        Miscellaneous.getRandomIntBetweenRange(-12, -3) : Miscellaneous.getRandomIntBetweenRange(3, 12);
                rightValue = x - a;
                newProblem.append("x").append(" - ").append(a);
                break;
            } default: {
                int a = Miscellaneous.getRandomTrueOrFalse() ?
                        Miscellaneous.getRandomIntBetweenRange(-12, -3) : Miscellaneous.getRandomIntBetweenRange(3, 12);
                rightValue = x * a;
                newProblem.append(a).append("x");
                break;
            }
        }
        for (int i = 0; i < 2; i++) {
            switch (Miscellaneous.getRandomIntBetweenRange(0, 2)) {
                case 0: {
                    int a = Miscellaneous.getRandomTrueOrFalse() ?
                            Miscellaneous.getRandomIntBetweenRange(-12, -3) : Miscellaneous.getRandomIntBetweenRange(3, 12);
                    rightValue += x + a;
                    newProblem.append(" + x + ").append(a);
                    break;
                } case 1: {
                    int a = Miscellaneous.getRandomTrueOrFalse() ?
                            Miscellaneous.getRandomIntBetweenRange(-12, -3) : Miscellaneous.getRandomIntBetweenRange(3, 12);
                    rightValue += x - a;
                    newProblem.append(" + x - ").append(a);
                    break;
                } default: {
                    int a = Miscellaneous.getRandomTrueOrFalse() ?
                            Miscellaneous.getRandomIntBetweenRange(-12, -3) : Miscellaneous.getRandomIntBetweenRange(3, 12);
                    rightValue += x * a;
                    newProblem.append(" + ").append(a).append("x");
                    break;
                }
            }
        }
        int correctIndex = Miscellaneous.getRandomIntBetweenRange(0, 3);
        problem = newProblem.toString() + " = " + rightValue;
        answer = String.valueOf(x);
        answerButtons.get(correctIndex).displayString = answer;
        for (int i = 0; i < 4; i++) {
            if (i != correctIndex) {
                int toAdd = Miscellaneous.getRandomTrueOrFalse() ?
                        Miscellaneous.getRandomIntBetweenRange(1, 5) : Miscellaneous.getRandomIntBetweenRange(-5, -1);
                answerButtons.get(i).displayString = String.valueOf(x + toAdd);
            }
        }
    }

    private void createVeryDifficultMathProblem() {
        int x = Miscellaneous.getRandomIntBetweenRange(-7, 7);
        int y = Miscellaneous.getRandomIntBetweenRange(-7, 7);
        int a = Miscellaneous.getRandomIntBetweenRange(-7, 7);
        int b = Miscellaneous.getRandomIntBetweenRange(-7, 7);
        int c = Miscellaneous.getRandomIntBetweenRange(-7, 7);
        int d = Miscellaneous.getRandomIntBetweenRange(-7, 7);
        
        int firstRightValue;
        int secondRightValue;
        
        if (Miscellaneous.getRandomTrueOrFalse()) {
            firstRightValue = a * x + b * y;
            problem = a + "x + " + b + "y = " + firstRightValue;
        } else {
            firstRightValue = b * y - a * x;
            problem = a + "y - " + a + "x = " + firstRightValue;
        }
        
        if (Miscellaneous.getRandomTrueOrFalse()) {
            secondRightValue = c * x + d * y;
            problem2 = c + "x + " + d + "y = " + secondRightValue;
        } else {
            secondRightValue = d * y - c * x;
            problem2 = c + "y - " + c + "x = " + secondRightValue;
        }

        answer = "x = {x}; y = {y}";

        int correctIndex = Miscellaneous.getRandomIntBetweenRange(0, 3);
        for (int i = 0; i < 4; i++) {
            if (i != correctIndex) {
                int newX = x + (Miscellaneous.getRandomTrueOrFalse() ? Miscellaneous.getRandomIntBetweenRange(1, 3) :
                        Miscellaneous.getRandomIntBetweenRange(-3, 1));
                int newY = y + (Miscellaneous.getRandomTrueOrFalse() ? Miscellaneous.getRandomIntBetweenRange(1, 3) :
                        Miscellaneous.getRandomIntBetweenRange(-3, 1));
                answerButtons.get(i).displayString = answer.replace("{x}", String.valueOf(newX)).replace("{y}", String.valueOf(newY));
            }
        }
        answer = "x = " + x + "; y = " + y;
        answerButtons.get(correctIndex).displayString = answer;
    }

    private void createExtremelyDifficultMathProblem() {

    }
}
