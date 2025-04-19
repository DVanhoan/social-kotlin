<?php

use App\Http\Controllers\Admin\UserController;
use App\Http\Controllers\Admin\CommentController;
use App\Http\Controllers\Admin\DashboardController;
use App\Http\Controllers\Admin\PostController;
use Illuminate\Support\Facades\Route;


Route::get('/login', [UserController::class, 'loginView'])->name('login.view');
Route::post('/login', [UserController::class, 'login'])->name('login');
Route::get('/register', [UserController::class, 'registerView'])->name('register.view');
Route::post('/register', [UserController::class, 'register'])->name('register');

Route::middleware(['auth', 'admin'])->group(function () {
    Route::get('/', [DashboardController::class, 'index'])->name('admin');

    Route::get('/users', [UserController::class, 'index'])->name('users');
    Route::post('/logout', [UserController::class, 'logout'])->name('logout');

    Route::get('/posts', [PostController::class, 'index'])->name('posts');
    Route::get('/posts/create', [PostController::class, 'create'])->name('posts.create');
    Route::post('/posts/store', [PostController::class, 'store'])->name('posts.store');
    Route::get('posts/{postId}/edit', [PostController::class, 'edit'])->name('posts.edit');
    Route::post('posts/{postId}/update', [PostController::class, 'update'])->name('posts.update');
    Route::get('posts/{postId}/destroy', [PostController::class, 'destroy'])->name('posts.destroy');
    Route::get('/posts/{post}/comments', [PostController::class, 'showComment'])->name('posts.showComment');

    Route::delete('/posts/{commentId}/comments', [CommentController::class, 'delete'])->name('comments.delete');
});