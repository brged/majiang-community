
function postComment(){
    var questionId = $("#question-id").val();
    var commentContent = $("#comment-content").val();
    $.ajax({
        type: "POST",
        url: "/comment",
        contentType: "application/json",
        data: JSON.stringify({"parentId": questionId, "content": commentContent, "parentType": 1}),
        success: function(response){
            console.log(response);
            if(response.code==200){
                $("#comment-edit").hide();
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