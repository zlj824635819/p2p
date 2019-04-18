
//同意实名认证协议
$(function() {
	$("#replayIdCard").val("");
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

//验证真实姓名
function checkRealName() {
	var realName = $.trim($("#realName").val());
	
	if (""==realName){
		showError("realName","请输入真实姓名");
		return false;
	}else if (!/^[\u4e00-\u9fa5]{0,}$/.test(realName)){
		showError("realName","真实姓名只支持中文");
		return false;
	}else{
		showSuccess("realName");
	}
	return true;
}
//验证身份证号
function checkIdCard() {
	var idCard = $.trim($("#idCard").val());
	var replayIdCard =$.trim($("#replayIdCard").val());

	if (""==idCard){
		showError("idCard","请输入身份证号码");
		return false;
	}else if (!/(^\d{15}$)|(^\d{18}$)|(^\d{17}(\d|X|x)$)/.test(idCard)){
		showError("idCard","请输入正确的身份证号码");
		return false;
	}else{
		showSuccess("idCard");
	}
	if (replayIdCard!=idCard){
		showError("replayIdCard","请再次输入身份证号码")
	}
	return true;
	
}
//确认身份证号码
function checkIdCardEqu() {

	var idCard = $.trim($("#idCard").val());
	var replayIdCard = $.trim($("#replayIdCard").val());
	if (""==idCard){
		showError("idCard","请输入身份证号");
		return false;
	} else if (""==replayIdCard){
		showError("replayIdCard","请再次输入身份证号");
		return false;
	} else if (idCard!=replayIdCard) {
		showError("replayIdCard","两次输入身份证号码不一致");
		return false;
	}else {
		showSuccess("replayIdCard");
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
//实名认证
function verifyRealName() {
	var  realName = $.trim($("#realName").val());
	var idCard = $.trim($("#idCard").val());
	if ( checkCaptcha()&&checkIdCardEqu()&&checkIdCard()&&checkRealName()){
		$.ajax({
			url:"loan/verifyRealName",
			type:"post",
			data:"realName="+realName+"&idCard="+idCard,
			success:function (jsonObject) {
				if (jsonObject.errowMessage=="ok"){
					window.location.href="index";
				}else {
					$("#captcha").val(jsonObject.errowMessage);
				}
			},
			error:function () {
				$("#captcha").val("实名认证，请稍后重试");
			}
		})
	}
}
























