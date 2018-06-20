<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
    <title><g:layoutTitle default="[meta(name: 'app.name')]"/></title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <g:layoutHead/>
    <link rel="shortcut icon" href="${resource(dir: 'images', file: 'favicon.ico')}"/>
</head>

<div id="main" class="container">
    <g:render template="/templates/headerIncludes"/>
    <div id="wrapper">
        %{--<g:layoutBody/>--}%
    </div>
</div>
<!-- All Javascript at the bottom of the page for faster page loading -->

<g:render template="/templates/pageFooterIncludes"/>

