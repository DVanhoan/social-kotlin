<!DOCTYPE html>
<html lang="vi">

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Đăng nhập</title>
    @include('include.styles')
    <style>
        body {
            display: flex;
            justify-content: center;
            align-items: center;
            min-height: 100vh;
            margin: 0;
        }

        .container {
            max-width: 400px;
            border: 1px solid #ccc;
            padding: 20px;
            border-radius: 10px;
        }

    </style>
</head>

<body>
    <div class="container">
        <h4 class="text-center" style="margin-bottom: 50px">Đăng nhập vào trang quản trị</h4>
        <form method="POST" action="{{ route('login') }}">
            @csrf

            <div class="mb-10">
                <label for="email" class="form-label">E-mail</label>
                <input type="text" id="email" name="email" class="form-control" placeholder="Địa chỉ email" required>
            </div>

            <div class="mb-10">
                <label for="password" class="form-label">Mật khẩu</label>
                <input type="password" id="password" name="password" class="form-control" placeholder="Mật khẩu"
                    required>
            </div>

            <div class="d-flex justify-content-between align-items-center mb-10">
                <a href="#" class="text-decoration-none text-primary">Quên mật khẩu?</a>
            </div>

            <input type="hidden" name="_token" value="{{ csrf_token() }}">

            <button type="submit" class="btn btn-primary w-100 mb-10" style="background-color: #6c63ff;">Đăng
                nhập</button>
        </form>

        <p class="text-center mt-3">
            Bạn chưa có tài khoản?
            <a href="{{ route('register') }}" class="text-decoration-none text-primary">Đăng ký ngay</a>.
        </p>
    </div>

    @include('include.scripts')
</body>

</html>
