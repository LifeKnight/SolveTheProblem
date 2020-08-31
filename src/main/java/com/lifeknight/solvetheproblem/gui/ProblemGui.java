package com.lifeknight.solvetheproblem.gui;

import com.lifeknight.solvetheproblem.gui.components.LifeKnightButton;
import com.lifeknight.solvetheproblem.gui.components.LifeKnightTextField;
import com.lifeknight.solvetheproblem.mod.Core;
import com.lifeknight.solvetheproblem.utilities.Miscellaneous;
import com.lifeknight.solvetheproblem.utilities.Text;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.lifeknight.solvetheproblem.mod.Core.*;
import static com.lifeknight.solvetheproblem.utilities.Video.getScaledHeight;
import static net.minecraft.util.EnumChatFormatting.DARK_BLUE;
import static net.minecraft.util.EnumChatFormatting.RED;

public class ProblemGui extends GuiScreen {
    private String problem = "";
    private String problem2 = "";
    private String problem3 = "";
    private String message = "";
    private boolean isButtonProblem;
    private String answer;
    private String alternateAnswer;
    private int attempts = 0;
    private Long endTime = null;
    private LifeKnightTextField textField;
    private final List<LifeKnightButton> answerButtons = new ArrayList<>();
    private LifeKnightButton enterButton;

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        if (endTime == null) {
            this.drawCenteredString(fontRendererObj, DARK_BLUE + "Problem", this.width / 2, 5, 0xffffffff);
            this.drawCenteredString(fontRendererObj, problem, this.width / 2, getScaledHeight(50), 0xffffffff);
            this.drawCenteredString(fontRendererObj, problem2, this.width / 2, getScaledHeight(50) + 12, 0xffffffff);
            this.drawCenteredString(fontRendererObj, problem3, this.width / 2, getScaledHeight(50) + 24, 0xffffffff);
            this.drawString(fontRendererObj, message, 5, 5, 0xffffffff);
            this.drawString(fontRendererObj, RED + "Attempts: " + attempts, 5, 30, 0xffffffff);
            textField.drawTextBox();
        } else {
            if (endTime - System.currentTimeMillis() <= 0) {
                this.close();
                return;
            }
            
            this.drawCenteredString(fontRendererObj, RED + "Too many incorrect attempts.", this.width / 2, 20, 0xffffffff);
            this.drawCenteredString(fontRendererObj, Text.formatTimeFromMilliseconds(endTime - System.currentTimeMillis(), 2), this.width / 2, 40, 0xffffffff);
        }
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    public void initGui() {
        answerButtons.clear();
        textField = new LifeKnightTextField(0, super.width / 2 - 50, getScaledHeight(140), 100, 21, "Unscramble the word.") {
            @Override
            public void handleInput() {
                this.lastInput = this.getText();
                this.setText("");
                checkForAnswer(this.lastInput);
            }
        };

        enterButton = new LifeKnightButton(">", 1, this.width / 2 + 55, getScaledHeight(142), 20) {
            @Override
            public void work() {
                textField.handleInput();
            }
        };

        answerButtons.add(new LifeKnightButton("", 2, this.width / 2 - 75, getScaledHeight(super.height / 2), 150) {
            @Override
            public void work() {
                checkForAnswer(this.displayString);
            }
        });

        answerButtons.add(new LifeKnightButton("", 3, this.width / 2 - 75, getScaledHeight(super.height / 2 + 50), 150) {
            @Override
            public void work() {
                checkForAnswer(this.displayString);
            }
        });

        answerButtons.add(new LifeKnightButton("", 4, this.width / 2 - 75, getScaledHeight(super.height / 2 + 100), 150) {
            @Override
            public void work() {
                checkForAnswer(this.displayString);
            }
        });

        answerButtons.add(new LifeKnightButton("", 5, this.width / 2 - 75, getScaledHeight(super.height / 2 + 150), 150) {
            @Override
            public void work() {
                checkForAnswer(this.displayString);
            }
        });

        super.buttonList.add(enterButton);
        super.buttonList.addAll(answerButtons);
        generateProblem();
    }
    
