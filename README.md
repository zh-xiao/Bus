[![](https://jitpack.io/v/zh-xiao/Bus.svg)](https://jitpack.io/#zh-xiao/Bus)
# Bus 
核心源码只有一个大概两百行代码的超轻量Bus，支持发布和订阅普通事件和粘性事件(普通事件的发布订阅和粘性事件的发送订阅是隔离的)。
### 添加步骤
1.在项目的gradle中添加maven { url 'https://jitpack.io' }

	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
2.在app的gradle中添加implementation 'com.github.zh-xiao:Bus:latestVersion',latestVersion用最新版本号替代

	dependencies {
	        implementation 'com.github.zh-xiao:Bus:latestVersion'
	}
### 使用
Bus注册和注销
```
//首先初始化bus实例
Bus bus = new Bus();

@Override
protected void onCreate(Bundle savedInstanceState) {
    ...
    //Bus注册
    bus.register(this);
}

@Override
public void onDestroy() {
    //Bus注销
    bus.unregister(this);
}
```
Bus订阅和发送(订阅需在注册之后,注销之前)
```
//Bus订阅
bus.subscribe("aaa", new Bus.OnPostListener<Integer>() {
    @Override
    public void onPost(Integer eventData) {
        //do something
    }
});

//Bus粘性订阅
bus.subscribeStick("bbb", new Bus.OnPostListener<Integer>() {
    @Override
    public void onPost(Integer eventData) {
        //do something
    }
});
         
//Bus发送
Bus.post("aaa",123);

//Bus粘性发送
Bus.postStick("bbb",456);

```
### 写在最后
以上就是基本用法,更多用法可以参考Demo,玩一下就会了．
