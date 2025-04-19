<?php

namespace App\Http\Controllers\Admin;

use App\Http\Controllers\Controller;
use App\Http\Services\UserService;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\Log;
use RealRashid\SweetAlert\Facades\Alert;

class UserController extends Controller
{
    private UserService $userService;

    public function __construct(UserService $userService)
    {
        $this->userService = $userService;
    }

    public function index()
    {
        $users = $this->userService->orderBy();
        $users = $users->paginate(10);
        return view("pages.users.index", compact('users'));
    }

    public function loginView() {
        Alert::toast('Please login', 'info');
        return view('auth.login');
    }

    public function login(Request $request)
    {
        $user = $this->userService->checkLogin($request->email, $request->password);
        if ($user) {
            auth()->login($user);
            Alert::toast('Login successfully', 'success');
            Log::info('User login successfully', ['user' => auth()->guard('web')->user()]);
            return redirect(route('admin'));
        }
        else {
            Alert::toast('Login failed', 'error');
            Log::info('Login failed', ['email' => $request->email, 'password' => $request->password]);
            return back();
        }
    }

    public function logout() {
        auth()->logout();
        return redirect()->route('login');
    }


    public function registerView() {
        return view('auth.register');
    }

    public function register(Request $request) {
        $request->validate([
            'username' => 'required|string|max:255',
            'email' => 'required|string|email|max:255|unique:users',
            'password' => 'required|string|min:8',
            'confirm_password' => 'required|same:password',
        ]);

        $user = $this->userService->register($request);

        if ($user) {
            auth()->guard('web')->login($user);
            Alert::toast('Registration successful', 'success');
            return redirect()->route('admin');
        }
        else {
            Alert::toast('Registration failed', 'error');
            return back();
        }
    }
}
