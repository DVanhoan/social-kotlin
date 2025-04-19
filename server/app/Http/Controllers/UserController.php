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

            $expiresIn = Auth::guard('api')->factory()->getTTL() * 60;

            $user = Auth::guard('api')->user();

            return response()->json([
                'user' => $user,
                'jwt' => $token,
                'expires_in' => $expiresIn
            ]);
        } catch (\Exception $e) {
            return response()->json(['message' => 'Error during login', 'error' => $e->getMessage()], 500);
        }
    }

    public function me()
    {
        $user = Auth::guard('api')->user();
        return response()->json($user, 200);
    }



    public function refresh()
    {
        try {
            $newToken = Auth::guard('api')->refresh();

            $expiresIn = Auth::guard('api')->factory()->getTTL() * 60;
            $user = Auth::guard('api')->user();

            return response()->json([
                'user' => $user,
                'jwt' => $newToken,
                'expires_in' => $expiresIn,
            ]);
        } catch (\Tymon\JWTAuth\Exceptions\TokenInvalidException $e) {
            return response()->json(['message' => 'Invalid token, unable to refresh'], 401);
        } catch (\Tymon\JWTAuth\Exceptions\JWTException $e) {
            return response()->json(['message' => 'Token refresh failed'], 500);
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

    public function loadUserList()
    {
        $users = User::all();
        return response()->json($users, 200);
    }

    public function editUser(Request $request)
    {
        $user = auth()->guard('api')->user();

        if (!$user) {
            return response()->json(['error' => 'User not found'], 404);
        }

        $validatedData = $request->validate([
            'fullName' => 'string',
            'username' => 'string',
            'email' => 'string|email',
            'biography' => 'string|nullable',
            'location' => 'string|nullable',
        ]);

        if (!$request->hasAny(array_keys($validatedData))) {
            return response()->json(['error' => 'No data provided'], 400);
        }

        $user->fill([
            'fullName' => $validatedData['fullName'] ?? $user->fullName,
            'username' => $validatedData['username'] ?? $user->username,
            'email' => $validatedData['email'] ?? $user->email,
            'biography' => $validatedData['biography'] ?? $user->biography,
            'location' => $validatedData['location'] ?? $user->location,
        ]);

        $user->save();

        return response()->json($user);
    }


    public function getUserByUserId($userId)
    {
        $user = User::find($userId);
        if ($user) {
            return response()->json($user);
        }
        return response()->json(['error' => 'User not found'], 404);
    }

    public function uploadProfilePicture(Request $request)
    {
        $request->validate([
            'picture' => 'required|image',
        ]);

        if ($request->hasFile('picture')) {
            $file = $request->file('picture');
            if ($file->isValid()) {
                $filename = Cloudinary::upload($file->getRealPath())->getSecurePath();
            } else {
                return response()->json(['error' => 'Invalid file'], 400);
            }

            $user = auth()->guard('api')->user();
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

        if(!$user || !$user->profile_picture) {
            return response()->json(['error' => 'Profile picture not found'], 404);
        }
        return response()->json($user->profile_picture, 200);
    }
}
