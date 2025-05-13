<?php

use App\Http\Controllers\ConversationController;
use App\Http\Controllers\MessageController;
use App\Http\Controllers\NotificationController;
use Illuminate\Support\Facades\Broadcast;
use Illuminate\Support\Facades\Route;

use App\Http\Controllers\UserController;
use App\Http\Controllers\PostController;
use App\Http\Controllers\FriendshipController;
use App\Http\Controllers\CommentController;
use App\Http\Controllers\ReactionController;

Broadcast::routes(['middleware' => ['auth:api']]);

Route::prefix('v1')->group(function () {
    Route::prefix('users')->group(function () {
        Route::post('login', [UserController::class, 'login']);
        Route::post('register', [UserController::class, 'register']);
        Route::post('refresh', [UserController::class, 'refresh']);

        Route::middleware('auth:api')->group(function () {
            Route::get('user/me', [UserController::class, 'me']);
            Route::post('logout', [UserController::class, 'logout']);
            Route::get('user/profile-picture/{userId}', [UserController::class, 'getProfilePicture']);
            Route::post('user', [UserController::class, 'editUser']);
            Route::get('user/list', [UserController::class, 'loadUserList']);
            Route::get('user/user-by-userId/{userId}', [UserController::class, 'getUserByUserId']);
            Route::post('user/upload-profile-picture', [UserController::class, 'uploadProfilePicture']);
        });
    });

    Route::middleware('auth:api')->group(function () {
        Route::prefix('posts')->group(function () {
            Route::get('post/friends', [PostController::class, 'getPostsFromFriends']);
            Route::post('post/create', [PostController::class, 'createPost']);
            Route::patch('post/{postId}', [PostController::class, 'addDescription']);
            Route::get('post/all', [PostController::class, 'getAllPosts']);
        });

        Route::prefix('friendlist')->group(function () {
            Route::post('add/{userId}', [FriendshipController::class, 'addFriend']);
            Route::patch('accept/{userId}', [FriendshipController::class, 'acceptFriendRequest']);
            Route::patch('reject/{userId}', [FriendshipController::class, 'rejectFriend']);
            Route::get('pending', [FriendshipController::class, 'getPendingRequests']);
            Route::get('sent', [FriendshipController::class, 'getSentRequests']);
            Route::get('friends', [FriendshipController::class, 'getListOfFriends']);
        });

        Route::get('comment/post/{postId}', [CommentController::class, 'getCommentsOnPost']);
        Route::post('comment', [CommentController::class, 'comment']);

        Route::get('reaction/post/{postId}', [ReactionController::class, 'getReactionsOnPost']);
        Route::post('reaction', [ReactionController::class, 'react']);

        Route::post('message/send', [MessageController::class, 'send']);
        Route::get('conversation/{conversationId}', [ConversationController::class, 'show']);
        Route::get('conversation', [ConversationController::class, 'index']);
        Route::post('conversation', [ConversationController::class, 'create']);

        Route::get('notifications', [NotificationController::class, 'index']);
        Route::post('notification/mark-as-read', [NotificationController::class, 'markAsRead']);
    });
});
