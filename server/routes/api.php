<?php

use Illuminate\Support\Facades\Route;

use App\Http\Controllers\UserController;
use App\Http\Controllers\PostController;
use App\Http\Controllers\FriendshipController;
use App\Http\Controllers\CommentController;
use App\Http\Controllers\ReactionController;

Route::prefix('v1')->group(function () {
    Route::prefix('users')->group(function () {
        Route::post('login', [UserController::class, 'login']);
        Route::post('register', [UserController::class, 'register']);

        Route::middleware('auth:api')->group(function () {
            Route::get('user/me', [UserController::class, 'me']);
            Route::post('logout', [UserController::class, 'logout']);
            Route::get('user/profile-picture/{userId}', [UserController::class, 'getProfilePicture']);
            Route::patch('user', [UserController::class, 'editUser']);
            Route::get('user/list', [UserController::class, 'loadUserList']);
            Route::get('user/user-by-userId/{userId}', [UserController::class, 'getUserByUserId']);
            Route::patch('user/upload-profile-picture', [UserController::class, 'uploadProfilePicture']);
        });
    });


    Route::prefix('posts')->group(function () {
        Route::middleware('auth:api')->group(function () {
            Route::get('post/{filename}', [PostController::class, 'getImage']);
            Route::get('post/user-can-post', [PostController::class, 'canUserPost']);
            Route::get('post/friends', [PostController::class, 'getPostsFromFriends']);
            Route::get('post/today/{userId}', [PostController::class, 'getTodaysPostByUser']);
            Route::post('post/create', [PostController::class, 'createPost']);
            Route::patch('post/{postId}', [PostController::class, 'addDescription']);
        });
    });


    Route::prefix('friendlist')->group(function () {
        Route::middleware('auth:api')->group(function () {
            Route::post('add/{userId}', [FriendshipController::class, 'addFriend']);
            Route::patch('accept/{userId}', [FriendshipController::class, 'acceptFriendRequest']);
            Route::patch('reject/{userId}', [FriendshipController::class, 'rejectFriend']);
            Route::get('pending', [FriendshipController::class, 'getPendingRequests']);
            Route::get('friends', [FriendshipController::class, 'getListOfFriends']);
        });
    });



    Route::middleware('auth:api')->group(function () {
        Route::get('comment/post/{postId}', [CommentController::class, 'getCommentsOnPost']);
        Route::post('comment', [CommentController::class, 'comment']);


        Route::get('reaction/{filename}', [ReactionController::class, 'getReactionImage']);
        Route::get('reaction/post/{postId}', [ReactionController::class, 'getReactionsOnPost']);
        Route::post('reaction', [ReactionController::class, 'react']);
    });
});
