# heasy  
heasy是一个基于Android平台的APP混合开发框架，页面层集成jquery、VUE、mint-ui、muse-ui等前端框架，具有容易使用、扩展性好、灵活性强、易维护等特性。  

## 架构图   
<img src="https://github.com/chenjuwen/heasy/blob/master/doc/design-pic.jpg" width="600" height="400"/> 

框架的主要扩展点：  
    1、	Action类  
    2、	Service类  
    3、	前端UI框架  
    4、	JSBridge的内置JS方法  

## 开发工具  
Android Studio  

## 使用指南  
### 使用前准备  
    框架中整合了百度开发平台的文字识别、语音合成、语音识别、人脸识别和中英文翻译等功能，在编译运行工程代码前，
    请先到百度开发平台申请开通对应的api接口使用权限。  
    
	AI开放平台：https://ai.baidu.com/  
	翻译开发平台：http://api.fanyi.baidu.com/api/trans/product/index  

	使用文字识别功能需要将AI开放平台生成的 aip.license 文件复制到baidu-ai\src\main\assets 目录下。  
    
	语音合成、语音识别、人脸识别和中英文翻译的接口参数需要更新到baidu-ai\src\main\assets\ai-config.xml 配置文件中。  

### JSBridge常用方法  
#### JS端 --> Java端  
##### dispatchAction方法  
功能：执行指定的Action类  
方法体： var result = jsBridge.dispatchAction(actionName, data, extend);  
	&nbsp;&nbsp;&nbsp;&nbsp;参数说明：  
		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;actionName：action的名字  
		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;data：action用到的数据内容，一般是json格式的字符串  
		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;extend：扩展字段，当一个Action类有多个业务方法时，可以通过该字段加以区分  
	&nbsp;&nbsp;&nbsp;&nbsp;返回值：  
		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;String型的值  

##### log方法  
功能：将日志信息发送到后台处理  
方法体：jsBridge.log(logLevel, logMessage);  
	&nbsp;&nbsp;&nbsp;&nbsp;参数说明：  
		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;logLevel：日志级别，可选值有debug（默认）、info、warn、error  
		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;logMessage：日志信息  
	&nbsp;&nbsp;&nbsp;&nbsp;返回值：  
		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;无  

#### Java端 --> JS端  
##### executeFunction方法  
功能：用于执行客户端指定的内置的JS方法。内置的JS方法在JSBridge.js文件中定义。  
方法体：public void executeFunction(String funcName, String data);  
	&nbsp;&nbsp;&nbsp;&nbsp;参数说明：  
		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;funcName：内置的JS方法，主要有CountDown、BindData、BindAppendedData等。  
		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;data：传入方法的数据内容  
范例：  
	&nbsp;&nbsp;&nbsp;&nbsp;//数据绑定：设置控件id为password的value值为123  
	&nbsp;&nbsp;&nbsp;&nbsp;String jsonData = FastjsonUtil.toJSONString("elementId", "password", "content", "123");  
	&nbsp;&nbsp;&nbsp;&nbsp;heasyContext.getJsInterface().executeFunction("BindData", jsonData);  

##### 执行自定义的JS方法  
定义JS方法：  
	&nbsp;&nbsp;&nbsp;&nbsp;function takePhotoCallback(imagePath){  
		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;//逻辑代码  
	&nbsp;&nbsp;&nbsp;&nbsp;}  

调用JS方法：  
	&nbsp;&nbsp;&nbsp;&nbsp;String script = "javascript: try{ takePhotoCallback(\"" + imageFilePath + "\"); }catch(e){ }";  
	&nbsp;&nbsp;&nbsp;&nbsp;heasyContext.getJsInterface().loadUrl(script);  


## 技术交流  
<img src="https://github.com/chenjuwen/heasy/blob/master/doc/author.jpg" width="150" height="200"/>  
