package cafe.qwq.longui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.MainMenuScreen;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(LongUI.MODID)
public class LongUI
{
    public static final String MODID = "longui";
    private static GuiConfig mainMenuConfig;
    public static final Logger LOGGER = LogManager.getLogger();
    
    public LongUI()
    {
        MinecraftForge.EVENT_BUS.addListener(LongUI::onGUiOpen);
        mainMenuConfig = GuiConfig.loadGui("mods/longui/MainMenu.json");
    }
    
    public static void onGUiOpen(final GuiOpenEvent event)
    {
        if (event.getGui() instanceof MainMenuScreen)
        {
            if (mainMenuConfig != null)
            {
                LUIScreen screen = new LUIScreen(mainMenuConfig);
                event.setGui(screen);
            }
        }
    }
}
