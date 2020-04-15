package cafe.qwq.longui;

import cafe.qwq.webcraft.api.View;
import cafe.qwq.webcraft.api.WebScreen;
import cafe.qwq.webcraft.api.math.Vec4i;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

public class LUIScreen extends WebScreen
{
    final View luiView;

    public LUIScreen(String name, String url, boolean drawBackground)
    {
        super(new StringTextComponent(name));
        luiView = new View();
        luiView.addJSFuncWithCallback("setMCTitle", JSFunctions::setMCTitle);
        luiView.addJSFuncWithCallback("getServerInfo", new JSFunctions.GetServerInfoFunc(this));
        luiView.addJSFuncWithCallback("connectToServer", JSFunctions::connectToServer);
        luiView.addJSFuncWithCallback("openLUIGui", JSFunctions::openGui);
        luiView.addJSFuncWithCallback("openModsGui", JSFunctions::openModsGui);
        luiView.addJSFuncWithCallback("openOptionsGui", JSFunctions::openOptionsGui);
        luiView.addJSFuncWithCallback("shutdownMC", JSFunctions::shutdownMC);
        luiView.addJSFuncWithCallback("openWorldSelectionGui", JSFunctions::openWorldSelectionGui);
        luiView.loadURL(url);
        luiView.setResizeCallback(vec -> new Vec4i(0, 0, vec.x, vec.y));
        luiView.addDOMReadyListener(() -> luiView.evaluteJS("luiScreenInit();"));
        addView(luiView);
        if (drawBackground)
        {
            addPreRenderer((x, y, p) -> renderBackground());
        }
    }

    public void onClose()
    {
        luiView.evaluteJS("luiScreenClose();");
        super.onClose();
    }
}
