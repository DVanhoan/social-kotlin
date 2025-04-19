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
        <h4 class="text-center mb-4">Đăng ký tài khoản mới</h4>
        <form method="POST" action="{{ route('register') }}">
            @csrf

            <div class="mb-10">
                <label for="email" class="form-label">E-mail</label>
                <input type="email" class="form-control" id="email" name="email" placeholder="Địa chỉ email" />
            </div>

            <div class="mb-10">
                <label for="name" class="form-label">Họ và Tên</label>
                <input type="text" class="form-control" id="name" name="username" placeholder="Họ và tên" />
            </div>

            <div class="mb-10">
                <label for="password" class="form-label">Mật khẩu</label>
                <input type="password" class="form-control" id="password" name="password" placeholder="Mật khẩu">
            </div>

            <div class="mb-10">
                <label for="password_confirmation" class="form-label">Xác nhận mật khẩu</label>
                <input type="password" class="form-control" id="password_confirmation" name="confirm_password"
                    placeholder="Xác nhận mật khẩu">
            </div>


            <button type="submit" class="btn btn-primary w-100 mb-10" style="background-color: #6c63ff;">
                Đăng ký
            </button>
        </form>

        <p class="text-center mt-10">
            đã có tài khoản
            <a href="{{ route('login') }}" class="text-decoration-none text-primary">Đến đăng nhập</a>.
        </p>
    </div>

    @include('include.scripts')
</body>

</html>
