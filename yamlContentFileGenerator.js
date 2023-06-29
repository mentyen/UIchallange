let contentFileElements = {};
let existingPaths = [];
let resultDiv = document.createElement("div");
resultDiv.id = "contentFileYaml"
function getPathTo(element) {
    if (element.id!=='')
        return "id('"+element.id+"')";
    if (element===document.body)
        return element.tagName;

    var ix= 0;
    var siblings= element.parentNode.childNodes;
    for (var i= 0; i<siblings.length; i++) {
        var sibling= siblings[i];
        if (sibling===element)
            return getPathTo(element.parentNode)+'/'+element.tagName+'['+(ix+1)+']';
        if (sibling.nodeType===1 && sibling.tagName===element.tagName)
            ix++;
    }
}
function onClickContent(e){
    if(e.target.textContent !== '' && e.target.textContent !== undefined && e.target.id !== "contentFileYaml" && e.target.tagName !== 'HTML'){
        let content = e.target.textContent.trim().replaceAll("\n", "\\n");
        let path = getPathTo(e.target);
        if(existingPaths.indexOf(path) === -1){

            existingPaths.push(path);
            let fileElement = {
                description: "",
                xpath: path,
                content: content
            }
            contentFileElements['element' + existingPaths.length] = fileElement;
            printYaml();
        }
        e.stopPropagation();
    }
    e.preventDefault();
  };
function onMouseUpContent(e){
    e.preventDefault();
}

function printYaml(){
    let result = "content:";
    const separatorKey = "\n  ";
    const separatorSubkey = "\n    ";
    const keys = Object.keys(contentFileElements);
    for(i=0; i<keys.length;i++){
        result += separatorKey + keys[i] + ": ";
        result += separatorSubkey + "description: " + '"' + contentFileElements[keys[i]].description + '"';
        result += separatorSubkey + "xpath: " + '"' + contentFileElements[keys[i]].xpath + '"';
        result += separatorSubkey + "content: " + '"' + contentFileElements[keys[i]].content + '"';
    }
    result += "\n";
    resultDiv.textContent = result;
}


function copyToClipboard(text) {
    var $temp = document.createElement("textarea");
    document.body.append($temp);
    $temp.value = text;
    $temp.select();
    document.execCommand("copy");
    $temp.remove();
}
function copyYamlToClipboard(){
    copyToClipboard(resultDiv.textContent);
}
document.querySelectorAll("*").forEach(node => {
    node.addEventListener('click', onClickContent);
    node.addEventListener('mouseup', onMouseUpContent);
})
function initResult(){
    contentFileElements = {}
    existingPaths = []
    resultDiv.style =  `
    z-index: 999999999!important;
    font-size: 10px!important;
    position:fixed!important;
    top: 0!important;
    bottom: 0!important;
    right: 0!important;
    background-color: #00000095!important;
    max-width: 30%!important;
    min-width: 30%!important;
    padding: 20px!important;
    white-space: pre-wrap!important;
    color: white!important;
    overflow: auto!important;`;
    resultDiv.addEventListener('click', copyYamlToClipboard);
    resultDiv.textContent = "";
    document.body.append(resultDiv);
}
initResult();
