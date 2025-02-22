<?php

namespace App\Http\Controllers;

use App\Http\Requests\LoginRequest;
use App\User;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\Hash;
use Tymon\JWTAuth\Facades\JWTAuth;
use CloudinaryLabs\CloudinaryLaravel\Facades\Cloudinary;
use Illuminate\Support\Facades\Auth;

class UserController extends Controller
{
    public function register(Request $request)
    {
        $request->validate([
            'name' => 'required|string',
            'username' => 'required|string|unique:users',
            'email' => 'required|string|email|unique:users',
            'password' => 'required|string|min:6',
        ]);
        try {
            $body = $request->all();
            $body['password'] = Hash::make($body['password']);

            $user = User::create($body);

            return response()->json($user, 201);
        } catch (\Exception $e) {
            return response()->json(['message' => 'Error during registration', 'error' => $e->getMessage()], 500);
        }
    }

    public function login(LoginRequest $request)
    {
        try {
            $usernameOrEmail = $request->input('usernameOrEmail');
            $password = $request->input('password');

            $credentials = filter_var($usernameOrEmail, FILTER_VALIDATE_EMAIL)
                ? ['email' => $usernameOrEmail, 'password' => $password]
                : ['username' => $usernameOrEmail, 'password' => $password];

            if (!$token = Auth::guard('api')->attempt($credentials)) {
                return response()->json(['message' => 'Invalid login credentials'], 401);
            }

            $user = Auth::guard('api')->user();

            return response()->json([
                'user' => $user->load('followers', 'followings', 'posts'),
                'token' => $token,
            ]);
        } catch (\Exception $e) {
            return response()->json(['message' => 'Error during login', 'error' => $e->getMessage()], 500);
        }
    }

    public function refresh()
    {
        try {
            $token = auth('api')->refresh();

            return response()->json([
                'token' => $token,
                'user' => auth('api')->user(),
            ]);
        } catch (\Exception $e) {
            return response()->json(['message' => 'Error during token refresh', 'error' => $e->getMessage()], 500);
        }
    }

    public function me()
    {
        $user = Auth::guard('api')->user();
        return response()->json(['user' => $user]);
    }


    public function resetPassword(Request $request, $id)
    {
        try {
            $request->validate(['password' => 'required|string|min:6']);

            $newPassword = Hash::make($request->input('password'));
            $user = User::findOrFail($id);
            $user->update(['password' => $newPassword]);

            return response()->json(['message' => 'Password updated successfully', 'user' => $user]);
        } catch (\Exception $e) {
            return response()->json(['message' => 'Error during password reset', 'error' => $e->getMessage()], 500);
        }
    }

    public function logout()
    {
        try {
            auth('api')->logout();

            return response()->json(['message' => 'Successfully logged out']);
        } catch (\Exception $e) {
            return response()->json(['message' => 'Error during logout', 'error' => $e->getMessage()], 500);
        }
    }

    public function getAll()
    {
        try {
            $users = User::all();

            return response()->json($users);
        } catch (\Exception $e) {
            return response()->json(['message' => 'Error fetching users', 'error' => $e->getMessage()], 500);
        }
    }

    public function getById($id)
    {
        try {
            $user = User::with(['followers', 'followings', 'posts'])->findOrFail($id);

            return response()->json($user);
        } catch (\Exception $e) {
            return response()->json(['message' => 'Error fetching user by ID', 'error' => $e->getMessage()], 500);
        }
    }

    public function update(Request $request)
    {
        try {
            $request->validate([
                'name' => 'required|string',
                'username' => 'required|string',
                'email' => 'required|string|email',
            ]);

            $user = auth('api')->user();
            $user->update($request->only('name', 'username', 'email'));

            return response()->json($user);
        } catch (\Exception $e) {
            return response()->json(['message' => 'Error during profile update', 'error' => $e->getMessage()], 500);
        }
    }

    public function uploadProfileImage(Request $request)
    {
        try {
            $request->validate(['image' => 'required|image']);

            $user = auth('api')->user();

            if ($request->hasFile('image')) {
                $uploadedFileUrl = Cloudinary::uploadFile($request->file('image')->getRealPath())->getSecurePath();
                $user->update(['pic' => $uploadedFileUrl]);
            }

            return response()->json($user);
        } catch (\Exception $e) {
            return response()->json(['message' => 'Error during profile image upload', 'error' => $e->getMessage()], 500);
        }
    }

    public function deleteUser($id)
    {
        try {
            $user = User::findOrFail($id);
            $user->delete();

            return response()->json(['message' => 'User deleted successfully', 'user' => $user]);
        } catch (\Exception $e) {
            return response()->json(['message' => 'Error during user deletion', 'error' => $e->getMessage()], 500);
        }
    }

    public function getByUsername($username)
    {
        try {
            $user = User::where('username', $username)->with(['followers', 'followings', 'posts'])->firstOrFail();

            return response()->json($user);
        } catch (\Exception $e) {
            return response()->json(['message' => 'Error fetching user by username', 'error' => $e->getMessage()], 500);
        }
    }

    public function getUserById($id){
        try {
            $user = User::find($id);
            return response($user);
        } catch (\Exception $e) {
            return response([
                'message' => 'Có lỗi xảy ra khi lấy người dùng',
                'error' => $e->getMessage()
            ], 500);
        }
    }

}


