<?php

use App\Http\Controllers\CommentController;
use App\Http\Controllers\FollowerController;
use App\Http\Controllers\LikeController;
use App\Http\Controllers\PostController;
use App\Http\Controllers\UserController;
use App\Http\Controllers\FacebookAuthController;
use Illuminate\Support\Facades\Route;

Route::prefix('v1')->group( function () {
    Route::prefix('users')->group(function () {
        Route::get('facebook', [FacebookAuthController::class, 'redirect']);
        Route::get('facebook/callback', [FacebookAuthController::class, 'callback']);

        Route::post('register', [UserController::class, 'register']);
        Route::post('login', [UserController::class, 'login']);
        Route::post('refresh', [UserController::class, 'refresh']);
        Route::put('password/{id}', [UserController::class, 'resetPassword']);
        Route::get('', [UserController::class,'getAll']);
        Route::delete('{id}', [UserController::class,'deleteUser']);
        Route::get('{id}', [UserController::class,'getUserById']);


        Route::middleware('auth:api')->group(function(){
            Route::get('user/me',[UserController::class, 'me']);
            Route::post('pic', [UserController::class,'uploadProfileImage']);
            Route::get('id/{id}',[UserController::class,'getById']);
            Route::get('logout',[UserController::class,'logout']);
            Route::get('username/{username}',[UserController::class,'getByUsername']);
            Route::put('update', [UserController::class,'update']);
        });
    });

    Route::prefix('followers')->group(function () {
        Route::middleware('auth:api')->group(function(){
            Route::get('/follow/{followed_id}', [FollowerController::class, 'follow']);
            Route::get('/unfollow/{followed_id}', [FollowerController::class, 'unfollow']);
        });
    });

    Route::prefix('posts')->group(function () {
        Route::middleware('auth:api')->group(function(){
            Route::post('post', [PostController::class,'uploadPost']);
            Route::delete('{post_id}', [PostController::class,'deletePost']);
            Route::get('post/{post_id}', [PostController::class,'getById']);
            Route::get('', [PostController::class,'getFeed']);
            Route::get('like/{post_id}', [LikeController::class,'like']);
            Route::get('unlike/{post_id}', [LikeController::class,'unlike']);
            Route::post('comment', [CommentController::class,'insert']);
        });
    });
});
