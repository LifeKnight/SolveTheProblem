package com.lifeknight.solvetheproblem.mod;

import com.lifeknight.solvetheproblem.gui.LifeKnightGui;
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
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
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

@net.minecraftforge.fml.common.Mod(modid = Core.modId, name = Core.modName, version = Core.modVersion, clientSideOnly = true)
public class Core {
    public static final String
            modName = "SolveTheProblem",
            modVersion = "1.0.1",
            modId = "solvetheproblem";
    public static final EnumChatFormatting modColor = DARK_BLUE;
    public static final ExecutorService THREAD_POOL = Executors.newCachedThreadPool(new LifeKnightThreadFactory());
    public static GuiScreen guiToOpen = null;
    public static final KeyBinding disableModKeyBind = new KeyBinding("Disable SolveTheProblem", 0x2B, modName);
    public static final LifeKnightBoolean runMod = new LifeKnightBoolean("Mod", "Main", false);
    public static final LifeKnightBoolean gridSnapping = new LifeKnightBoolean("Grid Snapping", "HUD", true);
    public static final LifeKnightBoolean hudTextShadow = new LifeKnightBoolean("HUD Text Shadow", "HUD", true);
    public static final LifeKnightBoolean linkToGame = new LifeKnightBoolean("Link To Game", "Settings", true);
    public static final LifeKnightCycle problemDifficulty = new LifeKnightCycle("Problem Difficulty", "Settings", Arrays.asList(
            "Easy",
            "Intermediate",
            "Difficult",
            "More Difficult",
            "Quite Difficult"
    ));
    public static final LifeKnightCycle problemTypes = new LifeKnightCycle("Problem Types", "Settings", Arrays.asList(
            "Scramble & Math",
            "Scramble Only",
            "Math Only"
    ));
    public static final LifeKnightBoolean superimposeGui = new LifeKnightBoolean("Superimpose GUI", "Settings", false);
    public static final LifeKnightBoolean hardLock = new LifeKnightBoolean("Hard Lock", "Settings", false);
    public static final LifeKnightCycle waitType = new LifeKnightCycle("Wait Type", "Settings", Arrays.asList(
            "Timer",
            "Random")) {
        @Override
        public void onValueChange() {
            Core.onProblemClose();
        }
    };
    public static final LifeKnightNumber.LifeKnightInteger timerTime = new LifeKnightNumber.LifeKnightInteger("Timer Time", "Timer", 15, 5, 120);
    public static final LifeKnightBoolean showTimer = new LifeKnightBoolean("Show Timer", "Timer", true);
    public static final LifeKnightNumber.LifeKnightFloat problemChance = new LifeKnightNumber.LifeKnightFloat("Problem Chance", "Random", 0.05F, 0.001F, 0.99F);
    public static final LifeKnightNumber.LifeKnightInteger checkInterval = new LifeKnightNumber.LifeKnightInteger("Check Interval", "Random", 10, 1, 60);
    private static boolean onHypixel = false;
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
    public void preInitialize(FMLPreInitializationEvent event) {
        THREAD_POOL.submit(this::gatherNouns);
    }

    @EventHandler
    public void initialize(FMLInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(this);
        ClientRegistry.registerKeyBinding(disableModKeyBind);
        ClientCommandHandler.instance.registerCommand(new ModCommand());

        problemChance.setiCustomDisplayString(objects -> {
            float value = (float) objects[0];
            return problemChance.getName() + ": " + value * 100 + "%";
        });

        problemTimer = new com.lifeknight.solvetheproblem.utilities.Timer(timerTime.getValue());
        problemTimer.onEnd(Core::onTimerEnd);

        new EnhancedHudText("ProblemTimer") {
            @Override
            public String getTextToDisplay() {
                return Text.formatTimeFromMilliseconds(problemTimer.getTotalMilliseconds(), 2);
            }

            @Override
            public boolean isVisible() {
                return waitType.getValue() == 0 && showTimer.getValue();
            }
        };

        configuration = new Configuration();
    }

    @SubscribeEvent
    public void onWorldLoad(WorldEvent.Load event) {
        if (runMod.getValue() && !linkToGame.getValue()) onProblemClose();
    }

    @SubscribeEvent
    public void onChatMessage(ClientChatReceivedEvent event) {
        if (!onHypixel) return;
        String message = Text.removeFormattingCodes(event.message.getFormattedText());

        if (!message.contains(":") && message.toLowerCase().startsWith("cages opened!") || message.toLowerCase().contains("protect your bed")) {
            onProblemClose();
        }
    }

    @SubscribeEvent
    public void onConnect(final FMLNetworkEvent.ClientConnectedToServerEvent event) {
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                Chat.sendQueuedChatMessages();
                onHypixel = !Minecraft.getMinecraft().isSingleplayer() && Minecraft.getMinecraft().getCurrentServerData().serverIP.toLowerCase().contains("hypixel.net");
            }
        }, 1000);
    }

    public static void onTimerEnd() {
        if (waitType.getValue() == 0) {
            displayProblem();
        }
        problemTimer.stop();
    }

    public static void displayProblem() {
        if (Minecraft.getMinecraft().thePlayer != null && (Minecraft.getMinecraft().inGameHasFocus || (superimposeGui.getValue() && !(Minecraft.getMinecraft().currentScreen instanceof ProblemGui) && !(Minecraft.getMinecraft().currentScreen instanceof LifeKnightGui))) && runMod.getValue())
            openGui(new ProblemGui());
    }

    public static void onProblemClose() {
        if (waitType.getValue() == 0) {
            problemTimer.reset();
            problemTimer.setTimeFromSeconds(timerTime.getValue());
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
        if (event.phase == TickEvent.Phase.END && runMod.getValue()) Manipulable.renderManipulables();
    }

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        if (Minecraft.getMinecraft().thePlayer == null) return;
        if (event.phase == TickEvent.Phase.END) {
            ticksSinceClose++;
        }

        if (linkToGame.getValue()) {
            if (Minecraft.getMinecraft().thePlayer.capabilities.allowFlying) {
                if (problemTimer.isRunning()) problemTimer.pause();
            }
            return;
        }

        if (((Minecraft.getMinecraft().inGameHasFocus || (superimposeGui.getValue() && !(Minecraft.getMinecraft().currentScreen instanceof ProblemGui) && !(Minecraft.getMinecraft().currentScreen instanceof LifeKnightGui))) && runMod.getValue())) {
            if (waitType.getValue() == 0 && !problemTimer.isRunning()) {
                new Timer().schedule(new TimerTask() {
                    @Override
                    public void run() {
                        if (!problemTimer.isRunning()) {
                            onProblemClose();
                        }
                    }
                }, 50);
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
        if (runMod.getValue() && Keyboard.isKeyDown(disableModKeyBind.getKeyCode())) {
            runMod.toggle();
            Chat.addChatMessage(runMod.getAsString());
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
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }
}