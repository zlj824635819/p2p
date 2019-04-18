


//错误提示
function showError(id,msg) {
	$("#"+id+"Ok").hide();
	$("#"+id+"Err").html("<i></i><p>"+msg+"</p>");
	$("#"+id+"Err").show();
	$("#"+id).addClass("input-red");
}
//错误隐藏
function hideError(id) {
	$("#"+id+"Err").hide();
	$("#"+id+"Err").html("");
	$("#"+id).removeClass("input-red");
}
//显示成功
function showSuccess(id) {
	$("#"+id+"Err").hide();
	$("#"+id+"Err").html("");
	$("#"+id+"Ok").show();
	$("#"+id).removeClass("input-red");
}

//注册协议确认
$(function() {
	$("#agree").click(function(){
		var ischeck = document.getElementById("agree").checked;
		if (ischeck) {
			$("#btnRegist").attr("disabled", false);
			$("#btnRegist").removeClass("fail");
		} else {
			$("#btnRegist").attr("disabled","disabled");
			$("#btnRegist").addClass("fail");
		}
	});
});

//打开注册协议弹层
function alertBox(maskid,bosid){
	$("#"+maskid).show();
	$("#"+bosid).show();
}
//关闭注册协议弹层
function closeBox(maskid,bosid){
	$("#"+maskid).hide();
	$("#"+bosid).hide();
}


//验证手机号码
function checkPhone() {
    var phone = $.trim($("#phone").val());
    var flag = true;

    if ("" == phone) {
        showError("phone","请输入手机号码");
        return false;
    } else if (!/^1[1-9]\d{9}$/.test(phone)) {
        showError("phone", "请输入正确的手机号码");
        return false;
	}else {
        $.ajax({
            url:"loan/checkPhone",
            type:"post",
            data:"phone="+phone,
            async:false,//关闭异步提交
            success:function (jsonObject) {
                if (jsonObject.errowMessage == "ok"){
                    showSuccess("phone");
                    flag = true;
                }else{
                    showError("phone",jsonObject.errowMessage);
                    flag = false;
                }
            },
            error:function () {
                showError("phone","系统繁忙");
                flag = false;
            }
        });
    }
    if (!flag){
        return false;
    }
    return true;
}
//验证登录密码
function checkLoginPassword() {
    var loginPassword = $.trim($("#loginPassword").val());
    var replayLoginPassword = $.trim($("#replayLoginPassword").val());
    if (""==loginPassword){
        showError("loginPassword","请输入密码");
        return false;
    }else if (!/^[0-9a-zA-Z]+$/.test(loginPassword)){
        showError("loginPassword","密码格式错误");
        return false;
    }else if (!/^(([a-zA-Z]+[0-9]+)|([0-9]+[a-zA-Z]+))[a-zA-Z0-9]*/.test(loginPassword)){
        showError("loginPassword","密码应同时包含英文或数字");
    }else {
        showSuccess("loginPassword");
    }

    return true;
}
//确认登录密码
function checkLoginPasswordEqu() {
    var loginPassword = $.trim($("#loginPassword").val());
    var replayLoginPassword = $.trim($("#replayLoginPassword").val());

    if ("" == loginPassword) {
        showError("loginPassword","请输入登录密码");
        return false;
    } else if ("" == replayLoginPassword) {
        showError("replayLoginPassword","请再次输入登录密码");
        return false;
    } else if (replayLoginPassword != loginPassword) {
        showError("replayLoginPassword", "两次输入密码不一致");
        return false;
    } else {
        showSuccess("replayLoginPassword");
    }
    return true;
}

//验证图形验证码
function checkCaptcha() {
    var captcha = $.trim($("#captcha").val());
    var flag = true;

    if ("" == captcha) {
        showError("captcha", "请输入图形验证码");
        return false;
    } else {
        $.ajax({
            url: "loan/checkCaptcha",
            type: "post",
            data: "captcha=" + captcha,
            async: false,//关闭异步提交
            success: function (jsonObject) {
                if (jsonObject.errowMessage == "ok") {
                    showSuccess("captcha");
                    flag = false;
                } else {
                    showError("captcha", jsonObject.errowMessage);
                    flag = false;
                }

            },
            error: function () {
                showError("captcha", "系统繁忙，请稍后重试...");
                flag = false;
            }
        });
    }
    return true;
}
//用户注册请求
function register() {
    var phone = $.trim($("#phone").val());
    var loginPassword = $.trim($("#loginPassword").val());
    var replayLoginPassword = $.trim($("#replayLoginPassword").val());

                alert("11111")
    if (checkPhone() && checkLoginPassword() && checkLoginPasswordEqu() && checkCaptcha()) {
        $("#loginPassword").val($.md5(loginPassword));
        $("#replayLoginPassword").val($.md5(replayLoginPassword));

        $.ajax({
            url:"loan/register",
            type:"post",
            data:{
                "phone":phone,
                "loginPassword":$.md5(loginPassword)
            },
            success:function (jsonObject) {
                alert(jsonObject.errowMessage);
                if (jsonObject.errowMessage == "ok") {
                    window.location.href = "realName.jsp";
                    return true;
                } else {
                    $("#captcha").val("注册人数过多，请稍后重试...");
                    return false;
                }
            },
            error:function () {
                alert("注册人数过多，请稍后重试...");
                return false;
            }
        });
    }

}
