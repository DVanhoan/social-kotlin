<?php

namespace App\Http\Controllers;

use Laravel\Socialite\Facades\Socialite;
use App\User;
use Illuminate\Support\Str;
use Illuminate\Support\Facades\Hash;
use Tymon\JWTAuth\Facades\JWTAuth;

class FacebookAuthController extends Controller
{
    public function redirect()
    {
        return Socialite::driver('facebook')->stateless()->redirect();
    }

    public function callback()
    {
        try {
            $facebookUser = Socialite::driver('facebook')->stateless()->user();


            $email = $facebookUser->email ?? Str::random(10) . '@facebook.com';

            $user = User::updateOrCreate(
                ['facebook_id' => $facebookUser->id],
                [
                    'username' => $facebookUser->name,
                    'name' => $facebookUser->name,
                    'email' => $email,
                    'pic' => $facebookUser->avatar ?? null,
                    'password' => Hash::make(Str::random(16)),
                ]
            );

            $token = JWTAuth::fromUser($user);

            return redirect()->to('https://duongvanhoan.netlify.app/login?token=' . $token);
        } catch (\Exception $e) {
            return response()->json([
                'error' => 'Login failed!',
                'message' => $e->getMessage(),
            ], 400);
        }
    }
}
