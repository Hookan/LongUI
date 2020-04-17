package cafe.qwq.longui;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.client.Minecraft;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

import java.nio.charset.StandardCharsets;
import java.util.function.Supplier;

import static org.apache.http.params.CoreProtocolPNames.PROTOCOL_VERSION;

@Mod.EventBusSubscriber(modid = LongUI.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class NetworkHandler
{
    private static final SimpleChannel channel = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(LongUI.MODID, "main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals);
    
    
    @SubscribeEvent
    public static void init(final FMLClientSetupEvent event)
    {
        channel.registerMessage(0, Message.class, Message::writeToPacket, Message::readFromPacket, Message::handle);
    }
    
    public static void sendPacket(String msg)
    {
        channel.sendToServer(new Message(msg));
    }
    
    public static class Message
    {
        private String msg;
        
        public Message(String msg)
        {
            this.msg = msg;
        }
        
        public static void writeToPacket(Message msg, PacketBuffer packet)
        {
            packet.writeBytes(msg.msg.getBytes(StandardCharsets.UTF_8));
        }
        
        public static Message readFromPacket(PacketBuffer packet)
        {
            return new Message(packet.toString(StandardCharsets.UTF_8));
        }
        
        private static JsonParser jsonParser = new JsonParser();
        
        public static void handle(Message msg, Supplier<NetworkEvent.Context> ctx)
        {
            JsonElement element = jsonParser.parse(msg.msg);
            JsonObject obj = element.getAsJsonObject();
            String type = obj.get("type").getAsString();
            Minecraft mc = Minecraft.getInstance();
            switch (type)
            {
                case "og":
                    GuiConfig config = GuiConfig.loadGui(obj.get("value"));
                    ctx.get().enqueueWork(() -> {
                        LUIScreen screen = new LUIScreen(config);
                        mc.displayGuiScreen(screen);
                    });
                    break;
                case "np":
                    ctx.get().enqueueWork(() -> {
                        if (mc.currentScreen instanceof LUIScreen)
                        {
                            ((LUIScreen) mc.currentScreen).onReceivePacket(element);
                        }
                    });
                    break;
                default:
                    LongUI.LOGGER.warn("Invalid Packet Type: " + type);
                    break;
            }
            ctx.get().setPacketHandled(true);
        }
    }
}
