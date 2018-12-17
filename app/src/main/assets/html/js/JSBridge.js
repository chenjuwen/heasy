;(function(jQuery){
	jQuery.JSBridge = {
		//##### JS --> Java #####
        dispatchAction: function(actionName, jsonData, extend){
            return heasy.dispatchAction(actionName, jsonData, extend);
        },

        log: function(logLevel, logMessage){
            heasy.dispatchAction("Log", logMessage, logLevel);
        }
	};
})(jQuery);


;(function(jQuery){
	jQuery.extend(jQuery.JSBridge, {
		//##### Java --> JS #####
        executeFunction: function(funcName, data){
            if (typeof(eval("this." + funcName)) == "function") {
                try{
                    eval("this." + funcName + "(data)");
                }catch(ex){
                    this.log("error", ex.message);
                }
            }else{
                this.log("error", "Function[" + funcName + "] not found");
            }
        },


        //##### Java --> dispatchMessage --> JSFunction #####
        //倒计时信息显示
        CountDown: function(jsonData){
            var jsonObject = JSON.parse(jsonData);
            this.bindData(jsonObject.elementId, jsonObject.content);
        },

        //控件绑定值
        //{"elementId":"eid1", "content":"content"}
        BindData: function(jsonData){
            var jsonObject = JSON.parse(jsonData);
            this.bindData(jsonObject.elementId, jsonObject.content);
        },

        //控件追加式绑定值
        //{"elementId":"eid1", "content":"content"}
        BindAppendedData: function(jsonData){
            var jsonObject = JSON.parse(jsonData);
            this.bindAppendedData(jsonObject.elementId, jsonObject.content);
        }
	});
})(jQuery);


;(function(jQuery){
	jQuery.extend(jQuery.JSBridge, {
	    //##### JS本地外部方法 #####
        getElementById: function(elementId){
            return document.getElementById(elementId);
        },

        getData: function(elementId){
            var value = "";
            var mElement = this.getElementById(elementId);
            if(mElement != null){
                if(mElement.tagName == "INPUT" || mElement.tagName == "SELECT" || mElement.tagName == "TEXTAREA"){
                    value = mElement.value;
                }else if(mElement.tagName == "IMG"){
                    value = mElement.src;
                }else{
                    value = mElement.innerHTML;
                }
            }
            return value;
        },

        bindData: function(elementId, content){
            var mElement = this.getElementById(elementId);
            if(mElement != null){
                if(mElement.tagName == "INPUT" || mElement.tagName == "SELECT" || mElement.tagName == "TEXTAREA"){
                    mElement.value = content;
                }else if(mElement.tagName == "IMG"){
                    mElement.src = content;
                }else{
                    mElement.innerHTML = content;
                }
            }
        },

        bindAppendedData: function(elementId, content){
            var mElement = this.getElementById(elementId);
            if(mElement != null){
                if(mElement.tagName == "INPUT"){
                    mElement.value += content;
                }else if(mElement.tagName == "TEXTAREA"){
                    mElement.value += content + "\n";
                }else{
                    mElement.innerHTML += content + "<br>";
                }
            }
        },

        getAttribute: function(elementId, attrName){
            var value = "";
            var mElement = this.getElementById(elementId);
            if(mElement != null){
                value = mElement.getAttribute(attrName);
            }
            return value;
        },

        setAttribute: function(elementId, attrName, attrValue){
            var mElement = this.getElementById(elementId);
            if(mElement != null){
                mElement.setAttribute(attrName, attrValue);
            }
        },

        removeAttribute: function(elementId, attrName){
            var mElement = this.getElementById(elementId);
            if(mElement != null){
                mElement.removeAttribute(attrName);
            }
        },

	});
})(jQuery);
