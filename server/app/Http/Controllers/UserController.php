<?php

namespace App\Http\Controllers;

use Illuminate\Http\Request;
use App\Models\User;
use Illuminate\Support\Facades\Hash;
use CloudinaryLabs\CloudinaryLaravel\Facades\Cloudinary;
use Illuminate\Support\Facades\Auth;

class UserController extends Controller
{
    public function register(Request $request)
    {
        $request->validate([
            'fullName' => 'required|string',
            'username' => 'required|string|unique:users',
            'email' => 'required|string|email|unique:users',
            'password' => 'required|string|min:6',
        ]);
        try {
            $body = $request->all();
            $body['password'] = Hash::make($body['password']);
            $body['registration_date'] = now();

            $user = User::create($body);

            return response()->json($user, 201);
        } catch (\Exception $e) {
            return response()->json(['message' => 'Error during registration', 'error' => $e->getMessage()], 500);
        }
    }

    public function login(Request $request)
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
                'user' => $user,
                'jwt' => $token,
            ]);
        } catch (\Exception $e) {
            return response()->json(['message' => 'Error during login', 'error' => $e->getMessage()], 500);
        }
    }

    public function me()
    {
        $user = Auth::guard('api')->user();
        return response()->json(['user' => $user]);
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

    public function loadUserList()
    {
        $users = User::all();
        return response()->json($users, 200);
    }

    public function editUser(Request $request)
    {

        $user = User::find($request->input('id'));
        if ($user) {
            $user->fill($request->only(['fullName', 'username', 'email', 'biography', 'location']));
            $user->save();
            return response()->json($user, 200);
        }
        return response()->json(['error' => 'User not found'], 404);
    }

    public function getUserByUserId($userId)
    {
        $user = User::find($userId);
        if ($user) {
            return response()->json($user, 200);
        }
        return response()->json(['error' => 'User not found'], 404);
    }

    public function uploadProfilePicture(Request $request)
    {
        if ($request->hasFile('picture')) {
            $file = $request->file('picture');
            if ($file->isValid()) {
                $filename = Cloudinary::upload($file->getRealPath())->getSecurePath();
            } else {
                return response()->json(['error' => 'Invalid file'], 400);
            }

            $user = User::find($request->input('id'));
            if ($user) {
                $user->profile_picture = $filename;
                $user->save();
                return response()->json($user, 200);
            }
            return response()->json(['error' => 'User not found'], 404);
        }
        return response()->json(['error' => 'No file uploaded'], 400);
    }


    public function getProfilePicture($userId)
    {
        $user = User::find($userId);
        if ($user && $user->profile_picture) {
            return response()->json(['profile_picture' => $user->profile_picture], 200);
        }
        return response()->json(['error' => 'Profile picture not found'], 404);
    }
}
