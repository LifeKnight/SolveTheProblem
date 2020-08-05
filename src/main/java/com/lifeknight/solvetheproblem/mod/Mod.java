package com.lifeknight.solvetheproblem.mod;

import com.lifeknight.solvetheproblem.gui.Manipulable;
import com.lifeknight.solvetheproblem.gui.ProblemGui;
import com.lifeknight.solvetheproblem.gui.hud.EnhancedHudText;
import com.lifeknight.solvetheproblem.utilities.Chat;
import com.lifeknight.solvetheproblem.utilities.Miscellaneous;
import com.lifeknight.solvetheproblem.utilities.Text;
import com.lifeknight.solvetheproblem.variables.LifeKnightBoolean;
import com.lifeknight.solvetheproblem.variables.LifeKnightCycle;
import com.lifeknight.solvetheproblem.variables.LifeKnightNumber;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;
import org.lwjgl.input.Keyboard;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static net.minecraft.util.EnumChatFormatting.DARK_BLUE;

@net.minecraftforge.fml.common.Mod(modid = Mod.modId, name = Mod.modName, version = Mod.modVersion, clientSideOnly = true)
public class Mod {
    public static final String
            modName = "SolveTheProblem",
            modVersion = "1.0",
            modId = "solvetheproblem";
    public static final EnumChatFormatting modColor = DARK_BLUE;
    public static final ExecutorService THREAD_POOL = Executors.newCachedThreadPool(new LifeKnightThreadFactory());
    public static GuiScreen guiToOpen = null;
    private static final KeyBinding disableModKeyBind = new KeyBinding("Disable SolveTheProblem", 0x2B, "SolveTheProblem");
    public static final LifeKnightBoolean runMod = new LifeKnightBoolean("Mod", "Main", true);
    public static final LifeKnightBoolean gridSnapping = new LifeKnightBoolean("Grid Snapping", "HUD", true);
    public static final LifeKnightBoolean hudTextShadow = new LifeKnightBoolean("HUD Text Shadow", "HUD", true);
    public static final LifeKnightCycle problemDifficulty = new LifeKnightCycle("Problem Difficulty", "Settings", Arrays.asList(
            "Easy",
            "Intermediate",
            "Difficult",
            "Very Difficult",
            "Impossibly Difficult"
    ));
    public static final LifeKnightBoolean mathProblems = new LifeKnightBoolean("Math Problems", "Problems", true);
    public static final LifeKnightBoolean superimposeGui = new LifeKnightBoolean("Super Impose GUI", "Settings", false);
    public static final LifeKnightBoolean hardLock = new LifeKnightBoolean("Hard Lock", "Settings", false);
    public static final LifeKnightCycle waitType = new LifeKnightCycle("Wait Type", "Settings", Arrays.asList(
            "Timer",
            "Random")) {
        @Override
        public void onValueChange() {
            Mod.onProblemClose();
        }
    };
    public static final LifeKnightNumber.LifeKnightInteger timerTime = new LifeKnightNumber.LifeKnightInteger("Timer Time", "Timer", 60, 5, 300);
    public static final LifeKnightBoolean showTimer = new LifeKnightBoolean("Show Timer", "Timer", true);
    public static final LifeKnightNumber.LifeKnightFloat problemChance = new LifeKnightNumber.LifeKnightFloat("Problem Chance", "Random", 0.05F, 0.001F, 0.99F);
    public static final LifeKnightNumber.LifeKnightInteger checkInterval = new LifeKnightNumber.LifeKnightInteger("Check Interval", "Random", 10, 1, 60);
    public static com.lifeknight.solvetheproblem.utilities.Timer problemTimer;
    public static List<String> nounsLength4 = new ArrayList<>();
    public static List<String> nounsLength5 = new ArrayList<>();
    public static List<String> nounsLength6 = new ArrayList<>();
    public static List<String> nounsLength7 = new ArrayList<>();
    public static List<String> remainingNouns = new ArrayList<>();
    public static List<List<String>> nounsListList = Arrays.asList(
            nounsLength4,
            nounsLength5,
            nounsLength6,
            nounsLength7,
            remainingNouns
    );
    public static Configuration configuration;
    private static long ticksSinceClose = 0L;

