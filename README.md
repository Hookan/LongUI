# LongUI

IntUI 升级版

当前只支持 MC 版本 1.15.2 （需要安装forge）

需要安装前置模组 WebCraft 。

## 配置指南

LongUI 目前只提供了修改主界面的 json 配置，它的路径在 `mods/longui/MainMenu.json` （以后还会添加一些修改其它界面的 json ）

json格式：

```json
{
  "name": "MyGui",
  "url": "http://mydomain",
  "drawBackground": true
}
```

`name` 表示Gui的名字（必填）

`url` 表示网页的链接（选填，如果不填则默认读取 `mods/longui/<name>/index.html` 作为链接）

`drawBackground` 是否绘制背景（选填，如果不填默认为 `true` ）

由于 WebCraft 采用 Ultralight 作为网页引擎，并没有支持全部 HTML5 特性，请注意，关于不支持的特性请看[这里](https://github.com/ultralight-ux/Ultralight/issues/178)。

LongUI 为方便 GUI 的编写以及与 MC 的交互，提供了以下几个 JS 函数（所有的LongUI添加的 JS 函数都必须在 `luiScreenInit` 函数中或者这个函数执行完之后执行）

* `setMCTitle` 设置MC的标题
  
  * 使用案例：
  
    ```javascript
    function luiScreenInit()
    {
        setMCTitle("喵喵喵");
    }
    ```

* `getServerInfo` 获取服务器信息

  * 使用案例：

    ```javascript
    function luiScreenInit()
    {
        getServerInfo({host:"<服务器地址>",port:<服务器端口(注意没有双引号)>,callback:"myFunc"});
    }
    
    function myFunc(info)
    {
        //info为一个json，其中有两个变量为 serverInfo 和 ping
        //serverInfo 是服务器信息，其格式请参考wiki.vg
        //ping即服务器延迟
    }
    ```

* `connectToServer` 连接服务器

  * 使用案例：

    ```html
    <button onClick="qwq()">QAQ</button>
    <script>
        function qwq()
        {
            connectToServer({name:"myServer", ip:"<服务器地址>:<服务器端口>"});
        }
    </script>
    ```

* `shutdownMC` 关闭MC

  * 使用案例：

    ```javascript
    shutdownMC();
    ```

* `openLUIGui` 打开一个LongUI的GUI

  * 使用案例：

    ```javascript
    openLUIGui({name: "MyGui"});//这个 json 格式与 MainMenu.json 相同
    ```

* `openModsGui` 打开mod列表

* `openOptionsGui` 打开设置

* `openWorldSelectionGui` 打开单人模式

* `getPlayerInfo` 获取玩家信息

  * 使用案例

    ```javascript
    var info = getPlayerInfo();
    info.name//玩家名称
    info.uuid//玩家的uuid
    info.token//玩家的token
    ```

* 以后还会添加更多的方法...



注意，请在你的 css 的 `font-family` 中多设置几个字体，亦或者是直接将字体放进你的设置文件夹中（然后在 css 中用 `file:///mods/longui/<gui名称>/xxx.ttf` 来使用你的字体）。在 LongUI 中（或者说 WebCraft 中），`file:///` 链接为**相对于 Minecraft 运行路径**的路径。