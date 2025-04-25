<!doctype html>
<html lang="{{ str_replace('_', '-', app()->getLocale()) }}">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">

    <meta name="csrf-token" content="{{ csrf_token() }}">

    <title>{{ config('app.name', 'social - admin') }}</title>
    <meta http-equiv="content-type" content="text/html; charset=utf-8" />
    <meta name="description" content="Social - Admin">

    <link rel="icon" type="image/png" href="{{ asset('images/logo/logo.png') }}" sizes="32x32">
    <link rel="icon" type="image/png" href="{{ asset('images/logo/logo.png') }}" sizes="16x16">
    <link rel="apple-touch-icon" href="{{ asset('images/logo/logo.png') }}">
    <link rel="manifest" href="{{ asset('images/logo/logo.png') }}">

    <meta name="author" content="Duong Van Hoan" />
    @include('include.styles')
</head>
<body class="body">
<div id="wrapper">
    <div id="page" class="">
        <div class="layout-wrap">
            @include('include.sidebar')

            <div class="section-content-right">
                @include('include.navbar')

                <div class="main-content">
                    @yield('content')
                </div>
            </div>
        </div>
    </div>
</div>

@include('sweetalert::alert')

@include('include.scripts')
</body>
</html>
