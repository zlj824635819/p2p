var referrer = "";//登录后返回页面
referrer = document.referrer;
if (!referrer) {
    try {
        if (window.opener) {
            // IE下如果跨域则抛出权限异常，Safari和Chrome下window.opener.location没有任何属性
            referrer = window.opener.location.href;
        }
    } catch (e) {
    }
}

//按键盘Enter键即可登录
$(document).keyup(function (event) {
    if (event.keyCode == 13) {
        login();
    }
});
$(function () {

    loadStat();
    $("#dateBtn1").on("click", function () {

        var phone = $.trim($("#phone").val());
        var loginPassword = $.trim($("#loginPassword").val());
        if ("" == phone) {
            $("#showId").html("请输入手机号");
            return false
        } else if ("" == loginPassword) {
            $("#loginPassword").html("请输入密码");
        } else {
            $.ajax({
                url: "loan/sendMessageCode",
                type: "post",
                data: "phone=" + phone,
                success: function (jsonObject) {
                    alert("您的短信验证码为"+jsonObject.messageCode);
                    if (jsonObject.errowMessage == "ok") {
                        $.leftTime(60, function (d) {
                            if (d.status) {
                                $("#dateBtn1").addClass("on");
                                $("#dateBtn1").html((d.s == "00" ? "60" : d.s) + "秒后重新获取");

                            } else {
                                $("#dateBtn1").removeClass("on");
                                $("#dateBtn1").html("获取验证码");
                            }

                        });
                        return true;
                    } else {
                        $("#showId").html("短信发送失败1")
                        return false;
                    }
                },
                error: function () {
                    $("#showId").html("短信发送失败2")
                    return false;
                }
            })
        }


    })
});

function loadStat() {
    $.ajax({
        url: "loan/loadStat",
        type: "get",
        success: function (jsonObject) {
            $(".historyAverageRate").html(jsonObject.historyAverageRate);
            $("#allBidMoney").html(jsonObject.allBidMoney);
            $("#allUserCount").html(jsonObject.allUserCount);
        }

    })

}

//验证手机号码
function checkPhone() {
    var phone = $.trim($("#phone").val());

    if ("" == phone) {
        $("#showId").html("请输入手机号");
        return false;
    } else if (!/^1[1-9]\d{9}$/.test(phone)) {
        $("#showId").html("请输入正确的手机号");
        return false;
    } else {
        $("#showId").html("");
    }
    return true;
}

//验证登录密码
function checkLoginPassword() {
    var loginPassword = $.trim($("#loginPassword").val());

    if ("" == loginPassword) {
        $("#showId").html("请输入登录密码")
    } else {
        $("#showId").html("");
    }
    return true;
}

//用户登录
function login() {
    var phone = $.trim($("#phone").val());
    var loginPassword = $.trim($("#loginPassword").val());

    if (checkPhone() && checkLoginPassword()) {
        $.ajax({
            url: "loan/login",
            type: "post",
            data: "phone=" + phone + "&loginPassword=" + $.md5(loginPassword),
            success: function (jsonObject) {
                if (jsonObject.errowMessage == "ok") {
                    window.location.href = referrer;
                } else {
                    $("#showId").html(jsonObject.errowMessage);
                }
            },
            error: function () {
                $("#showId").html("系统繁忙，请稍后重试")
            }
        })
    }
}












