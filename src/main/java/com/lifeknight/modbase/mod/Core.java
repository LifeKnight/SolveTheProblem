package com.lifeknight.modbase.mod;

import com.lifeknight.modbase.gui.Manipulable;
import com.lifeknight.modbase.gui.hud.EnhancedHudText;
import com.lifeknight.modbase.utilities.Chat;
import com.lifeknight.modbase.utilities.Miscellaneous;
import com.lifeknight.modbase.variables.LifeKnightBoolean;
import com.lifeknight.modbase.variables.LifeKnightCycle;
import com.lifeknight.modbase.variables.LifeKnightNumber;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;

import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static net.minecraft.util.EnumChatFormatting.DARK_BLUE;

@net.minecraftforge.fml.common.Mod(modid = Core.modId, name = Core.modName, version = Core.modVersion, clientSideOnly = true)
public class Core {
    public static final String
            modName = "SolveTheProblem",
            modVersion = "1.0",
            modId = "solvetheproblem";
    public static final EnumChatFormatting modColor = DARK_BLUE;
    public static final ExecutorService THREAD_POOL = Executors.newCachedThreadPool(new LifeKnightThreadFactory());
    public static boolean onHypixel = false;
    public static GuiScreen guiToOpen = null;
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
    public static final LifeKnightBoolean hardLock = new LifeKnightBoolean("Hard Lock", "Settings", false);
    public static final LifeKnightCycle waitType = new LifeKnightCycle("Wait Type", "Settings", Arrays.asList(
            "Timer",
            "Random"));
    public static final LifeKnightNumber.LifeKnightInteger timerTime = new LifeKnightNumber.LifeKnightInteger("Timer Time", "Timer", 60, 5, 300);
    public static final LifeKnightBoolean showTimer = new LifeKnightBoolean("Show Timer", "Timer", true);
    public static final LifeKnightNumber.LifeKnightFloat problemChance = new LifeKnightNumber.LifeKnightFloat("Problem Chance", "Random", 0.05F, 0.001F, 0.99F);
    public static final LifeKnightNumber.LifeKnightInteger checkInterval = new LifeKnightNumber.LifeKnightInteger("Check Interval", "Random", 10, 1, 60);
    public static com.lifeknight.modbase.utilities.Timer problemTimer = new com.lifeknight.modbase.utilities.Timer(99999);
    public static Configuration configuration;

    @EventHandler
    public void init(FMLInitializationEvent initEvent) {
        MinecraftForge.EVENT_BUS.register(this);
        ClientCommandHandler.instance.registerCommand(new ModCommand());

        Miscellaneous.createEnhancedHudTextDefaultPropertyVariables();

        problemChance.setiCustomDisplayString(objects -> {
            float value = (float) objects[0];
            return problemChance.getName() + ": " + value * 100 + "%";
        });

        problemTimer.onEnd(this::onTimerEnd);

        new EnhancedHudText("Timer") {
            @Override
            public String getTextToDisplay() {
                return problemTimer.getFormattedTime();
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
                onHypixel = !Minecraft.getMinecraft().isSingleplayer() && Minecraft.getMinecraft().getCurrentServerData().serverIP.toLowerCase().contains("hypixel.net");
            }
        }, 1000);
    }

    @SubscribeEvent
    public void onChatMessageReceived(ClientChatReceivedEvent event) {

    }

    public void onTimerEnd() {
        if (runMod.getValue() && waitType.getValue() == 0) {

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

        }
    }

    public static void openGui(GuiScreen guiScreen) {
        guiToOpen = guiScreen;
    }
}