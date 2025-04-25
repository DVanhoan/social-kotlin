<link rel="stylesheet" type="text/css" href="{{ asset('css/animate.min.css') }}">
<link rel="stylesheet" type="text/css" href="{{ asset('css/animation.css') }}">
<link rel="stylesheet" type="text/css" href="{{ asset('css/bootstrap.css') }}">
<link rel="stylesheet" type="text/css" href="{{ asset('css/bootstrap-select.min.css') }}">
<link rel="stylesheet" type="text/css" href="{{ asset('css/style.css') }}">
<link rel="stylesheet" href="{{ asset('font/fonts.css') }}">
<link rel="stylesheet" href="{{ asset('icon/style.css') }}">
<link rel="shortcut icon" href="{{ asset('images/favicon.ico') }}">
<link rel="apple-touch-icon-precomposed" href="{{ asset('images/favicon.ico') }}">
<link rel="stylesheet" type="text/css" href="{{asset('css/sweetalert.min.css')}}">
<link rel="stylesheet" type="text/css" href="{{ asset('css/custom.css') }}">
@yield('custom-css')
@stack("styles")
