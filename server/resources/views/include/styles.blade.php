<link rel="stylesheet" type="text/css" href="{{ secure_asset('css/animate.min.css') }}">
<link rel="stylesheet" type="text/css" href="{{ secure_asset('css/animation.css') }}">
<link rel="stylesheet" type="text/css" href="{{ secure_asset('css/bootstrap.css') }}">
<link rel="stylesheet" type="text/css" href="{{ secure_asset('css/bootstrap-select.min.css') }}">
<link rel="stylesheet" type="text/css" href="{{ secure_asset('css/style.css') }}">
<link rel="stylesheet" href="{{ secure_asset('font/fonts.css') }}">
<link rel="stylesheet" href="{{ secure_asset('icon/style.css') }}">
<link rel="shortcut icon" href="{{ secure_asset('images/favicon.ico') }}">
<link rel="apple-touch-icon-precomposed" href="{{ secure_asset('images/favicon.ico') }}">
<link rel="stylesheet" type="text/css" href="{{secure_asset('css/sweetalert.min.css')}}">
<link rel="stylesheet" type="text/css" href="{{ secure_asset('css/custom.css') }}">
@yield('custom-css')
@stack("styles")