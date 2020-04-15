package cafe.qwq.longui;

import com.google.gson.Gson;
import com.google.gson.JsonElement;

import java.io.File;
import java.io.FileReader;

public class GuiConfig
{
    public String name;
    public String guiPath;
    public String url;
    public boolean drawBackground = true;

    private static Gson gson = new Gson();

    public static GuiConfig loadGui(String path)
    {
        File f = new File(path);
        GuiConfig config = null;
        if (f.exists() && f.isFile())
        {
            try (FileReader reader = new FileReader(f))
            {
                config = gson.fromJson(reader, GuiConfig.class);
                if (config.url == null)
                    config.url = "file:///mods/longui/" + config.name + "/index.html";
                //System.out.println(config.url);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        return config;
    }

    public static GuiConfig loadGui(JsonElement element)
    {
        GuiConfig config = gson.fromJson(element, GuiConfig.class);
        if (config.url == null)
            config.url = "file:///mods/longui/" + config.name + "/index.html";
        return config;
    }
}
