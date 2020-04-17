# LongUI

IntUI 升级版

当前只支持 MC 版本 1.15.2 （需要安装forge）

需要安装前置模组 WebCraft 。

## 配置指南

LongUI 目前只提供了修改主界面的 json 配置，它的路径在 `mods/longui/MainMenu.json` （以后还会添加一些修改其它界面的 json ）

json格式（后文/LongUIS的文档还会提到本 json 格式）：

```json
{
  "name": "MyGui",
  "url": "http://mydomain",
  "drawBackground": true,
  "shouldCloseOnEsc": true
}
```

`name` 表示Gui的名字（必填）

`url` 表示网页的链接（选填，如果不填则默认读取 `mods/longui/<name>/index.html` 作为链接）

`drawBackground` 是否绘制背景（选填，如果不填默认为 `true` ）

`shouldCloseOnEsc` 在按下 Esc 时是否关闭 Gui （选填，如果不填默认为 `true` ）

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

  * 使用案例：

    ```javascript
    var info = getPlayerInfo();
    info.name//玩家名称
    info.uuid//玩家的uuid
    info.token//玩家的token
    ```
* `closeGui` 关闭Gui

* `sendPacket` 向服务端发包（仅能在服务器连接后使用，服务器需要安装插件 LongUIS ，本函数主要面向插件开发者）

  * 使用案例：
  
  ```javascript
  sendPacket({plugin:"MyPlugin",value:666});
  //plugin是你的插件名称
  //value是你想传递的一个json，格式任意
  ```
* `sendMessage` 让玩家在对话框发布信息，可以使用指令（仅能在游戏开始后使用）

* `addPacketReceiver` 添加一个接受插件发来的包的回调函数（仅能在服务器连接后使用，服务器需要安装插件 LongUIS ，本函数主要面向插件开发者）

  * 使用案例：
  
  ```javascript
  function luiScreenInit()
  {
      addPacketReceiver({plugin:"MyPlugin",callback:"myFunc"});
      //plugin是你的插件名称
      //callback是回调函数的名称
  }
  
  function myFunc(value)
  {
      //value是你的插件用 LongUIS API 发过来的一个任意格式的json
  }
  ```

* 以后还会添加更多的函数...



注意，请在你的 css 的 `font-family` 中多设置几个字体，亦或者是直接将字体放进你的设置文件夹中（然后在 css 中用 `file:///mods/longui/<gui名称>/xxx.ttf` 来使用你的字体）。在 LongUI 中（或者说 WebCraft 中），`file:///` 链接为**相对于 Minecraft 运行路径**的路径。不同启动器在不同模式下运行路径不同，比如：HMCL和**BakaXL（特别提示）**默认运行路径为 `.minecraft` ，HMCL开启版本隔离后，运行路径为 `.minecraft/versions/<版本号>`
