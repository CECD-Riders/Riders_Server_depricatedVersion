function checkValue() {
    var x = document.getElementById("inputPw1");
    var y = document.getElementById("inputPw2");
    var z = document.getElementById("inputId");
    var errorMsg = document.getElementsByClassName("errmsg");
    for (var i = 0; i < errorMsg.length; i++) {
        errorMsg[i].style.display = "none";
    }
    var skip = false;

    if (x.value == "" || y.value == "" || z.value == "") {
        errorMsg[1].style.display = "block";
        skip = true;
        return false;
    }

    if (!skip && (x.value != y.value)) {
        errorMsg[2].style.display = "block";
        return false;
    }
}