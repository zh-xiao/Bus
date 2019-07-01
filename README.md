[![](https://jitpack.io/v/zh-xiao/Bus.svg)](https://jitpack.io/#zh-xiao/Bus)
# Bus 
源码不到三百行代码的超轻量Bus，支持发布和订阅普通事件和粘性事件。
### 添加
1.在项目的gradle中添加 : **maven { url 'https://jitpack.io' }**

	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
2.在app的gradle中添加 : **implementation 'com.github.zh-xiao:Bus:1.0.3'**

	dependencies {
		...
	        implementation 'com.github.zh-xiao:Bus:1.0.3'
	}
### 使用
Bus订阅发送和注销
```
//Bus订阅,onPost响应在主线程,如果需要响应在子线程用onSubThread替换onMainThread
Bus.onMainThread(this,"aaa", new Bus.OnPostListener<Integer>() {
    @Override
    public void onPost(Integer eventData) {
        //do something
    }
});

//Bus粘性订阅,onPost响应在主线程,如果需要响应在子线程用onStickSubThread替换onStickMainThread
Bus.onStickMainThread(this,"bbb", new Bus.OnPostListener<Integer>() {
    @Override
    public void onPost(Integer eventData) {
        //do something
    }
});
         
//Bus发送
Bus.post("aaa",123);

//Bus粘性发送
Bus.postStick("bbb",456);

@Override
public void onDestroy() {
    ...
    //Bus注销
    Bus.cancel(this);
}

```
### 写在最后
以上就是基本用法,更多用法可以参考Demo,玩一下就会了．
