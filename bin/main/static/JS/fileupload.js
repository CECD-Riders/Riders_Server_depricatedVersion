$('.contents')

    .contents.on("dragover", dragOver)
  .on("dragleave", dragOver)
  .on("drop", uploadFiles);
 
  function dragOver(e) {
    e.stopPropagation();
    e.preventDefault();
    if (e.type == "dragover") {
        $(e.target).css({
            "background-color": "black",
            "outline-offset": "-20px"
        });
    } else {
        $(e.target).css({
            "background-color": "gray",
            "outline-offset": "-10px"
        });
    }
}


