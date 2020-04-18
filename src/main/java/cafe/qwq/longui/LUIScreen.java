package cafe.qwq.longui;

import cafe.qwq.webcraft.api.View;
import cafe.qwq.webcraft.api.WebScreen;
import cafe.qwq.webcraft.api.math.Vec4i;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.client.Minecraft;
import net.minecraft.util.text.StringTextComponent;

import java.util.HashMap;
import java.util.Map;

public class LUIScreen extends WebScreen
{
    final View luiView;
    
    Map<String, String> packetReceiverMap = new HashMap<>();
    
    public LUIScreen(GuiConfig config)
    {
        this(config.name, config.url, config.drawBackground, config.shouldCloseOnEsc);
    }
    
    public LUIScreen(String name, String url, boolean drawBackground, boolean shouldCloseOnEsc)
    {
        super(new StringTextComponent(name));
        setShouldCloseOnEsc(shouldCloseOnEsc);
        luiView = new View();
        luiView.addJSFuncWithCallback("setMCTitle", JSFunctions::setMCTitle);
        luiView.addJSFuncWithCallback("getServerInfo", new JSFunctions.GetServerInfoFunc(this));
        luiView.addJSFuncWithCallback("connectToServer", JSFunctions::connectToServer);
        luiView.addJSFuncWithCallback("openLUIGui", JSFunctions::openGui);
        luiView.addJSFuncWithCallback("openModsGui", JSFunctions::openModsGui);
        luiView.addJSFuncWithCallback("openOptionsGui", JSFunctions::openOptionsGui);
        luiView.addJSFuncWithCallback("shutdownMC", JSFunctions::shutdownMC);
        luiView.addJSFuncWithCallback("openWorldSelectionGui", JSFunctions::openWorldSelectionGui);
        luiView.addJSFuncWithCallback("getPlayerInfo", JSFunctions::getPlayerInfo);
        luiView.addJSFuncWithCallback("closeGui", JSFunctions::closeGui);
        luiView.addJSFuncWithCallback("isInGame",JSFunctions::isInGame);
        if (Minecraft.getInstance().world != null)
        {
            luiView.addJSFuncWithCallback("sendPacket", JSFunctions::sendPacket);
            luiView.addJSFuncWithCallback("sendChatMessage", JSFunctions::sendChatMessage);
            luiView.addJSFuncWithCallback("addPacketReceiver", new JSFunctions.AddPacketReceiverFunc(this));
        }
        luiView.loadURL(url);
        luiView.setResizeCallback(vec -> new Vec4i(0, 0, vec.x, vec.y));
        luiView.addDOMReadyListener(() -> luiView.evaluteJS("luiScreenInit();"));
        addView(luiView);
        if (drawBackground)
        {
            addPreRenderer((x, y, p) -> renderBackground());
        }
    }
    
    public void removed()
    {
        luiView.evaluteJS("luiScreenRemoved();");
    }
    
    void onReceivePacket(JsonElement element)
    {
        JsonObject obj = element.getAsJsonObject();
        String plugin = obj.get("plugin").getAsString();
        if (packetReceiverMap.containsKey(plugin))
        {
            String callback = packetReceiverMap.get(plugin);
            luiView.evaluteJS(callback + "(" + obj.get("value").toString() + ")");
        }
    }
}
