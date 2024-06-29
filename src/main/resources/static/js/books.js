
function loadpage() {
    let urlParams = new URLSearchParams(window.location.search);
    let pdfObject = document.getElementById("book");
    let bookname = urlParams.get("book");
    if (bookname === null) {
        bookname = "Virtual Craftality";
    }
    pdfObject.setAttribute("data", "pdf/" + bookname + ".pdf");
}
