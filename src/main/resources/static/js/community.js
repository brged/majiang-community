/**
 * 提交回复内容
 */
function postComment(){
    var questionId = $("#question-id").val();
    var commentContent = $("#comment-content").val();
    comment2target(questionId, 1, commentContent);
}

function comment2target(targetId, type, content){
    if(!content){
        alert("回复内容不能为空");
        return
    }
    $.ajax({
        type: "POST",
        url: "/comment",
        contentType: "application/json",
        data: JSON.stringify({"parentId": targetId, "content": content, "parentType": type}),
        success: function(response){
            console.log(response);
            if(response.code==200){
                // $("#comment-edit").hide();
                window.location.reload();
            } else {
                if(response.code==2003){
                    // 用户未登录
                    var isAccept = confirm(response.message);
                    if(isAccept){
                        window.open("https://github.com/login/oauth/authorize?client_id=9b7e61916f3db90ed305&redirect_uri=http://localhost:8887/callback&scope=user&state=1");
                        localStorage.setItem("closable", "true");
                    }
                } else {
                    alert(response.message)
                }
            }
        },
        dataType: "json"
    });
}

function comment(e){
    var commentId = e.getAttribute("data-id");
    var commentContent = $("#input-"+commentId).val();
    comment2target(commentId, 2, commentContent);
}

/**
 * 展开二级评论列表
 */
function collapseInComments(e){
    var commentId = e.getAttribute("data-id");
    var comments2 = $("#comment-"+commentId);

    // 二级评论展开状态
    var collapse = e.getAttribute("data-collapse");
    if(collapse){
        //折叠二级评论
        comments2.removeClass("in");
        e.removeAttribute("data-collapse");
        e.classList.remove("active");
    } else {
        //展开二级评论
        // 如果已经加载过了，就直接展示
        if(comments2.children().length==1){
            $.getJSON("/comment/"+commentId, function(data){
                $.each(data.data.reverse(), function(i, v){
                    var c = $("<div/>", {
                        "class": "media",
                        "html": "<div class=\"media\">\n" +
                        "    <div class=\"media-left\">\n" +
                        "        <a href=\"#\">\n" +
                        "            <img class=\"media-object img-rounded\" src=\""+v.user.avatarUrl+"\" alt=\"avatar\">\n" +
                        "        </a>\n" +
                        "    </div>\n" +
                        "    <div class=\"media-body\">\n" +
                        "        <h5 class=\"media-heading\">\n" +
                        "            <span>"+v.user.name+"</span>\n" +
                        "        </h5>\n" +
                        "        <!-- 回复内容 -->\n" +
                        "        <div>"+v.content+"</div>\n" +
                        "        <div class=\"reply-menu\">\n" +
                        "            <span class=\"pull-right\">"+moment(v.gmtCreate).format("YYYY-MM-DD")+"</span>\n" +
                        "        </div>\n" +
                        "    </div>\n" +
                        "</div>"
                    });
                    comments2.prepend(c);
                });
            });
        }
        comments2.addClass("in");
        e.setAttribute("data-collapse", "in");
        e.classList.add("active");
    }
}

function selectTag(e){
    var value = e.getAttribute("data-tag");
    var preValue = $("#tag").val();
    if(preValue){
        if(preValue.indexOf(value)==-1) {
            $("#tag").val(preValue + ',' + value);
        }
    } else {
        $("#tag").val(value)
    }
}