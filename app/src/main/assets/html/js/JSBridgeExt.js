var pageParameters = {}; //存放页面参数值
var jsBridge = jQuery.JSBridge;
var actionNames = JSConstants.ACTION_NAMES;



//倒计时
var oCountDown;
var oCountDownParameters = {};
function startCountDown(jsonObject){
    oCountDownParameters = jsonObject;
    var oCountDown = setInterval(function(){
        if(jsBridge != null){
            clearInterval(oCountDown);
            jsBridge.dispatchAction(actionNames.CountDown, JSON.stringify(oCountDownParameters));
            oCountDownParameters = {};
        }
    }, 50);
}

//解析页面参数
//key1=value1;key2=value2
function parsePageParameters(parameters){
    try{
        if(parameters == null || parameters == ""){
            return;
        }

        pageParameters = {};

        var arr1 = parameters.split("&");
        for(var i=0; i<arr1.length; i++){
            var arr2 = arr1[i].split("=");
            pageParameters[arr2[0]] = arr2[1];
        }
    }catch(ex){
        _log("error", ex.message);
    }
}

//获取页面参数值
function getPageParameter(paramName){
    var paramValue = "";
    if(pageParameters[paramName]){
        paramValue = pageParameters[paramName];
    }
    return paramValue;
}


//video
//播放视频
function playVideo(elementId){
    var video = document.getElementById(elementId);
    if(video != null){
        video.play();
    }
}

//暂停视频
function pauseVideo(elementId){
    var video = document.getElementById(elementId);
    if(video != null){
        video.pause();
    }
}

//更改视频
function changeVideo(elementId, videoFile){
    var video = document.getElementById(elementId);
    if(video != null){
        video.src = videoFile;
        video.play();
    }
}

//视频静音
function mutedVideo(elementId, isMuted){
    var video = document.getElementById(elementId);
    if(video != null){
        video.muted = isMuted;
    }
}

function backMainPage(){
    jsBridge.dispatchAction(actionNames.PageTransfer, JSON.stringify({"url":"main.html"}));
}

function backPage(pageURL, parameters){
    var obj = {"url":pageURL, "parameters":parameters};
    jsBridge.dispatchAction(actionNames.PageTransfer, JSON.stringify(obj));
}