    @EventHandler
    public void preInit(FMLPreInitializationEvent preInitializationEvent) {
        THREAD_POOL.submit(this::gatherNouns);
    }

    @EventHandler
    public void init(FMLInitializationEvent initEvent) {
        MinecraftForge.EVENT_BUS.register(this);
        ClientCommandHandler.instance.registerCommand(new ModCommand());

        problemChance.setiCustomDisplayString(objects -> {
            float value = (float) objects[0];
            return problemChance.getName() + ": " + value * 100 + "%";
        });

        new EnhancedHudText("ProblemTimer") {
            @Override
            public String getTextToDisplay() {
                return problemTimer != null ? problemTimer.getFormattedTime() : "";
            }

            @Override
            public boolean isVisible() {
                return runMod.getValue() && waitType.getValue() == 0 && showTimer.getValue();
            }
        };

        configuration = new Configuration();
    }

    @SubscribeEvent
    public void onConnect(final FMLNetworkEvent.ClientConnectedToServerEvent event) {
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                Chat.sendQueuedChatMessages();
            }
        }, 1000);
        onProblemClose();
    }

    public static void onTimerEnd() {
        if ((superimposeGui.getValue() || Minecraft.getMinecraft().inGameHasFocus) && runMod.getValue() && waitType.getValue() == 0) {
            displayProblem();
        }
        problemTimer.stop();
    }

    public static void displayProblem() {
        openGui(new ProblemGui());
    }

    public static void onProblemClose() {
        if (waitType.getValue() == 0) {
            problemTimer = new com.lifeknight.solvetheproblem.utilities.Timer(timerTime.getValue());
            problemTimer.onEnd(Mod::onTimerEnd);
            problemTimer.start();
        } else {
            ticksSinceClose = 0L;
        }
    }

    @SubscribeEvent
    public void onRenderTick(TickEvent.RenderTickEvent event) {
        if (guiToOpen != null) {
            Minecraft.getMinecraft().displayGuiScreen(guiToOpen);
            guiToOpen = null;
        }
        Manipulable.renderManipulables();
    }

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            ticksSinceClose++;
        }
        if ((superimposeGui.getValue() || Minecraft.getMinecraft().inGameHasFocus) && runMod.getValue()) {
            if (waitType.getValue() == 0 && !problemTimer.isRunning()) {
                onProblemClose();
            }
            if (waitType.getValue() == 1 && ticksSinceClose / 20 >= checkInterval.getValue()) {
                ticksSinceClose = 0L;
                int asInt = (int) (problemChance.getValue() * 100);
                int random = Miscellaneous.getRandomIntBetweenRange(1, 100);
                if (random <= asInt) {
                    displayProblem();
                }
            }
        }
    }

    @SubscribeEvent
    public void onKeyTyped(InputEvent.KeyInputEvent event) {
        if (Keyboard.isKeyDown(disableModKeyBind.getKeyCode())) {
            runMod.setValue(false);
            Chat.addSuccessMessage("Disabled mod.");
        }
    }

    public static void openGui(GuiScreen guiScreen) {
        guiToOpen = guiScreen;
    }

    private void gatherNouns() {
        try {
            URL url = new URL("http://www.desiquintans.com/downloads/nounlist/nounlist.txt");

            BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));

            String line;
            while ((line = in.readLine()) != null) {
                line = line.toLowerCase();
                if (line.equals(Text.removeAllPunctuation(line)) && line.length() > 3) {
                    char[] asChars = line.toCharArray();
                    StringBuilder result = new StringBuilder();
                    for (int i = 0; i < asChars.length; i++) {
                        if (i == 0) {
                            result.append(Character.toUpperCase(asChars[i]));
                        } else {
                            result.append(asChars[i]);
                        }
                    }
                    String noun = result.toString();
                    switch (noun.length()) {
                        case 4:
                            nounsListList.get(0).add(noun);
                            break;
                        case 5:
                            nounsListList.get(1).add(noun);
                            break;
                        case 6:
                            nounsListList.get(2).add(noun);
                            break;
                        case 7:
                            nounsListList.get(3).add(noun);
                            break;
                        default:
                            nounsListList.get(4).add(noun);
                            break;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}