    private void close() {
        this.mc.displayGuiScreen(null);
        if (this.mc.currentScreen == null) this.mc.setIngameFocus();
        onGuiClosed();
    }

    public void checkForAnswer(String input) {
        input = input.replace(" ", "");
        if (input.equalsIgnoreCase(answer.replace(" ", "")) || (!input.isEmpty() && input.equalsIgnoreCase(alternateAnswer.replace(" ", "")))) {
            this.close();
            return;
        }
        message = RED + "INCORRECT: " + answer + (!alternateAnswer.isEmpty() ? " OR " + alternateAnswer : "");
        attempts++;
        
        if (attempts >= 3) {
            endTime = System.currentTimeMillis() + (problemDifficulty.getValue() + 1L) * 3L * 1000L;
            this.buttonList.clear();
        } else generateProblem();
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
        if (keyCode == 1 && (!hardLock.getValue()) || !runMod.getValue()) {
            this.close();
            return;
        }
        textField.textboxKeyTyped(typedChar, keyCode);
        if (isButtonProblem) {
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
        Core.onProblemClose();
    }

    private void generateProblem() {
        isButtonProblem = false;
        for (LifeKnightButton lifeKnightButton : answerButtons) {
            lifeKnightButton.visible = false;
        }
        enterButton.visible = false;
        textField.setVisible(false);
        alternateAnswer = "";
        problem2 = "";
        problem3 = "";

        boolean scramble = Miscellaneous.getRandomTrueOrFalse();

        if (problemTypes.getValue() == 1) {
            scramble = true;
        } else if (problemTypes.getValue() == 2) {
            scramble = false;
        }


        if (!scramble) {
            for (LifeKnightButton lifeKnightButton : answerButtons) {
                lifeKnightButton.visible = true;
            }
            isButtonProblem = true;
            createMathProblem();
            return;
        }

        answer = Miscellaneous.getRandomElement(nounsListList.get(problemDifficulty.getValue()));
        problem = "Unscramble: " + Text.scramble(answer);
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
        int x;
        int y;
        int answerValue;
        boolean intermediate = problemDifficulty.getValue() == 1;
        switch (Miscellaneous.getRandomIntBetweenRange(0, 2)) {
            case 0: {
                x = intermediate ? Miscellaneous.getRandomIntBetweenRange(25, 100) : Miscellaneous.getRandomIntBetweenRange(5, 20);
                y = intermediate ? Miscellaneous.getRandomIntBetweenRange(25, 100) : Miscellaneous.getRandomIntBetweenRange(5, 20);
                answerValue = x + y;
                problem = x + " + " + y;
                answer = String.valueOf(answerValue);
                answerButtons.get(correctIndex).displayString = answer;
                break;
            }
            case 1: {
                x = intermediate ? Miscellaneous.getRandomIntBetweenRange(25, 100) : Miscellaneous.getRandomIntBetweenRange(5, 20);
                y = intermediate ? Miscellaneous.getRandomIntBetweenRange(25, 100) : Miscellaneous.getRandomIntBetweenRange(5, 20);
                answerValue = x - y;
                problem = x + " - " + y;
                answer = String.valueOf(answerValue);
                answerButtons.get(correctIndex).displayString = answer;
                break;
            }
            default: {
                x = intermediate ? Miscellaneous.getRandomIntBetweenRange(7, 15) : Miscellaneous.getRandomIntBetweenRange(5, 20);
                y = intermediate ? Miscellaneous.getRandomIntBetweenRange(7, 15) : Miscellaneous.getRandomIntBetweenRange(5, 20);
                answerValue = x * y;
                problem = x + " * " + y;
                answer = String.valueOf(answerValue);
                answerButtons.get(correctIndex).displayString = answer;
                break;
            }
        }
        if (intermediate) {
            int[] toAddArray = {
                    -10,
                    10,
                    Miscellaneous.getRandomTrueOrFalse() ? 20 : -20
            };
            List<Integer> usedIndexes = new ArrayList<>();
            for (int i = 0; i < 4; i++) {
                if (i != correctIndex) {
                    int toAdd = toAddArray[Miscellaneous.getRandomIntegerThatIsntAnother(0, 3, usedIndexes)];
                    answerButtons.get(i).displayString = String.valueOf(x + toAdd);
                }
            }
        } else {
            for (int i = 0; i < 4; i++) {
                if (i != correctIndex) {
                    int toAdd = Miscellaneous.getRandomTrueOrFalse() ?
                            Miscellaneous.getRandomIntBetweenRange(1, 5) : Miscellaneous.getRandomIntBetweenRange(-5, -1);
                    answerButtons.get(i).displayString = String.valueOf(x + toAdd);
                }
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
            }
            case 1: {
                int a = Miscellaneous.getRandomTrueOrFalse() ?
                        Miscellaneous.getRandomIntBetweenRange(-12, -3) : Miscellaneous.getRandomIntBetweenRange(3, 12);
                rightValue = x - a;
                newProblem.append("x").append(" - ").append(a);
                break;
            }
            default: {
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
                }
                case 1: {
                    int a = Miscellaneous.getRandomTrueOrFalse() ?
                            Miscellaneous.getRandomIntBetweenRange(-12, -3) : Miscellaneous.getRandomIntBetweenRange(3, 12);
                    rightValue += x - a;
                    newProblem.append(" + x - ").append(a);
                    break;
                }
                default: {
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
        int correctIndex = Miscellaneous.getRandomIntBetweenRange(0, 3);

        int random = Miscellaneous.getRandomIntBetweenRange(0, 2);

        if (random == 0 || random == 1) {
            for (LifeKnightButton lifeKnightButton : answerButtons) {
                lifeKnightButton.visible = false;
            }
            isButtonProblem = false;
            textField.setVisible(true);
            enterButton.visible = true;
        }

        if (random == 0) {
            int a = Miscellaneous.getRandomIntBetweenRange(1, 7);
            if (Miscellaneous.getRandomTrueOrFalse()) a = -a;
            int b = Miscellaneous.getRandomIntBetweenRange(1, 7);
            if (Miscellaneous.getRandomTrueOrFalse()) b = -b;
            // Factor
            // x^2 + ax + bx + ab
            String abSum = a + b < 0 ? ("- " + Math.abs(a + b)) : ("+ " + (a + b));
            String abProduct = a * b < 0 ? ("- " + Math.abs(a * b)) : ("+ " + (a * b));
            problem = "Factor: x^2 " + abSum + "x " + abProduct;
            String aString = a < 0 ? "- " + Math.abs(a) : "+ " + a;
            String bString = b < 0 ? "- " + Math.abs(b) : "+ " + b;
            answer = "(x " + aString + ")(x " + bString + ")";
            alternateAnswer = "(x " + bString + ")(x " + aString + ")";
        } else if (random == 1) {
            int a = Miscellaneous.getRandomIntBetweenRange(1, 7);
            if (Miscellaneous.getRandomTrueOrFalse()) a = -a;
            int b = Miscellaneous.getRandomIntBetweenRange(1, 7);
            if (Miscellaneous.getRandomTrueOrFalse()) b = -b;
            // Factor
            // x^2 + ax + bx + ab
            String abSum = a + b < 0 ? ("- " + Math.abs(a + b)) : ("+ " + (a + b));
            String abProduct = a * b < 0 ? ("- " + Math.abs(a * b)) : ("+ " + (a * b));
            String aString = a < 0 ? "- " + Math.abs(a) : "+ " + a;
            String bString = b < 0 ? "- " + Math.abs(b) : "+ " + b;
            problem = "Expand: (x " + aString + ")(x " + bString + ")";
            answer = "x^2 " + abSum + "x " + abProduct;
            if (a + b == 0) alternateAnswer = "x^2 " + abProduct;
        } else {
            int x = Miscellaneous.getRandomIntBetweenRange(-7, 7);
            int y = Miscellaneous.getRandomIntBetweenRange(-7, 7);
            int z = Miscellaneous.getRandomIntBetweenRange(-7, 7);
            int a = Miscellaneous.getRandomIntBetweenRange(-7, 7);
            int b = Miscellaneous.getRandomIntBetweenRange(-7, 7);
            int c = Miscellaneous.getRandomIntBetweenRange(-7, 7);
            int d = Miscellaneous.getRandomIntBetweenRange(-7, 7);
            int f = Miscellaneous.getRandomIntBetweenRange(-7, 7);
            int g = Miscellaneous.getRandomIntBetweenRange(-7, 7);
            int h = Miscellaneous.getRandomIntBetweenRange(-7, 7);
            int j = Miscellaneous.getRandomIntBetweenRange(-7, 7);
            int k = Miscellaneous.getRandomIntBetweenRange(-7, 7);


            int firstRightValue;
            int secondRightValue;
            int thirdRightValue;

            switch (Miscellaneous.getRandomIntBetweenRange(0, 2)) {
                case 0:
                    firstRightValue = a * x + b * y + c * z;
                    problem = a + "x + " + b + "y + " + c + "z = " + firstRightValue;
                    break;
                case 1:
                    firstRightValue = b * y - c * z + a * x;
                    problem = b + "y + " + c + "z + " + a + "x = " + firstRightValue;
                    break;
                case 2:
                    firstRightValue = c * z + b * y - a * x;
                    problem = c + "z + " + b + "y - " + a + "x = " + firstRightValue;
                    break;
            }

            switch (Miscellaneous.getRandomIntBetweenRange(0, 2)) {
                case 0:
                    secondRightValue = d * x + f * y + g * z;
                    problem2 = d + "x + " + f + "y + " + g + "z = " + secondRightValue;
                    break;
                case 1:
                    secondRightValue = f * y - g * z + d * x;
                    problem2 = f + "y + " + g + "z + " + d + "x = " + secondRightValue;
                    break;
                case 2:
                    secondRightValue = g * z + f * y - d * x;
                    problem2 = g + "z + " + f + "y - " + d + "x = " + secondRightValue;
                    break;
            }

            switch (Miscellaneous.getRandomIntBetweenRange(0, 2)) {
                case 0:
                    thirdRightValue = h * x + j * y + k * z;
                    problem3 = h + "x + " + j + "y + " + k + "z = " + thirdRightValue;
                    break;
                case 1:
                    thirdRightValue = j * y - k * z + h * x;
                    problem3 = j + "y + " + k + "z + " + h + "x = " + thirdRightValue;
                    break;
                case 2:
                    thirdRightValue = k * z + j * y - h * x;
                    problem3 = k + "z + " + j + "y - " + h + "x = " + thirdRightValue;
                    break;
            }

            answer = "x = {x}; y = {y}; z = {z}";
            for (int i = 0; i < 4; i++) {
                if (i != correctIndex) {
                    int newX = x + (Miscellaneous.getRandomTrueOrFalse() ? Miscellaneous.getRandomIntBetweenRange(1, 3) :
                            Miscellaneous.getRandomIntBetweenRange(-3, 1));
                    int newY = y + (Miscellaneous.getRandomTrueOrFalse() ? Miscellaneous.getRandomIntBetweenRange(1, 3) :
                            Miscellaneous.getRandomIntBetweenRange(-3, 1));
                    int newZ = z + (Miscellaneous.getRandomTrueOrFalse() ? Miscellaneous.getRandomIntBetweenRange(1, 3) :
                            Miscellaneous.getRandomIntBetweenRange(-3, 1));
                    answerButtons.get(i).displayString = answer.replace("{x}", String.valueOf(newX)).replace("{y}", String.valueOf(newY)).replace("{z}", String.valueOf(newZ));
                }
            }
            answer = "x = " + x + "; y = " + y + "; z = " + z;
            answerButtons.get(correctIndex).displayString = answer;
        }
    }
}
