<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Code</title>
    <style>
        #code_container {
            background-color: lightgray;
            padding: 20px;
            width: 50%;
            margin-bottom: 20px;
        }
        #load_date {
            font-family: Arial, sans-serif;
            color: green;
            margin-bottom: 10px;
        }
        .restrictions {
            margin-top: 20px;
        }
    </style>
    <link rel="stylesheet"
          href="//cdn.jsdelivr.net/gh/highlightjs/cdn-release@10.2.1/build/styles/default.min.css">
    <script src="//cdn.jsdelivr.net/gh/highlightjs/cdn-release@10.2.1/build/highlight.min.js"></script>
    <script>hljs.initHighlightingOnLoad();</script>
</head>
<body>
    <span id="load_date">Date created: ${userCode.getDate()}</span>
    <br>
    <#if (userCode.isViewRestriction())>
        <span class="restrictions" id="views_restriction">${userCode.getViews()} more views remaining</span>
    </#if>
    <br>
    <#if (userCode.isTimeRestriction())>
        <span class="restrictions" id="time_restriction">${userCode.getTimeRemaining()} seconds remaining</span>
    </#if>
    <br>
    <div id="code_container">
        <pre id="code_snippet"><code>${userCode.getCode()}</code></pre>
    </div>
</body>
</html>