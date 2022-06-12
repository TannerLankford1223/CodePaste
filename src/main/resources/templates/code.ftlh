<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Code</title>
    <link rel="stylesheet" href="/css/code.css">
    <link rel="stylesheet"
          href="//cdn.jsdelivr.net/gh/highlightjs/cdn-release@10.2.1/build/styles/default.min.css">
    <script src="//cdn.jsdelivr.net/gh/highlightjs/cdn-release@10.2.1/build/highlight.min.js"></script>
    <script>hljs.initHighlightingOnLoad();</script>
</head>
<body>
    <span id="load_date">${code.getDate()}</span>
    <#if (code.isViewRestriction())>
        <span id="views_restriction">${code.getViews()} more views remaining</span>
    </#if>

    <#if (code.isTimeRestriction())>
        <span id="time_restriction">${code.getTimeRemaining()} seconds remaining</span>
    </#if>

    <div id="code_container">
        <pre id="code_snippet"><code>${code.getCode()}</code></pre>
    </div>
</body>
</html>