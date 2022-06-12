<html lang="en">
<head>
    <title>Create</title>
    <link rel="stylesheet" href="/css/code.css"/>
</head>
<body>
    <form action="/code/new" method="get">
        <textarea id="code_snippet" name="code_snippet" rows="8" cols="80">// write your code here.
        </textarea>
        <br>

        <div id="restrictions">
            <label for="time_restriction">Time restriction: </label><input id="time_restriction" type="text" value="0"/>
            <label for="views_restriction">Views restriction: </label><input id="views_restriction" type="text" value="0">
        </div>
        <button id="send_snippet" type="submit" value="submit" onclick="send()">Submit</button>
    </form>
    <script type="text/javascript">
        function send() {
            let object = {
                code: document.getElementById("code_snippet").value,
                time: document.getElementById("time_restriction").value,
                views: document.getElementById("views_restriction").value
            };

            let json = JSON.stringify(object);

            let xhr = new XMLHttpRequest();
            xhr.open("POST", '/api/code/new', false);
            xhr.setRequestHeader('Content-type', 'application/json; charset=utf-8');
            xhr.send(json);

            if (xhr.status == 200) {
                alert("Success!");
            }
        }
    </script>
</body>
</html>