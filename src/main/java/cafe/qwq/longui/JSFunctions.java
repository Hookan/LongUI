package cafe.qwq.longui;

import cafe.qwq.webcraft.api.View;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.ConnectingScreen;
import net.minecraft.client.gui.screen.OptionsScreen;
import net.minecraft.client.gui.screen.WorldSelectionScreen;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraftforge.fml.client.gui.screen.ModListScreen;
import org.lwjgl.glfw.GLFW;

import java.io.IOException;
import java.net.InetSocketAddress;

public class JSFunctions
{
    public static JsonElement setMCTitle(JsonElement element)
    {
        String title = element.getAsString();
        long hwnd = Minecraft.getInstance().getMainWindow().getHandle();
        GLFW.glfwSetWindowTitle(hwnd, title);
        return null;
    }
    
    public static class GetServerInfoFunc implements View.IJSFuncCallback
    {
        private final LUIScreen screen;
        
        public GetServerInfoFunc(LUIScreen screen)
        {
            this.screen = screen;
        }
        
        public JsonElement callback(JsonElement element)
        {
            JsonObject obj = element.getAsJsonObject();
            String host = obj.get("host").getAsString();
            short port = obj.get("port").getAsShort();
            String callback = obj.get("callback").getAsString();
            new Thread(() -> {
                ServerListPing17 ping = new ServerListPing17();
                ping.setAddress(new InetSocketAddress(host, port));
                try
                {
                    String response = ping.fetchData();
                    //System.out.println(response);
                    Minecraft.getInstance().enqueue(() -> {
                        if (Minecraft.getInstance().currentScreen == screen)
                        {
                            screen.luiView.evaluteJS(callback + "(" + response + ");");
                        }
                    });
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                    Minecraft.getInstance().enqueue(() -> {
                        if (Minecraft.getInstance().currentScreen == screen)
                        {
                            screen.luiView.evaluteJS(callback + "({ping: -1, error: \"" + e.getMessage() + "\"});");
                        }
                    });
                }
            }).start();
            return null;
        }
    }
    
    public static JsonElement connectToServer(JsonElement element)
    {
        JsonObject obj = element.getAsJsonObject();
        String name = obj.get("name").getAsString();
        String ip = obj.get("ip").getAsString();
        Minecraft mc = Minecraft.getInstance();
        mc.enqueue(() -> mc.displayGuiScreen(new ConnectingScreen(null, mc, new ServerData(name, ip, false))));
        return null;
    }
    
    public static JsonElement openGui(JsonElement element)
    {
        GuiConfig config = GuiConfig.loadGui(element);
        Minecraft.getInstance().enqueue(() -> {
            LUIScreen screen = new LUIScreen(config);
            Minecraft.getInstance().displayGuiScreen(screen);
        });
        return null;
    }
    
    public static JsonElement openOptionsGui(JsonElement element)
    {
        Minecraft mc = Minecraft.getInstance();
        mc.enqueue(() -> mc.displayGuiScreen(new OptionsScreen(mc.currentScreen, mc.gameSettings)));
        return null;
    }
    
    public static JsonElement openModsGui(JsonElement element)
    {
        Minecraft mc = Minecraft.getInstance();
        mc.enqueue(() -> mc.displayGuiScreen(new ModListScreen(mc.currentScreen)));
        return null;
    }
    
    public static JsonElement openWorldSelectionGui(JsonElement element)
    {
        Minecraft mc = Minecraft.getInstance();
        mc.enqueue(() -> mc.displayGuiScreen(new WorldSelectionScreen(mc.currentScreen)));
        return null;
    }
    
    public static JsonElement shutdownMC(JsonElement element)
    {
        Minecraft.getInstance().shutdown();
        return null;
    }
    
    public static JsonElement closeGui(JsonElement element)
    {
        Minecraft.getInstance().enqueue(() -> Minecraft.getInstance().displayGuiScreen(null));
        return null;
    }
    
    static class PlayerInfo
    {
        String name;
        String token;
        String uuid;
    }
    
    private static JsonParser jsonParser = new JsonParser();
    private static Gson gson = new Gson();
    
    public static JsonElement getPlayerInfo(JsonElement element)
    {
        Minecraft mc = Minecraft.getInstance();
        PlayerInfo info = new PlayerInfo();
        info.uuid = mc.getSession().getPlayerID();
        info.name = mc.getSession().getUsername();
        info.token = mc.getSession().getToken();
        String json = gson.toJson(info);
        return jsonParser.parse(json);
    }
    
    public static JsonElement sendPacket(JsonElement element)
    {
        NetworkHandler.sendPacket(element.toString());
        return null;
    }
    
    public static JsonElement sendChatMessage(JsonElement element)
    {
        Minecraft.getInstance().enqueue(() -> Minecraft.getInstance().player.sendChatMessage(element.getAsString()));
        return null;
    }
    
    public static class AddPacketReceiverFunc implements View.IJSFuncCallback
    {
        private final LUIScreen screen;
        
        public AddPacketReceiverFunc(LUIScreen screen)
        {
            this.screen = screen;
        }
        
        public JsonElement callback(JsonElement element)
        {
            JsonObject obj = element.getAsJsonObject();
            String plugin = obj.get("plugin").getAsString();
            String callback = obj.get("callback").getAsString();
            screen.packetReceiverMap.put(plugin, callback);
            return null;
        }
    }
    
    public static JsonElement isInGame(JsonElement element)
    {
        return gson.toJsonTree(Minecraft.getInstance().world != null);
    }
}
